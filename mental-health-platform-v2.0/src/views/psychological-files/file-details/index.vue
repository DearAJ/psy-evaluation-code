<template>
  <div class="file-details-container">
    <p class="file-title">{{infoForm.name}}个人档案</p>
    <div class="content">
      <!-- 个人信息 -->
      <div class="module basic-info">
        <p class="module-title">个人信息</p>
        <div class="detail-info">
          <el-form label-width="90px" :model="infoForm" class="form-area">
            <el-form-item label="姓名:">
              <span>{{infoForm.name}}</span>
            </el-form-item>
            <el-form-item label="姓名拼音:">
              <span>{{infoForm.pinyin}}</span>
            </el-form-item>
            <el-form-item label="性别:">
              <span>{{infoForm.sex}}</span>
            </el-form-item>
            <el-form-item label="身份证类型:">
              <span>{{infoForm.idType}}</span>
            </el-form-item>
            <el-form-item label="民族:">
              <span>{{infoForm.nation}}</span>
            </el-form-item>
            <el-form-item label="身份证号:">
              <span>{{infoForm.idcard}}</span>
            </el-form-item>
            <el-form-item label="籍贯:">
              <span>{{infoForm.nativePlace}}</span>
            </el-form-item>
            <el-form-item label="联系方式:">
              <span>{{infoForm.phoneNumber}}</span>
            </el-form-item>
          </el-form>
          <div class="pic-area"></div>
        </div>
      </div>
      <!-- 测评记录 -->
      <div class="module evaluation-records">
        <p class="module-title">测评记录</p>
        <div class="search-container">
          <div class="search-box">
            <span>任务名称：&nbsp;</span>
            <el-input class="input" v-model="taskSearchForm.taskName" clearable></el-input>
          </div>
          <div class="search-box">
            <span>量表名称：&nbsp;</span>
            <el-input class="input" v-model="taskSearchForm.scaleName" clearable></el-input>
          </div>
          <div>
            <el-button type="primary" size="medium" @click="getTaskList">搜索</el-button>
          </div>  
        </div>
        <div class="content-container">
          <el-table :data="reportMoreInfo ? reportList.slice(0, 2) : reportList" :stripe="true" border>
            <el-table-column align="center" label="任务名称" prop="taskName"></el-table-column>
            <el-table-column align="center" label="量表名称" prop="scaleName"></el-table-column>
            <el-table-column align="center" label="生成时间" prop="submitTime"></el-table-column>
            <el-table-column align="center" label="测评报告">
              <template slot-scope="scope">
                <el-button size="mini" @click="gotoPersonalReport(scope.row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <p class="more-info" v-if="reportMoreInfo" @click="openMore('report')">
            查看更多信息
            <i class="el-icon-caret-bottom"></i>
          </p>
          <p class="more-info" v-if="reportList.length > 2 && !reportMoreInfo" @click="closeMore('report')">
            收起
            <i class="el-icon-caret-top"></i>
          </p>
        </div>
      </div>
      <!-- 干预记录 -->
      <div class="module intervention-records" v-if="basicInfo.identity === 'student'">
        <p class="module-title">危机干预</p>
        <div class="content-container">
          <el-table :data="interveneMoreInfo ? interveneList.slice(0, 2) : interveneList" :stripe="true" border>
            <el-table-column align="center" label="模块" prop="moudle"></el-table-column>
            <el-table-column align="center" label="心理老师" prop="psyTeacher"></el-table-column>
            <el-table-column align="center" label="危机等级" prop="crisisLevel"></el-table-column>
            <el-table-column align="center" label="干预方式" prop="methods"></el-table-column>
            <el-table-column align="center" label="时间" prop="date"></el-table-column>
            <el-table-column align="center" label="操作">
              <template slot-scope="scope">
                <el-button size="mini" @click="interveneDetails(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <p class="more-info" v-if="interveneMoreInfo" @click="openMore('intervene')">
            查看更多信息
            <i class="el-icon-caret-bottom"></i>
          </p>
          <p class="more-info" v-if="interveneList.length > 2 && !interveneMoreInfo" @click="closeMore('intervene')">
            收起
            <i class="el-icon-caret-top"></i>
          </p>
        </div>
      </div>
    </div>
    
    <plan-details ref="PlanDetails"></plan-details>
    <record-details ref="RecordDetails"></record-details>
  </div>
</template>

