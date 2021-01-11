
import { load } from '@/api/cs'

const state = {
  qq: '',
  wx: '',
  phone: '',
  csPopup: false,
  loaded: false
}

const mutations = {
  csPopup(state, cs) {
    state.csPopup = cs
  },
  qq(state, qq) {
    state.qq = qq
  },
  phone(state, phone) {
    state.phone = phone
  },
  wx(state, wx) {
    state.wx = wx
  },
  loaded(state, loaded) {
    state.loaded = loaded
  }
}
let loading = false
const actions = {
  load({ state, commit }) {
    if (loading) {
      return
    }
    loading = true
    commit('csPopup', true)
    load().then(cs => {
      commit('qq', cs.QQ)
      commit('phone', cs.Phone)
      commit('wx', cs.Wx)
      loading = false
    }).catch(() => {
      commit('csPopup', false)
      loading = false
    })
  },
  show({ state, commit, dispatch }) {
    dispatch('load')
  },
  close({ commit }) {
    commit('csPopup', false)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
