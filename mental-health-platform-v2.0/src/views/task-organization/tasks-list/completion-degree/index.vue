<template>
  <div class="school-task-container" v-loading.fullscreen.lock="loading"
    :element-loading-text="loadingText">
    <div class="header-box">
      <span style="font-size: 14px">{{taskName}}</span>
    </div>
    <div class="search-container">
      <div class="search-box">
        <span>学段</span>
        <el-select v-model="searchForm.period" placeholder="请选择学段" clearable 
          size="medium" 
          class="input"
          @change="autoSearch">
          <el-option v-for="item in periodOptions" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>年级</span>
        <el-select v-model="searchForm.grade" :placeholder="searchForm.period ? '请选择年级' : '请先选择学段'" clearable 
          size="medium" 
          class="input"
          @change="getClasses">
          <el-option v-for="item in gradeOptions[searchForm.period]" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>班级</span>
        <el-select v-model="searchForm.classes" :placeholder="searchForm.classes ? '请选择班级' : '请先选择年级'" clearable
          size="medium" 
          class="input"
          @change="autoSearch">
          <template v-if="searchForm.grade != ''">
            <el-option v-for="item in classesOptions" :key="item" :label="item" :value="item"></el-option>
          </template>
        </el-select>
      </div>
      <div class="search-box">
        <span>学生姓名</span>
        <el-input v-model="searchForm.name" placeholder="请输入学生名称" clearable size="medium" class="input" @change="autoSearch"></el-input>
      </div>
    </div>
    <div class="search-container">
      <div class="search-box">
        <span>学号</span>
        <el-input v-model="searchForm.student_id" placeholder="请输入学号" clearable size="medium" class="input" @change="autoSearch"></el-input>
      </div>
      <div class="search-box">
        <span>完成度</span>
        <el-select v-model="searchForm.completion" placeholder="请选择完成度" clearable size="medium" class="input"  @change="autoSearch">
          <el-option label="已完成" value="已完成"></el-option>
          <el-option label="未完成" value="未完成"></el-option>
        </el-select>
      </div>
      
      <div class="search-box">
        <span>所属学期</span>
        <el-select v-model="searchForm.semester" placeholder="所属学期" clearable size="medium" @change="autoSearch">
        <el-option
        v-for="item in semesterList"
        :key="item.value"
        :label="item.value"
        :value="item.value"
      ></el-option>
        </el-select>
      </div>
      <div class="search-btn">
        <el-button size="medium" type="primary" @click="search">搜索</el-button>
      
      <el-popconfirm title="点击确认将导出已勾选内容" @confirm="exportData">
        <el-button slot="reference" type="primary" size="medium">导出</el-button>
      </el-popconfirm>
      </div>
      <div class="search-btn">
      </div>
    </div>
    <div class="content-container">
      <el-table :data="completionTaskList" :stripe="true" :border="true">
        <el-table-column width="30" align="center">
          <template slot="header" slot-scope="scope">
            <el-checkbox v-model="isSelectedAll" @change="selectAll(scope)"></el-checkbox>
          </template>
          <template slot-scope="scope">
              <el-checkbox v-model="scope.row.selected" @change="handleChange(scope.row)"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column label="学段" prop="period" align="center"></el-table-column>
        <el-table-column label="年级" align="center"> 
          <template slot-scope="scope">
            <TableGradeField :row="scope.row" ></TableGradeField>
          </template>
        </el-table-column>
        <el-table-column label="班级" prop="classes" align="center"></el-table-column>
        <el-table-column label="学生姓名" prop="name" align="center"></el-table-column>
        <el-table-column label="学号" prop="student_id" align="center"></el-table-column>
        <el-table-column label="所属学期" prop="semester" align="center"></el-table-column>
        <el-table-column label="完成度" prop="completion" align="center"></el-table-column>
        <el-table-column label="班主任（负责人）" prop="charge_person" align="center"></el-table-column>
        <el-table-column label="联系方式" prop="phone" align="center"></el-table-column>
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
  </div>
</template>

<script>
import { schoolTaskCompletion } from '@/api/task.js'
import { getClasses } from '@/api/organization'
import { mapGetters } from 'vuex'
import { export2Excel } from '@/utils/excelExport.js'
import TableGradeField from '@/components/TableGradeField'

