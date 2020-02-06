package com.cpit.cpmt.biz.controller.battery;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.battery.BatteryDataTrackingAssessmentMgmt;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthBasicInformation;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.battery.BatteryDataTrackingAssessmentConditions;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
//2.2.	电池数据跟踪评估
@RestController
@RequestMapping("/battery")
public class BatteryDataTrackingAssessmentController {
	private final static Logger logger = LoggerFactory.getLogger(BatteryDataTrackingAssessmentController.class);
	
	@Autowired
	private BatteryDataTrackingAssessmentMgmt batteryDataTrackingAssessmentMgmt;
		//-------------------------------------2.2.	电池数据跟踪评估 内容一--------------------------
	/**
	 * 第一级钻取：
	 * 	     按查询条件统计某车辆在某时间段内充电次数；
	 * 	     生成散点图呈现不同时间段内充电次数，x轴为时间，y轴为次数，
	 */
	@GetMapping("/queryFirstLevelData")
	public ResultInfo queryFirstLevelData(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		logger.info("queryFirstLevelData begin params [{}]", param);
		try {
			//test start
			Date startTime =TimeConvertor.stringTime2Date("2020-02-03 19:29:40","yyyy-MM-dd HH:mm:ss");
			Date endTime =TimeConvertor.stringTime2Date("2020-02-03 20:33:37","yyyy-MM-dd HH:mm:ss");
			param.setStartTime(startTime);
			param.setEndTime(endTime); 
			param.setAllOperators(0);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(0);
			param.setStationId("1008601");
			param.setAllEquipments(1);
			param.setEquipmentId("10086001");
			param.setTimeGranularity(5);
			param.setbMSCode("1");
			//test end
			Object obj = batteryDataTrackingAssessmentMgmt.queryFirstLevelData(param);
			return new ResultInfo(ResultInfo.OK, obj);
		} catch (Exception e) {
			logger.error("queryFirstLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	/**
	 * 第二级钻取：
		点击统计次数，向下钻取对应时间内容的充电详细列表，需要传递 本次统计次数所对应的时间点,粒度,电池编码,运营商id,设备接口编码
		输出包括开始时间、结束时间、充电站名称、运营商名称、设备编码、型号等信息，以列表展现；
	 */
	@GetMapping("/querySecondLevelData/{pageNumber}/{pageSize}")
	public ResultInfo querySecondLevelData(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody BatteryDataTrackingAssessmentConditions param) {
		logger.info("querySecondLevelData begin params [{}],pageNumber[{}],pageSize[{}]", param,pageNumber,pageSize);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AnaBmsSingleCharge> infoList = null;
		try {
			//test start
			param.setbMSCode("1");;
			param.setOperatorId("10086");
			param.setConnectorId("100860001");
			param.setStatisticalHour("2020-02-03 19");
			param.setTimeGranularity(1);
			//test end
			if(pageNumber==-1){
				infoList = batteryDataTrackingAssessmentMgmt.querySecondLevelData(param);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = batteryDataTrackingAssessmentMgmt.querySecondLevelData(param);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("querySecondLevelData total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("querySecondLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	/**
	 * 第三级钻取：需要传递 开始充电时间和结束充电时间,电池编码,运营商id,设备接口id
		选中某条第二级钻取充电详情，获取此充电过程的信息，统计数据分析指标值。
	 */
	@GetMapping("/queryThirdLevelData")
	public ResultInfo queryThirdLevelData(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		logger.info("queryThirdLevelData begin params [{}],", param);
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
			List<AnaBmsSingleCharge> infoList = batteryDataTrackingAssessmentMgmt.queryThirdLevelData(param);
			return new ResultInfo(ResultInfo.OK, infoList);
		} catch (Exception e) {
			logger.error("querySecondLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	
	//-------------------------------------2.2.	电池数据跟踪评估 内容二--------------------------
	
	/*//预览报告(当粒度为：月，季 导出3.2.2季报告)
	
	//获取一、基本信息 月
	@RequestMapping("/queryBasicInformation")
	public Object queryMonthBasicInformation(@RequestBody BatteryDataTrackingAssessmentConditions param) {
		try {
			//test start
			Date startTime =TimeConvertor.stringTime2Date("2020-02-03 19:29:40","yyyy-MM-dd HH:mm:ss");
			Date endTime =TimeConvertor.stringTime2Date("2020-02-03 20:33:37","yyyy-MM-dd HH:mm:ss");
			param.setStartTime(startTime);
			param.setEndTime(endTime); 
			param.setAllOperators(0);//0表示单选 1表示多选
			param.setOperatorId("10086");
			param.setAllStations(0);
			param.setStationId("1008601");
			param.setAllEquipments(1);
			param.setEquipmentId("10086001");
			param.setTimeGranularity(5);//4.月、5.季
			param.setbMSCode("1");
			String bmsCode = "1";
			String statisticalMonth="202002";
			//test end
			AnaBatteryMonthBasicInformation information = batteryAnalysisMgmt.queryMonthBasicInformation(bmsCode,statisticalMonth);
			String json = JsonUtil.beanToJson(information);
			return json;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}*/
	
}
