package com.cpit.cpmt.biz.controller.security.battery;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.battery.AnaFaultKnowledgebaseMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.battery.AnaBmsSingleCharge;
import com.cpit.cpmt.dto.security.battery.AnaFaultKnowledgebase;

@RestController
@RequestMapping("/security/battery")
public class AnaFaultKnowledgebaseController {
	private final static Logger logger = LoggerFactory.getLogger(AnaFaultKnowledgebaseController.class);
	
	@Autowired AnaFaultKnowledgebaseMgmt anaFaultKnowledgebaseMgmt;
	
	//故障知识库 -- 查询
	@RequestMapping("/queryAnaFaultKnowledgebase/{pageNumber}/{pageSize}")
	public ResultInfo querySecondLevelData(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody  AnaFaultKnowledgebase param) {
		logger.debug("queryAnaFaultKnowledgebase begin params [{}],pageNumber[{}],pageSize[{}]", param,pageNumber,pageSize);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AnaFaultKnowledgebase> infoList = null;
		try {
			//test start
			param.setEventName("告警事件");
			param.setWarningStatus(2);
			param.setWarningType(1);
			param.setWarningLevel(1);
			Date startTime =TimeConvertor.stringTime2Date("2020-02-05 19:29:40","yyyy-MM-dd HH:mm:ss");
			Date endTime =TimeConvertor.stringTime2Date("2020-02-08 20:33:37","yyyy-MM-dd HH:mm:ss");
			param.setStartTime(startTime);
			param.setEndTime(endTime);
			//test end
			if(pageNumber==-1){
				infoList = anaFaultKnowledgebaseMgmt.queryAnaFaultKnowledgebase(param);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = anaFaultKnowledgebaseMgmt.queryAnaFaultKnowledgebase(param);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryAnaFaultKnowledgebase total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	//故障知识库 -- 新增
	@RequestMapping("/addAnaFaultKnowledgebase")
	public ResultInfo addAnaFaultKnowledgebase(@RequestBody AnaFaultKnowledgebase param) {
		logger.debug("addAnaFaultKnowledgebase begin params [{}]", param);
		try {
			//test start
			int baseId = SequenceId.getInstance().getId("anaFaultKnowledgebaseId");
			param.setBaseId(String.valueOf(baseId));
			param.setEventName("告警事件");
			param.setWarningStatus(2);
			param.setWarningType(1);
			param.setWarningLevel(1);
			//test end
			anaFaultKnowledgebaseMgmt.addAnaFaultKnowledgebase(param);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//故障知识库 -- 修改
	@RequestMapping("/updateAnaFaultKnowledgebase")
	public ResultInfo updateAnaFaultKnowledgebase(@RequestBody AnaFaultKnowledgebase param) {
		logger.debug("updateAnaFaultKnowledgebase begin params [{}]", param);
		try {
			//test start
			int baseId = 2;
			param.setBaseId(String.valueOf(baseId));
			param.setEventName("告警事件2222");
			param.setWarningStatus(3);
			param.setWarningType(1);
			param.setWarningLevel(1);
			//test end
			anaFaultKnowledgebaseMgmt.updateAnaFaultKnowledgebase(param);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("updateAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//故障知识库 -- 删除
	@RequestMapping("/deleteAnaFaultKnowledgebase")
	public ResultInfo deleteAnaFaultKnowledgebase(@RequestBody AnaFaultKnowledgebase param) {
		logger.debug("deleteAnaFaultKnowledgebase begin params [{}]", param);
		try {
			//test start
			int baseId = 2;
			param.setBaseId(String.valueOf(baseId));
			param.setEventName("告警事件2222");
			param.setWarningStatus(3);
			param.setWarningType(1);
			param.setWarningLevel(1);
			//test end
			anaFaultKnowledgebaseMgmt.deleteAnaFaultKnowledgebase(param);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("deleteAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
}
