package ecnu.edu.iotbackend.entity;

import java.util.List;

/**
 * Windows Agent 心跳请求体
 */
public class WindowsHeartbeatRequest {

    private String machineId;       // 唯一标识（主机名+MAC）
    private String hostname;        // 主机名
    private String ipAddress;       // IP 地址
    private String username;        // 登录用户名
    private String osVersion;       // 系统版本
    private Integer locationId;     // 所在房间ID
    private Integer reportInterval; // 上报间隔（秒）
    private List<MetricItem> metrics;

    public static class MetricItem {
        private String type;   // "CPU使用率" / "内存使用率" / "磁盘使用率" / "网络发送" / "网络接收"
        private float value;
        private String unit;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public float getValue() { return value; }
        public void setValue(float value) { this.value = value; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public Integer getReportInterval() { return reportInterval; }
    public void setReportInterval(Integer reportInterval) { this.reportInterval = reportInterval; }
    public List<MetricItem> getMetrics() { return metrics; }
    public void setMetrics(List<MetricItem> metrics) { this.metrics = metrics; }
}
