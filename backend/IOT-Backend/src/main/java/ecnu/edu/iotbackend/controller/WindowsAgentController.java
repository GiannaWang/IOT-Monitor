package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.WindowsEventRequest;
import ecnu.edu.iotbackend.entity.WindowsHeartbeatRequest;
import ecnu.edu.iotbackend.service.WindowsAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/windows")
public class WindowsAgentController {

    @Autowired
    private WindowsAgentService windowsAgentService;

    /**
     * Windows Agent 定时心跳上报（含 CPU/内存/磁盘指标）
     * POST /windows/heartbeat
     */
    @PostMapping("/heartbeat")
    public Result<String> heartbeat(@RequestBody WindowsHeartbeatRequest request) {
        windowsAgentService.handleHeartbeat(request);
        return Result.success("心跳接收成功");
    }

    /**
     * Windows Agent 电源事件上报（开机/关机/睡眠/唤醒）
     * POST /windows/event
     */
    @PostMapping("/event")
    public Result<String> event(@RequestBody WindowsEventRequest request) {
        windowsAgentService.handleEvent(request);
        return Result.success("事件记录成功");
    }
}
