-- 告警规则初始数据
-- 执行前请确保 alert_rules 表已创建（见 table.sql）
-- rulename 字段同时作为写入 alerts.alerttype 的值，需与前端 typeMap 保持一致

INSERT INTO alert_rules (rulename, description, rulecondition, sensortype, severity, createtime) VALUES

-- 温度告警
('高温',     '室内温度超过28°C，请检查空调设备',         '> 28', '温度', 'warning',  NOW()),
('高温',     '室内温度超过35°C，存在安全风险，需立即处理', '> 35', '温度', 'critical', NOW()),
('低温',     '室内温度低于10°C，请检查暖气设备',         '< 10', '温度', 'warning',  NOW()),

-- 湿度告警
('湿度异常', '室内湿度超过75%，存在发霉和电器受损风险',   '> 75', '湿度', 'warning',  NOW()),
('湿度异常', '室内湿度低于25%，空气过于干燥',            '< 25', '湿度', 'warning',  NOW()),

-- 设备离线告警（sensortype='device' 由调度器特殊处理，不用于传感器阈值检测）
('设备离线', '设备超过正常上报间隔3倍时间未上报数据',     'offline', 'device', 'warning', NOW()),

-- Windows 计算机资源告警
('CPU过载',   'Windows 主机 CPU 使用率超过 90%，请排查高占用进程', '> 90', 'CPU使用率',  'warning',  NOW()),
('内存不足',  'Windows 主机内存使用率超过 90%，存在内存溢出风险',  '> 90', '内存使用率', 'warning',  NOW()),
('磁盘告警',  'Windows 主机磁盘使用率超过 85%，请清理磁盘空间',   '> 85', '磁盘使用率', 'warning',  NOW());
