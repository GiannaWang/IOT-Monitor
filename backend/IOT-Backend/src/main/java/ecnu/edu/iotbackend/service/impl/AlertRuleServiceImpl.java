package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.entity.AlertRule;
import ecnu.edu.iotbackend.entity.AlertRuleSaveRequest;
import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.entity.SensorDevice;
import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.AlertMapper;
import ecnu.edu.iotbackend.mapper.AlertRuleMapper;
import ecnu.edu.iotbackend.mapper.SensorDataMapper;
import ecnu.edu.iotbackend.mapper.SensorDeviceMapper;
import ecnu.edu.iotbackend.security.CurrentUserProvider;
import ecnu.edu.iotbackend.service.AlertRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertRuleServiceImpl implements AlertRuleService {

    private static final String SENSOR_TEMPERATURE = "\u6e29\u5ea6";
    private static final String SENSOR_HUMIDITY = "\u6e7f\u5ea6";
    private static final String SENSOR_CPU = "CPU\u4f7f\u7528\u7387";
    private static final String SENSOR_MEMORY = "\u5185\u5b58\u4f7f\u7528\u7387";
    private static final String SENSOR_DISK = "\u78c1\u76d8\u4f7f\u7528\u7387";
    private static final String SENSOR_DEVICE = "device";

    private static final Map<String, String> DEFAULT_RULE_NAMES = new HashMap<>();

    static {
        DEFAULT_RULE_NAMES.put(SENSOR_TEMPERATURE, "\u6e29\u5ea6\u544a\u8b66");
        DEFAULT_RULE_NAMES.put(SENSOR_HUMIDITY, "\u6e7f\u5ea6\u544a\u8b66");
        DEFAULT_RULE_NAMES.put(SENSOR_CPU, "CPU\u8fc7\u8f7d");
        DEFAULT_RULE_NAMES.put(SENSOR_MEMORY, "\u5185\u5b58\u4e0d\u8db3");
        DEFAULT_RULE_NAMES.put(SENSOR_DISK, "\u78c1\u76d8\u544a\u8b66");
        DEFAULT_RULE_NAMES.put(SENSOR_DEVICE, "\u8bbe\u5907\u79bb\u7ebf");
    }

    private final AlertRuleMapper alertRuleMapper;
    private final AlertMapper alertMapper;
    private final SensorDeviceMapper sensorDeviceMapper;
    private final SensorDataMapper sensorDataMapper;
    private final CurrentUserProvider currentUserProvider;

    public AlertRuleServiceImpl(AlertRuleMapper alertRuleMapper,
                                AlertMapper alertMapper,
                                SensorDeviceMapper sensorDeviceMapper,
                                SensorDataMapper sensorDataMapper,
                                CurrentUserProvider currentUserProvider) {
        this.alertRuleMapper = alertRuleMapper;
        this.alertMapper = alertMapper;
        this.sensorDeviceMapper = sensorDeviceMapper;
        this.sensorDataMapper = sensorDataMapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public List<AlertRule> getManageableRules() {
        if (currentUserProvider.isAdmin()) {
            return alertRuleMapper.getAllRulesForManagement();
        }

        List<Integer> roomIds = currentUserProvider.getAccessibleRoomIds();
        if (roomIds == null) {
            return alertRuleMapper.getAllRulesForManagement();
        }
        if (roomIds.isEmpty()) {
            return alertRuleMapper.getGlobalRulesForManagement();
        }
        return alertRuleMapper.getRulesForManagementByLocationIds(roomIds);
    }

    @Override
    public List<AlertRule> getEnabledRulesForScheduling() {
        return alertRuleMapper.getEnabledRules();
    }

    @Override
    @Transactional
    public boolean saveRule(AlertRuleSaveRequest request) {
        validateRequest(request);

        Integer locationId = normalizeLocationId(request.getLocationId());
        ensurePermission(locationId);

        String sensorType = request.getSensorType().trim();
        AlertRule existing = locationId == null
                ? alertRuleMapper.getGlobalRuleBySensorType(sensorType)
                : alertRuleMapper.getLocationRuleBySensorType(locationId, sensorType);

        AlertRule rule = new AlertRule();
        rule.setId(existing == null ? 0 : existing.getId());
        rule.setLocationId(locationId);
        rule.setSensorType(sensorType);
        rule.setRuleName(resolveRuleName(request));
        rule.setDescription(resolveDescription(request));
        rule.setRuleCondition(request.getRuleCondition().trim());
        rule.setSeverity(request.getSeverity().trim());
        rule.setEnabled(request.getEnabled() == null || request.getEnabled());
        User currentUser = currentUserProvider.getCurrentUser();
        rule.setCreatedByUserId(currentUser == null ? null : currentUser.getUserId());
        LocalDateTime now = LocalDateTime.now();
        rule.setUpdateTime(now);

        if (existing == null) {
            rule.setCreateTime(now);
            boolean inserted = alertRuleMapper.insertRule(rule) > 0;
            if (inserted) {
                refreshUnhandledAlerts(resolvePersistedRule(locationId, sensorType));
            }
            return inserted;
        }

        rule.setCreateTime(existing.getCreateTime());
        boolean updated = alertRuleMapper.updateRule(rule) > 0;
        if (updated) {
            refreshUnhandledAlerts(rule);
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean deleteRule(Integer locationId, String sensorType) {
        Integer normalizedLocationId = normalizeLocationId(locationId);
        if (!StringUtils.hasText(sensorType)) {
            throw new IllegalArgumentException("\u7f3a\u5c11\u6570\u636e\u7c7b\u578b");
        }
        ensurePermission(normalizedLocationId);
        AlertRule existing = normalizedLocationId == null
                ? alertRuleMapper.getGlobalRuleBySensorType(sensorType.trim())
                : alertRuleMapper.getLocationRuleBySensorType(normalizedLocationId, sensorType.trim());
        if (normalizedLocationId == null) {
            if (!currentUserProvider.isAdmin()) {
                throw new IllegalArgumentException("\u6ca1\u6709\u6743\u9650\u5220\u9664\u5168\u5c40\u89c4\u5219");
            }
            boolean deleted = alertRuleMapper.deleteGlobalRuleBySensorType(sensorType.trim()) > 0;
            if (deleted) {
                clearUnhandledAlerts(existing);
            }
            return deleted;
        }
        boolean deleted = alertRuleMapper.deleteLocationRuleBySensorType(normalizedLocationId, sensorType.trim()) > 0;
        if (deleted) {
            clearUnhandledAlerts(existing);
        }
        return deleted;
    }

    private void validateRequest(AlertRuleSaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("\u8bf7\u6c42\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (!StringUtils.hasText(request.getSensorType())) {
            throw new IllegalArgumentException("\u7f3a\u5c11\u6570\u636e\u7c7b\u578b");
        }
        if (!StringUtils.hasText(request.getRuleCondition())) {
            throw new IllegalArgumentException("\u7f3a\u5c11\u89c4\u5219\u6761\u4ef6");
        }
        String condition = request.getRuleCondition().trim();
        if (!"offline".equalsIgnoreCase(condition)
                && !condition.matches("^(>=|<=|>|<)\\s*-?\\d+(\\.\\d+)?$")) {
            throw new IllegalArgumentException("\u89c4\u5219\u6761\u4ef6\u683c\u5f0f\u4e0d\u6b63\u786e");
        }
        if (!StringUtils.hasText(request.getSeverity())) {
            throw new IllegalArgumentException("\u7f3a\u5c11\u544a\u8b66\u7ea7\u522b");
        }
    }

    private void ensurePermission(Integer locationId) {
        if (locationId == null) {
            if (!currentUserProvider.isAdmin()) {
                throw new IllegalArgumentException("\u53ea\u6709\u7ba1\u7406\u5458\u53ef\u4ee5\u914d\u7f6e\u5168\u5c40\u89c4\u5219");
            }
            return;
        }

        if (currentUserProvider.isAdmin()) {
            return;
        }

        List<Integer> roomIds = currentUserProvider.getAccessibleRoomIds();
        List<Integer> safeRoomIds = roomIds == null ? Collections.emptyList() : roomIds;
        if (!safeRoomIds.contains(locationId)) {
            throw new IllegalArgumentException("\u6ca1\u6709\u6743\u9650\u914d\u7f6e\u8be5\u623f\u95f4\u89c4\u5219");
        }
    }

    private Integer normalizeLocationId(Integer locationId) {
        return locationId != null && locationId > 0 ? locationId : null;
    }

    private String resolveRuleName(AlertRuleSaveRequest request) {
        if (StringUtils.hasText(request.getRuleName())) {
            return request.getRuleName().trim();
        }
        String sensorType = request.getSensorType().trim();
        return DEFAULT_RULE_NAMES.getOrDefault(sensorType, sensorType + "\u544a\u8b66");
    }

    private String resolveDescription(AlertRuleSaveRequest request) {
        if (StringUtils.hasText(request.getDescription())) {
            return request.getDescription().trim();
        }
        String condition = request.getRuleCondition().trim();
        if ("offline".equalsIgnoreCase(condition)) {
            return "\u8bbe\u5907\u8d85\u8fc7\u6b63\u5e38\u4e0a\u62a5\u95f4\u9694 3 \u500d\u65f6\u95f4\u672a\u4e0a\u62a5\u6570\u636e\u65f6\u89e6\u53d1\u544a\u8b66";
        }
        return request.getSensorType().trim() + "\u6ee1\u8db3\u6761\u4ef6 " + condition + " \u65f6\u89e6\u53d1\u544a\u8b66";
    }

    private AlertRule resolvePersistedRule(Integer locationId, String sensorType) {
        return locationId == null
                ? alertRuleMapper.getGlobalRuleBySensorType(sensorType)
                : alertRuleMapper.getLocationRuleBySensorType(locationId, sensorType);
    }

    private void refreshUnhandledAlerts(AlertRule rule) {
        if (rule == null || rule.getId() <= 0) {
            return;
        }
        clearUnhandledAlerts(rule);
        if (!rule.isEnabled()) {
            return;
        }

        List<SensorDevice> devices = sensorDeviceMapper.getAllDevices();
        for (SensorDevice device : devices) {
            if (!isRuleApplicable(rule, device)) {
                continue;
            }
            if (SENSOR_DEVICE.equals(rule.getSensorType())) {
                rebuildOfflineAlert(rule, device);
                continue;
            }
            rebuildThresholdAlert(rule, device);
        }
    }

    private void clearUnhandledAlerts(AlertRule rule) {
        if (rule != null && rule.getId() > 0) {
            alertMapper.deleteUnhandledAlertsByRuleId(rule.getId());
        }
    }

    private boolean isRuleApplicable(AlertRule rule, SensorDevice device) {
        return rule.getLocationId() == null || rule.getLocationId().equals(device.getLocationid());
    }

    private void rebuildThresholdAlert(AlertRule rule, SensorDevice device) {
        if (device.getId() == null) {
            return;
        }
        SensorData latest = sensorDataMapper.getLatestDataByDeviceAndType(device.getId(), rule.getSensorType());
        if (latest == null || !isThresholdExceeded(latest.getValue(), rule.getRuleCondition())) {
            return;
        }
        alertMapper.insertAlert(buildSensorAlert(device, rule, latest.getValue()));
    }

    private void rebuildOfflineAlert(AlertRule rule, SensorDevice device) {
        if (device.getTimestamp() == null || device.getDatareportinterval() == null) {
            return;
        }
        long secondsSinceLastReport = ChronoUnit.SECONDS.between(device.getTimestamp(), LocalDateTime.now());
        long offlineThreshold = (long) device.getDatareportinterval() * 3;
        if (secondsSinceLastReport <= offlineThreshold) {
            return;
        }
        alertMapper.insertAlert(buildOfflineAlert(device, rule, secondsSinceLastReport));
    }

    private boolean isThresholdExceeded(float value, String condition) {
        String normalized = condition.trim();
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
        return false;
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
