<template>
  <div class="device-manage">
    <div class = "top-info">
      <h2>物联网监测系统 / 设备管理</h2>
      <div class="header-buttons">
        <el-button type="primary" plain @click="handleAdd">添加</el-button>
        <el-button type="primary" plain @click="handleDelete">删除</el-button>
        <el-button type="primary" plain @click="handleSetting">设置</el-button>
      </div>
    </div>
    <div class="device-table">
      <!-- 分类Tab -->
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all">全部</el-tab-pane>
        <el-tab-pane label="温湿度" name="humiture">温湿度</el-tab-pane>
        <el-tab-pane label="红外" name="infrared">红外</el-tab-pane>
        <el-tab-pane label="门磁" name="doorMagnet">门磁</el-tab-pane>
      </el-tabs>
      <!-- 设备表格 -->
      <el-table
        :data="tableData"
      >
        <el-table-column
          prop="deviceCode"
          label="设备编号"
          align="center"
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
          label="操作"
          align="center"
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
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

// 模拟全量数据
const allData = ref([
  {
    deviceCode: 'D001',
    name: '温湿度传感器1',
    type: '温湿度',
    room: '101室',
    onlineStatus: 1,
    lastUploadTime: '14:20',
    collectFreq: '5分钟',
    alarmStatus: '无'
  },
  {
    deviceCode: 'D002',
    name: '红外传感器',
    type: '红外',
    room: '203室',
    onlineStatus: 1,
    lastUploadTime: '14:18',
    collectFreq: '5分钟',
    alarmStatus: '正常'
  },
  {
    deviceCode: 'D003',
    name: '门磁传感器',
    type: '门磁',
    room: '102室',
    onlineStatus: 1,
    lastUploadTime: '14:15',
    collectFreq: '实时',
    alarmStatus: '正常'
  },
  {
    deviceCode: 'D004',
    name: 'CO₂探测器',
    type: '气体',
    room: '302室',
    onlineStatus: 1,
    lastUploadTime: '14:10',
    collectFreq: '10分钟',
    alarmStatus: '正常'
  },
  {
    deviceCode: 'D005',
    name: '光照传感器',
    type: '环境',
    room: '201室',
    onlineStatus: 0,
    lastUploadTime: '-',
    collectFreq: '-',
    alarmStatus: '未知'
  }
])

const activeTab = ref('all')

// 根据Tab筛选数据
const tableData = computed(() => {
  if (activeTab.value === 'all') {
    return allData.value
  }
  return allData.value.filter(item => item.type === activeTab.value)
})

const handleTabClick = (tab) => {
  console.log('切换到：', tab.label)
}

const formatOnlineStatus = (row) => {
  return row.onlineStatus === 1 ? '在线' : '离线'
}

const handleDetail = (row) => {
  ElMessage.info(`查看 ${row.name} 详情`)
  // 可跳转详情页，扩展路由传递参数
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