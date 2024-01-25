import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

/* Layout */
import Layout from '@/layout/index.vue'
import HomeLayout from '@/layout/home/index.vue'

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
  {
    path: '/groupReport',
    component: () => import('@/views/report-page/school/group'),
    meta: {
      roles: [2, 3]
    },
    name: 'Report',
    hidden: true,
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
 * 6  省级
 * 5  市级
 * 4  区县
 * 3  学校管理员
 * 2  心理老师
 * 1  校长
 * 0  学生
 */
export const asyncRoutes = [{
  path: '/',
  // component: Layout,   // 旧首页入口
  component: HomeLayout, // 新首页入口
  redirect: '/home',
  meta: { roles: [6, 5, 4, 3, 2, 1] },
  children: [{
      path: 'home',
      // component: () => import('@/views/home-page/old-index'),  // 旧首页
      component: () => import('@/views/home-page/index'), // 新首页
      name: 'HomePage',
      meta: {
        title: '首页',
        icon: 'iconfont icon-home_fill_light',
      },
    }
  ]
}, {
    path: '/report',
    component: () => import('@/views/report-page/index'),
    meta: {
      roles: [2, 3, 4, 5, 6]
    },
    name: 'Report',
    hidden: true,
},  {
  path: '/countyGroupReport',
  component: () => import('@/views/report-page/county/group'),
  meta: {
    roles: [4, 5, 6]
  },
  name: 'Report',
  hidden: true,
}, {
    path: '/organization',
    component: Layout,
    redirect: '/organization/index',
    meta: { 
      roles: [6, 5, 4, 3, 2],
      title: '组织管理',
      icon: 'iconfont icon-zuzhiguanli'
    },
    children: [{
      path: 'index',
      component: () => import('@/views/organization/index'),
      name: 'Organizations',
      meta: {
        title: '组织管理',
        icon: 'iconfont icon-zuzhiguanli',
        roles: [6, 5, 4]
      }
    }, {
      path: 'index',
      component: () => import('@/views/organization/school/student-organization/index'),
      name: 'StudentOrganizations',
      meta: {
        title: '学生管理',
        icon: 'iconfont icon-zuzhiguanli',
        breadNum: 2,
        roles: [ 3, 2 ]
      }
    }, {
      path: 't-index',
      component: () => import('@/views/organization/school/teacher-organization/index'),
      name: 'StudentOrganizations',
      meta: {
        title: '教师管理',
        breadNum: 2,
        roles: [ 3 ]
      }
    }, {
      path: 'r-index',
      component: () => import('@/views/organization/school/role-organization/index'),
      name: 'StudentOrganizations',
      meta: {
        title: '角色管理',
        breadNum: 2,
        roles: [ 3 ]
      }
    }]
}, {
  path: '/task-organization',
  component: Layout,
  redirect: '/task-organization/issue-tasks/index',
  meta: { 
    roles: [6, 5, 4, 3, 2],
    title: '任务管理',
    icon: 'iconfont icon-renwuguanli',
  },
  children: [{
      path: 'issue-tasks/index',
      component: () => import('@/views/task-organization/issue-tasks/index'),
      name: 'IssueTasks',
      meta: {
        title: '任务下发',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }, {
      path: 'monitor-tasks/index',
      component: () => import('@/views/task-organization/monitor-tasks/index'),
      name: 'MonitorTasks',
      meta: {
        title: '任务监控',
        breadNum: 2,
        roles: [6, 5, 4]
      },
    }, {
      path: 'tasks/index',
      component: () => import('@/views/task-organization/tasks-list/index'),
      name: 'Task',
      meta: {
        title: '任务列表',
        breadNum: 2,
        roles: [3, 2]
      },
    }, {
      path: 'select-scale/index',
      component: () => import('@/views/task-organization/issue-tasks/select-scale/index'),
      name: 'SelectScale',
      hidden: true,
      meta: {
        title: '选择量表',
        breadNum: 3,
        roles: [6, 5, 4, 3, 2]
      }
    }
  ]
}, {
  path: '/scale-organization',
  component: Layout,
  redirect: '/scale-organization/scale-list/index',
  meta: { 
    roles: [6, 5, 4, 3, 2],
    title: '量表管理',
    icon: 'iconfont icon-liangbiao',
  },
  children: [{
      path: 'scale-list/index',
      component: () => import('@/views/scale-organization/scale-list/index'),
      name: 'ScaleList',
      meta: {
        title: '测评量表',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }, {
      path: 'add-scale/index',
      component: () => import('@/views/scale-organization/add-scale/index'),
      name: 'AddScale',
      meta: {
        title: '新增量表',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }, {
      path: 'add-scale-data/index',
      component: () => import('@/views/scale-organization/add-scale-data/index'),
      name: 'AddScaleData',
      meta: {
        title: '新增量表数据 ',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }
  ]
}, {
  path: '/report-organization',
  component: Layout,
  redirect: '/report-organization/personal-report/index',
  meta: { 
    roles: [6, 5, 4, 3, 2],
    title: '报告管理',
    icon: 'iconfont icon-baogaoguanli',
  },
  children: [{
      path: 'personal-report/index',
      component: () => import('@/views/report-organization/personal-report/index'),
      name: 'PersonalReport',
      meta: {
        title: '个人报告',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }, {
      path: 'group-report/index',
      component: () => import('@/views/report-organization/group-report/index'),
      name: 'GroupReport',
      // hidden: true,
      meta: {
        title: '团体报告',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }/*, {
    path: 'group-report/exportStudent',
    component: () => import('@/views/report-organization/group-report/index'),
    name: 'ExportStudent',
    // hidden: true,
    meta: {
      title: '导出学生',
      breadNum: 2,
      roles: [6, 5, 4, 3, 2]
    },
  }*/]
}, {
  path: '/warning-intervention',
  component: Layout,
  redirect: '/warning-intervention/warning-database/index',
  meta: { 
    roles: [6, 5, 4, 3, 2],
    title: '危机干预',
    icon: 'iconfont icon-jinggao',
  },
  children: [{
      path: 'pre-warning-database/index',
      component: () => import('@/views/warning-intervention/pre-warning-database/index'),
      name: 'PreWarningDatabase',
      meta: {
        title: '初筛库',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }, {
      path: 'warning-database/index',
      component: () => import('@/views/warning-intervention/warning-database/index'),
      name: 'WarningDatabase',
      meta: {
        title: '预警库',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }, {
      path: 'warning-statistics/index',
      component: () => import('@/views/warning-intervention/warning-statistics/index'),
      name: 'WarningStatistics',
      meta: {
        title: '预警统计',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2]
      },
    }]
}, {
  path: '/psychological-files',
  component: Layout,
  redirect: '/psychological-files/student-files/index',
  meta: { 
    roles: [6, 5, 4, 3, 2],
    title: '心理档案',
    icon: 'iconfont icon-xinlidanganguanli',
  },
  children: [{
      path: 'student-files/index',
      component: () => import('@/views/psychological-files/student-files/index'),
      name: 'StudentFiles',
      meta: {
        title: '学生档案',
        breadNum: 2,
        roles: [6, 5, 4, 3, 2],
        icon: 'iconfont icon-xinlidanganguanli',
      },
    }, {
      path: 'teacher-files/index',
      component: () => import('@/views/psychological-files/teacher-files/index'),
      name: 'TeacherFiles',
      meta: {
        title: '教师档案',
        breadNum: 2,
        roles: [6, 5, 4, 3]
      },
    }, {
      path: 'file-details/index',
      component: () => import('@/views/psychological-files/file-details/index'),
      name: 'FileDetails',
      hidden: true,
      meta: {
        title: '档案详情',
        breadNum: 3,
        roles: [6, 5, 4, 3]
      }
    }]
}, {
  path: '/notice',
  component: Layout,
  redirect: '/notice/index',
  meta: { roles: [6, 5, 4, 3, 2] },
  children: [{
    path: 'index',
    component: () => import('@/views/notice/index'),
    name: 'Notice',
    meta: {
      title: '通知公告',
      icon: 'iconfont icon-tongzhigonggao'
    }
  }]
}, {
  path: '/notice/index/issue-notice',
  component: Layout,
  redirect: '/notice/index/issue-notice/index',
  meta: { roles: [6, 5, 4, 3, 2] },
  hidden: true,
  children: [{
    path: 'index',
    component: () => import('@/views/notice/issue-notice/index'),
    name: 'IssueNotice',
    meta: {
      title: '发布公告',
      breadNum: 2
    }
  }]
}, {
  path: '/notice/index/notice-details',
  component: Layout,
  redirect: '/notice/index/notice-details/index',
  meta: { roles: [6, 5, 4, 3, 2] },
  hidden: true,
  children: [{
    path: 'index',
    component: () => import('@/views/notice/notice-details/index'),
    name: 'NoticeDetails',
    meta: {
      title: '通知公告详情',
      breadNum: 2
    }
  }]
}, {
  path: '/personal-center',
  component: Layout,
  redirect: '/personal-center/index',
  meta: { roles: [6, 5, 4, 3, 2, 1, 0] },
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
}, {
  path: '/task-organization/perform-task',
  component: Layout,
  redirect: '/task-organization/perform-task/index',
  meta: { roles: [2, 3] },
  hidden: true,
  children: [{
      path: 'index',
      component: () => import('@/views/task-organization/tasks-list/perform-task/index'),
      name: 'PerformTask',
      meta: {
        title: '任务执行',
        breadNum: 3,
        activeMenu: '/task-organization/tasks/index/'
      }
    }]
}, {
  path: '/task-organization/completion-degree',
  component: Layout,
  redirect: '/task-organization/completion-degree/index',
  meta: { roles: [2, 3] },
  hidden: true,
  children: [{
      path: 'index',
      component: () => import('@/views/task-organization/tasks-list/completion-degree/index'),
      name: 'CompletionDegree',
      meta: {
        title: '任务完成度',
        breadNum: 3,
        activeMenu: '/task-organization/tasks/index/'
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
  path: '/monitor-tasks/progress-and-warning',
  component: Layout,
  redirect: '/monitor-tasks/progress-and-warning/index',
  meta: { roles: [6, 5, 4] },
  hidden: true,
  children: [{
    path: 'index',
    component: () => import('@/views/progress-and-warning/index'),
    name: 'ProgressAndWarning',
    meta: {
      title: '进度和预警',
      breadNum: 3,
      activeMenu: '/monitor-tasks/index'
    }
  }]
},]

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

