<template>
  <div id="main" style="width: 400px; height: 400px">
  </div>
</template>
<script>
import * as echarts from 'echarts'
import { mapGetters } from 'vuex'
export default {
  props: {
    pieData: {
      type: '',
      default: 0,
    },
  },
  watch: {
    number() {
      this.init();
    },
    pieData() {
      this.init();
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      // 基于准备好的dom，初始化echarts实例
      var myChart = echarts.init(document.getElementById("main"));
      // 指定图表的配置项和数据
      var option = {
        tooltip: {
          show: false
        },
        legend: {
          show: false
        },
        graphic: [{　　　　　　　　　　　　　　　　//环形图中间添加文字
          type: 'text',　　　　　　　　　　　　//通过不同top值可以设置上下显示
          left: 'center',
          top: '45%',
          style: {
            text: this.pieData+'%',
            textAlign: 'center',
            fill: '#000000',　　　　　　　　//文字的颜色
            width: 10,
            height: 10,
            fontSize: 40,
            color: "#a7aab0",
            fontFamily: "Microsoft YaHei"
          }
        }],
        series: [
          {
            type: "pie", //设为饼图
            radius: ["30%", "55%",], //可调整大小
            clockWise: true, //默认逆时针
            hoverAnimation: false, //移入放大
            avoidLabelOverlap: false, //避免标注重叠
            label: {
              show:false
            },

            data: [
              {
                value: this.pieData / 1,
                itemStyle: {
                  normal: {
                    borderColor: {
                      colorStops: [
                        {
                          offset: 0,
                          color: "#c8a0ec", // 0% 处的颜色
                        },
                        {
                          offset: 0.5,
                          color: "#c191ef", // 0% 处的颜色
                        },
                        {
                          offset: 1,
                          color: "#c191ef", // 100% 处的颜色
                        },
                      ],
                    },
                    color: {
                      // 完成的圆环的颜色
                      colorStops: [
                        {
                          offset: 0,
                          color: "#c8a0ec", // 0% 处的颜色
                        },
                        {
                          offset: 0.5,
                          color: "#c191ef", // 0% 处的颜色
                        },
                        {
                          offset: 1,
                          color: "#c191ef", // 100% 处的颜色
                        },
                      ],
                    },
                  },
                },
              },
              {
                value: 100 - this.pieData / 1,
                itemStyle: {
                  color: "#eae9e9",
                  label:{

                  }
                },
              },
            ],
          },
        ],
      };
      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
      window.addEventListener("resize",()=>{
        if(myChart) myChart.resize();
      })
    },
  },
};
</script>
<style>
</style>