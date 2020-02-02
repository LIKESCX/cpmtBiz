package com.cpit.cpmt.biz.controller.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.impl.monitor.RealTimeAlarmInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@RestController
@RequestMapping("/monitor")
public class RealTimeAlarmInfoController {
	private final static Logger logger = LoggerFactory.getLogger(RealTimeAlarmInfoController.class);
	@Autowired RealTimeAlarmInfoMgmt realTimeAlarmInfoMgmt;
	@Autowired RabbitMsgSender rabbitMsgSender;
	/*充电设施实时运行状态监控*/
	//postman测试 http://localhost:28060/monitor/queryRealTimeAlarmInfo/1/10
	//测试参数:{"operatorID":"MA5DEDCQ9","stationID":"902","equipmentID":"3301231230000001","areaCode":"440304","stationStreet":"12"}
	@RequestMapping("/queryRealTimeAlarmInfo/{pageNumber}/{pageSize}")
	public ResultInfo queryRealTimeAlarmInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody EquimentMonitorCondition emc) {
		logger.info("queryRealTimeAlarmInfo begin param===>>>pageNumber:"+pageNumber+",pageSize:"+pageSize+",operatorID:"+emc.getOperatorID()+",stationID="+emc.getStationID()+",equipmentID:"+emc.getAreaCode()+",areaCode;"+emc.getAreaCode()+",streetId:"+emc.getStreetId());
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AlarmInfo> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = realTimeAlarmInfoMgmt.queryRealTimeAlarmInfo(emc);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = realTimeAlarmInfoMgmt.queryRealTimeAlarmInfo(emc);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryRealTimeAlarmInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryRealTimeAlarmInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	
	//进入实时监控详情页面
	//postman测试 http://localhost:28060/monitor/queryAlarmRealDtailInfo/1/10?operatorID=MA5DEDCQ9&connectorID=330123123000000101
	@RequestMapping("/queryAlarmRealDtailInfo/{pageNumber}/{pageSize}")
	public ResultInfo queryAlarmRealDtailInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestParam("operatorID") String operatorID,
			@RequestParam("connectorID")String connectorID) {
		logger.info("queryAlarmRealDtailInfo begin param===>>>operatorID:"+operatorID+",connectorID:"+connectorID);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AlarmInfo> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = realTimeAlarmInfoMgmt.queryAlarmRealDtailInfo(operatorID,connectorID);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = realTimeAlarmInfoMgmt.queryAlarmRealDtailInfo(operatorID,connectorID);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryAlarmRealDtailInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAlarmRealDtailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//进入实时监控详情页面
    //告警级别、告警次数使用图形展示
	//postman测试 http://localhost:28060/monitor/queryAlarmGraphicDisplayInfo?operatorID=MA5DEDCQ9&connectorID=330123123000000101
	@RequestMapping("/queryAlarmGraphicDisplayInfo")
	public ResultInfo queryBmsGraphicDisplayInfo(
			@RequestParam("operatorID") String operatorID,
			@RequestParam("connectorID")String connectorID) {
		logger.info("queryAlarmGraphicDisplayInfo begin param===>>>operatorID:"+operatorID+",connectorID:"+connectorID);
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,String>> infoList = new ArrayList<Map<String,String>>();
		try {
			infoList = realTimeAlarmInfoMgmt.queryAlarmGraphicDisplayInfo(operatorID,connectorID);
			logger.info("queryAlarmGraphicDisplayInfo total:" + infoList.size());
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAlarmGraphicDisplayInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//测试rabbitmq消息发送的webUi端
//	@RequestMapping("/sendRealTimeAlarm")
//	public void publish() {
//		String msg = "alarmRealtime";
//		// 入队列
//		rabbitMsgSender.sendRealTimeAlarm(msg);
//	}
}
