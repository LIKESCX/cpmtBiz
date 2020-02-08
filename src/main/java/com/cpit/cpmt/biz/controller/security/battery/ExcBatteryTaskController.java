package com.cpit.cpmt.biz.controller.security.battery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.security.battery.AnaBmsSingleChargeMgmt;
import com.cpit.cpmt.biz.impl.security.battery.ExcBatteryTaskMgmt;
import com.cpit.cpmt.biz.impl.security.battery.SummaryMgmt;
import com.cpit.cpmt.dto.security.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsMonthCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsSeasonCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsWeekCharge;
import com.cpit.cpmt.dto.security.battery.AnaBmsYearCharge;

@RestController
public class ExcBatteryTaskController {
	private final static Logger logger = LoggerFactory.getLogger(ExcBatteryTaskController.class);
	@Autowired ExcBatteryTaskMgmt excBatteryTaskMgmt;
	@Autowired AnaBmsSingleChargeMgmt anaBmsSingleChargeMgmt;
	@Autowired SummaryMgmt summaryMgmt;
	
	//1.天
	@RequestMapping(value="/battery/excBatteryDayTask")
	public String excBatteryDayTask() {
        logger.debug("excBatteryDayTask_begin");
        Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String statisticalDate = TimeConvertor.date2String(d, TimeConvertor.FORMAT_DAY);
		logger.debug("statisticalDate="+statisticalDate);
        try {
        	//先去查原始数据
        	List<AnaBmsDayCharge> list = anaBmsSingleChargeMgmt.querySumAnaBmsSingleDayCharge(statisticalDate);
        	if(list!=null&&list.size()>0) {
        		//去插入天统计表
        		for (AnaBmsDayCharge anaBmsDayCharge : list) {
        			summaryMgmt.insertAnaBmsDayCharge(anaBmsDayCharge);
				}
        	}
        	return "OK";
        } catch (Exception ex) {
            logger.error("excBatteryDayTask_error", ex); 
        	return "FAIL";
        }
    }
	
	//2.周 //每周一的凌晨去统计上一周的数据
	@RequestMapping(value="/battery/excBatteryWeekTask")
	public String excBatteryWeekTask() {
		logger.debug("excBatteryWeekTask_begin");
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String statisticalWeek = TimeConvertor.date2String(d, TimeConvertor.FORMAT_DAY);
		statisticalWeek = "20200209";//test
		logger.debug("statisticalWeek="+statisticalWeek);
		try {
			//先去查原始数据
			List<AnaBmsWeekCharge> list = anaBmsSingleChargeMgmt.querySumAnaBmsSingleWeekCharge(statisticalWeek);
			if(list!=null&&list.size()>0) {
				//去插入周统计表
				for (AnaBmsWeekCharge anaBmsWeekCharge : list) {
					summaryMgmt.insertAnaBmsWeekCharge(anaBmsWeekCharge);
				}
			}
			return "OK";
		} catch (Exception ex) {
			logger.error("excBatteryWeekTask_error", ex); 
			return "FAIL";
		}
	}
	//3.月
	@RequestMapping(value="/battery/excBatteryMonthTask")
	public String excBatteryMonthTask() {
		logger.debug("excBatteryMonthTask_begin");
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String statisticalMonth = TimeConvertor.date2String(d, "yyyyMM");
		statisticalMonth= "202002";//test
		logger.debug("statisticalMonth="+statisticalMonth);
		try {
			//先去查原始数据
			List<AnaBmsMonthCharge> list = anaBmsSingleChargeMgmt.querySumAnaBmsSingleMonthCharge(statisticalMonth);
			if(list!=null&&list.size()>0) {
				//去插入月统计表
				for (AnaBmsMonthCharge anaBmsMonthCharge : list) {
					summaryMgmt.insertAnaBmsMonthCharge(anaBmsMonthCharge);
				}
			}
			return "OK";
		} catch (Exception ex) {
			logger.error("excBatteryMonthTask_error", ex); 
			return "FAIL";
		}
	}
	//4.季度
	@RequestMapping(value="/battery/excBatterySeasonTask")
	public String excBatterySeasonTask() {
		logger.debug("excBatterySeasonTask_begin");
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String statisticalSeason = TimeConvertor.date2String(d, TimeConvertor.FORMAT_DAY);
		statisticalSeason = "202001";//test
		logger.debug("statisticalSeason="+statisticalSeason);
		try {
			//先去查原始数据
			List<AnaBmsSeasonCharge> list = anaBmsSingleChargeMgmt.querySumAnaBmsSingleSeasonCharge(statisticalSeason);
			if(list!=null&&list.size()>0) {
				//去插入季统计表
				for (AnaBmsSeasonCharge anaBmsSeasonCharge : list) {
					summaryMgmt.insertAnaBmsSeasonCharge(anaBmsSeasonCharge);
				}
			}
			return "OK";
		} catch (Exception ex) {
			logger.error("excBatterySeasonTask_error", ex); 
			return "FAIL";
		}
	}
	//5.年
	@RequestMapping(value="/battery/excBatteryYearTask")
	public String excBatteryYearTask() {
		logger.debug("excBatteryYearTask_begin");
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String statisticalYear = TimeConvertor.date2String(d, "yyyy");
		statisticalYear= "202002";//test
		logger.debug("statisticalYear="+statisticalYear);
		try {
			//先去查原始数据
			List<AnaBmsYearCharge> list = anaBmsSingleChargeMgmt.querySumAnaBmsSingleYearCharge(statisticalYear);
			if(list!=null&&list.size()>0) {
				//去插入年统计表
				for (AnaBmsYearCharge anaBmsYearCharge : list) {
					summaryMgmt.insertAnaBmsYearCharge(anaBmsYearCharge);
				}
			}
			return "OK";
		} catch (Exception ex) {
			logger.error("excBatteryYearTask_error", ex); 
			return "FAIL";
		}
	}

}
