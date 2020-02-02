package com.cpit.cpmt.biz.dao.exchange.basic;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
@MyBatisDao
public interface ConnectorStatusInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConnectorStatusInfo record);

    int insertSelective(ConnectorStatusInfo record);

    ConnectorStatusInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConnectorStatusInfo record);

    int updateByPrimaryKey(ConnectorStatusInfo record);

	ConnectorStatusInfo selectByConditions(@Param("operatorID")String operatorID, @Param("connectorID")String connectorID);
}
