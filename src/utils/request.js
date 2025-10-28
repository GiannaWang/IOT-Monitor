import axios from 'axios';

const request = axios.create({
  baseURL: 'http://localhost:8080', // API基础URL
  timeout: 5000, // 请求超时时间
});

request.interceptors.request.use(
    (config) => {
        // 在发送请求之前做些什么，比如添加认证token
        // const token = localStorage.getItem('authToken');
        return config;
    },
    (error) => {
        // 处理请求错误
        return Promise.reject(error);
    }
);

// 响应拦截器（可选，统一处理响应格式）
request.interceptors.response.use(
  (response) => {
    return response.data;  // 直接返回响应体数据
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default request;