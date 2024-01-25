<template>
  <div class="detail">
    <el-dialog :title="title" :visible.sync="dialogVisible"
      width="60%"
      :close-on-click-modal="true">
      <div class="form-block">
        <el-form :model="form" label-width="90px" ref="intervenePlanForm">
          <div class="form-flex">
            <el-form-item label="心理医生: " prop="psyTeacher">
              <p>{{ form.psyTeacher }}</p>
            </el-form-item>
            <el-form-item label="干预方式: " prop="intervationWay">
              <p>{{ form.intervationWay }}</p>
            </el-form-item>
            <el-form-item label="干预时间: " prop="intervationTime">
              <p>{{ form.intervationTime }}</p>
            </el-form-item>
          </div>
          <el-form-item label="记录: " prop="intervationRecord">
            <p>{{ form.intervationRecord }}</p>
          </el-form-item>
          <div class="form-flex">
            <el-form-item label="危机等级: " prop="crisisLevel" class="inline-2">
              <p>{{ form.crisisLevel }}</p>
            </el-form-item> 
            <el-form-item label="当前状态: " prop="stuState" class="inline-2">
              <p>{{ form.stuState }}</p>
            </el-form-item>
          </div>
          <div class="form-flex">
            <el-form-item label="是否转介: " prop="isTransfer" class="inline-2">
              <p>{{ form.isTransfer }}</p>
            </el-form-item>
            <el-form-item label="去向/处理: " prop="postProcessing" class="inline-2">
              <p>{{ form.postProcessing }}</p>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getRcordDetailById } from '@/api/psyInterveneTask'
export default {
  name: 'RecordDetails',
  data() {
    return {
      dialogVisible: false,
      title: '干预记录详情',
      form: {
        intervationWay: '',   // 干预方式
        intervationTime: '',  // 干预时间
        intervationRecord: '',  // 干预记录
        crisisLevel: '',   // 危机等级
        stuState: '',   // 当前状态
        isTransfer: '',   // 是否转介
        postProcessing: '',  // 去向
        psyTeacher: ''
      },
      psyTeacher: '',
      recordId: '',  
    }
  },
  methods: {
    openDialog(id, psyTeacher) {
      this.recordId = id;
      this.psyTeacher = psyTeacher;
      this.getRcord();
      this.dialogVisible = true;
    },
    closeDialog() {
      this.dialogVisible = false;
      this.$emit('closeDialog')
    },
    getRcord() {
      const data = {
        recordId: this.recordId
      }
      getRcordDetailById(data).then(res => {
        if (res.success && res.result != null) {
          let isTransfer = res.result.isTransfer ? '是' : '否';
          this.form = {
            intervationWay: res.result.intervationWay,
            intervationTime: res.result.intervationTime,
            intervationRecord: res.result.intervationRecord,
            crisisLevel: res.result.crisisLevel,
            stuState: res.result.stuState,
            isTransfer: isTransfer,
            postProcessing: res.result.postProcessing,
            psyTeacher: this.psyTeacher
          }
        } else {
          this.$message.error(res.message);
        }
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@/styles/common/record-details.scss";

</style>