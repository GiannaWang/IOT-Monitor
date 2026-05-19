ALTER TABLE `alert_rules`
  ADD COLUMN IF NOT EXISTS `locationid` int NULL AFTER `severity`,
  ADD COLUMN IF NOT EXISTS `enabled` tinyint NOT NULL DEFAULT 1 AFTER `locationid`,
  ADD COLUMN IF NOT EXISTS `createdbyuserid` int NULL AFTER `enabled`,
  ADD COLUMN IF NOT EXISTS `updatetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `createtime`;

UPDATE `alert_rules`
SET `updatetime` = COALESCE(`updatetime`, `createtime`, NOW());

ALTER TABLE `alert_rules`
  ADD INDEX `idx_alert_rules_location` (`locationid`),
  ADD INDEX `idx_alert_rules_sensor` (`sensortype`);

ALTER TABLE `alert_rules`
  ADD CONSTRAINT `fk_alert_rules_location` FOREIGN KEY (`locationid`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_alert_rules_creator` FOREIGN KEY (`createdbyuserid`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT;
