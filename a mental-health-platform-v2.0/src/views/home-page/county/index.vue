<template>
  <div class="home-page">
    <div class="notice-block">
      <p>{{ lastestNotice.title }}</p>
      <p>{{ lastestNotice.issuedUnit }}</p>
      <p>{{ lastestNotice.issuedTime }}</p>
      <p class="para-more" @click="goto('Notice')">
        <span>更多</span>
      </p>
    </div>
    <div class="data-panel-block">
      <div class="warning-data bg-box" @click="goto('WarningDatabase')">
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
      <div class="service-count bg-box">
        <p class="box-title">服务人次</p>
        <div class="service-count-box box-content">
          <div class="top-btn btn-group">
            <p class="label" :class="{'active-color': serviceType == 1}" @click="serviceTypeSwitch(1)">心理咨询服务人次</p>
            <p class="label" :class="{'active-color': serviceType == 2}" @click="serviceTypeSwitch(2)">心理热线服务人次</p>
          </div>
          <template v-if="serviceType === 1">
            <div class="my-line">
              <my-line :id="'service'" :label="serviceData.label"
                :xAxis="serviceData.xAxis"
                :values="serviceData.values"
                :areaStyle="true"
                :legend="false">
              </my-line>
            </div>
            <div class="bottom-btn btn-group">
              <p class="label" :class="{'active-color': serviceState == 1}" @click="serviceSwitch(1)">当月</p>
              <p class="label" :class="{'active-color': serviceState == 2}" @click="serviceSwitch(2)">当年</p>
              <p class="label" :class="{'active-color': serviceState == 3}" @click="serviceSwitch(3)">历年</p>
            </div>
          </template>
          <template v-if="serviceType === 2">
            <div class="my-line">
              <my-line :id="'hotline'" :label="hotLineData.label"
                :color="hotlineColor"
                :xAxis="hotLineData.xAxis"
                :values="hotLineData.values">
              </my-line>
            </div>
            <div class="bottom-btn btn-group">
              <p class="label" :class="{'active-color': hotlineState == 1}" @click="hotlineSwitch(1)">当月</p>
              <p class="label" :class="{'active-color': hotlineState == 2}" @click="hotlineSwitch(2)">当年</p>
              <p class="label" :class="{'active-color': hotlineState == 3}" @click="hotlineSwitch(3)">历年</p>
            </div>
          </template>
        </div>
      </div>
      <div class="data-panel bg-box">
        <p class="box-title">数据看板</p>
        <div class="data-panel-box box-content">
          <div class="data-box">
            <div class="img-bg">
              <p class="number">{{ dataPanel.schoolNum }}</p>
              <p class="extra">学校数量</p>
            </div>
          </div>
          <div class="data-box">
            <div class="img-bg">
              <p class="number">{{ dataPanel.fullNum }}/{{ dataPanel.partNum }}</p>
              <p class="extra">专职/兼职心理老师数量</p>
            </div>
          </div>
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
        </div>
      </div>
    </div>
    <div class="middle">
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
              <div class="info">
                <div class="info-block">
                  <p class="info-title">已完成学校</p>
                  <p class="info-data">
                    <span class="data">{{ taskStatistics.completeSchool }}</span>
                    <span class="extra">个</span>
                  </p>
                </div>
                <div class="info-block">
                  <p class="info-title">已测人数</p>
                  <p class="info-data">
                    <span class="data">{{ taskStatistics.completeNumbers }}</span>
                    <span class="extra">人</span>
                  </p>
                </div>
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
              <div class="info">
                <div class="info-block">
                  <p class="info-title">有效数据</p>
                  <p class="info-data">
                    <span class="data">{{ taskStatistics.validNumbers }}</span>
                    <span class="extra">人</span>
                  </p>
                </div>
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
              <div class="info">
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
      </div>
      <div class="task-detail bg-box">
        <p class="box-title">任务详情</p>
        
      <el-select v-model="selectedTask"  placeholder="请选择任务" class="task-choose" clearable 
          @change="getLatestTask" size="medium" filterable popper-append-to-body>
        <el-option
          v-for="item in taskList"
          :key="item.task_id"
          :label="item.name"
          :value="item.task_id">
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
            <span>更多</span>
          </p>
        </div>
      </div>
    </div>
    <div class="warning-table bg-box">
      <p class="box-title">实时进度和预警监控</p>
      <div class="warning-table-block">
        <el-scrollbar wrap-class="scrollbar-wrapper">
          <el-table :data="schoolProcess">
            <el-table-column prop="school" label="学校" align="center"></el-table-column>
            <el-table-column prop="completePercent" label="进度" align="center"></el-table-column>
            <el-table-column prop="warnCount" label="预警人数" align="center"></el-table-column>
            <el-table-column prop="warnPercent" label="预警比例" align="center"></el-table-column>
            <el-table-column prop="chargePerson" label="负责人" align="center"></el-table-column>
            <el-table-column prop="phone" label="联系方式" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
              <template slot-scope="scope">
                <span class="table-font" @click="urge(scope.row)">催办</span>
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
import { latestTask, taskStatistics, taskProgress, taskList } from '@/api/task.js'
import { getDataStatistics } from '@/api/organization.js'
import { noticeList } from '../../../api/notice'
import { getIntervationStudentInfo, getExpiringStudent } from '../../../api/psyInterveneTask.js'
import { mapGetters } from 'vuex'
import CircleProgress from '../../../components/CircleProgress/index.vue'
import RoundPie from '@/components/RoundedPie/index.vue'
import MyLine from '@/components/Line/index.vue'

