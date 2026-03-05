package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.WindowsHeartbeatRequest;
import ecnu.edu.iotbackend.entity.WindowsEventRequest;

public interface WindowsAgentService {

    /**
     * 处理 Windows 机器心跳（注册/更新设备 + 写入指标数据）
     */
    void handleHeartbeat(WindowsHeartbeatRequest request);

    /**
     * 处理 Windows 机器电源事件（开机/关机/睡眠/唤醒）
     */
    void handleEvent(WindowsEventRequest request);
}
