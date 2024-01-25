<template>

  <div class="group-report-container" v-loading.fullscreen.lock="loading">
    <div class="search-container" v-for="box in searchBoxNum" :key="'pbox' + box.id">
      <div v-for="item in box.boxList" :key="item.key" class="search-box">
        <template v-if="item.type === 'input'">
          <span>{{item.chName}}&nbsp;</span>
          <el-input class="input" :placeholder="'请输入' + item.chName" clearable
                    v-model="searchForm[item.key]" size="medium">
          </el-input>
        </template>
        <template v-if="item.type === 'date'">
          <span style="width: 120px">{{item.chName}}&nbsp;</span>
          <el-date-picker v-model="searchForm[item.key]" type="date" placeholder="选择生成时间" value-format="yyyy-MM-dd">
          </el-date-picker>
        </template>
  </div>
    </div>
    <div class="search-btn" style="width: 30%">
      <el-button type="primary" size="medium" @click="search">搜索</el-button>
      <el-button type="primary" size="medium" @click="addDialog()" style="margin: 0 10px">生成团体报告</el-button>
    </div>
    <div class="content-container">
      <el-table :data="reportTableData" :stripe="true" :border="true">
        <el-table-column v-for="item in tableColumnInfo.filter(v => v.role.includes(level))" :key="item.prop"
                         align="center"
                         :min-width="item.minWidth"
                         :prop="item.prop"
                         :label="item.label">
        </el-table-column>
        <el-table-column align="center" label="操作" width="150px">
          <template slot-scope="scope">
            <el-button size="mini" @click="viewReport(scope.row)">查看</el-button>
            <el-button size="mini"  @click="exportPDF(scope.row)" style="margin: 0 10px">下载</el-button>
</template>
        </el-table-column>
      </el-table>
      <div class="page-block">
        <el-pagination background
                       layout="total, prev, pager, next"
                       :total="total"
                       :current-page="curPage"
                       :page-size='pageSize'
                       @current-change="pageChange">
        </el-pagination>
      </div>
    </div>

    <template v-if="this.level <= 3">
        <!-- 添加用户的对话框 -->
        <el-dialog title="生成团体报告" :visible.sync="groupReport.addDialog" width="30%">
          <!-- 内容主体区域 -->
          <el-form :model="groupReport.ruleForm" :rules="groupReport.rules" ref="addFormRef" label-width="90px">
            <el-form-item label="报告名称:" prop="reportName">
              <el-input v-model="groupReport.ruleForm.reportName" placeholder="请输入报告名称"></el-input>
            </el-form-item>
            <el-form-item label="测评任务:" prop="textTask">
              <el-select v-model="groupReport.ruleForm.textTask" placeholder="请选择测评任务"  @change="textTaskSelect">
                <el-option v-for="(item,index)  in tasksData" :key="item.id" :value="item">{{item.name}}</el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="测评量表:" prop="textScale">
              <el-select v-model="groupReport.ruleForm.textScale" placeholder="请选择测评量表"   @change="textScaleSelect">
                <el-option v-for="(item,index)  in scaleList" :key="item.id" :value="item">{{item.name}}</el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="报告范围" prop="reportScope">
              <el-cascader v-model="groupReport.ruleForm.reportScope" :options="formOptions.reportScope.options" :props="formOptions.reportScope.props"
                           @change="getRangeOptions"
                           collapse-tags
                           clearable>
              </el-cascader>
            </el-form-item>
            <!-- 底部区域 -->
            <el-form-item class="dialog-footer">
              <el-button type="primary" @click="createGroupOk('addFormRef')">确 定</el-button>
              <el-button @click="createGroupCancle()">取 消</el-button>
            </el-form-item>
          </el-form>
        </el-dialog>
    </template>
    <template v-if="this.level == 4">
      <!-- 添加用户的对话框 -->
      <el-dialog title="生成团体报告" :visible.sync="groupReport.addDialog4" width="30%">
        <!-- 内容主体区域 -->
        <el-form :model="groupReport.ruleForm" :rules="groupReport.rules" ref="addFormRef2" label-width="90px">
          <el-form-item label="报告名称:" prop="reportName">
            <el-input v-model="groupReport.ruleForm.reportName" placeholder="请输入报告名称"></el-input>
          </el-form-item>
