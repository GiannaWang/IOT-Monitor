CREATE TABLE `alert_rules`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '规则唯一标识',
  `rulename` varchar(50) NOT NULL COMMENT '规则名称',
  `description` text NULL COMMENT '规则描述',
  `rulecondition` varchar(255) NOT NULL COMMENT '触发条件',
  `sensortype` varchar(20) NOT NULL COMMENT '适用传感器类型',
  `severity` varchar(255) NOT NULL COMMENT '警告级别',
  `createtime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
);

CREATE TABLE `alerts`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '告警记录唯一标识',
  `deviceid` varchar(50) NOT NULL COMMENT '触发告警的编号',
  `locationid` int NOT NULL COMMENT '告警位置',
  `ruleid` int NOT NULL COMMENT '触发告警的id',
  `alerttype` varchar(50) NOT NULL COMMENT '告警类型',
  `severity` varchar(50) NOT NULL COMMENT '告警级别',
  `message` text NOT NULL COMMENT '告警详情描述',
  `timestamp` datetime NOT NULL COMMENT '告警时间戳',
  `handled` tinyint NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `locations`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '位置编号',
  `buildingname` varchar(50) NOT NULL COMMENT '楼宇名称',
  `floornumber` int NOT NULL COMMENT '楼层',
  `roomnumber` int NOT NULL COMMENT '房间号',
  `description` varchar(255) NULL COMMENT '位置描述',
  PRIMARY KEY (`id`)
);

CREATE TABLE `sensor_datas`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '数据记录唯一标识',
  `deviceid` int NOT NULL COMMENT '设备编号',
  `datatype` varchar(50) NOT NULL COMMENT '数据类型',
  `value` decimal(10, 2) NOT NULL COMMENT '数据值',
  `unit` varchar(10) NOT NULL COMMENT '数据单位',
  `timestamp` datetime NOT NULL COMMENT '数据采集时间戳',
  `status` varchar(10) NOT NULL COMMENT '数据状态',
  PRIMARY KEY (`id`)
);

CREATE TABLE `sensor_devices`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `devicename` varchar(50) NOT NULL COMMENT '设备名称',
  `deviceid` varchar(50) NOT NULL COMMENT '设备编号',
  `sensortype` varchar(50) NOT NULL COMMENT '传感器类型',
  `source` varchar(50) NOT NULL COMMENT '来源',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `installtime` datetime NOT NULL COMMENT '安装时间',
  `batterylevel` int NULL COMMENT '剩余电量',
  `timestamp` datetime NOT NULL COMMENT '状态上报时间',
  `datareportinterval` int NOT NULL COMMENT '数据上报频率',
  `locationid` int NOT NULL COMMENT '设备位置',
  PRIMARY KEY (`id`)
);

CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` varchar(50) NOT NULL COMMENT '登录用户名',
  `password_hash` varchar(255) NOT NULL COMMENT '密码哈希值',
  `role` varchar(50) NOT NULL COMMENT '角色',
	`avatar` varchar(50) NOT NULL COMMENT '头像',
	`lastLoginTime` varchar(50) NOT NULL COMMENT '上次登录时间',
	`room` varchar(50) NOT NULL COMMENT '管理房间',
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `uk_username`(`username`)
);

ALTER TABLE `alerts` ADD CONSTRAINT `fk_alerts_device` FOREIGN KEY (`deviceid`) REFERENCES `sensor_devices` (`deviceid`) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE `alerts` ADD CONSTRAINT `fk_alerts_location` FOREIGN KEY (`deviceid`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE `alerts` ADD CONSTRAINT `fk_alerts_rule` FOREIGN KEY (`ruleid`) REFERENCES `alert_rules` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE `sensor_datas` ADD CONSTRAINT `fk_sensor_data_device` FOREIGN KEY (`deviceid`) REFERENCES `sensor_devices` (`deviceid`) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE `sensor_devices` ADD CONSTRAINT `fk_sensordevices_location` FOREIGN KEY (`locationid`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

