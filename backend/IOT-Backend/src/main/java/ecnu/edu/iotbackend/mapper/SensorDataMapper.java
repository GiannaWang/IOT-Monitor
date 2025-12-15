package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.SensorData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SensorDataMapper {
    @Select("SELECT * FROM sensor_datas WHERE datatype = #{selectedDataType}")
    public List<SensorData> getSensorDataByType(@Param("selectedDataType") String selectedDataType);

    @Select("SELECT * FROM sensor_datas WHERE datatype = #{selectedDataType} " +
            "ORDER BY timestamp DESC LIMIT 10")
    public List<SensorData> get10SensorDataByType(@Param("selectedDataType") String selectedDataType);

    @Select("SELECT * FROM sensor_datas")
    public List<SensorData> getAllSensorData();

    @Select("SELECT COUNT(*) FROM sensor_datas WHERE status = '正常'")
    public int countOnlineDevices();

    @Select("SELECT COUNT(*) FROM sensor_datas")
    public int countAllDevices();
    
    // 根据设备编号删除传感器数据（deviceid在sensor_datas中是int类型，需要转换）
    // 注意：sensor_datas的deviceid是int，而sensor_devices的deviceid是varchar
    // 这里需要根据实际情况处理，可能需要先查询设备获取deviceid的数值部分
    @org.apache.ibatis.annotations.Delete("DELETE FROM sensor_datas WHERE deviceid = #{deviceid}")
    int deleteSensorDataByDeviceId(@Param("deviceid") Integer deviceid);


}
