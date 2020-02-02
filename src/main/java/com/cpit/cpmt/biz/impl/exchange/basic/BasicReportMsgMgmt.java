package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.BasicReportMsgInfoDao;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;

@Service
public class BasicReportMsgMgmt {
	//private final static Logger logger = LoggerFactory.getLogger(BasicReportMsgMgmt.class);
	
	@Autowired BasicReportMsgInfoDao basicReportMsgInfoDao;
	
	public Page<BasicReportMsgInfo> getBasicReportMsgList(SupplyCollect collectDao) {
		//return basicReportMsgInfoDao.queryList(collectDao);
		return null;
	}
	
	public List<BasicReportMsgInfo> getBasicReportMsgByOperatorIDAndFixedCycle(Map<String, String> map) {
		return basicReportMsgInfoDao.getBasicReportMsgByOperatorIDAndFixedCycle(map);
	}
}
