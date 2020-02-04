package com.cpit.cpmt.biz.dao.battery;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsWeekCharge;
@MyBatisDao
public interface AnaBmsWeekChargeDao {

    int insert(AnaBmsWeekCharge record);

    int insertSelective(AnaBmsWeekCharge record);
   
    int updateByPrimaryKeySelective(AnaBmsWeekCharge record);

    int updateByPrimaryKey(AnaBmsWeekCharge record);
}