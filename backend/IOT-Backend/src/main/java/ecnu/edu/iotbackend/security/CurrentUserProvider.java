package ecnu.edu.iotbackend.security;

import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collections;
import java.util.List;

@Component
public class CurrentUserProvider {

    public static final String ATTR_USERNAME = "currentUsername";
    public static final String ATTR_ROLE = "currentRole";

    private final UserMapper userMapper;

    public CurrentUserProvider(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public String getCurrentUsername() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        Object username = attributes.getAttribute(ATTR_USERNAME, RequestAttributes.SCOPE_REQUEST);
        return username == null ? null : username.toString();
    }

    public String getCurrentRole() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        Object role = attributes.getAttribute(ATTR_ROLE, RequestAttributes.SCOPE_REQUEST);
        return role == null ? null : role.toString();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null || username.isBlank()) {
            return null;
        }
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        user.setRoomIds(userMapper.getRoomIdsByUserId(user.getUserId()));
        user.setRoomNames(userMapper.getRoomNamesByUserId(user.getUserId()));
        return user;
    }

    public boolean isAdmin() {
        String role = getCurrentRole();
        return "admin".equalsIgnoreCase(role) || "super_admin".equalsIgnoreCase(role);
    }

    public List<Integer> getAccessibleRoomIds() {
        if (isAdmin()) {
            return null;
        }
        User user = getCurrentUser();
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getRoomIds();
    }
}
