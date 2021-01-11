import { userStatus } from '@/api/user'

const state = {
  alliance: false
}

const mutations = {
  alliance(state, alliance) {
    state.alliance = alliance
  }
}

const actions = {
  updateInfo({ commit }, alliance) {
    commit('alliance', alliance)
  },
  getInfo({ dispatch }) {
    userStatus().then(({ user }) => {
      dispatch('updateInfo', user.alliance)
    }).catch(err => {
      console.log(err)
    })
  }
}

export default {
  state,
  mutations,
  actions
}
