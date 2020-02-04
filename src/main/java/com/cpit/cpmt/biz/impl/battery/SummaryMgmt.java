package com.cpit.cpmt.biz.impl.battery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.battery.AnaBmsDayChargeDao;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;

@Service
public class SummaryMgmt {
	@Autowired AnaBmsDayChargeDao anaBmsDayChargeDao;
	
	public void insertAnaBmsDayCharge(AnaBmsDayCharge anaBmsDayCharge) {
		anaBmsDayChargeDao.insert(anaBmsDayCharge);
	}
	
}
