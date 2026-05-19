<template>
  <div class="login-page">
    <div class="login-card">
      <div class="visual-panel">
        <div class="visual-copy">
          <p class="eyebrow">安全访问</p>
          <h1>登录后查看你的房间与设备数据。</h1>
          <p>
            管理员可管理全部房间并分配访问权限，
            普通用户仅能查看自己负责的房间。
          </p>
        </div>
      </div>

      <div class="form-panel">
        <h2>用户登录</h2>
        <p class="form-desc">当前登录状态将保存在本地浏览器中。</p>

        <form @submit.prevent="handleLogin">
          <div v-if="errorMessage" class="error-message">
            <span>{{ errorMessage }}</span>
            <button type="button" class="close-btn" @click="errorMessage = ''">x</button>
          </div>

          <label class="field">
            <span>用户名</span>
            <input v-model.trim="username" type="text" autocomplete="username" />
          </label>

          <label class="field">
            <span>密码</span>
            <input v-model="password" type="password" autocomplete="current-password" />
          </label>

          <button class="submit-btn" type="submit" :disabled="isLoading">
            {{ isLoading ? '登录中...' : '登录' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import userService from '../utils/userService'

const router = useRouter()
const route = useRoute()

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

onMounted(async () => {
  if (!userService.isLoggedIn()) {
    return
  }

  try {
    await userService.fetchCurrentUser()
    router.replace('/dashboard')
  } catch (error) {
    userService.clearAuth()
  }
})

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = '请输入用户名和密码。'
    return
  }

  try {
    isLoading.value = true
    errorMessage.value = ''

    const response = await userService.login(username.value, password.value)
    if (response.code !== 200) {
      errorMessage.value = response.msg || '用户名或密码错误。'
      return
    }

    ElMessage.success('登录成功')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    router.replace(redirect)
  } catch (error) {
    errorMessage.value = error?.response?.data?.msg || error?.message || '登录失败，请稍后重试。'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at top left, rgba(30, 136, 229, 0.18), transparent 34%),
    radial-gradient(circle at bottom right, rgba(10, 32, 56, 0.22), transparent 38%),
    linear-gradient(135deg, #eef4f7 0%, #dce8ee 100%);
  padding: clamp(16px, 3vw, 32px);
}

.login-card {
  width: min(980px, 100%);
  min-height: min(560px, calc(100vh - clamp(32px, 6vw, 64px)));
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  background: #fff;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 28px 70px rgba(18, 38, 56, 0.16);
}

.visual-panel {
  background:
    linear-gradient(rgba(8, 26, 39, 0.58), rgba(8, 26, 39, 0.72)),
    url('../assets/login.png') center/cover no-repeat;
  color: #fff;
  padding: clamp(28px, 4vw, 48px);
  display: flex;
  align-items: flex-end;
}

.eyebrow {
  margin: 0 0 12px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.16em;
  color: rgba(255, 255, 255, 0.76);
}

.visual-copy h1 {
  margin: 0 0 16px;
  font-size: 42px;
  line-height: 1.08;
}

.visual-copy p {
  margin: 0;
  max-width: 420px;
  font-size: 16px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.86);
}

.form-panel {
  padding: clamp(28px, 4vw, 56px) clamp(24px, 4vw, 48px);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-panel h2 {
  margin: 0;
  font-size: 34px;
  color: #0f2435;
}

.form-desc {
  margin: 10px 0 24px;
  color: #6b7d8b;
}

.field {
  display: block;
  margin-bottom: 18px;
}

.field span {
  display: block;
  margin-bottom: 8px;
  color: #284257;
  font-size: 14px;
}

.field input {
  width: 100%;
  height: 48px;
  border-radius: 14px;
  border: 1px solid #c9d6df;
  padding: 0 14px;
  font-size: 15px;
}

.field input:focus {
  outline: none;
  border-color: #2f77b7;
  box-shadow: 0 0 0 4px rgba(47, 119, 183, 0.14);
}

.submit-btn {
  width: 100%;
  height: 50px;
  border: 0;
  border-radius: 14px;
  background: linear-gradient(135deg, #18456b 0%, #2f77b7 100%);
  color: #fff;
  font-size: 15px;
  cursor: pointer;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: wait;
}

.error-message {
  min-height: 44px;
  margin-bottom: 18px;
  padding: 0 14px;
  border-radius: 12px;
  background: #fff0f0;
  color: #c93b3b;
  border: 1px solid #efc7c7;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.close-btn {
  border: 0;
  background: transparent;
  color: inherit;
  cursor: pointer;
}

@media (max-width: 860px) {
  .login-card {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .visual-panel {
    min-height: 200px;
    align-items: center;
  }

  .visual-copy h1 {
    font-size: 30px;
  }

  .form-panel {
    padding: 36px 24px;
  }
}
</style>
