import router from './router'
import store from './store'
import { Message, Notification } from 'element-ui'
import { getToken } from '@/utils/auth'

router.beforeEach(async(to, from, next) => {
  // 判断用户是否登录
  const hasToken = getToken()
  const whiteList = ['/login', '/auth-redirect', '/groupReport'] // no redirect whitelist
  const todoList = ['PsychologicalDetection', 'CrisisWarning', 'PsychologicalCounseling', 'Questionnaire']
  if (hasToken) {
    if (to.path === '/login') {
      // if is logged in, redirect to the home page
      next({ path: '/' })

    } else {
      // determine whether the user has obtained his permission roles through getInfo
      const hasLevel = store.getters.level
      if (hasLevel || hasLevel === 0) {
        
        // 学生端进入psychological-test时，需保证sessionStorage中有task信息
        if(hasLevel === 0 && to.name == 'PsychologicalTest') {
          if(window.sessionStorage.getItem('selectedTask')) {
            next();
          }else {
            next('/');
          }
        }else if(todoList.includes(to.name)) {
          Message('该功能暂未开发');
          // next(from.path)
          return;
        }else {
          next()
        }

      } else {
        try {
          // get user info
          const { level, enabled } = await store.dispatch('user/getInfo')

          // 根据权限生成路由表
          const accessRoutes = await store.dispatch('permission/generateRoutes', level)
          // 动态添加路由信息
          // router.addRoutes(accessRoutes)  // 新版本已经废弃，动态添加路由使用以下方法
          // https://blog.csdn.net/qq_16221009/article/details/115349038
          for(let i = 0; i < accessRoutes.length; i++) {
            const element = accessRoutes[i];
            router.addRoute(element);
          }
          // hack method to ensure that addRoutes is complete
          // set the replace: true, so the navigation will not leave a history record

          // 判断用户是否是首次登录
          if(enabled) { // 不是首次登录，则直接next
            next({ ...to, replace: true })
          }else { // 是首次登录，则进入个人中心改信息
            next({path: '/personal-center', query: {state: true, firstLogin: true}, replace: true})
            Notification.info({
              message: '您的个人信息未完善，请先完善个人信息',
              position: 'bottom-right',
            });
          }
          
        } catch (error) {
          // 如果获取不到用户信息，则重置token，并重新登录
          await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next('/login')
        }
      }
    }
  } else {
      // has no token
      if (whiteList.indexOf(to.path) !== -1) {
        // in the free login whitelist, go directly
        next()
      } else {
        // other pages that do not have permission to access are redirected to the login page.
        next('/login')
      }
  }
})