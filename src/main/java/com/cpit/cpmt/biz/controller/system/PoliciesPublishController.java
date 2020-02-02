package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.system.PoliciesPublishMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.system.PoliciesPublish;

@RestController
@RequestMapping(value="/system/policiesPublish")
public class PoliciesPublishController {
	
	private final static Logger logger = LoggerFactory.getLogger(PoliciesPublishController.class);
	
	@Autowired
	private PoliciesPublishMgmt policiesPublishMgmt;
	
	//获取全部政策法规列表
	@PostMapping("/getPoliciesPublishList")
	public Object getPoliciesPublishList(int pageNumber,int pageSize,@RequestBody PoliciesPublish policiesPublish) {
		logger.debug("getPoliciesPublishList begin, pageNumber=" + pageNumber+",pageSize="+pageSize+",policiesPublish="+policiesPublish);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<OperatorInfoExtend> infoList = policiesPublishMgmt.getPoliciesPublishList(policiesPublish);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getPoliciesPublishList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询政策法规详细信息
	@GetMapping("/getPoliciesInfo")
	public Object getPoliciesInfo(Integer policyId) {
		logger.info("getPoliciesInfo begin, policyId=" + policyId);
		try {
			return new ResultInfo(ResultInfo.OK,policiesPublishMgmt.getPoliciesInfo(policyId));
		} catch (Exception ex) {
			logger.error("getPoliciesInfo error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	// 添加政策法规信息
	@PostMapping(value = "/addPolicyPublish")
	public ResultInfo addPolicyPublish(@RequestBody PoliciesPublish policiesPublish) {
		logger.debug("addPolicyPublish,begin,policiesPublish:" + policiesPublish);
		try {
			policiesPublishMgmt.addPolicyPublish(policiesPublish);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addPolicyPublish error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	// 修改政策法规信息
	@PutMapping(value = "/updatePoliciesPublish")
	public ResultInfo updatePoliciesPublish(@RequestBody PoliciesPublish policiesPublish) {
		logger.debug("updatePoliciesPublish,begin,policiesPublish:" + policiesPublish);
		try {
			policiesPublishMgmt.updatePoliciesPublish(policiesPublish);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("updatePoliciesPublish error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	//删除政策法规信息
	@DeleteMapping(value = "/delPoliciesPublish")
	public ResultInfo delPoliciesPublish(Integer policyId) {
		logger.debug("delPoliciesPublish,begin,policyId:" + policyId);
		try {
			policiesPublishMgmt.delPoliciesPublish(policyId);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("delPoliciesPublish error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	//获取审核历史
	@GetMapping(value = "/getPolicyAuditHisList")
	public Object getPolicyAuditHisList(@RequestParam(name="processId")Integer processId){
		try{
			return new ResultInfo(OK,policiesPublishMgmt.getPolicyAuditHisList(processId));
		}catch(Exception e){
			logger.error("getPolicyAuditHisList error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
}
 
