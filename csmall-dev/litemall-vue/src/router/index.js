import Vue from 'vue'
import Router from 'vue-router'
import { getLocalStorage } from '@/utils/local-storage'

import home from './home'
import items from './items'
import user from './user'
import order from './order'
import cart from './cart'
import record from './record'
import login from './login'
// import store from '../store/index'

Vue.use(Router)

const originalPush = Router.prototype.push
Router.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

const RouterModel = new Router({
  routes: [...home, ...items, ...user, ...order, ...record, ...login, ...cart]
})

RouterModel.beforeEach((to, from, next) => {
  const { Authorization } = getLocalStorage(
    'Authorization'
  )
  if (!Authorization) {
    if (to.meta.login) {
      next({ name: 'login', query: { redirect: to.name }})
      return
    }
  }
  // console.log(to, 'meta')
  // 页面顶部菜单拦截
  // const emptyObj = JSON.stringify(to.meta) === '{}'
  // const undefinedObj = typeof (to.meta.showHeader) === 'undefined'
  // if (!emptyObj && !undefinedObj) {
  //   store.commit('CHANGE_HEADER', to.meta)
  // } else {
  //   store.commit('CHANGE_HEADER', { showHeader: true, title: '' })
  // }
  next()
})

export default RouterModel
