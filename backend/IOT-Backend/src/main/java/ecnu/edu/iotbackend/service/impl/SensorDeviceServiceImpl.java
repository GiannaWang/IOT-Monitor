package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.HAEntity;
import ecnu.edu.iotbackend.entity.SensorDevice;
import ecnu.edu.iotbackend.mapper.SensorDeviceMapper;
import ecnu.edu.iotbackend.service.HAService;
import ecnu.edu.iotbackend.service.SensorDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorDeviceServiceImpl implements SensorDeviceService {

    @Autowired
    private SensorDeviceMapper sensorDeviceMapper;

    @Autowired
    private HAService haService;

    @Override
    public List<SensorDevice> getAllEnabledDevices() {
        return sensorDeviceMapper.getAllDevices();
    }

    @Override
    public List<HAEntity> getAvailableDevices() {
        // 获取所有 HA 传感器
        List<HAEntity> allHASensors = haService.getAllSensors();

        // 获取已启用的设备 ID 列表
        List<String> enabledDeviceIds = getAllEnabledDevices().stream()
                .map(SensorDevice::getDeviceid)
                .collect(Collectors.toList());

        // 过滤出未启用的设备
        return allHASensors.stream()
                .filter(entity -> !enabledDeviceIds.contains(entity.getEntityId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean enableDevice(String entityId, Integer locationId, Integer reportInterval) {
        // 检查是否已启用
        if (isDeviceEnabled(entityId)) {
            return false; // 设备已启用
        }

        // 从 HA 获取设备信息
        HAEntity haEntity = haService.getEntityState(entityId);
        if (haEntity == null) {
            return false; // HA 中不存在该设备
        }

        // 创建 SensorDevice 对象
        SensorDevice device = new SensorDevice();
        device.setDeviceid(entityId);
        device.setDevicename(haEntity.getFriendlyName());
        device.setSensortype(haEntity.getDeviceClass());
        device.setSource("HomeAssistant");
        device.setStatus("enabled");
        device.setInstalltime(LocalDateTime.now());
        device.setBatterylevel(100); // 默认电池电量
        device.setTimestamp(LocalDateTime.now());
        device.setDatareportinterval(reportInterval != null ? reportInterval : 60); // 默认 60 秒
        device.setLocationid(locationId);

        // 插入数据库
        int result = sensorDeviceMapper.insertDevice(device);
        return result > 0;
    }

    @Override
    public boolean disableDevice(Integer id) {
        int result = sensorDeviceMapper.deleteDeviceById(id);
        return result > 0;
    }

    @Override
    public SensorDevice getDeviceById(Integer id) {
        return sensorDeviceMapper.getDeviceById(id);
    }

    @Override
    public boolean isDeviceEnabled(String entityId) {
        SensorDevice device = sensorDeviceMapper.getDeviceByDeviceId(entityId);
        return device != null;
    }
}
