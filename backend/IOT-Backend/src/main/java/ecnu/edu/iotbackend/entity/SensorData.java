package ecnu.edu.iotbackend.entity;

import java.time.LocalDateTime;

public class SensorData {
    private int id;
    private int deviceId;
    private String dataType;
    private float value;
    private String unit;
    private LocalDateTime timeStamp;
    private String status;

    @Override
    public String toString() {
        return "sensor_datas{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", dataType='" + dataType + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", timeStamp=" + timeStamp +
                ", status='" + status + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
