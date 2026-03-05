package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.service.SensorDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SensorDataController {

    private static final Logger logger = LoggerFactory.getLogger(SensorDataController.class);

    @Autowired
    private SensorDataService sensorDataService;

    @GetMapping("/getSensorDataByType")
    public Result<List<SensorData>> getSensorDataByType(
            @RequestParam("selectedDataType") String selectedDataType) {
        try {
            List<SensorData> list = sensorDataService.getSensorDataByType(selectedDataType);

            if (list.isEmpty()) {
                return Result.fail("暂无该类型的数据");
            }

            logger.info("获取传感器数据成功，类型: {}, 数量: {}", selectedDataType, list.size());
            return Result.success(list);
        } catch (Exception e) {
            logger.error("获取传感器数据失败", e);
            return Result.fail("获取数据失败，请稍后重试");
        }
    }

    @GetMapping("/get10SensorDataByType")
    public Result<List<SensorData>> get10SensorDataByType(
            @RequestParam("selectedDataType") String selectedDataType) {
        try {
            List<SensorData> list = sensorDataService.get10SensorDataByType(selectedDataType);

            if (list.isEmpty()) {
                return Result.fail("暂无该类型的数据");
            }

            logger.info("获取最近10条传感器数据成功，类型: {}", selectedDataType);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("获取最近10条传感器数据失败", e);
            return Result.fail("获取数据失败，请稍后重试");
        }
    }

    @GetMapping("/getAllSensorData")
    public Result<List<SensorData>> getAllSensorData() {
        try {
            List<SensorData> list = sensorDataService.getAllSensorData();

            if (list.isEmpty()) {
                return Result.fail("暂无传感器数据");
            }

            logger.info("获取所有传感器数据成功，数量: {}", list.size());
            return Result.success(list);
        } catch (Exception e) {
            logger.error("获取所有传感器数据失败", e);
            return Result.fail("获取数据失败，请稍后重试");
        }
    }

    @GetMapping("/countOnlineDevices")
    public Result<Integer> countOnlineDevices() {
        try {
            int count = sensorDataService.countOnlineDevices();
            logger.info("在线设备数量: {}", count);
            return Result.success(count);
        } catch (Exception e) {
            logger.error("统计在线设备失败", e);
            return Result.fail("统计失败，请稍后重试");
        }
    }

    @GetMapping("/countAllDevices")
    public Result<Integer> countAllDevices() {
        try {
            int count = sensorDataService.countAllDevices();
            logger.info("设备总数: {}", count);
            return Result.success(count);
        } catch (Exception e) {
            logger.error("统计设备总数失败", e);
            return Result.fail("统计失败，请稍后重试");
        }
    }

    @GetMapping("/getSensorDataWithFilters")
    public Result<List<SensorData>> getSensorDataWithFilters(
            @RequestParam("selectedDataType") String selectedDataType,
            @RequestParam(value = "locationId", required = false) Integer locationId,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "timeSlot", required = false) String timeSlot) {
        try {
            List<SensorData> list = sensorDataService.getSensorDataWithFilters(selectedDataType, locationId, period, timeSlot);
            logger.info("获取筛选传感器数据成功，类型: {}, 位置: {}, 周期: {}, 时间段: {}, 数量: {}",
                    selectedDataType, locationId, period, timeSlot, list.size());
            return Result.success(list);
        } catch (Exception e) {
            logger.error("获取筛选传感器数据失败", e);
            return Result.fail("获取数据失败，请稍后重试");
        }
    }

    @GetMapping("/getDeviceRate")
    public Result<Double> getDeviceRate() {
        try {
            double rate = sensorDataService.calculateDeviceRate();
            logger.info("设备运行率: {}%", rate);
            return Result.success(rate);
        } catch (Exception e) {
            logger.error("计算设备运行率失败", e);
            return Result.fail("计算失败，请稍后重试");
        }
    }
}
