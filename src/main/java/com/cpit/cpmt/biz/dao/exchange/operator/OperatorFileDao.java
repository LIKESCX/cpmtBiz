package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

@MyBatisDao
public interface OperatorFileDao {
    int deleteByPrimaryKey(String fileId);

    int insert(OperatorFile record);

    int insertSelective(OperatorFile record);

    OperatorFile selectByPrimaryKey(String fileId);

    int updateByPrimaryKeySelective(OperatorFile record);

    int updateByPrimaryKey(OperatorFile record);

	Page<OperatorInfoExtend> getOperatorFileListById(String operatorId);

	void deleteFilesByOperatorId(String operatorId);
}