package ecnu.edu.iotbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 传感器设备实体类
 * 存储已启用的设备信息
 */
@TableName("sensor_devices")
public class SensorDevice {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String devicename;      // 设备名称
    private String deviceid;        // 设备 ID（对应 HA 的 entity_id）
    private String sensortype;      // 传感器类型
    private String source;          // 数据来源（如 "HomeAssistant"）
    private String status;          // 状态（online/offline/enabled/disabled）
    private LocalDateTime installtime;  // 安装时间
    private Integer batterylevel;   // 电池电量
    private LocalDateTime timestamp;    // 最后更新时间
    private Integer datareportinterval; // 数据上报间隔（秒）
    private Integer locationid;     // 位置 ID（外键）

    // 无参构造函数
    public SensorDevice() {
    }

    // 全参构造函数
    public SensorDevice(Integer id, String devicename, String deviceid, String sensortype,
                        String source, String status, LocalDateTime installtime,
                        Integer batterylevel, LocalDateTime timestamp,
                        Integer datareportinterval, Integer locationid) {
        this.id = id;
        this.devicename = devicename;
        this.deviceid = deviceid;
        this.sensortype = sensortype;
        this.source = source;
        this.status = status;
        this.installtime = installtime;
        this.batterylevel = batterylevel;
        this.timestamp = timestamp;
        this.datareportinterval = datareportinterval;
        this.locationid = locationid;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getSensortype() {
        return sensortype;
    }

    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getInstalltime() {
        return installtime;
    }

    public void setInstalltime(LocalDateTime installtime) {
        this.installtime = installtime;
    }

    public Integer getBatterylevel() {
        return batterylevel;
    }

    public void setBatterylevel(Integer batterylevel) {
        this.batterylevel = batterylevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDatareportinterval() {
        return datareportinterval;
    }

    public void setDatareportinterval(Integer datareportinterval) {
        this.datareportinterval = datareportinterval;
    }

    public Integer getLocationid() {
        return locationid;
    }

    public void setLocationid(Integer locationid) {
        this.locationid = locationid;
    }

    @Override
    public String toString() {
        return "SensorDevice{" +
                "id=" + id +
                ", devicename='" + devicename + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", sensortype='" + sensortype + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", installtime=" + installtime +
                ", batterylevel=" + batterylevel +
                ", timestamp=" + timestamp +
                ", datareportinterval=" + datareportinterval +
                ", locationid=" + locationid +
                '}';
    }
}
