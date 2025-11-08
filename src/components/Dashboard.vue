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
            <span>
              {{ alarm.time }} {{ alarm.content }}
            </span>
          </div>
        </div>
      </div>
  </div>
</template>



<script setup>
import { ref, onMounted, h} from 'vue'
import dataService from '../utils/dataService'
import alertService from '../utils/alertService'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'


const envChart = ref(null)    // 环境趋势图表容器引用
const currentTime = ref('')   // 当前时间字符串
const onlineDevices = ref(0)  // 在线设备数量
const todayAlarms = ref(0)    // 今日告警数量
const deviceRate = ref(0)     // 设备运行率
const alarmList = ref([ ])    // 最新告警列表
const chartDataTemp = ref([]) // 温度数据
const chartDataHum = ref([])  // 湿度数据

let myChart = null;           // ECharts 实例



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
const selectedAvatar = ref(getAvatar()) // 头像路径


// 刷新函数逻辑
const Refresh = async () => {
  try {
    const data = await dataService.getAllSensorData();
    const temperatureData = await dataService.getSensorDataByType('温度');
    const onlineCount = await dataService.getOnlineDeviceCount(); // 避免变量名重复
    const totalDevices = await dataService.getDeviceCount();
    onlineDevices.value = onlineCount 
    deviceRate.value = ((onlineCount / totalDevices) * 100).toFixed(2);
    todayAlarms.value = await alertService.getTodayAlertCount();
  } catch (error) {
    console.error('刷新告警数据失败:', error);
    ElMessage.error('获取数据失败失败，请稍后重试');
  }
}


// 刷新按钮点击处理函数
const handleRefresh = async () => {
  try {
    showLoadingMessage();
    const alerts = await alertService.getLatest5Alerts();
    alarmList.value = alerts.map(alert => ({
      time: alert.timestamp,
      content: alert.roomNumber +' '+ alert.alertType
    }));

    // 获取最新温度数据并更新图表
    chartDataTemp.value = await dataService.get10SensorDataByType('温度');
    chartDataHum.value = await dataService.get10SensorDataByType('湿度');
    updateChart();
    //window.location.reload();
    ElMessage.success('刷新成功');
  } catch (error) {
    console.error('刷新页面失败:', error);
    ElMessage.error('刷新页面失败，请稍后重试');
  }
}

// 更新图表数据的函数
const updateChart = () => {
  if(!myChart) return;

  // 统一时间戳（取温度和湿度的并集，确保x轴时间一致）
  const allTimeStamps = [...new Set(
    chartDataTemp.value.map(item => item.timeStamp).concat(
      chartDataHum.value.map(item => item.timeStamp)
    )
  )].sort((a, b) => new Date(a) - new Date(b));

  // 格式化时间为 MM-DD HH:mm
  const xAxisData = allTimeStamps.map(ts => {
    const date = new Date(ts);
    return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  });

  // 提取温度数据（按时间戳匹配）
  const temperatureData = allTimeStamps.map(ts => {
    const item = chartDataTemp.value.find(i => i.timeStamp === ts);
    return item?.value ?? null; // 无数据时显示null
  });

  // 提取湿度数据（按时间戳匹配）
  const humidityData = allTimeStamps.map(ts => {
    const item = chartDataHum.value.find(i => i.timeStamp === ts);
    return item?.value ?? null; // 无数据时显示null
  });

  // 更新图表配置
  myChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a0}: {c0}<br/>{a1}: {c1}'
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: {
      type: 'value',
      name: '数值'
    },
    series: [
      {
        name: '温度',
        type: 'line',
        data: temperatureData,
        smooth: true,
        lineStyle: { color: '#67C23A' },
        markPoint: {
          data: [{ type: 'max', name: '最高' }, { type: 'min', name: '最低' }]
        }
      },
      {
        name: '湿度',
        type: 'line',
        data: humidityData,
        smooth: true,
        lineStyle: { color: '#409EFF' },
        markPoint: {
          data: [{ type: 'max', name: '最高' }, { type: 'min', name: '最低' }]
        }
      }
    ]
  });
}

// 显示加载中消息的函数
const showLoadingMessage = () => {
  // 显示加载中消息（带旋转图标）
  const message = ElMessage({
    message: '正在加载数据...',
    icon: h(Loading), // 加载图标
    duration: 2000,   // 2秒后自动消失（可按需调整）
    type: 'info',     // 消息类型（不影响加载效果，仅控制边框颜色）
  })

  // 若需手动关闭（如接口请求完成后），可调用：
  // message.close()
}

onMounted(() => {
  // 初始化时间
  updateTime()
  // 每秒更新时间
  setInterval(updateTime, 1000)
  // 刷新数据
  Refresh()
  // 初始化图表
  myChart = echarts.init(envChart.value)
  myChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a0}: {c0}<br/>{a1}: {c1}'
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value',
      name: '数值'
    },
    series: [
      {
        name: '温度',
        type: 'line',
        data: [],
        smooth: true,
        lineStyle: { color: '#67C23A' }
      },
      {
        name: '湿度',
        type: 'line',
        data: [],
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
  color: #e42020;
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