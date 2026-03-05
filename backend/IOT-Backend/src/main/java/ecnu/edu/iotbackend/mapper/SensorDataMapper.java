package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.SensorData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

public interface SensorDataMapper {
    @Select("SELECT * FROM sensor_datas WHERE datatype = #{selectedDataType}")
    public List<SensorData> getSensorDataByType(@Param("selectedDataType") String selectedDataType);

    @Select("SELECT * FROM sensor_datas WHERE datatype = #{selectedDataType} " +
            "ORDER BY timestamp DESC LIMIT 10")
    public List<SensorData> get10SensorDataByType(@Param("selectedDataType") String selectedDataType);

    @Select("SELECT * FROM sensor_datas")
    public List<SensorData> getAllSensorData();

    @Select("SELECT COUNT(*) FROM sensor_devices WHERE status = 'online'")
    public int countOnlineDevices();

    @Select("SELECT COUNT(*) FROM sensor_devices")
    public int countAllDevices();
    
    /**
     * 获取指定设备指定类型的最新一条数据（供告警检测使用）
     */
    @Select("SELECT * FROM sensor_datas WHERE deviceid = #{deviceId} AND datatype = #{dataType} " +
            "ORDER BY timestamp DESC LIMIT 1")
    SensorData getLatestDataByDeviceAndType(@Param("deviceId") int deviceId, @Param("dataType") String dataType);

    /**
     * 动态筛选传感器数据：支持 dataType / locationId / period / timeSlot 组合
     */
    @SelectProvider(type = SensorDataProvider.class, method = "getSensorDataWithFilters")
    List<SensorData> getSensorDataWithFilters(
            @Param("dataType") String dataType,
            @Param("locationId") Integer locationId,
            @Param("period") String period,
            @Param("timeSlot") String timeSlot);

    @org.apache.ibatis.annotations.Delete("DELETE FROM sensor_datas WHERE deviceid = #{deviceid}")
    int deleteSensorDataByDeviceId(@Param("deviceid") Integer deviceid);

    /**
     * 插入一条传感器数据（Windows Agent 上报指标使用）
     */
    @org.apache.ibatis.annotations.Insert(
            "INSERT INTO sensor_datas (deviceid, datatype, value, unit, timestamp, status) " +
            "VALUES (#{deviceId}, #{dataType}, #{value}, #{unit}, #{timeStamp}, #{status})")
    int insertSensorData(SensorData data);
}
