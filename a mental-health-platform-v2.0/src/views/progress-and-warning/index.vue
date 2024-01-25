<template>
  <component :is="currentRole"></component>
</template>

<script>
import { mapGetters } from 'vuex'
import countyProgressAndWarning from './county/index.vue'
import cityProgressAndWarning from './city/index.vue'
import provinceProgressAndWarning from './province/index.vue'

export default {
  name: 'ProgressAndWarning',
  components: {
    countyProgressAndWarning,
    cityProgressAndWarning,
    provinceProgressAndWarning
  },
  computed: {
    ...mapGetters([
      'level'
    ]),
    currentRole() {
      switch (this.level) {
        case 6: 
          return 'provinceProgressAndWarning'
        case 5:
          return 'cityProgressAndWarning'
        case 4:
          return 'countyProgressAndWarning'
        default:
          break;
      }
    }
  }
}
</script>