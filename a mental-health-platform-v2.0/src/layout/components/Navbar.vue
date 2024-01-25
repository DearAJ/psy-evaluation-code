<template>
  <div class="navbar-block">
    <el-breadcrumb class="breadcrumb" separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for="route in breadList" :key="route.path" :to="{ path: route.path }">
        {{route.title}}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'Navbar',
  computed: {
    ...mapGetters([
      'sidebarStatus', // 侧边栏是否折叠，折叠为true，展开为false
      'level',
      'breadList'
    ]),
    sidebarIcon() {
      return this.sidebarStatus ? 'el-icon-s-unfold' : 'el-icon-s-fold'
    },
  },
  mounted() {
    this.getBreadList();
  },
  watch: {
    $route() {
      this.getBreadList();
    }
  },
  methods: {
    getBreadList() {
      let breadNumber = this.$route.meta.breadNum || 1;
      let breadItem = {path: this.$route.path, title: this.$route.meta.title};
      let matched = this.$route.matched.filter(item => item.meta && item.meta.title);
      matched.forEach(val => { val.meta.breadNum = val.meta.breadNum ? val.meta.breadNum : 1 })
      // console.log(matched)
      if(this.breadList.length < breadNumber) {
        this.$store.commit('breadcrumb/breadListAdd', breadItem);
      }else if(matched[0].meta.breadNum === 1) {
        this.$store.commit('breadcrumb/breadListRemove', 0);
        for(let r of matched) {
          this.$store.commit('breadcrumb/breadListAdd', {
            path: r.path,
            title: r.meta.title
          });
        }
      }else {
        this.$store.commit('breadcrumb/breadListRemove', parseInt(breadNumber))
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
.navbar-block {
  // position: absolute;
  // top: $headerHeight;
  z-index: 999;
  position: relative;
  height: $navbarHeight;
  width: 100%;
  // box-shadow: 0 2px 4px 0 rgb(0 0 0 / 6%);
  background-color: $backgroundColor;
  display: flex;
  padding: 0 20px;
  .breadcrumb {
    line-height: $navbarHeight;
    font-size: 16px;
  }
}
</style>

<style lang="scss">
@import "@/styles/variables.scss";
.breadcrumb {
  .el-breadcrumb__inner.is-link, .el-breadcrumb__inner a {
    font-weight: normal;
  }
  .el-breadcrumb__item:last-child .el-breadcrumb__inner, 
  .el-breadcrumb__item:last-child .el-breadcrumb__inner:hover, 
  .el-breadcrumb__item:last-child .el-breadcrumb__inner a, 
  .el-breadcrumb__item:last-child .el-breadcrumb__inner a:hover {
    cursor: pointer;
    color: $themeColor;
  }
}
</style>