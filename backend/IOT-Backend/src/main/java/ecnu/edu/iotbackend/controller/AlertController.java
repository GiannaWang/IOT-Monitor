package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.Alert;
import ecnu.edu.iotbackend.mapper.AlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AlertController {

    @Autowired
    private AlertMapper alertMapper;

    @GetMapping("/getLatest5Alerts")
    public Result<List<Alert>> getLatest5Alerts() {
        List<Alert> alerts = alertMapper.getLatest5Alerts();
        if (alerts.isEmpty()) {
            return Result.fail("后端获取告警失败");
        } else {
            System.out.println("最新告警："+alerts);
            return Result.success(alerts);
        }
    }

    @GetMapping("/getTodayAlertCount")
    public Result<Integer> getTodayAlertCount() {
        int count = alertMapper.getTodayAlertCount();
        return Result.success(count);
    }

}
