package ecnu.edu.iotbackend.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * Home Assistant 实体 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HAEntity {

    @JsonAlias("entity_id")  // 反序列化时接受 entity_id，序列化时输出 entityId
    private String entityId;

    private String state;

    private Map<String, Object> attributes;

    @JsonAlias("last_changed")
    private String lastChanged;

    @JsonAlias("last_updated")
    private String lastUpdated;

    // 无参构造函数
    public HAEntity() {
    }

    // Getters and Setters
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(String lastChanged) {
        this.lastChanged = lastChanged;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * 获取友好名称
     */
    public String getFriendlyName() {
        if (attributes != null && attributes.containsKey("friendly_name")) {
            return (String) attributes.get("friendly_name");
        }
        return entityId;
    }

    /**
     * 获取设备类别
     */
    public String getDeviceClass() {
        if (attributes != null && attributes.containsKey("device_class")) {
            return (String) attributes.get("device_class");
        }
        return "unknown";
    }
}
