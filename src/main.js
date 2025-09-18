import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import './style.css'
import './assets/global.css'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import App from './App.vue'
import Dashboard from './components/Dashboard.vue'
import DeviceManager from './components/DeviceManager.vue'
import DataAnalysis from './components/DataAnalysis.vue'
import Alarmcentre from './components/Alarmcentre.vue'
import LogIn from './components/LogIn.vue'  
import Admin from './components/Admin.vue'

// 定义路由
const routes = [
  { path: '/', redirect: '/dashboard' }, // 默认路由重定向到首页
  { path: '/dashboard', component: Dashboard },
  { path: '/device-manager', component: DeviceManager },
  { path: '/data-analysis', component: DataAnalysis },
  { path: '/alarmcentre', component: Alarmcentre },
  { path: '/login', component: LogIn },
  { path: '/admin', component: Admin, meta: { requiresAuth: true } } // 需要登录权限
]

// 创建路由实例并传递 `routes` 配置
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局导航守卫，检查登录状态
router.beforeEach((to, from, next) => {
  // 判断该路由是否需要登录权限
  if (to.meta.requiresAuth) {
    // 检查登录状态
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    
    if (isLoggedIn) {
      next() // 已登录，放行
    } else {
      next({ path: '/login' }) // 未登录，跳转到登录页
    }
  } else {
    next() // 不需要登录的路由直接放行
  }
})

createApp(App)
  .use(ElementPlus)
  .use(router)
  .mount('#app')