const now = new Date()

const users = [
  {
    userId: 1,
    username: 'admin',
    passwordHash: '123456',
    role: '系统管理员',
    avatar: '/src/assets/avatar/fall.bmp',
    lastLoginTime: '2026-03-18 09:20:00',
    room: 'A栋101'
  }
]

const locations = [
  { id: 1, buildingname: 'A栋', floornumber: '1', roomnumber: '101' },
  { id: 2, buildingname: 'A栋', floornumber: '1', roomnumber: '102' },
  { id: 3, buildingname: 'A栋', floornumber: '2', roomnumber: '201' },
  { id: 4, buildingname: 'B栋', floornumber: '3', roomnumber: '301' },
  { id: 5, buildingname: 'B栋', floornumber: '3', roomnumber: '302' },
  { id: 6, buildingname: 'C栋', floornumber: '4', roomnumber: '401' }
]

const windowsHosts = [
  {
    id: 4,
    deviceid: 'windows.pc_lab301',
    devicename: '实验室主机 301',
    sensortype: '计算机',
    source: 'B栋301',
    status: 'online',
    timestamp: '2026-03-18T09:26:00',
    datareportinterval: 60,
    batterylevel: null,
    locationid: 4
  },
  {
    id: 5,
    deviceid: 'windows.pc_dorm302',
    devicename: '宿舍值班机 302',
    sensortype: '计算机',
    source: 'B栋302',
    status: 'online',
    timestamp: '2026-03-18T09:27:00',
    datareportinterval: 60,
    batterylevel: null,
    locationid: 5
  },
  {
    id: 6,
    deviceid: 'windows.pc_admin401',
    devicename: '管理机 401',
    sensortype: '计算机',
    source: 'C栋401',
    status: 'online',
    timestamp: '2026-03-18T09:28:00',
    datareportinterval: 60,
    batterylevel: null,
    locationid: 6
  }
]

let enabledDevices = [
  {
    id: 1,
    deviceid: 'sensor.temp_101',
    devicename: '101 温湿度传感器',
    sensortype: '温湿度',
    source: 'A栋101',
    status: 'enabled',
    timestamp: '2026-03-18T09:25:00',
    datareportinterval: 300,
    batterylevel: 88,
    locationid: 1
  },
  {
    id: 2,
    deviceid: 'sensor.infrared_102',
    devicename: '102 红外传感器',
    sensortype: '红外',
    source: 'A栋102',
    status: 'enabled',
    timestamp: '2026-03-18T09:23:00',
    datareportinterval: 60,
    batterylevel: 76,
    locationid: 2
  },
  {
    id: 3,
    deviceid: 'binary_sensor.door_201',
    devicename: '201 门磁传感器',
    sensortype: '门磁',
    source: 'A栋201',
    status: 'enabled',
    timestamp: '2026-03-18T09:21:00',
    datareportinterval: 30,
    batterylevel: 91,
    locationid: 3
  },
  ...windowsHosts
]

let availableDevices = [
  {
    entityId: 'sensor.temp_202',
    state: '23.4',
    attributes: { friendly_name: '202 温湿度传感器' }
  },
  {
    entityId: 'binary_sensor.door_302',
    state: 'off',
    attributes: { friendly_name: '302 门磁传感器' }
  }
]

let alerts = [
  {
    id: 1,
    timestamp: '2026-03-18 08:12:00',
    roomNumber: 'A101',
    alertType: '温度过高',
    handled: 0
  },
  {
    id: 2,
    timestamp: '2026-03-18 07:48:00',
    roomNumber: 'A102',
    alertType: '设备离线',
    handled: 1
  },
  {
    id: 3,
    timestamp: '2026-03-17 21:15:00',
    roomNumber: 'A201',
    alertType: '门磁异常',
    handled: 0
  },
  {
    id: 4,
    timestamp: '2026-03-17 18:30:00',
    roomNumber: 'B301',
    alertType: 'CPU 使用率过高',
    handled: 0
  },
  {
    id: 5,
    timestamp: '2026-03-17 16:45:00',
    roomNumber: 'B302',
    alertType: '磁盘使用率过高',
    handled: 1
  },
  {
    id: 6,
    timestamp: '2026-03-17 10:10:00',
    roomNumber: 'C401',
    alertType: '内存使用率过高',
    handled: 0
  }
]

