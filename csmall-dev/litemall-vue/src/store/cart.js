const cart = {
  state: {
    address: {}
  },
  mutations: {
    SET_ADDRESS: (state, address) => {
      state.address = address
    }
  },

  actions: {
    GetAddress({ commit }, data) {
      commit('SET_ADDRESS', data)
    },
    DelAddress({ commit }) {
      commit('SET_ADDRESS', '')
    }
  }

}

export default cart
