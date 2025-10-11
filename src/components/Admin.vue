<template>
  <div class="admin-container">
    <div class="main-content">
      <div class="profile-column">
        <img src="/src/assets/fall.bmp" alt="Profile Picture" 
          style="width:200px;height:200px;border-radius:50%;cursor:pointer;margin-right:16px;">
        <div class="user-info">
          <span style="font-size: 24px; font-weight: bold;">{{ username }}</span>
        </div>
        <button class="profile-edit-button" @click="handleEdit">修改头像</button>
      </div>
      <div class="content-column">
        <h1>{{ greeting }}, {{ username }}</h1>
        <h3>现在是：{{ currentTime }}</h3>
        <p>您的上次登录时间为：{{ lastLoginTime }}</p>
        <p>您的管理员等级为：{{ userRole }}</p>
        <p>您负责的房间为：{{ userRoom }}</p>
        
        <button class="button" @click="handleKeywordChange">修改密码</button>
        <button class="button" @click="handleLogout">退出登录</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const greeting = ref('')
const currentTime = ref('')
const userRole = ref('房间管理员') // 示例角色
const userRoom = ref('101号房间') // 示例房间
const lastLoginTime = ref('2024-06-01 10:00:00') // 示例上次登录时间

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
const loadUserInfo = () => {
  const storedName = localStorage.getItem('username')
  if (storedName) {
    username.value = storedName
  } else {
    username.value = '管理员' // 默认值
  }
  greeting.value = getGreeting()
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('username') // 清除用户名
  router.push('/login')
}

onMounted(() => {
  loadUserInfo()
  updateTime()
  setInterval(updateTime, 1000) // 每秒更新时间
})
</script>

<style scoped>  
.admin-container {
  padding: 20%;
  background: #f5f6fa;
  color: #222;
  padding-top: 5%;
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
  flex: 1;
  text-align: left;
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
</style>