<!--          <el-form-item label="选择学校:" prop="textSchool">
            <el-select v-model="groupReport.ruleForm.textSchool" placeholder="请选择学校"  @change="schoolSelect">
              <el-option v-for="(item,index)  in schoolData" :key="item.id" :value="item">{{item.name}}</el-option>
            </el-select>
          </el-form-item>-->
          <el-form-item label="选择学段:" prop="textPeriod">
            <el-radio-group v-model="groupReport.ruleForm.textPeriod" >
              <el-radio label="primarySchool" @change="primarySchoolSelect">小学</el-radio>
              <el-radio label="juniorHighSchool" @change="juniorHighSchoolSelect">初中</el-radio>
              <el-radio label="highSchool" @change="highSchoolSelect">高中</el-radio>
              <el-radio label="teacher" @change="teacherSelect">教师</el-radio>
            </el-radio-group>
          </el-form-item>

<!--          <el-form-item label="选择年级:" prop="textGrade">
            <el-select v-model="groupReport.ruleForm.textGrade" placeholder="请选择年级"  @change="textGradeSelect">
              <el-option v-for="(item,index)  in gradesData" :key="item.id" :value="item">{{item.name}}</el-option>
            </el-select>
          </el-form-item>-->

          <el-form-item label="测评任务:" prop="textTask">
            <el-select v-model="groupReport.ruleForm.textTask" placeholder="请选择测评任务"  @change="textTaskSelect">
              <el-option v-for="(item,index)  in tasksData" :key="item.id" :value="item">{{item.name}}</el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="测评量表:" prop="textScale">
            <el-select v-model="groupReport.ruleForm.textScale" placeholder="请选择测评量表"   @change="textScaleSelect">
              <el-option v-for="(item,index)  in scaleList" :key="item.id" :value="item">{{item.name}}</el-option>
            </el-select>
          </el-form-item>
          <!-- 底部区域 -->
          <el-form-item class="dialog-footer">
            <el-button type="primary" @click="createGroupOk('addFormRef2')">确 定</el-button>
            <el-button @click="createGroupCancle(4)">取 消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>

    </template>
    
    <div style="visibility:hidden;position:absolute;width:100%;height:100%;">
    <!-- <div style="position:absolute;width:100%;height:100%;margin:0;padding:0;"> -->
    <iframe :src="groupReportURL" width="100%"></iframe>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import {
  getAllCity,
  getAllClasses,
  getAllCounty,
  getAllSchool,
  getClasses,
  getSchoolRoleData
} from '@/api/organization'
import {addGroupReport, getGroupReportList} from '@/api/reportOrganization.js'
import { mapGetters } from 'vuex'
import {getSchoolTasks} from "@/api/task";
import {getScaleList} from "@/api/scale";
import exportPDF from "../../report-page/school/group";
import request from "@/utils/request";
import {getAddress, getBaseHost} from "@/api/config";


