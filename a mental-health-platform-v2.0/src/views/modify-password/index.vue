<template>
  <div class="modify-pass-container">
    <el-form ref="infoForm" :model="infoForm" label-width="80px" :rules="rules"
      class="form-block"
      label-position="left">
      <el-form-item label="用户名" prop="userName">
        <el-input class="input" v-model="infoForm.userName" :disabled='true'></el-input>
      </el-form-item>
      <el-form-item label="原密码" prop="oldPass">
        <el-input class="input" v-model="infoForm.oldPass" type="password" auto-complete="off" show-password></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPass">
        <el-input class="input" v-model="infoForm.newPass" type="password" auto-complete="off" show-password></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="checkPassWord">
        <el-input class="input" v-model="infoForm.checkPassWord" type="password" auto-complete="off" show-password></el-input>
      </el-form-item>
      <el-form-item class="form-btn">
          <el-button type="primary" @click="submit('infoForm')">确认修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { modifyPassword } from '@/api/user.js'
export default {
  name: 'ModifyPassword',
  computed: {
    ...mapGetters([
      'userName'
    ])
  },
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.infoForm.checkPassWord !== '') {
          this.$refs.infoForm.validateField('checkPassWord');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.infoForm.newPass) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      infoForm: {
        userName: this.userName,
        oldPass: '',
        newPass: '',
        checkPassWord: ''
      },
      rules: {
        oldPass: [
          { required: true, message: '请输入原密码' }
        ],
        newPass: [
          { validator: validatePass, trigger: 'blur' },
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        checkPassWord: [
          { validator: validatePass2, trigger: 'blur' },
          { required: true, message: '请再次输入密码', trigger: 'blur' }
        ]
      },
    }
  },
  mounted(){
    this.infoForm = {
      userName: this.userName,
      oldPass: '',
      newPass: '',
      checkPassWord: ''
    }
  },
  methods: {
    submit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let oldPassWord = this.$encryptByPublicKey(this.infoForm.oldPass);
          let newPassWord = this.$encryptByPublicKey(this.infoForm.newPass);
          let data = {
            username: this.userName,
            oldPassWord: oldPassWord,
            newPassWord: newPassWord
          }
          modifyPassword(data).then(res => {
            this.$message.success('修改成功');
            this.resetForm(formName);
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.infoForm.userName = this.userName;
    }
  }
}
</script>

<style lang="scss" scoped> 
  @import "@/styles/mixin.scss";

  .modify-pass-container {
    @include container;
    height: calc(100% - 40px);
    overflow: hidden;
    .form-block {
      width: 90%;
      max-height: 95%;
      margin: 50px;
      .input {
        width: 220px;
      }
    }
  }
</style>