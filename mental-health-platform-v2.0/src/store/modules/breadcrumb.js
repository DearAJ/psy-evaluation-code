const state = {
  // 面包屑list
  breadList: window.sessionStorage.getItem('breadList') ? JSON.parse(window.sessionStorage.getItem('breadList')) : []
}

const mutations = {
  breadListAdd(state, obj) {
    state.breadList.push(obj);
    // console.log(state.breadList);
    window.sessionStorage.setItem('breadList', JSON.stringify(state.breadList));
  },
  // 点击面包屑返回某一层级
  breadListRemove(state, num) {
    state.breadList = state.breadList.slice(0, num);
    window.sessionStorage.setItem('breadList', JSON.stringify(state.breadList));
  }
}

export default {
  namespaced: true,
  state,
  mutations,
}
