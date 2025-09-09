<template>
  <div class="content-wrapper">
      <!-- 顶部信息栏 -->
      <div class="top-bar">
        <h2>物联网监测系统 / 首页仪表盘</h2>
        <div class="top-bar-info">
          <span>{{ currentTime }}</span>
          <img src="/src/assets/fall.bmp" alt="头像"
               style="width:60px;height:60px;border-radius:50%;cursor:pointer;margin-left:16px;">
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

<script>
export default {
  data() {
    return {
      currentTime: '',
      onlineDevices: 0,
      todayAlarms: 0,
      deviceRate: 0,
      alarmList: []
    };
  },
  methods: {
    handleRefresh() {
      // 刷新数据的逻辑
    }
  }
  // 其他生命周期钩子和方法...
};
</script>
<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'

const currentMenu = ref('dashboard')
const onlineDevices = ref(28)
const todayAlarms = ref(5)
const deviceRate = ref(92)
const alarmList = ref([
  { time: '14:25', content: '101室 高温' },
  { time: '13:10', content: '203室 设备离线' },
  { time: '10:05', content: '302室 CO₂超标' }
])

const currentTime = computed(() => {
  const date = new Date()
  return (
    date.getFullYear() + '-' +
    (date.getMonth() + 1).toString().padStart(2, '0') + '-' +
    date.getDate().toString().padStart(2, '0') + ' ' +
    date.getHours().toString().padStart(2, '0') + ':' +
    date.getMinutes().toString().padStart(2, '0') + ':' +
    date.getSeconds().toString().padStart(2, '0')
  )
})

const envChart = ref(null)
onMounted(() => {
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
  window.addEventListener('resize', () => {
    myChart.resize()
  })
})

const handleRefresh = () => {
  // 刷新告警数据逻辑
  console.log('刷新数据')
}
</script>

<style scoped>
.content-wrapper {
  width: 100%;
}
.body {
  background: #f5f6fa;
  margin: 0;
  padding: 0;
}
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  padding: 16px;
  text-align: center;
}
.chart-container {
  height: 300px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 24px;
}
.alarm-item {
  margin-bottom: 12px;
  font-size: 15px;
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
</style>s