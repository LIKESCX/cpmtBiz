package com.cpit.cpmt.biz.dao.security.battery;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.AnaBmsSeasonCharge;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;
@MyBatisDao
public interface AnaBmsSeasonChargeDao {

    int insert(AnaBmsSeasonCharge record);

    int insertSelective(AnaBmsSeasonCharge record);

    int updateByPrimaryKeySelective(AnaBmsSeasonCharge record);

    int updateByPrimaryKey(AnaBmsSeasonCharge record);

	List<AnaBmsSeasonCharge> queryFirstLevelDataGranularityBySeason(@Param("param")BatteryDataTrackingAssessmentConditions param);
}