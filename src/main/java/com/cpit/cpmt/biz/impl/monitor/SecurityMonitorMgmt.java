package com.cpit.cpmt.biz.impl.monitor;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.monitor.BmsAveInfoDAO;
import com.cpit.cpmt.biz.dao.monitor.BmsEvaluateResultDAO;
import com.cpit.cpmt.biz.dao.monitor.BmsThresholdRangeDAO;
import com.cpit.cpmt.biz.utils.monitor.IntervalUtil;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.monitor.BmsAveInfo;
import com.cpit.cpmt.dto.monitor.BmsEvaluateResult;
import com.cpit.cpmt.dto.monitor.BmsThresholdRange;
import org.apache.commons.lang.ObjectUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class SecurityMonitorMgmt {

    @Autowired
    private BmsAveInfoDAO bmsAveInfoDAO;

    @Autowired
    private BmsInfoDao bmsInfoDao;

    @Autowired
    private BmsThresholdRangeDAO bmsThresholdRangeDAO;

    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;

    @Autowired
    private BmsEvaluateResultDAO bmsEvaluateResultDAO;

    @Autowired
    private AlarmInfoDao alarmInfoDao;


    //查询单个充电设备评估结果
    public BmsEvaluateResult getBmsEvaluateResult(String equipmentId,String operatorId){
        BmsEvaluateResult bmsEvaluateResult = bmsEvaluateResultDAO.getBmsEvaluateResult(equipmentId, operatorId);
        if (bmsEvaluateResult!=null) {
            //使用寿命估计=充电设施使用年限-（当前时间-充电设施出厂时间）
            EquipmentInfoShow equipmentInfo = equipmentInfoDAO.selectByEquipId(equipmentId, operatorId);
            Integer periodUse = equipmentInfo.getPeriodUse();
            Double aDouble = Double.valueOf(periodUse != null ? periodUse : 0.0);
            Double aDouble1 = dayComparePrecise(equipmentInfo.getInDate(), new Date());
            bmsEvaluateResult.setChargerLifeTime(aDouble - aDouble1);
        }
        return bmsEvaluateResult;
    }

    //查询充电桩告警信息分页
    public Page<AlarmInfo> selectEquipmentAlarm (AlarmInfo alarm){
        return alarmInfoDao.selectEquipmentAlarm(alarm.getEquipmentID(),alarm.getOperatorID());
    }


    //查询最新阈值
    public BmsAveInfo selectBmsAveLastest(){
        return bmsAveInfoDAO.selectBmsAveLastest();
    }

    //修改阈值信息
    @Transactional
    public void updateByPrimaryKeySelective(BmsAveInfo record){
        bmsAveInfoDAO.updateByPrimaryKeySelective(record);
    }

    //生成阈值 定时任务生成
    @Transactional
    public void queryBmsAverageList() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -3);//当前时间往前三个月
        Date beginTime = calendar.getTime();

        BmsAveInfo bms = new BmsAveInfo();

        //总电压
        List<BmsAveInfo> bmsTotalVoltageNumber = bmsAveInfoDAO.getBmsTotalVoltageNumber(beginTime, endTime);
        if (bmsTotalVoltageNumber != null && bmsTotalVoltageNumber.size()!=0) {
            int totalVoltageSize = bmsTotalVoltageNumber.size();
            if (totalVoltageSize != 1) {
                bms.setTatalVoltageAve(bmsTotalVoltageNumber.get(totalVoltageSize - 1).getTatalVoltageAve());
                StringBuilder totalVoltageContainer = new StringBuilder();
                for (int i = 0; i < totalVoltageSize; i++) {
                    totalVoltageContainer.append(bmsTotalVoltageNumber.get(i).getTatalVoltageAve());
                    if (i != totalVoltageSize - 1) {
                        totalVoltageContainer.append(",");
                    }
                }
                bms.setTatalVoltageAveContainer(totalVoltageContainer.toString());
            } else {
                bms.setTatalVoltageAve(bmsTotalVoltageNumber.get(0).getTatalVoltageAve());
            }
        }


        //总电流
        List<BmsAveInfo> bmsTotalCurrentNumber = bmsAveInfoDAO.getBmsTotalCurrentNumber(beginTime, endTime);
        if (bmsTotalCurrentNumber != null && bmsTotalCurrentNumber.size()!=0) {
            int totalCurrentSize = bmsTotalCurrentNumber.size();
            if (totalCurrentSize != 1) {
                bms.setTotalCurrentAve(bmsTotalCurrentNumber.get(totalCurrentSize - 1).getTotalCurrentAve());
                StringBuilder totalCurrentContainer = new StringBuilder();
                for (int i = 0; i < totalCurrentSize; i++) {
                    totalCurrentContainer.append(bmsTotalCurrentNumber.get(i).getTotalCurrentAve());
                    if (i != totalCurrentSize - 1) {
                        totalCurrentContainer.append(",");
                    }
                }
                bms.setTotalCurrentAveContainer(totalCurrentContainer.toString());
            } else {//一个众数
                bms.setTotalCurrentAve(bmsTotalCurrentNumber.get(0).getTotalCurrentAve());
            }

        }

        //soc
        List<BmsAveInfo> bmsSocNumber = bmsAveInfoDAO.getBmsSocNumber(beginTime, endTime);
        if (bmsSocNumber != null && bmsSocNumber.size()!=0) {
            int socSize = bmsSocNumber.size();
            if (socSize != 1) {//多个众数
                bms.setSocAve(bmsSocNumber.get(socSize - 1).getSocAve());//默认取最大众数
                StringBuilder socAveContainer = new StringBuilder();
                for (int i = 0; i < socSize; i++) {
                    socAveContainer.append(bmsSocNumber.get(i).getSocAve());
                    if (i != socSize - 1) {
                        socAveContainer.append(",");
                    }
                }
                bms.setSocAveContainer(socAveContainer.toString());

            } else {//一个众数
                bms.setSocAve(bmsSocNumber.get(0).getSocAve());
            }
        }
        //单体最高电压
        List<BmsAveInfo> bmsVoltagehNumber = bmsAveInfoDAO.getBmsVoltagehNumber(beginTime, endTime);
        if (bmsVoltagehNumber != null && bmsVoltagehNumber.size()!=0) {
            int voltageSize = bmsVoltagehNumber.size();
            if (voltageSize != 1) {//多个众数
                bms.setVoltageHAve(bmsVoltagehNumber.get(voltageSize - 1).getVoltageHAve());//默认取最大众数
                StringBuilder voltageHAveContainer = new StringBuilder();
                for (int i = 0; i < voltageSize; i++) {
                    voltageHAveContainer.append(bmsVoltagehNumber.get(i).getVoltageHAve());
                    if (i != voltageSize - 1) {
                        voltageHAveContainer.append(",");
                    }
                }
                bms.setVoltageHAveContainer(voltageHAveContainer.toString());
            } else {//一个众数
                bms.setVoltageHAve(bmsVoltagehNumber.get(0).getVoltageHAve());
            }
        }

        //单体最低电压
        List<BmsAveInfo> bmsVoltagelNumber = bmsAveInfoDAO.getBmsVoltagelNumber(beginTime, endTime);
        if (bmsVoltagelNumber != null && bmsVoltagelNumber.size()!=0) {
            int VoltagelSize = bmsVoltagelNumber.size();
            if (VoltagelSize != 1) {
                bms.setVoltageLAve(bmsVoltagelNumber.get(VoltagelSize - 1).getVoltageLAve());
                StringBuilder voltageLAveContainer = new StringBuilder();
                for (int i = 0; i < VoltagelSize; i++) {
                    voltageLAveContainer.append(bmsVoltagelNumber.get(i).getVoltageLAve());
                    if (i != VoltagelSize - 1) {
                        voltageLAveContainer.append(",");
                    }
                }
                bms.setVoltageLAveContainer(voltageLAveContainer.toString());
            } else {
                bms.setVoltageLAve(bmsVoltagelNumber.get(0).getVoltageLAve());
            }
        }
        //最高温度
        List<BmsAveInfo> bmsTempturehNumber = bmsAveInfoDAO.getBmsTempturehNumber(beginTime, endTime);
        if (bmsTempturehNumber != null && bmsTempturehNumber.size()!=0) {
            int temptureHSize = bmsTempturehNumber.size();
            if (temptureHSize != 1) {
                bms.setTemptureHAve(bmsTempturehNumber.get(temptureHSize - 1).getTemptureHAve());
                StringBuilder temptureHContainer = new StringBuilder();
                for (int i = 0; i < temptureHSize; i++) {
                    temptureHContainer.append(bmsTempturehNumber.get(i).getTemptureHAve());
                    if (i != temptureHSize - 1) {
                        temptureHContainer.append(",");
                    }
                }
                bms.setTemptureHAveContainer(temptureHContainer.toString());
            } else {
                bms.setTemptureHAve(bmsTempturehNumber.get(0).getTemptureHAve());
            }
        }

        //最低温度
        List<BmsAveInfo> bmsTempturelNumber = bmsAveInfoDAO.getBmsTempturelNumber(beginTime, endTime);
        if (bmsTempturelNumber != null && bmsTempturelNumber.size()!=0) {
            int temptureLSize = bmsTempturelNumber.size();
            if (temptureLSize != 1) {
                bms.setTemptureLAve(bmsTempturelNumber.get(temptureLSize - 1).getTemptureLAve());
                StringBuilder temptureLContainer = new StringBuilder();
                for (int i = 0; i < temptureLSize; i++) {
                    temptureLContainer.append(bmsTempturelNumber.get(i).getTemptureLAve());
                    if (i != temptureLSize - 1) {
                        temptureLContainer.append(",");
                    }
                }
                bms.setTemptureLAveContainer(temptureLContainer.toString());
            } else {
                bms.setTemptureLAve(bmsTempturelNumber.get(0).getTemptureLAve());
            }
        }

        //故障率
        BmsAveInfo bmsAveInfo = new BmsAveInfo();
        bmsAveInfo.setBeginTime(beginTime);
        bmsAveInfo.setEndTime(endTime);
        Integer totalNumber = bmsAveInfoDAO.selectFault(bmsAveInfo);//总数
        bmsAveInfo.setCopareResult("255");
        Integer faultNumber = bmsAveInfoDAO.selectFault(bmsAveInfo);//故障数
        DecimalFormat df = new DecimalFormat("#,##0.00");
        bms.setFaultRate(new Double(df.format((float) faultNumber / totalNumber)));

        bms.setId(SequenceId.getInstance().getId("monBmsAveId", "", 6));
        bms.setInTime(endTime);
        bmsAveInfoDAO.insertSelective(bms);
    }

    //过程信息参数与阈值的关系及共性特点
    public String getResult() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -1);//当前时间往前1个月
        Date beginTime = calendar.getTime();

        Map<String, Object> compareResult = getCompareResult("", "", beginTime, endTime);
        int allNum = (int) compareResult.get("allNum");
        int[] voltageResult = (int[]) compareResult.get("voltageResult");
        int[] currentResult = (int[]) compareResult.get("currentResult");
        int[] socResult = (int[]) compareResult.get("socResult");
        int[] voltageHResult = (int[]) compareResult.get("voltageHResult");
        int[] voltageLResult = (int[]) compareResult.get("voltageLResult");
        int[] temptureHResult = (int[]) compareResult.get("temptureHResult");
        int[] temptureLResult = (int[]) compareResult.get("temptureLResult");

        String resultCommon = "总电压大于阈值的情况为" + numberTransform(numberRate(voltageResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(voltageResult[1], allNum)) +
                "；总电流大于阈值的情况为" + numberTransform(numberRate(currentResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(currentResult[1], allNum)) +
                "；荷电状态大于阈值的情况为" + numberTransform(numberRate(socResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(socResult[1], allNum)) +
                "；单体最高电压大于阈值的情况为" + numberTransform(numberRate(voltageHResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(voltageHResult[1], allNum)) +
                "；单体最低电压大于阈值的情况为" + numberTransform(numberRate(voltageLResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(voltageLResult[1], allNum)) +
                "；单体最高温度大于阈值的情况为" + numberTransform(numberRate(temptureHResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(temptureHResult[1], allNum)) +
                "；单体最低温度大于阈值的情况为" + numberTransform(numberRate(temptureLResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(temptureLResult[1], allNum)) + "。";
        return resultCommon;
    }

    //获取每个充电桩对比结果
    @Transactional
    public void getResultByCharger() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -1);//当前时间往前1个月
        Date beginTime = calendar.getTime();

        //所有桩状态次数
        EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
        Integer allEquNumber = equipmentInfoDAO.getEquipmentStatusNumber(equipmentInfoShow);

        //最新概率范围
        List<BmsThresholdRange> bmsThresholdRanges = bmsThresholdRangeDAO.selectBmsThresholdRangeAveLastest();
        IntervalUtil a = new IntervalUtil();

        List<BmsInfo> bmsInfos = bmsInfoDao.queryBmsequipmentIDList(beginTime, endTime);//所有充电桩
        if (bmsInfos != null) {
            for (BmsInfo bmsInfo : bmsInfos) {
                //单个桩的过程信息跟最新阈值做比对
                String equipmentID = bmsInfo.getEquipmentID();
                String operatorID = bmsInfo.getOperatorID();

                //荷电状态
                Map<String, Object> compareResult = getCompareResult(operatorID, equipmentID, beginTime, endTime);
                int allNum = (int) compareResult.get("allNum");
                int[] voltageResult = (int[]) compareResult.get("voltageResult");
                int[] currentResult = (int[]) compareResult.get("currentResult");
                int[] socResult = (int[]) compareResult.get("socResult");
                int[] voltageHResult = (int[]) compareResult.get("voltageHResult");
                int[] voltageLResult = (int[]) compareResult.get("voltageLResult");
                int[] temptureHResult = (int[]) compareResult.get("temptureHResult");
                int[] temptureLResult = (int[]) compareResult.get("temptureLResult");

                BmsEvaluateResult bmsEvaluateResult = new BmsEvaluateResult();
                StringBuilder recordEvaluteSB = null;
                StringBuilder evaluteBasisSB = null;
                String evaluteResult="";//评估结论
                String accordance="";//判定依据
                //四种级别阈值范围
                for (int i = 0; i < bmsThresholdRanges.size(); i++) {
                    recordEvaluteSB = new StringBuilder();
                    evaluteBasisSB = new StringBuilder();

                    BmsThresholdRange bmsRange = bmsThresholdRanges.get(i);
                    Double obj = numberRate(voltageResult[0], allNum);
                    boolean tatalVoltage1 = a.isInTheInterval(String.valueOf(obj), bmsRange.getTatalVoltageRange());
                    Double obj1 = numberRate(voltageResult[1], allNum);
                    boolean tatalVoltage2 = a.isInTheInterval(String.valueOf(obj1), bmsRange.getTatalVoltageRange());
                    if (tatalVoltage1 || tatalVoltage2) {
                        recordEvaluteSB.append("总电压; ");
                        evaluteBasisSB.append("总电压大于阈值的概率：").append(numberTransform(obj)).append(",小于阈值的概率：").append(numberTransform(obj1)).append(";");
                    }
                    Double obj2 = numberRate(currentResult[0], allNum);
                    boolean totalCurrentl = a.isInTheInterval(String.valueOf(obj2), bmsRange.getTotalCurrentRange());
                    Double obj3 = numberRate(currentResult[1], allNum);
                    boolean totalCurrent2 = a.isInTheInterval(String.valueOf(obj3), bmsRange.getTotalCurrentRange());
                    if (totalCurrentl || totalCurrent2) {
                        recordEvaluteSB.append("总电流; ");
                        evaluteBasisSB.append("总电流大于阈值的概率：").append(numberTransform(obj2)).append(",小于阈值的概率：").append(numberTransform(obj3)).append(";");
                    }
                    Double obj4 = numberRate(socResult[0], allNum);
                    boolean soc1 = a.isInTheInterval(String.valueOf(obj4), bmsRange.getSocRange());
                    Double obj5 = numberRate(socResult[1], allNum);
                    boolean soc2 = a.isInTheInterval(String.valueOf(obj5), bmsRange.getSocRange());
                    if (soc1 || soc2) {
                        recordEvaluteSB.append("soc; ");
                        evaluteBasisSB.append("soc大于阈值的概率：").append(numberTransform(obj4)).append(",小于阈值的概率：").append(numberTransform(obj5)).append(";");
                    }
                    Double obj6 = numberRate(voltageHResult[1], allNum);
                    boolean voltageH1 = a.isInTheInterval(String.valueOf(obj6), bmsRange.getVoltageHMin());
                    if(voltageH1){
                        recordEvaluteSB.append("单体最高电压; ");
                        evaluteBasisSB.append("单体最高电压小于阈值的概率：").append(numberTransform(obj6)).append(";");
                    }

                    Double obj7 = numberRate(voltageLResult[0], allNum);
                    boolean voltageL1 = a.isInTheInterval(String.valueOf(obj7), bmsRange.getVoltageLMin());
                    if(voltageL1){
                        recordEvaluteSB.append("单体最低电压; ");
                        evaluteBasisSB.append("单体最低电压大于阈值的概率：").append(numberTransform(obj7)).append(";");
                    }

                    Double obj8 = numberRate(temptureHResult[1], allNum);
                    boolean temptureH1 = a.isInTheInterval(String.valueOf(obj8), bmsRange.getTemptureHMin());
                    if(temptureH1){
                        recordEvaluteSB.append("单体最高温度; ");
                        evaluteBasisSB.append("单体最高温度小于阈值的概率：").append(numberTransform(obj8)).append(";");
                    }

                    Double obj9 = numberRate(temptureLResult[0], allNum);
                    boolean temptureL1 = a.isInTheInterval(String.valueOf(obj9), bmsRange.getTemptureLMin());
                    if(temptureL1){
                        recordEvaluteSB.append("单体最低温度; ");
                        evaluteBasisSB.append("单体最低温度大于阈值的概率：").append(numberTransform(obj9)).append(";");
                    }

                    //故障率
                    Double equipmentStatusNumber = getEquipmentStatusNumber(equipmentID, operatorID, allEquNumber);
                    String data_value10 = String.valueOf(equipmentStatusNumber);
                    boolean faultRate = a.isInTheInterval(data_value10, bmsRange.getFaultRateMin());
                    if(faultRate){
                        recordEvaluteSB.append("故障率; ");
                        evaluteBasisSB.append("故障率位于阈值范围内的概率：").append(numberTransform(equipmentStatusNumber)).append(";");
                        accordance="2";
                    }


                    //前三种风险
                    if (((tatalVoltage1 || tatalVoltage2) && (totalCurrentl || totalCurrent2) && (soc1 || soc2) && voltageH1 && voltageL1 && temptureH1 && temptureL1) || faultRate) {
                        if (i == 3) {
                            evaluteResult="低风险";
                            break;
                        } else if (i == 2) {
                            evaluteResult="一般风险";
                            break;
                        } else if (i == 1) {
                            evaluteResult="较大风险";
                            break;
                        }
                    }

                    //第四种风险
                    if (i == 0) {
                        Double obj10 = numberRate(voltageHResult[0], allNum);
                        boolean voltageH2 = a.isInTheInterval(String.valueOf(obj10), bmsRange.getVoltageHMax());
                        if(voltageH2){
                            recordEvaluteSB.append("单体最高电压; ");
                            evaluteBasisSB.append("单体最高电压大于阈值的概率：").append(numberTransform(obj10)).append(";");
                        }

                        Double obj11 = numberRate(voltageLResult[1], allNum);
                        boolean voltageL2 = a.isInTheInterval(String.valueOf(obj11), bmsRange.getVoltageLMax());
                        if(voltageL2){
                            recordEvaluteSB.append("单体最低电压; ");
                            evaluteBasisSB.append("单体最低电压小于阈值的概率：").append(numberTransform(obj11)).append(";");
                        }

                        Double obj12 = numberRate(temptureHResult[0], allNum);
                        boolean temptureH2 = a.isInTheInterval(String.valueOf(obj12), bmsRange.getTemptureHMax());
                        if(temptureH2){
                            recordEvaluteSB.append("单体最高温度; ");
                            evaluteBasisSB.append("单体最高温度大于阈值的概率：").append(numberTransform(obj12)).append(";");
                        }

                        Double obj13 = numberRate(temptureLResult[1], allNum);
                        boolean temptureL2 = a.isInTheInterval(String.valueOf(obj13), bmsRange.getTemptureLMax());
                        if(temptureL2){
                            recordEvaluteSB.append("单体最低温度; ");
                            evaluteBasisSB.append("单体最低温度小于阈值的概率：").append(numberTransform(obj13)).append(";");
                        }

                        if (((tatalVoltage1 || tatalVoltage2) && (totalCurrentl || totalCurrent2) && (soc1 || soc2) && (voltageH1 || voltageH2) && (voltageL1 || voltageL2) && (temptureH1 || temptureH2) && (temptureL1 || temptureL2)) || faultRate) {
                            evaluteResult="重大风险";
                            break;
                        }
                    }
                }

                String recordEvalute=""+recordEvaluteSB;//评估项
                String evaluteBasis=""+evaluteBasisSB;//评估依据
                if(!"2".equals(accordance)&&!"".equals(accordance))
                    accordance="1";

                bmsEvaluateResult.setAveId(accordance);
                bmsEvaluateResult.setId(SequenceId.getInstance().getId("bmsEvaluateId","",10));
                bmsEvaluateResult.setRecordEvalute(recordEvalute);
                bmsEvaluateResult.setEvaluteBasis(evaluteBasis);
                bmsEvaluateResult.setEvaluteResult(evaluteResult);
                bmsEvaluateResult.setEquipmentId(equipmentID);
                bmsEvaluateResult.setOperatorId(operatorID);
                bmsEvaluateResult.setInTime(new Date());
                bmsEvaluateResultDAO.insertSelective(bmsEvaluateResult);
            }
        }
    }

    //过程数据与阈值比对结果
    public Map<String, Object> getCompareResult(String operatorID, String equipmentID, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        //最新阈值信息
        BmsAveInfo bmsAveLastest = bmsAveInfoDAO.selectBmsAveLastest();
        Double tatalVoltageAve = bmsAveLastest.getTatalVoltageAve();//总电压
        Double totalCurrentAve = bmsAveLastest.getTotalCurrentAve();//总电流
        Integer socAve = bmsAveLastest.getSocAve();
        Double voltageHAve = bmsAveLastest.getVoltageHAve();
        Double voltageLAve = bmsAveLastest.getVoltageLAve();
        Integer temptureHAve = bmsAveLastest.getTemptureHAve();
        Integer temptureLAve = bmsAveLastest.getTemptureLAve();

        //1.所有过程数据情况
        List<BmsInfo> bms = bmsInfoDao.queryBmsAverageList(operatorID, equipmentID, beginTime, endTime);
        int allNum = bms.size();//总数（分母）
        int voltageNum1 = 0, voltageNum2 = 0;//总电压(大于，小于次数)
        int currentNum1 = 0, currentNum2 = 0;//总电流
        int socNum1 = 0, socNum2 = 0;//soc
        int voltageHNum1 = 0, voltageHNum2 = 0;//单体最高电压
        int voltageLNum1 = 0, voltageLNum2 = 0;//单体最低电压
        int temptureHNum1 = 0, temptureHNum2 = 0;//单体最高温度
        int temptureLNum1 = 0, temptureLNum2 = 0;//单体最低温度

        int[] voltageResult = null, currentResult = null, socResult = null, voltageHResult = null, voltageLResult = null, temptureHResult = null, temptureLResult = null;
        //荷电状态
        for (BmsInfo bm : bms) {
            voltageResult = compareMethod(bm.getTatalVoltage(), tatalVoltageAve, voltageNum1, voltageNum2);
            voltageNum1 = voltageResult[0];
            voltageNum2 = voltageResult[1];

            currentResult = compareMethod(bm.getTotalCurrent(), totalCurrentAve, currentNum1, currentNum2);
            currentNum1 = currentResult[0];
            currentNum2 = currentResult[1];

            socResult = compareMethod(bm.getSoc(), socAve, socNum1, socNum2);
            socNum1 = socResult[0];
            socNum2 = socResult[1];

            voltageHResult = compareMethod(bm.getVoltageH(), voltageHAve, voltageHNum1, voltageHNum2);
            voltageHNum1 = voltageHResult[0];
            voltageHNum2 = voltageHResult[1];

            voltageLResult = compareMethod(bm.getVoltageL(), voltageLAve, voltageLNum1, voltageLNum2);
            voltageLNum1 = voltageHResult[0];
            voltageLNum2 = voltageHResult[1];

            temptureHResult = compareMethod(bm.getTemptureH(), temptureHAve, temptureHNum1, temptureHNum2);
            temptureHNum1 = voltageHResult[0];
            temptureHNum2 = voltageHResult[1];

            temptureLResult = compareMethod(bm.getTemptureL(), temptureLAve, temptureLNum1, temptureLNum2);
            temptureLNum1 = voltageHResult[0];
            temptureLNum2 = voltageHResult[1];
        }
        map.put("allNum", allNum);
        map.put("voltageResult", voltageResult);
        map.put("currentResult", currentResult);
        map.put("socResult", socResult);
        map.put("voltageHResult", voltageHResult);
        map.put("voltageLResult", voltageLResult);
        map.put("temptureHResult", temptureHResult);
        map.put("temptureLResult", temptureLResult);
        return map;
    }

    //故障率 该桩故障次数/所有桩状态次数
    public Double getEquipmentStatusNumber(String equipmentId, String operatorId, int allNumber) {
        EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
        equipmentInfoShow.setAllowanceStatus("255");
        equipmentInfoShow.setEquipmentID(equipmentId);
        equipmentInfoShow.setOperatorID(operatorId);
        Integer equipmentStatusNumber = equipmentInfoDAO.getEquipmentStatusNumber(equipmentInfoShow);
        return numberRate(equipmentStatusNumber, allNumber);
    }


    //比较大小
    public static int[] compareMethod(Object number, Object theshold, int num1, int num2) {
        int[] arr = new int[]{num1, num2};
        String number2 = ObjectUtils.toString(number, "");
        String theshold2 = ObjectUtils.toString(theshold, "");

        if (StringUtils.isNotBlank(number2) && StringUtils.isNotBlank(theshold2)) {
            double dou = Double.parseDouble(number.toString());
            double thesholdDou = Double.parseDouble(theshold.toString());
            if (dou >= thesholdDou) {
                arr[0]++;//大于等于
            } else {
                arr[1]++;//小于
            }
        }
        return arr;
    }

    //两个数计算率
    public static Double numberRate(int num, int allNum) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return new Double(df.format((float) num / allNum));
    }

    //小数转百分数
    public static String numberTransform(Double littleNum) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(littleNum);
    }

    //获取时间差
    public static Double dayComparePrecise(Date fromDate,Date toDate){
        Calendar  from  =  Calendar.getInstance();
        from.setTime(fromDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(toDate);

        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_MONTH);
        Double year = Double.valueOf(toYear  -  fromYear);
        Double month = Double.valueOf(toMonth  - fromMonth);
        //int day = toDay  - fromDay;
        DecimalFormat df = new DecimalFormat("######0.0");
        return Double.valueOf(df.format(year + month / 12));
    }


}