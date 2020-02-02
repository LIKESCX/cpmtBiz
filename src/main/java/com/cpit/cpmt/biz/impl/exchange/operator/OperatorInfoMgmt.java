package com.cpit.cpmt.biz.impl.exchange.operator;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorChangeHisDao;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorFileDao;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorInfoDao;
import com.cpit.cpmt.biz.impl.exchange.basic.BasicReportMsgMgmt;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorChangeHis;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;


@Service
public class OperatorInfoMgmt {
	
	@Autowired
	private OperatorInfoDao operatorInfoDao;
	
	@Autowired
	private OperatorFileDao operatorFileDao;
	
	@Autowired
	private OperatorChangeHisDao operatorChangeHisDao;
	
	@Autowired
	private AccessParamMgmt accessParamMgmt;
	
	@Autowired
	private EquipmentInfoMgmt equipmentInfoMgmt;
	
	@Autowired
	private BasicReportMsgMgmt basicReportMsgMgmt;
	
	private final static Logger logger = LoggerFactory.getLogger(OperatorInfoMgmt.class);
	//运营商基本信息管理
	
	//查询
 	public Page<OperatorInfoExtend> getOperatorInfoList(OperatorInfoExtend operatorInfoExtend){
 		return operatorInfoDao.getOperatorInfoList(operatorInfoExtend);
 	}
 	
 	//创建
 	@Transactional
 	@CacheEvict(cacheNames="biz-operators",allEntries=true)
 	public void addOperatorInfo(OperatorInfoExtend operatorInfoExtend){
 		Date date = new Date();
 		operatorInfoDao.insertSelective(operatorInfoExtend);
 		int id = SequenceId.getInstance().getId("excChangeId");
		OperatorChangeHis changeHis = new OperatorChangeHis();
		changeHis.setChangeId(id);
		changeHis.setOperatorInfoExtend(operatorInfoExtend);
		changeHis.setConnectionTime(date);
		changeHis.setOperateType(OperatorChangeHis.OPERATOR_TYPE_ADD);
		operatorChangeHisDao.addOperatorChangeHis(changeHis);
 	}
 	
 	//删除
 	@Transactional
 	@Caching(evict={
 	 		@CacheEvict(cacheNames="biz-operator-id",key="#root.caches[0].name+#operatorId"),
 	 		@CacheEvict(cacheNames="biz-operators",allEntries=true)
 	})
 	public void deleteOperatorInfo(String operatorId){
 		operatorInfoDao.deleteByPrimaryKey(operatorId);
 	}
 	
	//更新
 	@Transactional
 	@Caching(evict={
 		@CacheEvict(cacheNames="biz-operator-id",key="#root.caches[0].name+#operatorInfoExtend.operatorID"),
 		@CacheEvict(cacheNames="biz-operators",allEntries=true)
 	})
 	public void updateOperatorInfo(OperatorInfoExtend operatorInfoExtend){
 		String operatorID = operatorInfoExtend.getOperatorID();
 		operatorInfoDao.updateByPrimaryKeySelective(operatorInfoExtend);
 		OperatorInfoExtend operatorInfo = operatorInfoDao.selectByPrimaryKey(operatorID);
 		operatorInfo.setOperatePerson(operatorInfoExtend.getOperatePerson());
 		int id = SequenceId.getInstance().getId("excChangeId");
		OperatorChangeHis changeHis = new OperatorChangeHis();
		changeHis.setChangeId(id);
		changeHis.setOperatorInfoExtend(operatorInfo);
		changeHis.setOperateType(OperatorChangeHis.OPERATOR_TYPE_UPDATE);
		operatorChangeHisDao.addOperatorChangeHis(changeHis);
		
		List<EquipmentInfoShow> equipList = equipmentInfoMgmt.selectEquipmentByOperatorId(operatorID);
		if(equipList == null || equipList.isEmpty())
			return;
		for(EquipmentInfoShow eis: equipList) {
			if(operatorInfoExtend.getStatusCd()==OperatorInfoExtend.STATUS_CD_TINGYUN) {
				eis.setEquipmentStatus(5);
			}else if(operatorInfoExtend.getStatusCd()==OperatorInfoExtend.STATUS_CD_HUOYUE){
				eis.setEquipmentStatus(50);
			}
			equipmentInfoMgmt.updateEquipmentInfo(eis);
		}
		
 	}
 	
	@Cacheable(cacheNames="biz-operator-id",key="#root.caches[0].name+#operatorId",unless="#result == null")
 	public OperatorInfoExtend getOperatorInfoById(String operatorId){
 		return operatorInfoDao.selectByPrimaryKey(operatorId);
 	}
	
	@Cacheable(cacheNames="biz-operator-name",key="#root.caches[0].name+#operatorName",unless="#result == null")
	public OperatorInfoExtend getOperatorInfoByName(String operatorName){
 		return operatorInfoDao.getOperatorInfoByName(operatorName);
 	}

