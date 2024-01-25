<template>
  <div class="app-wrapper">
    <sidebar></sidebar>
    <div class="right">
      <main-header></main-header>
      <navbar></navbar>
      <app-main></app-main>
    </div>
  </div>
</template>

<script>
import AppMain from './components/AppMain.vue'
import Navbar from './components/Navbar.vue'
import Sidebar from './components/Sidebar.vue'
import MainHeader from './components/MainHeader.vue'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    Sidebar,
    MainHeader
  },
}
</script>

<style lang="scss" scoped>
  @import "@/styles/mixin.scss";
  @import "@/styles/variables.scss";
  .app-wrapper {
     width: 100%;
     height: 100vh;
     display: flex;
     
    .right {
      width: calc(100% - #{$sidebarWidth});
      flex: 1 1 auto;
      position: relative;
      height: 100vh;
      overflow: hidden;
    }
  }
</style>
