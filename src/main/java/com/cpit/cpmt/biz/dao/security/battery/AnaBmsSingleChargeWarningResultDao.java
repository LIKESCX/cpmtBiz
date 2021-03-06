package com.cpit.cpmt.biz.dao.security.battery;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.AbnormalAlarmDataMiningConditions;
import com.cpit.cpmt.dto.security.battery.AnaBmsSingleChargeWarningResult;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;
import com.cpit.cpmt.dto.security.battery.BatteryWarningAbnormalMonthlyAnalysis;
@MyBatisDao
public interface AnaBmsSingleChargeWarningResultDao {
    int insertSelective(AnaBmsSingleChargeWarningResult record);
	
	public BatteryWarningAbnormalMonthlyAnalysis queryMonthlyWarningTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	public BatteryWarningAbnormalMonthlyAnalysis querySeasonlyWarningTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	public BatteryWarningAbnormalMonthlyAnalysis queryYearlyWarningTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	
	public List<BatteryWarningAbnormalMonthlyAnalysis> queryMonthlyEachWarningCodeTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	public List<BatteryWarningAbnormalMonthlyAnalysis> querySeasonlyEachWarningCodeTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	public List<BatteryWarningAbnormalMonthlyAnalysis> queryYearlyEachWarningCodeTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	
	public List<BatteryWarningAbnormalMonthlyAnalysis> queryMonthlyEachWarningLevelTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	public List<BatteryWarningAbnormalMonthlyAnalysis> querySeasonlyEachWarningLevelTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);
	public List<BatteryWarningAbnormalMonthlyAnalysis> queryYearlyEachWarningLevelTimes(@Param("param")BatteryDataTrackingAssessmentConditions param);

	public List<BatteryWarningAbnormalMonthlyAnalysis> queryBatteryAlarmTimesStatistics(@Param("param")AbnormalAlarmDataMiningConditions param);

	List<BatteryWarningAbnormalMonthlyAnalysis> queryBatteryAlarmLevelDistribution(@Param("param")AbnormalAlarmDataMiningConditions param);
}