	public Page<OperatorInfoExtend> getOperatorListWithStationCount(OperatorInfoExtend operatorInfoExtend) {
		if(operatorInfoExtend.getUserType()==OperatorInfoExtend.TYPE_MANGER) {
			List<String> operatorIdList = operatorInfoDao.getStationOperatorListByArea(operatorInfoExtend);
			operatorInfoExtend.setOperatorList(operatorIdList);
 		}
		return operatorInfoDao.getOperatorListWithStationCount(operatorInfoExtend);
	}

	public Page<OperatorInfoExtend> getOperatorFileListById(String operatorId) {
		return operatorFileDao.getOperatorFileListById(operatorId);
	}

	@Transactional
	public void addOperatorFile(OperatorFile operatorFile) {
		int id = SequenceId.getInstance().getId("excFiledId");
		operatorFile.setFileId(String.valueOf(id));
		operatorFileDao.insertSelective(operatorFile);
	}

	@Cacheable(cacheNames="biz-operators",key="#root.caches[0].name",unless="#result == null")
	public List<OperatorInfoExtend> getAuditPassOperatorList() {
		return operatorInfoDao.getAuditPassOperatorList();
	}

	@Transactional
	public void delFilesByOperatorId(String operatorID) {
		operatorFileDao.deleteFilesByOperatorId(operatorID);
	}

	public List<OperatorInfoExtend> getTotalElectric(String operatorId) {
		return operatorInfoDao.getTotalElectric(operatorId);
	}

	public List<EquipmentInfoShow> getTotalAllowance(String operatorId) {
		return operatorInfoDao.getTotalAllowance(operatorId);
	}

	public OperatorInfoExtend getTotalPower(String operatorId) {
		return operatorInfoDao.getTotalPower(operatorId);
	}

	/*public List<OperatorInfoExtend> getPowerByHour(String operatorId) {
		List<OperatorInfoExtend> list = new ArrayList<>();
		for(int i=0;i<24;i++){
			OperatorInfoExtend extend = new OperatorInfoExtend();
			Map<String, String> map = new HashMap<>();
			map.put("operatorID", operatorId);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)-i);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.set(calendar2.HOUR_OF_DAY, calendar2.get(calendar2.HOUR_OF_DAY)-i+1);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//logger.info(i+"小时前时间为："+df.format(calendar.getTime()));
			//logger.info(i+1+"小时前时间为："+df.format(calendar2.getTime()));
			map.put("date1", df.format(calendar.getTime()));
			map.put("date2", df.format(calendar2.getTime()));
			String powerByHour = operatorInfoDao.getPowerByHour(map);
			//logger.info("查询出的充电量为："+powerByHour);
			if(powerByHour==null) {
				powerByHour = "0";
			}
			extend.setFoundDate(calendar.getTime());
			extend.setTotalPower(powerByHour);
			list.add(extend);
		}
		return list;
	}*/
	
