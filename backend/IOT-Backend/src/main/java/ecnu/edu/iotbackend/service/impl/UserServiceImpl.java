package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.UserMapper;
import ecnu.edu.iotbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户业务逻辑实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        // 验证密码（实际项目中应该使用加密后的密码对比）
        if (user.getPasswordHash().equals(password)) {
            // 更新最后登录时间
            updateLastLoginTime(user.getUserId());
            return user;
        }

        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public boolean updateAvatar(int userId, String avatarUrl) {
        try {
            userMapper.updateAvatar(userId, avatarUrl);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        try {
            User user = userMapper.getUserById(userId);

            if (user == null) {
                return false;
            }

            // 验证旧密码
            if (!user.getPasswordHash().equals(oldPassword)) {
                return false;
            }

            // 更新密码（实际项目中应该加密后存储）
            userMapper.updatePassword(userId, newPassword);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateLastLoginTime(int userId) {
        try {
            userMapper.updateLastLoginTime(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
