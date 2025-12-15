package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.HAEntity;
import ecnu.edu.iotbackend.entity.SensorDevice;
import ecnu.edu.iotbackend.service.SensorDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/device")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SensorDeviceController {

    private static final Logger logger = LoggerFactory.getLogger(SensorDeviceController.class);

    @Autowired
    private SensorDeviceService sensorDeviceService;

    /**
     * 获取所有已启用的设备
     */
    @GetMapping("/enabled")
    public Result<List<SensorDevice>> getEnabledDevices() {
        try {
            List<SensorDevice> devices = sensorDeviceService.getAllEnabledDevices();
            logger.info("获取已启用设备成功，数量: {}", devices.size());
            return Result.success(devices);
        } catch (Exception e) {
            logger.error("获取已启用设备失败", e);
            return Result.fail("获取设备列表失败");
        }
    }

    /**
     * 获取可添加的 HA 设备（未启用的设备）
     */
    @GetMapping("/available")
    public Result<List<HAEntity>> getAvailableDevices() {
        try {
            List<HAEntity> devices = sensorDeviceService.getAvailableDevices();
            logger.info("获取可添加设备成功，数量: {}", devices.size());
            return Result.success(devices);
        } catch (Exception e) {
            logger.error("获取可添加设备失败", e);
            return Result.fail("获取可添加设备失败");
        }
    }

    /**
     * 启用设备（添加设备）
     * 请求体示例：
     * {
     *   "entityId": "sensor.cabinet_1_a",
     *   "locationId": 1,
     *   "reportInterval": 60
     * }
     */
    @PostMapping("/enable")
    public Result<String> enableDevice(@RequestBody Map<String, Object> request) {
        try {
            String entityId = (String) request.get("entityId");
            Integer locationId = request.get("locationId") != null
                ? Integer.parseInt(request.get("locationId").toString())
                : 1; // 默认位置 ID
            Integer reportInterval = request.get("reportInterval") != null
                ? Integer.parseInt(request.get("reportInterval").toString())
                : 60; // 默认 60 秒

            if (entityId == null || entityId.isEmpty()) {
                return Result.fail("设备 ID 不能为空");
            }

            boolean success = sensorDeviceService.enableDevice(entityId, locationId, reportInterval);
            if (success) {
                logger.info("启用设备成功: {}", entityId);
                return Result.success("设备添加成功");
            } else {
                logger.warn("启用设备失败: {}", entityId);
                return Result.fail("设备已存在或 HA 中不存在该设备");
            }
        } catch (Exception e) {
            logger.error("启用设备失败", e);
            return Result.fail("启用设备失败: " + e.getMessage());
        }
    }

    /**
     * 禁用设备（删除设备）
     */
    @DeleteMapping("/{id}")
    public Result<String> disableDevice(@PathVariable Integer id) {
        try {
            boolean success = sensorDeviceService.disableDevice(id);
            if (success) {
                logger.info("禁用设备成功，ID: {}", id);
                return Result.success("设备删除成功");
            } else {
                logger.warn("禁用设备失败，ID: {}", id);
                return Result.fail("设备不存在");
            }
        } catch (Exception e) {
            logger.error("禁用设备失败", e);
            return Result.fail("禁用设备失败: " + e.getMessage());
        }
    }

    /**
     * 根据 ID 获取设备详情
     */
    @GetMapping("/{id}")
    public Result<SensorDevice> getDeviceById(@PathVariable Integer id) {
        try {
            SensorDevice device = sensorDeviceService.getDeviceById(id);
            if (device != null) {
                return Result.success(device);
            } else {
                return Result.fail("设备不存在");
            }
        } catch (Exception e) {
            logger.error("获取设备详情失败", e);
            return Result.fail("获取设备详情失败");
        }
    }
}
