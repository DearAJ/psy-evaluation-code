import { login, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'
import { Message } from 'element-ui'
// import { clearConfigCache } from 'prettier'

const state = {
  token: getToken(),
  userName: '',
  userId: '',
  name: '',
  sex: '',
  role: '',
  level: '',
  avatar: '', //头像
  province: '', 
  city: '',
  county: '',
  extra: '',
  enabled: '', //是否为首次登录，false为首次
  unit: '',
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USERNAME: (state, userName) => {
    state.userName = userName
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_SEX: (state, sex) => {
    state.sex = sex
  },
  SET_ROLE: (state, role) => {
    state.role = role
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_LEVEL: (state, level) => {
    state.level = level;
  },
  SET_PROVINCE: (state, province) => {
    state.province = province;
  },
  SET_CITY: (state, city) => {
    state.city = city;
  },
  SET_COUNTY: (state, county) => {
    state.county = county;
  },
  SET_EXTRA: (state, extra) => {
    state.extra = extra;
  },
  SET_USERID: (state, userId) => {
    state.userId = userId;
  },
  SET_ENABLED: (state, enabled) => {
    state.enabled = enabled;
  },
  SET_UNIT: (state, unit) => {
    state.unit = unit;
  },
}

const actions = {
  // 登录
  // 在此处进行登录可以直接将登录信息存储到vuex中
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username, password }).then(response => {
        if(response.result) {
          // Message.success('登录成功');
          const token = response.result;
          commit('SET_TOKEN', token);
          setToken(token);
          resolve();
        }else {
          reject("用户名或密码错误")
          Message.error(response.message); 
        }
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取用户信息
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        if(response) {
          if (!response.result) {
            reject('身份信息过期, 请重新登录')
          }
          console.log(response.result)
          const { role, username, level, avatar, province, city, county, name, sex, extra, userId, enabled, unit } = response.result;
          commit('SET_USERNAME', username)
          commit('SET_AVATAR', avatar)
          commit('SET_PROVINCE', province)
          commit('SET_CITY', city)
          commit('SET_COUNTY', county)
          commit('SET_ROLE', role)
          commit('SET_LEVEL', level)
          // commit('SET_LEVEL', 6)
          commit('SET_NAME', name)
          commit('SET_SEX', sex)
          commit('SET_EXTRA', extra)
          commit('SET_USERID', userId)
          commit('SET_ENABLED', enabled)
          commit('SET_UNIT', unit)
          // response.result.level = 6;
          resolve(response.result)
        }
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit }) {
    return new Promise(async resolve => {
      await removeToken()
      commit('SET_TOKEN', '')
      commit('SET_ROLE', '')
      commit('SET_LEVEL', '')
      // 重置路由表
      resetRouter()
      resolve()
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_ROLE', '')
      commit('SET_LEVEL', '')
      removeToken()
      resolve()
    })
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
