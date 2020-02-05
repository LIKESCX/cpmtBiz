package com.cpit.cpmt.biz.dao.battery;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.battery.AnaBmsMonthCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSeasonCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.battery.AnaBmsWeekCharge;
import com.cpit.cpmt.dto.battery.AnaBmsYearCharge;
import com.cpit.cpmt.dto.battery.BatteryDataTrackingAssessmentConditions;

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
	public List<AnaBmsSingleCharge> querySecondLevelData(@Param("param")BatteryDataTrackingAssessmentConditions param);
	//第三级钻取数据
	public List<AnaBmsSingleCharge> queryThirdLevelData(@Param("param")BatteryDataTrackingAssessmentConditions param);
	
	
}
