package com.cpit.cpmt.biz.dao.battery;

import com.cpit.cpmt.dto.battery.AnaBmsWeekCharge;

public interface AnaBmsWeekChargeDao {

    int insert(AnaBmsWeekCharge record);

    int insertSelective(AnaBmsWeekCharge record);
   
    int updateByPrimaryKeySelective(AnaBmsWeekCharge record);

    int updateByPrimaryKey(AnaBmsWeekCharge record);
}