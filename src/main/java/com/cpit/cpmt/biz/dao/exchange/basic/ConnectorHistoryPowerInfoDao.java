package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorHistoryPowerInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentHistoryInfo;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface ConnectorHistoryPowerInfoDao {

    int deleteByPrimaryKey(Integer id);

    int insert(ConnectorHistoryPowerInfo record);

    int insertSelective(ConnectorHistoryPowerInfo record);

    ConnectorHistoryPowerInfo selectByPrimaryKey(Integer id);
  
    int updateByPrimaryKeySelective(ConnectorHistoryPowerInfo record);

    int updateByPrimaryKey(ConnectorHistoryPowerInfo record);

    //地图首页实时功率查询
    List<ConnectorHistoryPowerInfo> selectPowerTenMinutes(StationInfoShow station);

    //当前区域范围的总实时功率
    Double selectTotalPower(StationInfoShow station);

    //充电站充电设备详情实时功率
    ConnectorHistoryPowerInfo selectPowerByOne(@Param("stationId") String stationId,@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    //充电设备接口状态历史信息
    List<ConnectorHistoryPowerInfo> selectConnectorHis(EquipmentHistoryInfo equipmentHistoryInfo);

    //long countByExample(ConnectorHistoryPowerInfoExample example);
    //int updateByExampleSelective(@Param("record") ConnectorHistoryPowerInfo record, @Param("example") ConnectorHistoryPowerInfoExample example);
    //int updateByExample(@Param("record") ConnectorHistoryPowerInfo record, @Param("example") ConnectorHistoryPowerInfoExample example);
    //int deleteByExample(ConnectorHistoryPowerInfoExample example);
    //List<ConnectorHistoryPowerInfo> selectByExample(ConnectorHistoryPowerInfoExample example);
}