
import { logistics } from '@/api/order'

const state = {
  loading: false,
  loPopup: false,
  info: {},
  list: []
}

const mutations = {
  loPopup(state, loPopup) {
    state.loPopup = loPopup
  },
  info(state, info) {
    state.info = info
  },
  list(state, list) {
    state.list = list
    state.loading = !!list.length
  }
}
let loading = false
const actions = {
  show({ state, commit }, id) {
    if (loading) {
      return
    }
    loading = true
    logistics(id).then(data => {
      // "ebusinessID": "1672544",
      //   "logisticCode": "557020683421114",
      //   "shipperCode": "HTKY",
      //   "shipperName": "百世快递",
      //   "state": "3",
      //   "success": true,
      //   "traces": [
      //       {
      //           "AcceptStation": "【MKT-山东恒伊项目】已揽收",
      //           "AcceptTime": "2020-08-22 19:19:16",
      //           "acceptStation": "【MKT-山东恒伊项目】已揽收",
      //           "acceptTime": "2020-08-22 19:19:16"
      //       },
      console.log('lo: ', data)
      commit('info', {
        id: data.EBusinessID,
        loCode: data.LogisticCode,
        shipperCode: data.ShipperCode,
        shipperName: data.ShipperName,
        reason: '查询不到此物流信息',
        state: data.State,
        success: data.Success
      })
      commit('list', data.Traces.reverse())
      loading = false
    })
    commit('loPopup', true)
  },
  close({ commit }) {
    commit('info', {})
    commit('list', [])
    commit('loPopup', false)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
