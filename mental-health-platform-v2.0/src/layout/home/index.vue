<template>
  <div class="app-wrapper">
    <el-scrollbar v-if="level >= 5">
      <main-header></main-header>
      <div class="main-scrollbar">
        <sidebar v-if="level > 1"></sidebar>
        <app-main></app-main>
      </div>
    </el-scrollbar>
    <template v-else>
      <main-header></main-header>
      <div class="main">
        <sidebar v-if="level > 1"></sidebar>
        <app-main></app-main>
      </div>
    </template>
  </div>
</template>

<script>
import AppMain from './components/AppMain.vue'
import Sidebar from './components/Sidebar.vue'
import MainHeader from './components/MainHeader.vue'
import { mapGetters } from 'vuex'
export default {
  name: 'HomeLayout',
  components: {
    AppMain,
    MainHeader,
    Sidebar
  },
  computed: {
    ...mapGetters([
      'level'
    ]),
  },
}
</script>

<style lang="scss" scoped>
  @import "@/styles/mixin.scss";
  @import "@/styles/variables.scss";
  .app-wrapper {
     width: 100%;
     height: 100vh;
     background: url("~@/assets/imgs/home-img/beijing.png");
     ::v-deep .el-scrollbar {
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
     .main-scrollbar {
      height: 120vh;
      display: flex;
     }
    .main {
      height: calc(100% - #{$homeHeaderHeight});
      display: flex;
      // overflow: hidden;
    }
  }
</style>
