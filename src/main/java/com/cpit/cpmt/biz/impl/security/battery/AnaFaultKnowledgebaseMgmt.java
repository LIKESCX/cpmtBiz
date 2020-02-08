package com.cpit.cpmt.biz.impl.security.battery;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
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
		int baseId = SequenceId.getInstance().getId("anaFaultKnowledgebaseId");
		param.setBaseId(String.valueOf(baseId));
		anaFaultKnowledgebaseDao.insertSelective(param);
	}
	//故障知识库 --修改
	public void updateAnaFaultKnowledgebase(AnaFaultKnowledgebase param) {
		anaFaultKnowledgebaseDao.updateByPrimaryKeySelective(param);
		
	}
	//故障知识库 --删除
	@Transactional
	public void deleteAnaFaultKnowledgebase(AnaFaultKnowledgebase param) throws Exception {
		List<String> baseIdList = param.getBaseIdList();
		if(baseIdList!=null&&baseIdList.size()>0) {
			for (String baseId : baseIdList) {
				AnaFaultKnowledgebase result = anaFaultKnowledgebaseDao.selectByPrimaryKey(baseId);	
				if(result!=null) {
					anaFaultKnowledgebaseDao.deleteByPrimaryKey(param.getBaseId());
				}else {
					throw new Exception("无法删除,不存在");
				}
			}
		}
	}
	
	//导入功能
	@Transactional
	public void batchAddAnaFaultKnowledgebase(List<Map<String, Object>> list) throws Exception {
		if (list != null && !list.isEmpty()) {
			List<AnaFaultKnowledgebase> mkList = JsonUtil.mkList(list, AnaFaultKnowledgebase.class);
			System.out.println("mkList="+mkList);
			for (AnaFaultKnowledgebase anaFaultKnowledgebase : mkList) {
				anaFaultKnowledgebase.setBaseId(String.valueOf(SequenceId.getInstance().getId("anaFaultKnowledgebaseId")));
				anaFaultKnowledgebaseDao.insertSelective(anaFaultKnowledgebase);
			}
//			for (int i = 0; i < list.size(); i++) {
//				Map<String, Object> map = list.get(i);
//				String cardCode = (String) map.get("cardCode");
//				String unitId = (String) map.get("belongSubcompany");
//				String cityCode = (String) map.get("cityCode");
//				String coopUnitId = (String) map.get("coopUnitId");
//				String merchantCode = (String) map.get("merchantCode");
//				String note = (String) map.get("note");
//				map.put("cardId",SequenceId.getInstance().getId("anaFaultKnowledgebaseId"));
//				JsonUtil.mkList(mapList, clz,);
//			}
		}
		
	}

}
