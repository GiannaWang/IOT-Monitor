package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ecnu.edu.iotbackend.common.Result;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        try {
            // 验证用户名和密码
            User user = userService.login(loginUser.getUsername(), loginUser.getPasswordHash());

            if (user == null) {
                return Result.fail("用户名或密码错误");
            }

            logger.info("用户登录成功: {}", user.getUsername());
            return Result.success(user);
        } catch (Exception e) {
            logger.error("登录失败", e);
            return Result.fail("登录失败，请稍后重试");
        }
    }

    @GetMapping("/getUserByUsername")
    public Result<User> getUserByUsername(@RequestParam String username) {
        try {
            User user = userService.getUserByUsername(username);

            if (user == null) {
                return Result.fail("用户不存在");
            }

            return Result.success(user);
        } catch (Exception e) {
            logger.error("查询用户失败", e);
            return Result.fail("查询失败，请稍后重试");
        }
    }

    @PostMapping("/updateAvatar")
    public Result<Boolean> updateAvatar(@RequestParam int userId, @RequestParam String avatarUrl) {
        try {
            boolean success = userService.updateAvatar(userId, avatarUrl);

            if (success) {
                logger.info("更新用户头像成功，用户ID: {}", userId);
                return Result.success(true);
            } else {
                return Result.fail("更新头像失败");
            }
        } catch (Exception e) {
            logger.error("更新头像失败", e);
            return Result.fail("更新失败，请稍后重试");
        }
    }

    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(
            @RequestParam int userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            boolean success = userService.updatePassword(userId, oldPassword, newPassword);

            if (success) {
                logger.info("修改密码成功，用户ID: {}", userId);
                return Result.success(true);
            } else {
                return Result.fail("旧密码错误或修改失败");
            }
        } catch (Exception e) {
            logger.error("修改密码失败", e);
            return Result.fail("修改失败，请稍后重试");
        }
    }
}
