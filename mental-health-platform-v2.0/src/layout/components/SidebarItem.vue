<template>
  <div v-if="!item.hidden" class="sidebar-item">
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'menu-item-title':!isNest}">
          <span class="icon-style">
            <i v-if="!isNest" :class="onlyOneChild.meta.icon"></i>
          </span>
          <span class="title-font" :class="{'menu-item-font':isNest}">{{onlyOneChild.meta.title}}</span>
        </el-menu-item>
    </template>

    <el-submenu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template slot="title">
        <span class="icon-style">
          <i :class="item.meta && item.meta.icon"></i>
        </span>
        <span class="menu-item-title title-font">{{item.meta.title}}</span>
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
.sidebar-item {
  ::v-deep .el-menu-item{
    font-size: 16px;  // 侧边栏内部文字的大小
    i {
      color: #fff;
    }
  }
  ::v-deep .el-submenu {
    i {
      color: #fff;
    }
    .el-submenu__icon-arrow {
      margin-left: 15px;
      position: relative;
      top: 0;
      right: 0;
      margin-top: 2px;
      font-size: 16px;
    }
  }
  .icon-style {
    i {
      font-size: 22px !important;
      margin-right: 12px !important;
    }
  }
  .menu-item-title {
    font-size: 18px;  // 侧边栏title的文字大小
  }
  .title-font {
    margin-left: 15px;
  }
  .menu-item-font {
    margin-left: 42px;
  }
}
</style>
