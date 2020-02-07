package com.cpit.cpmt.biz.dao.security.battery;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.AnaBmsWeekCharge;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;
@MyBatisDao
public interface AnaBmsWeekChargeDao {

    int insert(AnaBmsWeekCharge record);

    int insertSelective(AnaBmsWeekCharge record);
   
    int updateByPrimaryKeySelective(AnaBmsWeekCharge record);

    int updateByPrimaryKey(AnaBmsWeekCharge record);

	List<AnaBmsWeekCharge> queryFirstLevelDataGranularityByWeek(@Param("param")BatteryDataTrackingAssessmentConditions param);
}