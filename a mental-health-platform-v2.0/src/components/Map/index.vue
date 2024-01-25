<template>
  <div :id="id" class="map"></div>
</template>

<script>
import { getMap } from '@/api/map.js'
import * as echarts from 'echarts'

export default {
  name: 'MyMap',
  props: {
    // 地图名字（拼音）
    mapName: {
      type: String,
      required: true
    },
    // 地图是否支持高亮（默认支持）
    highlight: {
      type: Boolean,
      default: true
    },
    id: {
      type: String,
      required: true
    },
    borderColor: {
      type: String,
      default: '#CA9FFF'
    },
    // 选中地图，默认为数组：渐变色
    selectedAreaColor: {
      type: String,
      default: "#854ED8"
    },
    // 文字颜色
    labelColor: {
      type: String,
      default: '#CA9FFF'
    },
    mapData: {
      type: Array,
      required: true,
    }
  }, 
  mounted() {
    this.getMap();
  },
  watch: {
    mapData(o, n) {
      this.getMap();
    }
  },
  data() {
    return {
      childNameList: [],
      color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'],
    }
  },
  methods: {
    async getMap() {
      const chartDom = document.getElementById(this.id);
      const myChart = echarts.init(chartDom);
      let geoJson = await getMap(this.mapName);
      let option;
      this.parseGeoData(geoJson);
      myChart.hideLoading();
      echarts.registerMap('mymap', geoJson);
      let that = this;
      myChart.setOption(
        (option = {
          tooltip: {
            show: that.highlight,
            trigger: 'item',
            formatter: '{b}<br/>{c}'
          },
          dataRange: {
            show:false,
            splitList: that.childNameList
          },
          color: that.color,
          series: [
            {
              type: 'map',
              map: 'mymap',
              label: {
                show: true,
                fontSize: 12,
                color: that.labelColor,
                formatter: '{b}\n{c}'
              },
              data: that.childNameList,
              emphasis: {
                disabled: !that.highlight,
                itemStyle: {
                  areaColor: that.selectedAreaColor,
                  color: 'fff'
                },
              },
              itemStyle: {
                borderColor: that.borderColor,
                borderWidth: 1.2
              },
              select: {
                itemStyle: {
                  color: that.selectedAreaColor,
                },
                label: {
                  color: '#fff'
                }
              }
            }
          ],
        })
      );
      // 鼠标点击事件
      myChart.off('click');//先移除
      myChart.on('click', function(params) {
        that.$emit('mapClick', params.data.name)
      })
      window.addEventListener("resize", () => {
        setTimeout(() => {
          myChart.resize();
        }, 200);
      })
    },
    parseGeoData(data) {
      this.childNameList = data.features.map((val, index) => {
        let v = this.mapData.filter(v => v.name == val.properties.name);
        return {
          name: val.properties.name,
          value: v.length > 0 ? v[0].percent : 0,
          color: '#3C1B6F'
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.map {
  width: 100%;
  height: 100%;
}
</style>