<template>
  <div class="personal-center-container">
    <el-form ref="infoForm" :model="infoForm" label-width="80px" 
      :rules="rules"
      class="form-block">
      <el-form-item label="用户名" prop="username">
        <el-input class="input" v-model="infoForm.username" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-input class="input" v-model="infoForm.role" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="省" prop="province" v-if="level <= 6 && level >= 1">
        <el-input class="input" v-model="infoForm.province" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="市" prop="city" v-if="level <= 5 && level >= 1">
        <el-input class="input" v-model="infoForm.city" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="区" prop="county" v-if="level <= 4 && level >= 1">
        <el-input class="input" v-model="infoForm.county" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="单位" prop="department" v-if="level >= 1">
        <el-input class="input" v-model="infoForm.department" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input class="input" v-model="infoForm.name" :disabled="!editState"></el-input>
      </el-form-item>
      <el-form-item label="手机" prop="phone" v-if="level >= 1">
        <el-input class="input" v-model="infoForm.phone" :disabled="!editState || level < 3"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="infoForm.sex" :disabled="!editState">
          <el-radio label="男">男</el-radio>
          <el-radio label="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <template v-if="level >= 1 && level <= 3">
        <el-form-item label="专兼职" prop="fullPart">
          <el-radio-group v-model="infoForm.fullPart" :disabled="!editState">
            <el-radio :label="0">专职心理老师</el-radio>
            <el-radio :label="1">兼职心理老师</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学历" prop="education">
          <el-select v-model="infoForm.education" placeholder="请选择学历" :disabled="!editState">
            <el-option v-for="item in educationOptions" :key="item" 
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input class="input" v-model="infoForm.major" :disabled="!editState"></el-input>
        </el-form-item>
        <el-form-item label="毕业院校" prop="graduatedSchool">
          <el-input class="input" v-model="infoForm.graduatedSchool" :disabled="!editState"></el-input>
        </el-form-item>
        <el-form-item label="证书" prop="certificate">
          <el-select v-model="infoForm.certificate" placeholder="请选择证书" :disabled="!editState">
            <el-option v-for="item in certificateOptions" :key="item" 
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
      </template>
      <el-form-item label="头像" prop="avatar">
        <el-upload v-if="editState" :loading="loading"
          class="avatar-uploader"
          action=""
          :show-file-list="false"
          :auto-upload="false"
          :on-change="beforeAvatarUpload">
          <img v-if="infoForm.avatar" :src="infoForm.avatar" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
        <img v-else :src="infoForm.avatar" class="avatar">
      </el-form-item>
      <el-form-item class="form-btn">
        <template v-if="!editState">
          <el-button type="primary" @click="edit">编辑</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="submit('infoForm')">提交个人信息</el-button>
          <el-button @click="cancel('infoForm')">取消</el-button>
        </template>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { updateInfo, uploadAvatar } from '@/api/user.js'
