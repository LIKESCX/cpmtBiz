package com.cpit.cpmt.biz.impl.battery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbap.model.WarningResult;
import com.cpit.cpmt.biz.dao.battery.AnaBmsSingleChargeDao;
import com.cpit.cpmt.dto.battery.AnaBmsManyChargesDto;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;

@Service
public class AnaBmsSingleChargeMgmt {
	@Autowired
	AnaBmsSingleChargeDao anaBmsSingleChargeDao;
	//原始计算结果的分析数据入库
	public void insertAnaBmsSingleCharge(AnaBmsSingleCharge anaBmsSingleCharge) {
		anaBmsSingleChargeDao.insertSelective(anaBmsSingleCharge);
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
