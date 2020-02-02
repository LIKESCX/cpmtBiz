package com.cpit.cpmt.biz.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.common.SpringContextHolder;

public class SmsUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(SmsUtil.class);

	private static String serverUrl = "";
	
	static {
		Environment environment = SpringContextHolder.getApplicationContext().getEnvironment();
		serverUrl = environment.getProperty("sms.server.url");

	}
	
	public static JSONObject getToken(){
		JSONObject jsonObject = null;
		try {
			String appid = "e158e100ffad42c28da0073786ca0b81";
			String appsecret = "1c36490597e44e4292108c0843a1c1da";
			HttpClient httpClient = HttpClients.createDefault();
			String URL = serverUrl+"B/BasicApi/GetAccessToken?appid="+appid+"&appsecret="+appsecret;
			HttpGet httpGet = new HttpGet(URL);
			HttpResponse execute = httpClient.execute(httpGet);
			HttpEntity entity = execute.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			jsonObject = JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("getToken fail:"+e);
		}
		return jsonObject;
	}
	
	public static Object sendMessage(String accessToken,String phone,String SmsContent) {
		JSONObject jsonObject = null;
		try {
			String authorSecret = "b58cb57e7d634197a585dd40d37d5f8e";
			String url = serverUrl+"C/SmsApi/SendSmsToUser";
			HttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("authorSecret", authorSecret);
			httpPost.setHeader("accessToken", accessToken);
			
			Map<String, String> map = new HashMap<>();
			map.put("Phone", phone);
			map.put("SmsContent", SmsContent);
			
			String json = JsonUtil.beanToJson(map);
			StringEntity param = new StringEntity(json,"utf-8");
			httpPost.setEntity(param);
			HttpResponse execute = httpClient.execute(httpPost);
			HttpEntity entity = execute.getEntity();
			String string = EntityUtils.toString(entity, "utf-8");
			jsonObject = JSON.parseObject(string);
		} catch (Exception e) {
			logger.error("getToken fail:"+e);
		}
		return jsonObject;
	}
	
	

}
