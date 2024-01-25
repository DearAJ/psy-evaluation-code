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
      <div class="left">
        <div class="warning-data bg-box" @click="goto('WarningDatabase')">
          <p class="box-title">预警库数据</p>
          <div class="warning-data-box box-content">
            <div class="warning-pie">
              <div class="title">
                <p class="title-font">预警库总人数</p>
              </div>
              <div class="round-pie">
                <round-pie :id="'pWarningPie'"
                  :pieData="warningTotalData"
                  :totalNum="totalWarning"
                  :title="'预警人数'">
                </round-pie>
              </div>
            </div>
            <div class="remove-people">
              <div class="title-common">
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
              <div class="title-common">
                <p class="title-font">超时提醒</p>
              </div>
              <div class="content">
                <div class="warning-database-box">
                  <p class="tp">总人数</p>
                  <p class="nump">{{ expireNum }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="words bg-box">
          <p class="box-title">心理健康词云</p>
          <div class="hotword-box box-content">
            <hot-words :id="'photwords'" :words="hotwords"></hot-words>
          </div>
        </div>
      </div>
      <div class="right">
        <div class="right-top">
          <div class="map-block">
            <div class="des">
              <p class="des-c">最新任务：{{ lastestTaskInfo.name }}</p>
              <p class="des-c">任务时间：{{ lastestTaskInfo.startDate }} - {{ lastestTaskInfo.endDate }}</p>
              <p class="des-c font-open" @click="taskDetailDialogVisible = true;">展开</p>
            </div>
            <div class="map-area">
              <my-map class="map" :mapName="mapName"
                :id="'pHomeMap'"
                :mapData = "mapShowData"
                @mapClick="mapClick">
              </my-map>
            </div>
            <div class="evaluator-num">
              <p class="title">测评人数</p>
              <div class="num-block">
                <div class="num" v-for="(item, index) in taskStatistics.completeNumbers" :key="`${index}+${item}`">{{item}}</div>
                <span>人</span>
              </div>
            </div>
            <div class="ranking">
              <p class="title">{{city}}</p>
              <p class="para" v-for="(item, index) in topThree" :key="item.name">{{index + 1}}、 {{item.name}}</p>
            </div>
          </div>
          <div class="service-data-block">
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
                    <p class="number">{{ dataPanel.countyNum }}</p>
                    <p class="extra">区县数量</p>
                  </div>
                </div>
                <div class="data-box">
                  <div class="img-bg">
                    <p class="number">{{ dataPanel.schoolNum }}</p>
                    <p class="extra">学校数量</p>
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
                <div class="data-box">
                  <div class="img-bg">
                    <p class="number">{{ dataPanel.fullNum }}</p>
                    <p class="extra">专职心理老师数量</p>
                  </div>
                </div>
                <div class="data-box">
                  <div class="img-bg">
                    <p class="number">{{ dataPanel.partNum }}</p>
                    <p class="extra">兼职心理老师数量</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="task-progress-block bg-box">
          <p class="box-title">
            <span>任务进度</span>
            <i class="location el-icon-location"></i>
            <span class="location province">{{city}}</span>
            <span v-if="selectedCounty" class="location city">-{{selectedCounty}}</span>
          </p>
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
                    <p class="info-title">已完成</p>
                    <p class="info-data">
                      <span class="data">{{ taskStatistics.complete }}</span>
                    </p>
                  </div>
                  <div class="info-block">
                    <p class="info-title">待完成</p>
                    <p class="info-data">
                      <span class="data">{{ taskStatistics.unComplete }}</span>
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
                      <span class="extra">份</span>
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
            <div class="progress-box">
              <div class="title">
                <p class="title-font">各学段预警比例</p>
              </div>
              <div class="progress">
                <my-polar-bar :id="'pPeriodWarn'" :label="periodWarningData.label"
                  class="polar-bar"
                  :value="periodWarningData.value"
                  :max="periodWarningData.max">
                </my-polar-bar>
                <div class="sex-radio">
                  <div class="male" :style="`width: ${periodWarningData.male}%`">
                    <span v-if="periodWarningData.male">男-{{periodWarningData.male}}%</span>
                  </div>
                  <div class="female" :style="`width: ${periodWarningData.female}%`">
                    <span v-if="periodWarningData.female > 0">女-{{periodWarningData.female}}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="warning-table bg-box">
      <p class="box-title">实时进度和预警监控</p>
      <div class="warning-table-block">
        <el-scrollbar wrap-class="scrollbar-wrapper">
          <el-table :data="cityProcess">
            <el-table-column prop="county" label="区县" align="center"></el-table-column>
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
import MyMap from '@/components/Map/index.vue'
import { mapGetters } from 'vuex'
import { latestTask, taskStatistics, taskProgress, allUnitTaskProcess } from '@/api/task.js'
import { getIntervationStudentInfo, getExpiringStudent, getHotWordList } from '../../../api/psyInterveneTask.js'
import { getDataStatistics } from '@/api/organization.js'
import { noticeList } from '../../../api/notice'
import RoundPie from '@/components/RoundedPie/index.vue'
import CircleProgress from '../../../components/CircleProgress/index.vue'
import MyPolarBar from '@/components/PolarBar/index.vue'
import MyLine from '@/components/Line/index.vue'
import HotWords from '@/components/HotWords/index.vue'

export default {
  name: 'cityHomePageProgress',
  components: {
    MyMap,
    RoundPie,
    CircleProgress,
    MyPolarBar,
    MyLine,
    HotWords
  },
  computed: {
    ...mapGetters([
      'province',
      'city'
    ]),
    mapName() {
      return this.$mapMapping[this.city]
    }
  },
  mounted(){
    this.getLastestNotice();
    this.getDataPanel();
    this.getLatestTask();
    this.getInterveneData();
    this.initHotlineData();
    this.initServiceData();
    this.getUnitProcess();
    this.getHotWordList();
  },
  data() {
    return {
      lastestNotice: {
        title: '暂无',
        issuedUnit: '暂无',
        issuedTime: '暂无'
      },
      lastestTaskInfo: {
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
        schoolNum: 0,
        cityNum: 0,
        countyNum: 0
      },
      cityProcess: [],
      warningTotalData: [
        {value: 0, name: '一级预警'},
        {value: 0, name: '二级预警'},
        {value: 0, name: '三级预警'}
      ],
      totalWarning: '0',
      unWarning: 0,
      unWarningMonth: 0,
      expireNum: 0,
      // evaluatorsNumber: '3000', // 地图那的测评人数，字符串格式的哦~
      selectedCounty: '', // 选中的区，注意，任务进度那一开始没选中，显示省的，选中了显示市的，切换的时候可以在mapClick函数中重新获取数据
      periodWarningData: {
        label: ['职高','高中', '初中', '小学', '其他'],
        value: [],
        max: 100,
        male: 0,
        female: 0, // 这里都用数字表示百分比
      },
      taskStatistics: {
        completeNumbers: '0',
        complete: 0,
        unComplete: 0,
        completePercent: 0,
        validNumbers: 0,
        validPercent: 0,
        warnNumbers: 0,
        warnPercent: 0,
        boyWarningPercent: 0,
        girlWarningPercent: 0,
        juniorMiddleSchoolWarningPercent: 0,
        primarySchoolWarningPercent: 0,
        seniorMiddleSchoolWarningPercent: 0,
      },
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
      // end
      mapShowData: [],
      taskDetailDialogVisible: false,
      topThree: [],
      hotwords: [],
    }
  },
  methods: {
    getLastestNotice() {
      let data = {
        pageNum: 1,
        pageSize: 1
      }
      noticeList(data).then(res => {
        if (res.result && Object.keys(res.result).length > 0) {
          this.lastestNotice = res.result.records[0];
          let time = this.lastestNotice.issuedTime;
          this.lastestNotice.issuedTime = time.substring(0, 10);
        }
      }).catch(err => {
        console.log(`noticeList${err}`)
      })
    },
    getDataPanel() {
      let data = {
        province: this.province,
        city: this.city
      }
      getDataStatistics(data).then(res => {
        if (res.result) {
          this.dataPanel = res.result;
        }
      }).catch(err => {
        console.log(`getDataStatistics${err}`)
      })
    },
    getLatestTask() {
      let data = {
        province: this.province,
        city: this.city
      }
      latestTask(data).then(res => {
        if(res.result) {
          this.lastestTaskInfo = res.result;
          this.getTaskStatistics();
        }
      }).catch(err => {
        console.log(`latestTask${err}`)
      })
    },
    getTaskStatistics() {
      let data = {
        province: this.province,
        city: this.city,
        taskId: this.lastestTaskInfo.taskId
      }
      this.periodWarningData.value = [];
      taskStatistics(data).then(res => {
        if (res.result) {
          this.taskStatistics = res.result;
          this.taskStatistics.complete = res.result.completeCounty;
          this.taskStatistics.unComplete = res.result.unCompleteCounty;
          this.periodWarningData.value.push(Math.round(res.result.seniorMiddleSchoolWarningPercent * 100));
          this.periodWarningData.value.push(Math.round(res.result.juniorMiddleSchoolWarningPercent * 100));
          this.periodWarningData.value.push(Math.round(res.result.primarySchoolWarningPercent * 100));
          this.periodWarningData.male = Math.round(res.result.boyWarningPercent * 100);
          this.periodWarningData.female = Math.round(res.result.girlWarningPercent * 100);
          this.taskStatistics.completeNumbers = res.result.completeNumbers + '';
        }
      }).catch(err => {
        console.log(`taskStatistics${err}`)
      })
      taskProgress(data).then(res => {
        if (res.result.records) {
          this.cityProcess = res.result.records;
          this.cityProcess.forEach(item => {
            item.completePercent = (item.completePercent * 100)%1 == 0 ? item.completePercent * 100 + '%' : (item.completePercent * 100).toFixed(2) + '%';
            item.warnPercent = (item.warnPercent * 100)%1 == 0 ? item.warnPercent * 100 + '%' : (item.warnPercent * 100).toFixed(2) + '%';
          })
        }
      }).catch(err => {
        console.log(`schoolTaskProgress${err}`)
      })
    },
    getInterveneData() {
      let data = {
        province: this.province,
        city: this.city,
        msgLevel: 'city'
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
    // 获取区县进度
    getUnitProcess() {
      let data = {
        province: this.province,
        city: this.city
      };
      allUnitTaskProcess(data).then(res => {
        if(res.success) {
          res.result.sort((a, b) => b.percent - a.percent);
          this.mapShowData = res.result;
          this.topThree = this.mapShowData.slice(0, 3);
        }else {
          this.mapShowData = [];
        }
      }).catch(err => {
        console.log(err);
      })
    },
    // 地图点击事件
    mapClick(selectedName) {
      if(this.selectedCounty === selectedName) {
        // 这样的话是取消选中;
        this.selectedCounty = ''
      }else {
        this.selectedCounty = selectedName;
      }
      console.log(this.selectedCounty)
      if(this.selectedCounty) {
        console.log('heihei')
      }
      let data = {};
      if (this.selectedCounty == '') {
        data = {
          province: this.province,
          city: this.city,
          taskId: this.lastestTaskInfo.taskId
        }
      } else {
        data = {
          province: this.province,
          city: this.city,
          county: this.selectedCounty,
          taskId: this.lastestTaskInfo.taskId
        }
      }
      this.periodWarningData.value = [];
      taskStatistics(data).then(res => {
        if (Object.keys(res.result).length != 0) {
          this.taskStatistics = res.result;
          if (data.county != null) {
            this.taskStatistics.complete = res.result.completeSchool;
            this.taskStatistics.unComplete = res.result.unCompleteSchool;
          } else {
            this.taskStatistics.complete = res.result.completeCounty;
            this.taskStatistics.unComplete = res.result.unCompleteCounty;
          }
          this.periodWarningData.value.push(Math.round(res.result.seniorMiddleSchoolWarningPercent * 100));
          this.periodWarningData.value.push(Math.round(res.result.juniorMiddleSchoolWarningPercent * 100));
          this.periodWarningData.value.push(Math.round(res.result.primarySchoolWarningPercent * 100));
          this.periodWarningData.male = Math.round(res.result.boyWarningPercent * 100);
          this.periodWarningData.female = Math.round(res.result.girlWarningPercent * 100);
          this.taskStatistics.completeNumbers = res.result.completeNumbers + '';
        } else {
          this.taskStatistics = {
            completeNumbers: '0',
            complete: 0,
            unComplete: 0,
            completePercent: 0,
            validNumbers: 0,
            validPercent: 0,
            warnNumbers: 0,
            warnPercent: 0,
            boyWarningPercent: 0,
            girlWarningPercent: 0,
            juniorMiddleSchoolWarningPercent: 0,
            primarySchoolWarningPercent: 0,
            seniorMiddleSchoolWarningPercent: 0
          }
          this.periodWarningData = {
            label: ['职高','高中', '初中', '小学', '其他'],
            value: [0, 0, 0],
            max: 100,
            male: 0,
            female: 0, // 这里都用数字表示百分比
          }
        }
      }).catch(err => {
        console.log(`taskStatistics${err}`)
      })
    },
    urge(row) {

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
    goto(name) {
      this.$router.push({name: name});
    },
    getHotWordList() {
      getHotWordList().then(res => {
        if(res.success) {
          this.hotwords = res.result.map(v => {
            return {
              name: v.name,
              value: v.count
            }
          });
        }
      }).catch(err => {
        console.log('获取词云getHotWordList', 'error')
      })
    },
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
    height: 72%;
    display: flex;
    justify-content: space-between;
    .left {
      width: 24%;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }
    .warning-data {
      width: 100%;
      height: 62%;
      background: url("~@/assets/imgs/home-img/shujukanban.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
      .warning-data-box {
        box-sizing: border-box;
        padding: 15px;
        display: flex;
        justify-content: space-between;
        flex-direction: column;
        cursor: pointer;
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
          height: 48%;
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
        .title-common {
          height: 30px;
          width: 85%;
          flex: 0 0 auto;
          background: url("~@/assets/imgs/home-img/biaoti-duan.png") no-repeat 0 center;
          background-size: 100% 100%;
        }
        .remove-people {
          height: 24%;
          display: flex;
          flex-direction: column;
          justify-content: space-around;
        }
        .out-time {
          height: 24%;
          display: flex;
          flex-direction: column;
          justify-content: space-around;
        }
        .content {
          height: calc(100% - 35px);
          display: flex;
          justify-content: center;
          .data-box {
            width: 30%;
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
            width: calc(60% + 28px);
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
    .words {
      width: 100%;
      height: 36%;
      background: url("~@/assets/imgs/home-img/shujukanban.png") no-repeat;
      background-size: 100% 100%;
      position: relative;
    }
    .right {
      width: 75%;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      .right-top {
        height: 70%;
        display: flex;
        justify-content: space-between;
        .map-block {
          width: 63%;
          height: 100%;
          position: relative;
          .des {
            height: 60px;
            background: rgba(202, 159, 255, 0);
            border: 1px solid #8960BB;
            padding: 0 20px;
            position: relative;
            display: flex;
            flex-direction: column;
            justify-content: center;
            .des-c {
              height: 25px;
              line-height: 25px;
            }
            .font-open {
              position: absolute;
              right: 20px;
              color: #A36F04;
              cursor: pointer;
              &:hover {
                text-decoration: underline;
              }
            }
          }
          .map-area {
            height: calc(100% - 30px);
            position: relative;
            .map {
              height: 100%;
            }
          }
          .evaluator-num {
            position: absolute;
            top: 80px;
            left: 0;
            width: 100%;
            .title {
              font-size: 18px;
            }
            .num-block {
              margin-top: 10px;
              width: 100%;
              height: 5.24%;
              .num {
                display: inline-block;
                margin-right: 10px;
                width: 4.25%;
                height: 100%;
                background: linear-gradient(167deg, #FCB317, #AE66ED);
                font-size: 25px;
                font-weight: bold;
                color: #FFFFFF;
                text-align: center;
                line-height: 40px;
              }
            }
          }
          .ranking {
            width: 21.4%;
            height: 25%;
            background: rgba(60,27,111,0.6200);
            border: 1px solid #C8A3FF;
            position: absolute;
            bottom: 0;
            right: 0;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            .title {
              font-size: 20px;
              height: 30px;
            }
            .para {
              font-size: 16px;
              height: 25px;
            }
          }
        }
        .service-data-block {
          width: 35%;
          height: 100%;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
        }
        .service-count {
          height: 54%;
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
          height: 44%;
          background: url("~@/assets/imgs/home-img/shujukanban.png") no-repeat;
          background-size: 100% 100%;
          position: relative;
          .data-panel-box {
            display: flex;
            flex-wrap: wrap;
            text-align: center;
            justify-content: space-around;
            align-items: center;
            .data-box {
              width: 40%;
              height: 25%;
              flex: 0 0 auto;
              display: flex;
              justify-content: center;
              .img-bg {
                width: 100%;
                height: 100%;
                background: url("~@/assets/imgs/home-img/shujubeijing.png") no-repeat 50% 50%;
                background-size: 100% 100%;
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
      .task-progress-block {
        height: 28%;
        background: url("~@/assets/imgs/home-img/yujingjiankong.png") no-repeat;
        background-size: 100% 100%;
        position: relative;
        .progress-block {
          padding: 10px 15px;
          box-sizing: border-box;
          display: flex;
          justify-content: space-around;
          .progress-box {
            width: 23%;
            flex: 0 0 auto;
            position: relative;
            display: flex;
            justify-content: center;
            flex-direction: column;
            .title {
              height: 30px;
              width: 90%;
              flex: 1 0 auto;
              background: url("~@/assets/imgs/home-img/biaoti-chang.png") no-repeat 0 center;
              background-size: 100% 100%;
              .title-font {
                font-size: 16px;
                margin-left: 12px;
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
                width: 60%;
                position: absolute;
                top: 0;
                left: 8px;
              }
              .info {
                width: 35%;
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
            .polar-bar {
              width: 100%;
              height: 90%;
              margin-top: -10%;
            }
            .sex-radio {
              height: 15%;
              position: absolute;
              width: 100%;
              border-radius: 13px 13px 13px 13px;
              overflow: hidden;
              display: flex;
              font-size: 14px;
              bottom: 5%;
              .male {
                height: 100%;
                background-color: #6977E4;
                display: flex;
                align-items: center;
                justify-content: center;
              }
              .female {
                height: 100%;
                background-color: #BC90F3;
                display: flex;
                align-items: center;
                justify-content: center;
              }
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
  .warning-table {
    height: 21%;
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
      display: flex;
      align-items: center;
      .location {
        color: #FDB316;
      }
      i {
        margin-left: 10px;
      }
      .province {
        font-size: 16px;
      }
      .city {
        font-size: 13px;
      }
    }
    .box-content {
      height: 89%;
    }
  }
}
</style>