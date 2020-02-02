package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_WRONG_PARAM;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.cpit.common.JsonUtil;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.system.PowerMgmt;
import com.cpit.cpmt.biz.impl.system.UserMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.system.User;
import com.cpit.cpmt.dto.system.UserAreaKey;
import com.cpit.cpmt.dto.system.UserOperatorKey;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping(value="/system")
public class UserController {
	
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserMgmt userMgmt;
	
	@Autowired
	private PowerMgmt powerMgmt;
	
	//用户信息查询
	@GetMapping(value = "/getUserById")
	public Object getUserById(@RequestParam(name="userId")String userId){
		try{
			return userMgmt.getUser(userId);
		}catch(Exception e){
			logger.error("getUserById error:" , e);
			return null;
		}
	}
	
	
	//添加用户
	@PostMapping(value = "/addUser")
	public Object addUser(
			@RequestBody User user
		){
		logger.debug("addUser begin,param:" + user);
		try {
			User result = userMgmt.getUserByLoginName(user.getAccount());
			if(result !=null){
				return new ResultInfo(FAIL,new ErrorMsg(ERR_WRONG_PARAM,"账号重复"));
			}
			userMgmt.add(user);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("addUser error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}	
		
	}
		
	//修改用户
	@PutMapping(value = "/updateUser")
	public Object updateUser(@RequestBody User user){
		logger.debug("updateUser begin,param:" + user);
		try {
			User oldUser = userMgmt.getUser(user.getId()); //清理缓存
			if(oldUser != null){
				userMgmt.delUserCacheById(oldUser.getId());
				userMgmt.delUserCacheByLoginName(oldUser.getAccount());
			}			
			
			userMgmt.update(user);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("updateUser error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	
	
	//删除用户,打上删除标志
	@DeleteMapping(value = "/mkUserDeleted")
	public Object mkUserDeleted(
			@RequestParam(name="userId")String userId,
			@RequestParam(name="deleteBy")String deletedBy
			){
		logger.debug("mkUserDeleted begin,param:" + userId);
		try {
			User oldUser = userMgmt.getUser(userId); //清理缓存
			if(oldUser != null){
				userMgmt.delUserCacheById(oldUser.getId());
				userMgmt.delUserCacheByLoginName(oldUser.getAccount());
			}			
			oldUser.setDeleteDate(new Date());
			oldUser.setDeletedBy(deletedBy);
			oldUser.setStatus(User.STATUS_DELETED);
			userMgmt.update(oldUser);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("mkUserDeleted error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	
	//直接删除
	@DeleteMapping(value = "/delUser")
	public Object delUser(
			@RequestParam(name="userId")String userId
			){
		logger.debug("delUser begin,param:" + userId);
		try {
			User oldUser = userMgmt.getUser(userId); //清理缓存
			if(oldUser != null){
				userMgmt.delUserCacheById(oldUser.getId());
				userMgmt.delUserCacheByLoginName(oldUser.getAccount());
			}			
			userMgmt.delete(userId);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("delUser error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}

	@GetMapping(value = "/getUserByLoginName")
	public User getUserByLoginName(@RequestParam(name="loginName")String loginName) {
		logger.info("===loginName is "+loginName);
		return userMgmt.getUserByLoginName(loginName);
	}
	

	
	/**
	 * 查询用户
	 * @param account
	 * @return
	 */
	@PostMapping(value = "/selectByCondition")
	public Object selectByCondition(
		@RequestBody User condition,
		@RequestParam(name="pageNumber")int pageNumber,
		@RequestParam(name="pageSize",required=false)int pageSize
		) {
		try{
			Map<String, Serializable> map = new HashMap<String, Serializable>();
			if(pageSize == 0){
				pageSize = Page.PAGE_SIZE;
			}
			Page<User> infoList = null;
			if(pageSize == -1){ //不分页
				infoList =  userMgmt.selectByCondition(condition);
				map.put("infoList", infoList);
				map.put("total", infoList.size());
				map.put("pages", 1);
				map.put("pageNum", 1);		
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList =  userMgmt.selectByCondition(condition);
				PageHelper.endPage();
				map.put("infoList", infoList);
				map.put("total", infoList.getTotal());
				map.put("pages", infoList.getPages());
				map.put("pageNum", infoList.getPageNum());				
			}

			return new ResultInfo(OK, map);
		}catch(Exception ex){
			logger.error("selectByCondition error",ex);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,ex.getMessage()));
		}
	}	
	
	
	/**
	 * 给用户分配可管运用商
	 * @param params
	 * @return
	 */
	@PostMapping(value="changeUserOperator")
	public Object changeUserOperator(@RequestBody Map<String,Object> params){
		try {
			String userId = (String)params.get("userId");
			List<UserOperatorKey> operators = JsonUtil.mkList((List)params.get("operators"),UserOperatorKey.class);
			userMgmt.changeUserOperator(userId, operators);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("changeUserOperator error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}		
	}
	
	
	/**
	 * 给用户分配可管区域
	 * @param params
	 * @return
	 */
	@PostMapping(value="changeUserArea")
	public Object changeUserArea(@RequestBody Map<String,Object> params){
		try {
			String userId = (String)params.get("userId");
			List<UserAreaKey> areas = JsonUtil.mkList((List)params.get("areas"),UserAreaKey.class);
			userMgmt.changeUserArea(userId, areas);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("changeUserArea error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}		
	}

	/**
	 * 修改用户的角色
	 * @param params
	 * @return
	 */
	@PostMapping(value="changeUserRole")
	public Object changeUserRole(@RequestBody User user){
		try {
			//删除缓存
			powerMgmt.delCacheOfPowersOfUser(user.getId());
			User oldUser = userMgmt.getUser(user.getId()); //清理缓存
			if(oldUser != null){
				userMgmt.delUserCacheById(oldUser.getId());
				userMgmt.delUserCacheByLoginName(oldUser.getAccount());
			}
			
			userMgmt.update(user);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("changeUserRole error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}		
	}
	
	
	//注册运营商
	@PostMapping(value = "/registerOperator")
	public Object registerOperator(
			@RequestBody Map<String,Object> params
		){
		logger.debug("registerOperator begin,param:" + params);
		try {
			Object userObj = params.get("user");
			String json = JsonUtil.beanToJson(userObj);
			User user = JsonUtil.jsonToBean(json, User.class);
			List<OperatorFile> files = JsonUtil.mkList((List)params.get("files"),OperatorFile.class);
			User result = userMgmt.getUserByLoginName(user.getAccount());
			if(result !=null){
				return new ResultInfo(FAIL,new ErrorMsg(ERR_WRONG_PARAM,"账号重复"));
			}
			userMgmt.registerOperator(user,files);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("registerOperator error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}	
	}
	
	//审核运营商
	@PutMapping(value = "/checkOperator")
	public Object checkOperator(@RequestBody User user){
		logger.debug("checkOperator begin,param:" + user);
		try {
			User oldUser = userMgmt.getUser(user.getId()); //清理缓存
			if(oldUser != null){
				userMgmt.delUserCacheById(oldUser.getId());
				userMgmt.delUserCacheByLoginName(oldUser.getAccount());
			}			
			
			userMgmt.checkOperator(user);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("checkOperator error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}	
	
	
	//删除未审核通过的运营商
	@DeleteMapping(value = "/delOperator")
	public Object delOperator(
			@RequestParam(name="userId")String userId
			){
		logger.debug("delOperator begin,param:" + userId);
		try {
			User oldUser = userMgmt.getUser(userId); //清理缓存
			if(oldUser != null){
				userMgmt.delUserCacheById(oldUser.getId());
				userMgmt.delUserCacheByLoginName(oldUser.getAccount());
			}			
			userMgmt.delOperator(userId,oldUser.getOperatorId());
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("delUser error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
}
 
