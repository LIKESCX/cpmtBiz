package com.cpit.cpmt.biz.utils.exchange;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.controller.exchange.shevcs.v1_0.StationsInfoControllerN;
import com.cpit.cpmt.biz.impl.exchange.basic.StationsInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;

@Service
public class TokenUtil {
	private final static Logger logger = LoggerFactory.getLogger(TokenUtil.class);
	@Autowired AccessManageMgmt accessManageMgmt ;
	@Autowired
	DataSigCheck dataSigCheck;
	@Autowired CacheUtil cacheUtil;

	  @Value("${platform.operator.id}")
	   private String self_operatorID;
	   
	    @Value("${secret.key.operator.id}")
	    private String operatorSecret;

	public String getToken(String operatorId) {
		String token = cacheUtil.getTokenByCache(operatorId);
		OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(token);

		boolean tokenValidate = accessToken.isExpired();
		if(!tokenValidate) {
			return token;
		}else {
			String token_;
			try {
				token_ = queryToken(operatorId);
				cacheUtil.upToken(operatorId,token_);
				 return token_;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(operatorId +" get token error",e);
				return null;
			}
			
			
		}
	}
	
	

	
	public String queryToken(String operatorID) throws Exception{
		//String url1 = "http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_token";
		//		String url ="http://124.205.228.170:48080/evcs/20160701/query_token";
				AccessManage accessManage = accessManageMgmt.getAccessManageInfoById(operatorID);
			String	 url = accessManage.getSecretKeyAddress();
//System.out.println(url.equals(url1));
				RestTemplate restTemplate =new RestTemplate();
				Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
				dataMap.put("OperatorID", self_operatorID);
				dataMap.put("OperatorSecret", operatorSecret);
		        
			
				String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
				//String timeStamp = "20191010143640";
				//
				String seq = SeqUtil.getUniqueInstance().getSeq();
				// seq = "0001";
				String data = JsonUtil.beanToJson(dataMap);
			
				data = dataSigCheck.encodeContentData(data);
				
				String msg = self_operatorID+data+timeStamp+seq;
				String sig = dataSigCheck.genSign(msg);
		        
				Map<String,Object> reqMap = new LinkedHashMap<String,Object>();
				reqMap.put("OperatorID", self_operatorID);
				reqMap.put("Data", data);
				reqMap.put("TimeStamp", timeStamp);
				reqMap.put("Seq", seq);
				reqMap.put("Sig",sig);
			
				String json = JsonUtil.beanToJson(reqMap);
				
				Dispatcher dispatcher = new Dispatcher(restTemplate);
				String result = (String)dispatcher.doPost(url, String.class, json);
				
			Map<String,Object>	resMap = (Map<String,Object>)JsonUtil.jsonToBean(result, Map.class);
			String encodeData = (String) resMap.get("Data");
			String o_data = dataSigCheck.decodeContentData(encodeData);
if(null==o_data ||"".equals(o_data)) {
	return null;
}else {

	String token = JSON.parseObject(o_data).getString("AccessToken");
		//token = (String)o_data.get("AccessToken");
	return token;
}
	}
}
