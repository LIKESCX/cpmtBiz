package com.cpit.cpmt.biz.impl.battery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.battery.AnaBmsDayChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsMonthChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsSeasonChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsWeekChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsYearChargeDao;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.battery.AnaBmsMonthCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSeasonCharge;
import com.cpit.cpmt.dto.battery.AnaBmsWeekCharge;
import com.cpit.cpmt.dto.battery.AnaBmsYearCharge;

@Service
public class SummaryMgmt {
	@Autowired AnaBmsDayChargeDao anaBmsDayChargeDao;
	@Autowired AnaBmsWeekChargeDao anaBmsWeekChargeDao;
	@Autowired AnaBmsMonthChargeDao anaBmsMonthChargeDao;
	@Autowired AnaBmsSeasonChargeDao anaBmsSeasonChargeDao;
	@Autowired AnaBmsYearChargeDao anaBmsYearChargeDao;
	
	public void insertAnaBmsDayCharge(AnaBmsDayCharge anaBmsDayCharge) {
		anaBmsDayChargeDao.insertSelective(anaBmsDayCharge);
	}

	public void insertAnaBmsWeekCharge(AnaBmsWeekCharge anaBmsWeekCharge) {
		anaBmsWeekChargeDao.insertSelective(anaBmsWeekCharge);
	}
	
	public void insertAnaBmsMonthCharge(AnaBmsMonthCharge anaBmsMonthCharge) {
		anaBmsMonthChargeDao.insertSelective(anaBmsMonthCharge);
	}
	
	public void insertAnaBmsSeasonCharge(AnaBmsSeasonCharge anaBmsSeasonCharge) {
		anaBmsSeasonChargeDao.insertSelective(anaBmsSeasonCharge);
	}
	
	public void insertAnaBmsYearCharge(AnaBmsYearCharge anaBmsYearCharge) {
		anaBmsYearChargeDao.insertSelective(anaBmsYearCharge);
	}
	
}
