package com.cpit.cpmt.biz.dao.battery;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;
@MyBatisDao
public interface AnaBmsDayChargeDao {
    int insert(AnaBmsDayCharge record);

    int insertSelective(AnaBmsDayCharge record);

    int updateByPrimaryKeySelective(AnaBmsDayCharge record);

    int updateByPrimaryKey(AnaBmsDayCharge record);
}