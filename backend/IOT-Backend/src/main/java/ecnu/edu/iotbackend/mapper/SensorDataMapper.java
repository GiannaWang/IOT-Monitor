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


}
