<template>
  <div class="device-manage">
    <div class="top-info">
      <h2>物联网检测系统 / 设备管理</h2>
      <div v-if="isAdmin" class="header-buttons">
        <template v-if="!isDeleteMode">
          <el-button type="primary" plain @click="handleAdd">新增</el-button>
          <el-button type="danger" plain @click="enterDeleteMode">删除</el-button>
        </template>
        <template v-else>
          <el-button type="danger" @click="confirmDelete">确认删除</el-button>
          <el-button @click="cancelDelete">取消</el-button>
        </template>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="新增设备" width="700px" @close="resetForm">
      <div v-if="loadingAvailable" class="loading-box">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>正在加载可添加设备...</p>
      </div>

      <div v-else-if="availableDevices.length === 0" class="loading-box empty-text">
        <p>当前没有可添加的设备。</p>
      </div>

      <div v-else>
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
          <el-form-item label="设备" prop="selectedDevice">
            <el-select
              v-model="formData.selectedDevice"
              placeholder="请选择设备"
              style="width: 100%"
            >
              <el-option
                v-for="device in availableDevices"
                :key="device.entityId"
                :label="`${device.attributes?.friendly_name || device.entityId} (${device.entityId})`"
                :value="device.entityId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="房间" prop="locationId">
            <el-select v-model="formData.locationId" placeholder="请选择房间" style="width: 100%">
              <el-option
                v-for="location in locations"
                :key="location.id"
                :label="location.displayName"
                :value="location.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="上报间隔" prop="reportInterval">
            <el-select v-model="formData.reportInterval" placeholder="请选择上报间隔" style="width: 100%">
              <el-option label="30 秒" :value="30" />
              <el-option label="1 分钟" :value="60" />
              <el-option label="5 分钟" :value="300" />
              <el-option label="10 分钟" :value="600" />
              <el-option label="30 分钟" :value="1800" />
              <el-option label="1 小时" :value="3600" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">新增设备</el-button>
      </template>
    </el-dialog>

    <div class="device-table">
      <div class="filter-row">
        <el-select v-model="selectedLocationId" clearable placeholder="按房间筛选" class="room-filter">
          <el-option
            v-for="location in locations"
            :key="location.id"
            :label="location.displayName"
            :value="location.id"
          />
        </el-select>
      </div>

      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="温湿度" name="humiture" />
        <el-tab-pane label="红外" name="infrared" />
        <el-tab-pane label="门磁" name="doorMagnet" />
        <el-tab-pane label="电脑" name="computer" />
      </el-tabs>

      <el-table :data="tableData" @selection-change="handleSelectionChange" :row-key="getRowKey">
        <el-table-column v-if="isDeleteMode" type="selection" width="55" align="center" />
        <el-table-column prop="deviceCode" label="设备编号" align="center" width="180" />
        <el-table-column prop="name" label="设备名称" align="center" />
        <el-table-column prop="type" label="设备类型" align="center" />
        <el-table-column prop="room" label="所属房间" align="center" />
        <el-table-column prop="onlineStatus" label="设备状态" align="center" />
        <el-table-column prop="lastUploadTime" label="最后上报时间" align="center" />
        <el-table-column prop="collectFreq" label="上报间隔" align="center" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import dataService from '../utils/dataService'
import deviceService from '../utils/deviceService'
import userService from '../utils/userService'

const activeTab = ref('all')
const selectedLocationId = ref(null)
const allData = ref([])
const locations = ref([])
const availableDevices = ref([])
const loadingAvailable = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isDeleteMode = ref(false)
const selectedDevices = ref([])
const formRef = ref(null)

const currentUser = computed(() => userService.getStoredUser())
const isAdmin = computed(() => ['admin', 'super_admin'].includes(currentUser.value?.role))

const formData = ref({
  selectedDevice: '',
  locationId: null,
  reportInterval: 300
})

const formRules = {
  selectedDevice: [{ required: true, message: '请选择设备', trigger: 'change' }],
  locationId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  reportInterval: [{ required: true, message: '请选择上报间隔', trigger: 'change' }]
}

const typeMap = {
  humiture: ['温度', '湿度', '温湿度'],
  infrared: ['红外'],
  doorMagnet: ['门磁'],
  computer: ['计算机']
}

