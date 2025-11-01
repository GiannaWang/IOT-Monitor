// 用户数据服务
import request from './request.js'

// 用户服务对象
const userService = {

  // 1. 获取所有用户
  async getAllUsers() {
    const response = await request.get('/getAll')
    return response.users
  },
  
  // 2. 根据用户名获取用户
  async getUserByUsername(username) {
  // 调用后端登录接口：POST 请求，传递用户名和密码
    const response = await request.post('/findByUsername?username='
       + encodeURIComponent(username));
    
    // 后端返回规则：
    // - 成功：用户对象（不含密码）
    // - 失败：错误字符串（如"用户名不存在"）
    if (typeof response === 'object') {
      // 登录成功，返回用户信息
      return response;
    } else {
      // 登录失败，返回 null（前端可捕获错误消息）
      console.error('登录失败:', response);
      return null;
    }
  },
  
  // 3. 用户登录验证（后端接口：对应后端 UserController 的 /api/user/login）
  async login(username, password) {
    // 调用后端登录接口：POST 请求，传递用户名和密码
    const response = await request.post('/login', {
      username: username,
      password_hash: password
    });
    
    // 后端返回规则：
    // - 成功：{ code: 200, data: 用户对象（不含密码） }
    // - 失败：{ code: 401, message: '错误消息' }
    if (response.code === 200) {
      // 登录成功，返回用户信息
      return response;
    } else {
      // 登录失败，返回 null（前端可捕获错误消息）
      console.error('登录失败:', response);
      return null;
    }
  },
  
  // 4. 更新用户头像
  async updateUserAvatar(userId, avatarPath) {
    try {
      const response = await request.post('/updateAvatarByUserId', {}, {
        params: {
          userId: userId,
          newAvatar: avatarPath
        }
      });

      // 后端返回规则：
      // - 成功：{ code: 200, message: '头像更新成功' }
      // - 失败：{ code: 500, message: '更新失败原因' }
      if (response.code === 200) {

        // 1. 正确读取并解析 localStorage 中的 user 对象
        const userStr = localStorage.getItem('user');
        if (!userStr) {
          console.error('未找到用户信息');
          return false;
        }
        const user = JSON.parse(userStr); // 解析为JS对象

        // 2. 修改 avatar 属性
        user.avatar = avatarPath; 

        // 3. 重新存入 localStorage（转成JSON字符串）
        localStorage.setItem('user', JSON.stringify(user));

        return true;
      } else {
        console.error('code-500, 后端返回更新头像失败:', response.message);
        return false;
      }
    } catch (error) {
      console.error('调用后端更新头像接口失败:', error);
      return false;
    }
    
  },
  
  // 5. 修改密码
  async changePassword(userId, oldPassword, newPassword) {
    try {
      // 解析user对象并获取password，不存在则返回null
      const userPassword = JSON.parse(localStorage.getItem('user')).passwordHash;
      if(oldPassword !== userPassword) {
        return ('原密码错误');
      } else {
        const response = await request.post('/changePassword', {}, {
          params: {
            userId: userId,
            newPassword: newPassword
          }
        });

        // 后端返回规则：
        // - 成功：{ code: 200, message: '密码修改成功' }
        // - 失败：{ code: 500, message: '修改失败原因' }
        if (response.code === 200) {
          const userStr = localStorage.getItem('user');
          if (!userStr) {
            console.error('未找到用户信息');
            return false;
          }
          const user = JSON.parse(userStr); // 解析为JS对象

          // 2. 修改 avatar 属性
          user.passwordHash = newPassword; 

          // 3. 重新存入 localStorage（转成JSON字符串）
          localStorage.setItem('user', JSON.stringify(user));

          return true;
        } else {
          console.error('code-500, 后端返回修改密码失败:', response.message);
          return ('修改密码失败: ' + response.message);
        }
      }
    } catch (error) {
      console.error('调用后端修改密码接口失败:', error);
      return ('调用后端修改密码接口失败');
    }
  },

  // 6. 更新用户登录时间
  async updateUserLoginTime(userId) {
    try {
      const date = new Date()
      const currentDate = (
        date.getFullYear() + '/' +
        (date.getMonth() + 1).toString().padStart(2, '0') + '/' +
        date.getDate().toString().padStart(2, '0')
      );
      const response = await request.post('/updateLastLoginTime', {}, {
        params: {
          userId: userId,
          currentDate: currentDate
        }
      });
    } catch (error) {
      console.error('调用后端更新登录时间接口失败:', error);
    }
  }
}

export default userService
    