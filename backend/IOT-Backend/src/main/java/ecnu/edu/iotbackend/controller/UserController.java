package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.LoginResult;
import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.entity.UserRoomAssignmentRequest;
import ecnu.edu.iotbackend.security.CurrentUserProvider;
import ecnu.edu.iotbackend.service.UserService;
import ecnu.edu.iotbackend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CurrentUserProvider currentUserProvider;

    public UserController(UserService userService, JwtUtil jwtUtil, CurrentUserProvider currentUserProvider) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody User loginUser) {
        try {
            User user = userService.login(loginUser.getUsername(), loginUser.getPasswordHash());
            if (user == null) {
                return Result.fail("用户名或密码错误");
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            user.setPasswordHash(null);
            logger.info("用户登录成功: {}", user.getUsername());
            return Result.success(new LoginResult(token, user));
        } catch (Exception e) {
            logger.error("登录异常", e);
            return Result.fail("登录失败，请稍后重试");
        }
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        try {
            User currentUser = userService.getCurrentUserProfile();
            if (currentUser == null) {
                return Result.fail("用户不存在");
            }
            currentUser.setPasswordHash(null);
            return Result.success(currentUser);
        } catch (Exception e) {
            logger.error("获取当前用户失败", e);
            return Result.fail("获取当前用户失败");
        }
    }

    @GetMapping("/getUserByUsername")
    public Result<User> getUserByUsername(@RequestParam String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            user.setPasswordHash(null);
            return Result.success(user);
        } catch (Exception e) {
            logger.error("查询用户失败", e);
            return Result.fail("查询失败，请稍后重试");
        }
    }

    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        try {
            if (!currentUserProvider.isAdmin()) {
                return Result.fail("没有权限");
            }

            List<User> users = userService.getAllUsers();
            users.forEach(user -> user.setPasswordHash(null));
            return Result.success(users);
        } catch (Exception e) {
            logger.error("获取用户列表失败", e);
            return Result.fail("获取用户列表失败");
        }
    }

    @PostMapping("/users/assign-rooms")
    public Result<Boolean> assignRooms(@RequestBody UserRoomAssignmentRequest request) {
        try {
            if (!currentUserProvider.isAdmin()) {
                return Result.fail("没有权限");
            }
            if (request.getUserId() == null) {
                return Result.fail("缺少用户ID");
            }

            boolean success = userService.assignRooms(request.getUserId(), request.getRoomIds());
            return success ? Result.success(true) : Result.fail("房间分配失败");
        } catch (Exception e) {
            logger.error("分配房间失败", e);
            return Result.fail("分配房间失败");
        }
    }

    @PostMapping("/updateAvatar")
    public Result<Boolean> updateAvatar(@RequestParam int userId, @RequestParam String avatarUrl) {
        try {
            boolean success = userService.updateAvatar(userId, avatarUrl);
            return success ? Result.success(true) : Result.fail("更新头像失败");
        } catch (Exception e) {
            logger.error("更新头像失败", e);
            return Result.fail("更新失败，请稍后重试");
        }
    }

    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(@RequestParam int userId,
                                          @RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        try {
            boolean success = userService.updatePassword(userId, oldPassword, newPassword);
            return success ? Result.success(true) : Result.fail("旧密码错误");
        } catch (Exception e) {
            logger.error("修改密码失败", e);
            return Result.fail("修改失败，请稍后重试");
        }
    }
}
