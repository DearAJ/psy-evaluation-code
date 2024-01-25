<template>
  <div class="warning-database-container" 
    v-loading.fullscreen.lock="loading">
    <div class="search-container" v-for="box in searchBoxNum" :key="'box' + box.id">
      <div v-for="item in box.boxList" :key="item.key" class="search-box">
        <template v-if="item.type === 'input'">
          <span>{{item.chName}}&nbsp;</span>
          <el-input class="input" :placeholder="'请输入' + item.chName" clearable
            v-model="searchForm[item.key]">
          </el-input>
        </template>
        <template v-else-if="item.key === 'stuPeriod'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.stuPeriod" placeholder="请选择学段" 
            clearable  
            @change="getClasses">
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'stuGrade'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.stuGrade" :placeholder="searchForm.stuPeriod ? '请选择年级' : '请先选择学段'" 
            clearable  
            @change="getClasses">
            <el-option v-for="o in item.options[searchForm.stuPeriod]" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'stuClass' && item.type === 'select'" >
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.stuClass" :placeholder="searchForm.stuClass ? '请选择班级' : '请先选择年级'" 
            clearable  >
            <template v-if="searchForm.stuGrade != ''">
              <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
            </template>
          </el-select>
        </template>
        <template v-else-if="item.type === 'select'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm[item.key]" :placeholder="'请选择' + item.chName" clearable>
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
      </div>
    </div>
     <div class="btn-container">
      
      <el-popconfirm title="点击确认将导出已勾选内容" @confirm="exportWarningStudents">
        <el-button slot="reference" type="primary" size="medium">导出</el-button>
      </el-popconfirm>
      
    </div>
    <div class="search-btn">
      <el-button type="primary" @click="search" size="medium">搜索</el-button>
    </div>
    <div class="content-container">
      <el-table :data="warningData" border
        :stripe="true">
        
          <el-table-column width="30" align="center">
            <template slot="header" slot-scope="scope">
              <el-checkbox v-model="isSelectedAll" @change="selectAll(scope)"></el-checkbox>
            </template>
            <template slot-scope="scope">
               <el-checkbox v-model="scope.row.selected" @change="handleChange(scope.row)"></el-checkbox>
            </template>
          </el-table-column>
        <el-table-column v-for="item in tableColumnInfo.filter(v => v.role.includes(level))"
          align="center"
          :min-width="item.minWidth"
          :key="item.prop"
          :prop="item.prop"
          :label="item.label">
        </el-table-column>
        <el-table-column align="center" width="90" label="操作">
          <template slot-scope="scope">
            <el-button v-if="level <= 3" size="mini" @click="intervene(scope.row)">干预</el-button>
            <el-button v-else size="mini" @click="intervene(scope.row)">查看</el-button>
          </template>
        </el-table-column><el-table-column align="center" label="操作" width="65px">
          <template slot-scope="scope">
            <el-button size="mini" @click="generateReport(scope.row)">查看</el-button>
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
    <confirm-crisis-level ref="confirmCrisisLevelDialog" @submit="openInterveneListDialog" 
      @closeDialog="getWarningData"></confirm-crisis-level>
    <intervention-record-list ref="InterventionRecordListDialog" @closeDialog="getWarningData"
      @addPlan="addPlan"
      @addRecord="addRecord"
      @checkDetails="checkDetails"
      @updateDetails="updateDetails">
    </intervention-record-list>
    <intervene-plan ref="IntervenePlan" @closeDialog="getWarningData"
      @submit="openInterveneListDialog"></intervene-plan>
    <plan-details ref="PlanDetails" @closeDialog="getWarningData"></plan-details>
    <record-details ref="RecordDetails" @closeDialog="getWarningData"></record-details>
  </div>
</template>

<script>
import { getWarningData, getSaveRecordData } from "@/api/psyInterveneTask.js"
import { getClasses } from '@/api/organization'
import { mapGetters } from 'vuex'

