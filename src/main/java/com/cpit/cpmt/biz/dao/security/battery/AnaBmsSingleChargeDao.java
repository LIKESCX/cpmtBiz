package com.cpit.cpmt.biz.dao.security.battery;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthBasicInformation;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthHistoricalOperationAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthPerformanceHistoryAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBatteryOperationMonthlyAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsMonthCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsSeasonCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsWeekCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsYearCharge;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;

@MyBatisDao
public interface AnaBmsSingleChargeDao {
	public int insertSelective(AnaBmsSingleCharge record);
	
	//按天统计汇总结果
	public List<AnaBmsDayCharge> selectSumAnaBmsSingleDayCharge(String statisticalDate);
	//按周统计汇总结果
	public List<AnaBmsWeekCharge> selectSumAnaBmsSingleWeekCharge(String statisticalWeek);
	//按月统计汇总结果
	public List<AnaBmsMonthCharge> selectSumAnaBmsSingleMonthCharge(String statisticalMonth);
	//按季统计汇总结果
	public List<AnaBmsSeasonCharge> selectSumAnaBmsSingleSeasonCharge(String statisticalSeason);
	//按年统计汇总结果
	public List<AnaBmsYearCharge> selectSumAnaBmsSingleYearCharge(String statisticalYear);

	//第一级钻取按小时粒度查询充电统计次数
	public List<AnaBmsSingleCharge> queryFirstLevelDataGranularityByHour(@Param("param")BatteryDataTrackingAssessmentConditions param);
	//第二级钻取数据
	public Page<AnaBmsSingleCharge> querySecondLevelData(@Param("param")BatteryDataTrackingAssessmentConditions param);
	//第三级钻取数据
	public List<AnaBmsSingleCharge> queryThirdLevelData(@Param("param")BatteryDataTrackingAssessmentConditions param);
	//获取月/季/年报告基本信息
	public AnaBatteryMonthBasicInformation queryMonthBasicInformation(@Param("param")BatteryDataTrackingAssessmentConditions param);
	
	//单独获取月报告电池性能历史分析
	public List<AnaBatteryMonthPerformanceHistoryAnalysis> queryMonthPerformanceHistoryAnalysis(@Param("param")BatteryDataTrackingAssessmentConditions param);
	//单独获取季报告电池性能历史分析
	public List<AnaBatteryMonthPerformanceHistoryAnalysis> querySeasonPerformanceHistoryAnalysis(@Param("param")BatteryDataTrackingAssessmentConditions param);
	//单独获取年报告电池性能历史分析
	public List<AnaBatteryMonthPerformanceHistoryAnalysis> queryYearPerformanceHistoryAnalysis(@Param("param")BatteryDataTrackingAssessmentConditions param);
	
	//获取月/季/年报告电池运行情况历史分析
	public List<AnaBatteryMonthHistoricalOperationAnalysis> queryMonthHistoricalOperationAnalysis(@Param("param")BatteryDataTrackingAssessmentConditions param);
	//获取月/季/年报告电池运行情况月度分析
	public AnaBatteryOperationMonthlyAnalysis queryBatteryOperationMonthlyAnalysis(@Param("param")BatteryDataTrackingAssessmentConditions param);

	public AnaBmsSingleCharge queryStatisticalTimes(@Param("param") BatteryDataTrackingAssessmentConditions param);
	
}
