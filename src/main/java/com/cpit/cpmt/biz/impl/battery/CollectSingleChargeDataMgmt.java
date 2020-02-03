package com.cpit.cpmt.biz.impl.battery;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.bbap.model.BmsInfo;
import com.cpit.common.JsonUtil;

@Service
public class CollectSingleChargeDataMgmt {
	private final static Logger logger = LoggerFactory.getLogger(CollectSingleChargeDataMgmt.class);
	
	@Autowired
	BmsAnalysisMgmt bmsAnalysisMgmt;
	//接收完整一次的充电过程数据
	//@RabbitListener(queues = "exc-single-charge")
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
}
