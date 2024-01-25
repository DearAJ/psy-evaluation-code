<template>
  <div class="online-service-container">
    <el-image
      class="image"
      :src="require('@/assets/imgs/erweima.png')"
      fit="fit"></el-image>
    <p>扫码添加客服</p>
    <p>获得专属服务</p>
    <div class="info-block">
      <p>服务电话: 15555555555</p>
      <p>联系地址: 杭州市西湖区x路x号x室</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OnlineService',
  data() {
    return {
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "@/styles/mixin.scss";

  .online-service-container {
    @include container;
    height: calc(100% - 90px);
    position: relative;
    display: flex;
    align-items: center;
    flex-direction: column;
    padding-top: 50px;
    .image {
      width: 180px;
      height: 180px;
    }
    p {
      margin-top: 10px;
      font-size: 14px;
    }
    .info-block {
      position: absolute;
      bottom: 40px;
    }
  }
</style>