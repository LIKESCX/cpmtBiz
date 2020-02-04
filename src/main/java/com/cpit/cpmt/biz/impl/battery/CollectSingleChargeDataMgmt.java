package com.cpit.cpmt.biz.impl.battery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;

@Service
public class CollectSingleChargeDataMgmt {
	private final static Logger logger = LoggerFactory.getLogger(CollectSingleChargeDataMgmt.class);
	private static ExecutorService msgProThreadPool =  Executors.newFixedThreadPool(50);
	@Autowired
	BmsAnalysisMgmt bmsAnalysisMgmt;
	//接收完整一次的充电过程数据
	@RabbitListener(queues = "exc-single-charge")
	public void CollectSingleChargeData(Message message) {
		Object _reportMsg = null;
		InputStream input = new ByteArrayInputStream(message.getBody());
		ConfigurableObjectInputStream inputStream = null;
		try {
			inputStream = new ConfigurableObjectInputStream(input,
			Thread.currentThread().getContextClassLoader());
			_reportMsg = inputStream.readObject();
		} catch (Exception e) {
			logger.info("RabbitMQ read error: "+e);
		}
		
		if(_reportMsg instanceof BmsInfo) {
			BasicReportMsgInfo reportMsg = (BasicReportMsgInfo)_reportMsg;
			msgProThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					//根据传过来的条件去MongoDB中查询本次要计算的数据
					
					logger.info("RabbitMQ storeDB "+reportMsg.toString());
				}
				
			});
	    
		}else {
			logger.error("RabbitMQ instranceOf fail");
		}
	}
	
	
/*	//@RabbitListener(queues = "exc-single-charge")
	public void CollectSingleChargeData(String msg) {
		Date recTime = new Date();
		if(msg!=null||!"".equals(msg)) {
			JSONArray arr = JSONArray.parseArray(msg);
			try {
				List<BmsInfo> list = JsonUtil.mkList(arr, BmsInfo.class, true);
				//调第三方算法接口
				bmsAnalysisMgmt.obtainAnalysisAll(list,recTime);;
			} catch (Exception e) {
				logger.error("collectSingleChargeData is exception:"+e);
			}
		}else {
			logger.error("msg is null");
		}
	}
	//接收补采后补充完整一次的充电过程数据
	//@RabbitListener(queues = "exc-single-charge-1")
	public void CollectSingleChargeData1(String msg) {
		Date recTime = new Date();
		if(msg!=null||!"".equals(msg)) {
			JSONArray arr = JSONArray.parseArray(msg);
			try {
				List<BmsInfo> list = JsonUtil.mkList(arr, BmsInfo.class, true);
				//调第三方算法接口
				bmsAnalysisMgmt.obtainAnalysisAll(list,recTime);
			} catch (Exception e) {
				logger.error("collectSingleChargeData is exception:"+e);
			}
		}else {
			logger.error("msg is null");
		}
	}
*/}
