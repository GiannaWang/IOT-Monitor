import axios from 'axios'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
  || `${window.location.protocol}//${window.location.hostname || 'localhost'}:8080`

const http = axios.create({
  baseURL: apiBaseUrl,
  timeout: 5000,
  paramsSerializer: {
    encode: (params) => {
      return Object.keys(params)
        .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
    }
  }
})

http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401 && error.config?.url !== '/login') {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('isLoggedIn')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

const request = {
  async get(url, config = {}) {
    if (useMock) {
      const { mockRequest } = await import('../mock/index.js')
      return mockRequest('get', url, config)
    }
    return http.get(url, config)
  },

  async post(url, data = null, config = {}) {
    const requestConfig = { ...(config || {}), data }
    if (useMock) {
      const { mockRequest } = await import('../mock/index.js')
      return mockRequest('post', url, requestConfig)
    }
    return http.post(url, data, config)
  },

  async delete(url, config = {}) {
    if (useMock) {
      const { mockRequest } = await import('../mock/index.js')
      return mockRequest('delete', url, config)
    }
    return http.delete(url, config)
  }
}

export default request
