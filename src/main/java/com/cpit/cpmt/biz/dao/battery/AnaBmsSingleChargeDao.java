package com.cpit.cpmt.biz.dao.battery;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;

@MyBatisDao
public interface AnaBmsSingleChargeDao {
	public int insertSelective(AnaBmsSingleCharge record);
	
	//按天统计汇总结果
	public List<AnaBmsDayCharge> selectSumAnaBmsSingleDayCharge(String statisticalDate);
}
