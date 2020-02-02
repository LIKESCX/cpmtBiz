package com.cpit.cpmt.biz.utils.exchange;

public class Consts {
	public static String station_Charge_stats_id ="stationChargeStatsId";
	public static String station_Discharge_stats_id ="stationDischargeStatsId";
	
	//public static final String INTERFACE_VERSION = "V1.0";   //接口版本号
	public static final String INTERFACE_VERSIONV1 = "V1.0";   //接口版本号
	public static final String INTERFACE_VERSIONV2 = "V2.0";   //接口版本号
	public static final String INTERFACE_VERSIONV3 = "V3.0";   //接口版本号
	
	//接口类型
	public static final int NOTIFICATION_STATIONINFO = 1;   //9.2   充电站信息变化推送接口名称
	public static final int NOTIFICATION_STATIONSTATUS = 2; //9.4   设备状态变化推送接口名称
	public static final int NOTIFICATION_BMSINFO = 3;		//9.9　过程信息推送接口名称
	public static final int NOTIFICATION_ALARMINFO = 4;     //9.11  告警信息推送接口名称
	public static final int NOTIFICATION_EVENTINFO = 5;     //9.13　事件信息推送接口名称
	public static final int QUERY_STATION_CHARGE_STATS = 6;     //9.6
	public static final int QUERY_STATION_DISCHARGE_STATS = 7;     //9.7
	public static final int QUERY_STATION_INFO = 8;     //9.3
	public static final int QUERY_STATION_STATUS = 9;     //9.5
	public static final int QUERY_BMS_INFO = 10;//9.8
	public static final int QUERY_ALARM_INFO = 11;//9.10
	public static final int QUERY_EVENT_INFO = 12;//9.12
	public static final int QUERY_DISEQUIPMENTSTATUS_INFO = 13;//9.15
	
	//接口名称
	public static final String NOTIFICATION_STATIONINFO_NAME = "notification_stationInfo";   //9.2   充电站信息变化推送接口名称
	public static final String NOTIFICATION_STATIONSTATUS_NAME = "notification_stationStatus";  //9.4   设备状态变化推送接口名称
	public static final String NOTIFICATION_BMSINFO_NAME = "notification_bmsInfo";		  //9.9　过程信息推送接口名称
	public static final String NOTIFICATION_ALARMINFO_NAME = "notification_alarmInfo";       //9.11  告警信息推送接口名称
	public static final String NOTIFICATION_EVENTINFO_NAME = "notification_eventInfo";       //9.13　事件信息推送接口名称
	
	public static String storage_Result_OK="0";
	public static String storage_Result_fail="1";
	public static String validate_Res_OK ="0";
	public static String validate_Res_fail ="1";

	//告警级别
	public static final String PERSONAL_SAFETY_LEVEL_1="1";//人身安全级
	public static final String EQUIPMENT_SAFETY_LEVEL_2="2";//设备安全级
	public static final String ALARM_PROMPT_LEVEL_3="3";//告警提示级

}
