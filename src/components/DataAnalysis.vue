<template>
  <div class="content-wrapper">
    <!-- 主内容区 -->
    <div class="analysis-header">
      <h2>物联网监测系统 / 数据分析</h2>
      <div class="analysis-filters">
        <!-- 周期下拉选择 -->
        <select v-model="selectedPeriod">
          <option value="">全部周期</option>
          <option value="today">今日</option>
          <option value="yesterday">昨日</option>
          <option value="7days">近7天</option>
          <option value="30days">近30天</option>
        </select>
        
        <!-- 时间段下拉选择 -->
        <select v-model="selectedTime">
          <option value="">全部时间段</option>
          <option value="teaching">教学期间（8:00-20:30）</option>
          <option value="morning">早上 (06:00-12:00)</option>
          <option value="afternoon">下午 (12:00-18:00)</option>
          <option value="evening">晚上 (18:00-24:00)</option>
        </select>

        <!-- 房间下拉选择 -->
        <select v-model="selectedRoom" @change="handleRoomChange">
          <option value="">全部房间</option>
          <option value="120">B120教室</option>
          <option value="116">B116教室</option>
          <option value="115">B115教室</option>
          <option value="104">B104教室</option>
        </select>
      </div>
    </div>

    <div class="analysis-table card">
      <!-- 分类Tab -->
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
        <el-tab-pane label="温湿度" name="humiture">
          <div class="tab-content">
            <h3>温湿度数据分析</h3>
            <p class="filter-desc">当前筛选条件: {{ getFilterDescription }}</p>
            
            <!-- 动态图表容器：单个房间显示一个图表，全部房间显示多个图表 -->
            <div 
              v-for="room in displayRooms" 
              :key="room.value" 
              class="chart-container"
              :ref="(el) => chartRefs[room.value] = el"
            >
              <h4>{{ room.label }}</h4>
              <div class="chart-inner"></div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="门磁-设备相关性" name="door-securiy">
          门磁-设备相关性
        </el-tab-pane>
        
        <el-tab-pane label="告警类型占比" name="alarm-property">
          告警类型占比
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>

  <button class="export-btn" @click="exportData">导出数据</button>

</template>

<script setup>
// 1. 导入依赖（统一放在顶部）
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'


// 2. 响应式变量声明
const selectedPeriod = ref('')    // 周期筛选
const selectedTime = ref('')      // 时间段筛选
const activeTab = ref('humiture') // 当前激活的Tab
const selectedRoom = ref('')      // 房间筛选
const chartRefs = ref({})         // 图表容器引用集合
const chartInstances = ref({})    // 图表实例集合
const roomDataCache = ref({})     // 房间数据缓存

// 房间数据配置
const rooms = [
  { value: '120', label: 'B120教室' },
  { value: '116', label: 'B116教室' },
  { value: '115', label: 'B115教室' },
  { value: '104', label: 'B104教室' }
]

// 根据选择的房间决定显示哪些图表
const displayRooms = computed(() => {
  if (selectedRoom.value === '') {
    // 选择全部房间，显示所有房间图表
    return rooms
  } else {
    // 选择单个房间，只显示该房间图表
    return rooms.filter(room => room.value === selectedRoom.value)
  }
})

// 3. 筛选条件描述
const getFilterDescription = computed(() => {
  const periodMap = {
    '': '全部周期',
    'today': '今日',
    'yesterday': '昨日',
    '7days': '近7天',
    '30days': '近30天'
  }
  const timeMap = {
    '': '全部时间段',
    'teaching': '教学期间（8:00-20:30）',
    'morning': '早上 (06:00-12:00)',
    'afternoon': '下午 (12:00-18:00)',
    'evening': '晚上 (18:00-24:00)'
  }
  const roomMap = {
    '': '全部房间',
    '120': 'B120教室',
    '116': 'B116教室',
    '115': 'B115教室',
    '104': 'B104教室'
  }
  return `${periodMap[selectedPeriod.value]} | ${timeMap[selectedTime.value]} | ${roomMap[selectedRoom.value]}`
})

