import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

/* Layout */
import Layout from '@/layout/index.vue'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  }, 
  // {
  //   path: '/404',
  //   component: () => import('@/views/error-page/404'),
  //   hidden: true
  // }, 
  // {
  //   path: '/401',
  //   component: () => import('@/views/error-page/401'),
  //   hidden: true
  // }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */

/**
 * 权限
 * 5  省级
 * 4  市级
 * 3  区县级
 * 2  学校
 * 1  学校公开版
 * 0  学生
 */
export const asyncRoutes = [{
  path: '/',
  component: Layout,
  redirect: '/home',
  meta: { roles: [3, 2, 1] },
  children: [{
      path: 'home',
      component: () => import('@/views/home-page/index'),
      name: 'HomePage',
      meta: {
        title: '首页',
        icon: 'el-icon-s-home',
      },
    }
  ]
}, {
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { roles: [5, 4] },
    children: [{
        path: 'home',
        component: () => import('@/views/home-page/index'),
        name: 'HomePage',
        // redirect: '/home/progress',
        meta: {
          title: '首页',
          icon: 'el-icon-s-home',
        },
      }
    ]
  }, {
    path: '/suibian',
    component: Layout,
    redirect: '/home/warn',
    meta: { roles: [5, 4] },
    hidden: true,
    children: [{
      path: '/home/warn',
      component: () => import('@/views/home-page/warn/index'),
      name: 'HomePageWarn',
      meta: {
        title: '预警',
        breadNum: 2,
        activeMenu: '/home'
      },
    }]
  }, {
    path: '/task',
    component: Layout,
    redirect: '/task/index',
    meta: { roles: [2, 1] },
    children: [{
      path: 'index',
      component: () => import('@/views/task/index'),
      name: 'Task',
      meta: {
        title: '我的任务',
        icon: 'el-icon-s-order'
      }
    }]
  }, {
    path: '/organization',
    component: Layout,
    redirect: '/organization/index',
    meta: { roles: [5, 4, 3, 2, 1] },
    children: [{
      path: 'index',
      component: () => import('@/views/organization/index'),
      name: 'Organizations',
      meta: {
        title: '组织管理',
        icon: 'el-icon-menu'
      }
    }]
  }, {
    path: '/issue-tasks',
    component: Layout,
    redirect: '/issue-tasks/index',
    meta: { roles: [5, 4, 3] },
    children: [{
      path: 'index',
      component: () => import('@/views/issue-tasks/index'),
      name: 'IssueTasks',
      meta: {
        title: '任务下发',
        icon: 'el-icon-s-claim',
      }
    }]
  },{
    path: '/monitor-tasks',
    component: Layout,
    redirect: '/monitor-tasks/index',
    meta: { roles: [5, 4, 3] },
    children: [{
      path: 'index',
      component: () => import('@/views/monitor-tasks/index'),
      name: 'MonitorTasks',
      meta: {
        title: '任务监控',
        icon: 'el-icon-video-camera'
      }
    }]
  }, {
    path: '/monitor-tasks/progress-and-warning',
    component: Layout,
    redirect: '/monitor-tasks/progress-and-warning/index',
    meta: { roles: [5, 4, 3] },
    hidden: true,
    children: [{
      path: 'index',
      component: () => import('@/views/progress-and-warning/index'),
      name: 'ProgressAndWarning',
      meta: {
        title: '进度和预警',
        breadNum: 2,
        activeMenu: '/monitor-tasks/index'
      }
    }]
  }, {
    path: '/questionnaire',
    component: Layout,
    redirect: '/questionnaire/index',
    meta: { roles: [5, 4, 3] },
    children: [{
      path: 'index',
      component: () => import('@/views/questionnaire/index'),
      name: 'Questionnaire',
      meta: {
        title: '量表/问卷',
        icon: 'el-icon-notebook-2'
      }
    }]
  }, {
    path: '/psychological-detection',
    component: Layout,
    redirect: '/psychological-detection/index',
    meta: { roles: [2, 1] },
    children: [{
      path: 'index',
      component: () => import('@/views/psychological-detection/index'),
      name: 'PsychologicalDetection',
      meta: {
        title: '心理检测',
        icon: 'el-icon-postcard'
      }
    }]
  }, {
    path: '/report-manage',
    component: Layout,
    redirect: '/report-manage/index',
    meta: { roles: [5, 4, 3] },
    children: [{
      path: 'index',
      component: () => import('@/views/report-manage/index'),
      name: 'ReportManage',
      meta: {
        title: '报告管理',
        icon: 'el-icon-s-order'
      }
    }]
  },{
    path: '/psychological-hotline',
    component: Layout,
    redirect: '/psychological-hotline/index',
    meta: { roles: [5, 4, 3] },
    children: [{
      path: 'index',
      component: () => import('@/views/psychological-hotline/index'),
      name: 'PsychologicalHotline',
      meta: {
        title: '心理热线',
        icon: 'el-icon-phone'
      }
    }]
  },{
    path: '/psychological-counseling',
    component: Layout,
    redirect: '/psychological-counseling/index',
    meta: { roles: [5, 4, 3, 2, 1] },
    children: [{
      path: 'index',
      component: () => import('@/views/psychological-counseling/index'),
      name: 'PsychologicalCounseling',
      meta: {
        title: '心理咨询',
        icon: 'el-icon-sunny'
      }
    }]
  }, {
    path: '/crisis-warning',
    component: Layout,
    redirect: '/crisis-warning/index',
    meta: { roles: [5, 4, 3, 2, 1] },
    children: [{
      path: 'index',
      component: () => import('@/views/crisis-warning/index'),
      name: 'CrisisWarning',
      meta: {
        title: '危机预警',
        icon: 'el-icon-warning'
      }
    }]
  }, {
    path: '/notice',
    component: Layout,
    redirect: '/notice/index',
    meta: { roles: [5, 4, 3, 2, 1] },
    children: [{
      path: 'index',
      component: () => import('@/views/notice/index'),
      name: 'Notice',
      meta: {
        title: '通知公告',
        icon: 'el-icon-message-solid'
      }
    }]
  }, {
    path: '/data-statistics',
    component: Layout,
    redirect: '/data-statistics/index',
    meta: { roles: [5, 4] },
    children: [{
      path: 'index',
      component: () => import('@/views/data-statistics/index'),
      name: 'Datatatistics',
      meta: {
        title: '数据统计',
        icon: 'el-icon-s-data'
      }
    }]
  }, {
    path: '/personal-center',
    component: Layout,
    redirect: '/personal-center/index',
    meta: { roles: [5, 4, 3, 2, 1] },
    hidden: true,
    children: [{
        path: 'index',
        component: () => import('@/views/personal-center/index'),
        name: 'PersonalCenter',
        meta: {
          title: '个人中心'
        }
      }]
  }, {
    path: '/modify-password',
    component: Layout,
    redirect: '/modify-password/index',
    meta: { roles: [5, 4, 3, 2, 1] },
    hidden: true,
    children: [{
        path: 'index',
        component: () => import('@/views/modify-password/index'),
        name: 'ModifyPassword',
        meta: {
          title: '修改密码'
        }
      }]
  },{
    path: '/online-service',
    component: Layout,
    redirect: '/online-service/index',
    meta: { roles: [5, 4, 3, 2, 1] },
    hidden: true,
    children: [{
        path: 'index',
        component: () => import('@/views/online-service/index'),
        name: 'OnlineService',
        meta: {
          title: '在线服务'
        }
      }]
  }, {
    path: '/',
    component: Layout,
    redirect: '/s-my-task',
    meta: { roles: [0] },
    children: [{
        path: 's-my-task',
        component: () => import('@/views/student-my-tasks/index'),
        name: 'StudentMyTask',
        meta: {
          title: '我的任务',
          icon: 'el-icon-s-order'
        }
      }]
  }, {
    path: '/s-my-task/psychological-test',
    component: Layout,
    redirect: '/s-my-task/psychological-test/index',
    meta: { roles: [0] },
    hidden: true,
    children: [{
        path: 'index',
        component: () => import('@/views/student-my-tasks/psychological-test/index'),
        name: 'PsychologicalTest',
        meta: {
          title: '测评',
          breadNum: 2,
          activeMenu: '/s-my-task/index'
        }
      }]
  }, {
    path: '/task/perform-task',
    component: Layout,
    redirect: '/task/perform-task/index',
    meta: { roles: [2] },
    hidden: true,
    children: [{
        path: 'index',
        component: () => import('@/views/task/perform-task/index'),
        name: 'PerformTask',
        meta: {
          title: '任务执行',
          breadNum: 2,
          activeMenu: '/task/index'
          
        }
      }]
  }, {
    path: '/task/completion-degree',
    component: Layout,
    redirect: '/task/completion-degree/index',
    meta: { roles: [2] },
    hidden: true,
    children: [{
        path: 'index',
        component: () => import('@/views/task/completion-degree/index'),
        name: 'CompletionDegree',
        meta: {
          title: '任务完成度',
          breadNum: 2,
          activeMenu: '/task/index'
        }
      }]
  }]

const createRouter = () => new VueRouter({
  mode: "history",
  scrollBehavior: () => ({ y: 0}),
  routes: constantRoutes
});

const router = createRouter()

const originalPush = VueRouter.prototype.push
  VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

