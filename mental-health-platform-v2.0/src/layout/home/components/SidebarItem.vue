<template>
  <div v-if="!item.hidden" class="sidebar-item">
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'menu-item-title':!isNest}">
          <span class="icon-style" :class="{'icon-open': !sidebarStatus, 'icon-fold': sidebarStatus}">
            <i v-if="!isNest" :class="onlyOneChild.meta.icon"></i>
          </span>
          <span :class="{'menu-item-font':isNest, 'font-item-open': !sidebarStatus, 'font-item-fold': sidebarStatus}">{{onlyOneChild.meta.title}}</span>
        </el-menu-item>
    </template>

    <el-submenu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template slot="title">
        <span class="icon-style" :class="{'icon-open': !sidebarStatus, 'icon-fold': sidebarStatus}">
          <i :class="item.meta && item.meta.icon"></i>
        </span>
        <span class="menu-item-title">{{item.meta.title}}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="resolvePath(child.path)"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-submenu>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'SidebarItem', 
  props: {
    // route object
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    },
  },
  computed: {
    ...mapGetters([
      'sidebarStatus', // 侧边栏是否折叠
    ])
  },
  data() {
    // To fix https://github.com/PanJiaChen/vue-admin-template/issues/237
    // TODO: refactor with render function
    this.onlyOneChild = null
    return {}
  },
  methods: {
    hasOneShowingChild(children = [], parent) {
      const showingChildren = children.filter(item => {
        // console.log(item);
        if (item.hidden) {
          return false
        } else {
          // Temp set(will be used if only has one showing child)
          this.onlyOneChild = item
          return true
        }
      })

      // When there is only one child router, the child router is displayed by default
      if (showingChildren.length === 1) {
        return true
      }

      // Show parent if there are no child router to display
      if (showingChildren.length === 0) {
        this.onlyOneChild = { ... parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    },
    resolvePath(path) {
      return this.basePath === '/' ? this.basePath + path : this.basePath + '/' + path;
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
// $foldWidth: calc((100vh - #{$homeHeaderHeight} - 15px) / 12.8);
.sidebar-item {
  ::v-deep .el-menu-item{
    font-size: 15px;  // 侧边栏内部文字的大小
    padding: 5px !important;
    i {
      color: #fff;
    }
  }
  ::v-deep .el-submenu__title {
    padding: 5px !important;
  }
  ::v-deep .el-submenu {
    i {
      color: #E4C7FD;
    }
    .el-submenu__icon-arrow {
      margin-left: 5px;
      position: relative;
      top: 0;
      right: 0;
      margin-top: 2px;
      font-size: 16px;
    }
  }
  .icon-style {
    i {
      color: #fff;
    }
  }
  .icon-open {
    padding-left: 10px;
    i {
      margin-right: 8px;
      font-size: 18px !important;
    }
  }
  .icon-fold {
    width: 50px;
    display: inline-block;
    text-align: center;
    margin-right: 10px;
    i {
      font-size: 26px !important;
    }
  }
  .menu-item-title {
    font-size: 16px;  // 侧边栏title的文字大小
  }
  .menu-item-font {
    margin-left: 30px;
  }
  .font-item-fold {
    margin-left: 10px;
    color: $themeColor;
  }

}
</style>
