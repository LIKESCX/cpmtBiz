package com.cpit.cpmt.biz.impl.exchange.basic;

import static com.cpit.cpmt.biz.utils.exchange.Consts.storage_Result_OK;
import static com.cpit.cpmt.biz.utils.exchange.Consts.storage_Result_fail;
import static com.cpit.cpmt.biz.utils.exchange.Consts.validate_Res_fail;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.exchange.basic.BasicReportMsgInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorStatusInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.StationStatusInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;

@Service
public class UnionStoreMgmt {
	@Autowired ConnectorStatusInfoMgmt connectorStatusInfoMgmt;
	@Autowired BasicReportMsgInfoDao basicReportMsgInfoDao;
	@Autowired StationStatusInfoDao stationStatusInfoDao;
	@Autowired StationStatusInfoMgmt stationStatusInfoMgmt;
	@Autowired ConnectorStatusInfoDao connectorStatusInfoDao;
	@Autowired AlarmInfoMgmt alarmInfoMgmt;
	@Autowired EventInfoMgmt eventInfoMgmt;
	@Autowired BmsInfoMgmt bmsInfoMgmt;
	@Autowired StationsInfoMgmt stationsInfoMgmt;
	@Autowired EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private RabbitMsgSender rabbitMsgSender;
	@Autowired CacheUtil cacheUtil;

	private final static Logger logger = LoggerFactory.getLogger(UnionStoreMgmt.class);
	public void storeDB(BasicReportMsgInfo basicReportMsgInfo) {
		//logger.info("RabbitMQ msg begin process. ");
		String validateRes =  basicReportMsgInfo.getValidateResult();
		if(validate_Res_fail.equals(validateRes)) {
			logger.error("reportMsg validate is wrong." +basicReportMsgInfo.toString());
			basicReportMsgInfo.setId(SequenceId.getInstance().getId("cpmtBizBasicReportMsgInfoId")+"");
			basicReportMsgInfo.setStoreResult(storage_Result_fail);
			basicReportMsgInfo.setInTime(new Date());
			basicReportMsgInfoDao.insert(basicReportMsgInfo);
			return;
		}
		//int type = Integer.parseInt(basicReportMsgInfo.getInfType());
		try {
			commonMethod(basicReportMsgInfo);
			basicReportMsgInfo.setStoreFailDetail(storage_Result_OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			basicReportMsgInfo.setStoreFailDetail(storage_Result_fail);
			logger.error("reportMsg process commMethod error ",e);
			
		}
		basicReportMsgInfo.setId(SequenceId.getInstance().getId("cpmtBizBasicReportMsgInfoId")+"");
		basicReportMsgInfo.setInTime(new Date());
		basicReportMsgInfoDao.insert(basicReportMsgInfo);
		
	}
	private   boolean commonMethod(BasicReportMsgInfo basicReportMsgInfo) throws Exception {
		//JSONArray jsonArray = null;
		int type = Integer.parseInt(basicReportMsgInfo.getInfType());
		String operatorId = basicReportMsgInfo.getOperatorId();
		String json = basicReportMsgInfo.getJsonMsg();
		Date recTime = basicReportMsgInfo.getRecTime();
		switch (type) {
		case Consts.NOTIFICATION_STATIONINFO:
			json = JSON.parseObject(json).getString("StationInfo");
			StationInfo stationInfo = (StationInfo) JsonUtil.jsonToBean((String)json, StationInfo.class, true);
			stationsInfoMgmt.addQueryStation(stationInfo);
			return true;
		case Consts.NOTIFICATION_STATIONSTATUS:
			json = JSON.parseObject(json).getString("ConnectorStatusInfo");
			ConnectorStatusInfo connStatusInfo = (ConnectorStatusInfo)JsonUtil.jsonToBean((String)json, ConnectorStatusInfo.class, true);
			connStatusInfo.setReceivedTime(recTime);
			stationStatusInfoMgmt.addConnectorStatusInfo(connStatusInfo, operatorId);
			rabbitMsgSender.sendConnectorStatus("connectorStatus");//给 plj提供
			return true;
		case Consts.NOTIFICATION_BMSINFO:
			bmsInfoMgmt.insertBmsInfo(basicReportMsgInfo);
			return true;
		case Consts.NOTIFICATION_ALARMINFO:
			//jsonArray = JSON.parseObject((String)json).getJSONArray("AlarmInfos");
			//List<AlarmInfo> alarmInfoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true); //
			alarmInfoMgmt.insertList(basicReportMsgInfo);
			return true;
		case Consts.NOTIFICATION_EVENTINFO:
			//jsonArray = JSON.parseObject((String)json).getJSONArray("EventInfos");
			//List<EventInfo> evenInfoList = JsonUtil.mkList(jsonArray, EventInfo.class, true); //
			eventInfoMgmt.insertList(basicReportMsgInfo);
			return true;
		default:
			return false;
		}
	}
}
