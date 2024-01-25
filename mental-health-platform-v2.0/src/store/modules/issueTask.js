const state = {
  taskInfo: {
    id: 0
  }
}

const mutations = {
  SET_TASKINFO: (state, taskInfo) => {
    state.taskInfo = taskInfo;
  }
}

export default {
  namespaced: true,
  state, 
  mutations
}