const now = new Date()

const SENSOR_TYPE_COMPUTER = '计算机'
const SENSOR_TYPE_TEMP_HUMIDITY = '温湿度'
const SENSOR_TYPE_INFRARED = '红外'
const SENSOR_TYPE_DOOR = '门磁'

let users = [
  {
    userId: 1,
    username: 'admin',
    passwordHash: 'admin123',
    role: 'admin',
    avatar: '/src/assets/avatar/fall.bmp',
    lastLoginTime: '2026-05-13 09:20:00',
    room: 'All Rooms',
    roomIds: [],
    roomNames: []
  },
  {
    userId: 2,
    username: 'room_user',
    passwordHash: 'user123',
    role: 'user',
    avatar: '/src/assets/avatar/avatar2-1.jpg',
    lastLoginTime: '2026-05-13 09:10:00',
    room: 'A-1F-101, A-2F-201',
    roomIds: [1, 2],
    roomNames: ['A-1F-101', 'A-2F-201']
  },
  {
    userId: 3,
    username: 'user_zhang',
    passwordHash: 'user123',
    role: 'user',
    avatar: '/src/assets/avatar/avatar3-1.jpg',
    lastLoginTime: '2026-05-13 08:45:00',
    room: 'B-1F-115',
    roomIds: [3],
    roomNames: ['B-1F-115']
  },
  {
    userId: 4,
    username: 'user_li',
    passwordHash: 'user123',
    role: 'user',
    avatar: '/src/assets/avatar/avatar4-1.jpg',
    lastLoginTime: '2026-05-13 08:30:00',
    room: 'B-1F-117',
    roomIds: [4],
    roomNames: ['B-1F-117']
  },
  {
    userId: 5,
    username: 'user_wang',
    passwordHash: 'user123',
    role: 'user',
    avatar: '/src/assets/avatar/avatar5-1.jpg',
    lastLoginTime: '2026-05-13 08:18:00',
    room: 'A-1F-101, B-1F-115',
    roomIds: [1, 3],
    roomNames: ['A-1F-101', 'B-1F-115']
  }
]

let locations = [
  { id: 1, buildingname: 'A', floornumber: 1, roomnumber: 101, description: 'Lab Room 101' },
  { id: 2, buildingname: 'A', floornumber: 2, roomnumber: 201, description: 'Lab Room 201' },
  { id: 3, buildingname: 'B', floornumber: 1, roomnumber: 115, description: 'Office 115' },
  { id: 4, buildingname: 'B', floornumber: 1, roomnumber: 117, description: 'Office 117' }
]

const buildRoomName = (locationId) => {
  const location = locations.find((item) => item.id === locationId)
  return location ? `${location.buildingname}-${location.floornumber}F-${location.roomnumber}` : `Room-${locationId}`
}

function syncUserRooms() {
  users = users.map((user) => {
    if (isAdmin(user)) return user
    const roomIds = (user.roomIds || []).filter((roomId) => locations.some((item) => item.id === roomId))
    const roomNames = roomIds.map(buildRoomName)
    return {
      ...user,
      roomIds,
      roomNames,
      room: roomNames.join(', ')
    }
  })
}

const windowsHosts = [
  {
    id: 4,
    deviceid: 'windows.pc_office_main',
    devicename: 'Office Host Main',
    sensortype: SENSOR_TYPE_COMPUTER,
    source: 'B-1F-115',
    status: 'online',
    timestamp: '2026-05-13T09:26:00',
    datareportinterval: 60,
    batterylevel: null,
    locationid: 3
  },
  {
    id: 5,
    deviceid: 'windows.pc_office_secondary',
    devicename: 'Office Host Secondary',
    sensortype: SENSOR_TYPE_COMPUTER,
    source: 'B-1F-117',
    status: 'online',
    timestamp: '2026-05-13T09:27:00',
    datareportinterval: 60,
    batterylevel: null,
    locationid: 4
  },
  {
    id: 6,
    deviceid: 'windows.pc_office_backup',
    devicename: 'Office Host Backup',
    sensortype: SENSOR_TYPE_COMPUTER,
    source: 'B-1F-117',
    status: 'online',
    timestamp: '2026-05-13T09:28:00',
    datareportinterval: 60,
    batterylevel: null,
    locationid: 4
  }
]

