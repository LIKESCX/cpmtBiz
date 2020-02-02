package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;

import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorProcDataDao;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;

import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorProcData;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;

@Service
public class BmsInfoMgmt {
	@Autowired UrlMgmt urlMgmt;
	@Autowired RestTemplate restTemplate;
	@Autowired ConnectorProcDataDao connectorProcDataDao;
	@Autowired BmsInfoDao bmsInfoDao;
	@Autowired RabbitMsgSender rabbitMsgSender;
	@Autowired JsonValidate jsonValidate;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired TokenUtil tokenUtil;
	@Autowired CheckOperatorPower checkOperatorPower;
	@Autowired ConnectorMgmt connectorMgmt;
	@Autowired CacheUtil cacheUtil;
	@Autowired ConnectorChargeInfoDao connectorChargeInfoDao;
	@Autowired ConnectorInfoDAO connectorInfoDao;
	@Value("${platform.operator.id}")
	private String self_operatorID;

	private final static Logger logger = LoggerFactory.getLogger(BmsInfoMgmt.class);
	@Transactional
	public String queryBmsInfo(String connectorID,String operatorID) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_bms_info");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		logger.info("queryUrl:"+queryUrl);
		String retJson = "";
		String timeStamp = "";
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("ConnectorID", connectorID);
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
			RestTemplate restTemplate = new RestTemplate();
			
