package com.cpit.cpmt.biz.dao.message;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.message.ExcMessage;

@MyBatisDao
public interface ExcMessageDao {
    int deleteByPrimaryKey(Integer smsId);

    int insert(ExcMessage record);

    int insertSelective(ExcMessage record);

    ExcMessage selectByPrimaryKey(Integer smsId);

    int updateByPrimaryKeySelective(ExcMessage record);

    int updateByPrimaryKey(ExcMessage record);
    ExcMessage getLastedMessage(String phoneNum);
}