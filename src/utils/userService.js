// 用户数据服务
import request from './request.js'

const userService = {

  // 1. 根据用户名获取用户
  async getUserByUsername(username) {
    const response = await request.get('/getUserByUsername', { params: { username } });
    return response.code === 200 ? response.data : null;
  },

  // 2. 用户登录
  // 成功后自动将 token 和用户信息存入 localStorage
  async login(username, password) {
    const response = await request.post('/login', {
      username: username,
      passwordHash: password  // 对应后端 User 实体字段名
    });

    if (response.code === 200) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
    }
    return response;
  },

  // 3. 登出：清除本地凭证
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  // 4. 更新用户头像
  async updateUserAvatar(userId, avatarPath) {
    try {
      const response = await request.post('/updateAvatar', {}, {
        params: { userId, avatarUrl: avatarPath }
      });

      if (response.code === 200) {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        user.avatar = avatarPath;
        localStorage.setItem('user', JSON.stringify(user));
        return true;
      }
      return false;
    } catch (error) {
      console.error('更新头像失败:', error);
      return false;
    }
  },

  // 5. 修改密码（由后端用 BCrypt 验证旧密码）
  async changePassword(userId, oldPassword, newPassword) {
    try {
      const response = await request.post('/changePassword', {}, {
        params: { userId, oldPassword, newPassword }
      });

      if (response.code === 200) {
        return true;
      }
      return response.msg || '修改密码失败';
    } catch (error) {
      console.error('修改密码失败:', error);
      return '调用后端修改密码接口失败';
    }
  },

  // 6. 更新最后登录时间
  async updateUserLoginTime(userId) {
    try {
      await request.post('/updateLastLoginTime', {}, { params: { userId } });
    } catch (error) {
      console.error('更新登录时间失败:', error);
    }
  }
}

export default userService
