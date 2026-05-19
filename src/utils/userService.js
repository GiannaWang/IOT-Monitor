import request from './request.js'

const TOKEN_KEY = 'token'
const USER_KEY = 'user'

const userService = {
  saveAuth(token, user) {
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
    localStorage.setItem('isLoggedIn', 'true')
  },

  getStoredUser() {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  },

  getToken() {
    return localStorage.getItem(TOKEN_KEY)
  },

  isLoggedIn() {
    return Boolean(this.getToken())
  },

  async login(username, password) {
    const response = await request.post('/login', {
      username,
      passwordHash: password
    })

    const authData = response?.data
    if (response?.code === 200 && authData?.token && authData?.user) {
      this.saveAuth(response.data.token, response.data.user)
    }

    return response
  },

  async fetchCurrentUser() {
    const response = await request.get('/me')
    if (response?.code === 200 && response.data) {
      localStorage.setItem(USER_KEY, JSON.stringify(response.data))
      localStorage.setItem('isLoggedIn', 'true')
      return response.data
    }
    this.clearAuth()
    return null
  },

  async getUserByUsername(username) {
    const response = await request.get('/getUserByUsername', { params: { username } })
    return response.code === 200 ? response.data : null
  },

  async getAllUsers() {
    const response = await request.get('/users')
    return response.code === 200 ? response.data || [] : []
  },

  async assignRooms(userId, roomIds) {
    const response = await request.post('/users/assign-rooms', {
      userId,
      roomIds
    })
    return response.code === 200
  },

  clearAuth() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    localStorage.removeItem('isLoggedIn')
  },

  logout() {
    this.clearAuth()
  },

  async updateUserAvatar(userId, avatarPath) {
    try {
      const response = await request.post('/updateAvatar', null, {
        params: { userId, avatarUrl: avatarPath }
      })

      if (response.code === 200) {
        const user = this.getStoredUser() || {}
        user.avatar = avatarPath
        localStorage.setItem(USER_KEY, JSON.stringify(user))
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
        return true
      }
      return response.msg || '修改密码失败'
    } catch (error) {
      console.error('修改密码失败:', error)
      return '调用后端修改密码接口失败'
    }
  }
}

export default userService
