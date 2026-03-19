import request from './request.js'

const userService = {
  async getUserByUsername(username) {
    const response = await request.get('/getUserByUsername', { params: { username } })
    return response.code === 200 ? response.data : null
  },

  async login(username, password) {
    const response = await request.post('/login', {
      username,
      passwordHash: password
    })

    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      localStorage.setItem('isLoggedIn', 'true')
    }
    return response
  },

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('isLoggedIn')
  },

  async updateUserAvatar(userId, avatarPath) {
    try {
      const response = await request.post('/updateAvatar', null, {
        params: { userId, avatarUrl: avatarPath }
      })

      if (response.code === 200) {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        user.avatar = avatarPath
        localStorage.setItem('user', JSON.stringify(user))
        return true
      }
      return false
    } catch (error) {
      console.error('更新头像失败:', error)
      return false
    }
  },

  async changePassword(userId, oldPassword, newPassword) {
    try {
      const response = await request.post('/changePassword', null, {
        params: { userId, oldPassword, newPassword }
      })

      if (response.code === 200) {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        user.passwordHash = newPassword
        localStorage.setItem('user', JSON.stringify(user))
        return true
      }
      return response.msg || '修改密码失败'
    } catch (error) {
      console.error('修改密码失败:', error)
      return '调用后端修改密码接口失败'
    }
  },

  async updateUserLoginTime(userId) {
    try {
      await request.post('/updateLastLoginTime', null, { params: { userId } })
    } catch (error) {
      console.error('更新登录时间失败:', error)
    }
  }
}

export default userService
