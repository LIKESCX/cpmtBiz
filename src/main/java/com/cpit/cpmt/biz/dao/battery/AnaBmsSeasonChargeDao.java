package com.cpit.cpmt.biz.dao.battery;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsSeasonCharge;
@MyBatisDao
public interface AnaBmsSeasonChargeDao {

    int insert(AnaBmsSeasonCharge record);

    int insertSelective(AnaBmsSeasonCharge record);

    int updateByPrimaryKeySelective(AnaBmsSeasonCharge record);

    int updateByPrimaryKey(AnaBmsSeasonCharge record);
}