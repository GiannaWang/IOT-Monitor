<template>
  <div class="content-wrapper">
    <div class="analysis-header">
      <h2>物联网监测系统 / 数据分析</h2>
      <div class="analysis-filters">
        <el-select v-model="selectedRoom" placeholder="选择房间" style="width: 180px" @change="handleRoomChange">
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
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
        <el-tab-pane label="温湿度分析" name="humiture">
          <div class="tab-content">
            <h3>温湿度趋势</h3>
            <p class="filter-desc">当前筛选：{{ currentRoomLabel }}</p>

            <div
              v-for="room in displayRooms"
              :key="room.id"
              class="chart-container"
              :ref="(el) => roomChartRefs[room.id] = el"
            >
              <h4>{{ room.displayName }}</h4>
              <div class="chart-inner"></div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="红外分析" name="infrared">
          <div class="tab-content">
            <h3>红外触发趋势</h3>
            <p class="filter-desc">展示各房间红外传感器触发次数变化</p>

            <div
              v-for="room in infraredRooms"
              :key="`infrared-${room.id}`"
              class="chart-container"
              :ref="(el) => infraredChartRefs[room.id] = el"
            >
              <h4>{{ room.displayName }}</h4>
              <div class="chart-inner"></div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="门磁分析" name="door">
          <div class="tab-content">
            <h3>门磁开关趋势</h3>
            <p class="filter-desc">展示各房间门磁事件次数变化</p>

            <div
              v-for="room in doorRooms"
              :key="`door-${room.id}`"
              class="chart-container"
              :ref="(el) => doorChartRefs[room.id] = el"
            >
              <h4>{{ room.displayName }}</h4>
              <div class="chart-inner"></div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="计算机状态" name="computer">
          <div class="tab-content">
            <h3>Windows 主机状态趋势</h3>
            <p class="filter-desc">展示已接入主机的 CPU、内存、磁盘使用率</p>

            <div v-if="computerDevices.length === 0" class="empty-state">
              暂无计算机状态数据
            </div>

            <div
              v-for="machine in computerDevices"
              :key="machine.deviceid"
              class="chart-container"
              :ref="(el) => computerChartRefs[machine.deviceid] = el"
            >
              <h4>{{ machine.devicename }}（{{ machine.source || '未知位置' }}）</h4>
              <div class="computer-summary">
                <span>设备编号：{{ machine.deviceid }}</span>
                <span>上报频率：{{ formatInterval(machine.datareportinterval) }}</span>
              </div>
              <div class="chart-inner"></div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import dataService from '../utils/dataService'
import deviceService from '../utils/deviceService'

const activeTab = ref('humiture')
const selectedRoom = ref('')
const locations = ref([])
const computerDevices = ref([])

const roomChartRefs = ref({})
const roomChartInstances = ref({})
const infraredChartRefs = ref({})
const infraredChartInstances = ref({})
const doorChartRefs = ref({})
const doorChartInstances = ref({})
const computerChartRefs = ref({})
const computerChartInstances = ref({})

const displayRooms = computed(() => {
  if (!selectedRoom.value) return locations.value
  return locations.value.filter((item) => item.id === selectedRoom.value)
})

const currentRoomLabel = computed(() => {
  if (!selectedRoom.value) return '全部房间'
  const room = locations.value.find((item) => item.id === selectedRoom.value)
  return room?.displayName || '全部房间'
})

const infraredRooms = computed(() => locations.value.filter((item) => item.id === 2))
const doorRooms = computed(() => locations.value.filter((item) => item.id === 3))

const destroyCharts = (chartMap) => {
  Object.values(chartMap.value).forEach((chart) => {
    chart.dispose()
  })
  chartMap.value = {}
}

const aggregateByDate = (records = []) => {
  const grouped = {}

  records.forEach((record) => {
    if (!record?.timeStamp) return
    const date = record.timeStamp.substring(0, 10)
    if (!grouped[date]) {
      grouped[date] = { sum: 0, count: 0 }
    }
    grouped[date].sum += Number(record.value || 0)
    grouped[date].count += 1
  })

  return Object.entries(grouped)
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([date, data]) => ({
      date,
      avg: data.count ? data.sum / data.count : 0
    }))
}

