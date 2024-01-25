<template>
  <div class="rounded-pie">
    <el-image :src="imgUrl" fit="contain" class="img"></el-image>
    <div :id="id" class="pie"></div>
    <div class="des">
      <p class="num">{{totalNum}}</p>
      <p class="detail">总人数</p>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { mapGetters } from 'vuex'

export default {
  name: 'RoundPie',
  props: {
    imgUrl: {
      type: String,
      default: require('@/assets/imgs/home-img/warning-outside.png')
    },
    id: {
      type: String,
      required: true
    },
    pieData: {
      type: Array,
      required: true
    },
    title: {
      type: String,
      required: true
    },
    radius: {
      type: Array,
      default() {
        return ['83%', '92%']
      }
    },
    pieColor: {
      type: Array,
      default() {
        return ['#52A4E9', '#CF8D00', '#A10808']
      }
    },
    totalNum: {
      type: String,
      default: '0'
    }
  },
  computed: {
    ...mapGetters([
      'sidebarStatus'
    ])
  },
  mounted() {
    const chartDom = document.getElementById(this.id);
    this.myChart = echarts.init(chartDom);
    window.addEventListener("resize", () => {
      setTimeout(() => {
        this.myChart.resize();
      }, 200);
    })
    if(this.pieData.length > 0) {
      this.showPie();
    } 
  },
  watch: {
    pieData(o, n) {
      this.showPie();
    },
    sidebarStatus(o, n) {
      setTimeout(() => {
        this.myChart.resize();
      }, 200)
    }
  },
  data() {
    return {
      myChart: {},
      option: {},
    }
  },
  methods: {
    async showPie() {
      this.option = {
        tooltip: {
          trigger: 'item'
        },
        series: [
          {
            name: this.title,
            type: 'pie',
            radius: this.radius,
            color: this.pieColor,
            label: {
              color: '#ffffff'
            },
            data: this.pieData
          }
        ]
      }
      this.option && this.myChart.setOption(this.option);
    }
  }
}
</script>

<style lang="scss" scoped>
.rounded-pie {
  position: relative;
  height: 95%;
  width: 100%;
  .img{
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
  }
  .pie {
    width: 100%;
    height: 100%;
  }
  .des {
    position: absolute;
    width: 100%;
    left: 0;
    text-align: center;
    height: 42px;
    top: 50%;
    margin-top: -21px;
    .num {
      color: #CF8D00;
      font-size: 18px;
      font-weight: bold;
    }
    .detail {
      font-size: 14px;
    }
  }
}
</style>