			String token = tokenUtil.getToken(operatorID);
			Dispatcher dispatcher = new Dispatcher(restTemplate);
			retJson = (String)dispatcher.doPost(token, queryUrl, String.class, param);
			logger.debug("查询结果retJson:"+retJson);
			if(retJson==null||"".equals(retJson)) {
				return "FAIL";
			}
		}else {
			logger.error("queryBmsInfo===>>>获取queryUrl为空");
			return "FAIL";
		}
		Date receivedTime = new Date();
		//校验
		String objectName = "BmsInfos";
		String result = JsonValidate.chgToStr(jsonValidate.validate(Consts.INTERFACE_VERSIONV1, objectName, retJson));
		logger.info("\n校验结果result="+result);
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			if(Integer.parseInt(ret)==0) {
				//json--->>dto
				String JsonStr = JSON.parseObject(result).getString("Data");
				JSONObject jsonObject1 = (JSONObject) JSONObject.parseObject(JsonStr);
				String connectorId = jsonObject1.getString("ConnectorID");
				String status = jsonObject1.getString("Status");
				logger.info("connectorId="+connectorId+",status="+status+",JsonStr="+JsonStr);
				//检查 connectorId是否合法即存在exc_connector_info表中
				ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorId,operatorID);
				if(connectorInfo==null) {
					logger.error("queryBmsInfo===>>>根据operatorID:"+operatorID+",connectorId:"+connectorId+",未发现此枪接口信息");
					return "FAIL";
				}else {
					logger.info("queryBmsInfo===>>>根据operatorID:"+operatorID+",connectorId:"+connectorId+",可发现此枪接口信息");
				}
				//入库插入exc_connector_proc_data表及exc_bms_info表
				ConnectorProcData connectorProcData = new ConnectorProcData();
				int cpmtBizConnectorProcDataId= SequenceId.getInstance().getId("cpmtBizConnectorProcDataId");
				connectorProcData.setId(cpmtBizConnectorProcDataId);
				connectorProcData.setConnectorID(connectorId);
				connectorProcData.setStatus(status);
				connectorProcData.setOperatorID(operatorID);
				connectorProcData.setRecTime(receivedTime);
				connectorProcData.setInTime(new Date());
				connectorProcData.setTimeStamp(timeStamp);
				logger.debug("connectorProcData:"+connectorProcData);
				connectorProcDataDao.insertSelective(connectorProcData);
				BmsInfo bmsInfo = new BmsInfo();
				if("3".equals(status)) {
					JsonStr = JSON.parseObject(JsonStr).getString("BmsInfo");
					bmsInfo = JsonUtil.jsonToBean(JsonStr, BmsInfo.class, true);
					int cpmtBizBmsInfoId= SequenceId.getInstance().getId("cpmtBizBmsInfoId");
					bmsInfo.setId(cpmtBizBmsInfoId);
					bmsInfo.setOperatorID(operatorID);
					bmsInfo.setCid(connectorInfo.getCid());
					bmsInfo.setEid(connectorInfo.getEid());
					bmsInfo.setEquipmentID(connectorInfo.getEquipmentID());
					bmsInfo.setConnectorID(connectorId);
					bmsInfo.setSourceType(2);
					bmsInfo.setConnectorProcDataId(cpmtBizConnectorProcDataId);
					bmsInfo.setReceivedTime(receivedTime);
					bmsInfo.setInTime(new Date());
					bmsInfoDao.insertSelective(bmsInfo);
					logger.info("query_bms_info===>>>>bms信息查询入库成功");
					rabbitMsgSender.sendRealTimeBms("bmsInfoCharge");//推送过程信息
				}
				return "SUCCESS";
			}else {
				logger.error("queryBmsInfo Validate is fail:"+result);
				return "FAIL";
			}
		}else {
			logger.error("queryBmsInfo===>>result:"+result);
			return "FAIL";
		}
		
		
	}
	
	@Transactional
	public void insertBmsInfo(BasicReportMsgInfo basicReportMsgInfo) throws Exception {
		String operatorID = basicReportMsgInfo.getOperatorId();
		String timeStamp = basicReportMsgInfo.getTimeStamp();
		Date receivedTime = basicReportMsgInfo.getRecTime();
		String jsonMsg = basicReportMsgInfo.getJsonMsg();
		 
		String status = JSON.parseObject(jsonMsg).getString("Status");
		String connectorId = JSON.parseObject(jsonMsg).getString("ConnectorID");
		logger.info("connectorId= "+connectorId+",status= "+status);
		// 检查 connectorId是否合法即存在exc_connector_info表中 若是推送的话,应该在发送消息队列前校验.这里只是为了,获取需要cid,eid等信息
		ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorId,operatorID);
		if(connectorInfo==null) {
			logger.error("insertBmsInfo===>>>根据operatorID:"+operatorID+",connectorId:"+connectorId+",未发现此枪接口信息");
			return ;
		}else {
			logger.info("insertBmsInfo===>>>根据operatorID:"+operatorID+",connectorId:"+connectorId+",可发现此枪接口信息");
		}
		//入库插入exc_connector_proc_data表及exc_bms_info表
		ConnectorProcData connectorProcData = new ConnectorProcData();
		int cpmtBizConnectorProcDataId= SequenceId.getInstance().getId("cpmtBizConnectorProcDataId");
		connectorProcData.setId(cpmtBizConnectorProcDataId);
		connectorProcData.setConnectorID(connectorId);
		connectorProcData.setStatus(status);
		connectorProcData.setOperatorID(operatorID);
		connectorProcData.setRecTime(receivedTime);
		connectorProcData.setInTime(new Date());
		connectorProcData.setTimeStamp(timeStamp);
		logger.debug("connectorProcData:"+connectorProcData);
		connectorProcDataDao.insertSelective(connectorProcData);
		BmsInfo bmsInfo = new BmsInfo();
		if("3".equals(status)) {
			String data = JSON.parseObject(jsonMsg).getString("BmsInfo");
			bmsInfo = JsonUtil.jsonToBean(data, BmsInfo.class, true);
			int cpmtBizBmsInfoId= SequenceId.getInstance().getId("cpmtBizBmsInfoId");
			bmsInfo.setId(cpmtBizBmsInfoId);
			bmsInfo.setOperatorID(operatorID);
			bmsInfo.setConnectorID(connectorId);
			bmsInfo.setCid(connectorInfo.getCid());
			bmsInfo.setEid(connectorInfo.getEid());
			bmsInfo.setEquipmentID(connectorInfo.getEquipmentID());
			bmsInfo.setSourceType(2);
			bmsInfo.setConnectorProcDataId(cpmtBizConnectorProcDataId);
			bmsInfo.setReceivedTime(receivedTime);
			bmsInfo.setInTime(new Date());
			bmsInfoDao.insertSelective(bmsInfo);
			rabbitMsgSender.sendRealTimeBms("bmsInfoCharge");//推送过程信息
			//String msg = JsonUtil.beanToJson(bmsInfo);
			//rabbitMsgSender.sendRealTimeInfo("bmsRealtime");//发送消息给UI,通知更新
			//addChargeInfo(operatorID,connectorId,status,bmsInfo.getBMSCode());
			logger.info("notification_bms_info====>>>bms信息推送入库成功");
		}else {
			//upChargeInfo(operatorID,connectorId,status);
		}
		
	}
	
	@Transactional
	public Object notificationBmsInfo(String content, String objectName, Date receivedTime) {
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
			logger.info(operatorId +" "+timeStamp+" notification_bmsInfo validateResult " + result);
			if (StringUtils.isNotEmpty(result)) {
				BasicReportMsgInfo repMsgInfo = new BasicReportMsgInfo();
				repMsgInfo.setOperatorId(operatorId);// 运营商ID
				repMsgInfo.setInfVersion(Consts.INTERFACE_VERSIONV1);// 接口版本
				repMsgInfo.setInfType(String.valueOf(Consts.NOTIFICATION_BMSINFO));// 接口类型
				repMsgInfo.setInfName(String.valueOf(Consts.NOTIFICATION_BMSINFO_NAME));// 接口名称
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
				
				logger.debug("notification_bmsInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
				
				repMsgInfo.setJsonMsg(jsonMsg);// 推送的核心信息不存入reportMsg表中,放入消息队列中
				// 入队列
				rabbitMsgSender.send(repMsgInfo);
				resMap.put("Ret", Integer.parseInt(validateResult));
				resMap.put("Msg", validateFailDetail);
				dataMap.put("Status", 0);
				resMap.put("Data", dataMap);
			} 
		} catch (Exception ex) {
			logger.info("error in notification_bmsInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
			logger.error("error in notification_bmsInfo", ex);
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ex.getMessage());
			dataMap.put("Status", 1);
			resMap.put("Data", dataMap);
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}
	
	
	
	

}
