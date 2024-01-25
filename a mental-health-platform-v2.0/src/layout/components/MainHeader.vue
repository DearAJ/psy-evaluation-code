<template>
  <div class="main-header">
    <nav class="navbar-menu">
      <div class="icon-menu">
        <div class="icon" v-if="level > 1">
          <el-popover placement="bottom" trigger="hover">
            <span>操作手册</span>
            <el-image slot="reference" class="img"
              :src="require('@/assets/imgs/icon/shuoming.png')">
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
              :src="require('@/assets/imgs/icon/kefu.png')">
            </el-image>
          </el-popover> 
        </div>
        <div class="icon" v-if="level > 1">
          <el-popover placement="bottom" trigger="hover">
            <span>消息通知</span>
            <el-image slot="reference" class="img"
              :src="require('@/assets/imgs/icon/xiaoxi.png')">
            </el-image>
          </el-popover>
        </div>
        <div class="icon">
          <el-tooltip effect="light" content="退出登录" placement="bottom">
            <el-popconfirm title="确认退出？"
              confirm-button-text="退出"
              @confirm="exit">
              <el-image slot="reference" class="img"
                :src="require('@/assets/imgs/icon/tuichu.png')">
              </el-image>
            </el-popconfirm>
          </el-tooltip>
        </div>
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
    </nav>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'MainHeader',
  computed: {
    ...mapGetters([
      'level', 
      'userName',
      'avatar',
      'name',
    ]),
  },
  data() {
    return {

    }
  },
  methods: {
    async exit() {
      await this.$store.dispatch('user/logout');
      await this.$router.replace('/login');
    },
    goto(name) {
      this.$router.push({name: name})
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "@/styles/variables.scss";
  @import "@/styles/mixin.scss";

.main-header {
  position: relative;
  color: #C9BBCF;
  height: $headerHeight;
  @include clearfix;

  a {
    text-decoration: none;
    cursor: pointer;
  }

  .navbar-menu {
    float: right;
    height: $headerHeight;
    width: 450px;
    display: flex;
    justify-content: space-around;
    align-items: center;
    font-size: 18px;

    .icon-menu {
      width: 200px;
      display: flex;
      justify-content: space-around;
      .icon {
        width: 26px;
        height: 26px;
        cursor: pointer;
        .img {
          width: 100%;
          height: 100%;
        }
      }
    }

    .personal-center {
      width: 40%;
      height: 40px;
      line-height: 40px;
      position: relative;
      cursor: pointer;
      display: flex;
      .avatar-image {
        border-radius: 100%;
        width: 40px;
        height: 40px;
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
          color: $themeColor;
        }
        i {
          margin: 0 5px;
        }
      }
    }
  }
}

li {
  font-size: 16px;
}

li:hover {
  color: $themeColor;
  text-decoration: underline;
}

.service {
  text-align: center;
}
</style>