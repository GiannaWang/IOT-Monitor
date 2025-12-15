package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.config.HAConfig;
import ecnu.edu.iotbackend.entity.HAEntity;
import ecnu.edu.iotbackend.service.HAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HAServiceImpl implements HAService {

    @Autowired
    private HAConfig haConfig;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 创建请求头（包含认证 token）
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(haConfig.getHaApiToken());
        return headers;
    }

    @Override
    public List<HAEntity> getAllSensors() {
        try {
            String url = haConfig.getHaApiUrl() + "/api/states";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<HAEntity[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    HAEntity[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 过滤出需要的传感器
                return Arrays.stream(response.getBody())
                        .filter(e -> e.getEntityId() != null && e.getEntityId().startsWith("sensor."))
                        .filter(this::isValidSensor)  // 应用自定义过滤规则
                        .collect(Collectors.toList());
            }

            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 判断传感器是否有效（过滤掉不需要的设备）
     */
    private boolean isValidSensor(HAEntity entity) {
        String entityId = entity.getEntityId().toLowerCase();
        String friendlyName = entity.getFriendlyName().toLowerCase();
        String deviceClass = entity.getDeviceClass().toLowerCase();

        // 过滤规则：排除以下类型的设备
        String[] excludeKeywords = {
                "current",      // 电流相关
                "browser",      // 浏览器相关
                "backup",       // 备份相关
                "update",       // 更新相关
                "version",      // 版本相关
                "uptime",       // 运行时间
                "memory",       // 内存相关
                "cpu",          // CPU相关
                "disk",         // 磁盘相关
                "network",      // 网络相关（根据需要可以保留）
                "ip_address",   // IP地址
                "processor",    // 处理器
                "runtime",      // 运行时
                "cabinet"       // Cabinet 电流传感器
        };

        // 检查 entity_id、friendly_name 和 device_class 是否包含排除关键词
        for (String keyword : excludeKeywords) {
            if (entityId.contains(keyword) ||
                friendlyName.contains(keyword) ||
                deviceClass.contains(keyword)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public HAEntity getEntityState(String entityId) {
        try {
            String url = haConfig.getHaApiUrl() + "/api/states/" + entityId;
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<HAEntity> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    HAEntity.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
