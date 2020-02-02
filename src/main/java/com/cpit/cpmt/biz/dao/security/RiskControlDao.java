package com.cpit.cpmt.biz.dao.security;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.RiskControl;

@MyBatisDao
public interface RiskControlDao {
    int deleteByPrimaryKey(Integer riskId);

    int insert(RiskControl record);

    int insertSelective(RiskControl record);

    RiskControl selectByPrimaryKey(Integer riskId);

    int updateByPrimaryKeySelective(RiskControl record);

    int updateByPrimaryKey(RiskControl record);

	Page<RiskControl> getRiskControlList(RiskControl riskControl);

	List<RiskControl> getCountByLevel();

	List<RiskControl> getCountByType();
}