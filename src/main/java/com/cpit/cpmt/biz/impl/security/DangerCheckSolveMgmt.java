package com.cpit.cpmt.biz.impl.security;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.security.DangerAuditHisDao;
import com.cpit.cpmt.biz.dao.security.DangerCheckSolveDao;
import com.cpit.cpmt.biz.dao.security.DangerFileDao;
import com.cpit.cpmt.dto.security.DangerAuditHis;
import com.cpit.cpmt.dto.security.DangerCheckSolve;
import com.cpit.cpmt.dto.security.DangerFile;

@Service
public class DangerCheckSolveMgmt {
	      
	@Autowired
	private DangerCheckSolveDao dangerCheckSolveDao;
	
	@Autowired
	private DangerAuditHisDao dangerAuditHisDao;
	
	@Autowired
	private DangerFileDao dangerFileDao;
	
	private final static Logger logger = LoggerFactory.getLogger(DangerCheckSolveMgmt.class);

	@Transactional
	public void addDangerCheckSolve(DangerCheckSolve dangerCheckSolve) {
		Date date = new Date();
		int id = SequenceId.getInstance().getId("dangerId");
		dangerCheckSolve.setDangerId(id);
		dangerCheckSolve.setInTime(date);
		dangerCheckSolveDao.insertSelective(dangerCheckSolve);
	}

	public Page<DangerCheckSolve> getDangerCheckSolveList(DangerCheckSolve dangerCheckSolve) {
		return dangerCheckSolveDao.getDangerCheckSolveList(dangerCheckSolve);
	}

	@Transactional
	public void updateDangerCheckSolve(DangerCheckSolve dangerCheckSolve) {
		Date date = new Date();
		dangerCheckSolve.setOperateTime(date);
		Integer dangerStatus = dangerCheckSolve.getDangerStatus();
		if(dangerStatus==DangerCheckSolve.DANGER_STATUS_YIZHENGGAI||dangerStatus==DangerCheckSolve.DANGER_STATUS_SHENHEBUTONGGUO) {
			DangerAuditHis dangerAuditHis = new DangerAuditHis();
			String auditPerson = dangerCheckSolve.getAuditPerson();
			String auditNote = dangerCheckSolve.getAuditNote();
			int id = SequenceId.getInstance().getId("dangerHisId");
			dangerAuditHis.setDangerHisId(id);
			dangerAuditHis.setDangerId(dangerCheckSolve.getDangerId());
			dangerAuditHis.setAuditDate(date);
			dangerAuditHis.setAuditPerson(auditPerson);
			if(null!=auditNote) {
				dangerAuditHis.setAuditNote(auditNote);
			}
			if(dangerStatus==DangerCheckSolve.DANGER_STATUS_YIZHENGGAI) {
				dangerAuditHis.setAuditStatus(DangerAuditHis.STATUS_TONGGUO);
			}else {
				dangerAuditHis.setAuditStatus(DangerAuditHis.STATUS_BUTONGGUO);
			}
			dangerAuditHisDao.insertSelective(dangerAuditHis);
		}
		dangerCheckSolveDao.updateByPrimaryKeySelective(dangerCheckSolve);
	}

	@Transactional
	public void delDangerCheckSolve(Integer riskId) {
		dangerCheckSolveDao.deleteByPrimaryKey(riskId);
	}

	public List<DangerAuditHis> getDangerAuditHisList(Integer dangerId) {
		return dangerAuditHisDao.getDangerAuditHisList(dangerId);
	}

	@Transactional
	public void importExcelData(List<Map> data) {
		DangerCheckSolve dangerCheckSolve = new DangerCheckSolve();
		for (Map map : data) {
			int id = SequenceId.getInstance().getId("dangerId");
			dangerCheckSolve.setDangerId(id);
			String operatorId = map.get("p1").toString();//统一社会信用代码
			String operatorName = map.get("p2").toString();//运营商名称
			String stationName = map.get("p3").toString();//充电站名称
			String equipmentId = map.get("p4").toString();//充电设施编码
			String equipmentName = map.get("p5").toString();//充电设施
			String dangerType = map.get("p6").toString();//隐患类型
			String dangerLevel = map.get("p7").toString();//隐患级别
			String dangerDesc = map.get("p8").toString();//隐患描述
			String dealStep = map.get("p9").toString();//整改措施及计划
			String dependArea = map.get("p10").toString();//属地监管责任辖区
			String dependPerson = map.get("p11").toString();//属地监管责任人
			String tradeArea = map.get("p12").toString();//行业领域监管部门
			String tradePerson = map.get("p13").toString();//行业领域监管责任人
			String mainUnit = map.get("p14").toString();//主体责任单位
			String mainPerson = map.get("p15").toString();//主体责任人
			String dangerCommit = map.get("p16").toString();//备注
		}
	}
	
	@Transactional
	public void addDangerFile(DangerFile dangerFile) {
		int id = SequenceId.getInstance().getId("dangerFileId");
		dangerFile.setDangerFileId(id);
		dangerFileDao.insertSelective(dangerFile);
	}
	
	public List<DangerFile> getDangerFileList(Integer dangerId) {
		return dangerFileDao.getDangerFileList(dangerId);
	}
}
