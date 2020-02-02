package com.cpit.cpmt.biz.controller.exchange.supplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/exchange/supplement")
public class GenSuppleMentInf {
	private final static Logger logger = LoggerFactory.getLogger(GenSuppleMentInf.class);
	/**
	 * 生成补采信息，根据采集结果，定时任务。
	 * 每天执行。
	 * @return
	 */
	@RequestMapping("/genSuppleMentInfo")
	public Object genSuppleMentInfo() {
		return null;
	}
}
