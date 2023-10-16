import { createApp } from 'vue'
import router from'./router/index.js'
import App from './App.vue'
import { createPinia } from 'pinia'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import './assets/main.css'

createApp(App).use(router).use(createPinia()).component('QuillEditor', QuillEditor).mount('#app')
