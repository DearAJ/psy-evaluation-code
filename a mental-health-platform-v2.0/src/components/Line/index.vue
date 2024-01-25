<template>
  <div :id="id" class="line"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'MyLine',
  props: {
    id: {
      type: String,
      required: true,
    },
    // 维度，折线图每条的label，举例：['学生', '家长', '老师']
    label: {
      type: Array,
      requried: true
    },
    // 横坐标显示
    xAxis: {
      type: Array,
      requried: true
    },
    // 数据，是一个二维数组
    values: {
      type: Array,
      requried: true
    },
    areaStyle: {
      type: Boolean,
      default: false,
    },
    color: {
      type: Array,
      default() {
        return ["#CF8D00", '#52A4E9', '#E96D6D', '#CA9FFF', '#fff']
      }
    },
    legend: {
      type: Boolean,
      default: true
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
    if(this.values.length > 0) {
      this.showLine();
    }
  },
  watch: {
    values(o, n) {
      this.showLine();
    }
  },
  data() {
    return {
      myChart: {},
      option: {},
      series: [],
    }
  },
  methods: {
    // 对数据进行初始化
    initData() {
      this.series = this.values.map((val, index) => {
        return {
          name: this.label[index],
          type: 'line',
          data: val,
          areaStyle: this.areaStyle ? {} : null
        }
      })
    },
    async showLine() {
      this.initData();
      this.option = {
        tooltip: {
          trigger: 'axis'
        },
        color: this.color,
        legend: {
          show: this.legend,
          data: this.label,
          right: 0,
          top: '15%',
          orient: 'horizontal',
          textStyle: {
            fontSize: 12,
            color: '#fff'
          },
          lineStyle: {
            width: 2,
          }
        },
        grid: {
          left: '6%',
          right: '4%',
          bottom: 0,
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: true,
          data: this.xAxis,
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.6)',
              type: 'solid'
            }
          }
        },
        yAxis: {
          type: 'value',
          show: true,
          splitLine: {
            lineStyle: {
              color: 'rgba(188, 144, 243, 0.4)',
              type: 'dashed'
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.6)',
              type: 'dashed'
            }
          }
        },
        series: this.series
      };
      this.option && this.myChart.setOption(this.option, true);
    }
  }
}
</script>

<style lang="scss" scoped>
.line {
  width: 100%;
  height: 100%;
}
</style>