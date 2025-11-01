<template>
  <div class="content-wrapper">
      <!-- 顶部信息栏 -->
      <div class="top-bar">
        <h2>物联网监测系统 / 首页仪表盘</h2>
        <div class="top-bar-info">
          <span>{{ currentTime }}</span>
          <img 
            :src="selectedAvatar" 
            alt="头像"
            style="width:50px;height:50px;border-radius:50%;cursor:pointer;margin-left:16px;"
            @click="$router.push('/admin')">
        </div>
      </div>

      <!-- 统计卡片区域 -->
      <div class="card-group">
        <div class="card">
          <h3>当日环境概况</h3>
          <svg class="environment-chart" viewBox="0 0 100 30" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 20 C 20 10 30 10 40 20 C 50 30 60 30 70 20 C 80 10 90 10 100 20" stroke="#66B2FF" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="card">
          <h3>在线设备</h3>
          <p class="stat-value">{{ onlineDevices }}</p>
        </div>
        <div class="card">
          <h3>今日告警</h3>
          <p class="stat-value">{{ todayAlarms }}</p>
        </div>
        <div class="card">
          <h3>设备运行率</h3>
          <p class="stat-value">{{ deviceRate }}%</p>
        </div>
      </div>

      <!-- 图表及告警列表区域 -->
      <div class="bottom-section">
        <div class="trend-panel card">
          <h2>环境趋势（近7天）</h2>
          <div class="chart-container" ref="envChart"></div>
        </div>
        <div class="alarm-panel card">
          <h2>最新告警</h2>
          <button class="refresh-btn" @click="handleRefresh">刷新数据</button>
          <div class="alarm-item" v-for="(alarm, index) in alarmList" :key="index">
            <span :style="{ color: index === 0 ? '#333' : index === 1 ? '#e6a23c' : '#f56c6c' }">
              {{ alarm.time }} {{ alarm.content }}
            </span>
          </div>
        </div>
      </div>
  </div>
</template>



<script setup>
import { ref, onMounted } from 'vue'
import dataService from '../utils/dataService'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { ca } from 'element-plus/es/locales.mjs'

const envChart = ref(null)
const currentTime = ref('')
const onlineDevices = ref(0)
const todayAlarms = ref(0)
const deviceRate = ref(0)

const alarmList = ref([
  { time: '14:25', content: '101室 高温' },
  { time: '13:10', content: '203室 设备离线' },
  { time: '10:05', content: '302室 CO₂超标' }
])

// 定义更新时间的函数
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

// 获取头像的函数
const getAvatar = () => {
  const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
  if (!isLoggedIn) return "/src/assets/avatar/default.jpg";

  const storedUser = localStorage.getItem("user");
  const user = storedUser ? JSON.parse(storedUser) : null;

  return user?.avatar || "/src/assets/avatar/fall.bmp";
};

const selectedAvatar = ref(getAvatar())


// 刷新数据逻辑
const Refresh = async () => {
  try {
    const data = await dataService.getAllSensorData();
    const temperatureData = await dataService.getSensorDataByType('温度');
    onlineDevices.value = await dataService.getOnlineDeviceCount();
    const totalDevices = await dataService.getDeviceCount();
    deviceRate.value =  (onlineDevices.value / totalDevices * 100).toFixed(2);
  } catch (error) {
    console.error('刷新告警数据失败:', error);
    ElMessage.error('获取数据失败失败，请稍后重试');
  }
}

const handleRefresh = () => {
  try {
    //window.location.reload();
    ElMessage.success('刷新成功');
  } catch (error) {
    console.error('刷新页面失败:', error);
    ElMessage.error('刷新页面失败，请稍后重试');
  }
  
}

onMounted(() => {
  // 初始化时间
  updateTime()
  // 每秒更新时间
  setInterval(updateTime, 1000)
  // 刷新数据
  Refresh()
  // 初始化图表
  const myChart = echarts.init(envChart.value)
  myChart.setOption({
    xAxis: {
      type: 'category',
      data: ['8/25', '8/26', '8/27', '8/28', '8/29', '8/30', '8/31']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '温度',
        type: 'line',
        data: [22, 24, 23, 25, 24, 26, 23],
        smooth: true,
        lineStyle: { color: '#67C23A' }
      },
      {
        name: '湿度',
        type: 'line',
        data: [50, 52, 48, 55, 53, 51, 49],
        smooth: true,
        lineStyle: { color: '#409EFF' }
      }
    ]
  })
  // 窗口大小变化时，重置图表大小
  window.addEventListener('resize', () => {
    myChart.resize()
  })
})


</script>

<style scoped>
.content-wrapper {
  width: 100%;
}
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.top-bar h2 {
  font-size: 18px;
  font-weight: bold;
  color: #222;
}
.top-bar-info {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #333;
}
.top-bar-info img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-left: 16px;
}
.card-group {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}
.card-group .card {
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.card {
  background: #fff;
  color: #222;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 16px;
  text-align: center;
}
.chart-container {
  height: 300px;
  background: #fff;
  border-radius: 12px;
  /* box-shadow: 0 2px 8px rgba(0,0,0,0.05); */
  margin-bottom: 24px;
}
.stat-value {
  font-size: 16px;
}
.alarm-item {
  margin-top: 20px;
  margin-bottom: 16px;
  font-size: 16px;
}
.refresh-btn {
  background: #000000;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 16px;
}
.bottom-section {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}
.trend-panel {
  flex: 7;
}
.alarm-panel {
  flex: 3;
}
</style>