let enabledDevices = [
  {
    id: 1,
    deviceid: 'sensor.temp_main',
    devicename: '温湿度传感器-A',
    sensortype: SENSOR_TYPE_TEMP_HUMIDITY,
    source: 'A-1F-101',
    status: 'enabled',
    timestamp: '2026-05-13T09:25:00',
    datareportinterval: 300,
    batterylevel: 88,
    locationid: 1
  },
  {
    id: 2,
    deviceid: 'sensor.infrared_main',
    devicename: '红外传感器-A',
    sensortype: SENSOR_TYPE_INFRARED,
    source: 'A-2F-201',
    status: 'enabled',
    timestamp: '2026-05-13T09:23:00',
    datareportinterval: 60,
    batterylevel: 76,
    locationid: 2
  },
  {
    id: 3,
    deviceid: 'binary_sensor.door_main',
    devicename: '门磁传感器-A',
    sensortype: SENSOR_TYPE_DOOR,
    source: 'B-1F-115',
    status: 'enabled',
    timestamp: '2026-05-13T09:21:00',
    datareportinterval: 30,
    batterylevel: 91,
    locationid: 3
  },
  ...windowsHosts
]

let availableDevices = [
  {
    entityId: 'sensor.temp_standby',
    state: '23.4',
    attributes: { friendly_name: '温湿度传感器-B' }
  },
  {
    entityId: 'binary_sensor.door_standby',
    state: 'off',
    attributes: { friendly_name: '门磁传感器-B' }
  }
]

let alerts = [
  { id: 1, timestamp: '2026-05-13 08:12:00', roomNumber: 'A101', alertType: '温度过高', handled: 0, locationId: 1 },
  { id: 2, timestamp: '2026-05-13 07:48:00', roomNumber: 'A201', alertType: '设备离线', handled: 1, locationId: 2 },
  { id: 3, timestamp: '2026-05-12 21:15:00', roomNumber: 'B115', alertType: '门磁异常', handled: 0, locationId: 3 },
  { id: 4, timestamp: '2026-05-12 18:30:00', roomNumber: 'B117', alertType: 'CPU 使用率过高', handled: 0, locationId: 4 },
  { id: 5, timestamp: '2026-05-12 16:45:00', roomNumber: 'B115', alertType: '磁盘使用率过高', handled: 1, locationId: 3 },
  { id: 6, timestamp: '2026-05-12 10:10:00', roomNumber: 'B117', alertType: '内存使用率过高', handled: 0, locationId: 4 }
]

const computerSnapshotMap = {
  4: { host: 'OFFICE-115', foreground_name: 'Code.exe', total: 162, background: 161, ratio: 0.99, cpu: 28, memory: 48, disk: 67, network_up_kbps: 320, network_down_kbps: 1580 },
  5: { host: 'OFFICE-117', foreground_name: 'chrome.exe', total: 143, background: 142, ratio: 0.99, cpu: 52, memory: 71, disk: 81, network_up_kbps: 180, network_down_kbps: 740 },
  6: { host: 'OFFICE-117-B', foreground_name: 'WINWORD.EXE', total: 118, background: 117, ratio: 0.99, cpu: 14, memory: 39, disk: 56, network_up_kbps: 90, network_down_kbps: 210 }
}

const buildTimeSeries = (count, minutesStep, endTime = now) => {
  return Array.from({ length: count }, (_, index) => {
    const date = new Date(endTime)
    date.setMinutes(date.getMinutes() - minutesStep * (count - index - 1))
    return date.toISOString().slice(0, 19)
  })
}

function buildMockSensorSeries({ type, locationId, deviceId, values, minutesStep, extras = {} }) {
  return buildTimeSeries(values.length, minutesStep).map((timeStamp, index) => ({
    id: `${type}-${locationId}-${index}`,
    type,
    locationId,
    deviceId,
    timeStamp,
    value: values[index],
    ...extras
  }))
}

