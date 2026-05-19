<template>
  <div class="alarm-page">
    <div class="alarm-header">
      <div>
        <h2>物联网监测系统 / 告警中心</h2>
        <p class="subtitle">{{ activeView === 'alerts' ? '查看当前告警记录' : '按房间和数据类型配置告警规则' }}</p>
      </div>

      <div class="header-actions">
        <button v-if="activeView === 'alerts'" class="primary-btn" @click="openRules">
          规则配置
        </button>
        <button v-else class="secondary-btn" @click="activeView = 'alerts'">
          返回告警列表
        </button>
      </div>
    </div>

    <section v-if="activeView === 'alerts'" class="card">
      <div class="alarm-filters">
        <select v-model="selectedPeriod">
          <option value="">全部周期</option>
          <option value="today">今日</option>
          <option value="yesterday">昨日</option>
          <option value="7days">近7天</option>
          <option value="30days">近30天</option>
        </select>

        <select v-model="selectedType">
          <option value="">全部类型</option>
          <option v-for="option in alertTypeOptions" :key="option" :value="option">{{ option }}</option>
        </select>

        <select v-model="selectedStatus">
          <option value="">全部状态</option>
          <option value="pending">未处理</option>
          <option value="done">已处理</option>
        </select>
      </div>

      <div class="alarm-table">
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
            <tr v-for="item in filteredAlarms" :key="item.id">
              <td>{{ item.time }}</td>
              <td>{{ item.room }}</td>
              <td>{{ item.type }}</td>
              <td>
                <span :class="item.status === '已处理' ? 'status-done' : 'status-pending'">
                  {{ item.status }}
                </span>
              </td>
              <td>{{ item.handler }}</td>
              <td>
                <button class="action-btn" @click="markAsDone(item)" :disabled="item.status === '已处理'">
                  {{ item.status === '已处理' ? '已处理' : '标记已处理' }}
                </button>
              </td>
            </tr>
            <tr v-if="filteredAlarms.length === 0">
              <td colspan="6" class="empty-state">暂无符合条件的告警记录</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section v-else class="rules-layout">
      <article v-if="isAdmin" class="card">
        <div class="section-header">
          <div>
            <h3>全局规则</h3>
            <p>管理员设置的全局规则会作为各房间默认规则使用。</p>
          </div>
        </div>

        <div class="rule-table">
          <table>
            <thead>
              <tr>
                <th>数据类型</th>
                <th>规则条件</th>
                <th>告警级别</th>
                <th>启用</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in globalRuleRows" :key="row.key">
                <td>
                  <div class="rule-label">{{ row.label }}</div>
                  <div class="rule-hint">{{ row.hint }}</div>
                </td>
                <td>
                  <input v-if="drafts[row.key]" v-model="drafts[row.key].ruleCondition" class="rule-input" :placeholder="row.defaultCondition" />
                </td>
                <td>
                  <select v-if="drafts[row.key]" v-model="drafts[row.key].severity" class="rule-select">
                    <option value="warning">warning</option>
                    <option value="critical">critical</option>
                  </select>
                </td>
                <td>
                  <label v-if="drafts[row.key]" class="toggle">
                    <input v-model="drafts[row.key].enabled" type="checkbox" />
                    <span>{{ drafts[row.key].enabled ? '已启用' : '已停用' }}</span>
                  </label>
                </td>
                <td>
                  <button class="primary-btn small-btn" :disabled="savingRuleKeys[row.key]" @click="saveRule(row)">
                    {{ savingRuleKeys[row.key] ? '保存中...' : '保存' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="card">
        <div class="section-header">
          <div>
            <h3>房间规则</h3>
            <p>普通用户只能修改自己管理房间的规则；没有房间专属规则时，将继承全局规则。</p>
          </div>
        </div>

        <div v-if="roomRuleGroups.length === 0" class="empty-block">
          当前没有可配置的房间。
        </div>

        <div v-else class="room-groups">
          <section v-for="group in roomRuleGroups" :key="group.locationId" class="room-card">
            <div class="room-card-header">
              <div>
                <h4>{{ group.locationName }}</h4>
                <p>该房间支持对每种数据类型单独配置规则。</p>
              </div>
            </div>

            <div class="rule-table">
              <table>
                <thead>
                  <tr>
                    <th>数据类型</th>
                    <th>规则条件</th>
                    <th>告警级别</th>
                    <th>启用</th>
                    <th>当前来源</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="row in group.rows" :key="row.key">
                    <td>
                      <div class="rule-label">{{ row.label }}</div>
                      <div class="rule-hint">{{ row.hint }}</div>
                    </td>
                    <td>
                      <input v-if="drafts[row.key]" v-model="drafts[row.key].ruleCondition" class="rule-input" :placeholder="row.defaultCondition" />
                    </td>
                    <td>
                      <select v-if="drafts[row.key]" v-model="drafts[row.key].severity" class="rule-select">
                        <option value="warning">warning</option>
                        <option value="critical">critical</option>
                      </select>
                    </td>
                    <td>
                      <label v-if="drafts[row.key]" class="toggle">
                        <input v-model="drafts[row.key].enabled" type="checkbox" />
                        <span>{{ drafts[row.key].enabled ? '已启用' : '已停用' }}</span>
                      </label>
                    </td>
                    <td>
                      <span :class="row.hasLocalRule ? 'scope-tag local' : 'scope-tag global'">
                        {{ row.hasLocalRule ? '房间规则' : '继承全局' }}
                      </span>
                    </td>
                    <td class="action-cell">
                      <button class="primary-btn small-btn" :disabled="savingRuleKeys[row.key]" @click="saveRule(row)">
                        {{ savingRuleKeys[row.key] ? '保存中...' : '保存' }}
                      </button>
                      <button
                        class="secondary-btn small-btn"
                        :disabled="!row.hasLocalRule || deletingRuleKeys[row.key]"
                        @click="resetRoomRule(row)"
                      >
                        {{ deletingRuleKeys[row.key] ? '恢复中...' : '恢复全局' }}
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import alertService from '../utils/alertService'
import dataService from '../utils/dataService'
import userService from '../utils/userService'

const RULE_DEFINITIONS = [
  { sensorType: '\u6e29\u5ea6', label: '\u6e29\u5ea6', defaultCondition: '> 28', hint: '\u793a\u4f8b\uff1a> 28' },
  { sensorType: '\u6e7f\u5ea6', label: '\u6e7f\u5ea6', defaultCondition: '> 75', hint: '\u793a\u4f8b\uff1a> 75' },
  { sensorType: 'CPU\u4f7f\u7528\u7387', label: 'CPU \u4f7f\u7528\u7387', defaultCondition: '> 90', hint: '\u793a\u4f8b\uff1a> 90' },
  { sensorType: '\u5185\u5b58\u4f7f\u7528\u7387', label: '\u5185\u5b58\u4f7f\u7528\u7387', defaultCondition: '> 90', hint: '\u793a\u4f8b\uff1a> 90' },
  { sensorType: '\u78c1\u76d8\u4f7f\u7528\u7387', label: '\u78c1\u76d8\u4f7f\u7528\u7387', defaultCondition: '> 85', hint: '\u793a\u4f8b\uff1a> 85' },
  { sensorType: 'device', label: '\u8bbe\u5907\u79bb\u7ebf', defaultCondition: 'offline', hint: '\u56fa\u5b9a\u4e3a offline' }
]

const activeView = ref('alerts')
const selectedPeriod = ref('')
const selectedType = ref('')
const selectedStatus = ref('')
const alarmList = ref([])
const rules = ref([])
const locations = ref([])
const currentUser = ref(userService.getStoredUser())
const drafts = reactive({})
const savingRuleKeys = reactive({})
const deletingRuleKeys = reactive({})

const isAdmin = computed(() => ['admin', 'super_admin'].includes(currentUser.value?.role))

const alertTypeOptions = computed(() => {
  const types = new Set(alarmList.value.map((item) => item.type).filter(Boolean))
  return [...types]
})

const globalRuleRows = computed(() => buildRows(null))
const roomRuleGroups = computed(() => {
  return locations.value.map((location) => ({
    locationId: location.id,
    locationName: formatLocation(location),
    rows: buildRows(location.id)
  }))
})

const filteredAlarms = computed(() => {
  return alarmList.value.filter((alarm) => {
    if (selectedType.value && alarm.type !== selectedType.value) return false
    if (selectedStatus.value && alarm.status !== statusMap[selectedStatus.value]) return false
    if (selectedPeriod.value && !isInPeriod(alarm.time, selectedPeriod.value)) return false
    return true
  })
})

const statusMap = {
  pending: '\u672a\u5904\u7406',
  done: '\u5df2\u5904\u7406'
}

function formatLocation(location) {
  return `${location.buildingname}-${location.floornumber}F-${location.roomnumber}`
}

function createDefaultDraft(definition, sourceRule = null) {
  return {
    ruleCondition: sourceRule?.ruleCondition || definition.defaultCondition,
    severity: sourceRule?.severity || 'warning',
    enabled: sourceRule?.enabled ?? true
  }
}

function ruleKey(locationId, sensorType) {
  return `${locationId == null ? 'global' : locationId}::${sensorType}`
}

function getRule(locationId, sensorType) {
  return rules.value.find((item) => {
    const sameLocation = locationId == null ? item.locationId == null : Number(item.locationId) === Number(locationId)
    return sameLocation && item.sensorType === sensorType
  }) || null
}

function buildRows(locationId) {
  return RULE_DEFINITIONS.map((definition) => {
    const localRule = getRule(locationId, definition.sensorType)
    const globalRule = getRule(null, definition.sensorType)
    const effectiveRule = localRule || globalRule
    return {
      ...definition,
      key: ruleKey(locationId, definition.sensorType),
      locationId,
      hasLocalRule: Boolean(localRule),
      effectiveRule
    }
  })
}

function syncDrafts() {
  const nextDrafts = {}

  buildRows(null).forEach((row) => {
    if (!isAdmin.value) return
    nextDrafts[row.key] = createDefaultDraft(row, row.effectiveRule)
  })

  locations.value.forEach((location) => {
    buildRows(location.id).forEach((row) => {
      nextDrafts[row.key] = createDefaultDraft(row, row.effectiveRule)
    })
  })

  Object.keys(drafts).forEach((key) => {
    delete drafts[key]
  })
  Object.assign(drafts, nextDrafts)
}

function toDisplayItem(alert) {
  return {
    id: alert.id,
    time: alert.timestamp ? String(alert.timestamp).replace('T', ' ').substring(0, 16) : '-',
    room: alert.roomNumber || '-',
    type: alert.alertType || '-',
    status: Number(alert.handled) === 1 ? '\u5df2\u5904\u7406' : '\u672a\u5904\u7406',
    handler: '-'
  }
}

function isInPeriod(timeStr, period) {
  if (!period) return true
  const now = new Date()
  const target = new Date(timeStr)
  if (period === 'today') return target.toDateString() === now.toDateString()
  if (period === 'yesterday') {
    const yesterday = new Date(now)
    yesterday.setDate(now.getDate() - 1)
    return target.toDateString() === yesterday.toDateString()
  }
  if (period === '7days') {
    const start = new Date(now)
    start.setDate(now.getDate() - 7)
    return target >= start
  }
  if (period === '30days') {
    const start = new Date(now)
    start.setDate(now.getDate() - 30)
    return target >= start
  }
  return true
}

async function loadAlerts() {
  const data = await alertService.getAllAlerts()
  alarmList.value = data.map(toDisplayItem)
}

async function loadLocations() {
  const data = await dataService.getAllLocations()
  locations.value = data || []
}

async function loadRules() {
  rules.value = await alertService.getAlertRules()
  syncDrafts()
}

async function loadCurrentUser() {
  const user = await userService.fetchCurrentUser()
  currentUser.value = user || userService.getStoredUser()
}

function openRules() {
  activeView.value = 'rules'
  loadRules()
}

async function saveRule(row) {
  const draft = drafts[row.key]
  if (!draft) return

  const normalizedCondition = String(draft.ruleCondition || '').trim()
  if (!normalizedCondition) {
    ElMessage.error('\u89c4\u5219\u6761\u4ef6\u4e0d\u80fd\u4e3a\u7a7a')
    return
  }

  if (row.sensorType === 'device' && normalizedCondition !== 'offline') {
    ElMessage.error('\u8bbe\u5907\u79bb\u7ebf\u89c4\u5219\u6761\u4ef6\u5fc5\u987b\u4e3a offline')
    return
  }

  savingRuleKeys[row.key] = true
  try {
    const result = await alertService.saveAlertRule({
      locationId: row.locationId,
      sensorType: row.sensorType,
      ruleCondition: normalizedCondition,
      severity: draft.severity,
      enabled: draft.enabled
    })

    if (!result.ok) {
      ElMessage.error(result.msg || '\u4fdd\u5b58\u5931\u8d25')
      return
    }

    ElMessage.success('\u89c4\u5219\u5df2\u4fdd\u5b58')
    await loadRules()
  } finally {
    savingRuleKeys[row.key] = false
  }
}

async function resetRoomRule(row) {
  deletingRuleKeys[row.key] = true
  try {
    const result = await alertService.deleteAlertRule(row.locationId, row.sensorType)
    if (!result.ok) {
      ElMessage.error(result.msg || '\u6062\u590d\u5168\u5c40\u5931\u8d25')
      return
    }

    ElMessage.success('\u5df2\u6062\u590d\u4e3a\u5168\u5c40\u89c4\u5219')
    await loadRules()
  } finally {
    deletingRuleKeys[row.key] = false
  }
}

async function markAsDone(item) {
  const success = await alertService.markAsHandled(item.id)
  if (success) {
    item.status = '\u5df2\u5904\u7406'
    ElMessage.success('\u5df2\u6807\u8bb0\u4e3a\u5904\u7406')
    return
  }
  ElMessage.error('\u6807\u8bb0\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5')
}

onMounted(async () => {
  await loadCurrentUser()
  await loadLocations()
  await loadAlerts()
  await loadRules()
})
</script>

<style scoped>
.alarm-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.alarm-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.alarm-header h2,
.section-header h3,
.room-card-header h4 {
  margin: 0;
}

.subtitle,
.section-header p,
.room-card-header p,
.rule-hint {
  margin: 6px 0 0;
  color: #607280;
}

.header-actions,
.alarm-filters,
.action-cell {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.card {
  background: #fff;
  border-radius: 18px;
  box-shadow: var(--card-shadow);
  padding: 24px;
}

.rules-layout,
.room-groups {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.room-card {
  border: 1px solid #e4edf3;
  border-radius: 16px;
  padding: 18px;
}

.room-card + .room-card {
  margin-top: 16px;
}

.alarm-filters select,
.rule-select,
.rule-input {
  min-height: 40px;
  border: 1px solid #c8d6df;
  border-radius: 10px;
  padding: 0 12px;
  background: #fff;
}

.rule-input {
  width: 140px;
}

.rule-table table,
.alarm-table table {
  width: 100%;
  border-collapse: collapse;
}

.rule-table th,
.rule-table td,
.alarm-table th,
.alarm-table td {
  padding: 12px 10px;
  border-bottom: 1px solid #edf2f6;
  text-align: left;
  vertical-align: middle;
}

.rule-label {
  font-weight: 600;
  color: #173a56;
}

.status-pending,
.status-done,
.scope-tag {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 13px;
}

.status-pending {
  background: #fff0cc;
  color: #b97900;
}

.status-done {
  background: #e0f3e8;
  color: #1d7c45;
}

.scope-tag.global {
  background: #edf4fa;
  color: #18456b;
}

.scope-tag.local {
  background: #e8f6ef;
  color: #1d7c45;
}

.toggle {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #425767;
}

.primary-btn,
.secondary-btn,
.action-btn {
  border: 0;
  border-radius: 10px;
  min-height: 40px;
  padding: 0 16px;
  cursor: pointer;
}

.primary-btn {
  background: #18456b;
  color: #fff;
}

.secondary-btn {
  background: #edf4fa;
  color: #18456b;
}

.action-btn {
  background: #e3eef9;
  color: #18456b;
}

.small-btn {
  min-height: 34px;
  padding: 0 12px;
}

.empty-state,
.empty-block {
  text-align: center;
  color: #7b8f9d;
  padding: 24px 0;
}

@media (max-width: 900px) {
  .alarm-header {
    flex-direction: column;
  }

  .rule-table {
    overflow-x: auto;
  }
}
</style>
