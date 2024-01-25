<template>
  <div class="school-tasks" v-loading.fullscreen.lock="loading">
    <div class="search-container">
      <div class="search-box">
        <span>任务名称</span>
        <el-input v-model="searchForm.name" placeholder="请输入任务名称" clearable size="medium" @change="autoSearch"></el-input>
      </div>
      <div class="search-box">
        <span>任务状态</span>
        <el-select v-model="searchForm.taskState" placeholder="请选择任务状态" clearable size="medium" @change="autoSearch">
          <el-option label="进行中" value="1"></el-option>
          <el-option label="未开始" value="0"></el-option>
          <el-option label="已结束" value="2"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>任务进度</span>
        <el-select v-model="searchForm.percent" placeholder="请选择任务进度" clearable size="medium" @change="autoSearch">
          <el-option label="0-30%" value="0-30%"></el-option>
          <el-option label="30%-60%" value="30%-60%"></el-option>
          <el-option label="60%-100%" value="60%-100%"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>任务级别</span>
        <el-select v-model="searchForm.taskLevel" placeholder="请选择任务级别" clearable  size="medium" @change="autoSearch">
          <el-option label="省级" value="省级"></el-option>
          <el-option label="市级" value="市级"></el-option>
          <el-option label="区县级" value="区县级"></el-option>
          <el-option label="校级" value="校级"></el-option>
        </el-select>
      </div>
      <div>
        <el-button type="primary" size="medium" @click="search">搜索</el-button>
      </div>
    </div>
    <div class="content-container">
      <p class="list-title">任务列表</p>
      <el-table :data="showTasksList" :stripe="true" :border="true">
        <el-table-column label="任务名称" prop="name" align="center"></el-table-column>
        <el-table-column label="开始时间" prop="start_date" align="center"></el-table-column>
        <el-table-column label="结束时间" prop="end_date" align="center"> </el-table-column>
        <el-table-column label="下发人" prop="issued_name" align="center"></el-table-column>
        <el-table-column label="任务状态" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.state == 0">未开始</span>
            <span v-else-if="scope.row.state == 1">进行中</span>
            <span v-else-if="scope.row.state == 2">已结束</span>
          </template>
        </el-table-column>
        <el-table-column label="任务级别" prop="level" align="center"></el-table-column>
        <el-table-column label="任务进度" prop="percent" align="center"></el-table-column>
        <el-table-column align='center' width="360px" label="操作" class="operation">
          <template slot-scope="scope">
            <el-button size="mini" @click="performTask(scope.row)">执行</el-button>
            <el-button size="mini" @click="taskDegree(scope.row)">完成度</el-button>
            <el-button size="mini" @click="exportOriginalData(scope.row)">导出原始数据</el-button>
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
  </div>
</template>

