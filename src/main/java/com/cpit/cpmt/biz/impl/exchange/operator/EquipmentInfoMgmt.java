package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.impl.exchange.basic.StationChargeStatsMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.StationDischargeStatsMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.StationStatusInfoMgmt;
import com.cpit.cpmt.dto.exchange.basic.*;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RefreshScope
public class EquipmentInfoMgmt {
    private final static Logger logger = LoggerFactory.getLogger(EquipmentInfoMgmt.class);
    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private StationInfoMgmt stationInfoMgmt;

    @Autowired
    private StationStatusInfoMgmt stationStatusInfoMgmt;

    @Autowired
    private StationChargeStatsMgmt stationChargeStatsMgmt;

    @Autowired
    private StationDischargeStatsMgmt stationDischargeStatsMgmt;

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    @Value("${start.time.for.query.charge}")
    private String startTime;


    public Page<EquipmentInfoShow> selectEquipmentByCondition(EquipmentInfoShow equipmentInfo){
        return equipmentInfoDAO.selectEquipmentByCondition(equipmentInfo);
    }

    /*根据主键查询充电设备信息*/
    @Cacheable(cacheNames="equipment-id",key="#root.caches[0].name+#equipmentId+'-'+#operatorId",unless="#result == null")
    public EquipmentInfoShow selectByPrimaryKey(String equipmentId,String operatorId){
        return equipmentInfoDAO.selectByEquipId(equipmentId, operatorId);
    }

