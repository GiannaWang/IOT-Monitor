import request from './request.js'

const alertService = {
  async getAllAlerts() {
    try {
      const response = await request.get('/getAllAlerts')
      return response.data || []
    } catch (error) {
      console.error('获取告警数据失败:', error)
      return []
    }
  },

  async markAsHandled(alertId) {
    try {
      const response = await request.post('/markAsHandled', null, {
        params: { alertId }
      })
      return response.code === 200
    } catch (error) {
      console.error('标记告警失败:', error)
      return false
    }
  },

  async getLatest5Alerts() {
    try {
      const response = await request.get('/getLatest5Alerts')
      return response.data || []
    } catch (error) {
      console.error('获取最新告警失败:', error)
      return []
    }
  },

  async getTodayAlertCount() {
    try {
      const response = await request.get('/getTodayAlertCount')
      return response.data ?? 0
    } catch (error) {
      console.error('获取今日告警数量失败:', error)
      return 0
    }
  },

  async getAlertRules() {
    try {
      const response = await request.get('/alert-rules')
      return response.code === 200 ? response.data || [] : []
    } catch (error) {
      console.error('获取告警规则失败:', error)
      return []
    }
  },

  async saveAlertRule(payload) {
    try {
      const response = await request.post('/alert-rules', payload)
      return {
        ok: response.code === 200,
        msg: response.msg || ''
      }
    } catch (error) {
      console.error('保存告警规则失败:', error)
      return {
        ok: false,
        msg: '保存告警规则失败'
      }
    }
  },

  async deleteAlertRule(locationId, sensorType) {
    try {
      const response = await request.delete('/alert-rules', {
        params: { locationId, sensorType }
      })
      return {
        ok: response.code === 200,
        msg: response.msg || ''
      }
    } catch (error) {
      console.error('删除告警规则失败:', error)
      return {
        ok: false,
        msg: '删除告警规则失败'
      }
    }
  }
}

export default alertService
