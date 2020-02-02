package com.cpit.cpmt.biz.impl.exchange.supplement;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SupplementProcess {
	private final static Logger logger = LoggerFactory.getLogger(SupplementProcess.class);
/**
 * 接口进行补采
 * @param operatorId
 * @param infName
 * @param params
 * @param startTime
 * @param endTime
 */
	public void executeSupply(String operatorId,String infName,String startTime,String endTime) {
		
	}
	
	/**
	 * 根据采集结果，生成补采信息
	 */
	public void genSupplyInfo() {
		
	}
}
