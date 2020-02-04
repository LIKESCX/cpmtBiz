package com.cpit.cpmt.biz.dao.battery;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsMonthCharge;
@MyBatisDao
public interface AnaBmsMonthChargeDao {

    int insert(AnaBmsMonthCharge record);

    int insertSelective(AnaBmsMonthCharge record);

    int updateByPrimaryKeySelective(AnaBmsMonthCharge record);

    int updateByPrimaryKey(AnaBmsMonthCharge record);
}