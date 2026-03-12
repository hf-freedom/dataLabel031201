import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userInfo: {},
    menuList: [],
    permissions: []
  },
  mutations: {
    setUserInfo(state, userInfo) {
      state.userInfo = userInfo
    },
    setMenuList(state, menuList) {
      state.menuList = menuList
    },
    setPermissions(state, permissions) {
      state.permissions = permissions
    },
    clearUserInfo(state) {
      state.userInfo = {}
      state.menuList = []
      state.permissions = []
    }
  },
  actions: {
    async getUserInfo({ commit }, token) {
      try {
        const res = await Vue.prototype.$http.get(`/auth/info?token=${token}`)
        if (res.code === 200) {
          commit('setUserInfo', res.data)
        }
      } catch (e) {}
    },
    async getMenuList({ commit }, token) {
      try {
        const res = await Vue.prototype.$http.get('/resource/userResources')
        if (res.code === 200) {
          commit('setMenuList', res.data)
        }
      } catch (e) {}
    }
  },
  getters: {
    hasPermission: (state) => (permission) => {
      return state.permissions.includes(permission)
    }
  }
})
