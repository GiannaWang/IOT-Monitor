package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.common.Result;
import ecnu.edu.iotbackend.entity.LoginResult;
import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.service.UserService;
import ecnu.edu.iotbackend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录接口（白名单，无需 token）
     * 成功返回 JWT token + 脱敏用户信息
     */
    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody User loginUser) {
        try {
            User user = userService.login(loginUser.getUsername(), loginUser.getPasswordHash());
            if (user == null) {
                return Result.fail("用户名或密码错误");
            }
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            user.setPasswordHash(null); // 不把密码哈希返回给前端
            logger.info("用户登录成功: {}", user.getUsername());
            return Result.success(new LoginResult(token, user));
        } catch (Exception e) {
            logger.error("登录异常", e);
            return Result.fail("登录失败，请稍后重试");
        }
    }

    @GetMapping("/getUserByUsername")
    public Result<User> getUserByUsername(@RequestParam String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) return Result.fail("用户不存在");
            user.setPasswordHash(null);
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