// import dateFormat from '@/utils/dateFormat'
import dateFormat from "dateformat"
import ConfirmCrisisLevel from '@/components/dialog/confirmCrisisLevel/index.vue'
import InterventionRecordList from '@/components/dialog/interveneRecordList/index.vue'
import IntervenePlan from '@/components/dialog/intervenePlan/index.vue'
import PlanDetails from '@/components/dialog/planDetails/index.vue'
import RecordDetails from '@/components/dialog/recordDetails/index.vue'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'WarningDatabase',
  components: {
    ConfirmCrisisLevel,
    InterventionRecordList,
    IntervenePlan,
    PlanDetails,
    RecordDetails
  },
  data() {
    return {
      semesterList: [],
      loading: false, 
      searchForm: {
        city: '',
        county: '',
        school: '',
        stuName: '',
        stuPeriod: '',
        stuGrade: '',
        stuClass: '',
        crisisLevel: '', // 危机等级
        stuState: '', // 当前状态
        isTransfer: '', // 是否转介
        postProcessing: '', // 去向/处理
      },
      //记录搜索表单都是啥，以省市区公用
      searchBox: [{
        key: 'city',
        chName: '市级',
        type: 'input',
        role: [ 6 ],
      }, {
        key: 'county',
        chName: '区县',
        type: 'input',
        role: [ 5, 6 ],
      }, {
        key: 'schoolName',
        chName: '学校',
        type: 'input',
        role: [ 4, 5, 6 ],
      }, {
        key: 'stuName',
        chName: '姓名',
        type: 'input',
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'stuPeriod',
        chName: '学段',
        type: 'select',
        options: [],
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'stuGrade',
        chName: '年级',
        type: 'select',
        options: {
          '小学': ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
          '初中': ['一年级', '二年级', '三年级'],
          '高中': ['一年级', '二年级', '三年级'],
          '职高': ['一年级', '二年级', '三年级'],
          '其他': ['一年级', '二年级', '三年级']
        },
        role: [2, 3, 4, 5, 6],
      }, 
      // {
      //   key: 'stuClass',
      //   chName: '班级',
      //   type: 'select',
      //   options: [],
      //   role: [2, 3],
      // },
      {
        key: 'stuClass',
        chName: '班级',
        type: 'input',
        role: [4, 5, 6, 2, 3],
      }, {
        key: 'crisisLevel',
        chName: '危机等级',
        type: 'select',
        options: ['一级预警（一般心理危机）', '二级预警（严重心理危机）', '三级预警（重大心理危机）', '无危机'],
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'stuState',
        chName: '当前状态',
        type: 'select',
        options: ['在校', '请假在家', '休学'],
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'isTransfer',
        chName: '是否转介',
        type: 'select',
        options: ['是', '否'],
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'postProcessing',
        chName: '去向/处理',
        type: 'select',
        options: ['住院治疗', '服药', '外部心理机构心理咨询（治疗）', '在校咨询', '其他'],
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'semester',
        chName: '所属学期',
        type: 'select',
        options: this.semesterList,
        role: [2, 3, 4, 5, 6],
      }], 
      tableColumnInfo: [{
        prop: 'city',
        label: '市级',
        role: [ 6 ],
        minWidth: '50px'
      }, {
        prop: 'county',
        label: '区县',
        role: [ 5, 6 ],
        minWidth: '50px'
      }, {
        prop: 'schoolName',
        label: '学校',
        role: [ 4, 5, 6 ]
      }, {
        prop: 'stuPeriod',
        label: '学段',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '50px'
      }, {
        prop: 'stuGrade',
        label: '年级',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '50px'
      }, {
        prop: 'stuClass',
        label: '班级',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '40px'
      }, {
        prop: 'taskName',
        label: '任务名称',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '90px'
      }, {
        prop: 'stuName',
        label: '姓名',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }, {
        prop: 'createTime',
        label: '更新时间',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '110px'
      }, {
        prop: 'expireTime',
        label: '下次干预截止时间',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '110px'
      }, {
        prop: 'psyTeacherName',
        label: '心理老师',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '70px'
      }, {
        prop: 'crisisLevel',
        label: '危机等级',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '90px'
      }, {
        prop: 'stuState',
        label: '当前状态',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '50px'
      }, {
        prop: 'isTransfer',
        label: '是否转介',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '30px'
      }, {
        prop: 'postProcessing',
        label: '去向/处理',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      },{
        prop: 'semester',
        label: '所属学期',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }, ],
      warningData: [],
      selectedStudents: [],
      // 与分页相关的
      
      isSelectedAll: false,

      total: 0,
      curPage: 1,
      pageSize: 10,
      curRow: {}
    }
  },
  computed: {
    ...mapGetters([
      'extra',
      'level',
      'province', 
      'city', 
      'county',
      'userId',
      'name'
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
  mounted() {
    for (let i = 0; i < 50; i++) {
      this.semesterList.push(2010 + i + "年春季学期"),
      this.semesterList.push(2010 + i + "年秋季学期")
    }
    this.searchBox.forEach(x => {
      console.log(x)
      if(x.key == 'semester'){
        x.options = this.semesterList
      }
    })
    if(this.level <= 3) {
      this.searchBox.filter(val => val.key == 'stuPeriod')[0].options = this.extra.period;
    }else {
      this.searchBox.filter(val => val.key == 'stuPeriod')[0].options = ['小学', '初中', '高中', '职高', '其他'];
    }
    this.getWarningData()
  },
  methods: {
    
    generateReport(row) {
      // console.log(row)
      // let { userId, taskId, scaleName, name, studentId, classes, grade } = row;
      const data = { 
        "userId": row.userId,
        "taskId": row.taskId, 
        "scaleName": row.scaleName, 
        "name": row.stuName, 
        "studentId": row.stuId, 
        "classes": row.stuClass, 
        "grade": row.stuGrade
      };
      // console.log(data)
      data.school = this.level <= 3 ? this.extra.school : row.school;
      data.type = 'personal';
      data.identity = 'student';
      const routeUrl = this.$router.resolve({
        path: '/report',
        query: data
      })
      window.open(routeUrl.href, "_blank");
    },
    handleChange(row) {
      if(row.selected) {
        this.selectedStudents.push(row);
      }else {
        let index = this.selectedStudents.indexOf(row);
        this.selectedStudents.splice(index, 1);
      }
    },
    selectAll(val) {
      // 如果点击全选，则将所有的数据都放入selectedStudents中
      if(this.isSelectedAll) {
        this.selectedStudents = this.warningData.slice();
        this.warningData.forEach(val => {
          val.selected = true;
        })
      }else {
        this.selectedStudents = [];
        this.warningData.forEach(val => {
          val.selected = false;
        })
      }
    },
    search() {
      // this.curPage = 1;
      this.getWarningData();
      console.log(this.warningData);
    },
    getWarningData() {
      this.loading = true;
      console.log(this.curPage);
      const data = {
        pageNum: this.curPage,
        pageSize: this.pageSize,
        province: this.province,
        userId: this.userId,
        mode: 'warn'
      };
      if(this.level === 6) {
        data.msgLevel = 'province';
      }else if(this.level === 5) {
        data.city = this.city;
        data.msgLevel = 'city'
      }else if(this.level === 4) {
        data.city = this.city;
        data.county = this.county;
        data.msgLevel = 'county'
      }else if(this.level === 3 || this.level === 2) {
        data.city = this.city;
        data.county = this.county;
        data.schoolName = this.extra.school;
        data.schoolId = this.extra.schoolId;
        data.msgLevel = 'school'
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          if(key === 'isTransfer') {
            data[key] = this.searchForm[key] === '是' ? true : false;
          }else {
            data[key] = this.searchForm[key];
          }
        }
      }
      getWarningData(data).then(res => {
        this.loading = false;
        if(res.success && res.result.records) {
          this.total = res.result.total;
          res.result.records.forEach(v => {
            v.isTransfer = v.isTransfer ? '是' : '否',
            v.selected = this.isSelectedAll
          });
          this.warningData = res.result.records
        }else if(!res.result.records) {
          this.warningData = [];
        }
      }).catch(err => {
        this.loading = false;
        this.warningData = [];
        this.total = 0;
      })
    },
    async getAllWarningData() {
      this.loading = true;
      console.log(this.curPage);
      const data = {
        pageNum: this.curPage,
        pageSize: -1,
        province: this.province,
        userId: this.userId,
        mode: 'warn'
      };
      if(this.level === 6) {
        data.msgLevel = 'province';
      }else if(this.level === 5) {
        data.city = this.city;
        data.msgLevel = 'city'
      }else if(this.level === 4) {
        data.city = this.city;
        data.county = this.county;
        data.msgLevel = 'county'
      }else if(this.level === 3 || this.level === 2) {
        data.city = this.city;
        data.county = this.county;
        data.schoolName = this.extra.school;
        data.schoolId = this.extra.schoolId;
        data.msgLevel = 'school'
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          if(key === 'isTransfer') {
            data[key] = this.searchForm[key] === '是' ? true : false;
          }else {
            data[key] = this.searchForm[key];
          }
        }
      }
      try{
      const res = await getWarningData(data);

        this.loading = false;
        if(res.success && res.result.records) {
          res.result.records.forEach(v => {
            v.isTransfer = v.isTransfer ? '是' : '否'
          });
          return res.result.records
        }else if(!res.result.records) {
          return [];
        }
      }catch(err) {
        console.log(err);
        return [];
      }
    },
    getClasses() {
      const data = {
        schoolId: this.extra.schoolId,
        period: this.searchForm.stuPeriod,
        grade: this.searchForm.stuGrade
      }
      getClasses(data).then(res => {
        this.searchBox.filter(val => val.key == 'stuClass')[0].options = [...new Set(res.result)];;
        this.searchForm.stuClass = '';
      })
    },
    // 点击干预按钮
    async intervene(row) {
      // 如果是第一次进入预警库，则打开“确定危机等级”的dialog框
      // 否则的话打开干预记录框，ps：在这个框有：添加方案和记录的按钮
      this.curRow = row;
      if(this.level <= 3) {
        if(!row.crisisLevel) {
          // 第一次进入预警库
          const res = await getSaveRecordData({
            recordType: '危机确认',
            fileId: row.fileId + '',
            userId: this.userId + ''
          });
          if(res.success && !res.result) {
            const data = [{
              period: row.stuPeriod,
              grade: row.stuGrade,
              classes: row.stuClass,
              name: row.stuName,
              time: dateFormat(new Date(), 'yyyy-mm-dd'),
              psyTeacher: this.name,
            }]
            this.$refs.confirmCrisisLevelDialog.openDialog(data, 'crisis', row.fileId);
          }else if(res.success && res.result) {
            this.openInterveneListDialog();
          }
        }else {
          this.openInterveneListDialog();
        }
      }else {
        this.openInterveneListDialog();
      }
    },
    // 提交 “确认等级或干预记录” dialog，打开干预表单dialog
    openInterveneListDialog() {
      const data = [{
        period: this.curRow.stuPeriod,
        grade: this.curRow.stuGrade,
        classes: this.curRow.stuClass,
        name: this.curRow.stuName
      }]
      this.$refs.InterventionRecordListDialog.openDialog(data, this.curRow.fileId);
    },
    // 在表单dialog点击添加干预方案按钮
    addPlan() {
      const data = [{
        period: this.curRow.stuPeriod,
        grade: this.curRow.stuGrade,
        classes: this.curRow.stuClass,
        name: this.curRow.stuName,
        time: dateFormat(new Date(), 'yyyy-mm-dd'),
        psyTeacher: this.name,
      }]
      this.$refs.IntervenePlan.openDialog(data, this.curRow.fileId, this.curRow.stuId)
    },
    // 在表单dialog点击添加干预记录按钮
    addRecord() {
      const data = [{
        period: this.curRow.stuPeriod,
        grade: this.curRow.stuGrade,
        classes: this.curRow.stuClass,
        name: this.curRow.stuName,
        time: dateFormat(new Date(), 'yyyy-mm-dd'),
        psyTeacher: this.name,
      }]
      this.$refs.confirmCrisisLevelDialog.openDialog(data, 'record', this.curRow.fileId);
    },
    
    pageChange(page) {
      this.curPage = page;
      this.getWarningData();
    },
    checkDetails(row) {
      if(row.moudle != '干预方案') {
        // 跳转到recordsDetails
        this.$refs.RecordDetails.openDialog(row.id, row.psyTeacher)

      }else if(row.moudle == '干预方案') {
        this.$refs.PlanDetails.openDialog(row.id, row.psyTeacher);
      }
    },
    updateDetails(row) {
      if(row.moudle != '干预方案') {
        // 跳转到recordsDetails
        const data = [{
          period: this.curRow.stuPeriod,
          grade: this.curRow.stuGrade,
          classes: this.curRow.stuClass,
          name: this.curRow.stuName,
          time: dateFormat(new Date(), 'yyyy-mm-dd'),
          psyTeacher: this.name,
        }]
        let title = row.moudle == '干预记录' ? 'record' : 'crisis';
        this.$refs.confirmCrisisLevelDialog.openDialog(data, title, this.curRow.fileId, 'update', row.id);
      }else if(row.moudle == '干预方案') {
        const data = [{
          period: this.curRow.stuPeriod,
          grade: this.curRow.stuGrade,
          classes: this.curRow.stuClass,
          name: this.curRow.stuName,
          time: dateFormat(new Date(), 'yyyy-mm-dd'),
          psyTeacher: this.name,
        }]
        this.$refs.IntervenePlan.openDialog(data, this.curRow.fileId, this.curRow.stuId, 'update', row.id)
      }
    }, async exportWarningStudents() {
      if(this.selectedStudents.length == 0) {
        this.$message('请勾选要导出的学生');
      }else {
        const headers = ['学段', '年级', '班级', '任务名称', '姓名', '更新时间', '下次干预截止时间', '心理老师', '危机等级', '当前状态', '是否转介', '去向/处理', '所属学期'];
        const val = ['stuPeriod', 'stuGrade', 'stuClass', 'taskName', 'stuName', 'createTime', 'expireTime', 'psyTeacher', 'crisisLevel', 'stuState', 'isTransfer', 'postProcessing', 'semester'];
        const title = '预警库学生导出数据';
        let exportData = [];
          if(this.isSelectedAll) {
            try {
              exportData = await this.getAllWarningData();
              // this.getAllWarningData().then(
              //   response => exportData = response
              // );
            }catch(err) {
              console.log(err)
            }
          }else {
            exportData = this.selectedStudents;
          }
          console.log(exportData);
          export2Excel(headers, val, exportData, title);
        }
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";
.warning-database-container {
  @include container;
}
</style>