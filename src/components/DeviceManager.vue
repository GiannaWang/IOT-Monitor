<template>
  <div class="device-manage">
    <div class = "top-info" @click.stop>
      <h2>物联网监测系统 / 设备管理</h2>
      <div class="header-buttons">
        <!-- 正常模式下的按钮 -->
        <template v-if="!isDeleteMode">
          <el-button type="primary" plain @click="handleAdd">添加</el-button>
          <el-button type="primary" plain @click="enterDeleteMode">删除</el-button>
          <el-button type="primary" plain @click="handleSetting">设置</el-button>
        </template>
        <!-- 删除模式下的按钮 -->
        <template v-else>
          <el-button type="danger" @click="confirmDelete">确定删除</el-button>
          <el-button @click="cancelDelete">取消</el-button>
        </template>
      </div>
    </div>
    <!-- 添加设备弹窗 - 从 HA 可用设备中选择 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加设备 - 从 Home Assistant 选择"
      width="700px"
      @close="resetForm"
    >
      <div v-if="loadingAvailable" style="text-align: center; padding: 20px;">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>正在加载可用设备...</p>
      </div>
      <div v-else-if="availableDevices.length === 0" style="text-align: center; padding: 20px; color: #999;">
        <p>暂无可添加的设备</p>
        <p style="font-size: 12px;">（所有 Home Assistant 中的设备已全部启用）</p>
      </div>
      <div v-else>
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
        >
          <el-form-item label="选择设备" prop="selectedDevice">
            <el-select
              v-model="formData.selectedDevice"
              placeholder="请选择要添加的 HA 设备"
              style="width: 100%"
              @change="handleDeviceSelect"
            >
              <el-option
                v-for="device in availableDevices"
                :key="device.entityId"
                :label="`${device.attributes?.friendly_name || device.entityId} (${device.entityId})`"
                :value="device.entityId"
              >
                <span>{{ device.attributes?.friendly_name || device.entityId }}</span>
                <span style="float: right; color: #8492a6; font-size: 12px;">{{ device.entityId }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="当前状态">
            <el-tag v-if="selectedHADevice">{{ selectedHADevice.state || '未知' }}</el-tag>
            <span v-else style="color: #999;">-</span>
          </el-form-item>
          <el-form-item label="所在位置" prop="locationId">
            <el-select v-model="formData.locationId" placeholder="请选择设备位置" style="width: 100%">
              <el-option label="默认位置" :value="1" />
              <el-option label="A栋101室" :value="2" />
              <el-option label="A栋102室" :value="3" />
              <el-option label="A栋201室" :value="4" />
              <el-option label="A栋202室" :value="5" />
              <el-option label="B栋101室" :value="6" />
              <el-option label="B栋102室" :value="7" />
            </el-select>
          </el-form-item>
          <el-form-item label="采集频率" prop="reportInterval">
            <el-select v-model="formData.reportInterval" placeholder="请选择采集频率" style="width: 100%">
              <el-option label="实时（30秒）" :value="30" />
              <el-option label="1分钟" :value="60" />
              <el-option label="5分钟" :value="300" />
              <el-option label="10分钟" :value="600" />
              <el-option label="30分钟" :value="1800" />
              <el-option label="1小时" :value="3600" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
            :disabled="!formData.selectedDevice"
          >
            确定添加
          </el-button>
        </span>
      </template>
    </el-dialog>
    <div class="device-table" @click.stop>
      <!-- 分类Tab -->
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all">全部</el-tab-pane>
        <el-tab-pane label="温湿度" name="humiture">温湿度</el-tab-pane>
        <el-tab-pane label="红外" name="infrared">红外</el-tab-pane>
        <el-tab-pane label="门磁" name="doorMagnet">门磁</el-tab-pane>
        <el-tab-pane label="计算机" name="computer">计算机</el-tab-pane>
      </el-tabs>
      <!-- 设备表格 -->
      <el-table
        :data="tableData"
        @selection-change="handleSelectionChange"
        :row-key="getRowKey"
      >
        <!-- 选择框列（删除模式下显示在最前面） -->
        <el-table-column 
          v-if="isDeleteMode"
          type="selection" 
          width="55" 
          align="center"
          fixed="left"
        />
        <!-- 设备编号列 -->
        <el-table-column
          prop="deviceCode"
          label="设备编号"
          align="center"
          width="120"
        />
        <el-table-column
          prop="name"
          label="名称"
          align="center"
        />
        <el-table-column
          prop="type"
          label="类型"
          align="center"
        />
        <el-table-column
          prop="room"
          label="所在房间"
          align="center"
        />
        <el-table-column
          prop="onlineStatus"
          label="在线状态"
          align="center"
          :formatter="formatOnlineStatus"
        />
        <el-table-column
          prop="lastUploadTime"
          label="最近上传时间"
          align="center"
        />
        <el-table-column
          prop="collectFreq"
          label="采集频率"
          align="center"
        />
        <el-table-column
          prop="alarmStatus"
          label="告警状态"
          align="center"
        />
        <el-table-column
          v-if="!isDeleteMode"
          label="操作"
          align="center"
          width="100"
        >
          <template #default="scope">
            <el-button type="text" @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import deviceService from '../utils/deviceService'

// 已启用的设备列表（从数据库获取）
const allData = ref([])

// 可添加的 HA 设备列表
const availableDevices = ref([])

// 加载状态
const loadingAvailable = ref(false)

const activeTab = ref('all')

// 根据Tab筛选数据
const tableData = computed(() => {
  if (activeTab.value === 'all') {
    return allData.value
  }
  // 根据传感器类型筛选
  const typeMap = {
    'humiture': '温湿度',
    'infrared': '红外',
    'doorMagnet': '门磁',
    'computer': '计算机'
  }
  const targetType = typeMap[activeTab.value]
  return allData.value.filter(item => item.type === targetType)
})

// 加载已启用的设备列表
const loadDeviceList = async () => {
  try {
    const devices = await deviceService.getAllDevices()
    // 将后端数据映射到前端表格格式
    allData.value = devices.map(device => ({
      id: device.id,
      deviceCode: device.deviceid, // HA entity_id
      name: device.devicename,
      type: device.sensortype,
      room: device.source || '-',
      onlineStatus: device.status === 'enabled' || device.status === 'online' ? 1 : 0,
      lastUploadTime: formatDateTime(device.timestamp),
      collectFreq: formatInterval(device.datareportinterval),
      alarmStatus: '正常',
      batterylevel: device.batterylevel
    }))
    console.log('设备列表加载成功:', allData.value)
  } catch (error) {
    console.error('加载设备列表失败:', error)
    ElMessage.error('加载设备列表失败')
  }
}

// 格式化时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  const date = new Date(datetime)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 格式化采集频率
const formatInterval = (seconds) => {
  if (!seconds || seconds === 0) return '未知'
  if (seconds < 60) return `${seconds}秒`
  if (seconds === 60) return '1分钟'
  if (seconds < 3600) return `${seconds / 60}分钟`
  return `${seconds / 3600}小时`
}

// 组件挂载时加载设备列表
onMounted(() => {
  loadDeviceList()
})

const handleTabClick = (tab) => {
  console.log('切换到：', tab.label)
  // 如果处于删除模式，切换Tab时取消删除模式
  if (isDeleteMode.value) {
    cancelDelete()
  }
}

const formatOnlineStatus = (row) => {
  return row.onlineStatus === 1 ? '在线' : '离线'
}

const handleDetail = (row) => {
  ElMessage.info(`查看 ${row.name} 详情`)
  // 可跳转详情页，扩展路由传递参数
}

// 弹窗相关
const dialogVisible = ref(false)
const formRef = ref(null)
const submitLoading = ref(false)

// 当前选中的 HA 设备
const selectedHADevice = computed(() => {
  if (!formData.value.selectedDevice) return null
  return availableDevices.value.find(d => d.entityId === formData.value.selectedDevice)
})

// 表单数据
const formData = ref({
  selectedDevice: '', // 选中的 HA entity_id
  locationId: 1, // 默认位置
  reportInterval: 300 // 默认5分钟
})

// 表单验证规则
const formRules = {
  selectedDevice: [
    { required: true, message: '请选择要添加的设备', trigger: 'change' }
  ],
  locationId: [
    { required: true, message: '请选择设备位置', trigger: 'change' }
  ],
  reportInterval: [
    { required: true, message: '请选择采集频率', trigger: 'change' }
  ]
}

// 处理设备选择
const handleDeviceSelect = (entityId) => {
  console.log('选择设备:', entityId)
}

// 加载可添加的 HA 设备
const loadAvailableDevices = async () => {
  loadingAvailable.value = true
  try {
    const devices = await deviceService.getAvailableDevices()
    availableDevices.value = devices
    console.log('可添加设备加载成功:', devices)
  } catch (error) {
    console.error('加载可添加设备失败:', error)
    ElMessage.error('加载可添加设备失败')
  } finally {
    loadingAvailable.value = false
  }
}

// 添加设备
const handleAdd = async () => {
  dialogVisible.value = true
  // 打开弹窗时加载可添加的设备
  await loadAvailableDevices()
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  formData.value = {
    selectedDevice: '',
    locationId: 1,
    reportInterval: 300
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 准备提交数据
    const submitData = {
      entityId: formData.value.selectedDevice,
      locationId: formData.value.locationId,
      reportInterval: formData.value.reportInterval
    }

    const response = await deviceService.enableDevice(submitData)

    if (response.code === 200) {
      ElMessage.success(response.msg || '设备添加成功')
      dialogVisible.value = false
      // 刷新设备列表
      await loadDeviceList()
    } else {
      ElMessage.error(response.msg || '添加设备失败')
    }
  } catch (error) {
    if (error === false) {
      // 表单验证失败
      return
    }
    // 错误可能是Result对象（从后端返回的错误响应）
    const errorMsg = error.msg || error.message || error.response?.data?.msg || '添加设备失败，请稍后重试'
    ElMessage.error(errorMsg)
    console.error('添加设备失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 删除模式状态
const isDeleteMode = ref(false)

// 选中的设备
const selectedDevices = ref([])

// 获取行的唯一标识
const getRowKey = (row) => {
  return row.id || row.deviceCode || Math.random()
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedDevices.value = selection
}

// 进入删除模式
const enterDeleteMode = () => {
  isDeleteMode.value = true
  selectedDevices.value = []
  ElMessage.info('请选择要删除的设备，然后点击"确定删除"')
}

// 取消删除模式
const cancelDelete = () => {
  isDeleteMode.value = false
  selectedDevices.value = []
  ElMessage.info('已取消删除操作')
}

// 确认删除设备（禁用设备）
const confirmDelete = async () => {
  if (selectedDevices.value.length === 0) {
    ElMessage.warning('请先选择要删除的设备')
    return
  }

  // 显示确认对话框
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedDevices.value.length} 个设备吗？删除后设备将从列表中移除（但仍保留在 Home Assistant 中），此操作可通过重新添加恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: false
    }
  ).then(async () => {
    // 用户确认删除
    try {
      let successCount = 0
      let failCount = 0

      // 逐个删除选中的设备
      for (const device of selectedDevices.value) {
        try {
          // 使用数据库 ID 删除
          if (!device.id) {
            ElMessage.warning(`设备 ${device.name} 缺少ID信息，无法删除`)
            failCount++
            continue
          }
          const response = await deviceService.disableDevice(device.id)

          if (response.code === 200) {
            successCount++
          } else {
            failCount++
            console.error(`删除设备 ${device.name} 失败:`, response.msg)
          }
        } catch (error) {
          failCount++
          console.error(`删除设备 ${device.name} 失败:`, error)
        }
      }

      // 显示删除结果
      if (failCount === 0) {
        ElMessage.success(`成功删除 ${successCount} 个设备`)
      } else {
        ElMessage.warning(`成功删除 ${successCount} 个设备，${failCount} 个设备删除失败`)
      }

      // 退出删除模式
      isDeleteMode.value = false
      selectedDevices.value = []

      // 刷新设备列表
      await loadDeviceList()
    } catch (error) {
      ElMessage.error('删除设备失败，请稍后重试')
      console.error('删除设备失败:', error)
    }
  }).catch(() => {
    // 用户取消删除
    ElMessage.info('已取消删除')
  })
}

// 设置
const handleSetting = () => {
  ElMessage.info('设置功能待实现')
}
</script>

<style scoped>
.device-manage {
  width: 100%;
}
.body{
  background: #f5f6fa;
  margin: 0;
  padding: 0;
}
.header-buttons {
  display: flex;
  float: right;
  gap: 10px;
}
.top-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.top-info h2 {
  font-size: 18px;
  font-weight: bold;
  color: #222;
}
.device-table {
  background: #fff;
  color: #333;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 24px;
}

/* 表格样式（穿透 scoped） */
::v-deep .el-table {
  font-size: 15px;
}

::v-deep .el-table th.el-table__cell,
::v-deep .el-table td.el-table__cell {
  padding: 10px 8px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
}

/* 状态标签 */
::v-deep .status-pending {
  background: #ffe4b3;
  color: #e6a23c;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}

::v-deep .status-done {
  background: #e3f6fd;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}

/* 操作按钮 */
::v-deep .action-btn {
  background: #e3eafc;
  color: #409eff;
  border: none;
  border-radius: 4px;
  padding: 4px 12px;
  cursor: pointer;
  font-size: 13px;
}

::v-deep .action-btn:hover {
  background: #409eff;
  color: #fff;
}
</style>