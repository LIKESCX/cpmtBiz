package com.cpit.cpmt.biz.controller.exchange.basic.shevcs.v1_0;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestOauth2 {
	
	private String token;
	
	@Autowired 
	private DataSigCheck dataSigCheck;


	//无ssl的
    @Test
    public void testHttp() throws Exception{
    	queryToken();
    	
    	String url = "http://localhost:28010/shevcs/v1.0/notification_stationInfo";
    	
		OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(token);
		if(accessToken.isExpired()) {
			System.out.println("===token expired");
			return;
			
		}
		RestTemplate template = new RestTemplate();
		Dispatcher dispatcher = new Dispatcher(template);
		String result = (String)dispatcher.doPost(token, url, String.class, "");
		System.out.println(result);

    }
    

	@Test
	//@Before
	public void queryToken() throws Exception {
		String url = "http://localhost:28010/shevcs/v1.0/query_token";
		RestTemplate restTemplate = new RestTemplate();
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String json = mkGetTokenPostData();
		String result = (String)dispatcher.doPost(url, String.class, json);
		System.out.println("===result:"+result);
		Map<String,Object> reqMap = (Map<String,Object>)JsonUtil.jsonToBean(result, Map.class);
		String data = (String)reqMap.get("Data");
		if(data != null && data.length() != 0) {
			data = dataSigCheck.decodeContentData(data);
			Map dataMap = JsonUtil.jsonToBean(data, Map.class);
			token = (String)dataMap.get("AccessToken");
			System.out.println("==="+token);
		}		
	}
	
	//有ssl访问的
    @Test
    public void testHttps() throws Exception{
    	queryTokenHttps();
    	
    	//String url = "https://120.241.28.5:8020/shevcs/v1.0/notification_stationInfo";
     	String url = "https://localhost:28011/shevcs/v1.0/notification_stationInfo";

		OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(token);
		if(accessToken.isExpired()) {
			System.out.println("===token expired");
			return;
			
		}
		RestTemplate template = sslTemplate();
		Dispatcher dispatcher = new Dispatcher(template);
		String result = (String)dispatcher.doPost(token, url, String.class, "");
		System.out.println(result);

    }
	
	@Test
	//@Before
	public void queryTokenHttps() throws Exception {
		//String url = "https://120.241.28.5:8020/shevcs/v1.0/query_token";
		String url = "https://localhost:28011/shevcs/v1.0/query_token";

		RestTemplate restTemplate = sslTemplate();

	
		String json = mkGetTokenPostData();
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String result = (String)dispatcher.doPost(url, String.class, json);
		System.out.println("===>>"+result);
		Map<String,Object> reqMap = (Map<String,Object>)JsonUtil.jsonToBean(result, Map.class);
		String data = (String)reqMap.get("Data");
		if(data != null && data.length() != 0) {
			data = dataSigCheck.decodeContentData(data);
			Map dataMap = JsonUtil.jsonToBean(data, Map.class);
			token = (String)dataMap.get("AccessToken");
			System.out.println("==="+token);
		}
		
	}
	
	//证书
	final static String URL = "http://120.241.28.5:16668/cpmt/security/client.jks";
	//证书密码
	final static String PWD = "123456";
	
    private RestTemplate sslTemplate() throws Exception {
      SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
      .loadTrustMaterial(new URL(URL), PWD.toCharArray())
      .build();
 
      SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();


        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
    

    private String mkGetTokenPostData() {
		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		//dataMap.put("OperatorID", "395815801");
		dataMap.put("OperatorID", "395815801");
		dataMap.put("OperatorSecret", "ABCDEF012345678900");
        
		String operatorID = "395815801";
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = "0001";
		
		String data = "";
		try {
			data = JsonUtil.beanToJson(dataMap);
		} catch (Exception e1) {
			return "";
		}
		
		//System.out.println("src data:\n"+data);
		data = dataSigCheck.encodeContentData(data);
		//System.out.println("encoded data:\n"+data);
		
		String msg = operatorID+data+timeStamp+seq;
		String sig = dataSigCheck.genSign(msg);
        
		Map<String,Object> reqMap = new HashMap<String,Object>();
		reqMap.put("OperatorID", operatorID);
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", seq);
		reqMap.put("Sig",sig);
	
		String json = "";
		try {
			json = JsonUtil.beanToJson(reqMap);
		} catch (Exception e) {
		}
		return json;

    }

}
