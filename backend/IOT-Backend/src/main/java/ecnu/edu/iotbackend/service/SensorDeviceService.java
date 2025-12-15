package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.HAEntity;
import ecnu.edu.iotbackend.entity.SensorDevice;

import java.util.List;

/**
 * 传感器设备服务接口
 */
public interface SensorDeviceService {

    /**
     * 获取所有已启用的设备
     */
    List<SensorDevice> getAllEnabledDevices();

    /**
     * 获取所有可添加的 HA 设备（HA 中存在但未启用的设备）
     */
    List<HAEntity> getAvailableDevices();

    /**
     * 启用设备（从 HA 设备添加到数据库）
     * @param entityId HA entity_id
     * @param locationId 位置 ID
     * @param reportInterval 数据上报间隔（秒）
     * @return 是否成功
     */
    boolean enableDevice(String entityId, Integer locationId, Integer reportInterval);

    /**
     * 禁用设备（从数据库中删除）
     * @param id 设备数据库 ID
     * @return 是否成功
     */
    boolean disableDevice(Integer id);

    /**
     * 根据设备 ID 获取设备
     */
    SensorDevice getDeviceById(Integer id);

    /**
     * 检查设备是否已启用
     */
    boolean isDeviceEnabled(String entityId);
}
