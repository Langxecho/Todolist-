

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './assets/global.css'
import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'
import axios from "axios";
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// axios.defaults.baseURL = 'http://10.20.8.135:8080'
axios.defaults.baseURL = 'http://localhost:8080'
const app = createApp(App)
app.use(ElementPlus, {
    locale: zhCn,
})

app.use(createPinia())
app.use(router)

app.mount('#app')
