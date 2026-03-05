<template>
  <div class="content-wrapper">
    <!-- 主内容区 -->
    <div class="analysis-header">
      <h2>物联网监测系统 / 数据分析</h2>
      <div class="analysis-filters">
        <!-- 周期下拉选择 -->
        <el-select v-model="selectedPeriod" placeholder="选择周期" style="width: 155px">
          <el-option label="全部周期" value="" />
          <el-option label="今日" value="today" />
          <el-option label="昨日" value="yesterday" />
          <el-option label="近7天" value="7days" />
          <el-option label="近30天" value="30days" />
        </el-select>

        <!-- 时间段下拉选择 -->
        <el-select v-model="selectedTime" placeholder="选择时间段" style="width: 180px">
          <el-option label="全部时间段" value="" />
          <el-option label="教学期间（8:00-20:30）" value="teaching" />
          <el-option label="早上 (06:00-12:00)" value="morning" />
          <el-option label="下午 (12:00-18:00)" value="afternoon" />
          <el-option label="晚上 (18:00-24:00)" value="evening" />
        </el-select>

        <!-- 房间下拉选择 -->
        <el-select
          v-model="selectedRoom"
          placeholder="选择房间"
          style="width: 155px"
          @change="handleRoomChange"
        >
          <el-option label="全部房间" value="" />
          <el-option
            v-for="location in locations"
            :key="location.id"
            :label="location.displayName"
            :value="location.id"
          />
        </el-select>
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
              :key="room.id"
              class="chart-container"
              :ref="(el) => chartRefs[room.id] = el"
            >
              <h4>{{ room.label }}</h4>
              <div class="chart-inner"></div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="计算机状态" name="computer">
          <div class="tab-content">
            <h3>计算机资源监控</h3>
            <p class="filter-desc">展示各 Windows 主机最近上报的 CPU、内存、磁盘使用率趋势</p>
            <div v-if="computerDevices.length === 0" style="color:#999;padding:20px 0;">
              暂无计算机设备数据，请确认 Windows Agent 已部署并正常上报。
            </div>
            <div
              v-for="machine in computerDevices"
              :key="machine.deviceid"
              class="chart-container"
              :ref="(el) => computerChartRefs[machine.deviceid] = el"
            >
              <h4>{{ machine.devicename }}（{{ machine.source }}）</h4>
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
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import dataService from '../utils/dataService'
import deviceService from '../utils/deviceService'

// 2. 响应式变量声明
const selectedPeriod = ref('')    // 周期筛选
const selectedTime = ref('')      // 时间段筛选
const activeTab = ref('humiture') // 当前激活的Tab
const selectedRoom = ref('')      // 房间筛选
const chartRefs = ref({})         // 图表容器引用集合
const chartInstances = ref({})    // 图表实例集合
const locations = ref([])         // 位置列表（从后端获取）
const sensorDataCache = ref({})   // 传感器数据缓存
const loading = ref(false)        // 加载状态
const computerDevices = ref([])       // 计算机设备列表
const computerChartRefs = ref({})     // 计算机图表容器引用
const computerChartInstances = ref({}) // 计算机图表实例

