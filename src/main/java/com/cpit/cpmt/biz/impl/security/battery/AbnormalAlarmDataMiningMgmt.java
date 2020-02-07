package com.cpit.cpmt.biz.impl.security.battery;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.security.battery.AbnormalAlarmDataMiningConditions;
import com.cpit.cpmt.dto.security.battery.AbnormalAlarmDataMiningDto;

@Service
public class AbnormalAlarmDataMiningMgmt {
	@Autowired AlarmInfoDao alarmInfoDao;
	
	public List<AbnormalAlarmDataMiningDto> queryFirstLevelAbnormalAlarmData(AbnormalAlarmDataMiningConditions param) {
		//Date startTime = param.getStartTime();
		//Date endTime = param.getEndTime();
		if(param.getTimeGranularity()==1) {//表示按小时统计
			List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelHourAbnormalAlarmData(param);
			return getHandleResult(infoList);
		}else if(param.getTimeGranularity()==2) {//表示按天统计
			List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelDayAbnormalAlarmData(param);
			return getHandleResult(infoList);
		}else if(param.getTimeGranularity()==3) {//表示按周统计
			List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelWeekAbnormalAlarmData(param);
			return getHandleResult(infoList);
		}else if(param.getTimeGranularity()==4) {//表示按月统计
			List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelMonthAbnormalAlarmData(param);
			return getHandleResult(infoList);
		}else if(param.getTimeGranularity()==5) {//表示按季统计
			List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelSeasonAbnormalAlarmData(param);
			return getHandleResult(infoList);
		}else {
			return null;
		}
		
	}
	
	public List<AbnormalAlarmDataMiningDto> getHandleResult(List<AlarmInfo> infoList){
		List<AbnormalAlarmDataMiningDto> list = new ArrayList<AbnormalAlarmDataMiningDto>();
		if(infoList!=null&&infoList.size()>0) {
			continueOut:
			for (AlarmInfo alarmInfo : infoList) {
				String commonStatisticalTime = alarmInfo.getCommonStatisticalTime();
				for (AbnormalAlarmDataMiningDto abnormalAlarmDataMiningDto : list) {
					if(commonStatisticalTime.equals(abnormalAlarmDataMiningDto.getCommonStatisticalTime())) {
						if("1".equals(alarmInfo.getAlarmType())) {
							abnormalAlarmDataMiningDto.setAlarmTypeOne("1");
							abnormalAlarmDataMiningDto.setAlarmTypeOneTimes(alarmInfo.getAlarmTypeTimes());
						}else if("2".equals(alarmInfo.getAlarmType())) {
							abnormalAlarmDataMiningDto.setAlarmTypeTwo("2");
							abnormalAlarmDataMiningDto.setAlarmTypeTwoTimes(alarmInfo.getAlarmTypeTimes());
						}else if ("3".equals(alarmInfo.getAlarmType())) {
							abnormalAlarmDataMiningDto.setAlarmTypeThree("3");
							abnormalAlarmDataMiningDto.setAlarmTypeThreeTimes(alarmInfo.getAlarmTypeTimes());
						}
						continue continueOut;//跳到外层循环执行下轮外层循环.
					}
				}
				//没有执行到这一步(continue continueOut;)会执行下面的代码
				AbnormalAlarmDataMiningDto newAbnormalAlarmDataMiningDto = new AbnormalAlarmDataMiningDto();
				if("1".equals(alarmInfo.getAlarmType())) {
					newAbnormalAlarmDataMiningDto.setAlarmTypeOne("1");
					newAbnormalAlarmDataMiningDto.setAlarmTypeOneTimes(alarmInfo.getAlarmTypeTimes());
				}else if("2".equals(alarmInfo.getAlarmType())) {
					newAbnormalAlarmDataMiningDto.setAlarmTypeTwo("2");
					newAbnormalAlarmDataMiningDto.setAlarmTypeTwoTimes(alarmInfo.getAlarmTypeTimes());
				}else if ("3".equals(alarmInfo.getAlarmType())) {
					newAbnormalAlarmDataMiningDto.setAlarmTypeThree("3");
					newAbnormalAlarmDataMiningDto.setAlarmTypeThreeTimes(alarmInfo.getAlarmTypeTimes());
				}
				newAbnormalAlarmDataMiningDto.setCommonStatisticalTime(alarmInfo.getCommonStatisticalTime());
				list.add(newAbnormalAlarmDataMiningDto);
			}
			return list;
		}else {
			return list;
		}
	}

}
