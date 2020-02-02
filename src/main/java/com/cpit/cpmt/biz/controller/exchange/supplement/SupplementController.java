package com.cpit.cpmt.biz.controller.exchange.supplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * user for UI query supplementInfo
 * @author admin
 *
 */

import com.cpit.cpmt.biz.controller.exchange.shevcs.v1_0.AlarmInfoControllerN;
import com.cpit.cpmt.biz.impl.exchange.supplement.SupplementLog;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;

import io.swagger.annotations.ApiOperation;
@RestController
@RequestMapping(value = "/exchange/supplement")
public class SupplementController {
	private final static Logger logger = LoggerFactory.getLogger(SupplementController.class);
	@Autowired SupplementLog supplementlog;
	@RequestMapping("/query_supplement_log")
	public Object query_supplement_log(
			@RequestParam(value="operatorID",required=true)String operatorID,
			@RequestParam(value="infName",required=true)String infName,
			@RequestParam(value="startTime",required=true)String startTime,
			@RequestParam(value="endTime",required=true)String endTime
			){
	return	null;
	
	}
	
	
	@RequestMapping("/query_supplement_info")
	public Object query_supplement_info(
			@RequestParam(value="operatorID",required=true)String operatorID,
			@RequestParam(value="infName",required=true)String infName,
			@RequestParam(value="startTime",required=true)String startTime,
			@RequestParam(value="endTime",required=true)String endTime
			){
	return	null;
	
	}
	
/**
 * 执行补采操作，对缺失数据进行补采
 * @param operatorID
 * @param infName 接口名称
 * @param infVer 
 * @param originalTime 原始采集时间
 * @param supplyType 补采类型
 * @return
 */
	@RequestMapping("/exe_supplement")
	public Object exe_supplement(
			@RequestParam(value="operatorID",required=true)String operatorID,
			@RequestParam(value="infName",required=true)String infName,
			@RequestParam(value="infVer",required=true)String infVer,
			@RequestParam(value="originalTime",required=true)String originalTime,
			@RequestParam(value="supplyType",required=true)String supplyType
			
			){
	return	null;
	
	}
	
	
	
	
}