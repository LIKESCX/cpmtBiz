package com.cpit.cpmt.biz.controller.security.battery;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.battery.AbnormalAlarmDataMiningMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.security.battery.AbnormalAlarmDataMiningConditions;
import com.cpit.cpmt.dto.security.battery.AbnormalAlarmDataMiningDto;
import com.cpit.cpmt.dto.security.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsSingleChargeWarningResult;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;
import com.cpit.cpmt.dto.security.battery.BatteryWarningAbnormalMonthlyAnalysis;
//2.3.	异常告警数据挖掘分析
@RestController
@RequestMapping("/security/battery")
public class AbnormalAlarmDataMiningController {
	private final static Logger logger = LoggerFactory.getLogger(AbnormalAlarmDataMiningController.class);
	
	@Autowired
	private AbnormalAlarmDataMiningMgmt abnormalAlarmDataMiningMgmt;
		//-------------------------------------2.2.	电池数据跟踪评估 内容一--------------------------
	/**
	 * 内容一：
	 * 	第一级钻取：
	 * 	按查询条件生成故障告警类型柱状图，横坐标表示时间，纵坐标统计一种或多种故障告警类型的数量；
	 */
	@RequestMapping("/queryFirstLevelAbnormalAlarmData")
	public ResultInfo queryFirstLevelAbnormalAlarmData(@RequestBody AbnormalAlarmDataMiningConditions param) {
		logger.debug("queryFirstLevelAbnormalAlarmData begin params [{}]", param);
		try {
			//test start
			Date startTime =TimeConvertor.stringTime2Date("2019-05-28 15:10:54","yyyy-MM-dd HH:mm:ss");
			Date endTime =TimeConvertor.stringTime2Date("2019-10-24 20:44:42","yyyy-MM-dd HH:mm:ss");
			param.setStartTime(startTime);
			param.setEndTime(endTime); 
			param.setAllOperators(1);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(1);//0表示单选 1表示多选
			param.setStationId("1008601");
			param.setAllEquipments(1);//0表示单选 1表示多选
			param.setEquipmentId("10086001");
			param.setTimeGranularity(1);//1.小时、2.天、3.周、4.月、5.季
			param.setAlarmStatus(1);	//告警状态 0-恢复,1-发生,2-全部
			param.setAlarmType(1);		//故障告警类型 1-充电系统故障，2-电池系统故障，3-配电系统故障，4全部
			param.setAlarmLevel(4);		//故障级别分类 1-人身安全级，2-设备安全级，3-告警提示级，4全部
			//param.setbMSCode("1");
			//test end
			List<AbnormalAlarmDataMiningDto> infoList = abnormalAlarmDataMiningMgmt.queryFirstLevelAbnormalAlarmData(param);
			return new ResultInfo(ResultInfo.OK, infoList);
		} catch (Exception e) {
			logger.error("queryFirstLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	/**
	 * 第二级钻取: 
	 * 	点击“不同故障告警类型”如：充电系统故障，向下钻取对应故障级别分类情况，统计故障级别数量，
	 * 	生成故障级别分类柱状图，横坐标表示时间，纵坐标表示统计一种或多种故障级别的数量；
	 */
	@RequestMapping("/querySecondLevelAbnormalAlarmData")
	public ResultInfo querySecondLevelAbnormalAlarmData(@RequestBody AbnormalAlarmDataMiningConditions param) {
		logger.debug("queryFirstLevelAbnormalAlarmData begin params [{}]", param);
		try {
			//test start
			//Date startTime =TimeConvertor.stringTime2Date("2019-05-28 15:10:54","yyyy-MM-dd HH:mm:ss");
			//Date endTime =TimeConvertor.stringTime2Date("2019-10-24 20:44:42","yyyy-MM-dd HH:mm:ss");
			//param.setStartTime(startTime);
			//param.setEndTime(endTime); 
			param.setAllOperators(1);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(1);//0表示单选 1表示多选
			param.setStationId("1008601");
			param.setAllEquipments(1);//0表示单选 1表示多选
			param.setEquipmentId("10086001");
			param.setTimeGranularity(2);//1.小时、2.天、3.周、4.月、5.季
			//param.setStatisticalHour("2019-05-28 15");
			param.setStatisticalDay("20190528");
			param.setAlarmType(2);	//故障告警类型 1-充电系统故障，2-电池系统故障，3-配电系统故障
			param.setAlarmStatus(1);	//告警状态 0-恢复,1-发生,2-全部
			//param.setbMSCode("1");
			//test end
			Map<String,Object> map = new HashMap<String,Object>();
			List<AlarmInfo> infoList = abnormalAlarmDataMiningMgmt.querySecondLevelAbnormalAlarmData(param);
			map.put("infoList", infoList);
			if(param.getTimeGranularity()==1) {
				map.put("statisticalHour", param.getStatisticalHour());
			}else if(param.getTimeGranularity()==2) {
				map.put("statisticalDay", param.getStatisticalDay());
			}else if(param.getTimeGranularity()==3) {
				map.put("statisticalWeek", param.getStatisticalWeek());
			}else if(param.getTimeGranularity()==4) {
				map.put("statisticalMonth", param.getStatisticalMonth());
			}else if(param.getTimeGranularity()==5) {
				map.put("statisticalSeason", param.getStatisticalSeason());
			}
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception e) {
			logger.error("queryFirstLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	/**
	 * 第三级钻取：
	 * 点击故障级别数量获取告警信息详细内容，
	 * 以列表展示告警信息，如充电站名称、运营商、设备编码、设备接口编码、告警状态、
	 * 告警类型、告警级别、告警时间、告警备注等信息，
	 * 按运营商发出告警从高到低数据量排序；
	*/
	@RequestMapping("/queryThirdLevelAbnormalAlarmData/{pageNumber}/{pageSize}")
	public ResultInfo querySecondLevelData(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody AbnormalAlarmDataMiningConditions param) {
		logger.debug("queryThirdLevelAbnormalAlarmData begin params [{}],pageNumber[{}],pageSize[{}]", param,pageNumber,pageSize);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AlarmInfo> infoList = null;
		try {
			//test start
			//Date startTime =TimeConvertor.stringTime2Date("2019-05-28 15:10:54","yyyy-MM-dd HH:mm:ss");
			//Date endTime =TimeConvertor.stringTime2Date("2019-10-24 20:44:42","yyyy-MM-dd HH:mm:ss");
			//param.setStartTime(startTime);
			//param.setEndTime(endTime); 
			param.setAllOperators(1);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(1);//0表示单选 1表示多选
			param.setStationId("1008601");
			param.setAllEquipments(1);//0表示单选 1表示多选
			param.setEquipmentId("10086001");
			param.setTimeGranularity(2);//1.小时、2.天、3.周、4.月、5.季
			//param.setStatisticalHour("2019-05-28 15");
			param.setStatisticalDay("20190528");
			param.setAlarmLevel(2);		//故障级别分类 1-人身安全级，2-设备安全级，3-告警提示级，4全部
			param.setAlarmStatus(1);	//告警状态 0-恢复,1-发生,2-全部
			//param.setbMSCode("1");
			//test end
			if(pageNumber==-1){
				infoList = abnormalAlarmDataMiningMgmt.queryThirdLevelAbnormalAlarmData(param);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = abnormalAlarmDataMiningMgmt.queryThirdLevelAbnormalAlarmData(param);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.debug("queryThirdLevelAbnormalAlarmData total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryThirdLevelAbnormalAlarmData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//-------------------------------------3.3.1. 动力电池告警分析报告-月/季/年报告 内容二--------------------------
	
	//预览报告(当粒度为：月,季,年 导出3.3的报告)
	

	//一、基本信息 
	@RequestMapping("/queryBatteryBasicInformation")
	public ResultInfo queryBatteryBasicInformation(@RequestBody AbnormalAlarmDataMiningConditions param) {
		try {
			//test start
			param.setbMSCode("1");
			//test end
			BmsInfo bmsInfo = abnormalAlarmDataMiningMgmt.queryBatteryBasicInformation(param.getbMSCode());
			return new ResultInfo(ResultInfo.OK,bmsInfo);
		} catch (Exception e) {
			logger.error("queryBatteryBasicInformation_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	//二、月/季/年度告警次数统计
	@RequestMapping("/queryBatteryAlarmTimesStatistics")
	public ResultInfo queryBatteryAlarmTimesStatistics(@RequestBody AbnormalAlarmDataMiningConditions param) {
		try {
			//test start
			//Date startTime =TimeConvertor.stringTime2Date("2019-05-28 15:10:54","yyyy-MM-dd HH:mm:ss");
			//Date endTime =TimeConvertor.stringTime2Date("2019-10-24 20:44:42","yyyy-MM-dd HH:mm:ss");
			//param.setStartTime(startTime);
			//param.setEndTime(endTime); 
			param.setbMSCode("1");
			param.setAllOperators(1);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(1);//0表示单选 1表示多选
			param.setStationId("1008601");
			param.setAllEquipments(1);//0表示单选 1表示多选
			param.setEquipmentId("10086001");
			param.setTimeGranularity(6);//1.小时、2.天、3.周、4.月、5.季  6.年
			//param.setStatisticalHour("2019-05-28 15");
			//param.setStatisticalMonth("202001");
			//param.setStatisticalSeason("202001");
			param.setStatisticalYear("2020");
			//param.setAlarmLevel(2);		//故障级别分类 1-表示最严重，2-一般严重，3-表示不严重
			//param.setAlarmType();
			//test end
			List<BatteryWarningAbnormalMonthlyAnalysis> infoList = abnormalAlarmDataMiningMgmt.queryBatteryAlarmTimesStatistics(param);
			return new ResultInfo(ResultInfo.OK,infoList);
		} catch (Exception e) {
			logger.error("queryBatteryAlarmTimesStatistics_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	//二、月/季/年度告警等级分布情况
	@RequestMapping("/queryBatteryAlarmLevelDistribution")
	public ResultInfo queryBatteryAlarmLevelDistribution(@RequestBody AbnormalAlarmDataMiningConditions param) {
		try {
			//test start
			//Date startTime =TimeConvertor.stringTime2Date("2019-05-28 15:10:54","yyyy-MM-dd HH:mm:ss");
			//Date endTime =TimeConvertor.stringTime2Date("2019-10-24 20:44:42","yyyy-MM-dd HH:mm:ss");
			//param.setStartTime(startTime);
			//param.setEndTime(endTime); 
			param.setbMSCode("1");
			param.setAllOperators(1);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(1);//0表示单选 1表示多选
			param.setStationId("1008601");
			param.setAllEquipments(1);//0表示单选 1表示多选
			param.setEquipmentId("10086001");
			param.setTimeGranularity(6);//1.小时、2.天、3.周、4.月、5.季  6.年
			//param.setStatisticalHour("2019-05-28 15");
			//param.setStatisticalMonth("202001");
			//param.setStatisticalSeason("202001");
			param.setStatisticalYear("2020");
			//param.setAlarmLevel(2);		//故障级别分类 1-表示最严重，2-一般严重，3-表示不严重
			//param.setAlarmType();
			//test end
			List<BatteryWarningAbnormalMonthlyAnalysis> infoList = abnormalAlarmDataMiningMgmt.queryBatteryAlarmLevelDistribution(param);
			return new ResultInfo(ResultInfo.OK,infoList);
		} catch (Exception e) {
			logger.error("queryBatteryAlarmLevelDistribution_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
			
		}
	}
	
}