const computerSnapshotMap = {
  4: {
    host: 'LAB-301',
    foreground_name: 'Code.exe',
    total: 162,
    background: 161,
    ratio: 0.99,
    cpu: 28,
    memory: 48,
    disk: 67,
    network_up_kbps: 320,
    network_down_kbps: 1580
  },
  5: {
    host: 'DORM-302',
    foreground_name: 'chrome.exe',
    total: 143,
    background: 142,
    ratio: 0.99,
    cpu: 52,
    memory: 71,
    disk: 81,
    network_up_kbps: 180,
    network_down_kbps: 740
  },
  6: {
    host: 'ADMIN-401',
    foreground_name: 'WINWORD.EXE',
    total: 118,
    background: 117,
    ratio: 0.99,
    cpu: 14,
    memory: 39,
    disk: 56,
    network_up_kbps: 90,
    network_down_kbps: 210
  }
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

const metricSeries = [
  ...buildMockSensorSeries({
    type: 'temperature',
    locationId: 1,
    deviceId: 1,
    values: [22.1, 22.6, 22.8, 23.2, 23.5, 23.1, 22.9, 23.0, 23.4, 23.6],
    minutesStep: 60
  }),
  ...buildMockSensorSeries({
    type: 'humidity',
    locationId: 1,
    deviceId: 1,
    values: [46, 47, 45, 44, 46, 48, 47, 49, 50, 48],
    minutesStep: 60
  }),
  ...buildMockSensorSeries({
    type: 'temperature',
    locationId: 2,
    deviceId: 2,
    values: [21.1, 21.3, 21.5, 21.9, 22.0, 22.2, 22.1],
    minutesStep: 180
  }),
  ...buildMockSensorSeries({
    type: 'humidity',
    locationId: 2,
    deviceId: 2,
    values: [43, 44, 45, 44, 42, 43, 44],
    minutesStep: 180
  }),
  ...buildMockSensorSeries({
    type: 'infrared',
    locationId: 2,
    deviceId: 2,
    values: [2, 4, 3, 6, 8, 7, 5, 9, 6, 4],
    minutesStep: 60
  }),
  ...buildMockSensorSeries({
    type: 'door',
    locationId: 3,
    deviceId: 3,
    values: [0, 1, 0, 2, 1, 3, 2, 1, 0, 1],
    minutesStep: 60
  }),
  ...buildComputerMetrics(4, [18, 25, 20, 44, 38, 60, 52, 34, 41, 28], [42, 45, 47, 49, 48, 53, 55, 52, 50, 48], [63, 63, 64, 64, 65, 65, 66, 66, 66, 67], [0.95, 0.96, 0.97, 0.98, 0.98, 0.99, 0.99, 0.98, 0.99, 0.99]),
  ...buildComputerMetrics(5, [32, 36, 40, 55, 61, 68, 72, 66, 58, 52], [58, 60, 63, 65, 67, 70, 72, 73, 71, 69], [75, 76, 76, 77, 78, 79, 80, 81, 81, 81], [0.96, 0.97, 0.97, 0.98, 0.98, 0.99, 0.99, 0.99, 0.99, 0.99]),
  ...buildComputerMetrics(6, [11, 13, 14, 16, 15, 18, 17, 16, 15, 14], [34, 35, 36, 38, 39, 41, 40, 39, 39, 38], [54, 54, 55, 55, 56, 56, 56, 56, 56, 56], [0.97, 0.97, 0.98, 0.98, 0.98, 0.99, 0.99, 0.99, 0.99, 0.99])
]

function buildComputerMetrics(deviceId, cpuValues, memoryValues, diskValues, ratioValues) {
  const device = enabledDevices.find((item) => item.id === deviceId)
  const snapshot = computerSnapshotMap[deviceId]
  if (!device || !snapshot) return []

  return [
    ...buildMockSensorSeries({
      type: 'cpu',
      locationId: device.locationid,
      deviceId,
      values: cpuValues,
      minutesStep: 30,
      extras: { source: device.source, host: snapshot.host }
    }),
    ...buildMockSensorSeries({
      type: 'memory',
      locationId: device.locationid,
      deviceId,
      values: memoryValues,
      minutesStep: 30,
      extras: { source: device.source, host: snapshot.host }
    }),
    ...buildMockSensorSeries({
      type: 'disk',
      locationId: device.locationid,
      deviceId,
      values: diskValues,
      minutesStep: 30,
      extras: { source: device.source, host: snapshot.host }
    }),
    ...buildMockSensorSeries({
      type: 'background_ratio',
      locationId: device.locationid,
      deviceId,
      values: ratioValues,
      minutesStep: 30,
      extras: { source: device.source, host: snapshot.host }
    })
  ]
}

function success(data, extra = {}) {
  return { code: 200, data, msg: 'success', ...extra }
}

function fail(msg = 'mock request failed', code = 500) {
  return { code, msg, data: null }
}

function normalizeType(input) {
  const value = String(input || '').toLowerCase()
  if (value.includes('cpu')) return 'cpu'
  if (value.includes('memory') || value.includes('内存') || value.includes('鍐呭瓨')) return 'memory'
  if (value.includes('disk') || value.includes('磁盘') || value.includes('纾佺洏')) return 'disk'
  if (value.includes('ratio') || value.includes('后台') || value.includes('background')) return 'background_ratio'
  if (value.includes('humidity') || value.includes('湿') || value.includes('婀')) return 'humidity'
  if (value.includes('temperature') || value.includes('温') || value.includes('娓')) return 'temperature'
  if (value.includes('infrared') || value.includes('红外') || value.includes('绾')) return 'infrared'
  if (value.includes('door') || value.includes('门磁') || value.includes('闂')) return 'door'
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

function filterSensorRecords(params = {}) {
  const type = normalizeType(params.selectedDataType || params.sensorType)
  const locationId = params.locationId != null && params.locationId !== '' ? Number(params.locationId) : null
  const deviceId = params.deviceId != null && params.deviceId !== '' ? Number(params.deviceId) : null

  return metricSeries.filter((item) => {
    if (type && item.type !== type) return false
    if (locationId != null && item.locationId !== locationId) return false
    if (deviceId != null && item.deviceId !== deviceId) return false
    return true
  })
}

export async function mockRequest(method, url, config = {}) {
  const params = getParams(config)
  const data = getData(config)

  if (method === 'get' && url === '/getUserByUsername') {
    const user = users.find((item) => item.username === params.username)
    return withDelay(user ? success(user) : fail('用户不存在', 404))
  }

  if (method === 'post' && url === '/login') {
    const user = users.find((item) => item.username === data.username)
    if (!user || user.passwordHash !== data.passwordHash) {
      return withDelay(fail('用户名或密码错误', 401))
    }

    return withDelay(success({
      token: 'mock-jwt-token',
      user: { ...user }
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

  if (method === 'post' && url === '/updateLastLoginTime') {
    const user = users.find((item) => item.userId === Number(params.userId))
    if (user) {
      user.lastLoginTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    return withDelay(success(true))
  }

  if (method === 'get' && url === '/getAllSensorData') {
    return withDelay({ sensorData: metricSeries })
  }

  if (method === 'get' && url === '/getSensorDataByType') {
    return withDelay({ sensorData: filterSensorRecords(params) })
  }

  if (method === 'get' && url === '/countAllDevices') {
    return withDelay(success(enabledDevices.length))
  }

  if (method === 'get' && url === '/countOnlineDevices') {
    return withDelay(success(enabledDevices.filter((item) => item.status === 'enabled' || item.status === 'online').length))
  }

  if (method === 'get' && url === '/get10SensorDataByType') {
    const records = filterSensorRecords(params)
    return withDelay(success(records.slice(-10)))
  }

  if (method === 'get' && url === '/location/all') {
    return withDelay(success(locations))
  }

  if (method === 'get' && /^\/location\/\d+$/.test(url)) {
    const id = Number(url.split('/').pop())
    const location = locations.find((item) => item.id === id)
    return withDelay(location ? success(location) : fail('位置不存在', 404))
  }

  if (method === 'get' && url === '/getSensorDataWithFilters') {
    return withDelay(success(filterSensorRecords(params)))
  }

  if (method === 'get' && url === '/getAllAlerts') {
    return withDelay(success(alerts))
  }

  if (method === 'post' && url === '/markAsHandled') {
    const alert = alerts.find((item) => item.id === Number(params.alertId))
    if (!alert) return withDelay(fail('告警不存在', 404))
    alert.handled = 1
    return withDelay(success(true))
  }

  if (method === 'get' && url === '/getLatest5Alerts') {
    const latest = [...alerts].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp)).slice(0, 5)
    return withDelay(success(latest))
  }

  if (method === 'get' && url === '/getTodayAlertCount') {
    const today = new Date().toISOString().slice(0, 10)
    const count = alerts.filter((item) => item.timestamp.startsWith(today)).length
    return withDelay(success(count))
  }

  if (method === 'get' && url === '/device/enabled') {
    return withDelay(success(enabledDevices))
  }

  if (method === 'get' && url === '/device/available') {
    return withDelay(success(availableDevices))
  }

  if (method === 'post' && url === '/device/enable') {
    const selected = availableDevices.find((item) => item.entityId === data.entityId)
    if (!selected) return withDelay(fail('设备不存在', 404))

    const location = locations.find((item) => item.id === Number(data.locationId))
    const newDevice = {
      id: Date.now(),
      deviceid: selected.entityId,
      devicename: selected.attributes?.friendly_name || selected.entityId,
      sensortype: selected.entityId.includes('door') ? '门磁' : '温湿度',
      source: location ? `${location.buildingname}${location.roomnumber}` : '默认位置',
      status: 'enabled',
      timestamp: new Date().toISOString(),
      datareportinterval: Number(data.reportInterval || 300),
      batterylevel: 100,
      locationid: Number(data.locationId || 1)
    }

    enabledDevices = [...enabledDevices, newDevice]
    availableDevices = availableDevices.filter((item) => item.entityId !== data.entityId)
    return withDelay(success(newDevice, { msg: '设备添加成功' }))
  }

  if (method === 'delete' && /^\/device\/\d+$/.test(url)) {
    const id = Number(url.split('/').pop())
    enabledDevices = enabledDevices.filter((item) => item.id !== id)
    return withDelay(success(true, { msg: '设备删除成功' }))
  }

  if (method === 'get' && /^\/device\/\d+$/.test(url)) {
    const id = Number(url.split('/').pop())
    const device = enabledDevices.find((item) => item.id === id)
    return withDelay(device ? success(device) : fail('设备不存在', 404))
  }

  if (method === 'get' && url === '/mock/windows/status') {
    return withDelay(success(Object.entries(computerSnapshotMap).map(([deviceId, snapshot]) => ({
      deviceId: Number(deviceId),
      ...snapshot
    }))))
  }

  return withDelay(fail(`未实现的 mock 接口: ${method.toUpperCase()} ${url}`, 404))
}
