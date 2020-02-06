package com.cpit.cpmt.biz.dao.battery;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsMonthCharge;
import com.cpit.cpmt.dto.battery.BatteryDataTrackingAssessmentConditions;
@MyBatisDao
public interface AnaBmsMonthChargeDao {

    int insert(AnaBmsMonthCharge record);

    int insertSelective(AnaBmsMonthCharge record);

    int updateByPrimaryKeySelective(AnaBmsMonthCharge record);

    int updateByPrimaryKey(AnaBmsMonthCharge record);

	List<AnaBmsMonthCharge> queryFirstLevelDataGranularityByMonth(@Param("param")BatteryDataTrackingAssessmentConditions param);

/*	public AnaBmsMonthCharge queryBmsMonthChargeInfo(@Param("param")BatteryDataTrackingAssessmentConditions param);*/
	}