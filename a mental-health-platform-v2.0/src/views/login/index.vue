<template>
  <div  class="login-container">
    <div  class="login-box">
      <el-form  ref="loginForm" class="login-form" :model="loginForm">
        <div class="login-header">
          <h2>账号登录</h2>
        </div>
        <el-form-item>
          <el-input  v-model="loginForm.userName" placeholder="请输入账号">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input  v-model="loginForm.password" placeholder="请输入密码" type="password">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input
              type="text"
              v-model="loginForm.identification"
              placeholder="验证码">
            <template slot="append" >
              <div id="v_container" style="align: center"></div>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="login" type="primary" :loading="loading">{{loading ? '登录中' : '登录'}}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {GVerify} from './verifyCode.js'   //验证码文件
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        userName: '',
        password: '',
        identification: '', //验证码
      },
      loading: false
    }
  },
  mounted () {
    this.identification=new GVerify('v_container');
    //GVerify()
  },
  methods: {
    login() {
      this.loading = true;
      let that = this;
      const data = {
        username: this.loginForm.userName,
        password: this.$encryptByPublicKey(this.loginForm.password)
      }

      var vscode=this.loginForm.identification;  //获取输入的验证码
      var codeflag=this.identification.validate(vscode); //验证码校验
      if(vscode==""){
        this.$message.error('请输入验证码');
      }else if(codeflag){  //判断是否验证成功
        this.loading = true;
      this.$store.dispatch('user/login', data).then(() => {
        setTimeout(() => {
          this.loading = false;
          this.$message.success('登录成功')
        }, 700);
        this.$router.push({ path: '/' });
      }).catch(() => {
        that.loading = false;
      })
      }else{
        this.$message.error('验证码错误');
        // this.identification=new GVerify('v_container');
        that.loading = false;
      }
    }
  },
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  .login-box {
    width: 400px;
    height: 500px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 14px;
    position: relative;
    box-shadow: 0.5px 0.5px 5px rgb(94, 94, 94);
    .login-header {
      color: $themeColor;
      margin-bottom: 60px;
      text-align: center;
    }
    .login-form {
      width: 80%;
    }
  }
}
</style>

<style lang="scss">
.login-form {
  .el-button {
    width: 100%;
    border-radius: 20px;
  }
}
</style>
