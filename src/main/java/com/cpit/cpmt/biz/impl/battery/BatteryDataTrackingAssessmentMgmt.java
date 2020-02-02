package com.cpit.cpmt.biz.impl.battery;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.dto.battery.BatteryDataTrackingAssessmentConditions;

@Service
public class BatteryDataTrackingAssessmentMgmt {
	private final static Logger logger = LoggerFactory.getLogger(BatteryDataTrackingAssessmentMgmt.class);
	
	//@Autowired
	//private OperatorInfoMgmt operatorInfoMgmt;
	public void queryBatteryChargingTimesByConditions(BatteryDataTrackingAssessmentConditions bdtac) {
		if(bdtac.getAllOperators()==1) {//表明全选
			logger.info("运营商全选标识符为[{}]表明全选", bdtac.getAllOperators());
			Date startTime = bdtac.getStartTime();
			Date endTime = bdtac.getEndTime();
			if(bdtac.getTimeGranularity()==1) {//表示按小时统计
				
			}else if(bdtac.getTimeGranularity()==2) {//表示按天统计
				
			}else if(bdtac.getTimeGranularity()==3) {//表示按周统计
				
			}else if(bdtac.getTimeGranularity()==4) {//表示按月统计
				
			}else if(bdtac.getTimeGranularity()==4) {//表示按季统计
				
			}
			//直接去电池分析结果表中去查.
			
		}
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
