package com.cpit.cpmt.biz.controller.exchange.operator;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.HistoryInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.*;
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
@RequestMapping("/exchange/operator/")
public class HistoryInfoController {
    private final static Logger logger = LoggerFactory.getLogger(HistoryInfoController.class);

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    @Autowired
    private ConnectorMgmt connectorMgmt;

    @GetMapping("/getConnectorList")
    public Object getConnectorList(@RequestParam(name = "equipmentId") String equipmentId,@RequestParam(name = "operatorId") String operatorId){
        try {
            return new ResultInfo(OK, connectorMgmt.getConnectorList(equipmentId, operatorId));
        } catch (Exception e) {
            logger.error("getConnectorList error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/queryBmsHistoryList")
    public Object queryBmsHistoryList(@RequestBody EquipmentHistoryInfo equipment){
        try {
            return new ResultInfo(OK, historyInfoMgmt.queryBmsHistoryList(equipment));
        } catch (Exception e) {
            logger.error("queryBmsHistoryList error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/selectConnectorHis")
    public Object selectConnectorHis(@RequestBody EquipmentHistoryInfo equipment){
        try {
            return new ResultInfo(OK, historyInfoMgmt.selectConnectorHis(equipment));
        } catch (Exception e) {
            logger.error("selectConnectorHis error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/selectStationHistory")
    public Object selectStationHistory(@RequestBody StationHistoryInfo stationHistoryInfo,
                                       @RequestParam(name="pageNumber")int pageNumber,
                                       @RequestParam(name="pageSize",required=false)int pageSize){
        logger.debug("selectStationHistory info:"+stationHistoryInfo);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<StationHistoryInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = historyInfoMgmt.selectStationHistory(stationHistoryInfo);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = historyInfoMgmt.selectStationHistory(stationHistoryInfo);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception e) {
            logger.error("selectStationHistory error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }



    @PostMapping("/selectEquipmentHistory")
    public Object selectEquipmentHistory(@RequestBody EquipmentHistoryInfo equipmentHistoryInfo,
                                       @RequestParam(name="pageNumber")int pageNumber,
                                       @RequestParam(name="pageSize",required=false)int pageSize){
        logger.debug("selectEquipmentHistory info:"+equipmentHistoryInfo);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<EquipmentHistoryInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = historyInfoMgmt.selectEquipmentHistoryInfo(equipmentHistoryInfo);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = historyInfoMgmt.selectEquipmentHistoryInfo(equipmentHistoryInfo);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception e) {
            logger.error("selectEquipmentHistory error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/selectDisEquipmentHistory")
    public Object selectDisEquipmentHistory(@RequestBody DisEquipmentHistoryInfo disEquipmentHistoryInfo,
                                         @RequestParam(name="pageNumber")int pageNumber,
                                         @RequestParam(name="pageSize",required=false)int pageSize){
        logger.debug("selectDisEquipmentHistory info:"+disEquipmentHistoryInfo);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<DisEquipmentHistoryInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = historyInfoMgmt.selectDisEquipmentHistory(disEquipmentHistoryInfo);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = historyInfoMgmt.selectDisEquipmentHistory(disEquipmentHistoryInfo);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception e) {
            logger.error("selectDisEquipmentHistory error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/selectChargeFileHistory")
    public Object selectChargeFileHistory(@RequestBody ChargeFileHistory chargeFileHistory,
                                            @RequestParam(name="pageNumber")int pageNumber,
                                            @RequestParam(name="pageSize",required=false)int pageSize){
        logger.debug("selectChargeFileHistory info:"+chargeFileHistory);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<ChargeFileHistory> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = historyInfoMgmt.selectChargeFileHistory(chargeFileHistory);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = historyInfoMgmt.selectChargeFileHistory(chargeFileHistory);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception e) {
            logger.error("selectChargeFileHistory error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }
}
