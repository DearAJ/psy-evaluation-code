<template>
  <div class="monitor-tasks-container">
    <component :is="currentRole"></component>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import countyMonitorTask from './county/index.vue'
import cityMonitorTask from './city/index.vue'
import provinceMonitorTask from './province/index.vue'

export default {
  name: 'MonitorTasks',
  components: {
    countyMonitorTask,
    cityMonitorTask,
    provinceMonitorTask
  },
  computed: {
    ...mapGetters([
      'level'
    ]),
    currentRole() {
      switch (this.level) {
        case 6:
          return 'provinceMonitorTask'
        case 5:
          return 'cityMonitorTask'
        case 4:
          return 'countyMonitorTask'
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