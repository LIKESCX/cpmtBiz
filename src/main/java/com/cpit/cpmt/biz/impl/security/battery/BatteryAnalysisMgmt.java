package com.cpit.cpmt.biz.impl.security.battery;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.security.battery.AnaBmsSingleChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.AnaBmsSingleChargeWarningResultDao;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthBasicInformation;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthHistoricalOperationAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBatteryMonthPerformanceHistoryAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBatteryOperationMonthlyAnalysis;
import com.cpit.cpmt.dto.security.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.security.battery.BatteryDataTrackingAssessmentConditions;
import com.cpit.cpmt.dto.security.battery.BatteryWarningAbnormalMonthlyAnalysis;

@Service
public class BatteryAnalysisMgmt {
	@Autowired AnaBmsSingleChargeDao anaBmsSingleChargeDao;
	/*@Autowired AnaBmsMonthChargeDao anaBmsMonthChargeDao;*/
	@Autowired AnaBmsSingleChargeWarningResultDao anaBmsSingleChargeWarningResultDao;
	//获取 一、基本信息--月/季/年
	public AnaBatteryMonthBasicInformation queryMonthBasicInformation(BatteryDataTrackingAssessmentConditions param) {
		AnaBatteryMonthBasicInformation monthBasicInformation = anaBmsSingleChargeDao.queryMonthBasicInformation(param);
		//需要去mysql库中查找monthBasicInformation中的以下信息
			//车辆用途
			//额定容量
			//额定电压
		//monthBasicInformation中的综合评估的获取是根据
			//综合评估是根据车辆在统计时间范围的所有表现(健康度、内阻、剩余容量的数值、变化趋势)进行综合评价；
			//易正需要单独提供一个接口,进行计算当月的综合评价.暂时还没给提供算法新版本.
		//都获取完后,可以入库,便于以后查询,也可以直接返回的前台页面
		return monthBasicInformation;
	}
	//获取 二、电池性能历史分析--月/季/年
	public List<AnaBatteryMonthPerformanceHistoryAnalysis> queryMonthPerformanceHistoryAnalysis(BatteryDataTrackingAssessmentConditions param) {
		if(param.getTimeGranularity()==4) {//粒度为月
			return anaBmsSingleChargeDao.queryMonthPerformanceHistoryAnalysis(param);
		}else if(param.getTimeGranularity()==5) {//粒度为季
			return anaBmsSingleChargeDao.querySeasonPerformanceHistoryAnalysis(param);
		}else if(param.getTimeGranularity()==6) {//粒度为年
			return anaBmsSingleChargeDao.queryYearPerformanceHistoryAnalysis(param);
		}else {
			return null;
		}
	}
	//获取 三、电池运行情况历史分析--月/季/年
	public List<AnaBatteryMonthHistoricalOperationAnalysis> queryMonthHistoricalOperationAnalysis(BatteryDataTrackingAssessmentConditions param) {
		return anaBmsSingleChargeDao.queryMonthHistoricalOperationAnalysis(param);
	}
	
