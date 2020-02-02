package com.cpit.cpmt.biz.impl.battery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@Import({CountRest.class,CountUtil.class,PmmlUtil.class})
public class BmsAnalysisMgmt {
	@Autowired
	CountRest countRest;
	@Autowired
	AnaBmsSingleChargeMgmt anaBmsSingleChargeMgmt;
	
	public void obtainAnalysisAll() {
		
		List<BmsInfo> list = new ArrayList<>();
		Date d = new Date();
		for (int i = 0; i < 10; i++) {
		    BmsInfo c = new BmsInfo();//必须用第三方定义的BmsInfo类,不能使用采集的定义的类.
		    c.setbMSCode("1");
		    c.setbMSVer("1");
		    c.setMaxChargeCellVoltage(400 + "");
		    c.setMaxChargeCurrent(200 + "");
		    c.setMaxTemp((int) (55) + "");
		    c.setRatedCapacity(130 + "");
		    c.setTatalVoltage(200 + Math.random() * 100 + "");
		    c.setTotalCurrent(Math.random() * 100 + "");
		    c.setSoc((int) (Math.random() * 100) + "");
		    c.setVoltageH(3 + Math.random() + "");
		    c.setVoltageL(2 + Math.random()+ "");
		    c.setTemptureH((int) (30 + Math.random() * 10) + "");
		    c.setTemptureL((int) (20 + Math.random() * 10) + "");
		    c.setStartTime(d);
		    c.setEndTime(new Date(d.getTime() + 1000 * i));
		    list.add(c);
		}
		TotalResponse tr;
		try {
				tr = countRest.analysisAll(list);
				BmsAnalysisResult bmsAnalysisResult = tr.getBmsAnalysisResult();//获取分析结果
				
				AnaBmsSingleCharge anaBmsSingleCharge = new AnaBmsSingleCharge();
				//test begin
				anaBmsSingleCharge.setOperatorId("10086");
				anaBmsSingleCharge.setStationId("1008601");
				anaBmsSingleCharge.setEquipmentId("10086001");
				anaBmsSingleCharge.setConnectorId("100860001");
				//test end
				BeanUtils.copyProperties(bmsAnalysisResult, anaBmsSingleCharge);
				anaBmsSingleChargeMgmt.insertAnaBmsSingleCharge(anaBmsSingleCharge);
				String newHourTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndtime(),"yyyyMMddHH");
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
				String dayTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndtime(),"yyyyMMdd");
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
				String weekTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndtime(),"yyyyMMdd");
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
				String monthTime = TimeConvertor.date2String(anaBmsSingleCharge.getEndtime(),"yyyyMM");
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
				String seasonTime = getSeasonTime(anaBmsSingleCharge.getEndtime());
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
