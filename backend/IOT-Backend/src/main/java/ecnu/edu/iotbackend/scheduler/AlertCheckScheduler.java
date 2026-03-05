package ecnu.edu.iotbackend.scheduler;

import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.entity.AlertRule;
import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.entity.SensorDevice;
import ecnu.edu.iotbackend.mapper.AlertMapper;
import ecnu.edu.iotbackend.mapper.AlertRuleMapper;
import ecnu.edu.iotbackend.mapper.SensorDataMapper;
import ecnu.edu.iotbackend.mapper.SensorDeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 告警检测定时任务
 * 每5分钟扫描一次传感器数据，与 alert_rules 对比，超阈值则生成告警记录
 */
@Component
public class AlertCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AlertCheckScheduler.class);

    @Autowired
    private AlertRuleMapper alertRuleMapper;

    @Autowired
    private SensorDeviceMapper sensorDeviceMapper;

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Autowired
    private AlertMapper alertMapper;

    /**
     * 每5分钟执行一次告警检测
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void checkAlerts() {
        logger.info("=== 开始告警检测 ===");

        List<AlertRule> rules = alertRuleMapper.getAllRules();
        if (rules.isEmpty()) {
            logger.warn("告警规则表为空，跳过检测");
            return;
        }

        List<SensorDevice> devices = sensorDeviceMapper.getAllDevices();
        if (devices.isEmpty()) {
            logger.info("暂无设备，跳过告警检测");
            return;
        }

        int alertCount = 0;
        for (AlertRule rule : rules) {
            if ("device".equals(rule.getSensorType())) {
                alertCount += checkDeviceOffline(rule, devices);
            } else {
                alertCount += checkSensorThreshold(rule, devices);
            }
        }

        logger.info("=== 告警检测完成，本次新增告警: {} 条 ===", alertCount);
    }

    /**
     * 检测传感器数据是否超阈值
     */
    private int checkSensorThreshold(AlertRule rule, List<SensorDevice> devices) {
        int count = 0;
        for (SensorDevice device : devices) {
            // 获取该设备对应类型的最新数据
            SensorData latest = sensorDataMapper.getLatestDataByDeviceAndType(
                    device.getId(), rule.getSensorType());
            if (latest == null) continue;

            if (isThresholdExceeded(latest.getValue(), rule.getRuleCondition())) {
                // 防重复：1小时内同设备同类型未处理告警已存在则跳过
                if (alertMapper.countRecentUnhandledAlerts(device.getDeviceid(), rule.getRuleName()) > 0) {
                    continue;
                }

                Alert alert = buildSensorAlert(device, rule, latest.getValue());
                alertMapper.insertAlert(alert);
                logger.warn("【告警】{} - 设备: {}, 值: {}{}, 规则: {}",
                        rule.getRuleName(), device.getDevicename(),
                        latest.getValue(), getSensorUnit(rule.getSensorType()),
                        rule.getRuleCondition());
                count++;
            }
        }
        return count;
    }

    /**
     * 检测设备是否离线（超过上报间隔×3未上报）
     */
    private int checkDeviceOffline(AlertRule rule, List<SensorDevice> devices) {
        int count = 0;
        for (SensorDevice device : devices) {
            if (device.getTimestamp() == null || device.getDatareportinterval() == null) continue;

            long secondsSinceLastReport = ChronoUnit.SECONDS.between(
                    device.getTimestamp(), LocalDateTime.now());
            long offlineThreshold = (long) device.getDatareportinterval() * 3;

            if (secondsSinceLastReport > offlineThreshold) {
                if (alertMapper.countRecentUnhandledAlerts(device.getDeviceid(), "设备离线") > 0) {
                    continue;
                }

                Alert alert = buildOfflineAlert(device, rule, secondsSinceLastReport);
                alertMapper.insertAlert(alert);
                logger.warn("【设备离线】设备: {}, 已 {}秒 未上报（阈值: {}秒）",
                        device.getDevicename(), secondsSinceLastReport, offlineThreshold);
                count++;
            }
        }
        return count;
    }

    // ===== 构建告警对象 =====

    private Alert buildSensorAlert(SensorDevice device, AlertRule rule, float value) {
        String unit = getSensorUnit(rule.getSensorType());
        String condition = rule.getRuleCondition().trim();
        String message = String.format("设备【%s】的%s为 %.1f%s，触发规则：%s %s",
                device.getDevicename(), rule.getSensorType(), value, unit,
                rule.getSensorType(), condition);

        Alert alert = new Alert();
        alert.setDeviceId(device.getDeviceid());
        alert.setLocationId(device.getLocationid());
        alert.setRuleId(rule.getId());
        alert.setAlertType(rule.getRuleName());
        alert.setSeverity(rule.getSeverity());
        alert.setMessage(message);
        alert.setTimestamp(LocalDateTime.now());
        alert.setHandled((byte) 0);
        return alert;
    }

    private Alert buildOfflineAlert(SensorDevice device, AlertRule rule, long secondsSinceLastReport) {
        long minutes = secondsSinceLastReport / 60;
        String message = String.format("设备【%s】已超过 %d 分钟未上报数据（上报间隔：%d 秒）",
                device.getDevicename(), minutes, device.getDatareportinterval());

        Alert alert = new Alert();
        alert.setDeviceId(device.getDeviceid());
        alert.setLocationId(device.getLocationid());
        alert.setRuleId(rule.getId());
        alert.setAlertType("设备离线");
        alert.setSeverity(rule.getSeverity());
        alert.setMessage(message);
        alert.setTimestamp(LocalDateTime.now());
        alert.setHandled((byte) 0);
        return alert;
    }

    // ===== 工具方法 =====

    /**
     * 解析规则条件并判断是否超阈值
     * 支持格式："> 28" / "< 10" / ">= 30" / "<= 25"
     */
    private boolean isThresholdExceeded(float value, String condition) {
        condition = condition.trim();
        try {
            if (condition.startsWith(">= ")) {
                return value >= Double.parseDouble(condition.substring(3));
            } else if (condition.startsWith("<= ")) {
                return value <= Double.parseDouble(condition.substring(3));
            } else if (condition.startsWith("> ")) {
                return value > Double.parseDouble(condition.substring(2));
            } else if (condition.startsWith("< ")) {
                return value < Double.parseDouble(condition.substring(2));
            }
        } catch (NumberFormatException e) {
            logger.error("告警规则条件解析失败: '{}', 跳过该规则", condition);
        }
        return false;
    }

    private String getSensorUnit(String sensorType) {
        switch (sensorType) {
            case "温度": return "°C";
            case "湿度":
            case "CPU使用率":
            case "内存使用率":
            case "磁盘使用率": return "%";
            case "CO2": return "ppm";
            default: return "";
        }
    }
}
