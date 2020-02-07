package com.cpit.cpmt.biz.dao.security.battery;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.AnaBmsYearCharge;
@MyBatisDao
public interface AnaBmsYearChargeDao {

    int insert(AnaBmsYearCharge record);

    int insertSelective(AnaBmsYearCharge record);

    int updateByPrimaryKeySelective(AnaBmsYearCharge record);

    int updateByPrimaryKey(AnaBmsYearCharge record);
}