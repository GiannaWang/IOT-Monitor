package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.mapper.SensorDataMapper;
import ecnu.edu.iotbackend.security.CurrentUserProvider;
import ecnu.edu.iotbackend.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SensorDataServiceImpl implements SensorDataService {

    private static final String CPU_TYPE = "\u0043\u0050\u0055\u4f7f\u7528\u7387";
    private static final String MEMORY_TYPE = "\u5185\u5b58\u4f7f\u7528\u7387";
    private static final String DISK_TYPE = "\u78c1\u76d8\u4f7f\u7528\u7387";

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Override
    public List<SensorData> getSensorDataByType(String dataType) {
        String normalizedType = normalizeDataType(dataType);
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return Collections.emptyList();
            }
            return sensorDataMapper.getSensorDataByTypeAndLocationIds(normalizedType, accessibleRoomIds);
        }
        return sensorDataMapper.getSensorDataByType(normalizedType);
    }

    @Override
    public List<SensorData> get10SensorDataByType(String dataType) {
        String normalizedType = normalizeDataType(dataType);
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return Collections.emptyList();
            }
            return sensorDataMapper.get10SensorDataByTypeAndLocationIds(normalizedType, accessibleRoomIds);
        }
        return sensorDataMapper.get10SensorDataByType(normalizedType);
    }

    @Override
    public List<SensorData> getAllSensorData() {
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return Collections.emptyList();
            }
            return sensorDataMapper.getAllSensorDataByLocationIds(accessibleRoomIds);
        }
        return sensorDataMapper.getAllSensorData();
    }

    @Override
    public int countOnlineDevices() {
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return 0;
            }
            return sensorDataMapper.countOnlineDevicesByLocationIds(accessibleRoomIds);
        }
        return sensorDataMapper.countOnlineDevices();
    }

    @Override
    public int countAllDevices() {
        List<Integer> accessibleRoomIds = getAccessibleRoomIds();
        if (accessibleRoomIds != null) {
            if (accessibleRoomIds.isEmpty()) {
                return 0;
            }
            return sensorDataMapper.countAllDevicesByLocationIds(accessibleRoomIds);
        }
        return sensorDataMapper.countAllDevices();
    }

    @Override
    public List<SensorData> getSensorDataWithFilters(String dataType, Integer locationId, String period, String timeSlot) {
        return sensorDataMapper.getSensorDataWithFilters(
                normalizeDataType(dataType),
                locationId,
                getAccessibleRoomIds(),
                period,
                timeSlot
        );
    }

    @Override
    public double calculateDeviceRate() {
        int onlineCount = countOnlineDevices();
        int totalCount = countAllDevices();

        if (totalCount == 0) {
            return 0.0;
        }

        return Math.round((double) onlineCount / totalCount * 10000.0) / 100.0;
    }

    private List<Integer> getAccessibleRoomIds() {
        if (currentUserProvider.isAdmin()) {
            return null;
        }
        return currentUserProvider.getAccessibleRoomIds();
    }

    private String normalizeDataType(String dataType) {
        if (dataType == null) {
            return null;
        }

        switch (dataType.trim().toLowerCase()) {
            case "cpu":
                return CPU_TYPE;
            case "memory":
                return MEMORY_TYPE;
            case "disk":
                return DISK_TYPE;
            default:
                return dataType;
        }
    }
}
