package com.cpit.cpmt.biz.dao.security.battery;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;
@MyBatisDao
public interface AnaBmsDayChargeDao {
    int insert(AnaBmsDayCharge record);

    int insertSelective(AnaBmsDayCharge record);

    int updateByPrimaryKeySelective(AnaBmsDayCharge record);

    int updateByPrimaryKey(AnaBmsDayCharge record);

	List<AnaBmsDayCharge> queryFirstLevelDataGranularityByDay(@Param("param")BatteryDataTrackingAssessmentConditions param);
}