export default {
  name: 'PersonalCenter',
  computed: {
    ...mapGetters([
      'userName',
      'name',
      'extra',
      'sex',
      'avatar',
      'level', 
      'role',
      'unit',
      'province',
      'city',
      'county'
    ])
  },
  data() {
    let phone = (rule, value, callback) => {
      let re = /^1(3[0-9]|4[5,7]|5[0,1,2,3,5,6,7,8,9]|6[2,5,6,7]|7[0,1,7,8]|8[0-9]|9[1,8,9])\d{8}$/;
      if (re.test(value)) {
        callback();
      } else {
        callback(new Error('请输入正确的手机号格式'));
      }
    }
    return {
      infoForm: {
        username: '',
        name: '',
        phone: '',
        sex: '',
        avatar: '',
        department: '', //单位
        role: '', //角色
        // 不同权限显示的地区
        province: '',
        city: '',
        county: '',
        // 以下是教师端才有的
        fullPart: '', //专兼职
        education: '', //学历
        major: '', //专业
        graduatedSchool: '', //毕业院校
        certificate: '', // 证书
      },
      editState: false, // 编辑状态
      rules: {
        phone: [{required: true, message: '手机号码为必选', trigger: 'blur'}, 
                { validator: phone, trigger: 'blur'}],
        sex: [{required: true, message: '性别为必选', trigger: 'blur'}],
        name: [{required: true, message: '姓名为必填', trigger: 'blur'}],
        department: [{required: true, message: '单位为必填', trigger: 'blur'}],
        fullPart: [{required: true, message: '专兼职为必填', trigger: 'blur'}],
      },
      loading: false,
      firstLogin: false,
      educationOptions: ['本科', '硕士', '博士', '专科'],
      certificateOptions: ['浙江省中小学生心理健康教育C证', '浙江省中小学生心理健康教育B证', '浙江省中小学生心理健康教育A证', '国家二级心理咨询师', '国家三级心理咨询师', '无'],

    }
  },
  mounted(){
    this.editState = this.$route.query.state ? this.$route.query.state : false;
    this.firstLogin = this.$route.query.firstLogin ? true : true;
    this.infoForm = {
      username: this.userName,
      name: this.name,
      phone: this.extra.phone,
      sex: this.sex,
      avatar: this.avatar,
      role: this.role,
      department: this.unit,
      province: this.province,
      city: this.city,
      county: this.county,
      fullPart: '', //专兼职
      education: '', //学历
      major: '', //专业
      graduatedSchool: '', //毕业院校
      certificate: '', // 证书
    }
    console.log(this.infoForm)
    if(this.level >= 1 && this.level <= 3) {
      this.infoForm.fullPart = this.extra.fullPart;
      this.infoForm.education = this.extra.education;
      this.infoForm.major = this.extra.major;
      this.infoForm.graduatedSchool = this.extra.graduatedSchool;
      this.infoForm.certificate = this.extra.certificate;
    }
  },
  methods: {
    beforeAvatarUpload(file) {
      const formData = new FormData();
      formData.append('file', file.raw);
      this.loading = true;
      uploadAvatar(formData).then(res => {
        this.loading = false;
        this.infoForm.avatar = res.result;
      }).catch(() => {
        this.loading = true;
      })
    }, 
    edit() {
      this.editState = true;
    },
    cancel() {
      this.editState = false;
      this.infoForm = {
        username: this.userName,
        name: this.name,
        phone: this.extra.phone,
        sex: this.sex,
        avatar: this.avatar,
        role: this.role,
        department: this.unit,
        province: this.province,
        city: this.city,
        county: this.county,
        fullPart: '', //专兼职
        education: '', //学历
        major: '', //专业
        graduatedSchool: '', //毕业院校
        certificate: '', // 证书
      }
      if(this.level >= 1 && this.level <= 3) {
        this.infoForm.fullPart = this.extra.fullPart;
        this.infoForm.education = this.extra.education;
        this.infoForm.major = this.extra.major;
        this.infoForm.graduatedSchool = this.extra.graduatedSchool;
        this.infoForm.certificate = this.extra.certificate;
      }
    },
    submit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.editState = false;
          this.infoForm.enabled = 1;
          updateInfo(this.infoForm).then(res => {
            this.$message.success('修改成功');
            this.$store.dispatch('user/getInfo');
            const timeout = setTimeout(() => {
              if(this.firstLogin) {
                this.$router.replace('/');
              }
              clearTimeout(timeout)
            }, 1000);
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
}
</script>

<style lang="scss" scoped> 
  @import "@/styles/mixin.scss";

  .personal-center-container {
    @include container;
    padding: 5px 0;
    .form-block {
      width: 80%;
      max-height: 95%;
      margin: 0 auto;
      padding: 30px 0 10px 0;
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      ::v-deep .el-form-item {
        width: 45%;
      }
      .input {
        width: 220px;
      }
      .form-btn {
        margin-top: 20px;
      }
    }
  }
</style>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>