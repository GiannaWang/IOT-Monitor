package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.entity.AlertRule;
import ecnu.edu.iotbackend.entity.AlertRuleSaveRequest;
import ecnu.edu.iotbackend.service.AlertRuleService;
import ecnu.edu.iotbackend.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertRuleService alertRuleService;

    @GetMapping("/getAllAlerts")
    public Result<List<Alert>> getAllAlerts() {
        try {
            List<Alert> alerts = alertService.getAllAlerts();
            logger.info("获取所有告警成功，数量: {}", alerts.size());
            return Result.success(alerts);
        } catch (Exception e) {
            logger.error("获取所有告警失败", e);
            return Result.fail("获取告警失败，请稍后重试");
        }
    }

    @GetMapping("/getLatest5Alerts")
    public Result<List<Alert>> getLatest5Alerts() {
        try {
            List<Alert> alerts = alertService.getLatest5Alerts();
            return Result.success(alerts);
        } catch (Exception e) {
            logger.error("获取最新告警失败", e);
            return Result.fail("获取告警失败，请稍后重试");
        }
    }

    @GetMapping("/getTodayAlertCount")
    public Result<Integer> getTodayAlertCount() {
        try {
            int count = alertService.getTodayAlertCount();
            return Result.success(count);
        } catch (Exception e) {
            logger.error("获取今日告警数量失败", e);
            return Result.fail("获取告警数量失败，请稍后重试");
        }
    }

    @PostMapping("/markAsHandled")
    public Result<Boolean> markAsHandled(@RequestParam("alertId") int alertId) {
        try {
            boolean success = alertService.markAsHandled(alertId);
            return success ? Result.success(true) : Result.fail("标记失败");
        } catch (Exception e) {
            logger.error("标记告警已处理失败", e);
            return Result.fail("操作失败，请稍后重试");
        }
    }

    @GetMapping("/alert-rules")
    public Result<List<AlertRule>> getAlertRules() {
        try {
            return Result.success(alertRuleService.getManageableRules());
        } catch (Exception e) {
            logger.error("获取告警规则失败", e);
            return Result.fail("获取告警规则失败");
        }
    }

    @PostMapping("/alert-rules")
    public Result<Boolean> saveAlertRule(@RequestBody AlertRuleSaveRequest request) {
        try {
            return alertRuleService.saveRule(request)
                    ? Result.success(true)
                    : Result.fail("保存告警规则失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("保存告警规则失败", e);
            return Result.fail("保存告警规则失败");
        }
    }

    @DeleteMapping("/alert-rules")
    public Result<Boolean> deleteAlertRule(@RequestParam(required = false) Integer locationId,
                                           @RequestParam String sensorType) {
        try {
            return alertRuleService.deleteRule(locationId, sensorType)
                    ? Result.success(true)
                    : Result.fail("删除告警规则失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("删除告警规则失败", e);
            return Result.fail("删除告警规则失败");
        }
    }
}
