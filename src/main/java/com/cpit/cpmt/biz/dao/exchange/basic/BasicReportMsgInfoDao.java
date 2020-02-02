package com.cpit.cpmt.biz.dao.exchange.basic;

import java.util.List;
import java.util.Map;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;
@MyBatisDao
public interface BasicReportMsgInfoDao {
    int deleteByPrimaryKey(String id);

    int insert(BasicReportMsgInfo record);

    int insertSelective(BasicReportMsgInfo record);

    BasicReportMsgInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BasicReportMsgInfo record);

    int updateByPrimaryKey(BasicReportMsgInfo record);

	List<BasicReportMsgInfo> getBasicReportMsgByOperatorIDAndFixedCycle(Map<String, String> map);

	//Page<BasicReportMsgInfo> queryList(SupplyCollect supplyCollect);
}