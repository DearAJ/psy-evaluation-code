<template>
  <div class="reports-manage-container">
    <component :is="currentRole"></component>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import countyReportManage from './county/index.vue'
import cityReportManage from './city/index.vue'

export default {
  name: 'ReportsManage',
  components: {
    countyReportManage,
    cityReportManage
  },
  computed: {
    ...mapGetters([
      'level'
    ]),
    currentRole() {
      console.log(this.level)
      switch (this.level) {
        case 4:
          return 'cityReportManage'
        case 3:
          return 'countyReportManage'
        default:
          break;
      }
    }
  },
}
</script>

<style lang="scss" scoped>
.monitor-tasks-container {
  margin: 20px;
  background-color: white;
  box-shadow: 0 0 4px 0 rgb(0 0 0 / 6%);
  border-radius: 4px;
}
</style>