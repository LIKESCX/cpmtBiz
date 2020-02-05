package com.cpit.cpmt.biz.controller.battery;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.battery.BatteryDataTrackingAssessmentMgmt;
import com.cpit.cpmt.dto.battery.BatteryDataTrackingAssessmentConditions;
//2.2.	电池数据跟踪评估
@RestController
@RequestMapping("/battery")
public class BatteryDataTrackingAssessmentController {
	private final static Logger logger = LoggerFactory.getLogger(BatteryDataTrackingAssessmentController.class);
	
	@Autowired
	private BatteryDataTrackingAssessmentMgmt batteryDataTrackingAssessmentMgmt;
	/**
	 * 第一级钻取：
	 * 	     按查询条件统计某车辆在某时间段内充电次数；
	 * 	     生成散点图呈现不同时间段内充电次数，x轴为时间，y轴为次数，
	 */
	@GetMapping("/queryFirstLevelData")
	public Object queryFirstLevelData(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		logger.info("queryFirstLevelData begin params [{}]", param);
		try {
			//test start
			Date startTime =TimeConvertor.stringTime2Date("2020-02-03 19:29:40","yyyy-MM-dd HH:mm:ss");
			Date endTime =TimeConvertor.stringTime2Date("2020-02-03 20:33:37","yyyy-MM-dd HH:mm:ss");
			param.setStartTime(startTime);
			param.setEndTime(endTime);
			param.setAllOperators(1);
			param.setTimeGranularity(1);
			//test end
			Object obj = batteryDataTrackingAssessmentMgmt.queryFirstLevelData(param);
			String str  = JsonUtil.beanToJson(obj);
			return str;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	/**
	 * 第二级钻取：
		点击统计次数，向下钻取对应时间内容的充电详细列表，需要传递 本次统计次数所对应的时间点,粒度,电池编码,运营商id,设备接口编码
		包括开始时间、结束时间、充电站名称、运营商名称、设备编码、型号等信息，以列表展现；
	 */
	@GetMapping("/querySecondLevelData")
	public Object querySecondLevelData(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		logger.info("querySecondLevelData begin params [{}]", param);
		try {
			//test start
			param.setbMSCode("1");;
			param.setOperatorId("10086");
			param.setConnectorId("100860001");
			param.setStartStatisticalHour("2020-02-03 19");
			param.setTimeGranularity(1);
			//test end
			Object obj = batteryDataTrackingAssessmentMgmt.querySecondLevelData(param);
			String str  = JsonUtil.beanToJson(obj);
			return str;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	/**
	 * 第三级钻取：需要传递 开始充电时间和结束充电时间,电池编码,运营商id,设备接口id
		第三级钻取：选中某条第二级钻取充电详情，获取此充电过程的信息，统计数据分析指标值。
	 */
	@GetMapping("/queryThirdLevelData")
	public Object queryThirdLevelData(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		logger.info("queryThirdLevelData begin params [{}]", param);
		try {
			//test start
			Date startTime =TimeConvertor.stringTime2Date("2020-02-03 19:29:31","yyyy-MM-dd HH:mm:ss");
			Date endTime =TimeConvertor.stringTime2Date("2020-02-03 19:29:40","yyyy-MM-dd HH:mm:ss");
			param.setStartTime(startTime);
			param.setEndTime(endTime);
			param.setbMSCode("1");;
			param.setOperatorId("10086");
			param.setConnectorId("100860001");
			//test end
			Object obj = batteryDataTrackingAssessmentMgmt.queryThirdLevelData(param);
			String str  = JsonUtil.beanToJson(obj);
			return str;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
}
