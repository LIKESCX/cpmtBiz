package com.cpit.cpmt.biz.impl.battery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.battery.AnaBmsSingleChargeDao;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthBasicInformation;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthHistoricalOperationAnalysis;
import com.cpit.cpmt.dto.battery.AnaBatteryMonthPerformanceHistoryAnalysis;

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
}
