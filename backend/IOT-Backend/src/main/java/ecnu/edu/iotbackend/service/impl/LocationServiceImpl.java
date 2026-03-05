package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.Location;
import ecnu.edu.iotbackend.mapper.LocationMapper;
import ecnu.edu.iotbackend.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Location 服务实现类
 */
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationMapper locationMapper;

    @Override
    public List<Location> getAllLocations() {
        return locationMapper.getAllLocations();
    }

    @Override
    public Location getLocationById(Integer id) {
        return locationMapper.getLocationById(id);
    }
}
