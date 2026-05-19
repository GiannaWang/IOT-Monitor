package ecnu.edu.iotbackend.scheduler;

import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.entity.AlertRule;
import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.entity.SensorDevice;
import ecnu.edu.iotbackend.mapper.AlertMapper;
import ecnu.edu.iotbackend.mapper.SensorDataMapper;
import ecnu.edu.iotbackend.mapper.SensorDeviceMapper;
import ecnu.edu.iotbackend.service.AlertRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AlertCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AlertCheckScheduler.class);

    private static final String SENSOR_TEMPERATURE = "\u6e29\u5ea6";
    private static final String SENSOR_HUMIDITY = "\u6e7f\u5ea6";
    private static final String SENSOR_CPU = "CPU\u4f7f\u7528\u7387";
    private static final String SENSOR_MEMORY = "\u5185\u5b58\u4f7f\u7528\u7387";
    private static final String SENSOR_DISK = "\u78c1\u76d8\u4f7f\u7528\u7387";
    private static final String SENSOR_DEVICE = "device";
    private static final String ALERT_DEVICE_OFFLINE = "\u8bbe\u5907\u79bb\u7ebf";

    @Autowired
    private AlertRuleService alertRuleService;

    @Autowired
    private SensorDeviceMapper sensorDeviceMapper;

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Autowired
    private AlertMapper alertMapper;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void checkAlerts() {
        logger.info("=== Start alert check ===");

        List<AlertRule> rules = alertRuleService.getEnabledRulesForScheduling();
        if (rules.isEmpty()) {
            logger.warn("Skip alert check because no enabled rules were found");
            return;
        }

        List<SensorDevice> devices = sensorDeviceMapper.getAllDevices();
        if (devices.isEmpty()) {
            logger.info("Skip alert check because no devices were found");
            return;
        }

        Map<String, AlertRule> globalRules = new HashMap<>();
        Map<Integer, Map<String, AlertRule>> roomRules = new HashMap<>();
        List<String> sensorTypes = new ArrayList<>();

        for (AlertRule rule : rules) {
            String sensorType = rule.getSensorType();
            if (SENSOR_DEVICE.equals(sensorType)) {
                putRule(globalRules, roomRules, rule, sensorType);
                continue;
            }

            if (!sensorTypes.contains(sensorType)) {
                sensorTypes.add(sensorType);
            }
            putRule(globalRules, roomRules, rule, sensorType);
        }

        int alertCount = 0;
        for (SensorDevice device : devices) {
            alertCount += checkDeviceOffline(resolveRule(device.getLocationid(), SENSOR_DEVICE, roomRules, globalRules), device);
            for (String sensorType : sensorTypes) {
                alertCount += checkSensorThreshold(resolveRule(device.getLocationid(), sensorType, roomRules, globalRules), device);
            }
        }

        logger.info("=== Alert check complete, created {} new alerts ===", alertCount);
    }

    private void putRule(Map<String, AlertRule> globalRules,
                         Map<Integer, Map<String, AlertRule>> roomRules,
                         AlertRule rule,
                         String sensorType) {
        if (rule.getLocationId() == null) {
            globalRules.put(sensorType, rule);
        } else {
            roomRules.computeIfAbsent(rule.getLocationId(), key -> new HashMap<>()).put(sensorType, rule);
        }
    }

    private int checkSensorThreshold(AlertRule rule, SensorDevice device) {
        if (rule == null) {
            return 0;
        }

        SensorData latest = sensorDataMapper.getLatestDataByDeviceAndType(device.getId(), rule.getSensorType());
        if (latest == null) {
            return 0;
        }

        if (!isThresholdExceeded(latest.getValue(), rule.getRuleCondition())) {
            return 0;
        }

        if (alertMapper.countRecentUnhandledAlerts(device.getDeviceid(), rule.getRuleName()) > 0) {
            return 0;
        }

        Alert alert = buildSensorAlert(device, rule, latest.getValue());
        alertMapper.insertAlert(alert);
        logger.warn("Alert triggered: rule={}, device={}, value={}{} condition={}",
                rule.getRuleName(), device.getDevicename(), latest.getValue(),
                getSensorUnit(rule.getSensorType()), rule.getRuleCondition());
        return 1;
    }

    private int checkDeviceOffline(AlertRule rule, SensorDevice device) {
        if (rule == null || device.getTimestamp() == null || device.getDatareportinterval() == null) {
            return 0;
        }

        long secondsSinceLastReport = ChronoUnit.SECONDS.between(device.getTimestamp(), LocalDateTime.now());
        long offlineThreshold = (long) device.getDatareportinterval() * 3;
        if (secondsSinceLastReport <= offlineThreshold) {
            return 0;
        }

        if (alertMapper.countRecentUnhandledAlerts(device.getDeviceid(), ALERT_DEVICE_OFFLINE) > 0) {
            return 0;
        }

        Alert alert = buildOfflineAlert(device, rule, secondsSinceLastReport);
        alertMapper.insertAlert(alert);
        logger.warn("Device offline: device={}, elapsed={}s threshold={}s",
                device.getDevicename(), secondsSinceLastReport, offlineThreshold);
        return 1;
    }

    private AlertRule resolveRule(Integer locationId, String sensorType,
                                  Map<Integer, Map<String, AlertRule>> roomRules,
                                  Map<String, AlertRule> globalRules) {
        Map<String, AlertRule> scopedRules = roomRules.get(locationId);
        if (scopedRules != null && scopedRules.containsKey(sensorType)) {
            return scopedRules.get(sensorType);
        }
        return globalRules.get(sensorType);
    }

    private Alert buildSensorAlert(SensorDevice device, AlertRule rule, float value) {
        String unit = getSensorUnit(rule.getSensorType());
        String condition = rule.getRuleCondition().trim();
        String message = String.format("\u8bbe\u5907\u3010%s\u3011\u7684%s\u4e3a %.1f%s\uff0c\u89e6\u53d1\u89c4\u5219\uff1a%s %s",
                device.getDevicename(), rule.getSensorType(), value, unit, rule.getSensorType(), condition);

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
        String message = String.format("\u8bbe\u5907\u3010%s\u3011\u5df2\u8d85\u8fc7 %d \u5206\u949f\u672a\u4e0a\u62a5\u6570\u636e\uff08\u4e0a\u62a5\u95f4\u9694\uff1a%d \u79d2\uff09",
                device.getDevicename(), minutes, device.getDatareportinterval());

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

    private boolean isThresholdExceeded(float value, String condition) {
        String normalized = condition.trim();
        try {
            if (normalized.startsWith(">=")) {
                return value >= Double.parseDouble(normalized.substring(2).trim());
            }
            if (normalized.startsWith("<=")) {
                return value <= Double.parseDouble(normalized.substring(2).trim());
            }
            if (normalized.startsWith(">")) {
                return value > Double.parseDouble(normalized.substring(1).trim());
            }
            if (normalized.startsWith("<")) {
                return value < Double.parseDouble(normalized.substring(1).trim());
            }
        } catch (NumberFormatException e) {
            logger.error("Failed to parse rule condition: {}", condition, e);
        }
        return false;
    }

    private String getSensorUnit(String sensorType) {
        switch (sensorType) {
            case SENSOR_TEMPERATURE:
                return "\u00b0C";
            case SENSOR_HUMIDITY:
            case SENSOR_CPU:
            case SENSOR_MEMORY:
            case SENSOR_DISK:
                return "%";
            case "CO2":
                return "ppm";
            default:
                return "";
        }
    }
}
