<template>
  <div class="dashboard-container">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>物联网监测系统</h2>
      </div>
      <nav class="sidebar-nav">
        <ul>
          <li :class="{ active: currentMenu === 'dashboard' }" @click="currentMenu = 'dashboard'">首页仪表盘</li>
          <li :class="{ active: currentMenu === 'device' }" @click="currentMenu = 'device'">设备管理</li>
          <li :class="{ active: currentMenu === 'analysis' }" @click="currentMenu = 'analysis'">数据分析</li>
          <li :class="{ active: currentMenu === 'alarm' }" @click="currentMenu = 'alarm'">告警中心</li>
        </ul>
      </nav>
      <div class="sidebar-footer">
        <p>管理员</p>
      </div>
    </aside>

    <!-- 主内容区：根据 currentMenu 显示不同组件 -->
    <main class="main-content">
      <Dashboard v-if="currentMenu === 'dashboard'" />
      <DeviceManager v-if="currentMenu === 'device'" />
      <DataAnalysis v-if="currentMenu === 'analysis'" />
      <Alarmcentre v-if="currentMenu === 'alarm'" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Dashboard from './components/Dashboard.vue'
import Alarmcentre from './components/Alarmcentre.vue'
// 下面两个组件尚未实现
import DeviceManager from './components/DeviceManager.vue'
import DataAnalysis from './components/DataAnalysis.vue'

const currentMenu = ref('dashboard') // 默认显示首页仪表盘
</script>

<style scoped>
.dashboard-container {
  display: flex;
  align-items: stretch;
  justify-content: flex-start; /* 让侧边栏和内容区紧贴左侧 */
  width: 100vw;
  height: 100vh;
  background: #f5f6fa;
  box-sizing: border-box;
}
.main-content {
  flex: 1; /* 让主内容区占满剩余宽度 */
  padding: 32px; 
  background: #f5f6fa;
  color: #222;
  overflow-y: auto; /* 内容超出时滚动 */
  min-width: 0;     /* 解决 Flex 子元素溢出问题 */
}
.sidebar {
  width: 220px;
  background: #2c3e50;
  color: #fff;
  display: flex;
  flex-direction: column;
}
.sidebar-header {
  height: 60px;
  background: #22313f;
  color: #fff;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  padding: 24px 0;
}
.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.sidebar-nav li {
  padding: 16px 0;
  text-align: center;
  color: #cfd8dc;
  cursor: pointer;
}
.sidebar-nav li.active,
.sidebar-nav li:hover {
  background: #34495e;
  color: #fff;
  font-weight: bold;
}
.sidebar-footer {
  text-align: center;
  height: 40px;
  padding: 16px 0;
  background: #22313f;
  color: #fff;
  font-size: 14px;
  margin-top: auto;
}
</style>
