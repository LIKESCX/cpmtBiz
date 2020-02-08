package com.cpit.cpmt.biz.dao.security.battery;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.AnaFaultKnowledgebase;

@MyBatisDao
public interface AnaFaultKnowledgebaseDao {
 
    int deleteByPrimaryKey(String baseId);

    int insert(AnaFaultKnowledgebase record);

    int insertSelective(AnaFaultKnowledgebase record);

    AnaFaultKnowledgebase selectByPrimaryKey(String baseId);

    int updateByPrimaryKeySelective(AnaFaultKnowledgebase record);

    int updateByPrimaryKey(AnaFaultKnowledgebase record);

	Page<AnaFaultKnowledgebase> queryAnaFaultKnowledgebase(@Param("param")AnaFaultKnowledgebase param);

}