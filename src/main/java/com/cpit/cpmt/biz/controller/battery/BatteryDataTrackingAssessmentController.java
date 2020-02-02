package com.cpit.cpmt.biz.controller.battery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@GetMapping("/chargeTimes")
	public void queryBatteryChargingTimesByConditions(@RequestBody BatteryDataTrackingAssessmentConditions bdtac) {
		logger.info("batteryChargingTimesByConditions begin params [{}]", bdtac);
		try {
			batteryDataTrackingAssessmentMgmt.queryBatteryChargingTimesByConditions(bdtac);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
