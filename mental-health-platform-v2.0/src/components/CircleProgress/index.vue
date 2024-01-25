<template>
  <div class="progress-circle">
    <el-image :src="imgUrl" fit="contain" class="img"></el-image>
    <div class="box">
      <svg width="100%" height="100%">
        <circle cx="50%" cy="50%" :r="radius" 
          class="circle-background"
          :style="`stroke: ${outsideColor}; fill: ${outsideFillColor}`"
          :class="{'bg-transparent': isTransparent}"></circle>
        <circle cx="50%" cy="50%" :r="radius" 
          class="circle"
          :style="`stroke: ${innerColor};`"
          :class="{'bg-transparent': isTransparent}" 
          :ref="id">
        </circle>
      </svg>
    </div>
    <p class="p-inner" :style="`color: ${innerColor}`">{{parseInt(percent * 100)}}%</p>
  </div>
</template>

<script>
export default {
  name: 'CircleProgress',
  props: {
    // width: {
    //   type: String,
    //   default: '100%'
    // },
    // height: {
    //   type: String,
    //   default: '100%'
    // },
    imgUrl: {
      type: String,
      default: require('@/assets/imgs/progress-bg/progress-outside.png')
    },
    radius: {
      type: String,
      required: true
    },
    percent: {
      type: Number,
      default: 0
    },
    id: {
      type: String, 
      required: true,
    },
    isTransparent: {
      type: Boolean,
      default: false
    },
    outsideColor: {
      type: String,
      default: '#E1C9FF'
    },
    outsideFillColor: {
      type: String,
      default: '#EFE2FF'
    },
    innerColor: {
      type: String,
      default: '#D08E00'
    }
  },
  computed: {
    // dashArray() {
    //   console.log(this.$refs[this.id])
    //   let circleLen = this.$refs[this.id].getTotalLength();
    //   return [this.percent * circleLen, circleLen];
    // },
    // dashOffset() {
    //   let circleLen = this.$refs[this.id].getTotalLength();
    //   return 0.25 * circleLen;
    // }
  },
  mounted() {
    let circle = this.$refs[this.id];
    let circleLen = circle.getTotalLength();
    circle.style.strokeDasharray = [this.percent * circleLen, circleLen];
    // circle.style.strokeDashoffset = 0.25 * circleLen;
  },
  watch: {
    percent(o, n) {
      // console.log('watch到的',o, n);
      let circle = this.$refs[this.id];
      let circleLen = circle.getTotalLength();
      // console.log(this.percent)
      circle.style.strokeDasharray = [this.percent * circleLen, circleLen];
      // circle.style.strokeDashoffset = 0.25 * circleLen;
    }
  }
}
</script>

<style lang="scss" scoped>
.progress-circle {
  width: 100%;
  height: 100%;
  .img{
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
  }
  .box {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    .circle {
      fill: none;
      stroke-width: 6px;
      transition: all 2s;
    }
    .circle-background {
      stroke-width: 6px;
    }
    .bg-transparent {
      fill: none;
      fill-opacity: 0;
    }
  }
  .p-inner {
    font-size: 30px;
    font-family: Microsoft YaHei;
    font-weight: 400;
    // color: #D08E00;s
    text-align: center;
    position: absolute;
    width: 100%;
    top: 50%;
    margin-top: -20px;
  }
}
</style>