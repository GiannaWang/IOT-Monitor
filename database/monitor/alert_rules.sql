-- 告警规则初始数据
-- 执行前请确保 alert_rules 表已创建（见 table.sql）
-- rulename 字段同时作为写入 alerts.alerttype 的值，需与前端 typeMap 保持一致

INSERT INTO alert_rules (rulename, description, rulecondition, sensortype, severity, locationid, enabled, createdbyuserid, createtime, updatetime) VALUES

-- 温度告警
('温度告警', '室内温度超过 28°C 时触发告警', '> 28', '温度', 'warning', NULL, 1, 1, NOW(), NOW()),

-- 湿度告警
('湿度告警', '室内湿度超过 75% 时触发告警', '> 75', '湿度', 'warning', NULL, 1, 1, NOW(), NOW()),

-- 设备离线告警（sensortype='device' 由调度器特殊处理，不用于传感器阈值检测）
('设备离线', '设备超过正常上报间隔 3 倍时间未上报数据时触发告警', 'offline', 'device', 'warning', NULL, 1, 1, NOW(), NOW()),

-- Windows 计算机资源告警
('CPU过载', 'Windows 主机 CPU 使用率超过 90% 时触发告警', '> 90', 'CPU使用率', 'warning', NULL, 1, 1, NOW(), NOW()),
('内存不足', 'Windows 主机内存使用率超过 90% 时触发告警', '> 90', '内存使用率', 'warning', NULL, 1, 1, NOW(), NOW()),
('磁盘告警', 'Windows 主机磁盘使用率超过 85% 时触发告警', '> 85', '磁盘使用率', 'warning', NULL, 1, 1, NOW(), NOW());
