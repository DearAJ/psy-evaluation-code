<template>
  <div class="home-page">
    <div class="notice-block">
      <p>{{ lastestNotice.title }}</p>
      <p>{{ lastestNotice.issuedUnit }}</p>
      <p>{{ lastestNotice.issuedTime }}</p>
      <p class="para-more" @click="goto('Notice', 1)">
        <span>更多</span>
      </p>
    </div>
    <div class="data-panel-block">
      <div class="warning-data bg-box" :class="{'manage-pointer': level >= 3}" @click="goto('WarningDatabase', 3)">
        <p class="box-title">预警库数据</p>
        <div class="warning-data-box box-content">
          <div class="warning-pie">
            <div class="title">
              <p class="title-font">预警库总人数</p>
            </div>
            <div class="round-pie">
              <round-pie :id="'sWarningPie'"
                :pieData="warningTotalData"
                :totalNum="totalWarning"
                :title="'预警人数'">
              </round-pie>
            </div>
          </div>
          <div class="warning-panel">
            <div class="remove-people">
              <div class="title">
                <p class="title-font">解除预警人数</p>
              </div>
              <div class="content">
                <div class="data-box">
                  <p class="tp">总人数</p>
                  <p class="nump">{{ unWarning }}</p>
                </div>
                <div class="data-box">
                  <p class="tp">当月</p>
                  <p class="nump">{{ unWarningMonth }}</p>
                </div>
              </div>
            </div>
            <div class="out-time">
              <div class="title">
                <p class="title-font">超时提醒</p>
              </div>
              <div class="content">
                <div class="warning-database-box">
                  <p class="tp">预警库</p>
                  <p class="nump">{{ expireNum }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="data-panel bg-box">
        <p class="box-title">数据看板</p>
        <div class="data-panel-box box-content">
          <div class="data-box">
            <div class="img-bg">
              <p class="number">{{ dataPanel.studentsNum }}</p>
              <p class="extra">学生数量</p>
            </div>
          </div>
          <div class="data-box">
            <div class="img-bg">
              <p class="number">{{ dataPanel.teachersNum }}</p>
              <p class="extra">教师数量</p>
            </div>
          </div>
          <div class="data-box">
            <div class="img-bg">
              <p class="number">{{ dataPanel.roleNum }}</p>
              <p class="extra">角色数量</p>
            </div>
          </div>
          <div class="data-box">
            <div class="img-bg">
              <p class="number">{{ dataPanel.psyTeacherNum }}</p>
              <p class="extra">心理老师数量</p>
            </div>
          </div>
        </div>
      </div>
      <div class="task-detail bg-box scrollbar-me">
        <p class="box-title">任务详情</p>
        
      <el-select v-model="selectedTask" placeholder="请选择任务" size="small" class="task-choose" clearable 
      style="backgroud-color: rgb(62, 44, 105, 0.7);"
          @change="getLatestTask" :loading="taskChooseLoading">
        <el-option
          v-for="item in taskOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
        <div class="task-detail-box box-content">
          <div class="para">
            <p class="p-title">任务名称：</p>
            <p class="p-content">{{ lastestTaskInfo.name }}</p>
          </div>
          <div class="para">
            <p class="p-title">任务时间：</p>
            <p class="p-content">{{ lastestTaskInfo.startDate }} 至 {{ lastestTaskInfo.endDate }}</p>
          </div>
          <div class="para">
            <p class="p-title">任务对象：</p>
            <el-tooltip :content="lastestTaskInfo.crowd" placement="bottom" effect="light">
              <p class="p-content last">{{ lastestTaskInfo.crowd }}</p>
            </el-tooltip>
          </div>
          <p class="para-more" @click="taskDetailDialogVisible = true;">
            <span>展开</span>
          </p>
        </div>
      </div>
    </div>
    <div class="task-progress-block bg-box">
      <p class="box-title">任务进度</p>
      <div class="progress-block box-content">
        <div class="progress-box">
          <div class="title">
            <p class="title-font">完成进度</p>
          </div>
          <div class="progress">
            <circle-progress :radius="'33%'" :id="'cityCP'" :percent="taskStatistics.completePercent"
              class="circle-progress"
              :isTransparent="true"
              :innerColor="'#CF8D00'"
              :outsideColor="'rgb(62, 44, 105, 0.7)'"
              :imgUrl="require('@/assets/imgs/home-img/progress-bg.png')">
            </circle-progress>
            <div class="info-block">
              <p class="info-title">已测人数</p>
              <p class="info-data">
                <span class="data">{{ taskStatistics.completeNumbers }}</span>
                <span class="extra">人</span>
              </p>
            </div>
          </div>
        </div>
        <div class="progress-box">
          <div class="title">
            <p class="title-font">有效比例</p>
          </div>
          <div class="progress">
            <circle-progress :radius="'33%'" :id="'cityCP'" :percent="taskStatistics.validPercent"
              class="circle-progress"
              :isTransparent="true"
              :innerColor="'#CF8D00'"
              :outsideColor="'rgb(62, 44, 105, 0.7)'"
              :imgUrl="require('@/assets/imgs/home-img/progress-bg.png')">
            </circle-progress>
            <div class="info-block">
              <p class="info-title">有效数据</p>
              <p class="info-data">
                <span class="data">{{ taskStatistics.validNumbers }}</span>
                <span class="extra">人</span>
              </p>
            </div>
          </div>
        </div>
        <div class="progress-box">
          <div class="title">
            <p class="title-font">预警比例</p>
          </div>
          <div class="progress">
            <circle-progress :radius="'33%'" :id="'cityCP'" :percent="taskStatistics.warnPercent"
              class="circle-progress"
              :isTransparent="true"
              :innerColor="'#A10808'"
              :outsideColor="'rgb(62, 44, 105, 0.7)'"
              :imgUrl="require('@/assets/imgs/home-img/progress-bg.png')">
            </circle-progress>
            <div class="info-block">
              <p class="info-title">预警人数</p>
              <p class="info-data">
                <span class="data" style="color: #A10808">{{ taskStatistics.warnNumbers }}</span>
                <span class="extra">人</span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="warning-table bg-box">
      <p class="box-title">实时进度和预警监控</p>
      <!-- <el-select v-model="selectedTask" placeholder="请选择任务" class="task-choose" clearable 
          @change="getTaskProgress" :loading="taskChooseLoading">
        <el-option
          v-for="item in taskOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select> -->
      <div class="warning-table-block" v-loading.fullscreen.lock="loading">
        <el-scrollbar wrap-class="scrollbar-wrapper">
          <el-table :data="classProcess">
            <el-table-column prop="period" label="学段" align="center"></el-table-column>
            <el-table-column prop="grade" label="年级" align="center"></el-table-column>
            <el-table-column prop="classes" label="班级" align="center"></el-table-column>
            <el-table-column prop="process" label="进度" align="center"></el-table-column>
            <el-table-column prop="warnNum" label="预警人数" align="center"></el-table-column>
            <el-table-column prop="warnPercent" label="预警比例" align="center"></el-table-column>
            <el-table-column prop="validNum" label="有效人数" align="center"></el-table-column>
            <el-table-column prop="validPercent" label="有效比例" align="center"></el-table-column>
            <el-table-column label="操作" align="center" v-if="level > 1">
              <template slot-scope="scope">
                <span class="table-font" @click="checkWarningData(scope.row)">查看</span>
              </template>
            </el-table-column>
          </el-table>
        </el-scrollbar>
      </div>
    </div>
    <el-dialog title="任务详情" :visible.sync="taskDetailDialogVisible"
      width="60%"
      :close-on-click-modal="true">
      
      <div class="form-block">
        <el-form :model="lastestTaskInfo" label-width="90px" ref="taskDetailForm">
          <el-form-item label="最新任务: " prop="name">
            <p>{{ lastestTaskInfo.name }}</p>
          </el-form-item>
          <el-form-item label="任务时间: " prop="date">
            <p>{{ lastestTaskInfo.startDate }} 至 {{ lastestTaskInfo.endDate }}</p>
          </el-form-item>
          <el-form-item label="任务对象: " prop="crowd">
            <p>{{ lastestTaskInfo.crowd }}</p>
          </el-form-item>
          <el-form-item label="任务内容: " prop="content">
            <p>{{ lastestTaskInfo.content }}</p>
          </el-form-item>
          <el-form-item label="下发人: " prop="issuedName">
            <p>{{ lastestTaskInfo.issuedName }}</p>
          </el-form-item> 
          <el-form-item label="下发单位: " prop="issuedUnit">
            <p>{{ lastestTaskInfo.issuedUnit }}</p>
          </el-form-item>
          <el-form-item label="测评量表: " prop="scaleName">
            <p>{{ lastestTaskInfo.scaleName }}</p>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import  NoticeList from '@/components/NoticeList/index.vue'
import { getSchoolTasks, schoolLatestTask, latestTask, taskStatistics, schoolTaskProgress } from '@/api/task.js'
import { getDataStatistics } from '@/api/organization.js'
import { noticeList } from '../../../api/notice'
import { getIntervationStudentInfo, getExpiringStudent } from '../../../api/psyInterveneTask.js'
import { mapGetters } from 'vuex'
import CircleProgress from '../../../components/CircleProgress/index.vue'
import RoundPie from '@/components/RoundedPie/index.vue'

export default {
  name: 'schoolHomePage',
  components: {
    NoticeList,
    CircleProgress,
    RoundPie,
},
  computed: {
    ...mapGetters([
      'extra',
      'province',
      'city',
      'county',
      'level'
    ])
  },
  data() {
    return {
      loading: false,
      taskOptions: [],
      taskChooseLoading: false,
      selectedTask: '',
      currentTaskInfo: {},
      taskProgress: [],
      lastestTaskInfo: {
        crowd: '暂无',   //下发范围
        name: '暂无',
        startDate: '暂无',
        endDate: '暂无',
        taskId: '',
        content: '',
        issuedUnit: '',
        issuedName: '',
        scaleName: ''
      },
      dataPanel: {
        studentsNum: 0,
        teachersNum: 0,
        roleNum: 0,
        PsyTeacherNum: 0
      },
      taskStatistics: {
        completeNumbers: 0, // 已测人数
        validNumbers: 0, // 有效人数
        warnNumbers: 0, // 预警人数
        completePercent: 0, //完成进度
        validPercent: 0, //有效比例
        warnPercent: 0, // 预警比例
      },
      classProcess: [],
      lastestNotice: {
        title: '暂无',
        issuedUnit: '暂无',
        issuedTime: '暂无'
      },
      // 预警库总人数处的数据格式
      warningTotalData: [
        {value: 0, name: '一级预警'},
        {value: 0, name: '二级预警'},
        {value: 0, name: '三级预警'}
      ],
      totalWarning: '0',
      unWarningMonth: '0',
      unWarning: '0',
      expireNum: '0',
      taskDetailDialogVisible: false,
    }
  },
  mounted() {
    this.getInterveneData();
    this.getLatestTask();
    this.getDataPanel();
    this.getTaskProgress();
    this.getLastestNotice();
    this.getTaskList();
  },
  methods: {
    getTaskList() {
      let data = {
        schoolId: this.extra.schoolId
      }
      this.taskChooseLoading = true
      getSchoolTasks(data).then(res => {
        if (res.result){
        res.result.forEach(item => {
            
            this.taskOptions.push({
              label: item.name,
              value: item.task_id
            })
        })
        }else {
          this.taskOptions = []
        }
        
      this.taskChooseLoading = false
      }).catch(err => {
        console.log(`taskprocess${err}`)
      })
    },
    getLatestTask(item) {
      let data = {
        province: this.province,
        city: this.city,
        county: this.county,
        schoolId: this.extra.schoolId,
        taskId: item
      }
      latestTask(data).then(res => {
        if(res.result) {
          this.lastestTaskInfo = res.result;
          console.log(this.lastestTaskInfo)
          this.getTaskStatistics();
        }
      }).catch(err => {
        console.log(`latestTask${err}`)
      })
    },
    getDataPanel() {
      let data = {
        province: this.province,
        city: this.city,
        county: this.county,
        schoolId: this.extra.schoolId
      }
      getDataStatistics(data).then(res => {
        if(res.result) {
          this.dataPanel = res.result;
        }
      }).catch(err => {
        console.log(`getDataStatistics${err}`)
      })
    },
    getTaskStatistics() {
      let data = {
        province: this.province,
        city: this.city,
        county: this.county,
        schoolId: this.extra.schoolId,
        taskId: this.lastestTaskInfo.taskId
      }
      taskStatistics(data).then(res => {
        if(res.result) {
          this.taskStatistics = res.result;
        }
      }).catch(err => {
        console.log(`taskStatistics${err}`)
      })
    },
    getTaskProgress(item) {
      console.log("==================> ",item)
      this.loading = true;
      let data = {
        schoolId: this.extra.schoolId,
        taskId: item
      }
      // this.classProcess = []
      schoolTaskProgress(data).then(res => {
        if(res.result.schoolList) {
          this.classProcess = res.result.schoolList;
          this.classProcess.forEach(item => {
            item.process = (item.process * 100)%1 == 0 ? item.process * 100 + '%' : (item.process * 100).toFixed(2) + '%';
            item.warnPercent = (item.warnPercent * 100)%1 == 0 ? item.warnPercent * 100 + '%' : (item.warnPercent * 100).toFixed(2) + '%';
            item.validPercent = (item.validPercent * 100)%1 == 0 ? item.validPercent * 100 + '%' : (item.validPercent * 100).toFixed(2) + '%';
          }
          )
          
        }
      this.loading = false;
      }).catch(err => {
        console.log(`schoolTaskProgress${err}`)
      this.loading = false;
      })
      
    },
    getLastestNotice() {
      let data = {
        pageNum: 1,
        pageSize: 1
      }
      noticeList(data).then(res => {
        if (res.result  && Object.keys(res.result).length > 0) {
          this.lastestNotice = res.result.records[0];
          let time = this.lastestNotice.issuedTime;
          this.lastestNotice.issuedTime = time.substring(0, 10);
        }
      }).catch(err => {
        console.log(`noticeList${err}`)
      })
    },
    getInterveneData() {
      let data = {
        province: this.province,
        city: this.city,
        county: this.county,
        schoolName: this.extra.school,
        msgLevel: 'school'
      }
      getIntervationStudentInfo(data).then(res => {
        let warningData = [];
        if (res.result) {
          for (let key in res.result) {
            if (key == '一级预警人数') {
              warningData.push({name: '一级预警', value: res.result[key]});
            }
            if (key == '二级预警人数') {
              warningData.push({name: '二级预警', value: res.result[key]});
            }
            if (key == '三级预警人数') {
              warningData.push({name: '三级预警', value: res.result[key]});
            }
            if (key == '总人数') {
              this.totalWarning = res.result[key] + '';
            }
            if (key == '解除预警总人数') {
              this.unWarning = res.result[key];
            }
            if (key == '当月解除人数') {
              this.unWarningMonth = res.result[key];
            }
          }
        }
        this.warningTotalData = warningData;
      }).catch(err => {
        console.log(`getIntervationStudentInfo${err}`);
      });
      getExpiringStudent(data).then(res => {
        if (res.result) {
          this.expireNum = res.result;
        }
      }).catch(err => {
        console.log(`getExpiringStudent${err}`)
      });
    },
    checkWarningData(row) {
      // 查看进度和预警数据，学校端跳转到任务执行页面
      // DFXCE是任务id，CGXSG是量表id，NN为taskName，这些是必传字段
      // 然后传一下学段、年级、班级，跳转过去直接填充搜索框，进行筛选
      if(this.level >= 2) {
        const data = {
          DFXCE: (this.lastestTaskInfo.taskId * 18) + '#',
          CGXSG: (this.lastestTaskInfo.scaleId * 18) + '#',
          period: row.period,
          grade: row.grade,
          classes: row.classes
        }
        this.$router.push({name: 'PerformTask', query: data});
      }
    },
    goto(name, level) {
      if(this.level >= level) {
        this.$router.push({name: name});
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/variables.scss";
@import "@/styles/common/warning-and-progress.scss";
.home-page {
  padding: 5px 30px 15px 20px;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
  .notice-block {
    height: 4%;
    background: url("~@/assets/imgs/home-img/notice-bg.png") no-repeat;
    background-size: 100% 100%;
    display: flex;
    font-size: 14px;
    justify-content: space-around;
    align-items: center;
    
  }
  .data-panel-block {
    height: 30%;
    display: flex;
    justify-content: space-between;
    .warning-data {
      width: 50%;
      height: 100%;
      background: url("~@/assets/imgs/home-img/warningdata.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
      .warning-data-box {
        box-sizing: border-box;
        padding: 10px 15px;
        display: flex;
        justify-content: space-between;
        .title {
          height: 30px;
          width: 100%;
          flex: 0 0 auto;
          background: url("~@/assets/imgs/home-img/biaoti-chang.png") no-repeat 0 center;
          background-size: 100% 100%;
        }
        .title-font {
          font-size: 16px;
          margin-left: 20px;
          line-height: 25px;
        }
        .warning-pie {
          width: 55%;
          flex: 0 0 auto;
          display: flex;
          justify-content: center;
          flex-direction: column;
          .round-pie {
            height: calc(100% - 30px);
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
        .warning-panel {
          width: 38%;
          flex: 0 0 auto;
          display: flex;
          flex-direction: column;
          justify-content: center;
          .title {
            background: url("~@/assets/imgs/home-img/biaoti-duan.png") no-repeat 0 center;
            background-size: 100% 100%;
          }
          .remove-people {
            height: 50%;
          }
          .out-time {
            height: 50%;
          }
          .content {
            height: calc(100% - 30px);
            padding: 6px 0;
            box-sizing: border-box;
            display: flex;
            .data-box {
              width: 40%;
              flex: 0 0 auto;
              background: url("~@/assets/imgs/home-img/zongrenshu-kuang.png") no-repeat 0 center;
              background-size: 100% 100%;
              margin-right: 14px;
              display: flex;
              flex-direction: column;
              justify-content: center;
              align-items: center;
              .tp {
                font-size: 16px;
                font-family: Microsoft YaHei;
                font-weight: 400;
                color: #FFFFFF;
              }
              .nump {
                font-size: 20px;
                font-weight: bold;
                color: #C190FE;
              }
            }
            .warning-database-box {
              width: 85%;
              height: 100%;
              background: url("~@/assets/imgs/home-img/yujingku-kuang.png") no-repeat 0 center;
              background-size: 100% 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              .tp {
                width: 35%;
                font-size: 16px;
                font-family: Microsoft YaHei;
                font-weight: 400;
                color: #FFFFFF;
              }
              .nump {
                width: 40%;
                font-size: 46px;
                font-weight: bold;
                color: #A10808;
              }
            }
          }
        }
      }
    }
    .manage-pointer {
      cursor: pointer;
    }
    .data-panel {
      width: 28%;
      height: 100%;
      background: url("~@/assets/imgs/home-img/shujukanban.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
      .data-panel-box {
        display: flex;
        flex-wrap: wrap;
        text-align: center;
        justify-content: space-around;
        .data-box {
          flex: 0 0 40%;
          display: flex;
          justify-content: center;
          .img-bg {
            width: 100%;
            height: 100%;
            background: url("~@/assets/imgs/home-img/shujubeijing.png") no-repeat 50% 50%;
            background-size: contain;
            display: flex;
            flex-direction: column;
            justify-content: center;
            .number {
              font-size: 24px;
              font-weight: bold;
              color: #C190FE;
            }
            .extra {
              font-size: 16px;
              font-family: Microsoft YaHei;
              color: #FFFFFF;
            }
          }
        }
      }
    }
    .task-detail {
      width: 20%;
      height: 100%;
      background: url("~@/assets/imgs/home-img/renwuxiangqing.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
      .task-detail-box {
        display: flex;
        flex-direction: column;
        padding: 15px 20px 10px 20px;
        box-sizing: border-box;
        justify-content: space-around;
        font-size: 14px;
        .para {
          display: flex;
          .p-title {
            color: #CA9FFF;
            width: 75px;
            flex: 0 0 auto;
          }
          .last {
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
          }
        }
      }
    }
  }

  .para-more {
    color: #CF8D00;
    text-align: right;
    cursor: pointer;

    span:hover {
      text-decoration: underline;
    }
  }
  .task-progress-block {
    height: 33.2%;
    background: url("~@/assets/imgs/home-img/renwujindu.png") no-repeat;
    background-size: 100% 100%;
    position: relative;
    .progress-block {
      padding: 10px 15px;
      box-sizing: border-box;
      display: flex;
      justify-content: space-around;
      .progress-box {
        width: 28%;
        flex: 0 0 auto;
        position: relative;
        display: flex;
        justify-content: center;
        flex-direction: column;
        .title {
          height: 30px;
          width: 100%;
          flex: 1 0 auto;
          background: url("~@/assets/imgs/home-img/biaoti-chang.png") no-repeat 0 center;
          background-size: 100% 100%;
          .title-font {
            font-size: 16px;
            margin-left: 20px;
            line-height: 25px;
          }
        }
        .progress {
          $circleHeight: calc(100% - 32px);
          height: $circleHeight;
          width: 100%;
          flex: 1 1 auto;
          position: relative;
          display: flex;
          align-items: center;
          justify-content: flex-end;
          .circle-progress {
            width: 40%;
            position: absolute;
            top: 0;
            left: 20px;
          }
          .info-block {
            width: 47%;
            display: flex;
            flex-direction: column;
            justify-content: space-around;
            .info-title {
              font-size: 18px;
              font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
              font-weight: 400;
              color: #C190FE;
            }
            .info-data {
              .data {
                font-size: 25px;
                font-weight: bold;
                color: #A36F04;
              }
              .extra {
                font-size: 15px;
                font-family: Microsoft YaHei;
                font-weight: 400;
                color: #C190FE;
                margin-left: 5px;
              }
            }
          }
        }
      }
    }
  }
  
  .warning-table {
    height: 30%;
    background: url("~@/assets/imgs/home-img/yujingjiankong.png") no-repeat;
    background-size: 100% 100%;
    position: relative;
    overflow: hidden;
    
    .warning-table-block {
      height: 88%;
      padding: 15px 20px;
      box-sizing: border-box;
      ::v-deep .el-scrollbar {
        height: 100%;
        .el-scrollbar__wrap {
          overflow-x: hidden;
          overflow-y: scroll;
        }
      }

      ::v-deep .el-table, ::v-deep .el-table__expanded-cell{
        background-color: transparent !important;
      }
      ::v-deep .el-table th, ::v-deep .el-table tr, ::v-deep .el-table td {
        background-color: transparent !important;
        color: #C190FE;
      }
      ::v-deep .el-table__header-wrapper {
        background: linear-gradient(to right, rgb(43, 13, 68, 0.8), rgb(63, 29, 94, 0.8), rgb(43, 13, 68, 0.8));
      }

      ::v-deep .el-table thead {
        color: #C190FE;
      }
      ::v-deep .el-table__cell.is-leaf, ::v-deep .el-table td.el-table__cell {
        border: 1px solid rgb(150, 78, 217, 0.3);
      }
      ::v-deep .el-table::before {
        bottom: -1px;
      }
      .table-font {
        color: #A36F04;
        cursor: pointer;
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
  
   .task-choose {
      height: 0;
      margin-top: 0%;
      margin-left: 40%;
      height: 10%;
      display: flex;
      align-items: center;
      
      position: relative;
      padding: 10px 0px 10px 10px;
      color: rgb(62, 44, 105, 0.7);
      
    }
    .task-choose::v-deep.el-input__inner {
        background-color: rgb(62, 44, 105, 0.7);
        color: rgb(62, 44, 105, 0.7);
       }
    .task-choose .el-select-dropdown__item {
      background-color:rgb(62, 44, 105, 0.7);
    }

    
  .bg-box {
    .box-title {
      height: 11%;
      margin-left: 1vw;
      display: flex;
      align-items: center;
    }
    .box-content {
      height: 89%;
      margin-bottom: 0;
    }
  }
  .scrollbar-me {
    overflow:scroll;
    scrollbar-color:rgb(62, 44, 105, 0.7);
  }
  .scrollbar-me::-webkit-scrollbar {
      width : 10px; 
      height: 1px;
    }
  .scrollbar-me::-webkit-scrollbar-thumb {
      border-radius: 10px;
      box-shadow : inset 0 0 5px rgb(62, 44, 105, 0.7);
      background : #535353;
    }
  .scrollbar-me::-webkit-scrollbar-track {
      box-shadow : inset 0 0 5px rgb(62, 44, 105, 0.7);
      border-radius: 10px;
      background : #ededed;
    }

}
</style>