<template>
  <router-view v-if="isLoginPage" />

  <div v-else class="dashboard-container">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>物联网检测系统</h2>
        <p>{{ ['admin', 'super_admin'].includes(currentUser?.role) ? '管理员' : '用户工作台' }}</p>
      </div>

      <nav class="sidebar-nav">
        <ul>
          <li :class="{ active: $route.path === '/dashboard' }">
            <router-link to="/dashboard" class="nav-link">首页仪表盘</router-link>
          </li>
          <li :class="{ active: $route.path === '/device-manager' }">
            <router-link to="/device-manager" class="nav-link">设备管理</router-link>
          </li>
          <li :class="{ active: $route.path === '/data-analysis' }">
            <router-link to="/data-analysis" class="nav-link">数据分析</router-link>
          </li>
          <li :class="{ active: $route.path === '/alarmcentre' }">
            <router-link to="/alarmcentre" class="nav-link">告警中心</router-link>
          </li>
        </ul>
      </nav>

      <nav class="sidebar-footer">
        <ul>
          <li :class="{ active: $route.path === '/admin' }">
            <router-link to="/admin" class="nav-link">
              {{ ['admin', 'super_admin'].includes(currentUser?.role) ? '个人信息' : '个人信息' }}
            </router-link>
          </li>
        </ul>
      </nav>
    </aside>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import userService from './utils/userService'

const route = useRoute()

const isLoginPage = computed(() => route.path === '/login')
const currentUser = computed(() => userService.getStoredUser())
</script>

<style scoped>
.dashboard-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  background: #f5f6fa;
}

.main-content {
  flex: 1;
  padding: 32px;
  background: #f5f6fa;
  color: #222;
  overflow-y: auto;
  min-width: 0;
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1f3447 0%, #15222f 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 28px 22px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 22px;
}

.sidebar-header p {
  margin: 8px 0 0;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.7);
}

.sidebar-nav ul,
.sidebar-footer ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li,
.sidebar-footer li {
  margin: 6px 12px;
  border-radius: 12px;
}

.sidebar-nav li.active,
.sidebar-nav li:hover,
.sidebar-footer li.active,
.sidebar-footer li:hover {
  background: rgba(255, 255, 255, 0.12);
}

.nav-link {
  display: block;
  padding: 14px 18px;
  color: inherit;
  text-decoration: none;
}

.sidebar-footer {
  margin-top: auto;
  padding-bottom: 16px;
}
</style>
