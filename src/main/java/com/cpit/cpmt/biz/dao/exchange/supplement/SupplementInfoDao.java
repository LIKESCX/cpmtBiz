package com.cpit.cpmt.biz.dao.exchange.supplement;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;

@MyBatisDao
public interface SupplementInfoDao {
public void addDto(SupplementInfo info);
public List<SupplementInfo> search(@Param("operatorID")String operatorID,@Param("infName")String infName,@Param("startTime") String startTime,@Param("endTime") String endTime);
}
