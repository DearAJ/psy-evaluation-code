<template>
  <el-dialog :title="title" :visible.sync="dialogVisible"
    width="60%"
    :close-on-click-modal="true"
    :before-close="closeDialog">
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
      <el-form :model="form" label-width="100px" ref="crisisLevelForm" 
        :rules="formRules">
        <el-form-item label="干预方式" prop="intervationWay">
          <el-select v-model="form.intervationWay" placeholder="请选择干预方式" clearable="">
            <el-option v-for="item in methodOptions" :key="item" 
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="记录" prop="intervationRecord">
          <el-input v-model="form.intervationRecord"
            type="textarea"
            :rows="4">
          </el-input>
          <div class="keywords-area">
            <div class="content" v-for="item in keywordsOptions" :key="item"
              @click="addKeywords(item)">
              <p>{{item}}</p>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="危机等级" prop="crisisLevel">
          <el-radio-group v-model="form.crisisLevel">
            <el-radio v-for="item in crisisLevelOptions" :key="item"
              :label="item">
              {{item}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="当前状态" prop="stuState">
          <el-select v-model="form.stuState" placeholder="请选择当前状态" clearable="">
            <el-option v-for="item in curStatusOptions" :key="item" 
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否转介" prop="isTransfer">
          <el-radio-group v-model="form.isTransfer">
            <el-radio label="true">是</el-radio>
            <el-radio label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="去向/处理" prop="postProcessing">
          <el-select v-model="form.postProcessing" placeholder="请选择去向" clearable="">
            <el-option v-for="item in postProcessingOptions" :key="item" 
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="dialog-confirm-btn-group"  >
          <el-button type="primary" size="medium" @click="submitForm('crisisLevelForm', 'true')">提交</el-button>
          <el-button size="medium" @click="submitForm('crisisLevelForm', 'false')">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-dialog>
</template>

<script>
import { getRcordDetailById, addRecordData, updateRecordOrCrisisById } from '@/api/psyInterveneTask.js'
import { mapGetters } from 'vuex'

// import dateFormat from '@/utils/dateFormat'
import dateFormat from "dateformat"
export default {
  name: 'ConfirmCrisisLevel',
  data() {
    return {
      dialogVisible: false,
      form: {
        intervationWay: '', // 干预方式
        intervationRecord: '', // 记录
        crisisLevel: '', // 危机等级
        stuState: '', // 当前状态
        isTransfer: '', // 是否转介
        postProcessing: '', // 去向/处理
      },
      methodOptions: ['面询', '团辅', '回访', '其他'],
      crisisLevelOptions: ['一级预警（一般心理危机）', '二级预警（严重心理危机）', '三级预警（重大心理危机）', '无危机'],
      curStatusOptions: ['在校', '请假在家', '休学'],
      postProcessingOptions: ['住院治疗', '服药', '外部心理机构心理咨询（治疗）', '在校咨询', '其他'],
      keywordsOptions: ['#抑郁症干预', '#学习焦虑干预', '#情绪疏通', '#人际关系', '#自伤干预', '#自杀干预', '#意外死亡干预', '#家庭变故干预'],
      formRules: {
        intervationWay: [{ required: true, message: '请选择干预方式', trigger: 'change' }],
        intervationRecord: [{ required: true, message: '请输入记录', trigger: 'blur' }],
        crisisLevel: [{ required: true, message: '请选择危机等级', trigger: 'change' }],
        stuState: [{ required: true, message: '请输入学生状态', trigger: 'change' }],
        isTransfer: [{ required: true, message: '请输入是否转介', trigger: 'change' }],
        postProcessing: [{ required: true, message: '请选择去向/处理', trigger: 'change' }],
      },
      basicInfo: [],
      title: '',
      type: 'crisis',
      fileId: '',
      // wk新加
      keyWordsList: [],
      updateType: '', // 如果是update，就自动获取记录数据，否则不获取
      recordId: '',
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ]),
  },
  methods: {
    openDialog(data, type, fileId, updateType, recordId) {
      this.dialogVisible = true;
      this.type = type;
      this.title = type === 'crisis' ? "确定危机等级" : "添加干预记录";
      this.basicInfo = data;
      this.fileId = fileId;
      this.updateType = updateType;
      this.recordId = recordId;
      if (this.updateType == 'update') {
        this.getRecordData(this.recordId);
      }
    },
    closeDialog() {
      this.dialogVisible = false;
      this.$emit('closeDialog')
      this.clearForm('crisisLevelForm');
    },
    submitForm(formName, submitState) {
      // 提交表单
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 调用接口，提交危机等级
          let record = JSON.parse(JSON.stringify(this.form));
          record.isSubmit = submitState;
          // const curDate = new Date();
          record.intervationTime = dateFormat(new Date(), 'yyyy-mm-dd hh:MM:ss')
          // `${curDate.getFullYear()}-${curDate.getMonth() + 1}-${curDate.getDay()} ${curDate.getHours()}:${curDate.getMinutes()}:${curDate.getSeconds()}`
          record.recordType = this.type == 'crisis' ? '危机确认' : '干预记录';
          record.fileId = this.fileId + '';
          record.userId = this.userId + '';
          const data = {
            recordDO: record,
            hotWords: this.keyWordsList
          }
          if (this.updateType == 'update') {
            // 更新保存的记录
            data.recordDO.id = this.recordId + '';
            updateRecordOrCrisisById(data).then(res => {
              if(res.success) {
                this.$message.success('操作成功')
                this.dialogVisible = false;
                this.$emit('submit');
              }else {
                this.$message.error(res.message)
              }
            }).catch()
          } else {
            // 添加新纪录（保存或提交）
            addRecordData(data).then(res => {
              if(res.success) {
                this.$message.success('操作成功');
                // 关闭这个dialog框，打开下一个
                this.dialogVisible = false;
                this.$emit('submit');
              }else {
                this.$message.error(res.message)
              }
            }).catch(err => {
              console.log(err);
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
        intervationWay: '', // 干预方式
        intervationRecord: '', // 记录
        crisisLevel: '', // 危机等级
        stuState: '', // 当前状态
        isTransfer: '', // 是否转介
        postProcessing: '', // 去向/处理
      }
    },
    addKeywords(str) {
      this.keyWordsList.push(str.slice(1));
      this.form.intervationRecord += (' ' + str + '  ');
    },
    getRecordData(recordId) {
      const data = {
        recordId: recordId
      }
      getRcordDetailById(data).then(res => {
        if (res.success && res.result != null) {
          let isTransfer = res.result.isTransfer ? 'true' : 'false';
          this.form = {
            intervationWay: res.result.intervationWay,
            intervationRecord: res.result.intervationRecord,
            crisisLevel: res.result.crisisLevel,
            stuState: res.result.stuState,
            isTransfer: isTransfer,
            postProcessing: res.result.postProcessing,
          }
        } else {
          this.$message.error(res.message);
        }
      })
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