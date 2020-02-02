package com.cpit.cpmt.biz.dao.exchange.operator;

import java.util.List;

import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;

@MyBatisDao
public interface EquipmentInfoDAO {


    int insertSelective(EquipmentInfoShow record);
    
    int insertSelective1(EquipmentInfo record);

    //动态信息
    EquipmentInfoShow selectByPrimaryKey(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    //基本信息
    EquipmentInfoShow selectByEquipId(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    int updateByPrimaryKeySelective(EquipmentInfoShow record);


    int deleteByPrimaryKey(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    Page<EquipmentInfoShow> selectEquipmentByCondition(EquipmentInfoShow record);

    /*地图首页 交直流桩数*/
    Integer selectEquipmentType(EquipmentInfoShow equipmentInfo);

    /*地图首页 正常 故障 桩*/
    Integer selectEquipmentStatus(EquipmentInfoShow equipmentInfo);

    //利用率
    String getAllUseRate(StationInfoShow station);

    //单个利用率
    String getOneUseRate(EquipmentInfoShow equipmentInfo);

    /*地图首页 公共，普通（个人），专用桩数*/
    Integer selectStationType(EquipmentInfoShow equipmentInfo);

    Integer selectUnStationType(EquipmentInfoShow equipmentInfo);

    /*在线 公共，专用桩数*/
    Integer selectStationTypeOnLine(EquipmentInfoShow equipmentInfo);

    /*在线 交直流桩数*/
    Integer selectEquipmentTypeOnline (EquipmentInfoShow equipmentInfo);




    //单双枪充电桩数量
    List<EquipmentInfoShow> selectEquipmentNumList(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    /*根据充电站id获取充电桩列表*/
    List<EquipmentInfoShow> selectEquipmentList(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    /*根据充电站id获取充电桩数量*/
    Integer selectEquipmentListSize(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    //故障/所有桩数量
    Integer getEquipmentStatusNumber(EquipmentInfoShow equipmentInfo);
    
    List<EquipmentInfoShow> selectEquipmentByOperatorId(String operatorId);

}