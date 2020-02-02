package com.cpit.cpmt.biz.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;

@Service
public class CacheUtil {
	private final static Logger logger = LoggerFactory.getLogger(CacheUtil.class);
@Autowired TokenUtil tokenUtil;
  
	/**
 * exc-connectorStatus  key:operatorId_connectorId  value status
 * @param key default '-1' for first time;
 * @return
 */
	@Cacheable(cacheNames= "exc-connectorStatus", key="#root.caches[0].name+#key")
	public String  getConnectorStatus(String key) {
	
	return "-1";	
	}
	
	@CachePut(cacheNames= "exc-connectorStatus", key="#root.caches[0].name+#key")
	public String  setConnectorStatus(String key,String status) {
	
	return status;	
	}

	@CacheEvict(cacheNames= "exc-connectorStatus", key="#root.caches[0].name+#key")
	public void  delConnectorStatus(String key) {
	
	
	}
	
	
	@CachePut(cacheNames= "exc-token", key="#root.caches[0].name+#key")
	public String upToken(String key,String token) {
		return token;
	}
	@CachePut(cacheNames= "exc-token", key="#root.caches[0].name+#key" )
	public String getTokenByCache(String key) {
		String token = null;
		try {
			token = tokenUtil.queryToken(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}
	@CacheEvict(cacheNames= "exc-token", key="#root.caches[0].name+#key")
	public void delToken(String key) {
		
	}
	
	
}
