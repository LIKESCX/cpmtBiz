package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EventInfoDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
import com.cpit.cpmt.dto.exchange.basic.EventItem;

@Service
public class AlarmAndEventCheckMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AlarmAndEventCheckMgmt.class);
	@Autowired EventInfoDao eventInfoDao;
	@Autowired AlarmInfoDao alarmInfoDao;
	
	//获取告警编码,告警类型，告警级别,告警描述的方法
	@Cacheable(cacheNames="alarm-code-type-level",key="#root.caches[0].name+#alarmCode+'-'+#alarmType+'-'+#alarmLevel",unless="#result == null")
	public AlarmItem checkCurrentAlarmValid(Map<String,String> map) {
		logger.info("checkCurrentAlarmValid param map==>>>"+map);
		return alarmInfoDao.checkCurrentAlarmValid(map);
	}
	//获取告警编码,告警类型，告警级别,告警描述的方法
	@Cacheable(cacheNames="event-code-type-level",key="#root.caches[0].name+#eventCode+'-'+#eventType",unless="#result == null")
	public EventItem checkCurrentEventValid(Integer eventCode,Integer eventType) {
		logger.info("checkCurrentEventValid param eventCode="+eventCode+",eventType="+eventType);
		return eventInfoDao.checkCurrentEventValid(eventCode,eventType);
	}
}
