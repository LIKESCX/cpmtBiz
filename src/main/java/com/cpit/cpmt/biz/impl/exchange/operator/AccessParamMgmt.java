package com.cpit.cpmt.biz.impl.exchange.operator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.AccessParamDao;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;

@Service
public class AccessParamMgmt {
	
	@Autowired
	private AccessParamDao accessParamDao;
	
	private final static Logger logger = LoggerFactory.getLogger(AccessParamMgmt.class);

	@Cacheable(cacheNames="operator-access-Param-by-id",key="#root.caches[0].name+#operatorId",unless="#result == null")
	public List<AccessParam> getAccessParamInfoById(String operatorId) {
		
		return accessParamDao.getAccessParamByOperatorId(operatorId);
	}
	
	@Transactional
	public void addAccessParam(AccessParam accessParam) {
		int id = SequenceId.getInstance().getId("excAccessId");
		accessParam.setAccessId(id);
		accessParamDao.insertSelective(accessParam);
	}

	public Page<AccessParam> getAccessParamList(AccessParam accessParam) {
		return accessParamDao.getAccessParamList(accessParam);
	}

	@Transactional
	@Caching(evict={
	 	@CacheEvict(cacheNames="operator-access-Param-by-id",key="#root.caches[0].name+#accessParam.operatorID")
	})
	public void updateAccessParam(AccessParam accessParam) {
		accessParamDao.updateByPrimaryKeySelective(accessParam);
	}

	@CacheEvict(cacheNames="operator-access-Param-by-id",allEntries=true)
	public void delAccessParam(Integer accessId) {
		accessParamDao.deleteByPrimaryKey(accessId);
	}

	public AccessParam getAccessParamByCondion(AccessParam accessParam) {
		
		return accessParamDao.getAccessParamByCondion(accessParam);
	}

}
