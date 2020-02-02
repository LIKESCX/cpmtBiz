package com.cpit.cpmt.biz.controller.security;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.RiskControlMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.RiskControl;

@RestController
@RequestMapping(value = "/security")
public class RiskControlController {

	private final static Logger logger = LoggerFactory.getLogger(RiskControlController.class);
	
	@Autowired
	private RiskControlMgmt riskControlMgmt;

	//添加风险管控
	@PostMapping(value = "/addRiskControl")
	public ResultInfo addRiskControl(@RequestBody RiskControl RiskControl) {
		logger.debug("addRiskControl,begin,RiskControl:" + RiskControl);
		try {
			riskControlMgmt.addRiskControl(RiskControl);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addRiskControl error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}

	}
	
	//获取风险管控列表
	@PostMapping(value = "/getRiskControlList")
	@ResponseBody
	public Object getRiskControlList(int pageNumber,int pageSize,@RequestBody RiskControl RiskControl) {
		logger.debug("getRiskControlList begin, pageNumber=" + pageNumber+",pageSize="+pageSize+",RiskControl is :"+RiskControl);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<RiskControl> infoList = riskControlMgmt.getRiskControlList(RiskControl);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getRiskControlList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//修改风险管控
	@PutMapping(value = "/updateRiskControl")
	public ResultInfo updateRiskControl(@RequestBody RiskControl RiskControl) {
		logger.debug("updateRiskControl,begin,param:" + RiskControl);
		try {
			riskControlMgmt.updateRiskControl(RiskControl);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("updateRiskControl error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	//删除风险管控
	@DeleteMapping(value = "/delRiskControl")
	public ResultInfo delRiskControl(Integer riskId) {
		logger.info("delRiskControl,begin,riskId:" + riskId);
		try {
			riskControlMgmt.delRiskControl(riskId);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("delRiskControl error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	@GetMapping("/getCountByLevelAndType")
	public Object getCountByLevelAndType() {
		logger.info("getCountByLevelAndType begin");
		try {
			return new ResultInfo(ResultInfo.OK,riskControlMgmt.getCountByLevelAndType());
		} catch (Exception ex) {
			logger.error("getCountByLevel error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
}
