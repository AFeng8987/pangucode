import Vue from 'vue'
import Vuex from 'vuex'
import * as getters from './getters'
import mutations from './mutations'
import cart from './cart'
import cs from './modules/cs'
import payment from './modules/payment'
import logistics from './modules/logistics'
import loading from './modules/loading'
import identity from './modules/identity'
import update from './modules/update'

Vue.use(Vuex)

const state = {
  showHeader: true
}

export default new Vuex.Store({
  strict: process.env.NODE_ENV !== 'production',
  state,
  getters,
  mutations,
  modules: {
    cs,
    payment,
    logistics,
    loading,
    cart,
    update,
    identity
  }
})
