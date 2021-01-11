import Vue from 'vue'
import App from './App.vue'
import router from './router'
// import 'vant/lib/index.css'
// import 'vant/lib/icon/local.css'
import '@/assets/scss/global.scss'
import '@/assets/scss/iconfont/iconfont.css'

import VueCountdown from '@chenfengyuan/vue-countdown'
import md5 from 'js-md5'

import store from './store'

import filters from '@/filter'

// import Vconsole from 'vconsole'

import 'lib-flexible/flexible'

import Video from 'video.js'
import 'video.js/dist/video-js.css'

Vue.prototype.$video = Video

// if (process.env.NODE_ENV === 'production') {
//   new Vconsole()
// }
Vue.prototype.$md5 = md5
Vue.component(VueCountdown.name, VueCountdown)
Vue.use(filters)

import { Lazyload, Icon, Cell, CellGroup, loading, Button, Toast, Dialog } from 'vant'
Vue.use(Icon)
Vue.use(Cell)
Vue.use(CellGroup)
Vue.use(loading)
Vue.use(Button)
Vue.use(Toast)
Vue.use(Dialog)
Vue.use(Lazyload, {
  preLoad: 1.3,
  error: require('@/assets/images/error.jpg'),
  loading: require('@/assets/images/loading.svg'),
  attempt: 1,
  lazyComponent: true
})

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
