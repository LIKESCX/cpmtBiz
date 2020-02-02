package com.cpit.cpmt.biz.impl.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.system.PoliciesPublish;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestPoliciesPublishMgmt {
	
	@Autowired
	private PoliciesPublishMgmt policiesPublishMgmt;
	
	@Test
	public void addPolicyPublish() {
		Date date = new Date();
		PoliciesPublish policiesPublish = new PoliciesPublish();
		policiesPublish.setPolicyId(1);
		policiesPublish.setPolicyName("全国人大常委第十七届三中全会");
		policiesPublish.setPolicyContent("暗室逢灯卡健身房卡视角的罚款时代峻峰has");
		policiesPublish.setPolicyLevel(1);
		policiesPublish.setPolicyPerson("asfdasdfasdf");
		policiesPublish.setPolicyType(1);
		policiesPublish.setProCapital(1);
		policiesPublish.setInTime(date);
		policiesPublish.setStatusCd(1);
		policiesPublishMgmt.addPolicyPublish(policiesPublish);
	}
	
	@Test
	public void update() {
		PoliciesPublish policiesPublish = new PoliciesPublish();
		policiesPublish.setPolicyId(4);
		//policiesPublish.setPolicyName("全国人大常委第十八届三中全会");
		//policiesPublish.setPolicyContent("按时发达是打发点");
		policiesPublish.setPolicyLevel(1);
		policiesPublish.setPolicyPerson("zxcvzxcv");
		policiesPublish.setPolicyType(3);
		policiesPublish.setProCapital(2);
		policiesPublish.setStatusCd(4);
		policiesPublish.setAuditNote("asfdasdfasfdafds");
		policiesPublishMgmt.updatePoliciesPublish(policiesPublish);
	}
	
	@Test
	public void testQuery() {
		try {
//			Calendar cal = Calendar.getInstance();
//			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
//			Date date = new Date();
			PoliciesPublish policiesInfo = (PoliciesPublish) policiesPublishMgmt.getPoliciesInfo(10);
			System.out.println(policiesInfo);
//			PoliciesPublish publish = new PoliciesPublish();
////			publish.setStartTime(cal.getTime());
////			publish.setEndTime(date);
//			publish.setPolicyId(10);
//			Page<OperatorInfoExtend> policiesPublishList = policiesPublishMgmt.getPoliciesPublishList(publish);
//			System.out.println(policiesPublishList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
