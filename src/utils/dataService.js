import request from './request'

const dataService = {
  async getAllSensorData() {
    try {
      const response = await request.get('/getAllSensorData')
      return response.data || []
    } catch (error) {
      console.error('Failed to fetch sensor data:', error)
      return []
    }
  },

  async getSensorDataByType(sensorType) {
    try {
      const response = await request.get('/getSensorDataByType', {
        params: { selectedDataType: sensorType }
      })
      return response.data || []
    } catch (error) {
      console.error(`Failed to fetch ${sensorType} data:`, error)
      return []
    }
  },

  async getDeviceCount() {
    try {
      const response = await request.get('/countAllDevices')
      return response.data ?? 0
    } catch (error) {
      console.error('Failed to fetch device count:', error)
      return 0
    }
  },

  async getOnlineDeviceCount() {
    try {
      const response = await request.get('/countOnlineDevices')
      return response.data ?? 0
    } catch (error) {
      console.error('Failed to fetch online device count:', error)
      return 0
    }
  },

  async get10SensorDataByType(sensorType) {
    try {
      const response = await request.get('/get10SensorDataByType', {
        params: { selectedDataType: sensorType }
      })
      return response.data || []
    } catch (error) {
      console.error(`Failed to fetch latest 10 ${sensorType} records:`, error)
      return []
    }
  },

  async getAllLocations() {
    try {
      const response = await request.get('/location/all')
      return response.data || []
    } catch (error) {
      console.error('Failed to fetch locations:', error)
      return []
    }
  },

  async getLocationById(id) {
    try {
      const response = await request.get(`/location/${id}`)
      return response.data || null
    } catch (error) {
      console.error('Failed to fetch location detail:', error)
      return null
    }
  },

  async createLocation(location) {
    try {
      const response = await request.post('/location', location)
      return response.code === 200 ? response.data || null : null
    } catch (error) {
      console.error('Failed to create location:', error)
      return null
    }
  },

  async deleteLocation(id) {
    try {
      const response = await request.delete(`/location/${id}`)
      return response.code === 200
        ? { success: true, msg: response.msg || 'success' }
        : { success: false, msg: response.msg || 'Failed to delete location' }
    } catch (error) {
      const msg = error?.response?.data?.msg || 'Failed to delete location'
      console.error('Failed to delete location:', error)
      return { success: false, msg }
    }
  },

  async getSensorDataWithFilters(params) {
    try {
      const response = await request.get('/getSensorDataWithFilters', { params })
      return response.data || []
    } catch (error) {
      console.error('Failed to fetch filtered sensor data:', error)
      return []
    }
  }
}

export default dataService
