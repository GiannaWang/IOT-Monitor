import { createApp } from 'vue'
import './style.css'
import './assets/global.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

//createApp(App).mount('#app', ElementPlus)

const app = createApp(App)
app.use(ElementPlus)
app.mount('#app')