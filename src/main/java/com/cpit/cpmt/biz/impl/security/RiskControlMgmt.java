package com.cpit.cpmt.biz.impl.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.dao.security.RiskControlDao;
import com.cpit.cpmt.dto.security.RiskControl;

@Service
public class RiskControlMgmt {
	      
	@Autowired
	private RiskControlDao riskControlDao;
	
	@Autowired
    private AmqpTemplate amqpTemplate;
	
	private final static Logger logger = LoggerFactory.getLogger(RiskControlMgmt.class);

	@Transactional
	public void addRiskControl(RiskControl riskControl) {
		int id = SequenceId.getInstance().getId("riskId");
		Date date = new Date();
		riskControl.setRiskId(id);
		riskControl.setAlarmTime(date);
		riskControlDao.insertSelective(riskControl);
		amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME,"riskControlChanged");
	}

	public Page<RiskControl> getRiskControlList(RiskControl RiskControl) {
		return riskControlDao.getRiskControlList(RiskControl);
	}

	@Transactional
	public void updateRiskControl(RiskControl RiskControl) {
		riskControlDao.updateByPrimaryKeySelective(RiskControl);
	}

	@Transactional
	public void delRiskControl(Integer riskId) {
		riskControlDao.deleteByPrimaryKey(riskId);
	}
	
	public Object getCountByLevelAndType() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<RiskControl> levelList = riskControlDao.getCountByLevel();
		List<RiskControl> typelist = riskControlDao.getCountByType();
		map.put("levelList", levelList);
		map.put("typelist", typelist);
		return map;
	}


	public static void main(String[] args){
	    String str = "  ";
		System.out.println(str.isEmpty());
		String str2 = str.trim();
		System.out.println(str2.isEmpty());
	}

}
