package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    @Select("SELECT * FROM users WHERE username = #{username}")
    User getUserByUsername(String username);

    // 根据用户ID查询用户
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User getUserById(int userId);

    @Select("SELECT * FROM users ORDER BY user_id")
    List<User> getAllUsers();

    // 更新用户头像
    @Update("UPDATE users SET avatar = #{avatarUrl} WHERE user_id = #{userId}")
    void updateAvatar(@Param("userId") int userId, @Param("avatarUrl") String avatarUrl);

    // 更新用户密码
    @Update("UPDATE users SET password_hash = #{newPassword} WHERE user_id = #{userId}")
    void updatePassword(@Param("userId") int userId, @Param("newPassword") String newPassword);

    // 更新最后登录时间（使用数据库当前时间）
    @Update("UPDATE users SET lastLoginTime = NOW() WHERE user_id = #{userId}")
    void updateLastLoginTime(@Param("userId") int userId);

    @Insert("INSERT INTO users (username, password_hash, role, avatar, lastLoginTime, room) VALUES (#{username}, #{passwordHash}, #{role}, #{avatar}, #{lastLoginTime}, #{room})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insertUser(User user);

    @Select("SELECT location_id FROM user_room_assignments WHERE user_id = #{userId} ORDER BY location_id")
    List<Integer> getRoomIdsByUserId(@Param("userId") int userId);

    @Select("SELECT CONCAT(l.buildingname, '-', l.floornumber, 'F-', l.roomnumber) " +
            "FROM user_room_assignments ura " +
            "JOIN locations l ON l.id = ura.location_id " +
            "WHERE ura.user_id = #{userId} ORDER BY ura.location_id")
    List<String> getRoomNamesByUserId(@Param("userId") int userId);

    @Delete("DELETE FROM user_room_assignments WHERE user_id = #{userId}")
    int deleteRoomAssignmentsByUserId(@Param("userId") int userId);

    @Insert({
            "<script>",
            "INSERT INTO user_room_assignments (user_id, location_id) VALUES ",
            "<foreach collection='roomIds' item='roomId' separator=','>",
            "(#{userId}, #{roomId})",
            "</foreach>",
            "</script>"
    })
    int insertRoomAssignments(@Param("userId") int userId, @Param("roomIds") List<Integer> roomIds);
}
