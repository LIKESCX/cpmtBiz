package com.cpit.cpmt.biz.impl.exchange.supplement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.exchange.supplement.SupplementInfoDao;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;

@Service
public class SuppleMentMgmt {
@Autowired SupplementInfoDao dao;


	public void addDto(SupplementInfo info) {
		dao.addDto(info);
	}
	
	public List<SupplementInfo> getByOid(String operatorID,String infName,String startTime,String endTime) {
		
		return dao.search(operatorID,infName, startTime, endTime);
		
	}
}
