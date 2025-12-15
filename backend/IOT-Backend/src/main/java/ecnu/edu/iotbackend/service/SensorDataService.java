package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.SensorData;
import java.util.List;

/**
 * 传感器数据业务逻辑接口
 */
public interface SensorDataService {

    /**
     * 根据数据类型获取传感器数据
     * @param dataType 数据类型（如"温度"、"湿度"）
     * @return 传感器数据列表
     */
    List<SensorData> getSensorDataByType(String dataType);

    /**
     * 根据数据类型获取最近10条传感器数据
     * @param dataType 数据类型
     * @return 传感器数据列表
     */
    List<SensorData> get10SensorDataByType(String dataType);

    /**
     * 获取所有传感器数据
     * @return 传感器数据列表
     */
    List<SensorData> getAllSensorData();

    /**
     * 统计在线设备数量
     * @return 在线设备数量
     */
    int countOnlineDevices();

    /**
     * 统计所有设备数量
     * @return 设备总数
     */
    int countAllDevices();

    /**
     * 计算设备运行率
     * @return 设备运行率（百分比，保留2位小数）
     */
    double calculateDeviceRate();
}
