package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    @Select("SELECT * FROM users WHERE username = #{username}")
    User getUserByUsername(String username);

    // 根据用户ID查询用户
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User getUserById(int userId);

    // 更新用户头像
    @Update("UPDATE users SET avatar = #{avatarUrl} WHERE user_id = #{userId}")
    void updateAvatar(@Param("userId") int userId, @Param("avatarUrl") String avatarUrl);

    // 更新用户密码
    @Update("UPDATE users SET password_hash = #{newPassword} WHERE user_id = #{userId}")
    void updatePassword(@Param("userId") int userId, @Param("newPassword") String newPassword);

    // 更新最后登录时间（使用数据库当前时间）
    @Update("UPDATE users SET lastLoginTime = NOW() WHERE user_id = #{userId}")
    void updateLastLoginTime(@Param("userId") int userId);
}
