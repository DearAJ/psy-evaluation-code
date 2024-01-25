<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style lang='scss'>
  @import "./styles/global-element-temp.scss";
  @import "./styles/mixin.scss";

  #app {
    @include relative;
    min-width: 860px;
    min-height: 600px;
  }
</style>