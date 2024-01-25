<template>
  <div :id="id" class="bar"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'MyVerticalBar',
  props: { 
    // 横坐标显示的
    label: {
      type: Array,
      required: true
    },
    values: {
      type: Array,
      required: true
    },
    // 与value相对应的title
    title: {
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
      default: '{c}'
    },
    color: {
      type: Array,
      default() {
        return ["#84a6f1", "#77c27c", "#f2b931"]
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
    if(this.label.length > 0) {
      this.showBar();
    }
  },
  watch: {
    label(o, n) {
      this.showBar();
    }
  },
  data() {
    return {
      myChart: {},
      option: {},
      series: []
    }
  },
  methods: {
    // 对数据进行初始化
    initData() {
      this.series = this.values.map((val, index) => {
        return {
          name: this.title[index],
          type: 'bar',
          data: val,
          label: {
            show: true,
            position: 'top',
            color: "#878787"
          },
          itemStyle: {
            color: this.color[index],
            borderRadius: [5, 5, 0, 0] //（顺时针左上，右上，右下，左下）
          },
          barMinHeight: 5
        }
      })
    },
    async showBar() {
      this.initData();
      this.option = {
        grid: {
          left: '3%',
          right: '4%',
          bottom: '25%',
          containLabel: true
        },
        legend: {
          show: true,
          bottom: 5,
          orient: 'horizontal',
          textStyle: {
            fontSize: 12,
          },
        },
        xAxis: {
          type: 'category',
          data: this.label
        },
        yAxis: {
          type: 'value',
          show: false
        },
        series: this.series,
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