<template>
  <div class="personal-report-container" v-loading.fullscreen.lock="loading">
    <div class="search-container" v-for="box in searchBoxNum" :key="'pbox' + box.id">
      <div v-for="item in box.boxList" :key="item.key" class="search-box">
        <template v-if="item.type === 'input'">
          <span>{{item.chName}}&nbsp;</span>
          <el-input class="input" :placeholder="'请输入' + item.chName" clearable
            v-model="searchForm[item.key]"
            size="medium">
          </el-input>
        </template>
        <template v-else-if="item.key === 'warningLevel'">
          <span><span style="color: red">*</span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.warningLevel" placeholder="请选择预警级别"
                     clearable
                     size="medium"
                     @change="getClasses">
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'period'">
          <span><span style="color: red">*</span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.period" placeholder="请选择学段" 
            clearable  
            size="medium"
            @change="getClasses">
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'grade'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.grade" :placeholder="searchForm.period ? '请选择年级' : '请先选择学段'" 
            clearable  
            size="medium"
            @change="getClasses">
            <el-option v-for="o in item.options[searchForm.period]" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'classes' && item.type === 'select'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.classes" :placeholder="searchForm.classes ? '请选择班级' : '请先选择年级'" 
            clearable  
            size="medium">
            <template v-if="searchForm.grade != ''">
              <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
            </template>
          </el-select>
        </template>
        <template v-else-if="item.type === 'select'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm[item.key]" :placeholder="'请选择' + item.chName" clearable size="medium">
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="search">搜索</el-button>
    </div>
    <div class="content-container">
      <el-table :data="reportTableData" :stripe="true" :border="true">
        <el-table-column v-for="item in tableColumnInfo.filter((v, idx) => (v.role.includes(level) && idx < 5))" :key="item.prop"
          align="center" 
          :min-width="item.minWidth"
          :prop="item.prop"
          :label="item.label">
        </el-table-column>
        <el-table-column label="年级" align="center">
          <template slot-scope="scope">
            <TableGradeField :row="scope.row" ></TableGradeField>
          </template>
        </el-table-column>
        <el-table-column v-for="item in tableColumnInfo.filter((v, idx) => (v.role.includes(level) && idx > 5))" :key="item.prop"
          align="center" 
          :min-width="item.minWidth"
          :prop="item.prop"
          :label="item.label">
        </el-table-column>
        <el-table-column align="center" label="操作" width="150px">
          <template slot-scope="scope">
            <el-button size="mini" @click="generateReport(scope.row)" :label="scope" :disabled="scope.row.scaleType != 1">查看</el-button>
            <el-button size="mini" @click="downloadPDF(scope.row)" :label="scope" :disabled="scope.row.scaleType != 1">下载</el-button>
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
 
    <div style="visibility:hidden;position:absolute;width:100%;height:100%;">
    <!-- <div style="position:absolute;width:100%;height:100%;margin:0;padding:0;"> -->
    <iframe :src="groupReportURL" width="100%"></iframe>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { getClasses } from '@/api/organization'
import { getPersonalReportList } from '@/api/reportOrganization.js'
import { mapGetters } from 'vuex'
import exportPDF from "../../report-page/school/group";
import TableGradeField from '@/components/TableGradeField'

