package com.cpit.cpmt.biz.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestSmsUtil {
	
	@Test
	public void obtainToken() {
		JSONObject jo = SmsUtil.getToken();
		System.out.println("token is"+jo);
	}

}
