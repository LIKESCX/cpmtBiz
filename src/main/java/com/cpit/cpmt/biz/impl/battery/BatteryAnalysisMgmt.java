package com.cpit.cpmt.biz.impl.battery;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.battery.AnaBmsSingleChargeDao;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthBasicInformation;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthHistoricalOperationAnalysis;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthPerformanceHistoryAnalysis;
import com.cpit.cpmt.dto.battery.AnaBatteryOperationMonthlyAnalysis;

@Service
public class BatteryAnalysisMgmt {
	@Autowired AnaBmsSingleChargeDao anaBmsSingleChargeDao;
	public AnaBatteryMonthBasicInformation queryMonthBasicInformation(String bmsCode, String statisticalMonth) {
		AnaBatteryMonthBasicInformation monthBasicInformation = anaBmsSingleChargeDao.queryMonthBasicInformation(bmsCode, statisticalMonth);
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
	//获取 二、电池性能历史分析--月
	public List<AnaBatteryMonthPerformanceHistoryAnalysis> queryMonthPerformanceHistoryAnalysis(String bmsCode, String statisticalMonth) {
		return anaBmsSingleChargeDao.queryMonthPerformanceHistoryAnalysis(bmsCode, statisticalMonth);
	}
	//获取 三、电池运行情况历史分析--月
	public List<AnaBatteryMonthHistoricalOperationAnalysis> queryMonthHistoricalOperationAnalysis(String bmsCode,String statisticalMonth) {
		return anaBmsSingleChargeDao.queryMonthHistoricalOperationAnalysis(bmsCode, statisticalMonth);
	}
	
	
	//获取 五、电池运行情况月度分析
	public AnaBatteryOperationMonthlyAnalysis queryBatteryOperationMonthlyAnalysis(String bmsCode,
			String statisticalMonth) {
		 AnaBatteryOperationMonthlyAnalysis information = anaBmsSingleChargeDao.queryBatteryOperationMonthlyAnalysis(bmsCode, statisticalMonth);
		 if(information!=null) {
			 Integer year = Integer.parseInt(statisticalMonth.substring(0, 4));
			 Integer month = Integer.parseInt(statisticalMonth.substring(4));
			 System.out.println("year="+year+",month="+month);
			 int days = getDaysByYearAndMonth(year, month);
			 double dailyAvgChargeTimes = new BigDecimal((float)information.getHistoryChargeTimes()/days).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			 information.setDailyAvgChargeTimes(dailyAvgChargeTimes);//平均每日充电次数
			 
			 //dailyAvgChargeTime 平均每日充电时长长度
			 double dailyAvgChargeTime = new BigDecimal((float)information.getHistoryChargeTime()/days).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
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