// 4. 动态加载房间数据的函数
const loadRoomData = async (roomId) => {
  // 模拟异步数据加载
  if (roomDataCache.value[roomId]) {
    return roomDataCache.value[roomId]
  }
  try {
    const response = await fetch(`/test_data_${roomId}.json`)
    if (!response.ok) throw new Error('网络响应失败')
    const data = await response.json()
    roomDataCache.value[roomId] = data
    return data
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 5. 图表初始化/更新（修改数据部分）
const initOrUpdateCharts = async (forceResize = false) => {
  // 先销毁所有现有图表
  Object.values(chartInstances.value).forEach(instance => {
    instance.dispose()
  })
  chartInstances.value = {}
  
  // 为每个需要显示的房间创建图表
  for (const room of displayRooms.value) {
    const container = chartRefs.value[room.value]?.querySelector('.chart-inner')
    if (!container) continue

    const roomData = await loadRoomData(room.value)
    
    // 创建图表实例
    const chart = echarts.init(container)
    chartInstances.value[room.value] = chart
    
    const xAxisData = roomData.dailyRecords.map(record => record.date)
    const tempData = roomData.dailyRecords.map(record => record.temperature.avg)  
    const humidityData = roomData.dailyRecords.map(record => record.humidity.avg)

    // 设置图表配置
    chart.setOption({
      legend: {
        data: ['温度', '湿度'],
        bottom: 10,
        left: 'center'
      },
      xAxis: {
        type: 'category',
        data: xAxisData // 使用处理后的x轴数据
      },
      yAxis: [
        {
          type: 'value',
          name: `温度 (${roomData.unit.temperature})`, // 使用数据中的单位
          min: 18,
          max: 30
        },
        {
          type: 'value',
          name: `湿度 (${roomData.unit.humidity})`, // 使用数据中的单位
          min: 30,
          max: 70,
          position: 'right'
        }
      ],
      series: [
        {
          name: '温度',
          type: 'line',
          data: tempData.map(v => v.toFixed(1)),
          smooth: true,
          lineStyle: { color: '#67C23A' }
        },
        {
          name: '湿度',
          type: 'line',
          data: humidityData.map(v => v.toFixed(1)),
          smooth: true,
          lineStyle: { color: '#409EFF' },
          yAxisIndex: 1
        }
      ]
    })
    
    if (forceResize) {
      chart.resize()
    }
  }
}

// 5. 事件处理函数
const handleRoomChange = () => {
  // 房间改变后重新渲染图表
  nextTick(async () => {
    await initOrUpdateCharts(true)
  })
}

const handleTabClick = (tab) => {
  console.log('当前选中的Tab:', tab.name)
  if (tab.name === 'humiture') {
    // 等待Tab切换完成再渲染图表
    nextTick(async() => {
      setTimeout(async () => 
        await initOrUpdateCharts(true), 100)
    })
  }
}

const exportData = () => {
  console.log('导出数据功能待实现')
}

// 6. 生命周期钩子
onMounted(async () => {
  await nextTick()
  await initOrUpdateCharts()
  
  // 窗口大小改变时重绘图表
  const resizeHandler = () => {
    Object.values(chartInstances.value).forEach(instance => {
      instance.resize()
    })
  }
  
  window.addEventListener('resize', resizeHandler)
  
  onUnmounted(() => {
    // 清理图表实例
    Object.values(chartInstances.value).forEach(instance => {
      instance.dispose()
    })
    window.removeEventListener('resize', resizeHandler)
  })
})
</script>

<style scoped>
.content-wrapper {
  width: 100%;
}
.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.analysis-header h2 {
  font-size: 18px;
  font-weight: bold;
  color: #222;
}
.analysis-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.analysis-filters select {
  padding: 6px 12px;
  border: 1px solid #e0e6ed;
  border-radius: 4px;
  background: #fff;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%23333' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  padding-right: 30px;
  cursor: pointer;
  width:155px;
}
.tab-content {
  margin-top: 16px;
  padding: 10px;
}
.tab-content h3 {
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 16px;
}
.tab-content h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}
.filter-desc {
  margin-top: 0;
  margin-bottom: 20px;
  color: #666;
}
.card {
  background: #fff;
  color: #333;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 16px;
  min-height: 450px;
}
.chart-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  padding: 16px;
  margin-bottom: 20px;
}
.chart-inner {
  height: 250px; /* 图表实际高度 */
  width: 100%;
}
.export-btn {
  background: #000000;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .analysis-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .chart-inner {
    height: 200px;
  }
}
</style>
