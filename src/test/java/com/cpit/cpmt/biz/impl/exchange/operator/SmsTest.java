package com.cpit.cpmt.biz.impl.exchange.operator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.biz.utils.SmsUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class SmsTest {

	String token ;
	
	@Test
	@Before
	public void getToken(){
		try {
			JSONObject jsonObject = SmsUtil.getToken();
			System.out.println(jsonObject);
			token = jsonObject.getJSONObject("resultData").getString("accessToken");
			System.out.println(token);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void sendMessage() {
		SmsUtil.sendMessage(token, "", "\nhhh\nhello world");
	}
}