    //单双枪充电桩数量
    public  Map<String, Object> selectEquipmentNumList(String stationId, String operatorId){
        Map<String, Object> map = new HashMap<>();
        List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentNumList(stationId, operatorId);
        if (equipmentInfoShows==null){
            map.put("equipmentList",null);
            return map;
        }
        double totalPower=0.00;
        Integer totalChargeNum=0;
        Integer totalGunNum=0;
        for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
            Integer gunSum = equipmentInfoShow.getGunSum();
            Double equipmentPower1 = equipmentInfoShow.getEquipmentPower();
            Double equipmentPower = equipmentPower1!=null?equipmentPower1:0.0;
            Integer numbers = equipmentInfoShow.getNumbers();
            double v = equipmentPower * (numbers!=null?numbers:0);
            totalGunNum+=gunSum!=null?gunSum:0;//枪数
            totalChargeNum+=numbers!=null?numbers:0;//总桩数
            totalPower+=v;//总功率
        }
        DecimalFormat   df   =new DecimalFormat("#,##0.00");
        map.put("equipmentList",equipmentInfoShows);
        map.put("totalGunNum",totalGunNum);
        map.put("totalChargeNum",totalChargeNum);
        map.put("totalPower",df.format(totalPower));
        return map;
    }


    //查询单个充电设备ABC相位电流电压
    public Map<String,Object> selectABCVolAndEletic(String equipmentId,String operatorId){
        Map<String,Object> map = new HashMap<>();
        Map<String, Object> stringDoubleMap = queryEquipPowerAndEnergy(null, equipmentId, operatorId);
        if(stringDoubleMap == null){
            logger.error("ABCVolAndEletic queryEquipPowerAndEnergy no result");
            map.put("error","no result");
            return  map;
        }
        List<ConnectorStatusInfo> connectorStatusInfoList = (List<ConnectorStatusInfo>) stringDoubleMap.get("connectorStatusInfoList");
        double CurrentA=0.0;
        double CurrentB=0.0;
        double CurrentC=0.0;
        double VoltageA=0.0;
        double VoltageB=0.0;
        double VoltageC=0.0;
        for (ConnectorStatusInfo connectorStatusInfo : connectorStatusInfoList) {
            VoltageA+=Double.parseDouble(connectorStatusInfo.getVoltageA());
            VoltageB+=Double.parseDouble(connectorStatusInfo.getVoltageB());
            VoltageC+=Double.parseDouble(connectorStatusInfo.getVoltageC());
            CurrentA+=Double.parseDouble(connectorStatusInfo.getCurrentA());
            CurrentB+=Double.parseDouble(connectorStatusInfo.getCurrentB());
            CurrentC+=Double.parseDouble(connectorStatusInfo.getCurrentC());
        }
        map.put("CurrentA",CurrentA);
        map.put("CurrentB",CurrentB);
        map.put("CurrentC",CurrentC);
        map.put("VoltageA",VoltageA);
        map.put("VoltageB",VoltageB);
        map.put("VoltageC",VoltageC);
        map.put("hmsDate",new SimpleDateFormat("HH:mm:ss").format(new Date()));//时分秒数据
        return  map;
    }

    //动态信息
    public EquipmentInfoShow selectDynamicByPrimaryKey(String equipmentId,String operatorId){
        EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByPrimaryKey(equipmentId, operatorId);
        Map<String, String> stringStringMap = queryChargeEnergySum(null, equipmentId, operatorId);
        Map<String, Object> stringDoubleMap = queryEquipPowerAndEnergy(null, equipmentId, operatorId);
        if(stringDoubleMap==null){
            return equipmentInfoShow;
        }
//        equipmentInfoShow.setRealTimePower(stringDoubleMap.get("chargePowerSingle").toString());//实时功率
        equipmentInfoShow.setRealTimePower("200");
        equipmentInfoShow.setChargeElecticSum(stringStringMap.get("equipmentCharge"));//设备累计充电量
        equipmentInfoShow.setDisChargeEleticsSum(stringStringMap.get("equipmentDisCharge"));//设备累计放电量
        equipmentInfoShow.setChargeElecticByMonth("3000");//当月充电量
        equipmentInfoShow.setChargTimes(34);//累计充电次数
        equipmentInfoShow.setUseRate("45%");//使用率
        equipmentInfoShow.setErrorRate("15%");//故障率
        equipmentInfoShow.setSelectCheck("100");//抽查检测
        equipmentInfoShow.setPassNum("99");//通过数量

        List<ConnectorInfoShow> connectorInfos = equipmentInfoShow.getConnectorShowInfos();
        if(connectorInfos!=null){
            for (ConnectorInfoShow connectorInfo : connectorInfos) {
                connectorInfo.setConnectorChargeSum(stringStringMap.get("connectorCharge"));//接口累计充电量
                connectorInfo.setConnectorDisChargeSum(stringStringMap.get("connectorDisCharge"));//接口累计放电量

                ConnectorStatusInfo connectorStatusInfo = connectorInfo.getConnectorStatusInfo();
                if(connectorStatusInfo!=null){
                    connectorStatusInfo.setChargeElectricity(stringDoubleMap.get("chargeElectEnergy").toString());//充电电能
                    connectorStatusInfo.setDischargeElectricity(stringDoubleMap.get("disChargeElectEnergy").toString());//放电电能
                }
            }
        }

        return equipmentInfoShow;
    }

    /*添加充电设备信息*/
    @Transactional
    public void addEquipmentInfo(EquipmentInfoShow equipmentInfo){
        equipmentInfo.setEid(SequenceId.getInstance().getId("cpmtEquipmentId","",6));
        equipmentInfo.setInDate(new Date());
        equipmentInfo.setAllowanceStatus("0");
        equipmentInfoDAO.insertSelective(equipmentInfo);

        //添加充电设备历史记录
        equipmentInfo.setOperateType(1);//新增
         historyInfoMgmt.insertEquipmentHisInfo(equipmentInfo);

         //更新充电站的总桩数chargeSum
        String operatorID = equipmentInfo.getOperatorID();
        String stationId = equipmentInfo.getStationId();
        if(StringUtils.isNotEmpty(stationId)&&StringUtils.isNotEmpty(operatorID)){
            List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentList(stationId, operatorID);
            if(equipmentInfoShows!=null){
                StationInfoShow station = new StationInfoShow();
                station.setChargeSum(equipmentInfoShows.size());
                station.setStationID(stationId);
                station.setOperatorID(operatorID);
                stationInfoMgmt.updateStationInfo(station);
            }
        }
    }

    /*更新充电设备信息*/
    @Transactional
    @CacheEvict(cacheNames="equipment-id",key="#root.caches[0].name+#equipmentInfo.equipmentID+'-'+#equipmentInfo.operatorID")
    public void updateEquipmentInfo(EquipmentInfoShow equipmentInfo){
        //logger.error("update equipmentId:"+equipmentInfo.getEquipmentID()+", operatorId:"+equipmentInfo.getOperatorID());
        equipmentInfo.setInDate(new Date());
        equipmentInfoDAO.updateByPrimaryKeySelective(equipmentInfo);

        //添加充电设备历史记录
        equipmentInfo.setOperateType(2);//变更
        historyInfoMgmt.insertEquipmentHisInfo(equipmentInfo);

        /*//更新充电站的总桩数chargeSum
        String operatorID = equipmentInfo.getOperatorID();
        String stationId = equipmentInfo.getStationId();
        if(StringUtils.isNotEmpty(stationId)&&StringUtils.isNotEmpty(operatorID)){
            List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentList(stationId, operatorID);
            if(equipmentInfoShows!=null){
                StationInfoShow station = new StationInfoShow();
                station.setChargeSum(equipmentInfoShows.size());
                station.setStationID(stationId);
                station.setOperatorID(operatorID);
                stationInfoMgmt.updateStationInfo(station);
            }
        }*/
    }
    

    /*scx接口 start*/
    /*获取累计充放电量*/
    public Map<String, String> queryChargeEnergySum(String stationID,String equipemntID,String operatorID){
        Map<String, String> result = new HashMap<>();
        //1.累计充电信息
        if(StringUtils.isEmpty(stationID)){
            EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByEquipId(equipemntID, operatorID);
            stationID=equipmentInfoShow.getStationId();
        }

        StationChargeStatsInfo stationCharge =null;
        try {
            stationCharge = stationChargeStatsMgmt.queryStationChargeStats(stationID,operatorID, startTime ,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } catch (Exception e) {
            logger.error("scx queryStationChargeStats error", e);
        }
        if(stationCharge!=null){
            result.put("stationCharge",stationCharge.getStationChargeElectricity());//站累计充电
        List<EquipmentChargeStatsInfo> equipmentChargeStatsInfos = stationCharge.getEquipmentChargeStatsInfos();
            for (EquipmentChargeStatsInfo equipmentChargeStats : equipmentChargeStatsInfos) {
                String equipmentId = equipmentChargeStats.getEquipmentID();
                if(StringUtils.isEmpty(stationID) && StringUtils.isNotEmpty(equipmentId) ){
                    if(equipmentId.equals(equipemntID)){
                        result.put("equipmentCharge",equipmentChargeStats.getEquipmentChargeElectricity());//桩累计充电
                        List<ConnectorChargeStatsInfo> connectorChargeStatsInfos = equipmentChargeStats.getConnectorChargeStatsInfos();
                        Double connectorCharge = 0.00;//接口累计充电
                        for (ConnectorChargeStatsInfo connectorChargeStats : connectorChargeStatsInfos) {
                            connectorCharge+=Double.parseDouble(connectorChargeStats.getConnectorChargeElectricity());
                        }
                        DecimalFormat   df   =new DecimalFormat("#,##0.00");
                        result.put("connectorCharge",df.format(connectorCharge));//接口累计充电
                    }else {
                        break;
                    }

                }else {
                    break;
                }

            }
        }else{
            result.put("error","无法获取此充电站累计充电量");
        }

        StationDischargeStatsInfo stationDischarge =null;
        //2.累计放电信息
        try {
            stationDischarge = stationDischargeStatsMgmt.queryStationDischargeStats(stationID, operatorID,startTime, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } catch (Exception e) {
            logger.error("scx queryStationDischargeStats error", e);
        }
        if(stationDischarge!=null) {
            result.put("stationDisCharge", stationDischarge.getStationDischargeElectricity());//站累计放电
            List<EquipmentDischargeStatsInfo> equipmentDischarges = stationDischarge.getEquipmentDischargeStatsInfos();
            for (EquipmentDischargeStatsInfo equipmentDischarge : equipmentDischarges) {
                String equipmentDischargeId = equipmentDischarge.getEquipmentID();
                if(StringUtils.isEmpty(stationID) && StringUtils.isNotEmpty(equipmentDischargeId) ) {
                    if (equipmentDischargeId.equals(equipemntID)) {
                        result.put("equipmentDisCharge",equipmentDischarge.getEquipmentDischargeElectricity());//桩累计放电
                        List<ConnectorDischargeStatsInfo> connectorDischarges = equipmentDischarge.getConnectorDischargeStatsInfos();
                        Double connectorDisCharge = 0.00;//接口累计放电
                        for (ConnectorDischargeStatsInfo connectorDischarge : connectorDischarges) {
                            connectorDisCharge+=Double.parseDouble(connectorDischarge.getConnectorDischargeElectricity());
                        }
                        result.put("connectorDisCharge",new DecimalFormat("#,##0.00").format(connectorDisCharge));
                    }else {
                        break;
                    }
                }else {
                    break;
                }
            }
        }else {
            result.put("error","无法获取此充电站累计放电量");
        }

        return result;

    }

    /*获取设备电能，实时功率*/
    public Map<String, Object> queryEquipPowerAndEnergy(String stationId,String equipmentId,String operatorId){
        Map<String, Object> result = new HashMap<>();
        if(StringUtils.isEmpty(stationId)){
            EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByEquipId(equipmentId, operatorId);
            stationId=equipmentInfoShow.getStationId();
        }
        String[] stationIds={stationId};
        List<StationStatusInfo> stationStatusInfos = null;
        try {
            stationStatusInfos = stationStatusInfoMgmt.queryStationsStatus(stationIds,operatorId);
        } catch (Exception e) {
            logger.error("scx queryStationsStatus error", e);
        }
        if(stationStatusInfos==null) {
            return null;
        }
        List<ConnectorStatusInfo> connectorStatusInfoList = new ArrayList<>();
        for (StationStatusInfo stationStatusInfo : stationStatusInfos) {
            //校验是否为传入参数的stationid (如果可以传入接口就无需判断)
            //if(stationStatusInfo.getStationId())
            Map<String, List<Double>> map = new HashMap<>();//key:设备id , value:单个设备接口功率集合
            Map<String, List<Double>> map2 = new HashMap<>();//key:设备id , value:单个设备接口充电电能集合
            Map<String, List<Double>> map3 = new HashMap<>();//key:设备id , value:单个设备接口放电电能集合
            List<Double> newList = new ArrayList<>();
            List<Double> newList2 = new ArrayList<>();
            List<Double> newList3 = new ArrayList<>();
            List<ConnectorStatusInfo> connectorStatusInfos = stationStatusInfo.getConnectorStatusInfos();
            for (ConnectorStatusInfo connectorStatus : connectorStatusInfos) {//设备接口状态对象
                String connectorID = connectorStatus.getConnectorID();//接口id
                String chargeId=connectorID.substring(0,connectorID.length()-3);//设备id
                //System.out.println("设备id:"+chargeId);
                //1.匹配一个站下所有设备
                if(StringUtils.isNotEmpty(stationId)){
                    //单个接口功率
                    Double chargePower = queryPowerRate(connectorStatus);
                    /*if(map.containsKey(chargeId)){
                        map.get(chargeId).add(chargePower);
                    }else{*/
                    newList.add(chargePower);
                    //map.put(chargeId, newList);
                    //}
                    //return queryPowerAndEnergy(map,null,null);
                }
                //2.查询单个设备信息时仅匹配单个设备
                if(StringUtils.isNotEmpty(equipmentId) && chargeId.equals(equipmentId)){
                    connectorStatusInfoList.add(connectorStatus);
                    //单个接口功率
                    Double chargePower = queryPowerRate(connectorStatus);
                    //充电电能
                    Double chargeElectric =Double.parseDouble(connectorStatus.getChargeElectricity());
                    //放电电能
                    Double disChargeElectric =Double.parseDouble(connectorStatus.getDischargeElectricity());

                    /*if(map.containsKey(chargeId)){
                        map.get(chargeId).add(chargePower);
                        map2.get(chargeId).add(chargeElectric);
                        map3.get(chargeId).add(disChargeElectric);
                    }else{*/
                        newList.add(chargePower);
                        //map.put(chargeId, newList);
                        newList2.add(chargeElectric);
                        //map2.put(chargeId, newList2);
                        newList3.add(disChargeElectric);
                        //map3.put(chargeId, newList3);
                    //}

                }else {
                    break;
                }
            }
            result.put("connectorStatusInfoList",connectorStatusInfoList);
            result = queryPowerAndEnergy2(newList, newList2, newList3);
        }
        return result;
    }

    //计算ABC相功率
    public Double queryPowerRate(ConnectorStatusInfo connectorStatus){
        return Double.parseDouble(connectorStatus.getCurrentA()) * Double.parseDouble(connectorStatus.getVoltageA())+
                Double.parseDouble(connectorStatus.getCurrentB()) * Double.parseDouble(connectorStatus.getVoltageB())+
                Double.parseDouble(connectorStatus.getCurrentC()) * Double.parseDouble(connectorStatus.getVoltageC());
    }

    public  Map<String, Object> queryPowerAndEnergy2(List<Double> newList,List<Double> newList2,List<Double> newList3) {
        Map<String, Object> result = new HashMap<>();
        Double stationPower = 0.00;//站的总功率

        //设备实时功率
        if(newList!=null){
            for (Double aDouble : newList) {
                stationPower+=aDouble;
            }
        }


        Double chargeElectEnergy = 0.00;//设备充电电能
        Double disChargeElectEnergy = 0.00;//设备放电电能

        //充电电能
        if(newList2!=null){
            for (Double aDouble : newList2) {
                chargeElectEnergy+=aDouble;
            }
        }

        //放电电能
        if(newList3!=null){
            for (Double aDouble : newList3) {
                disChargeElectEnergy+=aDouble;
            }
        }
        result.put("stationPower",stationPower);
        result.put("chargePowerSingle",stationPower);
        result.put("chargeElectEnergy",chargeElectEnergy);
        result.put("disChargeElectEnergy",disChargeElectEnergy);

        return result;
    }


    public  Map<String, Object> queryPowerAndEnergy(Map<String, List<Double>> map,Map<String, List<Double>> map2,Map<String, List<Double>> map3) {
        Map<String, Object> result = new HashMap<>();
        Double stationPower = 0.00;//站的总功率

        //设备实时功率
        for (String s : map.keySet()) {
            List<Double> powerConnects = map.get(s);
            Double chargePowerSingle =0.00;
            for (Double powerSingle : powerConnects) {
                chargePowerSingle+=powerSingle;//单个设备总功率
            }
            stationPower+=chargePowerSingle;
            result.put("chargePowerSingle",chargePowerSingle);
        }

        Double chargeElectEnergy = 0.00;//设备充电电能
        Double disChargeElectEnergy = 0.00;//设备放电电能

        //充电电能
        if(map2!=null){
            for (String s : map2.keySet()) {
                List<Double> chargeElectrics = map2.get(s);
                for (Double chargeEle : chargeElectrics) {
                    chargeElectEnergy+=chargeEle;
                }
            }
        }

        //放电电能
        if(map3!=null){
            for (String s : map3.keySet()) {
                List<Double> disChargeElectrics = map3.get(s);
                for (Double disChargeEle : disChargeElectrics) {
                    disChargeElectEnergy+=disChargeEle;
                }
            }
        }
        result.put("stationPower",stationPower);
        result.put("chargeElectEnergy",chargeElectEnergy);
        result.put("disChargeElectEnergy",disChargeElectEnergy);

        return result;
    }
    /*scx接口 end*/


    /*清除单个充电设备信息缓存*//*
    @CacheEvict(cacheNames="equipment-id",key="#root.caches[0].name+#equipmentId+'-'+#operatorId")
    public void delEquipmentIdInCache(String equipmentId,String operatorId){
        logger.info("delEquipmentIdInCache equipmentId:"+equipmentId+", operatorId:"+operatorId);
    }*/
    
    public List<EquipmentInfoShow> selectEquipmentByOperatorId(String operatorId){
        return equipmentInfoDAO.selectEquipmentByOperatorId(operatorId);
    }

}