function buildComputerMetrics(deviceId, cpuValues, memoryValues, diskValues, ratioValues) {
  const device = enabledDevices.find((item) => item.id === deviceId)
  const snapshot = computerSnapshotMap[deviceId]
  if (!device || !snapshot) return []

  return [
    ...buildMockSensorSeries({ type: 'cpu', locationId: device.locationid, deviceId, values: cpuValues, minutesStep: 30, extras: { source: device.source, host: snapshot.host } }),
    ...buildMockSensorSeries({ type: 'memory', locationId: device.locationid, deviceId, values: memoryValues, minutesStep: 30, extras: { source: device.source, host: snapshot.host } }),
    ...buildMockSensorSeries({ type: 'disk', locationId: device.locationid, deviceId, values: diskValues, minutesStep: 30, extras: { source: device.source, host: snapshot.host } }),
    ...buildMockSensorSeries({ type: 'background_ratio', locationId: device.locationid, deviceId, values: ratioValues, minutesStep: 30, extras: { source: device.source, host: snapshot.host } })
  ]
}

const metricSeries = [
  ...buildMockSensorSeries({ type: 'temperature', locationId: 1, deviceId: 1, values: [21.8, 22.1, 22.5, 22.9, 22.0, 22.4, 22.8, 23.3, 22.2, 22.7, 23.1, 23.6, 22.5, 23.0, 23.4, 23.9, 22.7, 23.2, 23.7, 24.1, 22.9, 23.5, 24.0, 24.4, 23.2, 23.8, 24.3, 24.8], minutesStep: 360 }),
  ...buildMockSensorSeries({ type: 'humidity', locationId: 1, deviceId: 1, values: [53, 52, 50, 49, 52, 51, 49, 48, 51, 50, 48, 47, 50, 49, 47, 46, 49, 48, 46, 45, 48, 47, 45, 44, 47, 46, 44, 43], minutesStep: 360 }),
  ...buildMockSensorSeries({ type: 'temperature', locationId: 2, deviceId: 2, values: [20.6, 20.9, 21.2, 21.5, 20.8, 21.1, 21.4, 21.8, 21.0, 21.4, 21.7, 22.0, 21.2, 21.6, 21.9, 22.3, 21.4, 21.8, 22.2, 22.5, 21.7, 22.0, 22.4, 22.8], minutesStep: 420 }),
  ...buildMockSensorSeries({ type: 'humidity', locationId: 2, deviceId: 2, values: [49, 48, 47, 46, 48, 47, 46, 45, 47, 46, 45, 44, 46, 45, 44, 43, 45, 44, 43, 42, 44, 43, 42, 41], minutesStep: 420 }),
  ...buildMockSensorSeries({ type: 'infrared', locationId: 2, deviceId: 2, values: [2, 4, 3, 6, 8, 7, 5, 9, 6, 4], minutesStep: 60 }),
  ...buildMockSensorSeries({ type: 'door', locationId: 3, deviceId: 3, values: [0, 1, 0, 2, 1, 3, 2, 1, 0, 1], minutesStep: 60 }),
  ...buildComputerMetrics(4, [18, 25, 20, 44, 38, 60, 52, 34, 41, 28], [42, 45, 47, 49, 48, 53, 55, 52, 50, 48], [63, 63, 64, 64, 65, 65, 66, 66, 66, 67], [0.95, 0.96, 0.97, 0.98, 0.98, 0.99, 0.99, 0.98, 0.99, 0.99]),
  ...buildComputerMetrics(5, [32, 36, 40, 55, 61, 68, 72, 66, 58, 52], [58, 60, 63, 65, 67, 70, 72, 73, 71, 69], [75, 76, 76, 77, 78, 79, 80, 81, 81, 81], [0.96, 0.97, 0.97, 0.98, 0.98, 0.99, 0.99, 0.99, 0.99, 0.99]),
  ...buildComputerMetrics(6, [11, 13, 14, 16, 15, 18, 17, 16, 15, 14], [34, 35, 36, 38, 39, 41, 40, 39, 39, 38], [54, 54, 55, 55, 56, 56, 56, 56, 56, 56], [0.97, 0.97, 0.98, 0.98, 0.98, 0.99, 0.99, 0.99, 0.99, 0.99])
]