export default {
  name: 'PersonalReport',
  components:{
    TableGradeField
  },
  computed: {
    ...mapGetters([
      'extra',
      'province',
      'city',
      'county',
      'level',
      'role'
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
      loading: false,
      groupReportURL: "",
      searchForm: {
        role: '',
        name: '',
        period: '',
        warningLevel: '',
        grade: '',
        classes: '',
        sex: '',
        idNumber: '',
        studentId: '', // 学号
        city: '',
        county: '',
        schoolName: '',
      },
      searchBox: [ {
        key: 'warningLevel',
        chName: '预警级别',
        type: 'select',
        options: ['一级预警（一般心理危机）', '二级预警（严重心理危机）', '三级预警（重大心理危机）'],
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'period',
        chName: '学段',
        type: 'select',
        options: [],
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'role',
        chName: '身份',
        type: 'select',
        role: [2, 3, 4, 5, 6],
        options: ['学生', '教师'],
      }, {
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
        key: 'name',
        chName: '姓名',
        type: 'input',
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'grade',
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
      }, {
        key: 'classes',
        chName: '班级',
        type: 'select',
        options: [],
        role: [2, 3],
      }, {
        key: 'classes',
        chName: '班级',
        type: 'input',
        role: [4, 5, 6],
      }, {
        key: 'phone',
        chName: '手机号',
        type: 'input',
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'sex',
        chName: '性别',
        type: 'select',
        role: [2, 3, 4, 5, 6],
        options: ['男', '女'],
      }, {
        key: 'idNumber',
        chName: '身份证号',
        type: 'input',
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'studentId',
        chName: '学号',
        type: 'input',
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
        prop: 'school',
        label: '学校',
        role: [ 4, 5, 6 ],
        minWidth: '50px'
      }, {
        prop: 'role',
        label: '身份',
        role: [2, 3, 4, 5, 6], 
        minWidth: '40px'
      }, {
        prop: 'period',
        label: '学段',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '40px'
      }, {
        prop: 'grade',
        label: '年级',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '50px'
      }, {
        prop: 'classes',
        label: '班级',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '40px'
      }, {
        prop: 'name',
        label: '姓名',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '50px'
      }, {
        prop: 'sex',
        label: '性别',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '30px'
      }, {
        prop: 'studentId',
        label: '学号',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '80px'
      }, {
        prop: 'idNumber',
        label: '身份证号',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '90px'
      }, {
        prop: 'phone',
        label: '手机号',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '80px'
      }, {
        prop: 'studentCode',
        label: '学籍号',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '90px'
      }, {
        prop: 'taskName',
        label: '任务名称',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '80px'
      }, {
        prop: 'scaleName',
        label: '量表名称',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '90px'
      }, {
        prop: 'submitTime',
        label: '报告生成时间',
        role: [ 2, 3, 4, 5, 6 ], 
        minWidth: '80px'
      },],
      reportTableData: [],
      // 分页
      total: 0,
      curPage: 1,
      pageSize: 10
    }
  },
  mounted() {
    if(this.level <= 3) {
      this.searchBox.filter(val => val.key == 'period')[0].options = this.extra.period;
    }else {
      this.searchBox.filter(val => val.key == 'period')[0].options = ['小学', '初中', '高中', '职高', '其他'];
    }
    this.getPersonalReportList();
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
    getPersonalReportList() {
      this.loading = true;
      const data = {
        province: this.province,
        pageNum: this.curPage, 
        pageSize: this.pageSize,
      }
      if(this.level <= 5) {
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
      }
      // 模糊搜索字段
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      getPersonalReportList(data).then(res => {
        this.loading = false;
        if(res.success && res.result) {
          this.total = res.result.total;
          this.reportTableData = res.result.records.map(v => {
            const {province, city, county, role, name, sex, school} = v;
            const data = {province, city, county, role, name, sex, school};
            data.taskName = v.task_name;
            data.idNumber = v.id_number;
            data.submitTime = v.submit_time;
            data.taskId = v.task_id;
            data.scaleName = v.scale_name;
            data.scaleType = v.scaleType;
            data.archived = v.archived;
            data.userId = v.user_id;
            if(data.role === '教师') {
              data.phone = v.phone;
            }else {
              data.classes = v.classes;
              data.grade = v.grade;
              data.period = v.period;
              data.studentCode = v.student_code;
              data.studentId = v.student_id;
            }
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
    
    generateReportURL(row){
      let routeUrl = '';
      let { userId, taskId, scaleName, name, studentId, classes, grade } = row;
      const data = { userId, taskId, scaleName, name, studentId, classes, grade };
      data.school = this.level <= 3 ? this.extra.school : row.school;
      data.type = 'personal';
      data.identity = row.role === '学生' ? 'student' : 'teacher';
      routeUrl = this.$router.resolve({
        path: '/report',
        query: data
      })
      return routeUrl;
    },
    generateReport(row) {
      console.log(row.scaleType)
      let routeUrl = this.generateReportURL(row);
      window.open(routeUrl.href, "_blank");
    },
    downloadPDF(row) {
      let routeUrl = this.generateReportURL(row).href
      this.groupReportURL = location.origin + routeUrl
      let { scaleName, name} = row;
      let reportName = scaleName + "-" + name
      let selector = '.report-container'
      exportPDF.methods.exportPDF(reportName, document.querySelector('iframe').contentDocument, selector)
    },
    pageChange(page) {
      this.curPage = page;
      this.getPersonalReportList();
    },
    search() {
      const warningLevel = this.searchForm.warningLevel;
      const perioded = this.searchForm.period;
      /*if(warningLevel==null||warningLevel.trim()==""){
        this.message ("请选择预警级别");
      }else if(perioded==null||perioded.trim()==""){
        this.message ("请选择学段");
      }else{
      this.curPage = 1;
      this.getPersonalReportList();
      }*/
      this.curPage = 1;
      this.getPersonalReportList();
    },
    message (param) {
      Vue.prototype.$message({
           message: param,
           title:'提示',
           type: "warning",
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";

.personal-report-container {
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
}

</style>