package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.AlertRule;
import ecnu.edu.iotbackend.entity.AlertRuleSaveRequest;

import java.util.List;

public interface AlertRuleService {

    List<AlertRule> getManageableRules();

    List<AlertRule> getEnabledRulesForScheduling();

    boolean saveRule(AlertRuleSaveRequest request);

    boolean deleteRule(Integer locationId, String sensorType);
}