let alertRules = [
  { id: 1, ruleName: '温度告警', description: '室内温度超过 28°C 时触发告警', ruleCondition: '> 28', sensorType: '温度', severity: 'warning', locationId: null, enabled: true, createdByUserId: 1, createTime: '2026-05-13 09:00:00', updateTime: '2026-05-13 09:00:00' },
  { id: 2, ruleName: '湿度告警', description: '室内湿度超过 75% 时触发告警', ruleCondition: '> 75', sensorType: '湿度', severity: 'warning', locationId: null, enabled: true, createdByUserId: 1, createTime: '2026-05-13 09:00:00', updateTime: '2026-05-13 09:00:00' },
  { id: 3, ruleName: '设备离线', description: '设备超过正常上报间隔 3 倍时间未上报数据时触发告警', ruleCondition: 'offline', sensorType: 'device', severity: 'warning', locationId: null, enabled: true, createdByUserId: 1, createTime: '2026-05-13 09:00:00', updateTime: '2026-05-13 09:00:00' },
  { id: 4, ruleName: 'CPU过载', description: 'Windows 主机 CPU 使用率超过 90% 时触发告警', ruleCondition: '> 90', sensorType: 'CPU使用率', severity: 'warning', locationId: null, enabled: true, createdByUserId: 1, createTime: '2026-05-13 09:00:00', updateTime: '2026-05-13 09:00:00' },
  { id: 5, ruleName: '内存不足', description: 'Windows 主机内存使用率超过 90% 时触发告警', ruleCondition: '> 90', sensorType: '内存使用率', severity: 'warning', locationId: null, enabled: true, createdByUserId: 1, createTime: '2026-05-13 09:00:00', updateTime: '2026-05-13 09:00:00' },
  { id: 6, ruleName: '磁盘告警', description: 'Windows 主机磁盘使用率超过 85% 时触发告警', ruleCondition: '> 85', sensorType: '磁盘使用率', severity: 'warning', locationId: null, enabled: true, createdByUserId: 1, createTime: '2026-05-13 09:00:00', updateTime: '2026-05-13 09:00:00' },
  { id: 7, ruleName: '温度告警', description: 'A-1F-101 房间温度超过 30°C 时触发告警', ruleCondition: '> 30', sensorType: '温度', severity: 'critical', locationId: 1, enabled: true, createdByUserId: 2, createTime: '2026-05-13 09:05:00', updateTime: '2026-05-13 09:05:00' }
]

let currentMockUserId = null

function success(data, extra = {}) {
  return { code: 200, data, msg: 'success', ...extra }
}

function fail(msg = 'mock request failed', code = 500) {
  return { code, msg, data: null }
}

function normalizeType(input) {
  const value = String(input || '').toLowerCase()
  if (value.includes('cpu')) return 'cpu'
  if (value.includes('memory') || value.includes('内存')) return 'memory'
  if (value.includes('disk') || value.includes('磁盘')) return 'disk'
  if (value.includes('ratio') || value.includes('后台') || value.includes('background')) return 'background_ratio'
  if (value.includes('humidity') || value.includes('湿')) return 'humidity'
  if (value.includes('temperature') || value.includes('温')) return 'temperature'
  if (value.includes('infrared') || value.includes('红外')) return 'infrared'
  if (value.includes('door') || value.includes('门磁')) return 'door'
  return value
}

function withDelay(result) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(result), 150)
  })
}

function getParams(config = {}) {
  return config.params || {}
}

function getData(config = {}) {
  return config.data || {}
}

function cloneUser(user) {
  return {
    ...user,
    roomIds: [...(user.roomIds || [])],
    roomNames: [...(user.roomNames || [])],
    passwordHash: null
  }
}

function cloneRule(rule) {
  return {
    ...rule,
    locationName: rule.locationId == null ? '全局规则' : buildRoomName(rule.locationId)
  }
}

function getCurrentUser() {
  return users.find((item) => item.userId === currentMockUserId) || null
}

function isAdmin(user) {
  return ['admin', 'super_admin'].includes(user?.role)
}

function getAccessibleLocationIds(user) {
  if (!user || isAdmin(user)) return null
  return user.roomIds || []
}

function filterByAccess(items, getLocationId) {
  const user = getCurrentUser()
  const roomIds = getAccessibleLocationIds(user)
  if (roomIds == null) return items
  return items.filter((item) => roomIds.includes(getLocationId(item)))
}

function canManageLocation(user, locationId) {
  if (locationId == null) return isAdmin(user)
  if (isAdmin(user)) return true
  return (user?.roomIds || []).includes(Number(locationId))
}

