package com.cpit.cpmt.biz.controller.security.battery;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.security.battery.BatteryAnalysisMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthBasicInformation;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthHistoricalOperationAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthPerformanceHistoryAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBatteryOperationMonthlyAnalysis;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;

@RestController
@RequestMapping("/security/battery")
//3.2.1.	动力电池分析报告-月报告和季报告
public class BatteryAnalysisController {
	private final static Logger logger = LoggerFactory.getLogger(BatteryAnalysisController.class);
	@Autowired BatteryAnalysisMgmt batteryAnalysisMgmt;
	//获取一、基本信息--月/季/年
	@RequestMapping("/queryMonthBasicInformation")
	public ResultInfo queryMonthBasicInformation(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		try {
			//test start
//			Date startTime =TimeConvertor.stringTime2Date("2020-02-03 19:29:40","yyyy-MM-dd HH:mm:ss");
//			Date endTime =TimeConvertor.stringTime2Date("2020-02-03 20:33:37","yyyy-MM-dd HH:mm:ss");
//			param.setStartTime(startTime);
//			param.setEndTime(endTime); 
			param.setAllOperators(0);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(0);
			param.setStationId("1008601");
			param.setAllEquipments(1);
			param.setEquipmentId("10086001");
			param.setTimeGranularity(6);//4.月、5.季  6.年
			param.setbMSCode("1");
			//param.setStatisticalMonth("202002");
			//param.setStatisticalSeason("202001");
			param.setStatisticalYear("2020");
			//test end
			AnaBatteryMonthBasicInformation information = batteryAnalysisMgmt.queryMonthBasicInformation(param);
			return new ResultInfo(ResultInfo.OK, information);
		} catch (Exception e) {
			logger.error("queryMonthBasicInformation_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	//获取 二、电池性能历史分析--月/季/年
	@RequestMapping("/queryMonthPerformanceHistoryAnalysis")
	public Object queryMonthPerformanceHistoryAnalysis(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		try {
			//test start
			param.setAllOperators(0);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(0);
			param.setStationId("1008601");
			param.setAllEquipments(1);
			param.setEquipmentId("10086001");
			param.setTimeGranularity(6);//4.月、5.季  6.年
			param.setbMSCode("1");
			//param.setStatisticalMonth("202002");
			//param.setStatisticalSeason("202001");
			param.setStatisticalYear("2020");
			//test end
			List<AnaBatteryMonthPerformanceHistoryAnalysis> information = batteryAnalysisMgmt.queryMonthPerformanceHistoryAnalysis(param);
			return new ResultInfo(ResultInfo.OK, information);
		} catch (Exception e) {
			logger.error("queryMonthPerformanceHistoryAnalysis_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	//获取 三、电池运行情况历史分析--月/季/年
	@RequestMapping("/queryMonthHistoricalOperationAnalysis")
	public Object queryMonthHistoricalOperationAnalysis(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		try {
			//test start
			param.setAllOperators(0);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(0);
			param.setStationId("1008601");
			param.setAllEquipments(1);
			param.setEquipmentId("10086001");
			param.setTimeGranularity(5);//4.月、5.季  6.年
			param.setbMSCode("1");
			//param.setStatisticalMonth("202002");
			param.setStatisticalSeason("202001");
			//param.setStatisticalYear("2020");
			//test end
			List<AnaBatteryMonthHistoricalOperationAnalysis> information = batteryAnalysisMgmt.queryMonthHistoricalOperationAnalysis(param);
			return new ResultInfo(ResultInfo.OK, information);
		} catch (Exception e) {
			logger.error("queryMonthHistoricalOperationAnalysis_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	//获取 四、电池告警异常月度分析
	@RequestMapping("/queryBatteryWarningAbnormalMonthlyAnalysis")
	public Object queryBatteryWarningAbnormalMonthlyAnalysis(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		try {
			//test start
			param.setAllOperators(0);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(0);
			param.setStationId("1008601");
			param.setAllEquipments(1);
			param.setEquipmentId("10086001");
			param.setTimeGranularity(6);//4.月、5.季  6.年
			param.setbMSCode("1");
			//param.setStatisticalMonth("202002");
			//param.setStatisticalSeason("202001");
			param.setStatisticalYear("2020");
			//test end
			 Map<String,Object> mapResult = batteryAnalysisMgmt.queryBatteryWarningAbnormalMonthlyAnalysis(param);
			 return new ResultInfo(ResultInfo.OK, mapResult);
		} catch (Exception e) {
			logger.error("queryMonthHistoricalOperationAnalysis_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//获取 五、电池运行情况分析--月/季/年
	@RequestMapping("/queryBatteryOperationMonthlyAnalysis")
	public Object queryBatteryOperationMonthlyAnalysis(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		try {
			//test start
			param.setAllOperators(0);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(0);
			param.setStationId("1008601");
			param.setAllEquipments(1);
			param.setEquipmentId("10086001");
			param.setTimeGranularity(5);//4.月、5.季  6.年
			param.setbMSCode("1");
			//param.setStatisticalMonth("202002");
			param.setStatisticalSeason("202001");
			//param.setStatisticalYear("2020");
			//test end
			AnaBatteryOperationMonthlyAnalysis information = batteryAnalysisMgmt.queryBatteryOperationMonthlyAnalysis(param);
			return new ResultInfo(ResultInfo.OK, information);
		} catch (Exception e) {
			logger.error("queryBatteryOperationMonthlyAnalysis_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
}