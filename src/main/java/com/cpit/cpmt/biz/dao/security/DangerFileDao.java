package com.cpit.cpmt.biz.dao.security;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.DangerFile;

@MyBatisDao
public interface DangerFileDao {
    int deleteByPrimaryKey(Integer dangerFileId);

    int insert(DangerFile record);

    int insertSelective(DangerFile record);

    DangerFile selectByPrimaryKey(Integer dangerFileId);

    int updateByPrimaryKeySelective(DangerFile record);

    int updateByPrimaryKey(DangerFile record);

	List<DangerFile> getDangerFileList(Integer dangerId);
}