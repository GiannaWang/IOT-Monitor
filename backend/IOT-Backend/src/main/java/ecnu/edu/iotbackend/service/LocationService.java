package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.Location;

import java.util.List;

/**
 * Location 服务接口
 */
public interface LocationService {

    /**
     * 获取所有位置
     */
    List<Location> getAllLocations();

    /**
     * 根据ID获取位置
     */
    Location getLocationById(Integer id);
}
