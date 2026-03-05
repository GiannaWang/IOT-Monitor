package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.SensorDevice;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SensorDeviceMapper {

    /**
     * 插入新设备（启用设备）
     */
    @Insert("INSERT INTO sensor_devices (devicename, deviceid, sensortype, source, status, " +
            "installtime, batterylevel, timestamp, datareportinterval, locationid) " +
            "VALUES (#{devicename}, #{deviceid}, #{sensortype}, #{source}, #{status}, " +
            "#{installtime}, #{batterylevel}, #{timestamp}, #{datareportinterval}, #{locationid})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertDevice(SensorDevice device);

    /**
     * 获取所有已启用的设备
     */
    @Select("SELECT * FROM sensor_devices")
    List<SensorDevice> getAllDevices();

    /**
     * 根据 ID 获取设备
     */
    @Select("SELECT * FROM sensor_devices WHERE id = #{id}")
    SensorDevice getDeviceById(Integer id);

    /**
     * 根据设备 ID（HA entity_id）获取设备
     */
    @Select("SELECT * FROM sensor_devices WHERE deviceid = #{deviceid}")
    SensorDevice getDeviceByDeviceId(String deviceid);

    /**
     * 删除设备（禁用设备）
     */
    @Delete("DELETE FROM sensor_devices WHERE id = #{id}")
    int deleteDeviceById(Integer id);

    /**
     * 根据设备 ID（HA entity_id）删除设备
     */
    @Delete("DELETE FROM sensor_devices WHERE deviceid = #{deviceid}")
    int deleteDeviceByDeviceId(String deviceid);

    /**
     * 更新设备状态
     */
    @Update("UPDATE sensor_devices SET status = #{status}, timestamp = #{timestamp} WHERE deviceid = #{deviceid}")
    int updateDeviceStatus(@Param("deviceid") String deviceid,
                          @Param("status") String status,
                          @Param("timestamp") java.time.LocalDateTime timestamp);

    /**
     * Windows 机器注册/心跳更新（存在则更新，不存在则插入）
     * 需要 sensor_devices.deviceid 列有 UNIQUE 约束
     */
    @Insert("INSERT INTO sensor_devices (devicename, deviceid, sensortype, source, status, " +
            "installtime, timestamp, datareportinterval, locationid) " +
            "VALUES (#{devicename}, #{deviceid}, #{sensortype}, #{source}, #{status}, " +
            "NOW(), NOW(), #{datareportinterval}, #{locationid}) " +
            "ON DUPLICATE KEY UPDATE " +
            "devicename = VALUES(devicename), " +
            "source = VALUES(source), " +
            "status = 'online', " +
            "timestamp = NOW(), " +
            "datareportinterval = VALUES(datareportinterval), " +
            "locationid = VALUES(locationid)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int upsertWindowsMachine(SensorDevice device);
}
