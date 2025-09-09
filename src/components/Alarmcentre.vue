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
          <input type="text" placeholder="全部周期" />
          <input type="text" placeholder="全部类型" />
          <input type="text" placeholder="今日" />
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
            <tr v-for="item in alarmList" :key="item.time">
              <td>{{ item.time }}</td>
              <td>{{ item.room }}</td>
              <td>{{ item.type }}</td>
              <td>
                <span :class="item.status === '未处理' ? 'status-pending' : 'status-done'">
                  {{ item.status }}
                </span>
              </td>
              <td>{{ item.handler }}</td>
              <td>
                <button class="action-btn">标记已处理</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
</template>

<script setup>
const alarmList = [
  { time: '14:25', room: '101室', type: '高温', status: '未处理', handler: '-' },
  { time: '13:10', room: '203室', type: '设备离线', status: '处理中', handler: '张三' },
  { time: '10:05', room: '302室', type: 'CO₂超标', status: '处理中', handler: '李四' },
  { time: '09:30', room: '102室', type: '高温', status: '未处理', handler: '李四' },
  { time: '08:45', room: '201室', type: '设备离线', status: '未处理', handler: '-' }
]
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
.alarm-filters input {
  margin-right: 12px;
  padding: 6px 12px;
  border: 1px solid #e0e6ed;
  border-radius: 4px;
  background: #fff;
}
.card {
  background: #fff;
  color: #333;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  padding: 24px;
}
.alarm-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 15px;
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
}
.action-btn:hover {
  background: #409eff;
  color: #fff;
}
</style>