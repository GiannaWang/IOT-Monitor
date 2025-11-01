package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.SensorData;
import ecnu.edu.iotbackend.mapper.SensorDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 允许前端域名+携带凭证
public class SensorDataController {

    @Autowired
    private SensorDataMapper sensorDataMapper;

    @GetMapping("/getSensorDataByType")
    public Result<List<SensorData>> getSensorDataByType(
            @RequestParam("selectedDataType") String selectedDataType) {
        List<SensorData> list = sensorDataMapper.getSensorDataByType(selectedDataType);

        if(list.isEmpty()){
            return Result.fail("后端获取数据失败/表为空");
        }

        System.out.println(list);
        return Result.success(list);
    }

    @GetMapping("/getAllSensorData")
    public Result<List<SensorData>> getAllSensorData() {
        List<SensorData> list = sensorDataMapper.getAllSensorData();
        if(list.isEmpty()){
            return Result.fail("后端获取数据失败/表为空");
        }

        System.out.println(list);
        return Result.success(list);
    }

    @GetMapping("/countOnlineDevices")
    public Result<Integer> countOnlineDevices() {
        int count = sensorDataMapper.countOnlineDevices();
        System.out.println("online devices: "+count);
        return Result.success(count);
    }

    @GetMapping("/countAllDevices")
    public Result<Integer> countAllDevices() {
        int count = sensorDataMapper.countAllDevices();
        System.out.println("total devices: "+count);
        return Result.success(count);
    }


}
