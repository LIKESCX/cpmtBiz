package com.cpit.cpmt.biz.impl.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.monitor.RealTimeAlarmInfoDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@Service
public class RealTimeAlarmInfoMgmt {
	@Autowired RealTimeAlarmInfoDao realTimeAlarmInfoDao;
	public Page<AlarmInfo> queryRealTimeAlarmInfo(EquimentMonitorCondition emc) {
		return realTimeAlarmInfoDao.queryRealTimeAlarmInfoByCondition(emc);
	}
	public Page<AlarmInfo> queryAlarmRealDtailInfo(String operatorID,String connectorID) {
		return realTimeAlarmInfoDao.queryAlarmDetailInfosByConditions(operatorID,connectorID);
	}
	public List<Map<String,String>> queryAlarmGraphicDisplayInfo(String operatorID,String connectorID) {
		List<AlarmInfo> infoList = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfoByConditions(operatorID,connectorID);
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		if(infoList!=null&&infoList.size()>=1) {
			List<AlarmInfo> infoSList = realTimeAlarmInfoDao.queryAlarmSumCountByConditions(operatorID,connectorID);
			for (AlarmInfo alarmsInfo : infoSList) {
				Map<String,String> map = new HashMap<String,String>();
				Date receivedSTime = alarmsInfo.getReceivedTime();
				for (AlarmInfo alarmInfo : infoList) {
					Date receivedTime = alarmInfo.getReceivedTime();
					int result = receivedTime.compareTo(receivedSTime);
					if(result==0) {
						String alarmLevel = alarmInfo.getAlarmLevel();
						switch (alarmLevel) {
						case "1":
							map.put("alarmLevel_1", String.valueOf(alarmInfo.getAlarmCount()));
							break;
						case "2":
							map.put("alarmLevel_2", String.valueOf(alarmInfo.getAlarmCount()));
							break;
						case "3":
							map.put("alarmLevel_3", String.valueOf(alarmInfo.getAlarmCount()));
							break;
						default:
							break;
						}
					}
				}
				String alarmLevel_1 = map.get("alarmLevel_1");
				String alarmLevel_2 = map.get("alarmLevel_2");
				String alarmLevel_3 = map.get("alarmLevel_3");
				if(StringUtils.isEmpty(alarmLevel_1)) {
					map.put("alarmLevel_1","0");
				} 
				if(StringUtils.isEmpty(alarmLevel_2)) {
					map.put("alarmLevel_2","0");
				}
				if(StringUtils.isEmpty(alarmLevel_3)) {
					map.put("alarmLevel_3","0");
				}
				map.put("alarmSumCount", String.valueOf(alarmsInfo.getAlarmSumCount()));
				map.put("receivedTime",DateFormatUtils.format(receivedSTime, TimeConvertor.FORMAT_MINUS_24HOUR));
				mapList.add(map);
			}
		}
		return mapList;
	}

}
