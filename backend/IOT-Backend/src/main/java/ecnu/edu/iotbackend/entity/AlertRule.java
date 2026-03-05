package ecnu.edu.iotbackend.entity;

import java.time.LocalDateTime;

/**
 * 告警规则实体类，对应 alert_rules 表
 */
public class AlertRule {

    private int id;
    private String ruleName;       // 规则名称（同时作为 alertType 写入 alerts 表）
    private String description;    // 规则描述
    private String ruleCondition;  // 触发条件，格式："> 28" / "< 10" / "offline"
    private String sensorType;     // 适用传感器类型（"温度" / "湿度" / "device"）
    private String severity;       // 级别："warning" / "critical"
    private LocalDateTime createTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRuleCondition() { return ruleCondition; }
    public void setRuleCondition(String ruleCondition) { this.ruleCondition = ruleCondition; }

    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
