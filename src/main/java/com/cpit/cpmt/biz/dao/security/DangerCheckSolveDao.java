package com.cpit.cpmt.biz.dao.security;

import java.util.List;
import java.util.Map;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.DangerCheckSolve;

@MyBatisDao
public interface DangerCheckSolveDao {
    int deleteByPrimaryKey(Integer dangerId);

    int insert(DangerCheckSolve record);

    int insertSelective(DangerCheckSolve record);

    DangerCheckSolve selectByPrimaryKey(Integer dangerId);

    int updateByPrimaryKeySelective(DangerCheckSolve record);

    int updateByPrimaryKey(DangerCheckSolve record);

	Page<DangerCheckSolve> getDangerCheckSolveList(DangerCheckSolve dangerCheckSolve);
}