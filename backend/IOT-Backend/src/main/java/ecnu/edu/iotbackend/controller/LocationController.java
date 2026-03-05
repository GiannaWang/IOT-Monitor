package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.Location;
import ecnu.edu.iotbackend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationService locationService;

    /**
     * 获取所有位置
     */
    @GetMapping("/all")
    public Result<List<Location>> getAllLocations() {
        try {
            List<Location> locations = locationService.getAllLocations();
            logger.info("获取所有位置成功，数量: {}", locations.size());
            return Result.success(locations);
        } catch (Exception e) {
            logger.error("获取所有位置失败", e);
            return Result.fail("获取位置列表失败");
        }
    }

    /**
     * 根据ID获取位置
     */
    @GetMapping("/{id}")
    public Result<Location> getLocationById(@PathVariable Integer id) {
        try {
            Location location = locationService.getLocationById(id);
            if (location != null) {
                return Result.success(location);
            } else {
                return Result.fail("位置不存在");
            }
        } catch (Exception e) {
            logger.error("获取位置详情失败", e);
            return Result.fail("获取位置详情失败");
        }
    }
}