const loadLocations = async () => {
  try {
    const locationList = await dataService.getAllLocations()
    locations.value = (locationList || []).map((loc) => ({
      ...loc,
      displayName: `${loc.buildingname}${loc.floornumber}楼${loc.roomnumber}室`
    }))
  } catch (error) {
    console.error('加载位置失败:', error)
    ElMessage.error('加载位置失败')
  }
}

const renderHumitureCharts = async () => {
  destroyCharts(roomChartInstances)
  await nextTick()

  for (const room of displayRooms.value) {
    const wrapper = roomChartRefs.value[room.id]
    const container = wrapper?.querySelector('.chart-inner')
    if (!container) continue

    const [tempRecords, humidityRecords] = await Promise.all([
      dataService.getSensorDataWithFilters({ selectedDataType: 'temperature', locationId: room.id }),
      dataService.getSensorDataWithFilters({ selectedDataType: 'humidity', locationId: room.id })
    ])

    const tempDaily = aggregateByDate(tempRecords)
    const humidityDaily = aggregateByDate(humidityRecords)

    const dates = [...new Set([
      ...tempDaily.map((item) => item.date),
      ...humidityDaily.map((item) => item.date)
    ])].sort()

    const tempMap = Object.fromEntries(tempDaily.map((item) => [item.date, item.avg]))
    const humidityMap = Object.fromEntries(humidityDaily.map((item) => [item.date, item.avg]))

    const chart = echarts.init(container)
    roomChartInstances.value[room.id] = chart

    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['温度', '湿度'], bottom: 0 },
      xAxis: { type: 'category', data: dates },
      yAxis: [
        { type: 'value', name: '温度 (°C)' },
        { type: 'value', name: '湿度 (%)', position: 'right' }
      ],
      series: [
        {
          name: '温度',
          type: 'line',
          smooth: true,
          data: dates.map((date) => tempMap[date] != null ? tempMap[date].toFixed(1) : null),
          lineStyle: { color: '#67C23A' }
        },
        {
          name: '湿度',
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          data: dates.map((date) => humidityMap[date] != null ? humidityMap[date].toFixed(1) : null),
          lineStyle: { color: '#409EFF' }
        }
      ]
    })
  }
}

const renderSingleMetricCharts = async ({ rooms, refs, instances, metricKey, title, color }) => {
  destroyCharts(instances)
  await nextTick()

  for (const room of rooms.value) {
    const wrapper = refs.value[room.id]
    const container = wrapper?.querySelector('.chart-inner')
    if (!container) continue

    const records = await dataService.getSensorDataWithFilters({
      selectedDataType: metricKey,
      locationId: room.id
    })

    const times = (records || []).map((item) => item.timeStamp?.substring(5, 16))
    const values = (records || []).map((item) => item.value)

    const chart = echarts.init(container)
    instances.value[room.id] = chart

    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: times },
      yAxis: { type: 'value', name: '次数' },
      series: [
        {
          name: title,
          type: 'line',
          smooth: true,
          data: values,
          lineStyle: { color },
          areaStyle: { color }
        }
      ]
    })
  }
}

const loadComputerDevices = async () => {
  try {
    const devices = await deviceService.getAllDevices()
    computerDevices.value = (devices || []).filter((device) => {
      return device.sensortype === '计算机'
    })
  } catch (error) {
    console.error('加载计算机设备失败:', error)
    ElMessage.error('加载计算机设备失败')
  }
}

