package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.entity.SensorDevice;
import ecnu.edu.iotbackend.entity.WindowsEventRequest;
import ecnu.edu.iotbackend.entity.WindowsHeartbeatRequest;
import ecnu.edu.iotbackend.mapper.SensorDataMapper;
import ecnu.edu.iotbackend.mapper.SensorDeviceMapper;
import ecnu.edu.iotbackend.service.WindowsAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WindowsAgentServiceImpl implements WindowsAgentService {

    @Autowired
    private SensorDeviceMapper sensorDeviceMapper;

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Override
    public void handleHeartbeat(WindowsHeartbeatRequest req) {
        // 1. 注册或更新设备记录
        SensorDevice device = new SensorDevice();
        device.setDeviceid(req.getMachineId());
        device.setDevicename(req.getHostname());
        device.setSensortype("计算机");
        device.setSource(req.getIpAddress() + " / " + req.getUsername());
        device.setStatus("online");
        device.setDatareportinterval(req.getReportInterval() != null ? req.getReportInterval() : 60);
        device.setLocationid(req.getLocationId() != null ? req.getLocationId() : 1);

        sensorDeviceMapper.upsertWindowsMachine(device);

        // 2. 查询数据库 id（ON DUPLICATE KEY UPDATE 不总会更新 id）
        SensorDevice saved = sensorDeviceMapper.getDeviceByDeviceId(req.getMachineId());
        if (saved == null || req.getMetrics() == null) return;

        int dbDeviceId = saved.getId();
        LocalDateTime now = LocalDateTime.now();

        // 3. 插入每个指标为一条 sensor_datas 记录
        for (WindowsHeartbeatRequest.MetricItem metric : req.getMetrics()) {
            SensorData data = new SensorData();
            data.setDeviceId(dbDeviceId);
            data.setDataType(metric.getType());
            data.setValue(metric.getValue());
            data.setUnit(metric.getUnit() != null ? metric.getUnit() : "%");
            data.setTimeStamp(now);
            data.setStatus("正常");
            sensorDataMapper.insertSensorData(data);
        }
    }

    @Override
    public void handleEvent(WindowsEventRequest req) {
        SensorDevice device = sensorDeviceMapper.getDeviceByDeviceId(req.getMachineId());
        if (device == null) return;

        String eventType = req.getEventType();
        LocalDateTime now = LocalDateTime.now();

        // 更新设备在线状态
        String newStatus = (eventType.equals("startup") || eventType.equals("wake")) ? "online" : "offline";
        sensorDeviceMapper.updateDeviceStatus(req.getMachineId(), newStatus, now);

        // 写入电源状态记录（startup/wake=1, shutdown/sleep=0）
        float stateValue = (eventType.equals("startup") || eventType.equals("wake")) ? 1.0f : 0.0f;
        SensorData data = new SensorData();
        data.setDeviceId(device.getId());
        data.setDataType("电源状态");
        data.setValue(stateValue);
        data.setUnit(eventType);
        data.setTimeStamp(now);
        data.setStatus("正常");
        sensorDataMapper.insertSensorData(data);
    }
}
