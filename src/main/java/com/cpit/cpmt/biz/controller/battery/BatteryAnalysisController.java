package com.cpit.cpmt.biz.controller.battery;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.impl.battery.BatteryAnalysisMgmt;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthBasicInformation;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthHistoricalOperationAnalysis;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthPerformanceHistoryAnalysis;

@RestController
@RequestMapping("/battery")
//3.2.1.	动力电池分析报告-月报告
public class BatteryAnalysisController {
	private final static Logger logger = LoggerFactory.getLogger(BatteryAnalysisController.class);
	@Autowired BatteryAnalysisMgmt batteryAnalysisMgmt;
	//获取一、基本信息
	@RequestMapping("/queryMonthBasicInformation")
	public Object queryMonthBasicInformation(@RequestBody Map map) {
		try {
			String bmsCode = (String) map.get("bmsCode");
			String statisticalMonth = (String) map.get("statisticalMonth");
			//test start
			bmsCode = "1";
			statisticalMonth="202002";
			//test end
			AnaBatteryMonthBasicInformation information = batteryAnalysisMgmt.queryMonthBasicInformation(bmsCode,statisticalMonth);
			String json = JsonUtil.beanToJson(information);
			return json;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	//获取 二、电池性能历史分析
	@RequestMapping("/queryMonthPerformanceHistoryAnalysis")
	public Object queryMonthPerformanceHistoryAnalysis(@RequestBody Map map) {
		try {
			String bmsCode = (String) map.get("bmsCode");
			String statisticalMonth = (String) map.get("statisticalMonth");
			//test start
			bmsCode = "1";
			statisticalMonth="202002";
			//test end
			List<AnaBatteryMonthPerformanceHistoryAnalysis> information = batteryAnalysisMgmt.queryMonthPerformanceHistoryAnalysis(bmsCode,statisticalMonth);
			String json = JsonUtil.beanToJson(information);
			return json;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	//获取 三、电池运行情况历史分析
	@RequestMapping("/queryMonthHistoricalOperationAnalysis")
	public Object queryMonthHistoricalOperationAnalysis(@RequestBody Map map) {
		try {
			String bmsCode = (String) map.get("bmsCode");
			String statisticalMonth = (String) map.get("statisticalMonth");
			//test start
			bmsCode = "1";
			statisticalMonth="202002";
			//test end
			List<AnaBatteryMonthHistoricalOperationAnalysis> information = batteryAnalysisMgmt.queryMonthHistoricalOperationAnalysis(bmsCode,statisticalMonth);
			String json = JsonUtil.beanToJson(information);
			return json;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	//获取 四、电池告警异常月度分析
	
	
	//获取 五、电池运行情况月度分析
	
	
}