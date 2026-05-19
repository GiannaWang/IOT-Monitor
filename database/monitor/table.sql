CREATE TABLE `alert_rules` (
  `id` int NOT NULL AUTO_INCREMENT,
  `rulename` varchar(50) NOT NULL,
  `description` text NULL,
  `rulecondition` varchar(255) NOT NULL,
  `sensortype` varchar(20) NOT NULL,
  `severity` varchar(255) NOT NULL,
  `locationid` int NULL,
  `enabled` tinyint NOT NULL DEFAULT 1,
  `createdbyuserid` int NULL,
  `createtime` datetime NOT NULL,
  `updatetime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_alert_rules_location` (`locationid`),
  KEY `idx_alert_rules_sensor` (`sensortype`)
);

CREATE TABLE `alerts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deviceid` varchar(50) NOT NULL,
  `locationid` int NOT NULL,
  `ruleid` int NOT NULL,
  `alerttype` varchar(50) NOT NULL,
  `severity` varchar(50) NOT NULL,
  `message` text NOT NULL,
  `timestamp` datetime NOT NULL,
  `handled` tinyint NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `locations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `buildingname` varchar(50) NOT NULL,
  `floornumber` int NOT NULL,
  `roomnumber` int NOT NULL,
  `description` varchar(255) NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `sensor_datas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deviceid` int NOT NULL,
  `datatype` varchar(50) NOT NULL,
  `value` decimal(10, 2) NOT NULL,
  `unit` varchar(10) NOT NULL,
  `timestamp` datetime NOT NULL,
  `status` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `sensor_devices` (
  `id` int NOT NULL AUTO_INCREMENT,
  `devicename` varchar(50) NOT NULL,
  `deviceid` varchar(50) NOT NULL,
  `sensortype` varchar(50) NOT NULL,
  `source` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL,
  `installtime` datetime NOT NULL,
  `batterylevel` int NULL,
  `timestamp` datetime NOT NULL,
  `datareportinterval` int NOT NULL,
  `locationid` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_deviceid` (`deviceid`)
);

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `avatar` varchar(255) NOT NULL,
  `lastLoginTime` varchar(50) NOT NULL,
  `room` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
);

CREATE TABLE `user_room_assignments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `location_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_location` (`user_id`, `location_id`),
  KEY `idx_user_room_user_id` (`user_id`),
  KEY `idx_user_room_location_id` (`location_id`)
);

ALTER TABLE `alerts`
  ADD CONSTRAINT `fk_alerts_device` FOREIGN KEY (`deviceid`) REFERENCES `sensor_devices` (`deviceid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_alerts_location` FOREIGN KEY (`locationid`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_alerts_rule` FOREIGN KEY (`ruleid`) REFERENCES `alert_rules` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `alert_rules`
  ADD CONSTRAINT `fk_alert_rules_location` FOREIGN KEY (`locationid`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_alert_rules_creator` FOREIGN KEY (`createdbyuserid`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT;

ALTER TABLE `sensor_devices`
  ADD CONSTRAINT `fk_sensordevices_location` FOREIGN KEY (`locationid`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `user_room_assignments`
  ADD CONSTRAINT `fk_user_room_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_user_room_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;
