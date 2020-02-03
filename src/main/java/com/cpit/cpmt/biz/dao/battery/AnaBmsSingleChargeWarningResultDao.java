package com.cpit.cpmt.biz.dao.battery;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsSingleChargeWarningResult;
@MyBatisDao
public interface AnaBmsSingleChargeWarningResultDao {
    int insertSelective(AnaBmsSingleChargeWarningResult record);
}