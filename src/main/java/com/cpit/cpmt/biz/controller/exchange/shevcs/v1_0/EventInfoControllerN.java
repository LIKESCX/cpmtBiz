package com.cpit.cpmt.biz.controller.exchange.shevcs.v1_0;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.exchange.basic.EventInfoMgmt;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value={"/shevcs/v1.0"},method = {RequestMethod.POST})
public class EventInfoControllerN {
	private final static Logger logger = LoggerFactory.getLogger(EventInfoControllerN.class);
	
	@Autowired EventInfoMgmt eventInfoMgmt;
	@Autowired DataSigCheck dataSigCheck;
			
	@ApiOperation(value = "事件信息推送的接口(充电运营商充电站事件信息发生变化时，主动上报市级平台)")
	@RequestMapping("/notification_eventInfo")
	public Object notificationEventInfo(HttpServletRequest request, @RequestBody String content){
		//中心平台收到推送消息的时间
		Date receivedTime = new Date();
		logger.info("notification_eventInfo_request_receivedTime is:\n "+receivedTime);
		//打印接受到的推送消息
		logger.debug("notification_eventInfo_content is:\n "+content);
		String objectName="EventInfos";
		return eventInfoMgmt.notificationEventInfo(content,receivedTime,objectName);
	}
	
	/** 
	* 判断时间格式是否正确 
	* @param str 
	* @return 
	*/ 
	private  boolean isValidDate(String str) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // 这里的时间格式根据自己需求更改（注意：格式区分大小写、格式区分大小写、格式区分大小写）
		try {
			Date date = (Date) formatter.parse(str);
			return str.equals(formatter.format(date));
		} catch (Exception e) {
			return false;
		}
	}
}
