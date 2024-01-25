<template>
  <div :id="id" style="width: 400px; height: 400px"></div>
</template>

<script>
import * as echarts from 'echarts'
import { mapGetters } from 'vuex'

export default {
  name: 'Pie',
  props: {
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
      type: String,
      default() {
        return '55%'
      }
    },
    pieColor: {
      type: Array,
      default() {
        return ['#c8a0ec', '#9562ee']
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
              show:true,
              color: '#ffffff',
              normal: {
                position: 'inside',
                formatter: "{b} \n\n {d}%",
                fontSize:22,
                color: '#ffffff',
              }
            },
            labelLine:{
              normal:{
                show:false
              }
            },
            data: this.pieData
          }
        ]
      }
      this.option && this.myChart.setOption(this.option);
      window.addEventListener("resize",()=>{
        if(this.myChart) this.myChart.resize();
      })
    }
  }
}
</script>
