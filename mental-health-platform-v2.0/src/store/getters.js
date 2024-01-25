const getters = {
  // 用户等基本信息
  token: state => state.user.token, // token
  userName: state => state.user.userName, // 用户名
  userId: state => state.user.userId,
  role: state => state.user.role, // 权限（string)
  level: state => state.user.level, // 权限代码
  name: state => state.user.name, // 姓名
  sex: state => state.user.sex, // 性别
  avatar: state => state.user.avatar, // 用户头像
  province: state => state.user.province, // 省份
  city: state => state.user.city, // 城市
  county: state => state.user.county, // 区县
  unit: state => state.user.unit, // 单位
  enabled: state => state.user.enabled, //是否为首次登录，false为首次
  extra: state => state.user.extra, // 其它信息，如学校端：学校名称；学生端：学校等等
  permission_routes: state => state.permission.routes, // 该权限下的路由表
  sidebarStatus: state => state.app.sidebarStatus, // 侧边栏的折叠状态，默认展开
  breadList : state => state.breadcrumb.breadList, // 面包屑
  taskInfo: state => state.issueTask.taskInfo
}

export default getters