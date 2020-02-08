package com.cpit.cpmt.biz.impl.security.battery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.security.battery.AnaFaultKnowledgebaseDao;
import com.cpit.cpmt.dto.security.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.security.battery.AnaFaultKnowledgebase;

@Service
public class AnaFaultKnowledgebaseMgmt {
	@Autowired AnaFaultKnowledgebaseDao anaFaultKnowledgebaseDao;
	
	//故障知识库 --查询
	public Page<AnaFaultKnowledgebase> queryAnaFaultKnowledgebase(AnaFaultKnowledgebase param) {
		// TODO Auto-generated method stub
		return anaFaultKnowledgebaseDao.queryAnaFaultKnowledgebase(param);
	}
	//故障知识库 --新增
	public void addAnaFaultKnowledgebase(AnaFaultKnowledgebase param) {
		anaFaultKnowledgebaseDao.insertSelective(param);
	}
	//故障知识库 --修改
	public void updateAnaFaultKnowledgebase(AnaFaultKnowledgebase param) {
		anaFaultKnowledgebaseDao.updateByPrimaryKeySelective(param);
		
	}
	//故障知识库 --删除
	public void deleteAnaFaultKnowledgebase(AnaFaultKnowledgebase param) throws Exception {
		AnaFaultKnowledgebase result = anaFaultKnowledgebaseDao.selectByPrimaryKey(param.getBaseId());	
		if(result!=null) {
			anaFaultKnowledgebaseDao.deleteByPrimaryKey(param.getBaseId());
		}else {
			throw new Exception("无法删除,不存在");
		}
	}

}