function filterSensorRecords(params = {}) {
  const type = normalizeType(params.selectedDataType || params.sensorType)
  const locationId = params.locationId != null && params.locationId !== '' ? Number(params.locationId) : null
  const deviceId = params.deviceId != null && params.deviceId !== '' ? Number(params.deviceId) : null

  const records = filterByAccess(metricSeries, (item) => item.locationId)

  return records.filter((item) => {
    if (type && item.type !== type) return false
    if (locationId != null && item.locationId !== locationId) return false
    if (deviceId != null && item.deviceId !== deviceId) return false
    return true
  })
}

export async function mockRequest(method, url, config = {}) {
  const params = getParams(config)
  const data = getData(config)

  if (method === 'get' && url === '/me') {
    const user = getCurrentUser()
    return withDelay(user ? success(cloneUser(user)) : fail('未登录', 401))
  }

  if (method === 'get' && url === '/users') {
    const user = getCurrentUser()
    if (!isAdmin(user)) return withDelay(fail('没有权限', 403))
    return withDelay(success(users.map(cloneUser)))
  }

  if (method === 'post' && url === '/users/assign-rooms') {
    const user = getCurrentUser()
    if (!isAdmin(user)) return withDelay(fail('没有权限', 403))
    const target = users.find((item) => item.userId === Number(data.userId))
    if (!target) return withDelay(fail('用户不存在', 404))
    if (isAdmin(target)) return withDelay(fail('管理员无需分配房间', 400))

    target.roomIds = [...(data.roomIds || [])].map(Number)
    target.roomNames = target.roomIds.map(buildRoomName)
    target.room = target.roomNames.join(', ')
    return withDelay(success(true))
  }

  if (method === 'get' && url === '/getUserByUsername') {
    const user = users.find((item) => item.username === params.username)
    return withDelay(user ? success(cloneUser(user)) : fail('用户不存在', 404))
  }

  if (method === 'post' && url === '/login') {
    const user = users.find((item) => item.username === data.username)
    if (!user || user.passwordHash !== data.passwordHash) {
      return withDelay(fail('用户名或密码错误', 401))
    }

    currentMockUserId = user.userId
    user.lastLoginTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
    return withDelay(success({
      token: `mock-jwt-token-${user.userId}`,
      user: cloneUser(user)
    }))
  }

  if (method === 'post' && url === '/updateAvatar') {
    const user = users.find((item) => item.userId === Number(params.userId))
    if (!user) return withDelay(fail('用户不存在', 404))
    user.avatar = params.avatarUrl
    return withDelay(success(true))
  }

  if (method === 'post' && url === '/changePassword') {
    const user = users.find((item) => item.userId === Number(params.userId))
    if (!user) return withDelay(fail('用户不存在', 404))
    if (params.oldPassword && user.passwordHash !== params.oldPassword) {
      return withDelay(fail('原密码错误', 400))
    }
    user.passwordHash = params.newPassword
    return withDelay(success(true))
  }

  if (method === 'get' && url === '/getAllSensorData') {
    return withDelay(success(filterByAccess(metricSeries, (item) => item.locationId)))
  }

  if (method === 'get' && url === '/getSensorDataByType') {
    return withDelay(success(filterSensorRecords(params)))
  }

  if (method === 'get' && url === '/countAllDevices') {
    return withDelay(success(filterByAccess(enabledDevices, (item) => item.locationid).length))
  }

  if (method === 'get' && url === '/countOnlineDevices') {
    const count = filterByAccess(enabledDevices, (item) => item.locationid)
      .filter((item) => item.status === 'enabled' || item.status === 'online').length
    return withDelay(success(count))
  }

  if (method === 'get' && url === '/get10SensorDataByType') {
    const records = filterSensorRecords(params)
    return withDelay(success(records.slice(-10)))
  }

  if (method === 'get' && url === '/location/all') {
    return withDelay(success(filterByAccess(locations, (item) => item.id)))
  }
 
  if (method === 'post' && url === '/location') {
    const user = getCurrentUser()
    if (!isAdmin(user)) return withDelay(fail('娌℃湁鏉冮檺', 403))

    const buildingname = String(data.buildingname || '').trim()
    const floornumber = Number(data.floornumber)
    const roomnumber = Number(data.roomnumber)
    const description = String(data.description || '').trim()
    if (!buildingname || !Number.isInteger(floornumber) || floornumber <= 0 || !Number.isInteger(roomnumber) || roomnumber <= 0) {
      return withDelay(fail('鍙傛暟涓嶅悎娉', 400))
    }

    const duplicate = locations.find((item) =>
      item.buildingname === buildingname
      && item.floornumber === floornumber
      && item.roomnumber === roomnumber
    )
    if (duplicate) return withDelay(fail('鎴块棿宸插瓨鍦', 400))

    const nextLocation = {
      id: locations.length ? Math.max(...locations.map((item) => item.id)) + 1 : 1,
      buildingname,
      floornumber,
      roomnumber,
      description
    }
    locations = [...locations, nextLocation]
    return withDelay(success(nextLocation))
  }

  if (method === 'get' && /^\/location\/\d+$/.test(url)) {
    const id = Number(url.split('/').pop())
    const location = filterByAccess(locations, (item) => item.id).find((item) => item.id === id)
    return withDelay(location ? success(location) : fail('位置不存在', 404))
  }

  if (method === 'delete' && /^\/location\/\d+$/.test(url)) {
    const user = getCurrentUser()
    if (!isAdmin(user)) return withDelay(fail('娌℃湁鏉冮檺', 403))

    const id = Number(url.split('/').pop())
    const location = locations.find((item) => item.id === id)
    if (!location) return withDelay(fail('鎴块棿涓嶅瓨鍦', 404))

    if (enabledDevices.some((item) => item.locationid === id)) {
      return withDelay(fail('璇ュ房闂翠笅浠嶆湁璁惧锛屾棤娉曞垹闄', 400))
    }
    if (alerts.some((item) => item.locationId === id)) {
      return withDelay(fail('璇ュ房闂翠笅浠嶆湁鍘嗗彶鍛婅锛屾棤娉曞垹闄', 400))
    }

    locations = locations.filter((item) => item.id !== id)
    alertRules = alertRules.filter((item) => item.locationId !== id)
    syncUserRooms()
    return withDelay(success(true, { msg: '鎴块棿鍒犻櫎鎴愬姛' }))
  }

  if (method === 'get' && url === '/getSensorDataWithFilters') {
    return withDelay(success(filterSensorRecords(params)))
  }

  if (method === 'get' && url === '/alert-rules') {
    const user = getCurrentUser()
    const roomIds = getAccessibleLocationIds(user)
    const visibleRules = roomIds == null
      ? alertRules
      : alertRules.filter((item) => item.locationId == null || roomIds.includes(item.locationId))
    return withDelay(success(visibleRules.map(cloneRule)))
  }

  if (method === 'post' && url === '/alert-rules') {
    const user = getCurrentUser()
    if (!user) return withDelay(fail('未登录', 401))

    const locationId = data.locationId != null && data.locationId !== '' ? Number(data.locationId) : null
    if (!canManageLocation(user, locationId)) {
      return withDelay(fail('没有权限', 403))
    }

    const sensorType = String(data.sensorType || '').trim()
    const ruleCondition = String(data.ruleCondition || '').trim()
    const severity = String(data.severity || 'warning').trim()
    if (!sensorType || !ruleCondition) {
      return withDelay(fail('参数不完整', 400))
    }

    const existing = alertRules.find((item) => item.sensorType === sensorType && item.locationId === locationId)
    const nextRule = {
      id: existing?.id || Date.now(),
      ruleName: data.ruleName || ({
        '温度': '温度告警',
        '湿度': '湿度告警',
        'CPU使用率': 'CPU过载',
        '内存使用率': '内存不足',
        '磁盘使用率': '磁盘告警',
        device: '设备离线'
      }[sensorType] || `${sensorType}告警`),
      description: data.description || (ruleCondition === 'offline'
        ? '设备超过正常上报间隔 3 倍时间未上报数据时触发告警'
        : `${sensorType}满足条件 ${ruleCondition} 时触发告警`),
      ruleCondition,
      sensorType,
      severity,
      locationId,
      enabled: data.enabled !== false,
      createdByUserId: user.userId,
      createTime: existing?.createTime || new Date().toISOString().slice(0, 19).replace('T', ' '),
      updateTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }

    if (existing) {
      alertRules = alertRules.map((item) => (item.id === existing.id ? nextRule : item))
    } else {
      alertRules = [...alertRules, nextRule]
    }

    return withDelay(success(true))
  }

  if (method === 'delete' && url === '/alert-rules') {
    const user = getCurrentUser()
    if (!user) return withDelay(fail('未登录', 401))

    const locationId = params.locationId != null && params.locationId !== '' ? Number(params.locationId) : null
    if (!canManageLocation(user, locationId)) {
      return withDelay(fail('没有权限', 403))
    }

    const sensorType = String(params.sensorType || '').trim()
    const before = alertRules.length
    alertRules = alertRules.filter((item) => !(item.sensorType === sensorType && item.locationId === locationId))
    return withDelay(before === alertRules.length ? fail('规则不存在', 404) : success(true))
  }

  if (method === 'get' && url === '/getAllAlerts') {
    return withDelay(success(filterByAccess(alerts, (item) => item.locationId)))
  }

  if (method === 'post' && url === '/markAsHandled') {
    const alert = alerts.find((item) => item.id === Number(params.alertId))
    if (!alert) return withDelay(fail('告警不存在', 404))
    alert.handled = 1
    return withDelay(success(true))
  }

  if (method === 'get' && url === '/getLatest5Alerts') {
    const latest = filterByAccess(alerts, (item) => item.locationId)
      .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
      .slice(0, 5)
    return withDelay(success(latest))
  }

  if (method === 'get' && url === '/getTodayAlertCount') {
    const today = new Date().toISOString().slice(0, 10)
    const count = filterByAccess(alerts, (item) => item.locationId)
      .filter((item) => item.timestamp.startsWith(today)).length
    return withDelay(success(count))
  }

  if (method === 'get' && url === '/device/enabled') {
    return withDelay(success(filterByAccess(enabledDevices, (item) => item.locationid)))
  }

  if (method === 'get' && url === '/device/available') {
    const user = getCurrentUser()
    if (!isAdmin(user)) return withDelay(fail('没有权限', 403))
    return withDelay(success(availableDevices))
  }

  if (method === 'post' && url === '/device/enable') {
    const user = getCurrentUser()
    if (!isAdmin(user)) return withDelay(fail('没有权限', 403))

    const selected = availableDevices.find((item) => item.entityId === data.entityId)
    if (!selected) return withDelay(fail('设备不存在', 404))

    const locationId = Number(data.locationId || 1)
    const newDevice = {
      id: Date.now(),
      deviceid: selected.entityId,
      devicename: selected.attributes?.friendly_name || selected.entityId,
      sensortype: selected.entityId.includes('door') ? SENSOR_TYPE_DOOR : SENSOR_TYPE_TEMP_HUMIDITY,
      source: buildRoomName(locationId),
      status: 'enabled',
      timestamp: new Date().toISOString(),
      datareportinterval: Number(data.reportInterval || 300),
      batterylevel: 100,
      locationid: locationId
    }

    enabledDevices = [...enabledDevices, newDevice]
    availableDevices = availableDevices.filter((item) => item.entityId !== data.entityId)
    return withDelay(success(newDevice, { msg: '设备添加成功' }))
  }

  if (method === 'delete' && /^\/device\/\d+$/.test(url)) {
    const user = getCurrentUser()
    if (!isAdmin(user)) return withDelay(fail('没有权限', 403))
    const id = Number(url.split('/').pop())
    enabledDevices = enabledDevices.filter((item) => item.id !== id)
    return withDelay(success(true, { msg: '设备删除成功' }))
  }

  if (method === 'get' && /^\/device\/\d+$/.test(url)) {
    const id = Number(url.split('/').pop())
    const device = filterByAccess(enabledDevices, (item) => item.locationid).find((item) => item.id === id)
    return withDelay(device ? success(device) : fail('设备不存在', 404))
  }

  if (method === 'get' && url === '/mock/windows/status') {
    const rows = Object.entries(computerSnapshotMap).map(([deviceId, snapshot]) => ({
      deviceId: Number(deviceId),
      ...snapshot
    }))
    return withDelay(success(rows))
  }

  return withDelay(fail(`未实现的 mock 接口: ${method.toUpperCase()} ${url}`, 404))
}
