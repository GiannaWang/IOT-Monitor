// 用户数据服务
import usersData from '../../public/users.json'

// 可选：模拟API延迟
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

// 用户服务对象
const userService = {
  // 获取所有用户
  async getAllUsers() {
    await delay(300) // 可选：模拟网络请求延迟
    return [...usersData]
  },
  
  // 根据用户名获取用户
  async getUserByUsername(username) {
    await delay(200)
    return usersData.find(user => user.username === username) || null
  },
  
  // 用户登录验证
  async login(username, password) {
    await delay(300)
    const user = usersData.find(u => u.username === username && u.password === password)
    
    if (user) {
      // 更新最后登录时间
      const updatedUser = {
        ...user,
        lastLoginTime: new Date().toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
      }
      
      // 保存更新
      this.updateUser(updatedUser)
      return updatedUser
    }
    
    return null
  },
  
  // 更新用户信息
  async updateUser(updatedUser) {
    await delay(300)
    const index = usersData.findIndex(u => u.id === updatedUser.id)
    
    if (index !== -1) {
      usersData[index] = { ...usersData[index], ...updatedUser }
      
      // 注意：这里需要调用后端API将更新保存到服务器
      // 前端仅修改内存中的数据，刷新后会恢复
      console.log('用户信息已更新:', usersData[index])
      return true
    }
    
    return false
  },
  
  // 更新用户头像
  async updateUserAvatar(userId, avatarPath) {
    await delay(200)
    return this.updateUser({ id: userId, avatar: avatarPath })
  },
  
  // 修改密码
  async changePassword(userId, oldPassword, newPassword) {
    await delay(300)
    const user = usersData.find(u => u.id === userId)
    
    if (!user) return { success: false, message: '用户不存在' }
    if (user.password !== oldPassword) return { success: false, message: '原密码不正确' }
    
    await this.updateUser({ id: userId, password: newPassword })
    return { success: true, message: '密码修改成功' }
  }
}

export default userService
    