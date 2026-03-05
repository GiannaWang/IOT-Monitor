<template>
  <!-- 侧边栏 -->
  <!-- <div class="dashboard-container">
    
    <aside class="sidebar">
      <div class="sidebar-header">物联网监测系统</div>
      <nav class="sidebar-nav">
        <ul>
          <li>首页仪表盘</li>
          <li>设备管理</li>
          <li>数据分析</li>
          <li class="active">告警中心</li>
        </ul>
      </nav>
      <div class="sidebar-footer">管理员</div>
    </aside> -->

    <div class="content-wrapper">
    <!-- 主内容区 -->
      <div class="alarm-header">
        <h2>物联网监测系统 / 告警中心</h2>
        <div class="alarm-filters">
          <!-- 周期下拉选择 -->
          <select v-model="selectedPeriod">
            <option value="">全部周期</option>
            <option value="today">今日</option>
            <option value="yesterday">昨日</option>
            <option value="7days">近7天</option>
            <option value="30days">近30天</option>
            <option value="custom">自定义</option>
          </select>
          
          <!-- 类型下拉选择 -->
          <select v-model="selectedType">
            <option value="">全部类型</option>
            <option value="highTemp">高温</option>
            <option value="offline">设备离线</option>
            <option value="co2">CO₂超标</option>
            <option value="humidity">湿度异常</option>
            <option value="power">电源异常</option>
          </select>
          
          <!-- 状态下拉选择 -->
          <select v-model="selectedStatus">
            <option value="">全部状态</option>
            <option value="pending">未处理</option>
            <option value="processing">处理中</option>
            <option value="done">已处理</option>
          </select>
        </div>
      </div>
      <div class="alarm-table card">
        <table>
          <thead>
            <tr>
              <th>时间</th>
              <th>房间</th>
              <th>类型</th>
              <th>状态</th>
              <th>处理人</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredAlarms" :key="item.time">
              <td>{{ item.time }}</td>
              <td>{{ item.room }}</td>
              <td>{{ item.type }}</td>
              <td>
                <span :class="item.status === '未处理' ? 'status-pending' : 
                            item.status === '处理中' ? 'status-processing' : 'status-done'">
                  {{ item.status }}
                </span>
              </td>
              <td>{{ item.handler }}</td>
              <td>
                <button class="action-btn" @click="markAsDone(item)" 
                        :disabled="item.status === '已处理'">
                  {{ item.status === '已处理' ? '已处理' : '标记已处理' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import alertService from '../utils/alertService'

const selectedPeriod = ref('');
const selectedType = ref('');
const selectedStatus = ref('');

const alarmList = ref([]);

// 将后端 Alert 对象转为页面显示格式
function toDisplayItem(alert) {
  return {
    id: alert.id,
    time: alert.timestamp ? alert.timestamp.replace('T', ' ').substring(0, 16) : '-',
    room: alert.roomNumber ? `${alert.roomNumber}室` : '-',
    type: alert.alertType || '-',
    status: alert.handled === 1 ? '已处理' : '未处理',
    handler: '-'
  }
}

onMounted(async () => {
  const data = await alertService.getAllAlerts();
  alarmList.value = data.map(toDisplayItem);
});

// 日期范围过滤辅助
function isInPeriod(timeStr, period) {
  if (!period) return true;
  const now = new Date();
  const t = new Date(timeStr);
  if (period === 'today') {
    return t.toDateString() === now.toDateString();
  }
  if (period === 'yesterday') {
    const y = new Date(now); y.setDate(now.getDate() - 1);
    return t.toDateString() === y.toDateString();
  }
  if (period === '7days') {
    const d = new Date(now); d.setDate(now.getDate() - 7);
    return t >= d;
  }
  if (period === '30days') {
    const d = new Date(now); d.setDate(now.getDate() - 30);
    return t >= d;
  }
  return true;
}

const typeMap = { highTemp: '高温', offline: '设备离线', co2: 'CO₂超标', humidity: '湿度异常', power: '电源异常' };
const statusMap = { pending: '未处理', done: '已处理' };

const filteredAlarms = computed(() => {
  return alarmList.value.filter(alarm => {
    if (selectedType.value && alarm.type !== (typeMap[selectedType.value] || '')) return false;
    if (selectedStatus.value && alarm.status !== (statusMap[selectedStatus.value] || '')) return false;
    if (selectedPeriod.value && !isInPeriod(alarm.time, selectedPeriod.value)) return false;
    return true;
  });
});

async function markAsDone(item) {
  const success = await alertService.markAsHandled(item.id);
  if (success) {
    item.status = '已处理';
    ElMessage.success('已标记为处理');
  } else {
    ElMessage.error('标记失败，请重试');
  }
}
</script>

<style scoped>
.content-wrapper {
  width: 100%;
}
.alarm-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.alarm-header h2 {
  font-size: 18px;
  font-weight: bold;
  color: #222;
}
.alarm-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.alarm-filters select {
  margin-right: 12px;
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
}
.card {
  background: #fff;
  color: #333;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 24px;
}
.alarm-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 15px;
  table-layout: fixed; /* 固定表格布局算法，防止列宽随内容变化 */
}
.alarm-table th, .alarm-table td {
  padding: 10px 8px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
}
.status-pending {
  background: #ffe4b3;
  color: #e6a23c;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}
.status-processing {
  background: #e6f7ff;
  color: #1890ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}
.status-done {
  background: #e3f6fd;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}
.action-btn {
  background: #e3eafc;
  color: #409eff;
  border: none;
  border-radius: 4px;
  padding: 4px 12px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s; /* 添加过渡效果 */
}
.action-btn:hover {
  background: #409eff;
  color: #fff;
}
</style>