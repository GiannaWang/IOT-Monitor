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

    private static final String COMPUTER_TYPE = "\u8ba1\u7b97\u673a";
    private static final String POWER_STATE_TYPE = "\u7535\u6e90\u72b6\u6001";
    private static final String NORMAL_STATUS = "\u6b63\u5e38";
    private static final String CPU_TYPE = "\u0043\u0050\u0055\u4f7f\u7528\u7387";
    private static final String MEMORY_TYPE = "\u5185\u5b58\u4f7f\u7528\u7387";
    private static final String DISK_TYPE = "\u78c1\u76d8\u4f7f\u7528\u7387";

    @Autowired
    private SensorDeviceMapper sensorDeviceMapper;

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Override
    public void handleHeartbeat(WindowsHeartbeatRequest req) {
        SensorDevice device = new SensorDevice();
        device.setDeviceid(req.getMachineId());
        device.setDevicename(req.getHostname());
        device.setSensortype(COMPUTER_TYPE);
        device.setSource(req.getIpAddress() + " / " + req.getUsername());
        device.setStatus("online");
        device.setDatareportinterval(req.getReportInterval() != null ? req.getReportInterval() : 60);
        device.setLocationid(req.getLocationId() != null ? req.getLocationId() : 1);

        sensorDeviceMapper.upsertWindowsMachine(device);

        SensorDevice saved = sensorDeviceMapper.getDeviceByDeviceId(req.getMachineId());
        if (saved == null || req.getMetrics() == null) {
            return;
        }

        int dbDeviceId = saved.getId();
        LocalDateTime now = LocalDateTime.now();

        for (WindowsHeartbeatRequest.MetricItem metric : req.getMetrics()) {
            SensorData data = new SensorData();
            data.setDeviceId(dbDeviceId);
            data.setDataType(normalizeMetricType(metric.getType()));
            data.setValue(metric.getValue());
            data.setUnit(metric.getUnit() != null ? metric.getUnit() : "%");
            data.setTimeStamp(now);
            data.setStatus(NORMAL_STATUS);
            sensorDataMapper.insertSensorData(data);
        }
    }

    @Override
    public void handleEvent(WindowsEventRequest req) {
        SensorDevice device = sensorDeviceMapper.getDeviceByDeviceId(req.getMachineId());
        if (device == null) {
            return;
        }

        String eventType = req.getEventType();
        LocalDateTime now = LocalDateTime.now();
        String newStatus = ("startup".equals(eventType) || "wake".equals(eventType)) ? "online" : "offline";
        sensorDeviceMapper.updateDeviceStatus(req.getMachineId(), newStatus, now);

        float stateValue = ("startup".equals(eventType) || "wake".equals(eventType)) ? 1.0f : 0.0f;
        SensorData data = new SensorData();
        data.setDeviceId(device.getId());
        data.setDataType(POWER_STATE_TYPE);
        data.setValue(stateValue);
        data.setUnit(eventType);
        data.setTimeStamp(now);
        data.setStatus(NORMAL_STATUS);
        sensorDataMapper.insertSensorData(data);
    }

    private String normalizeMetricType(String type) {
        if (type == null) {
            return null;
        }

        switch (type.trim().toLowerCase()) {
            case "cpu":
                return CPU_TYPE;
            case "memory":
                return MEMORY_TYPE;
            case "disk":
                return DISK_TYPE;
            default:
                return type;
        }
    }
}
