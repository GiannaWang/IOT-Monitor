import axios from 'axios'
import { mockRequest } from '../mock/index.js'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'

const http = axios.create({
  baseURL: 'http://localhost:8080',
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
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('isLoggedIn')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

function normalizeConfig(dataOrConfig, maybeConfig) {
  if (maybeConfig !== undefined) {
    return { ...(maybeConfig || {}), data: dataOrConfig }
  }
  return dataOrConfig || {}
}

const request = {
  async get(url, config = {}) {
    if (useMock) {
      return mockRequest('get', url, config)
    }
    return http.get(url, config)
  },

  async post(url, dataOrConfig = {}, maybeConfig) {
    const config = normalizeConfig(dataOrConfig, maybeConfig)
    if (useMock) {
      return mockRequest('post', url, config)
    }
    return http.post(url, config.data, config)
  },

  async delete(url, config = {}) {
    if (useMock) {
      return mockRequest('delete', url, config)
    }
    return http.delete(url, config)
  }
}

export default request
