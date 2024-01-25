<template>
  <div :id="id" style="width: 67vh; height: 450px"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'lineBar',
  props: {
    id: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: true
    },
    xLabel: {
      type: Array,
      required: true
    },
    yValue: {
      type: Array,
      required: true
    },
    yValue2: {
      type: Array,
      required: true
    },
    total:{
      type:String,
      required:true
    },
    color: {
      type: Array,
      default() {
        return ["#b17ae3"]
      }
    },
    tooltipLabel: {
      type: Array,
      default() {
        return []
      }
    }
  },
  watch:{
    xLabel(o, n) {
      this.showBar();
    }
  },
   mounted() {
    const chartDom = document.getElementById(this.id);
    this.myChart = echarts.init(chartDom);
    this.showBar();
  },
  data() {
    return {
      myChart: {},
      option: {},
    }
  },
  methods: {
    async showBar() {
      //var rangeY = Math.max.apply(null, this.yValue);
      var lcmVal = this.chartlcm(this.yValue, 100)//获取两条y轴的最大公约数
      var Ymaxval_interval = this.YmaxvalAndinterval(this.total, 100, lcmVal, 10);//计算y轴最大值和间隔值
      this.option = {
        title: {
          x:'center',
          text: this.title,
          textStyle:{
            fontSize:16,
            fontWeight:'normal',
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        legend: {
          data: ['预警人数', '预警比例'],
          x: '65%',
          y:'4%',
          itemHeight:5,
          textStyle:{
            fontSize:15,
            fontWeight:'normal',
          }
        },
        xAxis: [
          {
            type: 'category',
            data: this.xLabel,
            axisPointer: {
              type: 'shadow'
            },
            axisLabel: {
              interval: 0, //代表显示所有x轴标签显示
            }
          }
        ],
        grid:{

        },
        yAxis: [
          {
            type: 'value',
            name: '',
            min: 0,//最小值
            max: Ymaxval_interval.max1,//最大值
            interval: Ymaxval_interval.interval1,//每个刻度的间隔值
            splitNumber:10,
            axisLabel: {
              formatter: '{value}'
            },
            splitLine: {
              show: true,
              lineStyle: {
                type: 'dashed',
              }
            }
          },
          {
            type: 'value',
            name: '',
            min: 0,
            max: Ymaxval_interval.max2,
            interval: Ymaxval_interval.interval2,
            splitNumber:10,
            axisLabel: {
              formatter: '{value}%'
            },
            splitLine: {
              show: false,
              lineStyle: {
                type: 'dashed',
              }
            },
          }
        ],
        series: [
          {
            name: '预警人数',
            type: 'bar',
            barWidth: '50%',
            tooltip: {
              valueFormatter: function (value) {
                return value ;
              }
            },
            data: this.yValue,
            itemStyle:{
              normal: {
                color: '#c191ef',
                label:{
                  show:true,
                  position: 'insideBottom',
                  textStyle:{
                    color:'white',
                    fontSize:16
                  }
                }
              }
            }
          },
          {
            name: '预警比例',
            type: 'line',
            yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value + ' %';
              }
            },
            data: this.yValue2,
            itemStyle:{
              normal: {
                color: '#f8b50d',
                label:{
                  show:true,
                  position: 'insideBottom',
                  formatter: '{c}%',
                  textStyle:{
                    fontSize:16
                  }
                }
              }
            }
          }
        ]
      };
      this.option && this.myChart.setOption(this.option);
      window.addEventListener("resize", () => {
        this.myChart.resize();
      });
    },

    chartlcm (a, b) {
      var result = 1;
      for (var i = 1; i <= a && i <= b; i++) {
        if (a % i == 0 && b % i == 0) {
          result = i;
        }
        if (result > 1 && i >= 1)//公约数大于10的时候 直接跳出 避免y轴刻度太多  (如果不介意刻度太多可以把这一段去掉）
          break;
      }
      return result;
    },
    YmaxvalAndinterval(m, n, lcmval, divisor) {
      var interval1 = Math.ceil(m / lcmval);
      var interval2 = Math.ceil(n / lcmval);

      if (lcmval != 1 && lcmval != 0 && lcmval <= 10) {
        return { max1: m, max2: n, interval1: interval1, interval2: interval2 }
      }

      m = Math.ceil(m / divisor) * divisor
      n = Math.ceil(n / divisor) * divisor
      interval1 = Math.ceil(m / divisor );
      interval2 = Math.ceil(n / divisor);

      return { max1: m, max2: n, interval1: interval1, interval2: interval2 }
    }
  }
}
</script>