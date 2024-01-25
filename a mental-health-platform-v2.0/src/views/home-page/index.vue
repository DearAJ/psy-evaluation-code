<template>
  <component :is="currentRole"></component>
</template>

<script>
import { mapGetters } from 'vuex'
import schoolHomePage from './school/index.vue'
import countyHomePage from './county/index.vue'
import cityHomePage from './city/index.vue'
import provinceHomePage from './province/index.vue'
export default {
  name: 'HomePage',
  components: {
    schoolHomePage,
    countyHomePage,
    cityHomePage,
    provinceHomePage
  },
  mounted() {
    // console.log(this.level)
  },
  computed: {
    ...mapGetters([
      'level'
    ]),
    currentRole() {
      if(this.level === 6) {
        return 'provinceHomePage'
      }else if(this.level === 5) {
        return 'cityHomePage';
      }else if(this.level === 4) {
        return 'countyHomePage'
      }else {
        return 'schoolHomePage'
      }
    }
  },
}
</script>
