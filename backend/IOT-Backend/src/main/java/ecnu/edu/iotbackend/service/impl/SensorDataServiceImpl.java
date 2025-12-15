package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.mapper.SensorDataMapper;
import ecnu.edu.iotbackend.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 传感器数据业务逻辑实现类
 */
@Service
public class SensorDataServiceImpl implements SensorDataService {

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Override
    public List<SensorData> getSensorDataByType(String dataType) {
        return sensorDataMapper.getSensorDataByType(dataType);
    }

    @Override
    public List<SensorData> get10SensorDataByType(String dataType) {
        return sensorDataMapper.get10SensorDataByType(dataType);
    }

    @Override
    public List<SensorData> getAllSensorData() {
        return sensorDataMapper.getAllSensorData();
    }

    @Override
    public int countOnlineDevices() {
        return sensorDataMapper.countOnlineDevices();
    }

    @Override
    public int countAllDevices() {
        return sensorDataMapper.countAllDevices();
    }

    @Override
    public double calculateDeviceRate() {
        int onlineCount = countOnlineDevices();
        int totalCount = countAllDevices();

        if (totalCount == 0) {
            return 0.0;
        }

        // 计算运行率并保留2位小数
        return Math.round((double) onlineCount / totalCount * 10000.0) / 100.0;
    }
}
