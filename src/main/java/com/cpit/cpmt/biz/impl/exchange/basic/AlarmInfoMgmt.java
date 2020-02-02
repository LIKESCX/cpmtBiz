package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;
import com.cpit.cpmt.biz.dao.security.MessageRemindDao;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.biz.impl.security.RiskControlMgmt;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.security.MessageRemind;
import com.cpit.cpmt.dto.security.RiskControl;

@Service
@RefreshScope
public class AlarmInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AlarmInfoMgmt.class);
	@Autowired private AlarmInfoDao alarmInfoDao;
	@Autowired private BmsInfoDao bmsInfoDao;
	@Autowired private RabbitMsgSender rabbitMsgSender;
	@Autowired private UrlMgmt urlMgmt;
	@Autowired private JsonValidate jsonValidate;
	@Autowired private DataSigCheck dataSigCheck;
	@Autowired private TokenUtil tokenUtil;
	@Autowired private CheckOperatorPower checkOperatorPower;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	@Autowired private EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private ConnectorMgmt connectorMgmt;
	@Autowired private AlarmAndEventCheckMgmt alarmAndEventCheckMgmt;
	@Autowired private RiskControlMgmt riskControlMgmt;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private MessageRemindDao messageRemindDao;
	@Autowired private MessageMgmt messageMgmt;
    @Value("${platform.operator.id}")
	private String self_operatorID;
	@Transactional
	public boolean insertList(BasicReportMsgInfo basicReportMsgInfo) throws Exception {
		JSONArray jsonArray = new JSONArray();
		//String operatorID = basicReportMsgInfo.getOperatorId();
		//String timeStamp = basicReportMsgInfo.getTimeStamp();
		Date receivedTime = basicReportMsgInfo.getRecTime();
		String jsonMsg = basicReportMsgInfo.getJsonMsg();
		jsonArray = JSON.parseObject(jsonMsg).getJSONArray("AlarmInfos");
		List<AlarmInfo> alarmInfoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true); //
		Boolean flag = false;
		for (AlarmInfo alarmInfo : alarmInfoList) {
			alarmInfo.setReceivedTime(receivedTime);
			addAlarmInfo(alarmInfo,flag);
		}
		rabbitMsgSender.sendRealTimeAlarm("alarmInfoCharge");//推送告警信息
		logger.info("sendRealTimeAlarm is success");
		if(flag) {
			rabbitMsgSender.sendRealTimeBms("bmsInfoCharge");//推送过程信息
			logger.info("sendRealTimeBms is success");
		}
		return true;
	}
	@Transactional
	public Map<String, Object> notificationAlarmInfo(String content,Date receivedTime,String objectName) {
	
		String operatorId = JSON.parseObject(content).getString("OperatorID");
		String timeStamp = JSON.parseObject(content).getString("TimeStamp");
		Map<String, Object> resMap = new LinkedHashMap<String, Object>();
		Map<String, Integer> dataMap = new HashMap<String, Integer>();
		String jsonMsg = null;
		try {
			if(!checkOperatorPower.isAccess(operatorId)) {
				resMap.put("Ret", ReturnCode.CODE_4003);
				resMap.put("Msg", ReturnCode.MSG_4003_Operator_Forbid_To_Access);
				dataMap.put("Status", 1);
				resMap.put("Data", dataMap);
				dataSigCheck.mkReturnMap(resMap);
				return resMap;
			}
			// 1.校验推送信息
			String result = JsonValidate.chgToStr(jsonValidate.validate1(Consts.INTERFACE_VERSIONV1, objectName, content));
			logger.info(operatorId +" "+timeStamp+" notification_alarmInfo validateResult " + result);
			if (StringUtils.isNotEmpty(result)) {
				BasicReportMsgInfo repMsgInfo = new BasicReportMsgInfo();
				repMsgInfo.setOperatorId(operatorId);// 运营商ID
				repMsgInfo.setInfVersion(Consts.INTERFACE_VERSIONV1);// 接口版本
				repMsgInfo.setInfType(String.valueOf(Consts.NOTIFICATION_ALARMINFO));// 接口类型
				repMsgInfo.setInfName(String.valueOf(Consts.NOTIFICATION_ALARMINFO_NAME));// 接口名称
				repMsgInfo.setRecTime(receivedTime);// 平台接收时间
				repMsgInfo.setTimeStamp(timeStamp);// 接口请求时时间戳信息
				JSONObject parseObject = JSON.parseObject(result);
				String validateResult = parseObject.getString("Ret");// 提取校验结果
				String validateFailDetail = parseObject.getString("Msg");//
				if (!"0".equals(validateResult)) {
					repMsgInfo.setValidateFailDetail(validateFailDetail);// 校验失败原因详情
					repMsgInfo.setValidateResult("1");// 封装校验结果
					repMsgInfo.setStoreResult("1");// 存储结果
				} else {
					repMsgInfo.setValidateResult(validateResult);// 封装校验结果
					repMsgInfo.setStoreResult(validateResult);// 存储结果
					
				}
				jsonMsg = JSON.parseObject(content).getString("Data");
				
				logger.debug("notification_alarmInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
				
				repMsgInfo.setJsonMsg(jsonMsg);// 推送的核心信息不存入reportMsg表中,放入消息队列中
				// 入队列前先检查
				
				rabbitMsgSender.send(repMsgInfo);
				resMap.put("Ret", Integer.parseInt(validateResult));
				resMap.put("Msg", validateFailDetail);
				dataMap.put("Status", 0);
				resMap.put("Data", dataMap);
			} 
		} catch (Exception ex) {
			logger.info("error in notification_alarmInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
			logger.error("error in notification_alarmInfo", ex);
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ex.getMessage());
			dataMap.put("Status", 1);
			resMap.put("Data", dataMap);
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}
	
	//查询告警信息接口
	@Transactional
	public List<AlarmInfo> queryAlarmInfo(String stationID, String operatorID, String equipmentID) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		List<AlarmInfo> infoList = new ArrayList<AlarmInfo>();
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_alarm_info");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		logger.info("queryUrl:"+queryUrl);
		String retJson = "";
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			
			String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("StationID", stationID);
			map.put("OperatorID", operatorID);
			map.put("EquipmentID", equipmentID);
			String beanToJson = JsonUtil.beanToJson(map);
			String data = dataSigCheck.encodeContentData(beanToJson);
			Map<String,Object> reqMap =new HashMap<String,Object>();
			reqMap.put("OperatorID", self_operatorID);
			reqMap.put("Data", data);
			reqMap.put("TimeStamp", timeStamp);
			reqMap.put("Seq", seq);
			String msg = self_operatorID+data+timeStamp+seq;
			String sig = dataSigCheck.genSign(msg);
			reqMap.put("Sig", sig);
			String param = JsonUtil.beanToJson(reqMap);
			logger.debug("\n加密后的参数param:"+param);
			
			String token = tokenUtil.getToken(operatorID);
			RestTemplate restTemplate = new RestTemplate();
			Dispatcher dispatcher = new Dispatcher(restTemplate);
			retJson = (String)dispatcher.doPost(token,queryUrl,String.class, param);
			logger.debug("查询结果retJson:"+retJson);
			if(retJson==null||"".equals(retJson)) {
				return infoList;
			}
		}else {
			logger.error("queryUrl为空");
			return infoList;
		}
		Date receivedTime = new Date();
		//校验
		String objectName = "AlarmInfos";
		String result = JsonValidate.chgToStr(jsonValidate.validate(Consts.INTERFACE_VERSIONV1, objectName, retJson));
		logger.info("\n校验结果result="+result);
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				String JsonStr = JSON.parseObject(result).getString("Data");
				JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("AlarmInfos");
				infoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true);
				if(infoList == null) {
					logger.info("operatorID:"+operatorID+",stationID:"+stationID+",equipmentID:"+equipmentID+"===>>>此时无告警信息");
					return null;
				}
				Boolean flag = false;
				for (AlarmInfo alarmInfo : infoList) {
					alarmInfo.setReceivedTime(receivedTime);
					addAlarmInfo(alarmInfo,flag);
				}
				//rabbitMsgSender.sendRealTimeAlarm("alarmInfoCharge");
			}else {
				logger.error("queryAlarmInfo Validate is fail:"+result);
			}
		}else {
			logger.error("queryAlarmInfo===>>result:"+result);
			return infoList;
		}
		return infoList;

	}
	
	//抽取的处理业务的公共部分
	@Transactional
	public void addAlarmInfo(AlarmInfo alarmInfo,Boolean flag) throws Exception {
		//检验告警编码是否符合要求
		String alarmCode = alarmInfo.getAlarmCode();
		String alarmType = alarmInfo.getAlarmType();
		String alarmLevel = alarmInfo.getAlarmLevel();
		Map<String,String> map = new HashMap<String,String>();
		map.put("alarmCode", alarmCode);
		map.put("alarmType", alarmType);
		map.put("alarmLevel", alarmLevel);
		AlarmItem checkCurrentAlarmValid = alarmAndEventCheckMgmt.checkCurrentAlarmValid(map);
		if(checkCurrentAlarmValid==null) {
			logger.error("根据alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+",未发现匹配的告警编码分类级别");
			return;
		}else {
			logger.info("根据alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+",可发现匹配的告警编码分类级别");
		}
		//根据operator_id+station_id检查
		StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(alarmInfo.getStationID(), alarmInfo.getOperatorID());
	     if(stationInfo==null) {
	    	 logger.error("根据operatorID:"+alarmInfo.getOperatorID()+",stationID:"+alarmInfo.getStationID()+",未发现此站点信息");
	    	 return;
	     }else {
	    	 logger.info("根据operatorID:"+alarmInfo.getOperatorID()+",stationID:"+alarmInfo.getStationID()+",可发现此站点信息");
	     }
	   //根据operator_id+equipment_id,检查
	     EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(alarmInfo.getEquipmentID(), alarmInfo.getOperatorID());
	     if(equipmentInfo==null) {
	    	 logger.error("根据operatorID:"+alarmInfo.getOperatorID()+",equipmentID:"+alarmInfo.getEquipmentID()+",未发现此充电设备信息");
	    	 return;
	     }else {
	    	 logger.info("根据operatorID:"+alarmInfo.getOperatorID()+",equipmentID:"+alarmInfo.getEquipmentID()+",可发现此充电设备信息");
	     }
	   //根据operator_id+connector_id检查
	    ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(alarmInfo.getConnectorID(),alarmInfo.getOperatorID());
	    if(connectorInfo==null) {
	    	logger.error("根据operatorID:"+alarmInfo.getOperatorID()+",connectorID:"+alarmInfo.getConnectorID()+",未发现此枪接口信息");
	    	return;
	    }else {
	    	logger.info("根据operatorID:"+alarmInfo.getOperatorID()+",connectorID:"+alarmInfo.getConnectorID()+",可发现此枪接口信息");
	    }
	    BmsInfo bmsInfo = alarmInfo.getBmsInfo();
		/*if(equipmentInfo.getEquipmentType()==1) {//'设备类型	1：直流设备 2：交流设备 3：交直流一体设备 4：无线充电 5：充放电设备 255：其他', 
			//当是直流设备时,依照地标要求有告警上报时,必须携带bmsInfo信息
			if(bmsInfo!=null) {
				logger.info("直流设备告警上报时已携带bmsInfo信息===>>>operatorID:"+alarmInfo.getOperatorID()+",equipmentID:"+alarmInfo.getEquipmentID());
			}else {
				logger.error("直流设备告警上报时未携带bmsInfo信息===>>>operatorID:"+alarmInfo.getOperatorID()+",equipmentID:"+alarmInfo.getEquipmentID());
				return;
			}
		}*/
		if(equipmentInfo.getEquipmentType()!=1) {//非直流设备
			if(bmsInfo!=null) {
				logger.error("设备类型[{}],设备id[{}],运营商id[{}]==非直流设备不允许上报bmsInfo信息",equipmentInfo.getEquipmentType(),alarmInfo.getEquipmentID(),alarmInfo.getOperatorID());
				return;
			}
		}
		//从序列表中取主键id的值
		int alarmInfoId= SequenceId.getInstance().getId("cpmtBizAlarmInfoId");
		alarmInfo.setId(alarmInfoId);
		alarmInfo.setCid(connectorInfo.getCid());
		alarmInfo.setEid(connectorInfo.getEid());
		alarmInfo.setEquipmentType(equipmentInfo.getEquipmentType());
		//alarmInfo.setOperatorID(operatorID);
		//alarmInfo.setReceivedTime(receivedTime);
		alarmInfo.setInTime(new Date());
		alarmInfoDao.insertSelective(alarmInfo);
		OperatorInfoExtend operatorInfoById = null;
		//发送短信的代码暂时注释掉
		/*if("2".equals(alarmLevel)||"3".equals(alarmLevel)) {//告警级别在2或3时，通过短信推送给市和区发改委
			
			operatorInfoById = operatorMgmt.getOperatorInfoById(alarmInfo.getOperatorID());
			if(operatorInfoById!=null&&StringUtils.isNotEmpty(operatorInfoById.getOperatorName())) {
				logger.info("打印运营商名称信息===>>>{}",operatorInfoById.getOperatorName());
				//短信内容
				String message= operatorInfoById.getOperatorName() + "的" + stationInfo.getStationName() + "的"
						+ equipmentInfo.getEquipmentName() + "的" + connectorInfo.getConnectorName() + "接口发生"
						+ alarmInfo.getNoteString() + ",告警级别:"+alarmInfo.getAlarmLevel()+",告警类型:"+alarmInfo.getAlarmType();
				logger.info("短信内容===>>>{}", message);
				//获取接收人列表
				MessageRemind messageRemind = new MessageRemind();
				messageRemind.setAreaCode(stationInfo.getAreaCode());
				List<MessageRemind> messageRemindList = messageRemindDao.getMessageRemindList(messageRemind);
				if(messageRemindList !=null&&messageRemindList.size()>0) {
					for (MessageRemind msgRemind : messageRemindList) {
						logger.info("接收姓名{},接收人手机号{}",msgRemind.getRemindName(),msgRemind.getPhoneNumber());
						ExcMessage excMessage = new ExcMessage();
						excMessage.setPhoneNumber(msgRemind.getPhoneNumber());
						excMessage.setSubContent(message);
						excMessage.setSmsType(ExcMessage.TYPE_ALARM_ANNOUNCE);
						messageMgmt.sendMessage(excMessage);
						logger.info("sendMessage is ok!");
					}
				}else {
					logger.error("获取接收人列表为null或者列表长度为0");
				}
			}else {
				logger.error("获取的运营商信息===>>>{}",operatorInfoById);
			}
			
		}*/
		
		if(bmsInfo!=null) {
			int bmsInfoId= SequenceId.getInstance().getId("cpmtBizBmsInfoId");
			bmsInfo.setId(bmsInfoId);
			bmsInfo.setCid(connectorInfo.getCid());
			bmsInfo.setEid(connectorInfo.getEid());
			bmsInfo.setEquipmentID(alarmInfo.getEquipmentID());
			bmsInfo.setAlarmInfoId(alarmInfoId);
			bmsInfo.setOperatorID(alarmInfo.getOperatorID());
			bmsInfo.setSourceType(1);
			bmsInfo.setConnectorID(alarmInfo.getConnectorID());
			bmsInfo.setReceivedTime(alarmInfo.getReceivedTime());
			bmsInfo.setInTime(new Date());
			bmsInfoDao.insertSelective(bmsInfo);
			logger.info("来自告警信息推送的bms信息入库成功");
			if(!flag) {
				flag = true;
			}
		}
		//插入风险管控
		RiskControl riskControl = new RiskControl();
		riskControl.setAreaCode(stationInfo.getAreaCode());
		riskControl.setOperatorId(alarmInfo.getOperatorID());
		if(operatorInfoById!=null)
			riskControl.setOperatorName(operatorInfoById.getOperatorName());
		riskControl.setStationId(alarmInfo.getStationID());
		riskControl.setStationName(alarmInfo.getStationName());
		riskControl.setEquipmentId(alarmInfo.getEquipmentID());
		riskControl.setEquipmentName(equipmentInfo.getEquipmentName());
		riskControl.setAlarmCode(alarmInfo.getAlarmCode());
		riskControl.setAlarmStatus(Integer.parseInt(alarmInfo.getAlarmStatus()));
		riskControl.setAlarmLevel(Integer.parseInt(alarmInfo.getAlarmLevel()));
		riskControl.setAlarmType(Integer.parseInt(alarmInfo.getAlarmType()));
		riskControl.setAlarmDesc(alarmInfo.getNoteString());
		riskControl.setAlarmTime(alarmInfo.getAlarmTime());
		riskControlMgmt.addRiskControl(riskControl);
		logger.info("addRiskControl is ok!");
		
	}
}


