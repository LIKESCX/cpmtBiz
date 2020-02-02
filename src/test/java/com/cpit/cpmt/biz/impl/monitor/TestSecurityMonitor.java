package com.cpit.cpmt.biz.impl.monitor;

import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorHistoryPowerInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.basic.ConnectorHistoryPowerInfo;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestSecurityMonitor {
    @Autowired
    private SecurityMonitorMgmt securityMonitorMgmt;

    @Autowired
    private ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;

    @Autowired
    private RabbitMsgSender rabbitMsgSender;

    @Autowired
    private StationEvaluateResultMgmt stationEvaluateResultMgmt;

    @Autowired
    private StationInfoMgmt stationInfoMgmt;

    @Test
    public void selectBigScreenChargeInfo(){
        StationInfoShow stationInfoShow = stationInfoMgmt.selectBigScreenChargeInfo();
        System.out.println(stationInfoShow.getAlarmStatus());
        System.out.println(stationInfoShow.getStationLng());
        System.out.println(stationInfoShow.getHours());
    }

    //充电站对比结果
    @Test
    public void stationResult(){
        stationEvaluateResultMgmt.getStationRiskResult();
    }

    @Test
    public void testWebSocket(){
        rabbitMsgSender.sendConnectorStatus("connectorStatus");
    }

    @Test
    public void insert(){
        securityMonitorMgmt.getResultByCharger();
    }

    @Test
    public void getResult(){
        securityMonitorMgmt.queryBmsAverageList();
    }

    @Test
    public void onnectorHistoryPowerInfo(){
        StationInfoShow station = new StationInfoShow();
        List<String> areaCodeList = new ArrayList<>();
        areaCodeList.add("370212");
        station.setAreaCodeList(areaCodeList);
        List<ConnectorHistoryPowerInfo> connectorHistoryPowerInfos = connectorHistoryPowerInfoDao.selectPowerTenMinutes(station);
        for (ConnectorHistoryPowerInfo connectorHistoryPowerInfo : connectorHistoryPowerInfos) {
            System.out.println(connectorHistoryPowerInfo);
        }
    }
}
