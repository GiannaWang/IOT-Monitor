package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.HAEntity;
import java.util.List;

/**
 * Home Assistant API 服务接口
 */
public interface HAService {

    /**
     * 获取所有传感器实体
     * @return 传感器实体列表
     */
    List<HAEntity> getAllSensors();

    /**
     * 获取指定实体的状态
     * @param entityId 实体 ID
     * @return 实体信息
     */
    HAEntity getEntityState(String entityId);
}
