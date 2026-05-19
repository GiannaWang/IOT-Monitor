package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.Location;

import java.util.List;

public interface LocationService {

    List<Location> getAllLocations();

    Location getLocationById(Integer id);

    Location createLocation(Location location);

    boolean deleteLocation(Integer id);
}