	//获取 四、电池告警异常分析--月/季/年
	public Map<String,Object> queryBatteryWarningAbnormalMonthlyAnalysis(BatteryDataTrackingAssessmentConditions param) {
		if(param.getTimeGranularity()==4) {//粒度为月
			BatteryWarningAbnormalMonthlyAnalysis monthlyWarningTimes = anaBmsSingleChargeWarningResultDao.queryMonthlyWarningTimes(param);
			AnaBmsSingleCharge anaBmsSingleCharge = anaBmsSingleChargeDao.queryStatisticalTimes(param);//查询充电次数
			Integer warningTimes = monthlyWarningTimes.getWarningTimes();
			Integer statisticsTimes = Integer.parseInt(anaBmsSingleCharge.getStatisticalTimes());
			Double warningRate = new BigDecimal((float)warningTimes/statisticsTimes).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			monthlyWarningTimes.setWarningRate(warningRate);//存入告警率
			List<BatteryWarningAbnormalMonthlyAnalysis> monthlyEachWarningCodeTimes = anaBmsSingleChargeWarningResultDao.queryMonthlyEachWarningCodeTimes(param);
			List<BatteryWarningAbnormalMonthlyAnalysis> monthlyEachWarningLevelTimes = anaBmsSingleChargeWarningResultDao.queryMonthlyEachWarningLevelTimes(param);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("monthlyWarningTimes", monthlyWarningTimes);
			map.put("monthlyEachWarningCodeTimes", monthlyEachWarningCodeTimes);
			map.put("monthlyEachWarningLevelTimes", monthlyEachWarningLevelTimes);
			return map;
		}else if(param.getTimeGranularity()==5) {//粒度为季
			BatteryWarningAbnormalMonthlyAnalysis seasonlyWarningTimes = anaBmsSingleChargeWarningResultDao.querySeasonlyWarningTimes(param);
			AnaBmsSingleCharge anaBmsSingleCharge = anaBmsSingleChargeDao.queryStatisticalTimes(param);//查询充电次数
			Integer warningTimes = seasonlyWarningTimes.getWarningTimes();
			Integer statisticsTimes = Integer.parseInt(anaBmsSingleCharge.getStatisticalTimes());
			Double warningRate = new BigDecimal((float)warningTimes/statisticsTimes).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			seasonlyWarningTimes.setWarningRate(warningRate);//存入告警率
			List<BatteryWarningAbnormalMonthlyAnalysis> seasonlyEachWarningCodeTimes = anaBmsSingleChargeWarningResultDao.querySeasonlyEachWarningCodeTimes(param);
			List<BatteryWarningAbnormalMonthlyAnalysis> seasonlyEachWarningLevelTimes = anaBmsSingleChargeWarningResultDao.querySeasonlyEachWarningLevelTimes(param);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("seasonlyWarningTimes", seasonlyWarningTimes);
			map.put("seasonlyEachWarningCodeTimes", seasonlyEachWarningCodeTimes);
			map.put("seasonlyEachWarningLevelTimes", seasonlyEachWarningLevelTimes);
			return map;
		}else if(param.getTimeGranularity()==6) {//粒度为年
			BatteryWarningAbnormalMonthlyAnalysis yearlyWarningTimes = anaBmsSingleChargeWarningResultDao.queryYearlyWarningTimes(param);
			AnaBmsSingleCharge anaBmsSingleCharge = anaBmsSingleChargeDao.queryStatisticalTimes(param);//查询充电次数
			Integer warningTimes = yearlyWarningTimes.getWarningTimes();
			Integer statisticsTimes = Integer.parseInt(anaBmsSingleCharge.getStatisticalTimes());
			Double warningRate = new BigDecimal((float)warningTimes/statisticsTimes).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			yearlyWarningTimes.setWarningRate(warningRate);//存入告警率
			List<BatteryWarningAbnormalMonthlyAnalysis> yearlyEachWarningCodeTimes = anaBmsSingleChargeWarningResultDao.queryYearlyEachWarningCodeTimes(param);
			List<BatteryWarningAbnormalMonthlyAnalysis> yearlyEachWarningLevelTimes = anaBmsSingleChargeWarningResultDao.queryYearlyEachWarningLevelTimes(param);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("yearlyWarningTimes", yearlyWarningTimes);
			map.put("yearlyEachWarningCodeTimes", yearlyEachWarningCodeTimes);
			map.put("yearlyEachWarningLevelTimes", yearlyEachWarningLevelTimes);
			return map;
		}else {
			return null;
		}
		
	}
	
	//获取 五、电池运行情况分析--月/季/年
	public AnaBatteryOperationMonthlyAnalysis queryBatteryOperationMonthlyAnalysis(BatteryDataTrackingAssessmentConditions param) {
		 AnaBatteryOperationMonthlyAnalysis information = anaBmsSingleChargeDao.queryBatteryOperationMonthlyAnalysis(param);
		 if(information!=null) {
			 int num = 1;
			 if(param.getTimeGranularity()==4) {//粒度为月
				 Integer year = Integer.parseInt(param.getStatisticalMonth().substring(0, 4));
				 Integer month = Integer.parseInt(param.getStatisticalMonth().substring(4));
				 System.out.println("year="+year+",month="+month);
				 num = getDaysByYearAndMonth(year, month);
			 }else if (param.getTimeGranularity()==5) {//粒度为季
				 num = 4;
			 }else if (param.getTimeGranularity()==6) {//粒度为年
				 num = 12;
			 }
			 double dailyAvgChargeTimes = new BigDecimal((float)information.getHistoryChargeTimes()/num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			 information.setDailyAvgChargeTimes(dailyAvgChargeTimes);//平均每日充电次数(这里的dailyAvgChargeTimes字段当统计粒度为季和年的时候可以共用)
			 
			 //dailyAvgChargeTime 平均每日/月充电时长长度
			 double dailyAvgChargeTime = new BigDecimal((float)information.getHistoryChargeTime()/num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			 information.setDailyAvgChargeTime(dailyAvgChargeTime);
			 
			 //avgTimeChargeTime 平均每次充电时长长度，单位秒
			 double avgTimeChargeTime = new BigDecimal((float)information.getHistoryChargeTime()/information.getHistoryChargeTimes()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			 information.setAvgTimeChargeTime(avgTimeChargeTime);
			 
		 }
		 return information;
	}
	
	//获取当前月的天数
	public int getDaysByYearAndMonth(int year, int month)
	   {
		   int result;
		   if (2==month)
		   {
			   if(year%4==0&&year%100!=0||year%400==0){
					result=29;
			   }else{
					result=28;
			   }
		   }
		   else if (month==4||month==6||month==9||month==11)
		   {
			   result=30;
		   }
		   else{
			   result=31;
		   }
		   return result;
	   }	
}
