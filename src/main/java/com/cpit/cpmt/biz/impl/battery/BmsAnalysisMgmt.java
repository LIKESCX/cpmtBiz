package com.cpit.cpmt.biz.impl.battery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.bbap.model.BmsAnalysisResult;
import com.bbap.model.BmsInfo;
import com.bbap.model.TotalResponse;
import com.bbap.model.WarningResult;
import com.bbap.rest.CountRest;
import com.bbap.util.CountUtil;
import com.bbap.util.PmmlUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.battery.AnaBmsSingleChargeDao;
import com.cpit.cpmt.biz.dao.battery.AnaBmsSingleChargeWarningResultDao;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.battery.AnaBmsSingleChargeWarningResult;

@Service
@Import({ CountRest.class, CountUtil.class, PmmlUtil.class })
public class BmsAnalysisMgmt {
	private final static Logger logger = LoggerFactory.getLogger(BmsAnalysisMgmt.class);
	@Autowired
	CountRest countRest;
	@Autowired
	AnaBmsSingleChargeDao anaBmsSingleChargeDao;
	@Autowired
	AnaBmsSingleChargeWarningResultDao anaBmsSingleChargeWarningResultDao;

	public void obtainAnalysisAll(List<BmsInfo> list, Date recTime) {

		TotalResponse tr;
		try {
			tr = countRest.analysisAll(list);
			
			BmsAnalysisResult bmsAnalysisResult = tr.getBmsAnalysisResult();// 获取分析结果

			// 调接口
			// 根据bmsCode和结束充电时间 获取本次充电时的运营商id,站信息id,设备id,设备接口id信息详情
			// AnaBmsSingleCharge anaBmsSingleCharge =
			// 服务名.queryBmsInfo(bmsAnalysisResult.getbMSCode(),bmsAnalysisResult.getEndTime());
			
			AnaBmsSingleCharge anaBmsSingleCharge = new AnaBmsSingleCharge();
			// test begin
			anaBmsSingleCharge.setOperatorId("10086");
			anaBmsSingleCharge.setStationId("1008601");
			anaBmsSingleCharge.setEquipmentId("10086001");
			anaBmsSingleCharge.setConnectorId("100860001");
			// test end
			BeanUtils.copyProperties(bmsAnalysisResult, anaBmsSingleCharge);

			Date endTime = bmsAnalysisResult.getEndTime();
			// 天
			String statisticalDate = TimeConvertor.date2String(endTime, "yyyyMMdd");
			// 对应的周日
			String statisticalWeek = TimeConvertor.date2String(endTime, "yyyyMMdd");
			statisticalWeek = getMonday(statisticalWeek);// 返回所在星期的周日
			// 月
			String statisticalMonth = TimeConvertor.date2String(endTime, "yyyyMM");
			// 季
			String statisticalSeason = getSeasonTime(endTime);

			anaBmsSingleCharge.setStatisticalDate(statisticalDate);
			anaBmsSingleCharge.setStatisticalWeek(statisticalWeek);
			anaBmsSingleCharge.setStatisticalMonth(statisticalMonth);
			anaBmsSingleCharge.setStatisticalSeason(statisticalSeason);
			// 收到的时间此字段待定
			// 入库的时间此字段待定
			// 计算后的正常分析结果原始数据入库
			logger.info("anaBmsSingleCharge"+anaBmsSingleCharge);
			anaBmsSingleChargeDao.insertSelective(anaBmsSingleCharge);
			logger.info("anaBmsSingleChargeDao.insertSelective is success");

			// 计算后的正常分析结果原始数据入库
			List<WarningResult> warningResultList = tr.getWarningResults();// 获取告警结果
			for (WarningResult warningResult : warningResultList) {
				AnaBmsSingleChargeWarningResult ascwr = new AnaBmsSingleChargeWarningResult();
				BeanUtils.copyProperties(warningResult, ascwr);
				ascwr.setOperatorId(anaBmsSingleCharge.getOperatorId());
				ascwr.setStationId(anaBmsSingleCharge.getStationId());
				ascwr.setEquipmentId(anaBmsSingleCharge.getEquipmentId());
				ascwr.setConnectorId(anaBmsSingleCharge.getConnectorId());
				
				ascwr.setStartTime(anaBmsSingleCharge.getStartTime());//暂时返回值无
				ascwr.setEndTime(anaBmsSingleCharge.getEndTime());//暂时返回值无
				
				ascwr.setStatisticalDate(statisticalDate);
				ascwr.setStatisticalWeek(statisticalWeek);
				ascwr.setStatisticalMonth(statisticalMonth);
				ascwr.setStatisticalSeason(statisticalSeason);
				anaBmsSingleChargeWarningResultDao.insertSelective(ascwr);
			}
			logger.info("anaBmsSingleChargeWarningResultDao.insertSelective is success");
		} catch (Exception e) {
			logger.error("obtainAnalysisAll is exception:"+e);
		}
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

}
