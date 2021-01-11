
const state = {
  loading: false,
  req: []
}

const mutations = {
  req(state, req) {
    state.req = req
    state.loading = !!req.length
  }
}
const actions = {
  add({ state, commit }, req) {
    commit('req', [...state.req, req])
  },
  remove({ state, commit }, req) {
    const reqs = [...state.req]
    reqs.splice(reqs.indexOf(req), 1)
    commit('req', reqs)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
