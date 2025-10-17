<template>
  <div class="admin-container">
    <div class="main-content">
      <div class="profile-column">
        <img           
          :src="selectedAvatar" 
          alt="Profile Picture" 
          style="width:200px;height:200px;border-radius:50%;cursor:pointer;margin-right:16px;">
        <div class="user-info">
          <span style="font-size: 24px; font-weight: bold;">{{ username }}</span>
        </div>
        <button class="profile-edit-button" @click="showAvatarModal = true">修改头像</button>
      </div>
      <div class="content-column">
        <h1>{{ greeting }}, {{ username }}</h1>
        <h3>现在是：{{ currentTime }}</h3>
        <p>您的上次登录时间为：{{ userInfo?.lastLoginTime || '暂无记录' }}</p>
        <p>您的管理员等级为：{{ userInfo?.role }}</p>
        <p>您负责的房间为：{{ userInfo?.room }}</p>

        <button class="button" @click="showPasswordModal = true">修改密码</button>
        <button class="button" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <!-- 密码更改框 -->
    <div class="modal-overlay" v-if="showPasswordModal" @click="showPasswordModal = false">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>修改密码</h2>
          <button class="close-button" @click="showPasswordModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitPasswordChange" class="password-form" id="password-form">
            <div class="label-group">
              <label>当前密码:</label>
              <label>新密码:</label>
              <label>确认新密码:</label>
            </div>
            <div class="input-group">
              <input type="password" id="currentPassword" v-model="currentPassword" required>
              <input type="password" id="newPassword" v-model="newPassword" required>
              <input type="password" id="confirmPassword" v-model="confirmPassword" required>
            </div>
          </form>
          <button type="submit" class="button" form="password-form">提交</button>
        </div>
      </div>
    </div>

    <!-- 头像选择模态框 -->
    <div class="modal-overlay" v-if="showAvatarModal" @click="showAvatarModal = false">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>选择头像</h2>
          <button class="close-button" @click="showAvatarModal = false">&times;</button>
        </div>
        <div class="avatar-grid">
          <div 
            class="avatar-item" 
            v-for="(avatar, index) in availableAvatars" 
            :key="index"
            @click="selectAvatar(avatar)"
          >
            <img :src="avatar" :alt="`Avatar ${index + 1}`" class="avatar-thumbnail">
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import userService from '../utils/userService'

const router = useRouter()
const userInfo = ref(null)
const username = ref('')
const currentTime = ref('')
const userRole = ref('房间管理员') // 示例角色
const userRoom = ref('101号房间') // 示例房间
const lastLoginTime = ref('2024-06-01 10:00:00') // 示例上次登录时间
const greeting = ref('')

// 头像相关状态
const showAvatarModal = ref(false)
// 假设assets文件夹中的头像图片列表，根据实际文件名修改
const availableAvatars = ref([
  '/src/assets/avatar/fall.bmp',
  '/src/assets/avatar/avatar1-1.jpg',
  '/src/assets/avatar/avatar2-1.jpg',
  '/src/assets/avatar/avatar3-1.jpg',
  '/src/assets/avatar/avatar4-1.jpg',
  '/src/assets/avatar/avatar5-1.jpg'
])

// 默认头像
const selectedAvatar = ref('') 

// 密码修改相关状态
const showPasswordModal = ref(false)
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

// 提交密码修改
const submitPasswordChange = async () => {
  if(!userInfo.value) return 
  
    // 前端基础验证
  if (newPassword.value !== confirmPassword.value) {
    alert('新密码和确认密码不匹配')
    return
  }
  if (newPassword.value.length < 6) {
    alert('新密码长度不能少于6位')
    return
  }
  try {
    // 调用userService修改密码
    const result = await userService.changePassword(
      userInfo.value.id,
      currentPassword.value,
      newPassword.value
    )

    if(result.success) {
      alert(result.message)
      showPasswordModal.value = false
      // 清空密码输入框
      currentPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
    } else {
      alert(result.message) // 显示错误信息
    }
  } catch (err) {
    console.error('修改密码失败:', err)
    alert('修改密码失败，请重试')
  }
}


