package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface userMapper {

    // 获取所有用户信息（返回完整用户信息，包含密码）
    @Select("select * from users")
    public List<User> findAll();

    // 根据用户名查询用户（返回完整用户信息，包含密码）
    @Select("SELECT * FROM users WHERE username = #{username}")
    public User findByUsername(String username);

    // 根据用户id查询用户（返回完整用户信息，包含密码）
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    public User findByUserId(int userId);

    @Update("UPDATE users SET avatar= #{newAvatar} WHERE user_id = #{userId}")
    public boolean updateAvatar(@Param("userId") int userId, @Param("newAvatar") String newAvatar);

    @Update("UPDATE users SET password_hash= #{newPassword} WHERE user_id = #{userId}")
    public boolean changePassword(@Param("userId") int userId, @Param("newPassword") String newPassword);

    @Update("UPDATE users SET lastLoginTime = #{currentDate} where user_id = #{userId}")
    public boolean updateLastLoginTime(@Param("userId") int userId, @Param("currentDate") String currentDate);
}
