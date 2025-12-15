package ecnu.edu.iotbackend.service;

import ecnu.edu.iotbackend.entity.User;

/**
 * 用户业务逻辑接口
 */
public interface UserService {

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户信息，失败返回null
     */
    User login(String username, String password);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 是否成功
     */
    boolean updateAvatar(int userId, String avatarUrl);

    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updatePassword(int userId, String oldPassword, String newPassword);

    /**
     * 更新最后登录时间
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateLastLoginTime(int userId);
}
