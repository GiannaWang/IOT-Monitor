package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.UserMapper;
import ecnu.edu.iotbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User login(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        if (user == null) return null;

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
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
            if (user == null) return false;

            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                return false;
            }

            userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
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
