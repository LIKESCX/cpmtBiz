package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorHistoryPowerInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.DisEquipmentInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.*;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorHistoryPowerInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;
import com.cpit.cpmt.dto.exchange.operator.*;
import com.cpit.cpmt.dto.system.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class HistoryInfoMgmt {
    @Autowired
    private StationHistoryInfoDAO stationHistoryInfoDAO;

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private  EquipmentInfoDAO equipmentInfoDAO;

    @Autowired
    private DisEquipmentInfoDao disEquipmentInfoDao;

    @Autowired
    private EquipmentHistoryInfoDAO equipmentHistoryInfoDAO;

    @Autowired
    private DisEquipmentHistoryInfoDAO disEquipmentHistoryInfoDAO;

    @Autowired
    private ChargeFileHistoryDAO chargeFileHistoryDAO;

    @Autowired
    private OperatorChangeHisDao operatorChangeHisDao;

    @Autowired
    private BmsInfoDao bmsInfoDao;

    @Autowired
    private ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;

    public List<BmsInfo> queryBmsHistoryList(EquipmentHistoryInfo equipment){
        return bmsInfoDao.queryBmsHistoryList(equipment);
    }

    public List<ConnectorHistoryPowerInfo> selectConnectorHis(EquipmentHistoryInfo equipmentHistoryInfo){
        return connectorHistoryPowerInfoDao.selectConnectorHis(equipmentHistoryInfo);
    }

    //充电站历史信息
    @Transactional
    public void insertStationHisInfo(StationInfoShow stationInfo){
        Integer operateType = stationInfo.getOperateType();
        StationInfoShow stationInfoShow = stationInfoDAO.selectByStationId(stationInfo.getStationID(), stationInfo.getOperatorID());
        StationHistoryInfo stationHistoryInfo = new StationHistoryInfo();
        BeanUtils.copyProperties(stationInfoShow,stationHistoryInfo);
        stationHistoryInfo.setSID(String.format("%06d", SequenceId.getInstance().getId("cpmtStationHistoryId")));
        User user = stationInfo.getUser();
        if(user!=null){
            stationHistoryInfo.setUserID(user.getId());
            stationHistoryInfo.setUserName(user.getName());
            if("0".equals(user.getId())){
                stationHistoryInfo.setChangeMethod(1);//地标
            }else{
                stationHistoryInfo.setChangeMethod(2);//界面
            }
        }
        stationInfo.setOperateType(operateType);
        stationHistoryInfo.setCheckDate(new Date());
        stationHistoryInfo.setOperate(stationInfo.getRemark()!=null?stationInfo.getRemark():null);
        //获取运营商历史表里此运营商最新一条
        OperatorChangeHis lastedOperator = operatorChangeHisDao.getLastedOperatorChangeHis(stationInfo.getOperatorID());
        stationHistoryInfo.setOperatorHisID(lastedOperator!=null?lastedOperator.getChangeId():null);
        stationHistoryInfoDAO.insertSelective(stationHistoryInfo);
    }

    public Page<StationHistoryInfo> selectStationHistory(StationHistoryInfo stationHistoryInfo){
        return stationHistoryInfoDAO.selectStationHistory(stationHistoryInfo);
    }

    //充电设备历史信息
    @Transactional
    public void insertEquipmentHisInfo(EquipmentInfoShow equipmentInfo){
        Integer operateType = equipmentInfo.getOperateType();
        EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByEquipId(equipmentInfo.getEquipmentID(), equipmentInfo.getOperatorID());
        EquipmentHistoryInfo equipmentHistoryInfo = new EquipmentHistoryInfo();
        BeanUtils.copyProperties(equipmentInfoShow,equipmentHistoryInfo);
        equipmentHistoryInfo.setEID(String.format("%06d", SequenceId.getInstance().getId("cpmtEquipmentHistoryId")));
        User user = equipmentInfo.getUser();
        if(user!=null){
            equipmentHistoryInfo.setUserID(user.getId());
            equipmentHistoryInfo.setUserName(user.getName());
            if("0".equals(user.getId())){
                equipmentHistoryInfo.setChangeMethod(1);//地标
            }else{
                equipmentHistoryInfo.setChangeMethod(2);//界面
            }
        }
        equipmentHistoryInfo.setOperateType(operateType);
        equipmentHistoryInfo.setCheckDate(new Date());
        equipmentHistoryInfo.setOperate(equipmentInfo.getNote()!=null?equipmentInfo.getNote():null);

        //获取运营商历史表里此运营商最新一条
        OperatorChangeHis lastedOperator = operatorChangeHisDao.getLastedOperatorChangeHis(equipmentInfo.getOperatorID());
        equipmentHistoryInfo.setOperatorHisID(lastedOperator!=null?lastedOperator.getChangeId():null);

        //充电站历史信息最新一条
        StationHistoryInfo stationHistoryInfo = stationHistoryInfoDAO.selectStationHisNewestOne(equipmentInfo.getStationId(), equipmentInfo.getOperatorID());
        equipmentHistoryInfo.setHisSID(stationHistoryInfo!=null?stationHistoryInfo.getSID():null);
        equipmentHistoryInfoDAO.insertSelective(equipmentHistoryInfo);
    }

    public Page<EquipmentHistoryInfo> selectEquipmentHistoryInfo(EquipmentHistoryInfo record){
        return equipmentHistoryInfoDAO.selectEquipmentHistoryInfo(record);
    }

    //配电设备历史信息
    @Transactional
    public void insertDisEquipmentHisInfo(DisEquipmentInfo disEquipmentInfo){
        DisEquipmentInfo disEquipment = disEquipmentInfoDao.selectByDisEquipmentId(disEquipmentInfo.getDisequipmentID(), disEquipmentInfo.getOperatorID());
        DisEquipmentHistoryInfo disEquipmentHistoryInfo = new DisEquipmentHistoryInfo();
        BeanUtils.copyProperties(disEquipment,disEquipmentHistoryInfo);
        disEquipmentHistoryInfo.setId(SequenceId.getInstance().getId("excDisEquipmentInfoHistoryId"));
        User user = disEquipmentInfo.getUser();
        disEquipmentHistoryInfo.setUserID(user!=null?user.getId():null);
        disEquipmentHistoryInfo.setUserName(user!=null?user.getName():null);
        disEquipmentHistoryInfo.setCheckDate(new Date());
        disEquipmentHistoryInfo.setOperate(disEquipmentHistoryInfo.getNote()!=null?disEquipmentHistoryInfo.getNote():null);

        //充电站历史信息最新一条
        StationHistoryInfo stationHistoryInfo = stationHistoryInfoDAO.selectStationHisNewestOne(disEquipmentInfo.getStationID(), disEquipmentInfo.getOperatorID());
        disEquipmentHistoryInfo.setHisSID(stationHistoryInfo!=null?stationHistoryInfo.getSID():null);
        disEquipmentHistoryInfoDAO.insertSelective(disEquipmentHistoryInfo);
    }

    public Page<DisEquipmentHistoryInfo> selectDisEquipmentHistory(DisEquipmentHistoryInfo record){
        return disEquipmentHistoryInfoDAO.selectDisEquipmentHistory(record);
    }

    //上传文件历史信息
    @Transactional
    public void insertChargeFileHisInfo(ChargeFile chargeFile){
        ChargeFileHistory chargeFileHistory = new ChargeFileHistory();
        BeanUtils.copyProperties(chargeFile,chargeFileHistory);
        chargeFileHistory.setFileId(SequenceId.getInstance().getId("excChargeFileHistoryId"));
        User user = chargeFile.getUser();
        chargeFileHistory.setUserId(user.getId());
        chargeFileHistory.setUserName(user.getName());
        chargeFileHistory.setOperate("添加附件");
        chargeFileHistory.setCheckDate(new Date());


        if(StringUtils.isNotEmpty(chargeFile.getOperatorId())){
            OperatorChangeHis lastedOperator = operatorChangeHisDao.getLastedOperatorChangeHis(chargeFile.getOperatorId());
            chargeFileHistory.setOperatorHisId(lastedOperator!=null?lastedOperator.getChangeId():null);
            //充电站附件
            if(StringUtils.isNotEmpty(chargeFile.getStationId())){
                //充电站历史信息最新一条
                StationHistoryInfo stationHistoryInfo = stationHistoryInfoDAO.selectStationHisNewestOne(chargeFile.getStationId(), chargeFile.getOperatorId());
                chargeFileHistory.setHisSid(stationHistoryInfo!=null?stationHistoryInfo.getSID():null);
            }else if(StringUtils.isNotEmpty(chargeFile.getEquipmentId())){ //充电设备附件
                EquipmentHistoryInfo equipmentHistoryInfo = equipmentHistoryInfoDAO.selectEquNewestOne(chargeFile.getEquipmentId(), chargeFile.getOperatorId());
                chargeFileHistory.setHisEid(equipmentHistoryInfo!=null?equipmentHistoryInfo.getEID():null);
            }
        }
        chargeFileHistoryDAO.insertSelective(chargeFileHistory);
    }

    public Page<ChargeFileHistory> selectChargeFileHistory(ChargeFileHistory record){
        return chargeFileHistoryDAO.selectChargeFileHistory(record);
    }
}