<script>
import { fileDetails } from '@/api/files.js'
import { getAllInterveneDataById, getIntervationContentByStuId } from '@/api/psyInterveneTask.js'
import { getTaskByUserId } from '@/api/task.js'
import { mapGetters  } from 'vuex'
import PlanDetails from '@/components/dialog/planDetails/index.vue'
import RecordDetails from '@/components/dialog/recordDetails/index.vue'
export default {
  name: 'FileDetails',
  components: {
    PlanDetails,
    RecordDetails
  },
  mounted() {
    console.log(this.$route.query)
    this.basicInfo = this.$route.query;
    this.getFileDetailData();
    if(this.basicInfo.identity === 'student') {
      this.getInterveneList();
    }
    this.getTaskList();
  },
  computed: {
    ...mapGetters([
      'extra',
      'userId'
    ])
  },
  data() {
    return {
      basicInfo: {},
      infoForm: {},
      taskSearchForm: {
        taskName: '',
        scaleName: ''
      },
      interveneList: [],
      reportList: [],
      interveneMoreInfo: false,
      reportMoreInfo: false,
    }
  },
  methods: {
    getFileDetailData() {
      let data = {};
      if(this.basicInfo.identity === 'student') {
        const { studentId, identity } = this.basicInfo;
        data = { studentId, identity };
      }else if(this.basicInfo.identity === 'teacher') {
        const { teacherId, identity } = this.basicInfo;
        data = { teacherId, identity };
      }
      fileDetails(data).then(res => {
        if(res.success) {
          this.infoForm = res.result;
          this.infoForm.pinyin = '';
          this.infoForm.nativePlace = '';
        }
      }).catch(err => {
        this.$message.error(err)
      })
    },
    getInterveneList() {
      getIntervationContentByStuId({stuId: this.basicInfo.studentId, userId: this.userId}).then(res => {
        if(res.success) {
          this.interveneList = res.result.records;
          if(this.interveneList.length > 2) {
            this.interveneMoreInfo = true;
          }else {
            this.interveneMoreInfo = false;
          }
        }
      }).catch(err => {
        this.$message.error(err)
      })
    },
    getTaskList() {
      const data = {
        userId: this.basicInfo.userId
      }
      for(let key in this.taskSearchForm) {
        if(this.taskSearchForm[key]) {
          data[key] = this.taskSearchForm[key];
        }
      }
      getTaskByUserId(data).then(res => {
        if(res.success) {
          this.reportList = res.result;
          if(this.reportList.length > 2) {
            this.reportMoreInfo = true;
          }else {
            this.reportMoreInfo = false;
          }
        }
      }).catch(err => {
        console.log(err)
      })
    },
    gotoPersonalReport(row) {
      const {name, studentId, classes, grade, userId, school, identity } = this.basicInfo ; 
      const { taskId, scaleName } = row;
      const data = { taskId, scaleName, name, studentId, classes, grade, userId, school, identity };
      data.type = 'personal'
      const routeUrl = this.$router.resolve({
        path: '/report',
        query: data
      })
      window.open(routeUrl.href, "_blank")
    },
    openMore(type) {
      if(type === 'report') {
        this.reportMoreInfo = false;
      }else if(type === 'intervene') {
        this.interveneMoreInfo = false;
      }
    },
    closeMore(type) {
      if(type === 'report') {
        this.reportMoreInfo = true;
      }else if(type === 'intervene') {
        this.interveneMoreInfo = true;
      }
    },
    interveneDetails(row) {
      if(row.moudle != '干预方案') {
        // 跳转到recordsDetails
        this.$refs.RecordDetails.openDialog(row.id, row.psyTeacher)

      }else if(row.moudle == '干预方案') {
        this.$refs.PlanDetails.openDialog(row.id, row.psyTeacher);
      }
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/variables.scss";
@import "@/styles/common/organization.scss";
.file-details-container {
  .file-title {
    font-size: 28px;
    font-family: Microsoft YaHei;
    font-weight: 400;
    color: #000000;
    line-height: 72px;
    text-align: center;
    height: 72px;
    border-bottom: 1px solid #E0E0E0;
  }
  .content {
    margin: 0 60px;
    .module {
      margin: 32px 0;
      .module-title {
        height: 22px;
        font-size: 22px;
        font-family: Microsoft YaHei;
        font-weight: 400;
        color: #000000;
        line-height: 22px;
        &::before {
          content: '1';
          color: $themeColor; 
          width: 6px;
          height: 22px;
          background-color: $themeColor;
          border-radius: 3px;
          margin-right: 10px;
        }
      }
      .detail-info {
        padding-top: 30px;
        display: flex;
        justify-content: space-between;
        .form-area {
          width: 80%;
          flex: 1 1 auto;
          display: flex;
          flex-wrap: wrap;
          ::v-deep .el-form-item {
            width: 30%;
          }
        }
        .pic-area {
          width: 240px;
          height: 150px;
          flex: 1 1 auto;
          background: url(../../../assets/imgs/personal-file/zhaopian.png) no-repeat left center;
        }
      }
      .search-container {
        margin-top: 25px;
        .search-box {
          width: 28%;
          margin-right: 40px;
        }
      }
      .content-container {
        ::v-deep .el-button {
          border: 1px solid $themeColor;
          border-radius: 13px;
          color: $themeColor;
        }
      }
      .more-info {
        margin-top: 25px;
        text-align: center;
        color: $themeColor;
        cursor: pointer;
      }
    }
  }
}
</style>