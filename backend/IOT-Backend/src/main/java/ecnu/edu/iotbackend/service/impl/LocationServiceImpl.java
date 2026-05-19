package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.Location;
import ecnu.edu.iotbackend.mapper.AlertMapper;
import ecnu.edu.iotbackend.mapper.LocationMapper;
import ecnu.edu.iotbackend.mapper.SensorDeviceMapper;
import ecnu.edu.iotbackend.security.CurrentUserProvider;
import ecnu.edu.iotbackend.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationMapper locationMapper;
    private final CurrentUserProvider currentUserProvider;
    private final SensorDeviceMapper sensorDeviceMapper;
    private final AlertMapper alertMapper;

    public LocationServiceImpl(LocationMapper locationMapper,
                               CurrentUserProvider currentUserProvider,
                               SensorDeviceMapper sensorDeviceMapper,
                               AlertMapper alertMapper) {
        this.locationMapper = locationMapper;
        this.currentUserProvider = currentUserProvider;
        this.sensorDeviceMapper = sensorDeviceMapper;
        this.alertMapper = alertMapper;
    }

    @Override
    public List<Location> getAllLocations() {
        if (currentUserProvider.isAdmin()) {
            return locationMapper.getAllLocations();
        }

        List<Integer> roomIds = currentUserProvider.getAccessibleRoomIds();
        if (roomIds == null) {
            return locationMapper.getAllLocations();
        }
        if (roomIds.isEmpty()) {
            return Collections.emptyList();
        }
        return locationMapper.getLocationsByIds(roomIds);
    }

    @Override
    public Location getLocationById(Integer id) {
        return locationMapper.getLocationById(id);
    }

    @Override
    public Location createLocation(Location location) {
        ensureAdmin();
        validateLocation(location);
        if (locationMapper.countByRoomIdentity(
                location.getBuildingname().trim(),
                location.getFloornumber(),
                location.getRoomnumber()
        ) > 0) {
            throw new IllegalArgumentException("房间已存在");
        }

        location.setBuildingname(location.getBuildingname().trim());
        location.setDescription(location.getDescription() == null ? "" : location.getDescription().trim());
        locationMapper.insertLocation(location);
        return location;
    }

    @Override
    public boolean deleteLocation(Integer id) {
        ensureAdmin();
        if (id == null) {
            throw new IllegalArgumentException("房间ID不能为空");
        }

        Location location = locationMapper.getLocationById(id);
        if (location == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        if (sensorDeviceMapper.countByLocationId(id) > 0) {
            throw new IllegalStateException("该房间下仍有设备，无法删除");
        }
        if (alertMapper.countByLocationId(id) > 0) {
            throw new IllegalStateException("该房间下仍有历史告警，无法删除");
        }
        return locationMapper.deleteLocationById(id) > 0;
    }

    private void ensureAdmin() {
        if (!currentUserProvider.isAdmin()) {
            throw new IllegalStateException("没有权限");
        }
    }

    private void validateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("房间信息不能为空");
        }
        if (location.getBuildingname() == null || location.getBuildingname().trim().isEmpty()) {
            throw new IllegalArgumentException("楼栋不能为空");
        }
        if (location.getFloornumber() == null || location.getFloornumber() <= 0) {
            throw new IllegalArgumentException("楼层必须大于0");
        }
        if (location.getRoomnumber() == null || location.getRoomnumber() <= 0) {
            throw new IllegalArgumentException("房间号必须大于0");
        }
    }
}
