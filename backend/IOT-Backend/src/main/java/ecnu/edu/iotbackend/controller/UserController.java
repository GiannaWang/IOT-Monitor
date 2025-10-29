package ecnu.edu.iotbackend.controller;

import ecnu.edu.iotbackend.entity.User;
import ecnu.edu.iotbackend.mapper.userMapper;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ecnu.edu.iotbackend.common.Result;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 允许前端域名+携带凭证
public class UserController {

    @Autowired
    private userMapper userMapper;


    @GetMapping("/getAll")
    public List<User> all_query() {

        List<User> list= userMapper.findAll();
        System.out.println(list);
        return list;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        // 1. 根据用户名查询数据库中的用户
        User dbUser = userMapper.findByUsername(loginUser.getUsername());

        // 2. 判断用户是否存在
        if (dbUser == null) {
            return Result.fail("用户名不存在");
        }

        // 3. 存在则返回用户信息（包含密码，供前端对比）
        return Result.success(dbUser);
    }

    @PostMapping("/findByUserId")
    public Result<User> findByUserId(@RequestParam int userId) {
        User dbUser = userMapper.findByUserId(userId);
        if (dbUser == null) {
            return Result.fail("");
        }
        return Result.success(dbUser);
    }

    @PostMapping("/updateAvatarByUserId")
    public Result<Null> updateAvatarByUserId(@RequestParam int userId, @RequestParam String newAvatar) {
        boolean result = userMapper.updateAvatar(userId, newAvatar);
        System.out.println("头像更新结果："+newAvatar+result);
        if (!result) {
            System.out.println("failed updateAvatarByUserId");
            return Result.fail("SpringBoot：头像更新失败");
        }
        return Result.success("");
    }

    @PostMapping("/changePassword")
    public Result<Null> changePassword(@RequestParam int userId,  @RequestParam String newPassword) {
        boolean result = userMapper.changePassword(userId, newPassword);
        System.out.println("密码更新结果："+newPassword+result);
        if (!result) {
            System.out.println("failed changePassword");
            return Result.fail("SpringBoot：密码更新失败");
        }
        return Result.success("");
    }

    @PostMapping("/updateLastLoginTime")
    public Result<Null> updateLastLoginTime(@RequestParam int userId, @RequestParam String currentDate) {
        boolean result = userMapper.updateLastLoginTime(userId, currentDate);
        System.out.println("更新登录时间："+ currentDate +result);
        if (!result) {
            System.out.println("failed updateLastLoginTime");
            return Result.fail("SpringBoot：登录时间更新失败");
        }
        return Result.success("");
    }
}
