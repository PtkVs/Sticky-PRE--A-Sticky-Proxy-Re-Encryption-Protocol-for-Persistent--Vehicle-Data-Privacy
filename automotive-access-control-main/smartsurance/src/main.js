import Vue from 'vue'
import App from './App.vue'

import axios from 'axios'
import VueAxios from 'vue-axios'

import config from '@/config'

let axiosConfig = axios.create({
  withCredentials: true,
  baseURL: config.dataSource,
  auth: { username: config.username, password: config.password }
})

Vue.use(VueAxios, axiosConfig)

import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

import { LMap, LTileLayer, LMarker } from 'vue2-leaflet'
import 'leaflet/dist/leaflet.css'

Vue.component('l-map', LMap);
Vue.component('l-tile-layer', LTileLayer)
Vue.component('l-marker', LMarker)

import VueSlider from 'vue-slider-component'
import 'vue-slider-component/theme/default.css'

Vue.component('v-slider', VueSlider)

import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
Vue.use(Toast, {});

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')
