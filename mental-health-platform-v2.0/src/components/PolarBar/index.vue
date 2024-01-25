<template>
  <div :id="id" class="polar-bar"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'MyPolarBar',
  props: {
    label: {
      type: Array,
      required: true
    },
    value: {
      type: Array,
      required: true
    },
    color: {
      type: Array,
      default() {
        return ["#854ED8", "#FDB315", "#E96D6D", "#77c27c", "#b7db75", "#f2b931", "#f48c64", "#f4655f"]
      }
    },
    id: {
      type: String,
      required: true,
    },
    formatter: {
      type: String,
      default: '{b}-{c}%'
    },
    max: {
      type: Number,
      default: 100
    }
  }, 
  mounted() {
    // 如果一开始传过来的是空值，则先不渲染
    // 暂时只能想到这种办法了，watch这个值是否改变，因为echarts不能响应数据的变化
    // 数据变了需要重新setOption（官网上也是这样举例的）
    const chartDom = document.getElementById(this.id);
    this.myChart = echarts.init(chartDom);
    window.addEventListener("resize", () => {
      setTimeout(() => {
        this.myChart.resize();
      }, 200);
    })
    if(this.label.length > 0) {
      this.showBar();
    }
  },
  watch: {
    value(o, n) {
      this.showBar();
    }
  },
  data() {
    return {
      myChart: {},
      option: {},
    }
  },
  methods: {
    async showBar() {
      this.option = {
        polar: {
          radius: [5 , '70%']
        },
        angleAxis: {
          max: this.max,
          show: false,
          axisLine: {
            show: false, // x轴刻度分割线
          },
          axisLabel: {
            show: false
          },
          splitLine: {
            show: false // 切分线
          },
        },
        radiusAxis: {
          show: false, // 带弧度的x轴轴线
          type: 'category',
          data: this.label
        },
        tooltip: {
          formatter: this.formatter,
        },
        series: [
          {
            type: 'bar',
            data: this.value,
            coordinateSystem: 'polar',
            label: {
              show: true,
              position: 'start',
              valueAnimation: true,
              formatter: this.formatter,
              color: '#fff',
              fontSize: 14
            },
            barWidth: '15px',
            barMinAngle: 1,
            barGap: '10%',
            itemStyle: {
              normal: {
                color: (params) => {
                  return this.color[params.dataIndex % 8]
                },
              },
            },
            // roundCap: true, // 圆角效果
          }
        ],
      };
      this.option && this.myChart.setOption(this.option);
    }
  }
}
</script>

<style lang="scss" scoped>
.polar-bar {
  width: 100%;
  height: 100%;
}
</style>