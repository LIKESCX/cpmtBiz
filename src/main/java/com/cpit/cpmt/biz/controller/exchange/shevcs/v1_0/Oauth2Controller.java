package com.cpit.cpmt.biz.controller.exchange.shevcs.v1_0;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;



@RestController
@RequestMapping(value= {"/shevcs/v1.0"},method = {RequestMethod.POST})
public class Oauth2Controller{
	
	private final static Logger logger = LoggerFactory.getLogger(Oauth2Controller.class);
	
    @Autowired
    private DiscoveryClient client;

	@Autowired
	private DataSigCheck dataSigCheck;


    @RequestMapping("/query_token")
  	public Object getTokenForOperator(
  			HttpServletRequest request, @RequestBody String content
  			){
		JSONObject jo = JSON.parseObject(content);
		JSONObject decodedDataJO = jo.getJSONObject("Data");
		String clientId = decodedDataJO.getString("OperatorID");
		String secret = decodedDataJO.getString("OperatorSecret");

		Map<String, Object> resMap = new LinkedHashMap<String, Object>();

		Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
		dataMap.put("OperatorID", clientId);
		dataMap.put("SuccStat", 0);
		dataMap.put("AccessToken", null);
		dataMap.put("TokenAvailableTime", null);

		try {
			dataMap.put("OperatorID", clientId);
			OAuth2AccessToken token = createNewToken(clientId, secret);
			// logger.info("---token is "+token+", type="+token.getTokenType());
			dataMap.put("AccessToken", token.getValue());
			dataMap.put("TokenAvailableTime", token.getExpiresIn());
			dataMap.put("FailReason", 0);
			
			resMap.put("Ret", ReturnCode.CODE_OK);
			resMap.put("Msg", "");
			resMap.put("Data", dataMap);
		} catch (Exception ex) {
			logger.error("error in getTokenForOperator", ex);
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_OperatorId_Invalid);
			resMap.put("Data", "");
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}
	//==================================private methods
	


	
	private OAuth2AccessToken createNewToken(String clientId,String secret){
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		
		String tokenUri = obtainUrl();
		
 	    resource.setAccessTokenUri(tokenUri);
		resource.setClientId(clientId);
		resource.setTokenName("oauth_token");
		resource.setId("cpmt");
		resource.setClientSecret(secret);
		resource.setScope(Arrays.asList("read"));
		resource.setUsername("tom");
		resource.setPassword("sonia");
		
		ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
		OAuth2AccessToken accessToken = provider.obtainAccessToken(resource, new DefaultAccessTokenRequest());
		
			
		return accessToken;
	}
	
	private String obtainUrl() {
		List<ServiceInstance> instances = client.getInstances("cpmt-gateway");
		if(instances != null && instances.size() != 0) {
			return "http://"+instances.get(0).getHost() + ":" + instances.get(0).getPort()+"/oauth/token";
		}
		return null;
	}

}