const tableData = computed(() => {
  let data = allData.value

  if (selectedLocationId.value != null) {
    data = data.filter((item) => Number(item.locationId) === Number(selectedLocationId.value))
  }

  if (activeTab.value === 'all') {
    return data
  }

  const targetTypes = typeMap[activeTab.value] || []
  return data.filter((item) => targetTypes.includes(item.type))
})

const loadLocations = async () => {
  const locationList = await dataService.getAllLocations()
  locations.value = (locationList || []).map((location) => ({
    ...location,
    displayName: `${location.buildingname}-${location.floornumber}F-${location.roomnumber}`
  }))

  if (!formData.value.locationId && locations.value.length > 0) {
    formData.value.locationId = locations.value[0].id
  }
}

const locationNameMap = computed(() => {
  const map = {}
  locations.value.forEach((location) => {
    map[location.id] = location.displayName
  })
  return map
})

const loadDeviceList = async () => {
  try {
    const devices = await deviceService.getAllDevices()
    allData.value = devices.map((device) => ({
      id: device.id,
      locationId: device.locationid,
      deviceCode: device.deviceid,
      name: device.devicename,
      type: device.sensortype,
      room: locationNameMap.value[device.locationid] || `房间 ${device.locationid}`,
      onlineStatus: device.status === 'enabled' || device.status === 'online' ? '在线' : '离线',
      lastUploadTime: formatDateTime(device.timestamp),
      collectFreq: formatInterval(device.datareportinterval)
    }))
  } catch (error) {
    ElMessage.error('加载设备列表失败')
  }
}

const loadAvailableDevices = async () => {
  loadingAvailable.value = true
  try {
    availableDevices.value = await deviceService.getAvailableDevices()
  } catch (error) {
    ElMessage.error('加载可添加设备失败')
  } finally {
    loadingAvailable.value = false
  }
}

const handleAdd = async () => {
  dialogVisible.value = true
  await loadAvailableDevices()
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  formData.value = {
    selectedDevice: '',
    locationId: locations.value[0]?.id ?? null,
    reportInterval: 300
  }
}

const submitForm = async () => {
  if (!formRef.value) {
    return
  }

  try {
    await formRef.value.validate()
    submitLoading.value = true
    const response = await deviceService.enableDevice({ ...formData.value, entityId: formData.value.selectedDevice })
    if (response.code !== 200) {
      ElMessage.error(response.msg || '新增设备失败')
      return
    }

    ElMessage.success('设备新增成功')
    dialogVisible.value = false
    await loadDeviceList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.msg || '新增设备失败')
    }
  } finally {
    submitLoading.value = false
  }
}

const enterDeleteMode = () => {
  isDeleteMode.value = true
  selectedDevices.value = []
}

const cancelDelete = () => {
  isDeleteMode.value = false
  selectedDevices.value = []
}

const confirmDelete = async () => {
  if (selectedDevices.value.length === 0) {
    ElMessage.warning('请至少选择一个设备')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认删除 ${selectedDevices.value.length} 个设备吗？`,
      '删除确认',
      { type: 'warning' }
    )

    for (const device of selectedDevices.value) {
      await deviceService.disableDevice(device.id)
    }

    ElMessage.success('设备删除成功')
    cancelDelete()
    await loadDeviceList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除设备失败')
    }
  }
}

const getRowKey = (row) => row.id

const handleSelectionChange = (selection) => {
  selectedDevices.value = selection
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  const date = new Date(datetime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatInterval = (seconds) => {
  if (!seconds) return '-'
  if (seconds < 60) return `${seconds}秒`
  if (seconds < 3600) return `${seconds / 60}分钟`
  return `${seconds / 3600}小时`
}

onMounted(async () => {
  await loadLocations()
  await loadDeviceList()
})
</script>

<style scoped>
.device-manage {
  width: 100%;
}

.top-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.device-table {
  background: #fff;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 24px;
}

.filter-row {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.room-filter {
  width: min(320px, 100%);
}

.loading-box {
  text-align: center;
  padding: 24px;
}

.empty-text {
  color: #8c9aa5;
}
</style>
