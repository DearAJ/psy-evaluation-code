<template>
  <div class="menu-sidebar" :class="{'open': !sidebarStatus, 'fold': sidebarStatus, 'sidebar-scroll': level >= 5}">
    <el-image v-if="!sidebarStatus" class="bg-img"
      :src="require('@/assets/imgs/home-img/sidebar-open-2.png')">
    </el-image>
    <el-image v-if="sidebarStatus" class="bg-img"
      :src="require('@/assets/imgs/home-img/sidebar-fold.png')">
    </el-image>
    <div class="toggle-icon">
      <el-image slot="reference" class="img" @click="toggleSidebarStatus"
        :src="sidebarIcon">
      </el-image>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarStatus"
        :router="true"
        text-color="#ffffff"
        active-text-color="#CF8D00">
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
  name: 'HomeSidebar',
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
      return this.sidebarStatus ? require('@/assets/imgs/home-img/zhankai-icon.png') : require('@/assets/imgs/home-img/shousuo-icon.png');
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
$sidebarHeight: calc(100vh - #{$homeHeaderHeight} - 15px);
$sidebarHeightScrollbar: calc(120vh - 15px);
.menu-sidebar {
  height: $sidebarHeight;
  padding-left: 25px;
  position: relative;
  transition: all .2s;
  flex: 0 0 auto;
  .toggle-icon {
    position: absolute;
    right: 0;
    top: -32px;
    height: 20px;
    .img {
      height: 100%;
      cursor: pointer;
    }
  }
  ::v-deep .el-menu {
    background-color: rgba($color: #854ED8, $alpha: 0) !important;
  }
}

.sidebar-scroll {
  height: $sidebarHeightScrollbar;
}

.open {
  // width: calc(#{$sidebarHeight} * (164 / 897));
  width: 150px;
  // background: url("~@/assets/imgs/home-img/sidebar-open-2.png") no-repeat 25px 0;
  // background-size: 80% 100%;
}

.fold {
  // width: calc(#{$sidebarHeight} / 12.8);
  // background: url("~@/assets/imgs/home-img/sidebar-fold.png") no-repeat 25px 0;
  // background-size: contain;
  width: 60px;
}
.bg-img {
  width: calc(100% - 25px);
  height: 100%;
  position: absolute;
  top: 0;
  left: 25px;
}
</style>