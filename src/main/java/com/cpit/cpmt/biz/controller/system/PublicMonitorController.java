package com.cpit.cpmt.biz.controller.system;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.controller.exchange.operator.StationInfoController;
import com.cpit.cpmt.biz.impl.system.PublicMonitorMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.system.PublicMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@RestController
@RequestMapping("/public")
public class PublicMonitorController {
    private final static Logger logger = LoggerFactory.getLogger(PublicMonitorController.class);

    @Autowired
    private PublicMonitorMgmt publicMonitorMgmt;

    @PostMapping("/selectPublicMonitorByPage")
    public ResultInfo selectPublicMonitorByPage(@RequestBody PublicMonitor publicMonitor,
                                                @RequestParam(name="pageNumber")int pageNumber,
                                                @RequestParam(name="pageSize",required=false)int pageSize){
        logger.debug("page publicMonitor info:"+publicMonitor+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<PublicMonitor> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = publicMonitorMgmt.selectPublicMonitorByPage(publicMonitor);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = publicMonitorMgmt.selectPublicMonitorByPage(publicMonitor);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectAlarmInfoByStation error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectPublicMonitorByPeimaryKey")
    public Object selectPublicMonitorByPeimaryKey(@RequestParam(name = "id") String id){
        try {
            return new ResultInfo(OK, publicMonitorMgmt.selectByPeimaryKey(id));
        } catch (Exception e) {
            logger.error("selectStationById error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/addPublicMonitor")
    public Object addPublicMonitor(@RequestBody PublicMonitor publicMonitor){
        logger.debug("add publicMonitor info:"+publicMonitor);
        try {
            publicMonitorMgmt.insertSelective(publicMonitor);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("addPublicMonitor error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PutMapping("/updateSelective")
    public ResultInfo updateSelective(@RequestBody PublicMonitor publicMonitor){
        try {
            publicMonitorMgmt.updateSelective(publicMonitor);
            return  new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updatepublicMonitor error",e);
            return  new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }
}
