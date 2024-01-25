import Cookies from 'js-cookie'

const state = {
  // 侧边栏是否折叠，折叠为true，展开为false
  sidebarStatus: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : false
}

const mutations = {
  // 切换侧边栏状态
  TOGGLE_SIDEBAR: state => {
    state.sidebarStatus = !state.sidebarStatus
    if (state.sidebarStatus) {
      Cookies.set('sidebarStatus', 1)
    } else {
      Cookies.set('sidebarStatus', 0)
    }
  },
}

const actions = {
  toggleSideBar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