const renderComputerCharts = async () => {
  destroyCharts(computerChartInstances)
  await loadComputerDevices()
  await nextTick()

  const metricConfig = [
    { key: 'cpu', label: 'CPU', color: '#E6A23C' },
    { key: 'memory', label: '内存', color: '#409EFF' },
    { key: 'disk', label: '磁盘', color: '#67C23A' }
  ]

  for (const machine of computerDevices.value) {
    const wrapper = computerChartRefs.value[machine.deviceid]
    const container = wrapper?.querySelector('.chart-inner')
    if (!container) continue

    const recordsByMetric = await Promise.all(
      metricConfig.map((metric) =>
        dataService.getSensorDataWithFilters({
          selectedDataType: metric.key,
          locationId: machine.locationid
        }).then((records) => (records || []).filter((item) => item.deviceId === machine.id))
      )
    )

    const times = [...new Set(
      recordsByMetric.flatMap((records) => records.map((item) => item.timeStamp?.substring(5, 16) || ''))
    )].sort()

    const chart = echarts.init(container)
    computerChartInstances.value[machine.deviceid] = chart

    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: metricConfig.map((item) => item.label), bottom: 0 },
      xAxis: { type: 'category', data: times },
      yAxis: { type: 'value', name: '使用率 (%)', min: 0, max: 100 },
      series: metricConfig.map((metric, index) => {
        const map = Object.fromEntries(
          recordsByMetric[index].map((record) => [record.timeStamp?.substring(5, 16), record.value])
        )

        return {
          name: metric.label,
          type: 'line',
          smooth: true,
          data: times.map((time) => map[time] != null ? Number(map[time]).toFixed(1) : null),
          lineStyle: { color: metric.color }
        }
      })
    })
  }
}

const handleRoomChange = async () => {
  if (activeTab.value === 'humiture') {
    await renderHumitureCharts()
  }
}

const handleTabClick = async (tab) => {
  if (tab.props.name === 'humiture') {
    await renderHumitureCharts()
    return
  }

  if (tab.props.name === 'infrared') {
    await renderSingleMetricCharts({
      rooms: infraredRooms,
      refs: infraredChartRefs,
      instances: infraredChartInstances,
      metricKey: 'infrared',
      title: '红外触发次数',
      color: '#F56C6C'
    })
    return
  }

  if (tab.props.name === 'door') {
    await renderSingleMetricCharts({
      rooms: doorRooms,
      refs: doorChartRefs,
      instances: doorChartInstances,
      metricKey: 'door',
      title: '门磁事件次数',
      color: '#8E44AD'
    })
    return
  }

  if (tab.props.name === 'computer') {
    await renderComputerCharts()
  }
}

const formatInterval = (seconds) => {
  if (!seconds) return '未知'
  if (seconds < 60) return `${seconds} 秒`
  if (seconds < 3600) return `${seconds / 60} 分钟`
  return `${seconds / 3600} 小时`
}

const resizeAllCharts = () => {
  Object.values(roomChartInstances.value).forEach((chart) => chart.resize())
  Object.values(infraredChartInstances.value).forEach((chart) => chart.resize())
  Object.values(doorChartInstances.value).forEach((chart) => chart.resize())
  Object.values(computerChartInstances.value).forEach((chart) => chart.resize())
}

onMounted(async () => {
  await loadLocations()
  await renderHumitureCharts()
  window.addEventListener('resize', resizeAllCharts)
})

onBeforeUnmount(() => {
  destroyCharts(roomChartInstances)
  destroyCharts(infraredChartInstances)
  destroyCharts(doorChartInstances)
  destroyCharts(computerChartInstances)
  window.removeEventListener('resize', resizeAllCharts)
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

.card {
  background: #fff;
  color: #333;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 24px;
  min-height: 450px;
}

.tab-content {
  margin-top: 16px;
  padding: 10px;
}

.tab-content h3 {
  margin: 0 0 16px;
  font-size: 16px;
}

.tab-content h4 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #666;
}

.filter-desc {
  margin: 0 0 20px;
  color: #666;
}

.empty-state {
  color: #999;
  padding: 20px 0;
}

.chart-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 16px;
  margin-bottom: 20px;
}

.chart-inner {
  height: 280px;
  width: 100%;
}

.computer-summary {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  color: #666;
  font-size: 13px;
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .analysis-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .chart-inner {
    height: 220px;
  }
}
</style>
