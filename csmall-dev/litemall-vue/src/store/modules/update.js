
const state = {
  current: '',
  version: '',
  needUpdate: false,
  force: false,
  downloaded: false
}

const mutations = {
  current(state, current) {
    state.current = current
  },
  version(state, version) {
    state.version = version
  },
  needUpdate(state, need) {
    state.needUpdate = need
  },
  force(state, force) {
    state.force = force
  },
  downloaded(state, dled) {
    state.downloaded = dled
  }
}

const newVersion = (version, current) => {
  const va = version.split('.')
  const ca = current.split('.')
  const van = parseInt(va.reduce((curr, next) => curr + next.padStart(3, '0'), ''))
  const can = parseInt(ca.reduce((curr, next) => curr + next.padStart(3, '0'), ''))
  return van > can
}

/**
 * 是否强制升级
 * @param {string} version 版本号,最后版本号为92则会强制更新
 */
const isForce = (version) => {
  return /92$/.test(version)
}

const actions = {
  async getCurrentVersion({ state, commit }) {
    const currentVersion = await cordova.getAppVersion.getVersionNumber()
    commit('current', currentVersion)
  },
  async check({ state, commit, dispatch }) {
    if (!state.current) {
      dispatch('getCurrentVersion')
    }
    // eslint-disable-next-line no-undef
    if (device.platform === 'Android') {
      commit('needUpdate', false)
      const url = process.env.VUE_APP_EXTERNAL_SERVER
      const manifest = await cordova.plugins.apkupdater.check(url + 'update/manifest.json')
      const needUpdate = newVersion(manifest.version, state.current)

      commit('force', isForce(manifest.version))
      commit('version', manifest.version)
      commit('needUpdate', needUpdate)
    }
  },
  async download({ commit, dispatch }) {
    await cordova.plugins.apkupdater.download()
    commit('downloaded', true)
    dispatch('install')
  },
  needUpdate({ commit }, need) {
    commit('needUpdate', need)
  },
  async install({ commit }) {
    if (state.downloaded) {
      await cordova.plugins.apkupdater.install()
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
