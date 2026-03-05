package ecnu.edu.iotbackend.entity;

/**
 * Windows Agent 电源事件请求体
 */
public class WindowsEventRequest {

    private String machineId;   // 唯一标识
    private String eventType;   // "startup" / "shutdown" / "sleep" / "wake"

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}
