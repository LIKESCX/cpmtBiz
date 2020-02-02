package com.cpit.cpmt.biz.dao.security;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.EquipmentSafeWarning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface EquipmentSafeWarningDao {
    int deleteByPrimaryKey(Integer warningId);

    int insertSelective(EquipmentSafeWarning record);

    EquipmentSafeWarning selectByPrimaryKey(Integer warningId);

    int updateByPrimaryKeySelective(EquipmentSafeWarning record);

    List<EquipmentSafeWarning> getEquipmentSafeWarningListByEquipmentSafeWarning(EquipmentSafeWarning record);

    Integer selectStationWarning(@Param("stationId") String stationId, @Param("operatorId") String operatorId);
}