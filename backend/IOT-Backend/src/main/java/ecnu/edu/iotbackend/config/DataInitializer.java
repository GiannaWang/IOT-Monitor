package ecnu.edu.iotbackend.config;

import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动时自动初始化默认管理员账号
 * 仅在 users 表为空时执行，避免重复创建
 */
@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userMapper.getUserByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("admin");
            admin.setAvatar("/default.png");
            admin.setLastLoginTime("-");
            admin.setRoom("全部");
            userMapper.insertUser(admin);
            logger.info("已自动创建默认管理员账号：admin / admin123（请登录后修改密码）");
        }
    }
}
