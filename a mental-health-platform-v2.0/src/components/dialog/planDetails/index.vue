<template>
  <div class="detail">
    <el-dialog :title="title" :visible.sync="dialogVisible"
      :close-on-click-modal="true">
      <div class="form-block">
        <el-form :model="form" label-width="110px" ref="intervenePlanForm">
          <el-form-item label="心理医生: " prop="psyTeacher">
            <p>{{ form.psyTeacher }}</p>
          </el-form-item>
          <el-form-item label="主要心理症状: " prop="symptom">
            <p>{{ form.symptom }}</p>
          </el-form-item>
          <el-form-item label="心理评估结论: " prop="conclusion">
            <p>{{ form.conclusion }}</p>
          </el-form-item>
          <el-form-item label="干预方案: " prop="intervationPlanContent">
            <p>{{ form.intervationPlanContent }}</p>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPlanDetailById } from '@/api/psyInterveneTask'
export default {
  name: 'PlanDetails',
  data() {
    return {
      dialogVisible: false,
      title: '干预方案详情',
      form: {
        symptom: '无',   // 主要心理症状
        intervationPlanContent: '无',  // 干预方案
        conclusion: '无',  // 结论
        // 暂时写死，需要上一个页面传输
        psyTeacher: ''
      },
      psyTeacher: '',
      planId: 16,  // 暂时写死，需要上一个页面传输
    }
  },
  methods: {
    openDialog(planId, psyTeacher) {
      this.planId = planId;
      this.psyTeacher = psyTeacher;
      this.getPlan();
      this.dialogVisible = true;
    },
    closeDialog() {
      this.dialogVisible = false;
      this.$emit('closeDialog');
    },
    getPlan() {
      const data = {
        planId: this.planId
      }
      getPlanDetailById(data).then(res => {
        if (res.success && res.result != null) {
          this.form = {
            symptom: res.result.symptom,
            intervationPlanContent: res.result.intervationPlanContent,
            conclusion: res.result.conclusion,
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