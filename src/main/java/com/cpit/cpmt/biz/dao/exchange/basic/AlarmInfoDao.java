package com.cpit.cpmt.biz.dao.exchange.basic;
import java.util.Map;

import com.cpit.common.db.Page;
import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
@MyBatisDao
public interface AlarmInfoDao {
  
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmInfo record);

    int insertSelective(AlarmInfo record);

    AlarmInfo selectByPrimaryKey(Integer id);
 
    int updateByPrimaryKeySelective(AlarmInfo record);

    int updateByPrimaryKey(AlarmInfo record);

	AlarmItem checkCurrentAlarmValid(@Param("params")Map<String,String> map);

	//查询充电站最新一次告警信息
    String selectStationAlarmLastest (@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    Page<AlarmInfo> selectEquipmentAlarm (@Param("equipmentId") String equipmentId, @Param("operatorId") String operatorId);
}