// 根据选择的房间决定显示哪些图表
const displayRooms = computed(() => {
  if (selectedRoom.value === '') {
    return locations.value
  } else {
    return locations.value.filter(loc => loc.id === selectedRoom.value)
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

  let roomName = '全部房间'
  if (selectedRoom.value !== '') {
    const location = locations.value.find(loc => loc.id === selectedRoom.value)
    if (location) {
      roomName = location.displayName
    }
  }

  return `${periodMap[selectedPeriod.value]} | ${timeMap[selectedTime.value]} | ${roomName}`
})

// 4. 加载位置信息
const loadLocations = async () => {
  try {
    const locationList = await dataService.getAllLocations()
    // 为每个位置创建显示名称
    locations.value = locationList.map(loc => ({
      ...loc,
      displayName: `${loc.buildingname}${loc.floornumber}楼${loc.roomnumber}室`
    }))
    console.log('加载位置成功:', locations.value)
  } catch (error) {
    console.error('加载位置失败:', error)
    ElMessage.error('加载位置列表失败')
  }
}

// 5. 加载某个房间的温湿度数据并处理为图表所需格式
const loadRoomData = async (locationId) => {
  const cacheKey = `room_${locationId}`
  if (sensorDataCache.value[cacheKey]) {
    return sensorDataCache.value[cacheKey]
  }

  try {
    loading.value = true
    const [tempData, humidData] = await Promise.all([
      dataService.getSensorDataWithFilters({ selectedDataType: '温度', locationId }),
      dataService.getSensorDataWithFilters({ selectedDataType: '湿度', locationId })
    ])

    // 按日期聚合：计算每天平均值
    const aggregateByDate = (records) => {
      const map = {}
      ;(records || []).forEach(r => {
        const date = r.timeStamp ? r.timeStamp.substring(0, 10) : ''
        if (!date) return
        if (!map[date]) map[date] = { sum: 0, count: 0 }
        map[date].sum += r.value
        map[date].count += 1
      })
      return Object.entries(map)
        .sort(([a], [b]) => a.localeCompare(b))
        .map(([date, { sum, count }]) => ({ date, avg: sum / count }))
    }

    const tempAgg = aggregateByDate(tempData)
    const humidAgg = aggregateByDate(humidData)

    // 取两者日期的并集
    const allDates = [...new Set([...tempAgg.map(r => r.date), ...humidAgg.map(r => r.date)])].sort()
    const tempMap = Object.fromEntries(tempAgg.map(r => [r.date, r.avg]))
    const humidMap = Object.fromEntries(humidAgg.map(r => [r.date, r.avg]))

    const processedData = {
      dailyRecords: allDates.map(date => ({
        date,
        temperature: { avg: tempMap[date] ?? null },
        humidity: { avg: humidMap[date] ?? null }
      })),
      unit: { temperature: '°C', humidity: '%' }
    }

    sensorDataCache.value[cacheKey] = processedData
    return processedData
  } catch (error) {
    console.error('加载房间数据失败:', error)
    ElMessage.error('加载数据失败')
    return { dailyRecords: [], unit: { temperature: '°C', humidity: '%' } }
  } finally {
    loading.value = false
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
    const container = chartRefs.value[room.id]?.querySelector('.chart-inner')
    if (!container) continue

    const roomData = await loadRoomData(room.id)

    // 创建图表实例
    const chart = echarts.init(container)
    chartInstances.value[room.id] = chart

    const xAxisData = roomData.dailyRecords.map(record => record.date)
    const tempData = roomData.dailyRecords.map(record => record.temperature.avg)
    const humidityData = roomData.dailyRecords.map(record => record.humidity.avg)

    chart.setOption({
      legend: {
        data: ['温度', '湿度'],
        bottom: 10,
        left: 'center'
      },
      xAxis: {
        type: 'category',
        data: xAxisData
      },
      yAxis: [
        {
          type: 'value',
          name: `温度 (${roomData.unit.temperature})`,
          min: 18,
          max: 30
        },
        {
          type: 'value',
          name: `湿度 (${roomData.unit.humidity})`,
          min: 30,
          max: 70,
          position: 'right'
        }
      ],
      series: [
        {
          name: '温度',
          type: 'line',
          data: tempData.map(v => v != null ? v.toFixed(1) : null),
          smooth: true,
          connectNulls: false,
          lineStyle: { color: '#67C23A' }
        },
        {
          name: '湿度',
          type: 'line',
          data: humidityData.map(v => v != null ? v.toFixed(1) : null),
          smooth: true,
          connectNulls: false,
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

// 加载计算机设备列表并渲染趋势图
const loadComputerCharts = async () => {
  // 销毁旧图表
  Object.values(computerChartInstances.value).forEach(c => c.dispose())
  computerChartInstances.value = {}

  // 从 sensor_devices 获取计算机类型设备
  try {
    const allDevices = await deviceService.getAllDevices()
    computerDevices.value = (allDevices || []).filter(d => d.sensortype === '计算机')
  } catch (e) {
    console.error('加载计算机设备失败', e)
    return
  }

  await nextTick()

  const metricTypes = ['CPU使用率', '内存使用率', '磁盘使用率']
  const colors = ['#E6A23C', '#409EFF', '#67C23A']

  for (const machine of computerDevices.value) {
    const wrapper = computerChartRefs.value[machine.deviceid]
    const container = wrapper?.querySelector('.chart-inner')
    if (!container) continue

    // 查询该机器最近的三种指标
    const seriesData = await Promise.all(
      metricTypes.map(type =>
        dataService.getSensorDataWithFilters({ selectedDataType: type, locationId: machine.locationid })
          .then(records => (records || []).filter(r => r.deviceId === machine.id))
      )
    )

    const allTimes = [...new Set(
      seriesData.flatMap(records => records.map(r => r.timeStamp?.substring(0, 16) || ''))
    )].sort()

    const chart = echarts.init(container)
    computerChartInstances.value[machine.deviceid] = chart

    chart.setOption({
      legend: { data: metricTypes, bottom: 0 },
      xAxis: { type: 'category', data: allTimes },
      yAxis: { type: 'value', name: '%', min: 0, max: 100 },
      series: metricTypes.map((type, i) => {
        const map = Object.fromEntries(
          seriesData[i].map(r => [r.timeStamp?.substring(0, 16), r.value])
        )
        return {
          name: type,
          type: 'line',
          smooth: true,
          connectNulls: false,
          lineStyle: { color: colors[i] },
          data: allTimes.map(t => map[t] != null ? map[t].toFixed(1) : null)
        }
      })
    })
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
    nextTick(async() => {
      setTimeout(async () => await initOrUpdateCharts(true), 100)
    })
  } else if (tab.name === 'computer') {
    nextTick(async () => {
      setTimeout(async () => await loadComputerCharts(), 100)
    })
  }
}

const exportData = () => {
  console.log('导出数据功能待实现')
}

// 6. 生命周期钩子
onMounted(async () => {
  await loadLocations()
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
  padding: 24px;
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
