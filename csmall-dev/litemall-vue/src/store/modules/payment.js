
import { Dialog } from 'vant'

const state = {
  id: 0,
  money: 0,
  paying: false
}

const mutations = {
  id(state, id) {
    state.id = id
  },
  money(state, money) {
    state.money = money
  },
  paying(state, paying) {
    state.paying = paying
  }
}
const actions = {
  show({ commit }, { id, money }) {
    commit('paying', true)
    commit('id', id)
    commit('money', money)
  },
  closeNow({ commit }) {
    commit('paying', false)
    commit('id', 0)
    commit('money', 0)
  },
  close({ commit, state }, page) {
    if (!state.paying) {
      return
    }
    Dialog.confirm({
      title: '确定放弃付款吗?',
      confirmButtonText: '继续付款',
      cancelButtonText: '放弃'
    })
      .then(() => {
        // on confirm
      })
      .catch(() => {
        commit('paying', false)
        commit('id', 0)
        commit('money', 0)
        if (page && page.$route.name === 'orderConfirm') {
          page.$router.replace({ name: 'order', params: { active: 1 }})
        }
      })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
