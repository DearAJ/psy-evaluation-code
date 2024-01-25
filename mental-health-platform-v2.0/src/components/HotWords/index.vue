<template>
  <div :id="id" class="hotwords"></div>
</template>

<script>
import * as echarts from 'echarts'
import "echarts-wordcloud/dist/echarts-wordcloud"

export default {
  name: 'HotWords',
  props: {
    id: {
      type: String,
      required: true,
    },
    words: {
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
    this.showWords();
    window.addEventListener("resize", () => {
      setTimeout(() => {
        this.myChart.resize();
      }, 200);
    })
  },
  watch: {
    words(o, n) {
      this.showWords();
    }
  },
  data() {
    return {
      myChart: {},
      option: {},
    }
  },
  methods: {
    async showWords() {
      this.option = {
        series: [
          {
            type: 'wordCloud',
            shape: 'circle',

            // 词云整个图表放置的位置 和 尺寸大小
            left: 'center',
            top: 'center',
            width: '90%',
            height: '90%',
            right: null,
            bottom: null,

            //词云文本大小范围,  默认为最小12像素，最大60像素。
            sizeRange: [16, 20],

            // 词云文字旋转范围和步长。 文本将通过旋转在[-90，90]范围内随机旋转步骤45
            // 如果都设置为 0 , 则是水平显示
            rotationRange: [0, 0],

            // 词云文本之间的距离, 距离越大，单词之间的间距越大, 单位像素
            gridSize: 12,

            //设置为true可以使单词部分在画布之外绘制, 允许绘制大于画布大小的单词
            drawOutOfBound: false,

            // 文本样式
            textStyle: {
              color: '#DFCAFF',
            },
            data: this.words,
            
          }
        ]
      };
      this.option && this.myChart.setOption(this.option);
    }
  }
}
</script>

<style lang="scss" scoped>
.hotwords {
  width: 100%;
  height: 100%;
}
</style>