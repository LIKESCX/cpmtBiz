package com.cpit.cpmt.biz.impl.battery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.battery.AnaBmsDayChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsMonthChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsSeasonChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsSingleChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsWeekChargeDao;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.battery.AnaBmsMonthCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSeasonCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.battery.AnaBmsWeekCharge;
import com.cpit.cpmt.dto.battery.BatteryDataTrackingAssessmentConditions;

@Service
public class BatteryDataTrackingAssessmentMgmt {
	private final static Logger logger = LoggerFactory.getLogger(BatteryDataTrackingAssessmentMgmt.class);
	
	@Autowired private AnaBmsSingleChargeDao anaBmsSingleChargeDao;
	@Autowired private AnaBmsDayChargeDao anaBmsDayChargeDao;
	@Autowired private AnaBmsWeekChargeDao anaBmsWeekChargeDao;
	@Autowired private AnaBmsMonthChargeDao anaBmsMonthChargeDao;
	@Autowired private AnaBmsSeasonChargeDao anaBmsSeasonChargeDao;
	public Object queryFirstLevelData(BatteryDataTrackingAssessmentConditions param) {
		if(param.getAllOperators()==1) {//表明全选
			logger.info("运营商全选标识符为[{}]表明全选", param.getAllOperators());
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getTimeGranularity()==1) {//表示按小时统计
				List<AnaBmsSingleCharge> list = anaBmsSingleChargeDao.queryFirstLevelDataGranularityByHour(param);
				logger.info("queryFirstLevelDataGranularityByHour:"+list);
			}else if(param.getTimeGranularity()==2) {//表示按天统计
				String startStatisticalDay = TimeConvertor.date2String(startTime, "yyyyMMdd");
				String endStatisticalDay = TimeConvertor.date2String(endTime, "yyyyMMdd");
				param.setStartStatisticalDay(startStatisticalDay);
				param.setEndStatisticalDay(endStatisticalDay);
				List<AnaBmsDayCharge> list = anaBmsDayChargeDao.queryFirstLevelDataGranularityByDay(param);
				logger.info("queryFirstLevelDataGranularityByDay:"+list);
				return list;
			}else if(param.getTimeGranularity()==3) {//表示按周统计
				String startStatisticalWeek = TimeConvertor.date2String(startTime, "yyyyMMdd");
				startStatisticalWeek = getMonday(startStatisticalWeek);// 返回开始时间所在星期的周日
				String endStatisticalWeek = TimeConvertor.date2String(endTime, "yyyyMMdd");
				endStatisticalWeek = getMonday(endStatisticalWeek);// 返回结束时间所在星期的周日
				param.setStartStatisticalWeek(startStatisticalWeek);
				param.setEndStatisticalWeek(endStatisticalWeek);
				List<AnaBmsWeekCharge> list = anaBmsWeekChargeDao.queryFirstLevelDataGranularityByWeek(param);
				logger.info("queryFirstLevelDataGranularityByWeek:"+list);
				return list;
			}else if(param.getTimeGranularity()==4) {//表示按月统计
				String startStatisticalMonth = TimeConvertor.date2String(startTime, "yyyyMM");
				String endStatisticalMonth = TimeConvertor.date2String(endTime, "yyyyMM");
				param.setStartStatisticalMonth(startStatisticalMonth);
				param.setEndStatisticalMonth(endStatisticalMonth);
				List<AnaBmsMonthCharge> list = anaBmsMonthChargeDao.queryFirstLevelDataGranularityByMonth(param);
				logger.info("queryFirstLevelDataGranularityByMonth:"+list);
				return list;
			}else if(param.getTimeGranularity()==5) {//表示按季统计
				String startStatisticalSeason = getSeasonTime(startTime);
				String endStatisticalSeason = getSeasonTime(endTime);
				param.setStartStatisticalSeason(startStatisticalSeason);
				param.setEndStatisticalSeason(endStatisticalSeason);
				List<AnaBmsSeasonCharge> list = anaBmsSeasonChargeDao.queryFirstLevelDataGranularityBySeason(param);
				logger.info("queryFirstLevelDataGranularityBySeason:"+list);
				return list;
			}
			return null;
			
		}
		return null;
	}
	
	public Object querySecondLevelData(BatteryDataTrackingAssessmentConditions param) {
		List<AnaBmsSingleCharge> list = anaBmsSingleChargeDao.querySecondLevelData(param);
		return list;
	}
	
	public Object queryThirdLevelData(BatteryDataTrackingAssessmentConditions param) {
		List<AnaBmsSingleCharge> list = anaBmsSingleChargeDao.queryThirdLevelData(param);
		return list;
	}
	
	
	private String getMonday(String date) {
		if (date == null || date.equals("")) {
			System.out.println("date is null or empty");
			return "00000000";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		// set the first day of the week is Monday
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 设置要返回的日期为传入时间对于的周日
		return format.format(cal.getTime());
	}

	private  String getSeasonTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(cal.MONTH) + 1;
		int quarter = 0;
		// 判断季度
		if (month >= 1 && month <= 3) {
			quarter = 1;
		} else if (month >= 4 && month <= 6) {
			quarter = 2;
		} else if (month >= 7 && month <= 9) {
			quarter = 3;
		} else {
			quarter = 4;
		}
		return TimeConvertor.date2String(date, "yyyy") + "0" + quarter;
	}

	

//	private Date tools(Date date) {
//		Calendar cal1 = Calendar.getInstance();
//		cal1.setTime(date);
//		// 将时分秒,毫秒域清零
//		//cal1.set(Calendar.HOUR_OF_DAY, 0);
//		cal1.set(Calendar.MINUTE, 0);
//		cal1.set(Calendar.SECOND, 0);
//		cal1.set(Calendar.MILLISECOND, 0);
//		return cal1.getTime();
//	}
//	
//	private Date addOneHour(Date date) {
//		Calendar calendar = new GregorianCalendar();
//				calendar.setTime(date); //你自己的数据进行类型转换
//				calendar.add(calendar.HOUR_OF_DAY,1);//把时往后增加一小时.整数往后推,负数往前移动
//				return calendar.getTime();
//	}			
}
