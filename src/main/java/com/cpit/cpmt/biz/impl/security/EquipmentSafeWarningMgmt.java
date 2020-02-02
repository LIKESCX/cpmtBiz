package com.cpit.cpmt.biz.impl.security;

import com.alibaba.fastjson.JSONArray;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.security.EquipmentSafeWarningDao;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.security.EquipmentSafeWarning;
import com.cpit.cpmt.dto.security.MessageRemind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @author : xuqingxun
 * @className: EquipmentSafeWarningMgmt
 * @description: 充电设施安全预警服务类
 * @time: 2019/11/295:05 下午
 */

@Service
public class EquipmentSafeWarningMgmt {

    private final static Logger logger = LoggerFactory.getLogger(EquipmentSafeWarningMgmt.class);

    @Autowired
    EquipmentSafeWarningDao equipmentSafeWarningDao;

    @Autowired
    MessageMgmt messageMgmt;


    public void addEquipmentSafeWarning(EquipmentSafeWarning equipmentSafeWarning) {
        Integer warningId = SequenceId.getInstance().getId("warningId");
        if (warningId != null) {
            equipmentSafeWarning.setWarningId(warningId);
        }
        equipmentSafeWarningDao.insertSelective(equipmentSafeWarning);
    }

    public EquipmentSafeWarning getEquipmentSafeWarningByWarningId(Integer warningId) {
        return equipmentSafeWarningDao.selectByPrimaryKey(warningId);
    }


    public void sendESWSms(Integer warningId) throws Exception {
        logger.info("sendESWSms,warningId=", warningId);
        EquipmentSafeWarning equipmentSafeWarning = equipmentSafeWarningDao.selectByPrimaryKey(warningId);
        String smsReceiver = equipmentSafeWarning.getSmsReceiver();
        JSONArray jsonArray = JSONArray.parseArray(smsReceiver);
//        XX（运营商名称）的XX（充电站名称）的运行风险评估结果：XX，或安全隐患排查情况：XX，特此发出预警，预警级别：XX，请采取相应措施，避免事故发生。

        String message = equipmentSafeWarning.getOperatorName() + "的" + equipmentSafeWarning.getStationName() + (equipmentSafeWarning.getRiskAssessmentResult() != null && !equipmentSafeWarning.getRiskAssessmentResult().isEmpty() ? "的运行风险评估结果：" + equipmentSafeWarning.getRiskAssessmentResult() : "") + (equipmentSafeWarning.getScreeningResult() != null && !equipmentSafeWarning.getScreeningResult().isEmpty() ? "或安全隐患排查情况" + equipmentSafeWarning.getScreeningResult() : "") + "，特此发出预警，预警级别" + equipmentSafeWarning.getWarningLevel() + "，请采取相应措施，避免事故发生。";
        List<MessageRemind> messageReminds = JsonUtil.mkList(jsonArray, MessageRemind.class);
    if(messageReminds!=null&&!messageReminds.isEmpty()){
        for (MessageRemind messageRemind : messageReminds) {
            ExcMessage excMessage = new ExcMessage();
            excMessage.setSubContent(message);
            excMessage.setSmsType(ExcMessage.TYPE_CHECK_ESW);
            excMessage.setPhoneNumber(messageRemind.getPhoneNumber());
            messageMgmt.sendMessage(excMessage);
        }
        equipmentSafeWarning.setSendTime(new Date());
        equipmentSafeWarning.setSendSmsStatus(1);
        equipmentSafeWarningDao.updateByPrimaryKeySelective(equipmentSafeWarning);
    }


    }

    public void deleteEquipmentSafeWarningByWarningId(Integer warningId) {
        logger.info("deleteESWByWarningId,warningId=", warningId);
        equipmentSafeWarningDao.deleteByPrimaryKey(warningId);
    }

    public List<EquipmentSafeWarning> getEquipmentSafeWarningList(EquipmentSafeWarning equipmentSafeWarning) {
        List<EquipmentSafeWarning> list = equipmentSafeWarningDao.getEquipmentSafeWarningListByEquipmentSafeWarning(equipmentSafeWarning);
        return list;
    }


    public void updateEquipmentSafeWarning(EquipmentSafeWarning equipmentSafeWarning) {
        equipmentSafeWarningDao.updateByPrimaryKeySelective(equipmentSafeWarning);
    }


}