	public List<OperatorInfoExtend> getPowerByHour(String operatorId) throws Exception {
		List<OperatorInfoExtend> list = new ArrayList<>();
		for(int i=0;i<10;i++){
			OperatorInfoExtend extend = new OperatorInfoExtend();
			Map<String, String> map = new HashMap<>();
			map.put("operatorID", operatorId);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-i);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.set(calendar2.DAY_OF_MONTH, calendar2.get(calendar2.DAY_OF_MONTH)-i);
			calendar2.set(calendar2.HOUR_OF_DAY, 23);
			calendar2.set(calendar2.MINUTE, 59);
			calendar2.set(calendar2.SECOND, 59);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = "";
			if(i==0) {
				time = df.format(calendar.getTime());
			}else {
				time = df.format(calendar2.getTime());
			}
			map.put("time",time);
			String power = operatorInfoDao.getPowerDynamic(map);
			if(power==null) {
				power = "0";
			}
			Date dd = df.parse(time);
			extend.setFoundDate(dd);
			extend.setTotalPower(power);
			list.add(extend);
		}
		Collections.reverse(list);
		return list;
	}
	
	//运营商近10个月补贴金额动态信息
	public List<Map<String, String>> getDynamicPowerAndAllowancePrice(String operatorId) {
		List<Map<String, String>> list = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1); //要先+1,才能把本月的算进去
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String time = "";
		for(int i=0; i<10; i++){
			Map<String, String> map = new HashMap<>();
			Map<String, String> result = new HashMap<>();
			map.put("operatorID", operatorId);
			cal.set(Calendar.DAY_OF_MONTH, 20);
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1); //逐次往前推1个月
			int lastMonthMaxDay=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);
			if(i==0) {
				time = sdf.format(cal2.getTime());
			}else {
				time = sdf.format(cal.getTime());
			}
			map.put("time", time);
			String totalAllowance = operatorInfoDao.getAllowancePriceDynamic(map);
			if(null == totalAllowance) {
				totalAllowance = "0";
			}
			String power = operatorInfoDao.getPowerDynamic(map);
			if(null == power) {
				power = "0";
			}
			result.put("time", time);
			result.put("totalAllowance", totalAllowance);
			result.put("power", power);
			list.add(result);
		}
		Collections.reverse(list);
		return list;
	}
	
	public void updateOperatorStatusByFixedCycle() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-6);
		OperatorInfoExtend extend = new OperatorInfoExtend();
		extend.setStatusCd(OperatorInfoExtend.STATUS_CD_HUOYUE);
		Page<OperatorInfoExtend> operatorInfoList = operatorInfoDao.getOperatorInfoList(extend);
		for (OperatorInfoExtend operatorInfoExtend : operatorInfoList) {
			Date connectionTime = operatorInfoExtend.getConnectionTime();
			if(connectionTime.before(cal.getTime())) {
				updateOperatorStatusByFixedCycleProgress(operatorInfoExtend);
			}else {
				continue;
			}
			
		}
	}

	@Transactional
	public void updateOperatorStatusByFixedCycleProgress(OperatorInfoExtend operatorInfoExtend) {
		String operatorId = operatorInfoExtend.getOperatorID();
		int interFaceCount = 0;
		int statusCd = 0;
		int reportTime = 0;
		List<String> interFaceList = new ArrayList<>();
		List<AccessParam> accessParamInfoById = accessParamMgmt.getAccessParamInfoById(operatorId);
		for (AccessParam accessParam : accessParamInfoById) {
			String interfaceAddress = accessParam.getInterfaceAddress();
			if(null!=interfaceAddress && ""!=interfaceAddress) {
				interFaceList.add(interfaceAddress);
			}
		}
		interFaceCount = interFaceList.size();
		
		EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
		equipmentInfoShow.setOperatorID(operatorId);
		Page<EquipmentInfoShow> selectEquipmentByCondition = equipmentInfoMgmt.selectEquipmentByCondition(equipmentInfoShow);
		for (EquipmentInfoShow equipment : selectEquipmentByCondition) {
			Integer equipmentStatus = equipment.getEquipmentStatus();
			if(!equipmentStatus.equals("5")) {
				statusCd = 1;
				break;
			}
		}
		
		String now = "";
		String fixedTime = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		now = sdf.format(cal.getTime());
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-6);
		fixedTime = sdf.format(cal.getTime());
		Map<String, String> map = new HashMap<>();
		map.put("operatorID", operatorId);
		map.put("now", now);
		map.put("fixedTime", fixedTime);
		List<BasicReportMsgInfo> reportList = basicReportMsgMgmt.getBasicReportMsgByOperatorIDAndFixedCycle(map);
		reportTime = reportList.size();
		
		logger.info("interFaceCount is:"+interFaceCount+",statusCd:"+statusCd+",reportTime="+reportTime);
		if(interFaceCount==0 && statusCd==0 && reportTime==0) {
			OperatorInfoExtend operatorInfo = new OperatorInfoExtend();
			operatorInfo.setOperatorID(operatorId);
			operatorInfo.setStatusCd(OperatorInfoExtend.STATUS_CD_TINGYUN);
			operatorInfoDao.updateByPrimaryKeySelective(operatorInfo);
			
			EquipmentInfoShow equipmentInfo = new EquipmentInfoShow();
			equipmentInfo.setOperatorID(operatorId);
			equipmentInfo.setEquipmentStatus(5);
			equipmentInfoMgmt.updateEquipmentInfo(equipmentInfo);
			
			int id = SequenceId.getInstance().getId("excChangeId");
			OperatorChangeHis changeHis = new OperatorChangeHis();
			changeHis.setChangeId(id);
			changeHis.setOperatorInfoExtend(operatorInfoExtend);
			operatorChangeHisDao.addOperatorChangeHis(changeHis);
		}
	}
	
	public Page<OperatorChangeHis> getChangeListByCondion(OperatorChangeHis operatorChangeHis){
 		return operatorChangeHisDao.getChangeListByCondion(operatorChangeHis);
 	}
	
	public List<OperatorInfoExtend> getOperatorWithAccess(){
		return operatorInfoDao.getOperatorWithAccess();
	}
	
	public List<OperatorInfoExtend> getOperatorTotalEquipment() {
		return operatorInfoDao.getOperatorTotalEquipment();
	}
	
	public List<OperatorInfoExtend> getOperatorTotalCharge() {

		return operatorInfoDao.getOperatorTotalCharge();
	}
	
	public List<OperatorInfoExtend> getAreaTotalEquipment() {
		return operatorInfoDao.getAreaTotalEquipment();
	}
	
	public List<OperatorInfoExtend> getAreaTotalCharge() {
		/*Map<String, String> map = new HashMap<>();
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.HOUR_OF_DAY, 00);
		calendar.set(calendar.MINUTE, 00);
		calendar.set(calendar.SECOND, 00);
		map.put("date1", df.format(calendar.getTime()));
		map.put("date2", df.format(date));*/
		return operatorInfoDao.getAreaTotalCharge();
	}
}
 
