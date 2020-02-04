CREATE TABLE `ana_bms_single_charge` (
  `operator_id` varchar(9) NOT NULL COMMENT '运营商唯一id',
  `station_id` varchar(20) NOT NULL COMMENT '充电站id',
  `equipment_id` varchar(23) NOT NULL COMMENT '设备唯一编码',
  `connector_id` varchar(30) NOT NULL COMMENT '设备接口编码',
  `bms_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS编码',
  `bms_ver` varchar(50) DEFAULT '' COMMENT 'BMS版本',
  `esti_r` int(10) DEFAULT NULL COMMENT '电池内阻估算',
  `remain_capacity` int(10) DEFAULT NULL COMMENT '电池剩余容量',
  `charge_time` int(9) DEFAULT NULL COMMENT '充电时间长度，单位秒',
  `sOH` int(9) DEFAULT NULL COMMENT '电池健康度',
  `remark_1` varchar(10) DEFAULT NULL COMMENT '综合评估',
  `soc` int(10) DEFAULT NULL COMMENT '荷电状态',
  `voltage_h` float(20,7) NOT NULL DEFAULT '0.0000000' COMMENT '单体最高电压',
  `voltage_l` float(20,7) NOT NULL DEFAULT '0.0000000' COMMENT '单体最低电压',
  `tempture_h` int(10) NOT NULL DEFAULT '0' COMMENT '单体最高温度',
  `tempture_l` varchar(10) NOT NULL DEFAULT '0' COMMENT '单体最低温度',
  `beforeSoc` varchar(10) DEFAULT NULL COMMENT '充电前soc值',
  `afterSoc` varchar(10) DEFAULT NULL COMMENT '充电后soc值',
  `startTime` datetime NOT NULL COMMENT '开始时间',
  `endTime` datetime NOT NULL COMMENT '结束时间',
  `statistical_date` char(10) NOT NULL COMMENT '对应的日期(以结束时间为准)',
  `statistical_week` char(10) NOT NULL COMMENT '对应的日期所在的周日(以结束时间为准)',
  `statistical_month` char(7) NOT NULL COMMENT '对应的月份(以结束时间为准)',
  `statistical_season` char(7) NOT NULL COMMENT '对应的季度(以结束时间为准)',
  KEY `operator_id` (`operator_id`),
  KEY `station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`),
  KEY `startTime` (`startTime`),
  KEY `endTime` (`endTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单次充电过程分析结果表';

CREATE TABLE `ana_bms_day_charge` (
  `operator_id` varchar(9) NOT NULL COMMENT '运营商唯一id',
  `station_id` varchar(20) NOT NULL COMMENT '充电站id',
  `equipment_id` varchar(23) NOT NULL COMMENT '设备唯一编码',
  `connector_id` varchar(30) NOT NULL COMMENT '设备接口编码',
  `bms_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS编码',
  `bms_ver` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS版本',
  `statistics_times` int(10) DEFAULT NULL COMMENT '统计次数',
  `statistical_day` char(10) NOT NULL COMMENT '对应的日期(以结束时间为准)',
  PRIMARY KEY (`bms_code`,`statistical_day`),
  KEY `operator_id` (`operator_id`),
  KEY `station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`),
  KEY `statistical_day` (`statistical_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='按天统计分析结果表';

CREATE TABLE `ana_bms_week_charge` (
  `operator_id` varchar(9) NOT NULL COMMENT '运营商唯一id',
  `station_id` varchar(20) NOT NULL COMMENT '充电站id',
  `equipment_id` varchar(23) NOT NULL COMMENT '设备唯一编码',
  `connector_id` varchar(30) NOT NULL COMMENT '设备接口编码',
  `bms_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS编码',
  `bms_ver` varchar(50) DEFAULT '' COMMENT 'BMS版本',
  `statistics_times` int(10) DEFAULT NULL COMMENT '统计次数',
  `statistical_week` char(10) NOT NULL COMMENT '对应的日期所在的周日(以结束时间为准)',
  PRIMARY KEY (`bms_code`,`operator_id`,`connector_id`,`statistical_week`),
  KEY `operator_id` (`operator_id`),
  KEY `station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`),
  KEY `statistical_week` (`statistical_week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='按周统计分析结果表';

CREATE TABLE `ana_bms_month_charge` (
  `operator_id` varchar(9) NOT NULL COMMENT '运营商唯一id',
  `station_id` varchar(20) NOT NULL COMMENT '充电站id',
  `equipment_id` varchar(23) NOT NULL COMMENT '设备唯一编码',
  `connector_id` varchar(30) NOT NULL COMMENT '设备接口编码',
  `bms_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS编码',
  `bms_ver` varchar(50) DEFAULT '' COMMENT 'BMS版本',
  `statistics_times` int(10) DEFAULT NULL COMMENT '统计次数',
  `statistical_month` char(7) NOT NULL COMMENT '对应的月份(以结束时间为准)',
  PRIMARY KEY (`bms_code`,`operator_id`,`connector_id`,`statistical_month`),
  KEY `operator_id` (`operator_id`),
  KEY ` station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`),
  KEY `statistical_month` (`statistical_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='按月份统计过程信息结果表';

CREATE TABLE `ana_bms_season_charge` (
  `operator_id` varchar(9) NOT NULL COMMENT '运营商唯一id',
  `station_id` varchar(20) NOT NULL COMMENT '充电站id',
  `equipment_id` varchar(23) NOT NULL COMMENT '设备唯一编码',
  `connector_id` varchar(30) NOT NULL COMMENT '设备接口编码',
  `station_street` varchar(50) DEFAULT NULL COMMENT '充电站街道',
  `bms_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS编码',
  `bms_ver` varchar(50) DEFAULT '' COMMENT 'BMS版本',
  `statistics_times` int(10) DEFAULT NULL COMMENT '统计次数',
  `statistical_season` char(7) NOT NULL COMMENT '对应的季度(以结束时间为准)',
  PRIMARY KEY (`bms_code`,`operator_id`,`connector_id`,`statistical_season`),
  KEY `operator_id` (`operator_id`),
  KEY `station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`),
  KEY `statistical_season` (`statistical_season`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='按季度统计过程信息结果表';

CREATE TABLE `ana_bms_year_charge` (
  `operator_id` varchar(9) NOT NULL COMMENT '运营商唯一id',
  `station_id` varchar(20) NOT NULL COMMENT '充电站id',
  `equipment_id` varchar(23) NOT NULL COMMENT '设备唯一编码',
  `connector_id` varchar(30) NOT NULL COMMENT '设备接口编码',
  `station_street` varchar(50) DEFAULT NULL COMMENT '充电站街道',
  `bms_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS编码',
  `bms_ver` varchar(50) DEFAULT '' COMMENT 'BMS版本',
  `statistics_times` int(10) DEFAULT NULL COMMENT '统计次数',
  `statistical_year` char(4) NOT NULL COMMENT '对应的季度(以结束时间为准)',
  PRIMARY KEY (`bms_code`,`operator_id`,`connector_id`,`statistical_year`),
  KEY `operator_id` (`operator_id`),
  KEY `station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`),
  KEY `statistical_season` (`statistical_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='按季度统计过程信息结果表';

CREATE TABLE `ana_bms_single_charge_warning_result` (
  `operator_id` varchar(9) NOT NULL COMMENT '运营商唯一id',
  `station_id` varchar(20) NOT NULL COMMENT '充电站id',
  `equipment_id` varchar(23) NOT NULL COMMENT '设备唯一编码',
  `connector_id` varchar(30) NOT NULL COMMENT '设备接口编码',
  `bMS_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'BMS编码',
  `bMS_ver` varchar(50) DEFAULT '' COMMENT 'BMS版本',
  `warning_code` int(9) DEFAULT NULL COMMENT '预警代码',
  `warning_desc` varchar(50) DEFAULT NULL COMMENT '预警描述',
  `warning_level` int(9) DEFAULT NULL COMMENT '预警等级',
  `startTime` datetime NOT NULL COMMENT '开始时间',
  `endTime` datetime NOT NULL COMMENT '结束时间',
  `statistical_date` char(10) NOT NULL COMMENT '对应的日期(以结束时间为准)',
  `statistical_week` char(10) NOT NULL COMMENT '对应的日期所在的周日(以结束时间为准)',
  `statistical_month` char(7) NOT NULL COMMENT '对应的月份(以结束时间为准)',
  `statistical_season` char(7) NOT NULL COMMENT '对应的季度(以结束时间为准)',
  KEY `operator_id` (`operator_id`),
  KEY `station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单次充电过程信息异常数据库结果表';

CREATE TABLE `ana_fault_knowledge base` (
  `base_id` varchar(9) NOT NULL COMMENT '序号id',
  `event_name` varchar(50) NOT NULL COMMENT '事件名称',
  `warning_status` int(2) NOT NULL COMMENT '告警状态 1已处理，2待处理，3无法处理，4其他',
  `warning_type` int(2) NOT NULL COMMENT '故障告警类型 1充电系统故障，2电池系统故障，3配电系统故障',
  `warning_level` int(2) NOT NULL COMMENT '告警级别分类 1人身安全级，2设备安全级，3告警提示级',
  `warning_time` datetime NOT NULL COMMENT '发生时间',
  `reporting_time` datetime NOT NULL COMMENT '上报时间',
  `reporting_person` varchar(10) NOT NULL COMMENT '上报人',
  `processed_time` datetime NOT NULL COMMENT '处理完成时间',
  `processe_desc` varchar(50) NOT NULL COMMENT '处理过程',
  `processe_result` varchar(50) NOT NULL COMMENT '处理结果',
  `processe_person` varchar(10) NOT NULL COMMENT '处理人',
  `is_risk` int(1) NOT NULL COMMENT '风险是否已消除 1 是 2 否',
  `appendix` varchar(255) DEFAULT NULL COMMENT '风附件',
  PRIMARY KEY (`base_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='故障知识库表';

