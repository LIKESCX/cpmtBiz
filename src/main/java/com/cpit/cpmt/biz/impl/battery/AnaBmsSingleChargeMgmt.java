package com.cpit.cpmt.biz.impl.battery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbap.model.WarningResult;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.battery.AnaBmsSingleChargeDao;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.battery.AnaBmsManyChargesDto;
import com.cpit.cpmt.dto.battery.AnaBmsMonthCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSeasonCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.battery.AnaBmsWeekCharge;
import com.cpit.cpmt.dto.battery.AnaBmsYearCharge;

@Service
public class AnaBmsSingleChargeMgmt {
	@Autowired
	AnaBmsSingleChargeDao anaBmsSingleChargeDao;
	//原始计算结果的分析数据入库
	public void insertAnaBmsSingleCharge(AnaBmsSingleCharge anaBmsSingleCharge) {
		anaBmsSingleChargeDao.insertSelective(anaBmsSingleCharge);
	}
	
	//天 定时任务查询计算后的原始数据
	public List<AnaBmsDayCharge> querySumAnaBmsSingleDayCharge(String statisticalDate) {
		return anaBmsSingleChargeDao.selectSumAnaBmsSingleDayCharge(statisticalDate);
	}
	//周 定时任务查询计算后的原始数据
	public List<AnaBmsWeekCharge> querySumAnaBmsSingleWeekCharge(String statisticalWeek) {
		return anaBmsSingleChargeDao.selectSumAnaBmsSingleWeekCharge(statisticalWeek);
	}
	//月 定时任务查询计算后的原始数据
	public List<AnaBmsMonthCharge> querySumAnaBmsSingleMonthCharge(String statisticalMonth) {
		return anaBmsSingleChargeDao.selectSumAnaBmsSingleMonthCharge(statisticalMonth);
	}
	//季 定时任务查询计算后的原始数据
	public List<AnaBmsSeasonCharge> querySumAnaBmsSingleSeasonCharge(String statisticalSeason) {
		return anaBmsSingleChargeDao.selectSumAnaBmsSingleSeasonCharge(statisticalSeason);
	}
	//年 定时任务查询计算后的原始数据
	public List<AnaBmsYearCharge> querySumAnaBmsSingleYearCharge(String statisticalYear) {
		return anaBmsSingleChargeDao.selectSumAnaBmsSingleYearCharge(statisticalYear);
	}
	
	
	public void insertSingleChargeWarningResult(WarningResult warningResult) {
		// TODO Auto-generated method stub
		
	}

	public AnaBmsManyChargesDto queryAnaBmsSingleChargeHour(String newHourTime, String bMSCode, String connectorId, String operatorId) {
		
		return null;
		
	}

	public void insertAnaBmsSingleChargeHour(AnaBmsManyChargesDto anaBmsManyChargesDto) {
		// TODO Auto-generated method stub
		
	}

	public void updateAnaBmsSingleChargeHour(AnaBmsManyChargesDto anaBmsManyChargesDto) {
		// TODO Auto-generated method stub
		
	}

	public AnaBmsManyChargesDto queryAnaBmsSingleChargeDay(String newDayTime, String bMSCode, String connectorId,
			String operatorId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void insertAnaBmsSingleChargeDay(AnaBmsManyChargesDto anaBmsManyChargesparam) {
		// TODO Auto-generated method stub
		
	}

	public void updateAnaBmsSingleChargeDay(AnaBmsManyChargesDto anaBmsManyChargesDto) {
		// TODO Auto-generated method stub
		
	}

	public AnaBmsManyChargesDto queryAnaBmsSingleChargeWeek(String sundayTime, String bMSCode, String connectorId,
			String operatorId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void insertAnaBmsSingleChargeWeek(AnaBmsManyChargesDto anaBmsManyChargesparam) {
		// TODO Auto-generated method stub
		
	}

	public void updateAnaBmsSingleChargeWeek(AnaBmsManyChargesDto anaBmsManyChargesDto) {
		// TODO Auto-generated method stub
		
	}
	
	public AnaBmsManyChargesDto queryAnaBmsSingleChargeMonth(String monthTime, String bMSCode, String connectorId,
			String operatorId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void insertAnaBmsSingleChargeMonth(AnaBmsManyChargesDto anaBmsManyChargesparam) {
		// TODO Auto-generated method stub
		
	}

	public void updateAnaBmsSingleChargeMonth(AnaBmsManyChargesDto anaBmsManyChargesDto) {
		// TODO Auto-generated method stub
		
	}

	public AnaBmsManyChargesDto queryAnaBmsSingleChargeSeason(String seasonTime, String bMSCode, String connectorId,
			String operatorId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void insertAnaBmsSingleChargeSeason(AnaBmsManyChargesDto anaBmsManyChargesparam) {
		// TODO Auto-generated method stub
		
	}

	public void updateAnaBmsSingleChargeSeason(AnaBmsManyChargesDto anaBmsManyChargesDto) {
		// TODO Auto-generated method stub
		
	}

	
}
