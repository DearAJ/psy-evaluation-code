<template>
  <div class="menu-sidebar" :class="{'open': !sidebarStatus, 'fold': sidebarStatus}">
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <div class="sidebar-header">
        <p v-if="!sidebarStatus">心理数字云平台</p>
        <i :class="sidebarIcon" class="toggle-slider-btn" @click="toggleSidebarStatus"></i>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarStatus"
        :router="true"
        text-color="#ffffff"
        active-text-color="#CF8D00"
        background-color="#1B0036">
        <sidebar-item v-for="route in permission_routes" :key="route.path" 
          :item="route" 
          :basePath="route.path">
        </sidebar-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import SidebarItem from './SidebarItem'
export default {
  name: 'Sidebar',
  components: {
    SidebarItem,
  },
  computed: {
    ...mapGetters([
      'permission_routes', // 路由表
      'sidebarStatus', // 侧边栏是否折叠
      'userName', // 用户名
      'role', // 权限
      'avatar', // 头像
      'level',
      'extra',
      'name',
      'province',
      'city',
      'county'
    ]),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    sidebarIcon() {
      return this.sidebarStatus ? {'el-icon-s-unfold': true, 'toggle-slider-btn-fold': true} : {'el-icon-s-fold': true, 'toggle-slider-btn-unfold': true}
    },
  },
  methods: {
    goto(name) {
      this.$router.push({name: name})
    },
    toggleSidebarStatus() {
      this.$store.dispatch('app/toggleSideBar');
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";

.menu-sidebar {
  height: 100%;
  flex: 0 0 auto;
  z-index: 1001;
  box-shadow: 8px 0 6px 0 rgb(0 0 0 / 4%);
  background-color: $meunBackgroundColor;
  color: white;
  position: relative;
  transition: all 0.2s;
  .sidebar-header {
    height: $headerHeight;
    background-color: $themeColor;
    line-height: $headerHeight;
    text-align: center;
    position: relative;
    p {
      font-size: 24px;
    }
    .toggle-slider-btn {
      color: #E4C7FD;
      text-align: center;
      cursor: pointer;
      position: absolute;
      background: #5F28AF;
    }
    .toggle-slider-btn-unfold {
      font-size: 20px;
      bottom: 0;
      right: 0;
      height: 20px;
      width: 20px;
      line-height: 20px;
    }
    .toggle-slider-btn-fold {
      width: 80px;
      height: 80px;
      line-height: 80px;
      top: 50%;
      margin-top: -40px;
      left: 50%;
      margin-left: -40px;
      font-size: 24px;
    }
  }
  ::v-deep .el-menu-item.is-active {
    background: $activeSidebar !important;
  }
}

.open {
  width: $sidebarWidth;
}

.fold {
  width: $foldSidebarWidth;
}

.avatar-open {
  height: $sidebarWidth;
}

.avatar-fold {
  height: $foldAvatarHeight;
}

.img-open {
  height: 100px;
  width: 100px;
}

.img-fold {
  height: 45px;
  width: 45px;
}
</style>

<style lang="scss">
@import "@/styles/variables.scss";
.menu-sidebar {
  .el-scrollbar {
    height: 100%;
    .el-scrollbar__wrap {
      overflow-x: hidden;
      overflow-y: scroll;
    }
    .el-scrollbar__bar {
      &.is-vertical {
        width: 0;
      }
      &.is-horizontal {
        height: 0;
      }
    }
  }
  .el-menu {
    border-right: none;
    .el-menu-item:hover {
      background: $activeSidebar !important;
    }
    .sidebar-item .el-submenu__title:hover {
      background: $activeSidebar !important;
    }
  }
  .el-menu--collapse {
    width: 100%;
  }
}
</style>