package ecnu.edu.iotbackend.entity;

/**
 * 登录成功响应体：包含 JWT token 和脱敏后的用户信息
 */
public class LoginResult {

    private String token;
    private User user;

    public LoginResult(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
