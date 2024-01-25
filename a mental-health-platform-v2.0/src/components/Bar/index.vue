<template>
  <div :id="id" class="bar"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'MyBar',
  props: {
    yLabel: {
      type: Array,
      required: true
    },
    yValue: {
      type: Array,
      required: true
    },
    color: {
      type: Array,
      default() {
        return ["#38b2eb", "#44f214"]
      }
    },
    id: {
      type: String,
      required: true,
    },
    formatter: {
      type: String,
      required: true
    },
    xRange: {
      type: Array,
      default() {
        return [0, 100]
      }
    },
    tooltipLabel: {
      type: Array,
      default() {
        return []
      }
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
    if(this.yLabel.length > 0) {
      this.showBar();
    }
  },
  watch: {
    yLabel(o, n) {
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
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            return this.tooltipLabel[params[0].dataIndex];
          }
        },
        grid: {
          left: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          show: false,
          min: this.xRange[0],
          max: this.xRange[1]
        },
        yAxis: {
          type: 'category',
          data: this.yLabel
        },
        series: [
          {
            type: 'bar',
            data: this.yValue,
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 1,
              y2: 0,
              colorStops: [{
                  offset: 0, color: this.color[0] // 0% 处的颜色
              }, {
                  offset: 1, color: this.color[1] // 100% 处的颜色
              }],
              global: false // 缺省为 false
            },
            label: {
              show: true,
              position: 'right',
              valueAnimation: true,
              formatter: this.formatter,
              fontSize: 10,
              offset: [-5, 0]
            },
            barWidth: 10,
            barMinHeight: 3,
            itemStyle: {
              borderRadius: [0, 5, 5, 0] //（顺时针左上，右上，右下，左下）
            },
            showBackground: true,
          }
        ]
      };
      this.option && this.myChart.setOption(this.option);
    }
  }
}
</script>

<style lang="scss" scoped>
.bar {
  width: 100%;
  height: 100%;
}
</style>