export default {
  name: 'GroupReport',
  components: {
    // Report,
    // 'school-report': Report
    // 'school-report': () => import('../../report-page/school/group')
  },
  computed: {
    ...mapGetters([
      'extra',
      'province',
      'city',
      'county',
      'level',
      'role',
      'userId',
      'name',


    ]),
    searchBoxNum() {
      const arr = this.searchBox.filter(val => val.role.includes(this.level));
      const num = Math.ceil(arr.length / 4);
      return new Array(num).fill(0).map((v, i) => {
        return {
          id: i,
          boxList: arr.slice(i * 4, (i + 1) * 4)
        }
      });
    }
  },
  data() {
    return {
      groupReportURL: "",
      loading: false,
      taskId:'',
      scaleId:'',
      tasksData:[],
      scaleList: [],
      schoolData:[],
      gradesData:[],
      searchForm: {
        reportName: '',
        scaleName: '',
        createTime: '',
      },
      searchBox: [ {
        key: 'reportName',
        chName: '报告名称:',
        type: 'input',
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'scaleName',
        chName: '量表名称:',
        type: 'input',
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'createTime',
        chName: '生成时间:',
        type: 'date',
        options: [],
        role: [2, 3, 4, 5, 6],
      }],
      tableColumnInfo: [{
        prop: 'reportName',
        label: '报告名称',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '90px'
      }, {
        prop: 'school',
        label: '学校',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '90px'
      }, {
        prop: 'period',
        label: '学段',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '40px'

      }, {
        prop: 'scale',
        label: '量表',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '80px'

      }, {
        prop: 'createTime',
        label: '生成日期',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '80px'
      }, {
        prop: 'createPerson',
        label: '生成人',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '90px'
      },],
      reportTableData: [],
      // 分页
      total: 0,
      curPage: 1,
      pageSize: 10,

      gradeOptions: {
        '小学': ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
        '初中': ['一年级', '二年级', '三年级'],
        '高中': ['一年级', '二年级', '三年级'],
        '中职': ['一年级', '二年级', '三年级'],
        '职高': ['一年级', '二年级', '三年级'],
        '其他': ['一年级', '二年级', '三年级'],
      },
      formOptions: {
        reportScope: {
          options: [],
          props: {multiple: true},
          isAll: false,
        }
      },
      groupReport:{
        addDialog: false,
        addDialog4: false,
        period:'',
        ruleForm:{
          reportName:"",
          textTask:"",
          textScale:"",
          textSchool:"",
          textPeriod:"",
          textGrade:"",
          reportScope:[],
        },
        rules: {
          reportName: [
            {
              required: true,
              message: "请输入报告名称",
              trigger: "blur",
            },
          ],
          textTask: [
            {
              required: true,
              message: "请输入测评任务",
              trigger: "change",
            },
          ],
          textScale: [
            {
              required: true,
              message: "请输入测评量表",
              trigger: "change",
            },
          ],
          reportScope: [
            {
              required: true,
              message: "请选择报告范围",
              trigger: "change",
            },
          ],
          textSchool: [
            {
              required: true,
              message: "请输入学校",
              trigger: "change",
            },
          ],
          textPeriod: [
            {
              required: true,
              message: "请选择学段",
              trigger: "change",
            },
          ],
          textGrade: [
            {
              required: true,
              message: "请选择年级",
              trigger: "change",
            },
          ],
        }
      }
    }
  },
  async mounted() {
    this.getGroupReportList();
    if(this.level <= 3) {
      const allClasses = await this.getAllClasses();
      this.formOptions.reportScope.options = this.extra.period.map(v => {
        return {
          value: v,
          label: v,
          children: this.gradeOptions[v].map(g => {
            let child = [];
            if(allClasses.hasOwnProperty(v + g)) {
              child = allClasses[v + g];
            }
            return {
              value: g,
              label: g,
              children: child.map(c => {
                return {
                  value: c,
                  label: c
                }
              })
            }
          })
        }
      });
    }else {
      this.formOptions.reportScope.options = [{
        value: '其他',
        label: '其他',
        children: this.gradeOptions['其他'].map(v => { return { value: v, label: v } })
      },{
        value: '职高',
        label: '职高',
        children: this.gradeOptions['职高'].map(v => { return { value: v, label: v } })
      },{
        value: '小学',
        label: '小学',
        children: this.gradeOptions['小学'].map(v => { return { value: v, label: v } })
      }, {
        value: '初中',
        label: '初中',
        children: this.gradeOptions['初中'].map(v => { return { value: v, label: v } })
      },{
        value: '高中',
        label: '高中',
        children: this.gradeOptions['高中'].map(v => { return { value: v, label: v } })
      },{
        value: '中职',
        label: '中职',
        children: this.gradeOptions['中职'].map(v => { return { value: v, label: v } })
      }]
    };
    this.getTasksData();
    this.getScaleList();
    this.getSchools();
  },
  methods: {
    getClasses(val) {
      if(val) {
        const data = {
          schoolId: this.extra.schoolId,
          period: this.searchForm.period,
          grade: this.searchForm.grade,
        }
        getClasses(data).then(res => {
          this.searchBox.filter(val => val.key == 'classes')[0].options = [...new Set(res.result)];;
          this.searchForm.classes = '';
        })
      }
    },
    async getAllClasses() {
      const data = {
        schoolId: this.extra.schoolId,
      }
      try {
        let res = await getAllClasses(data);
        if(res.success) {
          return res.result;
        }
      }catch (err) {
        console.log("getAllClasses:"+err)
      }
    },
    getGroupReportList() {
      this.loading = true;
      const data = {
        reportName: this.searchForm.reportName,
        textScale:this.searchForm.scaleName,
        createTime:  this.searchForm.createTime,
        schoolId: this.extra.schoolId,
        userId: this.userId,
        pageNum: this.curPage,
        pageSize: this.pageSize,
        level: this.level,
        county: this.county,
      }
     /* if(this.level <= 5) {
        data.city = this.city;
      }
      if(this.level <= 4) {
        data.county = this.county;
      }
      if(this.level <= 3) {
        data.schoolId = this.extra.schoolId;
      }
      if (this.level <= 2) {
        data.teaRole = this.role;
        data.level = this.level;
        data.chargeClasses = this.extra.classes;
      }*/
      getGroupReportList(data).then(res => {
        this.loading = false;
        if(res.success && res.result) {
          this.total = res.result.total;
          this.reportTableData = res.result.records.map(v => {
            const {id,reportName, scale, period, createTime, createPerson,taskId,schoolId,school} = v;
            const data = {id,reportName, scale, period, createTime, createPerson,taskId,schoolId,school};
            data.reportName = v.name;
            data.scale = v.scaleName;
            data.period = v.period;
            data.createTime = v.createTime;
            data.createPerson = v.createPerson;
            data.taskId = v.taskId;
            data.scaleId = v.scaleId;
            data.schoolId = v.schoolId;
            data.userId = this.userId;
            data.id = v.id;
            return data;
          });
        }else {
          this.reportTableData = [];
          this.total = 0;
        }
      }).catch(res => {
        this.loading = false;
      })
    },
    async getRangeOptions() {
      let period = this.groupReport.ruleForm.reportScope.map(v => v[0]);
      period = Array.from(new Set(period));
      let rangeOptions = [];
      if(this.level === 6) {
        let allCity = await getAllCity({ province: this.province});
        allCity = allCity.result
        rangeOptions = [{
          value: this.province,
          label: this.province,
          children: allCity
        }]
        const cityArr = rangeOptions[0].children;
        for(let i = 0; i < cityArr.length; i++) {
          let countyList = await getAllCounty({
            province: this.province,
            city: cityArr[i]
          })
          countyList = countyList.result;
          cityArr[i] = {
            value: cityArr[i],
            label: cityArr[i],
            children: countyList
          }
          for(let j = 0; j < countyList.length; j++) {
            let schoolList = await getAllSchool({
              province: this.province,
              city: cityArr[i].value,
              county: countyList[j],
              period: period
            })
            schoolList = schoolList.result.map(v => { return { value: `${v.id},${v.name}`, label: v.name } });
            cityArr[i].children[j] = {
              value: cityArr[i].children[j],
              label: cityArr[i].children[j],
              children: schoolList
            }
          } //for-j
        } // for-i
      }else if(this.level === 5) {
        let allCounty = await getAllCounty({
          province: this.province,
          city: this.city
        })
        allCounty = allCounty.result.map(v => {  return { value: v, label: v} })
        rangeOptions = [{
          value: this.city,
          label: this.city,
          children: allCounty
        }]
        for(let i = 0; i < allCounty.length; i++) {
          let schoolList = await getAllSchool({
            province: this.province,
            city: this.city,
            county: allCounty[i].label,
            period: period
          })
          schoolList = schoolList.result.map(v => { return { value: `${v.id},${v.name}`, label: v.name } });
          rangeOptions[0].children[i].children = schoolList;
        }
      }else if(this.level === 4) {
        let schoolList = await getAllSchool({
          province: this.province,
          city: this.city,
          county: this.county,
          period: period
        })
        schoolList = schoolList.result.map(v => { return { value: `${v.id},${v.name}`, label: v.name } });
        rangeOptions = [{
          value: this.county,
          label: this.county,
          children: schoolList
        }]
      }
    },
    getViewReportURL(row) {
let { id,reportName, scale, period, createTime, createPerson,taskId,scaleId,school,schoolId } = row;
      const data = { id,reportName, scale, period, createTime, createPerson,taskId,scaleId,school,schoolId };
      data.type = 'schoolGroup';
      data.userId = this.userId;
      data.taskId = taskId;
      data.scaleId = scaleId;
      data.schoolId = this.extra.schoolId;
      data.level = this.level;
      let routeUrl = '';
      if(this.level==4){
          routeUrl = this.$router.resolve({
          path: '/countyGroupReport',
          query: data
        })
      }else{
          routeUrl = this.$router.resolve({
          path: '/groupReport',
          query: data
        })
      }
      return routeUrl
    },
    viewReport(row) {
      let routeUrl = this.getViewReportURL(row);
      window.open(routeUrl.href, "_blank");
      // window.open(routeUrl.href, "_self");
    },
    pageChange(page) {
      this.curPage = page;
      this.getGroupReportList();
    },
    search() {
      this.curPage = 1;
      this.getGroupReportList();
    },
    message (param) {
      Vue.prototype.$message({
        message: param,
        title:'提示',
        type: "warning",
      })
    },
    createGroupCancle(f) {
      if(f==4){
        this.groupReport.addDialog4 = false;
        this.groupReport.ruleForm.textSchool = "";
        this.$refs["addFormRef2"].resetFields();
      }else {
        this.$refs["addFormRef"].resetFields();
        this.groupReport.addDialog = false;
      }
      this.groupReport.ruleForm.reportName = "";
      this.groupReport.ruleForm.textTask = "";
      this.groupReport.ruleForm.textGrade = "";
      this.groupReport.ruleForm.textScale = "";
      this.groupReport.ruleForm.reportScope = "";
    },
    createGroupOk(addFormRef) {
      if(this.level<4){
        this.groupReport.ruleForm.textSchool=this.extra.school;
      }

      this.$refs[addFormRef].validate((valid) => {
        if (valid) {
          const data = JSON.parse(JSON.stringify(this.groupReport.ruleForm));
          let params = {
            reportName: this.groupReport.ruleForm.reportName,
            textTask:this.groupReport.ruleForm.textTask,
            textSchool:this.groupReport.ruleForm.textSchool,
            period:this.groupReport.period,
            textGrade:this.groupReport.ruleForm.textGrade,
            textScale: this.groupReport.ruleForm.textScale,
            reportScope:  this.groupReport.ruleForm.reportScope,
            schoolId: this.extra.schoolId,
            taskId: this.taskId,
            scaleId:this.scaleId,
            userId: this.userId,
            createPerson: this.name,
            level: this.level,
            county:this.county,
          };

          if(this.level <= 3){
            params.classes=data.reportScope.map(v => v.join('')).join(',');
          }else if(this.level>=4){
            params.schoolId = this.textSchool;
          }
          addGroupReport(params).then(res => {
            this.loading = false;
            this.$message.success(res.message);
            this.curPage = 1;
            this.createGroupCancle(this.level);//弹窗清空
            this.getGroupReportList();
            this.dialogVisible = false;
          }).catch(() => {
            this.loading = false;
          })
        }
      });
    },
    getTasksData() {
      if(this.level>=4){
        const data = { county: this.county };
        this.getCountyTasks(data).then(res => {
          res.result.forEach(val => {
            this.tasksData.push({"id":val.id,"name":val.name})
          })
        }).catch();
      }else{
        const data = { schoolId: this.extra.schoolId };
        getSchoolTasks(data).then(res => {
          res.result.forEach(val => {
            this.tasksData.push({"id":val.id,"name":val.name})
          })
        }).catch();
      }
    },
    getScaleList() {
      const data = {userId: this.userId,pageNum: 1,
        pageSize: -1,}
      getScaleList(data).then(res => {
        if(res.success) {
          this.scaleList = res.result.records;
        }
      }).catch()
    },
    textTaskSelect(val) {
      if(val) {
        this.groupReport.ruleForm.textTask=val.name;
        this.taskId = val.id;
      }
    },
    textScaleSelect(val) {
      if(val) {
        this.groupReport.ruleForm.textScale=val.name;
        this.scaleId = val.id;
      }
    },
    textGradeSelect(val) {
      if(val) {
        this.groupReport.ruleForm.textGrade=val.name;
      }
    },
    exportPDF(row){
      let routeUrl = this.getViewReportURL(row).href
      let link = document.createElement('a');
      link.style.display = 'none';
      link.href = getBaseHost() + "/node/genpdf" + routeUrl;
      document.body.appendChild(link);
      link.click();
    },
    addDialog(){
      if(this.level < 4){
        this.groupReport.addDialog = true
      }else if(this.level == 4){
        this.groupReport.addDialog4 = true
      }

    },
    getSchools() {
      const data = {county: this.county}
      this.getSchoolsByCounty(data).then(res => {
        res.result.forEach(val => {
          this.schoolData.push({"id":val.id,"name":val.name})
        })
      }).catch()
    },
    schoolSelect(val) {
      if(val) {
        this.groupReport.ruleForm.textSchool=val.name;
        this.textSchool = val.id;
      }
    },
    primarySchoolSelect(){
      this.gradesData=[];
      this.gradesData.push({"id":1,"name":'一年级'});
      this.gradesData.push({"id":2,"name":'二年级'});
      this.gradesData.push({"id":3,"name":'三年级'});
      this.gradesData.push({"id":4,"name":'四年级'});
      this.gradesData.push({"id":5,"name":'五年级'});
      this.gradesData.push({"id":6,"name":'六年级'});
      this.groupReport.period='小学';
    },
    juniorHighSchoolSelect(){
      this.gradesData=[];
      this.gradesData.push({"id":7,"name":'一年级'});
      this.gradesData.push({"id":8,"name":'二年级'});
      this.gradesData.push({"id":9,"name":'三年级'});
      this.groupReport.period='初中';
    },
    highSchoolSelect(){
      this.gradesData=[];
      this.gradesData.push({"id":10,"name":'一年级'});
      this.gradesData.push({"id":11,"name":'二年级'});
      this.gradesData.push({"id":12,"name":'三年级'});
      this.groupReport.period='高中';
    },
    teacherSelect(){
      this.gradesData=[];
    },
    getSchoolsByCounty(data) {
      return request({
        url: getAddress() + 'groupReport/getSchoolsByCounty',
        method: 'post',
        data
      })
    },
    getCountyTasks(data) {
      return request({
        url: getAddress() + 'groupReport/getCountyTasks',
        method: 'post',
        data
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";

.group-report-container {
  @include container;
  // .search-container {
  //   justify-content: flex-start;
  //   .search-box {
  //     width: 20%;
  //     &:last-of-type {
  //       justify-content: center;
  //     }
  //   }
  // }
  .dialog-footer {
    margin-top: 40px;
    margin-left: -60px;
  }
}
</style>
<style lang="scss">
.group-report-container {
  .el-dialog__body {
    height: 40vh;
    overflow: auto;
  }
  .content-container {
    .el-date-editor.el-input {
      width: auto;
    }
    .input-with-select .el-input-group__prepend {
      background-color: #fff;
    }
    .el-select .el-input {
      width: 80px;
    }
  }

  .search-box {
    .el-date-picker {
      width: 220px;
    }
  }
}
</style>