<script>
import { getSchoolTasks, getAllOriginalData } from '@/api/task.js'
import { mapGetters } from 'vuex'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'Task',
  computed: {
    ...mapGetters([
      'extra'
    ])
  },
  data() {
    return {
      tasksList: [], // 全部数据
      showTasksList: [], // 显示数据
      filterTaskList: [], // 筛选后的数据

      searchForm: {
        name: '',
        taskState: '',
        percent: '',
        taskLevel: ''
      },
      // 与分页有关的
      total: 1,
      curPage: 1,
      pageSize: 10,
      // end
      loading: false,
    }
  },
  mounted() {
    this.getTasksData();
  },
  methods: {
    getTasksData() {
      const data = { schoolId: this.extra.schoolId };
      getSchoolTasks(data).then(res => {
        res.result.forEach(val => {
          val.percent = Number((val.percent * 100).toFixed(2)) + '%';
        })
        this.tasksList = res.result;
        this.total = this.tasksList.length;
        this.search();
      }).catch();
    },
    pageChange(page) {
      this.curPage = page;
      // this.getTasksData();
      console.log(this.curPage)
      console.log(this.filterTaskList)
      this.showTasksList = this.filterTaskList.slice((this.curPage - 1) * this.pageSize, this.curPage * this.pageSize);
    },
    performTask(row) {
      // DFXCE 任务id; CGXSG: 量表id; NN 任务名称
      this.$router.push({name: 'PerformTask', query: {DFXCE: (row.id * 18) + '#', CGXSG: (row.scale_id * 18) + '#',  NN: row.name}});
    },
    taskDegree(row) {
      this.$router.push({name: 'CompletionDegree', query: {DFXCE: (row.id * 18) + '#', CGXSG: (row.scale_id * 18) + '#', NN: row.name}})
    },
    async getAllOriginalData(row) {
      this.loading = true;
      const data = {
        taskIds: [row.task_id],
        schoolIds: [this.extra.schoolId],
      };
      try{
      const res = await getAllOriginalData(data);
        this.loading = false;
        if(res.success && res.result) {
          this.loading = false;
          return res.result
        }else if(!res.result) {
      this.loading = false;
          return [];
        }
      }catch(err) {
        console.log(err);
      this.loading = false;
        return [];
      }
    },
    async exportOriginalData(row) {
      let exportData = await this.getAllOriginalData(row);
      let original_headers = Object.keys(exportData[0]);
      let non_question_headers = ["name", "sex", "period", "grade", "classes", "task_id", "score"];
      let questions_headers = original_headers.filter(item => !non_question_headers.includes(item));;
      console.log(original_headers)
      console.log(non_question_headers)
      console.log(questions_headers)
       const headers = ["姓名", "性别", "学段", "年级", "班级", "总分"].concat(questions_headers);
        const val = ["name", "sex", "period", "grade", "classes", "score"].concat(questions_headers);
        // const headers = original_headers;
        // const val = original_headers;

        const title = row.name + '任务-原始数据';

          export2Excel(headers, val, exportData, title);
    },
    search() {
      // this.getTasksData();
      let { name, taskState, percent, taskLevel } = this.searchForm;
      // console.log(name, taskState, percent, taskLevel, (this.tasksList[0].state + '').includes(taskState), this.tasksList[0].percentRange.includes(percent))
       console.log(this.tasksList)
      this.filterTaskList = this.tasksList.filter(val => {
        let flag = val.name.toLowerCase().includes(name.toLowerCase()) && val.level.toLowerCase().includes(taskLevel.toLowerCase()) && (val.state + '').toLowerCase().includes(taskState.toLowerCase()) && val.percentRange.includes(percent)
      console.log(val)
        return flag;
      })
      this.curPage = 1;
      this.total = this.filterTaskList.length;
      this.showTasksList = this.filterTaskList.slice((this.curPage - 1) * this.pageSize, this.curPage * this.pageSize);
    },
    autoSearch(val) {
      if(!val) {
        this.search();
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
.school-tasks {
  @include container;
  .search-container {
    height: 50px;
    display: flex;
    justify-content: space-around;
    line-height: 50px;
    margin: 10px 10px 0 10px;
    padding-top: 10px;
    .search-box {
      display: flex;
      width: 23%;
      span {
        width: 70px;
        flex: 1 0 auto;
        text-align: right;
        font-size: 14px;
        margin-right: 10px;
      }
      .input {
        flex: 0 1 220px;
      }
    }
    .btn {
      justify-content: right;
    }
  }
  .content-container {
    padding: 20px;
    .page-block {
      text-align: center;
      margin-top: 20px;
    }
    .list-title {
      color: #656565;
      margin: 5px 5px 10px 5px;
    }
  }
  .dialog-block {
    margin: 0 auto;
    text-align: center;
    p {
      margin: 20px 0;
      font-size: 16px;
    }
    .btn {
      width: 200px;
      margin-top: 30px;
    }
  }
}
</style>
<style lang="scss">
.school-tasks {
  .search-container {
    .el-button {
      border-radius: 20px;
    }
  }
  .content-container {
    // .el-table td.el-table__cell div {
    //   white-space: nowrap;
    //   text-overflow: ellipsis;
    //   overflow: hidden;
    //   cursor: pointer;
    // }
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
}
</style>