package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.Location;
import ecnu.edu.iotbackend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/all")
    public Result<List<Location>> getAllLocations() {
        try {
            List<Location> locations = locationService.getAllLocations();
            logger.info("Fetched locations, count={}", locations.size());
            return Result.success(locations);
        } catch (Exception e) {
            logger.error("Failed to fetch locations", e);
            return Result.fail("获取房间列表失败");
        }
    }

    @GetMapping("/{id}")
    public Result<Location> getLocationById(@PathVariable Integer id) {
        try {
            Location location = locationService.getLocationById(id);
            return location != null ? Result.success(location) : Result.fail("房间不存在");
        } catch (Exception e) {
            logger.error("Failed to fetch location detail", e);
            return Result.fail("获取房间详情失败");
        }
    }

    @PostMapping
    public Result<Location> createLocation(@RequestBody Location location) {
        try {
            Location created = locationService.createLocation(location);
            logger.info("Created location id={}", created.getId());
            return Result.success(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to create location", e);
            return Result.fail("新增房间失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteLocation(@PathVariable Integer id) {
        try {
            boolean success = locationService.deleteLocation(id);
            return success ? Result.success(true) : Result.fail("删除房间失败");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to delete location id={}", id, e);
            return Result.fail("删除房间失败");
        }
    }
}
