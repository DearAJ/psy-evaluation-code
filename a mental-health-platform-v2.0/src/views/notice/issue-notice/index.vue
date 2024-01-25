<template>
  <div class="issue-notice-container">
    <el-form :model="form" :rules="rules" label-width="100px" ref="issueNoticeForm">
      <el-form-item label="公告标题：" prop="title">
        <el-input v-model="form.title" class="input">
        </el-input>
      </el-form-item>
      <el-form-item label="公告内容：" prop="content">
        <tinymce :height="200" :toolbar="richTextToolbar" v-model="form.content"></tinymce>
      </el-form-item>
      <el-form-item label="附件：" prop="appendix">
        <el-upload
          :multiple="true"
          :file-list="fileList"
          :show-file-list="true"
          :on-remove="handleRemove"
          :on-success="handleSuccess"
          class="editor-slide-upload"
          :action="getBaseHost() + '/notice/noticeAppendix.upload'"
          :data="additionalData">
          <el-button size="small" type="primary">
            上传文件
          </el-button>
        </el-upload>
      </el-form-item>
      <el-form-item class="issue-btn">
        <el-button type="primary" @click="submitNotice">发布公告</el-button>
      </el-form-item>
    </el-form>
  </div>
</template> 

<script>
import Tinymce from '@/components/Tinymce'
import { mapGetters } from 'vuex'
import { deleteFile, addNotice } from '@/api/notice.js'
import { getBaseHost } from '@/api/config.js'

export default {
  name: 'IssueNotice',
  components: {
    Tinymce
  },
  computed: {
    ...mapGetters([
      'extra',
      'userName',
      'unit',
      'province',
      'city', 
      'county',
      'level'
    ]),
  },
  data() {
    return {
      richTextToolbar: ['bold italic underline strikethrough alignleft aligncenter alignright outdent indent  undo redo hr bullist numlist forecolor'],
      form: {
        title: '',
        content: '',
        appendix: '', // url，字符串，用逗号,隔开
      },
      rules: {
        title:[
          { required: true, message: '标题不能为空', trigger: 'blur' },
        ],
        content: [
          { required: true, message: '内容不能为空', trigger: 'blur'}
        ]
      },
      additionalData: {
        type: 'appendix'
      },
      // 与文件上传有关的
      fileList: [],
      listObj: {},
    }
  },
  methods: {
    // 与附件相关的函数
    checkAllSuccess() {
      return Object.keys(this.listObj).every(item => this.listObj[item].hasSuccess)
    },
    handleSubmit() {
      if (!this.checkAllSuccess()) {
        this.$message('文件还未上传完毕，请稍等')
        return false;
      }
      return true;
    },
    handleSuccess(response, file) {
      const uid = file.uid
      this.listObj[uid] = {
        hasSuccess: true, 
        uid: file.uid,
        url: response.result
      }
    },
    handleRemove(file) {
      const uid = file.uid
      const objKeyArr = Object.keys(this.listObj)
      for (let i = 0, len = objKeyArr.length; i < len; i++) {
        if (this.listObj[objKeyArr[i]].uid === uid) {
          deleteFile({
            url: this.listObj[objKeyArr[i]].url
          })
          delete this.listObj[objKeyArr[i]]
          return
        }
      }
    },
    submitNotice() {
      if(this.handleSubmit()) {
        this.$refs.issueNoticeForm.validate((valid) => {
          if(valid) {
            const arr = [];
            for(let key in this.listObj) {
              arr.push(this.listObj[key].url);
            }
            this.form.appendix = arr.join(',');
            console.log(this.form);
            this.form.id = 1;
            this.form.author = this.userName;
            this.form.issuedUnit = this.unit;
            this.form.province = this.province;
            if(this.level <= 5) {
              this.form.city = this.city;
            }
            if(this.level <= 4) {
              this.form.county = this.county;
            }
            if(this.level <= 3) {
              this.form.school = this.extra.school;
            }
            addNotice(this.form).then(res => {
              if(res.success) {
                this.$message.success('发布成功');
                this.form = {
                  title: '',
                  content: '',
                  appendix: '',
                }
                this.fileList = [];
                this.listObj = {};
                this.$refs.issueNoticeForm.resetFields();
                this.$router.push({ name: 'Notice' })
              }
            }).catch(err => {
              this.$message.error(err)
            })
          }else {
            return false;
          }
        })
      }else {
        return;
      }
    }
  }

}
</script>

<style lang="scss" scoped>
.issue-notice-container {
  margin: 50px 60px;
  .input {
    width: 240px;
  }
  .issue-btn {
    text-align: right;
  }
}
</style>