<template>
  <div class="home-header-block">
    <div class="page-title">
      <p>心理数字云平台</p>
    </div>
    <div class="icon-group">
      <div class="icon" v-if="level > 1">
        <el-popover placement="bottom" trigger="hover">
          <span>操作手册</span>
          <el-image slot="reference" class="img"
            :src="require('@/assets/imgs/icon/shuoming-icon.png')">
          </el-image>
        </el-popover>
      </div>
      <div class="icon">
        <el-popover placement="bottom" trigger="hover">
          <div class="service">
            <h3>联系客服</h3>
            <el-image style="width: 100px; height: 100px; margin: 15px;"
              :src="require('@/assets/imgs/erweima.png')"
              fit="fit">
            </el-image>
            <p>有问题，请添加客服二维码咨询哦~</p>
          </div>
          <el-image slot="reference" class="img"
            :src="require('@/assets/imgs/icon/kefu-icon.png')">
          </el-image>
        </el-popover> 
      </div>
      <div class="icon" v-if="level > 1">
        <el-popover placement="bottom" trigger="hover">
          <span>消息通知</span>
          <el-image slot="reference" class="img"
            :src="require('@/assets/imgs/icon/xiaoxi-icon.png')">
          </el-image>
        </el-popover>
      </div>
      <div class="icon">
        <el-tooltip effect="light" content="退出登录" placement="bottom">
          <el-popconfirm title="确认退出？"
            confirm-button-text="退出"
            @confirm="exit">
            <el-image slot="reference" class="img"
              :src="require('@/assets/imgs/icon/tuichu-icon.png')">
            </el-image>
          </el-popconfirm>
        </el-tooltip>
      </div>
      <div class="personal-center">
        <el-popover placement="bottom" trigger="hover" >
          <ul>
            <li @click="goto('PersonalCenter')">个人中心</li>
            <li @click="goto('ModifyPassword')">修改密码</li>
          </ul>
          <div class="avatar-image" slot="reference">
            <el-image  class="img-block" :src="avatar" fit="cover"> </el-image>
          </div>
        </el-popover>
        <div class="avatar-font">
          <span>{{name}}</span>
          <i class="el-icon-caret-bottom"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'HomeMainHeader',
  computed: {
    ...mapGetters([
      'level', 
      'userName',
      'avatar',
      'name',
    ])
  },
  methods: {
    async exit() {
      await this.$store.dispatch('user/logout');
      await this.$router.replace('/login');
    },
    goto(name) {
      this.$router.push({name: name})
    }
  },
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
.home-header-block {
  height: $homeHeaderHeight;
  background: url("~@/assets/imgs/home-img/header-bg.png") no-repeat center center;
  background-size: contain;
  position: relative;
  .page-title {
    font-size: 30px;
    font-family: FZCuYuan-M03S, Arial, Verdana, Georgia;
    font-weight: bold;
    color: #FFFFFF;
    text-align: center;
    height: 100%;
    line-height: $homeHeaderHeight;
  }
  .icon-group {
    position: absolute;
    width: 100%;
    height: 40%;
    top: 0;
    left: 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    .icon {
      width: 22px;
      height: 22px;
      cursor: pointer;
      margin: 0 12px;
      .img {
        width: 100%;
        height: 100%;
      }
    }

    .personal-center {
      width: 14%;
      height: 40px;
      line-height: 40px;
      position: relative;
      cursor: pointer;
      display: flex;
      margin-left: 20px;
      align-items: center;
      .avatar-image {
        border-radius: 100%;
        width: 32px;
        height: 32px;
        overflow: hidden;
        position: relative;
        margin-right: 8px;
        .img-block {
          height: 100%;
        }
      }
      .avatar-font {
        height: 40px;
        line-height: 40px;
        span {
          color: white;
        }
        i {
          color: #E4C7FD;
          margin: 0 5px;
        }
      }
    }
  }
}
</style>