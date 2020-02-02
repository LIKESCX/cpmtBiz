package com.cpit.cpmt.biz.impl.exchange.process;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.impl.exchange.basic.StationChargeStatsMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.UnionStoreMgmt;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
@Service
public class ReportMsgProcess {
	private final static Logger logger = LoggerFactory.getLogger(ReportMsgProcess.class);
	
	private static ExecutorService msgProThreadPool =  Executors.newFixedThreadPool(50);
	@Autowired UnionStoreMgmt unionStoreMgmt;
	@RabbitListener(queues = "exc-report-msg")
	public void reportMsgProc(Message msg) {
		Object _reportMsg = null;
		InputStream input = new ByteArrayInputStream(msg.getBody());
		ConfigurableObjectInputStream inputStream = null;
		try {
			inputStream = new ConfigurableObjectInputStream(input,
			Thread.currentThread().getContextClassLoader());
			_reportMsg = inputStream.readObject();
		} catch (Exception e) {
			logger.info("RabbitMQ read error: "+e);
		}
	
	if(_reportMsg instanceof BasicReportMsgInfo) {
		BasicReportMsgInfo reportMsg = (BasicReportMsgInfo)_reportMsg;
		msgProThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				unionStoreMgmt.storeDB(reportMsg);
				logger.info("RabbitMQ storeDB "+reportMsg.toString());
			}
			
		});
  		//logger.info("RabbitMQ getResportMsg: "+reportMsg.toString());
    
	}else {
		logger.error("RabbitMQ instranceOf fail");
	}
	}
}
