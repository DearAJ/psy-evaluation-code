<template>
  <div class="organization-container">
    <component :is="currentRole"></component>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import countyOrganization from './county/index.vue'
import cityOrganization from './city/index.vue'
import provinceOrganization from './province/index.vue'

export default {
  name: 'Organization',
  components: {
    countyOrganization,
    cityOrganization,
    provinceOrganization
  },
  computed: {
    ...mapGetters([
      'level'
    ]),
    currentRole() {
      // 学校端的管理直接在路由表中了，不需要在这区分
      switch (this.level) {
        case 6:
          return 'provinceOrganization'
        case 5:
          return 'cityOrganization'
        case 4:
          return 'countyOrganization'
        default:
          break;
      }
    }
  },
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
.organization-container {
  @include container;
}
</style>