export default {
  name: 'countyHomePage',
  components: {
    NoticeList,
    CircleProgress,
    RoundPie,
    MyLine,
},
  computed: {
    ...mapGetters([
      'extra',
      'province',
      'city',
      'county'
    ])
  },
  data() {
    return {
      taskList: [],
      selectedTask: '',
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
        fullNum: 0,
        partNum: 0,
        schoolNum: 0
      },
      taskStatistics: {
        completeNumbers: 0, // 已测人数
        validNumbers: 0, // 有效人数
        warnNumbers: 0, // 预警人数
        completePercent: 0, //完成进度
        validPercent: 0, //有效比例
        warnPercent: 0, // 预警比例
        completeSchool: 0 // 完成学校
      },
      schoolProcess: [],
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
      // 心理咨询数据
      serviceData: {
        label: ['人数'],
        xAxis: [],
        values: [],
      },
      serviceState: 1, // 1-当月，2-当年，3-历年
      serviceType: 2, // 1-心理咨询，2-心理热线,
      hotLineData: {
        label: ['学生', '老师', '家长', '其他'],
        xAxis: [],
        values: [],
      },
      hotlineState: 1, // 1-当月，2-当年，3-历年
      hotlineColor: ["#CF8D00", '#52A4E9', '#E96D6D', '#CA9FFF', '#fff'],
      taskDetailDialogVisible: false,
      // end
    }
  },
  mounted() {
    this.getTaskList();
    this.getInterveneData();
    this.getLatestTask();
    this.getDataPanel();
    this.getLastestNotice();
    this.initHotlineData();
    this.initServiceData();
  },
  methods: {
    
    getTaskList() {
      this.loading = true;
      const data = {
        province: this.province,
        city: this.city,
        county: this.county
      }
      taskList(data).then(res => {
        this.loading = false;
        res.result.forEach(val => {
          val.showState = 'text';
          val.state = val.state == 0 ? '未开始' : ( val.state == 1 ? '进行中' : '已结束');
          val.percent = parseInt(val.percent * 100) + '%';
        })
        this.taskList = res.result;
      }).catch(() => {
        this.loading = true;
      })},
    getLatestTask(item) {
      let data = {
        province: this.province,
        city: this.city,
        county: this.county,
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
        county: this.county
      }
      getDataStatistics(data).then(res => {
        if (res.result) {
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
        taskId: this.lastestTaskInfo.taskId
      }
      taskStatistics(data).then(res => {
        if (res.result) {
          this.taskStatistics = res.result;
        }
      }).catch(err => {
        console.log(`taskStatistics${err}`)
      })
      taskProgress(data).then(res => {
        if (res.result.records) {
          this.schoolProcess = res.result.records;
          this.schoolProcess.forEach(item => {
            item.completePercent = (item.completePercent * 100)%1 == 0 ? item.completePercent * 100 + '%' : (item.completePercent * 100).toFixed(2) + '%';
            item.warnPercent = (item.warnPercent * 100)%1 == 0 ? item.warnPercent * 100 + '%' : (item.warnPercent * 100).toFixed(2) + '%';
          })
        }
      }).catch(err => {
        console.log(`schoolTaskProgress${err}`)
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
        msgLevel: 'county'
      }
      getIntervationStudentInfo(data).then(res => {
        if (res.result) {
          let warningData = [];
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
          this.warningTotalData = warningData;
        }
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
    initServiceData() {
      let values = [];
      this.serviceData.xAxis = [];
      this.serviceData.values = [];
      if(this.serviceState == 1) {
        let a = 10;
        for(let i = 0; i < 31; i++) {
          this.serviceData.xAxis.push("7." + (i + 1));
          a = a + parseInt(Math.random() * 20)
          values.push(a);
        }
      }else if(this.serviceState == 2) {
        let a = 10;
        for(let i = 0; i < 12; i++) {
          this.serviceData.xAxis.push(i + 1 + '月');
          a = a + parseInt(Math.random() * 20)
          values.push(a);
        }
      }else if(this.serviceState == 3) {
        let a = 10;
        for(let i = 19; i < 23; i++) {
          this.serviceData.xAxis.push( "20" + i + '年');
          a = a + parseInt(Math.random() * 20)
          values.push(a);
        }
      }
      this.serviceData.values.push(values);
    },
    initHotlineData() {
      let values = [];
      this.hotLineData.xAxis = [];
      this.hotLineData.values = [];
      if(this.hotlineState == 1) {
        for(let i = 0; i < 31; i++) {
          this.hotLineData.xAxis.push("3." + (i + 1));
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 0; i < 31; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 0; i < 31; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 0; i < 31; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
      }else if(this.hotlineState == 2) {
        for(let i = 0; i < 12; i++) {
          this.hotLineData.xAxis.push(i + 1);
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 0; i < 12; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 0; i < 12; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 0; i < 31; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
      }else if(this.hotlineState == 3) {
        for(let i = 19; i < 23; i++) {
          this.hotLineData.xAxis.push( '20' + i);
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 19; i < 23; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 19; i < 23; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
        values = [];
        for(let i = 0; i < 31; i++) {
          values.push(parseInt(Math.random() * 100));
        }
        this.hotLineData.values.push(values);
      }
    },
    serviceSwitch(type){
      this.serviceState = type;
      this.initServiceData();
    },
    serviceTypeSwitch(type) {
      this.serviceType = type;
      if(type == 1) {
        this.initServiceData();
      }else {
        this.initHotlineData();
      }
    },
    hotlineSwitch(type) {
      this.hotlineState = type;
      this.initHotlineData();
    },
    // 催办
    urge(row) {

    },
    goto(name) {
      this.$router.push({name: name});
    },
  },
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
    height: 33%;
    display: flex;
    justify-content: space-between;
    .warning-data {
      width: 50%;
      height: 100%;
      background: url("~@/assets/imgs/home-img/warningdata.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
      cursor: pointer;
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
    .service-count {
      width: 28%;
      height: 100%;
      background: url("~@/assets/imgs/home-img/shujukanban.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
      .service-count-box {
        position: relative;
        width: 100%;
        .my-line {
          position: absolute;
          top: 8%;
          left: 0;
          height: 70%;
          width: 100%;
          margin-left: -2%;
        }
        .btn-group {
          display: flex;
          position: absolute;
          width: 100%;
          font-size: 16px;
          justify-content: space-around;
        }
        .bottom-btn {
          bottom: 8%;
        }
        .top-btn {
          top: 8%;
          z-index: 101;
        }
        .label{
          // width: 12%;
          border: 1px solid #CA9FFF;
          text-align: center;
          padding: 0 10px;
          border-radius: 12px;
          color: #C190FE;
          cursor: pointer;
          &:hover {
            color: white;
            background-color: #C190FE;
          }
        }
        .active-color {
          color: white;
          background-color: #C190FE;
        }
      }
    }
    .data-panel {
      width: 20%;
      height: 100%;
      background: url("~@/assets/imgs/home-img/shujukanban.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
      .data-panel-box {
        display: flex;
        flex-direction: column;
        text-align: center;
        justify-content: space-around;
        .data-box {
          height: 20%;
          flex: 0 0 auto;
          .img-bg {
            width: 100%;
            height: 100%;
            background: url("~@/assets/imgs/home-img/shujubeijing.png") no-repeat 50% 50%;
            background-size: 75% 100%;
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
  }

  .para-more {
    color: #CF8D00;
    text-align: right;
    cursor: pointer;

    span:hover {
      text-decoration: underline;
    }
  }
  .middle {
    height: 50%;
    display: flex;
    justify-content: space-between;
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
  .task-progress-block {
    width: 79%;
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
            width: 50%;
            position: absolute;
            top: 0;
            left: 20px;
          }
          .info {
            width: 37%;
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
    height: 27%;
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
  .bg-box {
    .box-title {
      height: 11%;
      margin-left: 1vw;
      margin-right: 1vw;
      display: flex;
      align-items: center;
    }
    .box-content {
      height: 89%;
    }
  }
   .task-choose {
      margin-top: 0%;
      margin-left: 50%;
      height: 10%;
      display: flex;
      align-items: center;
      color: white;
      ::v-deep.el-input__inner {
        background-color: rgb(62, 44, 105, 0.7);
        color: rgb(62, 44, 105, 0.7);
       }
    }


}
</style>