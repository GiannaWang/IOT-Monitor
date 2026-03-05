package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlertService alertService;

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

            if (alerts.isEmpty()) {
                logger.info("暂无告警记录");
                return Result.success(alerts);
            }

            logger.info("获取最新5条告警成功，数量: {}", alerts.size());
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
            logger.info("今日告警数量: {}", count);
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

            if (success) {
                logger.info("标记告警为已处理成功，告警ID: {}", alertId);
                return Result.success(true);
            } else {
                return Result.fail("标记失败");
            }
        } catch (Exception e) {
            logger.error("标记告警为已处理失败", e);
            return Result.fail("操作失败，请稍后重试");
        }
    }
}
