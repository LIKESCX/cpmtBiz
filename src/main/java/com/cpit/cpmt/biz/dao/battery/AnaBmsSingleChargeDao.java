package com.cpit.cpmt.biz.dao.battery;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;

@MyBatisDao
public interface AnaBmsSingleChargeDao {
	int insertSelective(AnaBmsSingleCharge record);
}
