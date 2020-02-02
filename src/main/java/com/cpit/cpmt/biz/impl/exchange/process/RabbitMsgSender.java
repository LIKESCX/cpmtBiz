package com.cpit.cpmt.biz.impl.exchange.process;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;



@Service
public class RabbitMsgSender {

    private final static Logger logger = LoggerFactory.getLogger(RabbitMsgSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;
  
    

    public void send(BasicReportMsgInfo repotMsg) {
       // logger.info("RabbitMQ sender,  :"+repotMsg.toString());
        try {
            amqpTemplate.convertAndSend("exc-report-msg",repotMsg);
            logger.info("RabbitMQ sender, success :"+repotMsg.toString()); 
        } catch (AmqpException e) {
            logger.error("RabbitMQ sender,monRechargeRecord",e);
        }
         
    }
    
    public void sendRealTimeAlarm(String msg) {
    	try {
    		amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME,msg);
    		logger.info("RabbitMQ sender, success :"+msg); 
    	} catch (AmqpException e) {
    		logger.error("RabbitMQ sender,sendRealTimeAlarm",e);
    	}
    	
    }

    public void sendConnectorStatus(String alarmNum){
        try{
            amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME,alarmNum);
            logger.info("RabbitMQ sender, success :"+alarmNum);
        }catch (AmqpException e){
            logger.error("RabbitMQ sender,sendConnectorStatus",e);
        }
    }

	public void sendRealTimeBms(String msg) {
		try{
            amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME,msg);
            logger.info("RabbitMQ sender, success :"+msg);
        }catch (AmqpException e){
            logger.error("RabbitMQ sender,sendRealTimeBms",e);
        }
		
	}

 
}
