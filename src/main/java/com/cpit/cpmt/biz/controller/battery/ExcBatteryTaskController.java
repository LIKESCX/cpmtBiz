package com.cpit.cpmt.biz.controller.battery;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.battery.AnaBmsSingleChargeMgmt;
import com.cpit.cpmt.biz.impl.battery.ExcBatteryTaskMgmt;
import com.cpit.cpmt.biz.impl.battery.SummaryMgmt;
import com.cpit.cpmt.dto.battery.AnaBmsDayCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;

@Controller
public class ExcBatteryTaskController {
	private final static Logger logger = LoggerFactory.getLogger(ExcBatteryTaskController.class);
	@Autowired ExcBatteryTaskMgmt excBatteryTaskMgmt;
	@Autowired AnaBmsSingleChargeMgmt anaBmsSingleChargeMgmt;
	@Autowired SummaryMgmt summaryMgmt;
	
	@RequestMapping(value="/excBatteryDayTask")
	public void excBatteryDayTask() {
        logger.info("excBatteryDayTask_begin");
        Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String statisticalDate = TimeConvertor.date2String(d, TimeConvertor.FORMAT_MINUS_DAY);
        try {
        	//先去查原始数据
        	List<AnaBmsDayCharge> list = anaBmsSingleChargeMgmt.querySumAnaBmsSingleDayCharge(statisticalDate);
        	if(list!=null&&list.size()>0) {
        		//去插入天统计表
        		for (AnaBmsDayCharge anaBmsDayCharge : list) {
        			summaryMgmt.insertAnaBmsDayCharge(anaBmsDayCharge);
				}
        	}
        } catch (Exception ex) {
            logger.error("excBatteryDayTask_error", ex); 
        }
    }

}
