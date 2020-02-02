package com.cpit.cpmt.biz.dao.monitor;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@MyBatisDao
public interface RealTimeAlarmInfoDao {

	public Page<AlarmInfo> queryRealTimeAlarmInfoByCondition(EquimentMonitorCondition emc);
	
	public Page<AlarmInfo> queryAlarmDetailInfosByConditions(@Param("operatorID")String operatorID,@Param("connectorID") String connectorID);
	
	public List<AlarmInfo> queryAlarmGraphicDisplayInfoByConditions(@Param("operatorID")String operatorID,@Param("connectorID") String connectorID);
	
	public List<AlarmInfo> queryAlarmSumCountByConditions(@Param("operatorID")String operatorID,@Param("connectorID") String connectorID);
}
