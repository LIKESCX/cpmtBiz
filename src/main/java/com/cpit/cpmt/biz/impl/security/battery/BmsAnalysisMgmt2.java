/*package com.cpit.cpmt.biz.impl.battery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.cpit.cpmt.dto.battery.AnaBmsManyChargesDto;
import com.cpit.cpmt.dto.battery.AnaBmsSingleCharge;

@Service
@Import({CountRest.class,CountUtil.class,PmmlUtil.class,WarningCount.class,BbapBatterySoh.class})
public class BmsAnalysisMgmt2 {
	@Autowired
	CountRest countRest;
	@Autowired
	AnaBmsSingleChargeMgmt anaBmsSingleChargeMgmt;
	
	public void obtainAnalysisAll(List<BmsInfo> list) {

		TotalResponse tr;
		try {
			tr = countRest.analysisAll(list);
			BmsAnalysisResult bmsAnalysisResult = tr.getBmsAnalysisResult();//获取分析结果
			//调接口
			//根据bmsCode和结束充电时间 获取本次充电时的运营商id,站信息id,设备id,设备接口id信息详情
			//AnaBmsSingleCharge anaBmsSingleCharge = 服务名.queryBmsInfo(bmsAnalysisResult.getbMSCode(),bmsAnalysisResult.getEndTime());
			
			AnaBmsSingleCharge anaBmsSingleCharge = new AnaBmsSingleCharge();
			//test begin
			anaBmsSingleCharge.setOperatorId("10086");
			anaBmsSingleCharge.setStationId("1008601");
			anaBmsSingleCharge.setEquipmentId("10086001");
			anaBmsSingleCharge.setConnectorId("100860001");
			//test end
			BeanUtils.copyProperties(bmsAnalysisResult, anaBmsSingleCharge);
			//计算后的原始数据入库
			anaBmsSingleChargeMgmt.insertAnaBmsSingleCharge(anaBmsSingleCharge);
			
			String newHourTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndTime(),"yyyyMMddHH");
			//test begin
			String bMSCode = anaBmsSingleCharge.getBmsCode();
			String operatorId = anaBmsSingleCharge.getOperatorId();
			//String stationId = anaBmsSingleCharge.getStationId();
			//String equipmentId = anaBmsSingleCharge.getEquipmentId();
			String connectorId = anaBmsSingleCharge.getConnectorId();
			//test end
			//更新或插入小时表
			AnaBmsManyChargesDto anaBmsManyChargesDto = anaBmsSingleChargeMgmt.queryAnaBmsSingleChargeHour(newHourTime,bMSCode,connectorId,operatorId);
			if(anaBmsManyChargesDto==null) {
				AnaBmsManyChargesDto anaBmsManyChargesparam = new AnaBmsManyChargesDto();
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesparam);
				anaBmsManyChargesparam.setFlagTime(newHourTime);
				anaBmsManyChargesparam.setChargeTimes(1);
				anaBmsSingleChargeMgmt.insertAnaBmsSingleChargeHour(anaBmsManyChargesparam);
			}else {
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesDto);
				anaBmsManyChargesDto.setChargeTimes(anaBmsManyChargesDto.getChargeTimes()+1);//充电次数加一
				anaBmsSingleChargeMgmt.updateAnaBmsSingleChargeHour(anaBmsManyChargesDto);
			}
			//更新或插入天统计表
			String dayTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndTime(),"yyyyMMdd");
			anaBmsManyChargesDto = anaBmsSingleChargeMgmt.queryAnaBmsSingleChargeDay(dayTime,bMSCode,connectorId,operatorId);
			if(anaBmsManyChargesDto==null) {
				AnaBmsManyChargesDto anaBmsManyChargesparam = new AnaBmsManyChargesDto();
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesparam);
				anaBmsManyChargesparam.setFlagTime(dayTime);
				anaBmsManyChargesparam.setChargeTimes(1);
				anaBmsSingleChargeMgmt.insertAnaBmsSingleChargeDay(anaBmsManyChargesparam);
			}else {
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesDto);
				anaBmsManyChargesDto.setChargeTimes(anaBmsManyChargesDto.getChargeTimes()+1);//充电次数加一
				anaBmsSingleChargeMgmt.updateAnaBmsSingleChargeDay(anaBmsManyChargesDto);
			}
			//更新或插入周统计表
			String weekTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndTime(),"yyyyMMdd");
			String sundayTime = getMonday(weekTime);//返回所在星期的周日
			anaBmsManyChargesDto = anaBmsSingleChargeMgmt.queryAnaBmsSingleChargeWeek(sundayTime,bMSCode,connectorId,operatorId);
			if(anaBmsManyChargesDto==null) {
				AnaBmsManyChargesDto anaBmsManyChargesparam = new AnaBmsManyChargesDto();
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesparam);
				anaBmsManyChargesparam.setFlagTime(sundayTime);
				anaBmsManyChargesparam.setChargeTimes(1);
				anaBmsSingleChargeMgmt.insertAnaBmsSingleChargeWeek(anaBmsManyChargesparam);
			}else {
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesDto);
				anaBmsManyChargesDto.setChargeTimes(anaBmsManyChargesDto.getChargeTimes()+1);//充电次数加一
				anaBmsSingleChargeMgmt.updateAnaBmsSingleChargeWeek(anaBmsManyChargesDto);
			}
			
			//更新或插入月统计表
			String monthTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndTime(),"yyyyMM");
			anaBmsManyChargesDto = anaBmsSingleChargeMgmt.queryAnaBmsSingleChargeMonth(monthTime,bMSCode,connectorId,operatorId);
			if(anaBmsManyChargesDto==null) {
				AnaBmsManyChargesDto anaBmsManyChargesparam = new AnaBmsManyChargesDto();
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesparam);
				anaBmsManyChargesparam.setFlagTime(monthTime);
				anaBmsManyChargesparam.setChargeTimes(1);
				anaBmsSingleChargeMgmt.insertAnaBmsSingleChargeMonth(anaBmsManyChargesparam);
			}else {
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesDto);
				anaBmsManyChargesDto.setChargeTimes(anaBmsManyChargesDto.getChargeTimes()+1);//充电次数加一
				anaBmsSingleChargeMgmt.updateAnaBmsSingleChargeMonth(anaBmsManyChargesDto);
			}
			
			//更新或插入季统计表
			String seasonTime = getSeasonTime(anaBmsSingleCharge.getEndTime());
			anaBmsManyChargesDto = anaBmsSingleChargeMgmt.queryAnaBmsSingleChargeSeason(seasonTime,bMSCode,connectorId,operatorId);
			if(anaBmsManyChargesDto==null) {
				AnaBmsManyChargesDto anaBmsManyChargesparam = new AnaBmsManyChargesDto();
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesparam);
				anaBmsManyChargesparam.setFlagTime(seasonTime);
				anaBmsManyChargesparam.setChargeTimes(1);
				anaBmsSingleChargeMgmt.insertAnaBmsSingleChargeSeason(anaBmsManyChargesparam);
			}else {
				BeanUtils.copyProperties(anaBmsSingleCharge,anaBmsManyChargesDto);
				anaBmsManyChargesDto.setChargeTimes(anaBmsManyChargesDto.getChargeTimes()+1);//充电次数加一
				anaBmsSingleChargeMgmt.updateAnaBmsSingleChargeSeason(anaBmsManyChargesDto);
			}
			
			
			List<WarningResult> warningResultList = tr.getWarningResults();//获取告警结果
				
			} catch (Exception e) {
				System.out.println(e);
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
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//设置要返回的日期为传入时间对于的周日
		return format.format(cal.getTime());
	}
	
	private  String getSeasonTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
        int month = cal.get(cal.MONTH) + 1;
        int quarter = 0;
        //判断季度
        if (month >= 1 && month <= 3) {
            quarter = 1;
        } else if (month >= 4 && month <= 6) {
            quarter = 2;
        } else if (month >= 7 && month <= 9) {
            quarter = 3;
        } else {
            quarter = 4;
        }
        return TimeConvertor.date2String(date,"yyyy")+"0"+quarter;
    }

}
*/