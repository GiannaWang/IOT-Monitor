package ecnu.edu.iotbackend.service.impl;

import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.UserMapper;
import ecnu.edu.iotbackend.security.CurrentUserProvider;
import ecnu.edu.iotbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Override
    public User login(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        if (user == null) return null;

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            updateLastLoginTime(user.getUserId());
            return enrichUser(user);
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return enrichUser(userMapper.getUserByUsername(username));
    }

    @Override
    public User getCurrentUserProfile() {
        User currentUser = currentUserProvider.getCurrentUser();
        return currentUser == null ? null : enrichUser(currentUser);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userMapper.getAllUsers();
        List<User> enrichedUsers = new ArrayList<>();
        for (User user : users) {
            enrichedUsers.add(enrichUser(user));
        }
        return enrichedUsers;
    }

    @Override
    public boolean assignRooms(int userId, List<Integer> roomIds) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null || "admin".equalsIgnoreCase(user.getRole())) {
                return false;
            }

            List<Integer> distinctRoomIds = roomIds == null
                    ? new ArrayList<>()
                    : new ArrayList<>(new LinkedHashSet<>(roomIds));

            userMapper.deleteRoomAssignmentsByUserId(userId);
            if (!distinctRoomIds.isEmpty()) {
                userMapper.insertRoomAssignments(userId, distinctRoomIds);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    private User enrichUser(User user) {
        if (user == null) {
            return null;
        }

        user.setRoomIds(userMapper.getRoomIdsByUserId(user.getUserId()));
        user.setRoomNames(userMapper.getRoomNamesByUserId(user.getUserId()));
        user.setRoom(String.join(", ", user.getRoomNames()));
        return user;
    }
}
