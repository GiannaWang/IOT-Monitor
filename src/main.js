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
import userService from './utils/userService'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', component: LogIn, meta: { guestOnly: true } },
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path: '/device-manager', component: DeviceManager, meta: { requiresAuth: true } },
  { path: '/data-analysis', component: DataAnalysis, meta: { requiresAuth: true } },
  { path: '/alarmcentre', component: Alarmcentre, meta: { requiresAuth: true } },
  { path: '/admin', component: Admin, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const hasToken = userService.isLoggedIn()

  if (to.meta.requiresAuth && !hasToken) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.guestOnly && hasToken) {
    next({ path: '/dashboard' })
    return
  }

  if (hasToken && !userService.getStoredUser()) {
    try {
      await userService.fetchCurrentUser()
    } catch (error) {
      userService.clearAuth()
      next({ path: '/login' })
      return
    }
  }

  next()
})

createApp(App)
  .use(ElementPlus)
  .use(router)
  .mount('#app')
