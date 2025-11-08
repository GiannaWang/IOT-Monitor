package ecnu.edu.iotbackend.entity;

import java.time.LocalDateTime;

public class Alert {
    private int id;
    private String deviceId;
    private int locationId;
    private int ruleId;
    private String alertType;
    private String severity;
    private String message;
    private LocalDateTime timestamp;
    private byte handled;
    private String roomNumber;

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", locationId=" + locationId +
                ", ruleId=" + ruleId +
                ", alertType='" + alertType + '\'' +
                ", severity='" + severity + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", handled=" + handled +
                ", roomNumber='" + roomNumber + '\'' +
                '}';
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public byte getHandled() {
        return handled;
    }

    public void setHandled(byte handled) {
        this.handled = handled;
    }
}