export default {
  name: 'CompletionDegree',
  components:{
    TableGradeField
  },
  mounted() {
    
    for (let i = 0; i < 50; i++) {
      this.semesterList.push({
        "value": 2010 + i + "年春季学期"
      }),
      this.semesterList.push({
        "value": 2010 + i + "年秋季学期"
      })
    }
    const params = this.$route.query;
    this.taskId = parseInt(params.DFXCE) / 18;
    this.scaleId = parseInt(params.CGXSG) / 18;
    this.taskName = params.NN;
    this.periodOptions = this.extra.period;
    this.getCompletionTasks();
  },
  computed: {
    ...mapGetters([
      'extra'
    ])
  },
  data() {
    return {
      semesterList: [],
      taskId: '',
      scaleId: '',
      taskName: '',
      searchForm: {
        period: '',
        grade: '',
        classes: '',
        name: '',
        student_id: '',
        completion: ''
      },

      completionTaskList: [], // 全部数据

      // 分页相关data
      isSelectedAll: false,
      selectedRecords: [],
      curPage: 1,
      pageSize: 10,
      total: 0,
      // end
      loading: false,
      periodOptions: [],
      gradeOptions: {
        '小学': ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
        '初中': ['一年级', '二年级', '三年级'],
        '高中': ['一年级', '二年级', '三年级'],
        '职高': ['一年级', '二年级', '三年级'],
        '其他': ['一年级', '二年级', '三年级'],
      },
      classesOptions: [],
      loadingText: ''
    }
  },
  methods: {
    // 勾选某个多选框
    handleChange(row) {
      if(row.selected) {
        this.selectedRecords.push(row);
      }else {
        let index = this.selectedRecords.indexOf(row);
        this.selectedRecords.splice(index, 1);
      }
      if(this.selectedRecords.length === this.completionTaskList.length) {
        this.isSelectedAll = true;
      }else {
        this.isSelectedAll = false;
      }
    },
    // 勾选全选框
    selectAll(val) {
      console.log(this.isSelectedAll)
      // 如果点击全选，则将所有的数据都放入selectedRecords中
      if(this.isSelectedAll) {
        this.selectedRecords = this.completionTaskList.slice();
        this.completionTaskList.forEach(val => {
          val.selected = true;
        })
        console.log(this.selectedRecords)
      }else {
        this.selectedRecords = [];
        this.completionTaskList.forEach(val => {
          val.selected = false;
        })
      }
    },
    pageChange(page) {
      this.curPage = page;
      this.getCompletionTasks();
    },
    getCompletionTasks() {
      this.loading = true;
      this.loadingText = '数据加载中，请稍后'
      const data = {
        taskId: this.taskId,
        schoolId: this.extra.schoolId,
        pageNum: this.curPage,
        pageSize: this.pageSize
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      schoolTaskCompletion(data).then(res => {
        this.loading = false;
        this.loadingText = '';
        if(res.success && res.result) {
          this.total = res.result.total;
          res.result.records.forEach((val, index) => {

            val.selected = this.isSelectedAll;
          })
          this.completionTaskList = res.result.records;
        }else {
          this.total = 0;
          this.completionTaskList = [];
        }
      }).catch(() => {
        this.loading = false;
        this.loadingText = '';
      })
    },
    async getAllCompletionTasks() {
      this.loading = true;
      this.loadingText = '数据加载中，请稍后'
      const data = {
        taskId: this.taskId,
        schoolId: this.extra.schoolId,
        pageNum: this.curPage,
        pageSize: -1
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      let res = await schoolTaskCompletion(data)

        this.loading = false;
        this.loadingText = '';
        if(res.success && res.result) {
          this.total = res.result.total;
          return res.result.records;
        }else {
          this.total = 0;
          return [];
        }
    },
    search() {
      this.curPage = 1;
      this.getCompletionTasks();
    },
    autoSearch(val) {
      if(!val) {
        this.search();
      }
    },
    async exportData() {
      let toExportData = []
      if(this.isSelectedAll){
        toExportData = await this.getAllCompletionTasks();
      }else{
        toExportData = this.selectedRecords;
      }
      console.log(toExportData)
       const headers = ["学生姓名", "学号", "学段", "年级", "班级", "完成度", "班主任（负责人）", "联系方式", "所属学期"];
        const val = ["name", "student_id", "period", "grade", "classes", "completion","charge_person", "phone", "semester"];
        // const headers = original_headers;
        // const val = original_headers;

        const title = this.taskName + '任务完成度列表数据';

          export2Excel(headers, val, toExportData, title);
    },
    getClasses(val) {
      if(!val) {
        this.search();
      }else {
        this.searchForm.classes = '';
        const data = {
          schoolId: this.extra.schoolId,
          period: this.searchForm.period,
          grade: this.searchForm.grade
        }
        getClasses(data).then(res => {
          this.classesOptions = [...new Set(res.result)];
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/common/school-task.scss";
</style>

<style lang="scss">
.school-task-container {
  .header-box, .search-container {
    .el-button {
      border-radius: 40px;
      display: inline-block;
    }
    .el-select {
      width: 100%;
    }
  }
}
</style>