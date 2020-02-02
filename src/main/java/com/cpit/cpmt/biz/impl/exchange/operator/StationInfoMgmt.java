package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorHistoryPowerInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EventInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.dao.security.EquipmentSafeWarningDao;
import com.cpit.cpmt.biz.impl.exchange.basic.AlarmInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.StationStatusInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.TimeUtil;
import com.cpit.cpmt.dto.exchange.basic.*;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StationInfoMgmt {
    private final static Logger logger = LoggerFactory.getLogger(StationInfoMgmt.class);

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;


    @Autowired
    private EquipmentInfoMgmt equipmentInfoMgmt;

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    @Autowired
    ConnectorInfoDAO connectorInfoDAO;

    @Autowired
    private EventInfoDao eventInfoDao;

    @Autowired
    private ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;

    @Autowired
    private AlarmInfoDao alarmInfoDao;

    @Autowired
    private EquipmentSafeWarningDao equipmentSafeWarningDao;


    public Page<StationInfoShow> selectStationByCondition(StationInfoShow station) throws Exception {
        //rabbitMsgSender.sendConnectorStatus("connectorStatus");
        return stationInfoDAO.selectStationByCondition(station);
    }

    /*根据主键查询充电站信息*/
    @Cacheable(cacheNames = "station-id", key = "#root.caches[0].name+#stationId+'-'+#operatorId", unless = "#result == null")
    public StationInfoShow selectByPrimaryKey(String stationId, String operatorId) {
        return stationInfoDAO.selectByPrimaryKey(stationId, operatorId);
    }

    public StationInfoShow selectByStationId(String stationId, String operatorId) {
        StationInfoShow stationInfoShow = stationInfoDAO.selectByStationId(stationId, operatorId);
        /*List<EquipmentInfoShow> equList = stationInfoShow.getEquipmentShowInfos();
        stationInfoShow.setOperateType(equList!=null?equList.size():0);*/
        return stationInfoShow;
    }

    /*动态信息*/
    public StationInfoShow selectDynamicByStationId(String stationId, String operatorId) {
        StationInfoShow stationInfoShow = stationInfoDAO.selectDynamicByStationId(stationId, operatorId);
        Map<String, String> stringStringMap = equipmentInfoMgmt.queryChargeEnergySum(stationId, null, operatorId);
        stationInfoShow.setChargeElecticSum(stringStringMap.get("stationCharge"));//累计充电量
        stationInfoShow.setDisChargeEleticsSum(stringStringMap.get("stationDisCharge"));//累计放电量
        /*Map<String, Object> stringDoubleMap = equipmentInfoMgmt.queryEquipPowerAndEnergy(stationId, null, operatorId);
        if (stringDoubleMap != null) {
            stationInfoShow.setRealTimePower(String.valueOf(stringDoubleMap.get("stationPower")));//实时功率
        }*/

        stationInfoShow.setTotalServiceTime("22h");//累计服务时间
        stationInfoShow.setChargTimes(34);//累计充电次数
        stationInfoShow.setCurrentUseRate("45%");//当前使用率
        stationInfoShow.setErrorRate("15%");//故障率
        stationInfoShow.setSelectCheck("100");//抽查检测
        stationInfoShow.setPassNum("99");//通过数量
        return stationInfoShow;
    }

    /*充电站 充电设备单个信息实时功率*/
    public Map<String, Object> selectRealTimePowerByOne(String stationId, String equipmentId, String operatorId) {
        Map<String, Object> map = new HashMap<>();
        ConnectorHistoryPowerInfo connectorHistoryPowerInfo = connectorHistoryPowerInfoDao.selectPowerByOne(stationId, equipmentId, operatorId);
        if (connectorHistoryPowerInfo != null) {
            map.put("stationPower", connectorHistoryPowerInfo.getPower());//站实时功率
            map.put("chargePower", connectorHistoryPowerInfo.getPower());//桩实时功率
        }
        map.put("hmsDate", connectorHistoryPowerInfo.getRemark1());//时分秒数据
        return map;
    }

    /*根据角色获取充电站集合*/
    public Map<String, Object> getMapStationByPower(StationInfoShow stationInfo) {
        Map<String, Object> map = new HashMap<>();
        List<StationInfoShow> mapStationByPower = stationInfoDAO.getMapStationByPower(stationInfo);
        if (mapStationByPower == null) {
            logger.error("user not root");
            map.put("error", "user not root");
            return map;
        } else {
            for (StationInfoShow stationInfoShow : mapStationByPower) {
                //优先级：故障，预警，正常
                String s = alarmInfoDao.selectStationAlarmLastest(stationInfoShow.getStationID(), stationInfoShow.getOperatorID());
                //预警
                Integer integer = equipmentSafeWarningDao.selectStationWarning(stationInfoShow.getStationID(), stationInfoShow.getOperatorID());
                if (s != null) {
                    if ("0".equals(s)&&integer==0) {
                        stationInfoShow.setAlarmStatus("正常");
                    }
                } else {
                    stationInfoShow.setAlarmStatus("正常");
                }

                if(integer!=0){
                    stationInfoShow.setAlarmStatus("预警");
                }

                if (s != null&&"1".equals(s)) {
                    stationInfoShow.setAlarmStatus("故障");
                }
            }

            map.put("stationList", mapStationByPower);
        }

        /*for (StationInfoShow stationInfoShow : mapStationByPower) {
            int flag=0;
            String stationID = stationInfoShow.getStationID();
            String operatorID = stationInfoShow.getOperatorID();
            List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentList(stationID, operatorID);
            if(equipmentInfoShows==null){
                map.put("error","no equipments in station");
                logger.error("no equipments in station");
                return map;
            }
            aa:
            for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
                try {
                    List<AlarmInfo> alarmInfos = alarmInfoMgmt.queryAlarmInfo(stationID, operatorID, equipmentInfoShow.getEquipmentID());
                    if(alarmInfos==null){
                        logger.error("queryAlarmInfo result is null");
                        map.put("error","queryAlarmInfo result is null");
                        return map;
                    }
                    for (AlarmInfo alarmInfo : alarmInfos) {
                        if(alarmInfo.getAlarmStatus()!=null && "1".equals(alarmInfo.getAlarmStatus())){//故障
                            flag=1;
                            break aa;
                        }
                    }
                } catch (Exception e) {
                    logger.error("scx queryAlarmInfo error", e);
                    map.put("error","queryAlarmInfo result error");
                    return map;
                }
            }

            if(flag==1){
                stationInfoShow.setAlarmStatus("故障");
            }else{
                stationInfoShow.setAlarmStatus("正常");
            }
        }
        map.put("stationList",mapStationByPower);*/
        return map;
    }

    /*根据充电站获取运营商信息，充电桩集合*/
    public StationInfoShow getMapOperAndEquipList(String stationId, String operatorId) {
        StationInfoShow mapOperAndEquipList = stationInfoDAO.getMapOperAndEquipList(stationId, operatorId);
        if (mapOperAndEquipList != null) {
            List<EquipmentInfoShow> equipmentShowInfos = mapOperAndEquipList.getEquipmentShowInfos();
            for (EquipmentInfoShow equipmentShowInfo : equipmentShowInfos) {
                Map<String, String> stringStringMap = equipmentInfoMgmt.queryChargeEnergySum(null, equipmentShowInfo.getEquipmentID(), operatorId);
                String equipmentCharge = stringStringMap.get("equipmentCharge");
                equipmentShowInfo.setChargeElecticSum(equipmentCharge);
                equipmentShowInfo.setChargTimes(10);
            }
            return mapOperAndEquipList;
        } else {
            return null;
        }
        /*List<EquipmentInfo> equipmentInfos = mapOperAndEquipList.getEquipmentInfos();
        for (EquipmentInfo equipmentInfo : equipmentInfos) {
            Map<String, String> stringStringMap = equipmentInfoMgmt.queryChargeEnergySum(null, equipmentInfo.getequipmentID(), operatorId);
            String equipmentCharge = stringStringMap.get("equipmentCharge");
            EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
            BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
            try {
                beanUtilsBean.copyProperties(equipmentInfo,equipmentInfoShow);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            equipmentInfoShow.setChargeElecticSum(equipmentCharge);
            equipmentInfoShow.setChargTimes(10);

        }*/
    }

    /*充电设施单条信息动态信息折线图*/
    public Map<String, Object> selectDynamicGrapyByOne(String equipmentId, String operatorId) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectDynamicGrapyByOne(equipmentId, operatorId);
        Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
        String[] hours = (String[]) hoursBefore.get("list");
        String[] chargeElectic = new String[24];
        int[] chargeNum = new int[24];
        for (int i = 0; i < hours.length; i++) {
            for (StationInfoShow stationInfoShow : stationInfoShows) {
                if (hours[i].equals(Integer.parseInt(stationInfoShow.getHours()))) {
                    chargeElectic[i] = stationInfoShow.getChargeElecticSum();
                    chargeNum[i] = stationInfoShow.getChargTimes();
                    break;
                } else {
                    chargeElectic[i] = "0";
                    chargeNum[i] = 0;
                }
            }
        }
        map.put("daoHours", hours);
        map.put("chargeElectic", chargeElectic);
        map.put("chargeNum", chargeNum);
        return map;
    }

    /*地图动态图*/
    public Map<String, Object> selectGrapy(String stationId, String operatorId) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> tenDaysBefore = TimeUtil.getTenDaysBefore();//前十天至今数组
        String[] chargeEneryList = new String[10];//充电量
        int[] chargeNumList = new int[10];//充电次数
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setStationID(stationId);
        stationInfo.setOperatorID(operatorId);
        stationInfo.setStartDate((Date) tenDaysBefore.get("startTime"));
        stationInfo.setEndDate((Date) tenDaysBefore.get("endTime"));
        String[] tenDays = (String[]) tenDaysBefore.get("list");
        List<StationInfoShow> chargeEnergy = stationInfoDAO.queryMapChargeEnergyDay(stationInfo);//日充电量(前10天)
        List<StationInfoShow> chargeNum = stationInfoDAO.queryMapChargeNumDay(stationInfo);//日充电次数(前10天)
        for (int i = 0; i < tenDays.length; i++) {
            if (chargeEnergy != null) {
                for (StationInfoShow stationInfoShow : chargeEnergy) {
                    if (tenDays[i].equals(stationInfoShow.getHours())) {
                        chargeEneryList[i] = stationInfoShow.getChargeElecticSum();
                        break;
                    } else {
                        chargeEneryList[i] = "0";
                    }
                }
            }
            if (chargeNum != null) {
                for (StationInfoShow stationInfoShow : chargeNum) {
                    if (tenDays[i].equals(stationInfoShow.getHours())) {
                        chargeNumList[i] = stationInfoShow.getChargeSum();
                        break;
                    } else {
                        chargeNumList[i] = 0;
                    }
                }
            }
        }
        map.put("daoHours", tenDaysBefore);
        map.put("chargeElectic", chargeEneryList);
        map.put("chargeNum", chargeNumList);
        return map;
    }

    /*地图上一栏 3年月日充电量*/
    public Map<String, Object> selectMapChargeEleByYMD(EquipmentInfoShow equipment) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setAreaCode(equipment.getAreaCode());
        stationInfo.setAreaCodeList(equipment.getAreaCodeList());
        stationInfo.setOperatorID(equipment.getOperatorID());
        stationInfo.setStationID(equipment.getStationId());

        Map<String, Object> tenDaysBefore =  TimeUtil.getTenDaysBefore();//前十天至今数组
        String[] chargeNum = new String[10];//充电量
        stationInfo.setStartDate((Date) tenDaysBefore.get("startTime"));
        stationInfo.setEndDate((Date) tenDaysBefore.get("endTime"));
        String[] tenDays = (String[]) tenDaysBefore.get("list");
        List<StationInfoShow> chargeEnergy = stationInfoDAO.queryMapChargeEnergyDay(stationInfo);//日(前10天)
        for (int i = 0; i < tenDays.length; i++) {
            if (chargeEnergy != null) {
                for (StationInfoShow stationInfoShow : chargeEnergy) {
                    if (tenDays[i].equals(stationInfoShow.getHours())) {
                        chargeNum[i] = stationInfoShow.getChargeElecticSum();
                        break;
                    } else {
                        chargeNum[i] = "0";
                    }
                }
            }

        }
        map.put("tenDaysBefore", tenDays);
        map.put("tenDaysChargeBefore", chargeNum);

        Double monthChargeSum = stationInfoDAO.queryMapChargeEnergyMonth(stationInfo);
        map.put("month", monthChargeSum != null ? monthChargeSum : "0");//月
        Double yearChargeSum = stationInfoDAO.queryMapChargeEnergyYear(stationInfo);
        map.put("year", yearChargeSum != null ? yearChargeSum : "0");//年

        return map;
    }


    /*地图上一栏 1桩数 2实时功率 4利用率*/
    public Map<String, Object> selectMapFirstNumAndRate(EquipmentInfoShow equipment){
        Map<String, Object> map = new HashMap<>();

        Page<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentByCondition(equipment);

        //一.获取用户权限下所有的stationid
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setAreaCodeList(equipment.getAreaCodeList());
        stationInfo.setAreaCode(equipment.getAreaCode());
        stationInfo.setOperatorID(equipment.getOperatorID());
        stationInfo.setStationID(equipment.getStationId());
        List<StationInfoShow> mapStationByPower = stationInfoDAO.getMapStationByPower(stationInfo);
        if (mapStationByPower == null) {
            map.put("error", "user has no root");
            return map;
        }

        //条件下的桩总数
        int totalEquipment = equipmentInfoShows.size();
        map.put("totalEquipment", totalEquipment);//总桩数

        DecimalFormat df = new DecimalFormat("0.00%");

        //1.
        //在线
        /*List<Object> namesList = Arrays.asList("1", "2", "3", "4", "255");
        List<Object> normalObjects = new ArrayList<>();
        normalObjects.addAll(namesList);
        equipment.setStatusList(normalObjects);
        Integer normalInteger = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (normalInteger != null && normalInteger != 0) {
            map.put("changeingUsedRate", df.format((double) normalInteger / totalEquipment));//利用率(在线充电桩和总桩数的比率)
        } else {
            map.put("changeingUsedRate", "0.00%");
        }*/
        String allUseRate = equipmentInfoDAO.getAllUseRate(stationInfo);
        if(allUseRate!=null){
            map.put("changeingUsedRate", allUseRate);//利用率
        } else {
            map.put("changeingUsedRate", "0.00%");
        }

        //正常
        List<Object> normalList = Arrays.asList("1", "2", "3", "4");
        List<Object> normalObject = new ArrayList<>();
        normalObject.addAll(normalList);
        equipment.setStatusList(normalObject);
        Integer normalIntegerList = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (normalIntegerList != null && normalIntegerList != 0) {
            map.put("workOn", normalIntegerList);
        } else {
            map.put("workOn", "0");
        }


        //故障
        List<Object> faultObjects = new ArrayList<>();
        faultObjects.add("255");
        equipment.setStatusList(faultObjects);
        Integer faultInteger = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (faultInteger != null && faultInteger != 0) {
            map.put("breakDown", faultInteger);
        } else {
            map.put("breakDown", "0");
        }

        //4.
        //公共
        List<Integer> commonList = new ArrayList<>();
        commonList.add(1);
        equipment.setStationTypeList(commonList);
        /*Integer common = equipmentInfoDAO.selectStationType(equipment);//总数
        Integer commonOnline = equipmentInfoDAO.selectStationTypeOnLine(equipment);//在线
        if (common != null && commonOnline != null && common != 0 && commonOnline != 0) {
            map.put("commonRate", df.format((double) commonOnline / common));//公共率
        } else {
            map.put("commonRate", "0.00%");//公共率
        }*/
        map.put("commonRate",equipmentInfoDAO.getOneUseRate(equipment));//公共率

        //普通
        List<Integer> ordinaryList = new ArrayList<>();
        ordinaryList.add(50);
        equipment.setStationTypeList(ordinaryList);
        Integer ordinary = equipmentInfoDAO.selectStationType(equipment);//总数
        if (ordinary != null && ordinary != 0) {
            map.put("ordinary", ordinary);//普通
        } else {
            map.put("ordinary", "0");
        }

        //专用
        List<Integer> specialStatTypeList = Arrays.asList(100, 101, 102, 103);
        List<Integer> spicalList = new ArrayList<>();
        spicalList.addAll(specialStatTypeList);
        equipment.setStationTypeList(spicalList);
        Integer spical = equipmentInfoDAO.selectStationType(equipment);//总数
        //Integer spicalOnline = equipmentInfoDAO.selectStationTypeOnLine(equipment);//在线
        if (spical != null && spical != 0) {
            map.put("spical", spical);//专用
            /*if (spicalOnline != null && spicalOnline != 0) {
                map.put("spicalRate", df.format((double) spicalOnline / spical));//专用率
            } else {
                map.put("spicalRate", "0.00%");
            }*/
        } else {
            map.put("spical", "0");//专用
        }
        map.put("spicalRate", equipmentInfoDAO.getOneUseRate(equipment));//专用率

        //直流
        equipment.setEquipmentType(1);
        /*Integer direct = equipmentInfoDAO.selectEquipmentType(equipment);
        Integer directOnline = equipmentInfoDAO.selectEquipmentTypeOnline(equipment);
        if (direct != null && directOnline != null && direct != 0 && directOnline != 0) {
            map.put("directRate", df.format((double) directOnline / direct));//直流
        } else {
            map.put("directRate", "0.00%");
        }*/
        map.put("directRate", equipmentInfoDAO.getOneUseRate(equipment));//直流

        //交流
        equipment.setEquipmentType(2);
        /*Integer exchange = equipmentInfoDAO.selectEquipmentType(equipment);
        Integer exchangeOnline = equipmentInfoDAO.selectEquipmentTypeOnline(equipment);
        if (exchange != null && exchangeOnline != null && exchange != 0 && exchangeOnline != 0) {
            map.put("exchangeRate", df.format((double) exchangeOnline / exchange));
        } else {
            map.put("exchangeRate", "0.00%");
        }*/
        map.put("exchangeRate", equipmentInfoDAO.getOneUseRate(equipment));

        //交直流一体
        equipment.setEquipmentType(3);
        /*Integer directExchange = equipmentInfoDAO.selectEquipmentType(equipment);
        Integer directExchangeOnline = equipmentInfoDAO.selectEquipmentTypeOnline(equipment);
        if (directExchange != null && directExchangeOnline != null && directExchange != 0 && directExchangeOnline != 0) {
            map.put("directExchangeRate", df.format((double) directExchangeOnline / directExchange));
        } else {
            map.put("directExchangeRate", "0.00%");
        }*/
        map.put("directExchangeRate", equipmentInfoDAO.getOneUseRate(equipment));


        //二.scx接口返回在充电的设备id集合
        Double installTotalPower = 0.00;//装机功率
        for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
            installTotalPower += equipmentInfoShow.getPower();
        }

        //2.
        map.put("installTotalPower", String.format("%.1f", installTotalPower));//装机功率
        map.put("hmsDate", new SimpleDateFormat("HH:mm:ss").format(new Date()));//时分秒数据

        //实时功率
        List<ConnectorHistoryPowerInfo> connectorHistoryPowerInfos = connectorHistoryPowerInfoDao.selectPowerTenMinutes(stationInfo);
        map.put("powerRateNow", connectorHistoryPowerInfos);//实时功率

        //总实时功率
        Double aDouble = connectorHistoryPowerInfoDao.selectTotalPower(stationInfo);
        map.put("realTimeTotalPower",aDouble);
        return map;
    }


    /*更新充电站信息*/
    @Transactional
    @CacheEvict(cacheNames = "station-id", key = "#root.caches[0].name+#stationInfo.stationID+'-'+#stationInfo.operatorID")
    public void updateStationInfo(StationInfoShow stationInfo) {
        stationInfo.setConnectionTime(new Date());
        stationInfoDAO.updateByPrimaryKeySelective(stationInfo);

        //停运
        if (stationInfo.getStationStatus() != null && stationInfo.getStationStatus() == 5) {
            String operatorID = stationInfo.getOperatorID();
            String stationId = stationInfo.getStationID();
            if (StringUtils.isNotEmpty(stationId) && StringUtils.isNotEmpty(operatorID)) {
                List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentList(stationId, operatorID);
                if (equipmentInfoShows != null) {
                    for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
                        equipmentInfoShow.setEquipmentStatus(5);
                        equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
                    }
                }
            }
        }

        //启用
        if (stationInfo.getStationStatus() != null && stationInfo.getStationStatus() == 50) {
            String operatorID = stationInfo.getOperatorID();
            String stationId = stationInfo.getStationID();
            if (StringUtils.isNotEmpty(stationId) && StringUtils.isNotEmpty(operatorID)) {
                List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentList(stationId, operatorID);
                if (equipmentInfoShows != null) {
                    for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
                        equipmentInfoShow.setEquipmentStatus(50);
                        equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
                    }

                }
            }
        }

        //添加充电站历史
        stationInfo.setOperateType(2);//修改
        historyInfoMgmt.insertStationHisInfo(stationInfo);

    }

    /*添加充电站信息*/
    @Transactional
    public void addStationInfo(StationInfoShow stationInfo) {
        stationInfo.setSid(SequenceId.getInstance().getId("cpmtStationId", "", 6));
        stationInfo.setAllowanceStatus(0);
        stationInfo.setConnectionTime(new Date());
        stationInfoDAO.insertSelective(stationInfo);

        //添加充电站历史
        stationInfo.setOperateType(1);//新增
        historyInfoMgmt.insertStationHisInfo(stationInfo);
    }

    /*查询充电站告警信息*/
    public Page<AlarmInfo> selectAlarmInfoByStation(AlarmInfo alarmInfo) {
        return stationInfoDAO.selectAlarmInfoByStation(alarmInfo);
    }

    //热力图
    public Map<Object, Object> getThermalMap(StationInfoShow stationInfo, int cycle, int standard) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        switch (standard) {
            //充电站充电量
            case 1:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumHour(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = TimeUtil.getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumDay(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumWeek(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumMonth(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumQuarter(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            //装机功率
            case 2:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerHour(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = TimeUtil.getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerDay(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerWeek(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerMonth(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerQuarter(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            //充电次数
            case 3:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesHour(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = TimeUtil.getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesDay(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesWeek(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesMonth(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesQuarter(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            case 4:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowHour(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = TimeUtil.getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowDay(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowWeek(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowMonth(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowQuarter(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            case 5:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateHour(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = TimeUtil.getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateDay(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateWeek(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateMonth(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateQuarter(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            default:
                break;
        }
        return resultMap;
    }


    @Transactional
    public void addQueryStation(StationInfo stationInfo) {
        String stationId = stationInfo.getStationID();
        String oprationId = stationInfo.getOperatorID();

        StationInfoShow s = stationInfoDAO.selectByPrimaryKey(stationId, oprationId);
        StationInfoShow stationInfoShow = new StationInfoShow();
        BeanUtils.copyProperties(stationInfo, stationInfoShow);
        if (null == s) {

            addStationInfo(stationInfoShow);
        } else {
            updateStationInfo(stationInfoShow);
        }
        //equipmentInfos
        List<EquipmentInfo> equipmentInfos = stationInfo.getEquipmentInfos();
        for (EquipmentInfo e : equipmentInfos) {
            String equipmentId = e.getEquipmentID();
            e.setOperatorID(oprationId);
            EquipmentInfo e_ = equipmentInfoDAO.selectByEquipId(e.getEquipmentID(), e.getOperatorID());
            EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
            BeanUtils.copyProperties(e, equipmentInfoShow);
            if (null == e_) {

                equipmentInfoShow.setEid(String.format("%06d", SequenceId.getInstance().getId("cpmtEquipmentId")));
                equipmentInfoShow.setStationId(stationInfo.getStationID());
                equipmentInfoDAO.insertSelective(equipmentInfoShow);

            } else {
                equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
            }
            //connectors
            List<ConnectorInfo> cs = e.getConnectorInfos();
            for (ConnectorInfo c : cs) {
                c.setOperatorID(oprationId);
                c.setEquipmentID(equipmentId);
                String ci = c.getConnectorID();
                ConnectorInfoShow c_ = connectorInfoDAO.getConnectorById(c.getConnectorID(), oprationId);
                ConnectorInfoShow cshow = new ConnectorInfoShow();
                BeanUtils.copyProperties(c, cshow);
                cshow.setCid(String.format("%06d", SequenceId.getInstance().getId("cpmtConnectorId")));
                if (null == c_) {
                    connectorInfoDAO.insertSelective(cshow);
                } else {
                    connectorInfoDAO.updateRecord(cshow);
                }
            }
        }

    }

    public List<ConnectorInfoShow> getConnectorsByoperatorId(String operatorId) {
        return connectorInfoDAO.getConnectorsByoperatorId(operatorId);
    }

    public List<StationInfoShow> getStationsByOperatorId(String operatorId) {
        return stationInfoDAO.getStationsByOperatorId(operatorId);
    }

    //事件分页
    public Page<EventInfo> selectEventByCondition(EventInfo event) {
        return eventInfoDao.selectByCondition(event);
    }

    //充电量 公用方法
    public static Map<Object, Object> hotMapMethod(List<StationInfoShow> stationInfoShows,List<Map<String, Object>> mapValueList ,Map<Object, Object> resultMap,String s){
        if(stationInfoShows==null ||stationInfoShows.size()==0){
            resultMap.put(s,null);
        }else {
            for (StationInfoShow stationInfoShow : stationInfoShows) {
                Map<String, Object> map = new HashMap<>();
                List<Object> elementList = new ArrayList<>();
                map.put("name", stationInfoShow.getStationName());
                elementList.add(stationInfoShow.getStationLng());
                elementList.add(stationInfoShow.getStationLat());
                elementList.add(stationInfoShow.getChargeElecticSum());
                map.put("value", elementList);

                map.put("time", stationInfoShow.getHours());
                map.put("code", stationInfoShow.getAreaCode());
                mapValueList.add(map);
            }
            resultMap.put(s,mapValueList);
        }

        return resultMap;
    }

    //装机功率
    public static Map<Object, Object> hotMapPowerSumMethod(List<StationInfoShow> stationInfoShows,List<Map<String, Object>> mapValueList ,Map<Object, Object> resultMap,String s){
        if(stationInfoShows==null ||stationInfoShows.size()==0){
            resultMap.put(s,null);
        }else {
            for (StationInfoShow stationInfoShow : stationInfoShows) {
                Map<String, Object> map = new HashMap<>();
                List<Object> elementList = new ArrayList<>();
                map.put("name", stationInfoShow.getStationName());
                elementList.add(stationInfoShow.getStationLng());
                elementList.add(stationInfoShow.getStationLat());
                elementList.add(stationInfoShow.getPowerSum());
                map.put("value", elementList);

                map.put("time", stationInfoShow.getHours());
                map.put("code", stationInfoShow.getAreaCode());
                mapValueList.add(map);
            }
            resultMap.put(s,mapValueList);
        }

        return resultMap;
    }

    //充电次数
    public static Map<Object, Object> hotMapChargTimesMethod(List<StationInfoShow> stationInfoShows,List<Map<String, Object>> mapValueList ,Map<Object, Object> resultMap,String s){
        if(stationInfoShows==null ||stationInfoShows.size()==0){
            resultMap.put(s,null);
        }else {
            for (StationInfoShow stationInfoShow : stationInfoShows) {
                Map<String, Object> map = new HashMap<>();
                List<Object> elementList = new ArrayList<>();
                map.put("name", stationInfoShow.getStationName());
                elementList.add(stationInfoShow.getStationLng());
                elementList.add(stationInfoShow.getStationLat());
                elementList.add(stationInfoShow.getChargTimes());
                map.put("value", elementList);

                map.put("time", stationInfoShow.getHours());
                map.put("code", stationInfoShow.getAreaCode());
                mapValueList.add(map);
            }
            resultMap.put(s,mapValueList);
        }

        return resultMap;
    }

    //大屏接口 start
    public List<StationInfoShow> selectBigScreenChargeNumByArea(){
        return stationInfoDAO.selectBigScreenChargeNumByArea();
    }
    public List<StationInfoShow> selectBigScreenChargeNumByOperator(){
        return stationInfoDAO.selectBigScreenChargeNumByOperator();
    }
    public List<StationInfoShow> selectBigScreenUseRateByArea(){
        return stationInfoDAO.selectBigScreenUseRateByArea();
    }
    public List<StationInfoShow> selectBigScreenUseRateByOperator(){
        return stationInfoDAO.selectBigScreenUseRateByOperator();
    }
    public StationInfoShow selectBigScreenChargeInfo(){
        DecimalFormat   df   =new DecimalFormat("0.00");
        StationInfoShow stationInfoShow = stationInfoDAO.selectBigScreenChargeInfo();

        Double aDouble = stationInfoDAO.selectBigScreenChargeAmount();//之前充电量
        StationInfoShow stationInfoShow1 = stationInfoDAO.selectChargeNumberToday();//今日充电量
        stationInfoShow.setStationLng(aDouble+(stationInfoShow1!=null?stationInfoShow1.getStationLng():0.0));//充电量
        //stationInfoShow.setStationLng(stationInfoShow1!=null?stationInfoShow1.getStationLng():0.0);//充电量

        Double powerSum = stationInfoShow.getPowerSum();
        if(powerSum!=null)
            stationInfoShow.setHours(df.format((double) powerSum / 60));//时长
        else
            stationInfoShow.setHours("0");

        Integer checkoutStatus = stationInfoShow.getCheckoutStatus();
        if(checkoutStatus!=null && checkoutStatus!=0)
            //stationInfoShow.setAlarmStatus(df.format((double) checkoutStatus/10000));
            stationInfoShow.setAlarmStatus(String.valueOf(checkoutStatus));
        else
            stationInfoShow.setAlarmStatus("0");//充电次数
        /*Integer integer = stationInfoDAO.selectBigScreenChargeNums();
        stationInfoShow.setAlarmStatus(String.valueOf(integer));*/

        return stationInfoShow;
    }
    //正常故障
    public EquipmentInfoShow selectBigScreenNormalDown(){
        EquipmentInfoShow equipmentResult = new EquipmentInfoShow();
        equipmentResult.setChargeElecticSum("正常桩");
        equipmentResult.setDisChargeEleticsSum("故障桩");
        EquipmentInfoShow equipment = new EquipmentInfoShow();
        //正常

        List<Object> normalList = Arrays.asList("1", "2", "3", "4");
        List<Object> normalObject = new ArrayList<>();
        normalObject.addAll(normalList);
        equipment.setStatusList(normalObject);
        Integer normalIntegerList = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (normalIntegerList != null) {
            equipmentResult.setChargTimes(normalIntegerList);
        } else {
            equipmentResult.setChargTimes(0);
        }


        //故障
        List<Object> faultObjects = new ArrayList<>();
        faultObjects.add("255");
        equipment.setStatusList(faultObjects);
        Integer faultInteger = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (faultInteger != null) {
            equipmentResult.setChargeErrorTimes(faultInteger);
        } else {
            equipmentResult.setChargeErrorTimes(0);
        }

        return equipmentResult;
    }

    //普通专用
    public EquipmentInfoShow selectBigScreenOrdinarySpecial(){
        EquipmentInfoShow equipmentResult = new EquipmentInfoShow();
        equipmentResult.setChargeElecticSum("普通桩");
        equipmentResult.setDisChargeEleticsSum("专用桩");
        EquipmentInfoShow equipment = new EquipmentInfoShow();
        //普通
        /*
        commonList.add(50);
        equipment.setStationTypeList(commonList);*/
        Integer ordinary = equipmentInfoDAO.selectUnStationType(equipment);//总数
        if (ordinary != null) {
            equipmentResult.setChargTimes(ordinary);
        } else {
            equipmentResult.setChargTimes(0);
        }

        //专用
        List<Integer> commonList = new ArrayList<>();
        List<Integer> specialStatTypeList = Arrays.asList(100, 101, 102, 103);
        commonList.addAll(specialStatTypeList);
        equipment.setStationTypeList(commonList);
        Integer spical = equipmentInfoDAO.selectStationType(equipment);//总数
        if (spical != null) {
            equipmentResult.setChargeErrorTimes(spical);
        } else {
            equipmentResult.setChargeErrorTimes(0);
        }

        return equipmentResult;
    }

    //大屏接口 end

    public  AlarmInfo getAlarmInfoByPrimaryKey(Integer id){
        return alarmInfoDao.selectByPrimaryKey(id);
    }
}
