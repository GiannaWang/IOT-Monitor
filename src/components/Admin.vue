<template>
  <div class="admin-container">
    <h1>{{ greeting }}, {{ username }}</h1>
    <p>这里是管理员专用的内容。</p>
    <button class="button" @click="handleLogout">退出登录</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const greeting = ref('')

// 获取时间段问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 4) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 23) return '晚上好'
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
})
</script>

<style scoped>  
.admin-container {
  padding: 32px;
  background: #f5f6fa;
  color: #222;
}
.button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>