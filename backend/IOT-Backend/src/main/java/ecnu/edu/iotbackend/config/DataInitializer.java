package ecnu.edu.iotbackend.config;

import ecnu.edu.iotbackend.entity.Location;
import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.LocationMapper;
import ecnu.edu.iotbackend.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper,
                           LocationMapper locationMapper,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        ensureDefaultLocations();
        ensureDefaultAdmin();
        ensureDefaultRoomUser();
    }

    private void ensureDefaultLocations() {
        if (locationMapper.countLocations() > 0) {
            return;
        }

        List<Location> defaultLocations = Arrays.asList(
                buildLocation("A", 1, 101, "Lab Room 101"),
                buildLocation("A", 1, 102, "Lab Room 102"),
                buildLocation("A", 2, 201, "Lab Room 201")
        );

        defaultLocations.forEach(locationMapper::insertLocation);
        logger.info("已初始化默认房间数据，共 {} 个", defaultLocations.size());
    }

    private void ensureDefaultAdmin() {
        if (userMapper.getUserByUsername("admin") != null) {
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole("admin");
        admin.setAvatar("/default.png");
        admin.setLastLoginTime("-");
        admin.setRoom("All Rooms");
        userMapper.insertUser(admin);
        logger.info("已初始化默认管理员账号：admin / admin123");
    }

    private void ensureDefaultRoomUser() {
        if (userMapper.getUserByUsername("room_user") != null) {
            return;
        }

        User roomUser = new User();
        roomUser.setUsername("room_user");
        roomUser.setPasswordHash(passwordEncoder.encode("user123"));
        roomUser.setRole("user");
        roomUser.setAvatar("/default.png");
        roomUser.setLastLoginTime("-");
        roomUser.setRoom("A-1F-101");
        userMapper.insertUser(roomUser);

        Integer firstLocationId = locationMapper.getFirstLocationId();
        if (firstLocationId != null) {
            userMapper.insertRoomAssignments(roomUser.getUserId(), List.of(firstLocationId));
        }

        logger.info("已初始化默认普通用户账号：room_user / user123");
    }

    private Location buildLocation(String building, int floor, int room, String description) {
        Location location = new Location();
        location.setBuildingname(building);
        location.setFloornumber(floor);
        location.setRoomnumber(room);
        location.setDescription(description);
        return location;
    }
}
