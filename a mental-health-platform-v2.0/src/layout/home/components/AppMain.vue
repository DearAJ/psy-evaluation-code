<template>
  <section class="app-main">
    <!-- 从需求上来说，不需要keep-alive，几乎每次进入一个页面都需要刷新（可能会有数据改变） -->
    <router-view :key="key" />
  </section>
</template>

<script>
export default {
  name: 'HomeAppMain',
  computed: {
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
.app-main {
  height: 100%;
  position: relative;
  overflow: hidden;
  width: 100%;
  flex: 1 1 auto;
}
</style>