const updateTime = () => {
  const date = new Date()
  currentTime.value = (
    date.getFullYear() + '-' +
    (date.getMonth() + 1).toString().padStart(2, '0') + '-' +
    date.getDate().toString().padStart(2, '0') + ' ' +
    date.getHours().toString().padStart(2, '0') + ':' +
    date.getMinutes().toString().padStart(2, '0') + ':' +
    date.getSeconds().toString().padStart(2, '0')
  )
}

// 获取时间段问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 4) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 24) return '晚上好'
}

// 获取用户名
const loadUserInfo = async () => {
  const storedName = localStorage.getItem('username')
  if (!storedName) {
    router.push('/login')
    return
  }

  try {
    //
    const user = await userService.getUserByUsername(storedName)
    if(user) {
      userInfo.value = user
      username.value = user.username 
      selectedAvatar.value = user.avatar || '/src/assets/avatar/fall.bmp' // 默认头像
      lastLoginTime.value = user.lastLoginTime || '首次登录'
      userRole.value = user.role || '房间管理员'
      userRoom.value = user.room || '101号房间'
    } else {
      alert('用户信息加载失败，请联系管理员')
      router.push('/login')
    }
  }
  catch (err) {
    console.error('加载用户信息失败:', err)
    alert('用户信息加载失败，请联系管理员')
    router.push('/login')
  }
  greeting.value = getGreeting()
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('username') // 清除用户名
  router.push('/login')
}

// 选择头像
const selectAvatar = async (avatar) => {
  if(!userInfo.value) return

  try {
    // 更新用户头像信息
    const success = await userService.updateUserAvatar(userInfo.value.id, avatar)
    if(success) {
      selectedAvatar.value = avatar
      userInfo.value.avatar = avatar
      localStorage.setItem('userAvatar', avatar) // 可选：本地存储头像信息
      alert('头像更新成功')
    } else {
        alert('头像更新失败')
    }
  } catch (err) {
    console.error('更新头像失败:', err)
    alert('头像更新失败，请重试')
  }

  showAvatarModal.value = false
}

// 组件挂载时加载用户信息和更新时间
onMounted(() => {
  loadUserInfo()
  updateTime()
  setInterval(updateTime, 1000) // 每秒更新时间
})
</script>

<style scoped>  
.admin-container {
  padding: 5% 16%;
  background: #f5f6fa;
  color: #222;
}
.main-content {
  display: flex;
  flex-direction: row;
  gap: 32px;
  background: #fff;
  border-radius: 16px;
  box-shadow: var(--card-shadow);
}
.profile-column {
  width: auto;
  display: flex;
  flex-direction: column;
  padding-left: 50px;
  padding-top: 50px;
  padding-bottom: 50px;
  padding-right: 10px;
  border-radius: 16px;
  box-shadow: var(--card-shadow);
}
.user-info {
  font-size: 20px;
  color: #000000;
  margin-top: 16px;
  text-align: left;
}
.content-column {
  width: auto;
  text-align: left;
  padding-bottom: 24px;
  padding-right: 12px;
  /* background: #fff;
  border-radius: 8px;
  box-shadow: var(--card-shadow); */
}
.button {
  display: inline-block; /* 使按钮横向排列 */
  margin-right: 32px; /* 按钮间距 */
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.profile-edit-button {
  width: 200px;
  margin-top: 16px;
  padding: 8px 16px;
  background-color: #f5f6fa;
  color: rgb(88, 88, 88);
  border: 1px solid #c2c3c7;
  border-radius: 4px;
  cursor: pointer;
}


/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-container {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.modal-header {
  padding: 8px 24px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}
.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  transition: color 0.2s;
}
.close-button:hover {
  color: #333;
}
.avatar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
  padding: 24px;
}
.avatar-item {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}
.avatar-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.avatar-thumbnail {
  width: 100%;
  height: 120px;
  object-fit: cover;
  display: block;
}

.modal-body {
  padding: 32px;
}
.password-form {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}
.label-group {
  display: flex;
  width: 100px; 
  flex-direction: column; 
  align-items: flex-start; 
  gap: 16px;
}
.label-group label {
  height: 32px;
  line-height: 32px;
}
.input-group {
  display: flex;
  width: 240px; 
  flex-direction: column; 
  align-items: flex-start; 
  gap: 16px;
}
.input-group input {
  width: 100%;
  height: 32px;
  padding: 0 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
  line-height: 1;
}
</style>  