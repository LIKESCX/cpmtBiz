package com.cpit.cpmt.biz.utils.exchange;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.Protocol2Parse;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class JsonValidate {
	    private final static Logger logger = LoggerFactory.getLogger(JsonValidate.class);
	    @Autowired DataSigCheck dataSigCheck;
	    //查询的使用
		public  Object validate( String version, String objectName, String json,String...param){
			ReturnCode result = null;
			String decocdContentData = "";
			JSONObject parseObject = null;
			try{
				String Ret = JSON.parseObject(json).getString("Ret");
				//判断请求状态
				if(ReturnCode.CODE_OK==Integer.parseInt(Ret)) {
					logger.info("Ret:"+Ret+",Msg:"+ReturnCode.MSG_OK);
					String data = JSON.parseObject(json).getString("Data");
					//解密部分代码
					decocdContentData = dataSigCheck.decodeContentData(data);
					logger.debug("\n解密后的decocdContentData="+decocdContentData);
					//logger.debug("decocdContentData:"+decocdContentData);
					parseObject = JSON.parseObject(decocdContentData);
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("Data", parseObject);
					String mapJson = JsonUtil.beanToJson(map);
					//JSONObject.parseObject(mapJson);
					result = Protocol2Parse.validate2Parameter(version, objectName, mapJson,param);
				}else if(ReturnCode.CODE_BUSY==Integer.parseInt(Ret)) {
					logger.error("Ret:"+Ret+",Msg:"+ReturnCode.MSG_BUSY);
				}else if(ReturnCode.CODE_BUSY==Integer.parseInt(Ret)) {
					logger.error("Ret:"+Ret+",Msg:"+ReturnCode.MSG_BUSY);
				}else if(ReturnCode.CODE_500==Integer.parseInt(Ret)) {
					logger.error("Ret:"+Ret+",Msg:"+ReturnCode.MSG_500);
				}
			}catch(Exception ex){
				logger.error("error in validateParameter",ex);
			}

			Map<String,Object> map = new HashMap<String,Object>();
			
			if(result == null){
				logger.error("===validate get null");
				map.put("Ret", ReturnCode.CODE_500);
				map.put("Msg", ReturnCode.MSG_500+"0");
				return map;
			}		
			if(result.getCode() != ReturnCode.CODE_OK){
				map.put("Ret", result.getCode());
				map.put("Msg", result.getErrorMsg());
				return map;
			}else{
				Map<String, Object> dataMap = new HashMap<String, Object>();
				/////根据地标规范需要修改返回值
				map.put("Ret", ReturnCode.CODE_OK);
				map.put("Msg", ReturnCode.MSG_OK);
				dataMap.put("Status", 0);
				map.put("Data", parseObject);
				return map;
			}
			
		}
		//推送的使用
		public  Object validate1( String version, String objectName, String json,String...param) throws Exception{
			ReturnCode result = null;
			JSONObject parseObject = null;
			try{
			result = Protocol2Parse.validate2Parameter(version, objectName, json,param);
			}catch(Exception ex){
				throw new Exception("推送的数据格式有问题,请修改后,再推!");
			}
			Map<String,Object> map = new HashMap<String,Object>();
			if(result == null){
				logger.error("===validate get null");
				map.put("Ret", ReturnCode.CODE_500);
				map.put("Msg", ReturnCode.MSG_500+"0");
				return map;
			}		
			if(result.getCode() != ReturnCode.CODE_OK){
				map.put("Ret", result.getCode());
				map.put("Msg", result.getErrorMsg());
				return map;
			}else{
				Map<String, Object> dataMap = new HashMap<String, Object>();
				String data = JSON.parseObject(json).getString("Data");
				parseObject = JSON.parseObject(data);
				/////根据地标规范需要修改返回值
				map.put("Ret", ReturnCode.CODE_OK);
				map.put("Msg", ReturnCode.MSG_OK);
				dataMap.put("Status", 0);
				map.put("Data", parseObject);
				return map;
			}
			
		}
		public static String chgToStr(Object obj){
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(obj);
			} catch (Exception e) {
				return "";
			}		
		}
}
