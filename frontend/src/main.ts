import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { rippleDirective } from './shared/directives/ripple'
import './styles/main.css'

createApp(App).use(createPinia()).use(router).directive('ripple', rippleDirective).mount('#app')
