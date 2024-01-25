<template>
  <el-dialog :title="title" :visible.sync="dialogVisible"
    width="60%"
    :close-on-click-modal="true"
    @close="clearForm('intervenePlanForm')">
    <div class="basic-info">
      <el-table :data="basicInfo" border>
        <el-table-column prop="period" label="学段" align="center"></el-table-column>
        <el-table-column prop="grade" label="年级" align="center"></el-table-column>
        <el-table-column prop="classes" label="班级" align="center"></el-table-column>
        <el-table-column prop="name" label="姓名" align="center"></el-table-column>
        <el-table-column prop="time" label="时间" align="center"></el-table-column>
        <el-table-column prop="psyTeacher" label="心理老师" align="center"></el-table-column>
      </el-table>
    </div>
    <div class="form-block">
      <el-form :model="form" label-width="110px" ref="intervenePlanForm" 
        :rules="formRules">
        <el-form-item label="主要心理症状" prop="symptom">
          <el-input v-model="form.symptom"></el-input>
        </el-form-item>
        <el-form-item label="心理评估结论" prop="conclusion">
          <el-input v-model="form.conclusion"
            type="textarea"
            :rows="4">
          </el-input>
        </el-form-item>
        <el-form-item label="干预方案" prop="intervationPlanContent">
          <el-input v-model="form.intervationPlanContent"
            type="textarea"
            :rows="4">
          </el-input>
        </el-form-item>
        <el-form-item class="dialog-confirm-btn-group"  >
          <el-button type="primary" size="medium" @click="submitForm('intervenePlanForm', 'true')">提交</el-button>
          <el-button size="medium" @click="submitForm('intervenePlanForm', 'false')">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-dialog>
</template>

<script>
import { addPlanData, getPlanDetailById, updateIntervationPlanById } from '@/api/psyInterveneTask'
import { mapGetters } from 'vuex'
export default {
  name: 'IntervenePlan',
  data() {
    return {
      dialogVisible: false,
      form: {
        symptom: '', // 心理症状
        conclusion: '', // 评估结论
        intervationPlanContent: ''  // 干预方案
      },
      formRules: {
        symptom: [{ required: true, message: '请输入主要心理症状', trigger: 'blur' }],
        conclusion: [{ required: true, message: '请输入心理评估结论', trigger: 'blur' }],
        intervationPlanContent: [{ required: true, message: '请输入干预方案', trigger: 'blur' }]
      },
      basicInfo: [],
      title: '干预方案',
      studentId: '',
      fileId: '',
      planId: '',  // 暂时写死，需要上一个页面传递
      type: '',   // 暂时写死，需要上一个页面传递，type是update
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ]),
  }, 
  methods: {
    openDialog(data, fileId, stuId, type, planId) {
      this.fileId = fileId;
      this.studentId = stuId;
      this.dialogVisible = true;
      this.basicInfo = data;
      this.type = type;
      this.planId = planId;
      if (this.type == 'update') {
        this.getPlanData();
      }
    },
    closeDialog() {
      this.dialogVisible = false;
      this.$emit('closeDialog')
    },
    submitForm(formName, submitState) {
      // 提交表单
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 调用接口，提交危机等级
          const data = JSON.parse(JSON.stringify(this.form));
          data.isSubmit = submitState;
          data.fileId = this.fileId + '';
          data.userId = this.userId + '';
          data.studentId = this.studentId + '';
          if (this.type == 'update') {
            data.id = this.planId;
            updateIntervationPlanById(data).then(res => {
              if (res.success && res.result > 0) {
                this.$message.success(res.message);
                this.dialogVisible = false;
                this.$emit('submit');
              } else {
                this.$message.error(res.message);
              }
            })
          } else {
            addPlanData(data).then(res => {
              if (res.success && res.result > 0) {
                this.$message.success(res.message);
                // 添加或保存成功
                this.dialogVisible = false;
                this.$emit('submit');
              } else {
                this.$message.error(res.message);
              }
            }).catch(err => {
              console.log('添加干预方案', err)
            })
          }
        } else {
          return false;
        }
      });
    },
    clearForm(formName) {
      // 清除表单数据
      this.$refs[formName].resetFields();
      this.form = {
        symptom: '', // 心理症状
        conclusion: '', // 评估结论
        intervationPlanContent: ''  // 干预方案
      }
    },
    getPlanData() {
      const data = {
        planId: this.planId + ''
      }
      getPlanDetailById(data).then(res => {
        if (res.success && res.result != null) {
          this.form = {
            symptom: res.result.symptom, // 心理症状
            conclusion: res.result.conclusion, // 评估结论
            intervationPlanContent: res.result.intervationPlanContent  // 干预方案
          }
        } else {
          this.$message.error(res.message);
        }
      }).catch()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
.form-block {
  margin: 30px 0;
  .keywords-area {
    display: flex;
    flex-wrap: wrap;
    .content {
      width: 120px;
      height: 28px;
      margin-top: 10px;
      cursor: pointer;
      p {
        width: 95%;
        height: 100%;
        line-height: 28px;
        text-align: center;
        background: #F3F0F8;
        border-radius: 14px;
        font-size: 13px;
        font-family: Microsoft YaHei;
        font-weight: 400;
        color: $themeColor;
      }
    }
  }
  .dialog-confirm-btn-group {
    ::v-deep .el-button {
      border-radius: 18px;
    }
  }
}
</style>