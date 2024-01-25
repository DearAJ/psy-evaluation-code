<template>
  <div v-loading.fullscreen.lock="loading">
    <div class="search-container">
      <div class="block input">
        <span>任务名称:</span>
        <el-input placeholder="请输入任务关键词" v-model="search.name" clearable></el-input>
      </div>
      <div class="block input">
        <span>量表名称:</span>
        <el-input placeholder="请输入量表关键词" v-model="search.name" clearable></el-input>
      </div>
      <div class="block">
        <el-button type="primary" size="medium" @click="filterData">搜索</el-button>
      </div>
    </div>
    <div class="content-container">
      <el-form :model="taskForm">
        <el-table :data="showTaskList" border>
          <el-table-column v-for="item in taskProps" align="center"
            :prop="item.prop"
            :key="item.prop"
            :label="item.label">
          </el-table-column>
          <el-table-column align='center'
            label="操作">
            <template slot-scope="">
              <el-button size="medium" type="primary" @click="$message('该功能暂未开发')">查看报告</el-button>
              <el-button size="medium" type="primary" @click="$message('该功能暂未开发')">导出报告</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
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
import { taskList } from '@/api/task.js'
import { mapGetters } from 'vuex'

export default {
  name: 'cityReportManage',
  computed: {
    ...mapGetters([
      'province',
      'city',
    ])
  },
  data() {
    return {
      search: { // 搜索框对应字段
        name: '', // 任务名称
        state: '', // 任务状态
        progress: '', // 任务进度
        level: '' // 任务级别
      },
      taskProps: [{
        prop: 'name',
        label: '任务名称',
        type: 'string',
        rules: [{
          required: true, message: '任务名称不能为空', trigger: 'blur'
        }]
      }, {
        prop: 'start_date',
        label: '开始时间',
        type: 'date'
      }, {
        prop: 'end_date',
        label: '结束时间',
        type: 'date'
      }, {
        prop: 'scaleName',
        label: '量表名称',
        type: 'string'
      }],
      taskList: [],
      showTaskList: [],
      filterTaskList: [],

      taskForm: {},
      curPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
    }
  },
  mounted() {
    this.getTaskList();
  },
  methods: {
    getTaskList() {
      this.loading = true;
      const data = {
        province: this.province,
        city: this.city,
      }
      taskList(data).then(res => {
        this.loading = false;
        res.result.forEach(val => {
          val.showState = 'text';
          val.state = val.state == 0 ? '未开始' : ( val.state == 1 ? '进行中' : '已结束');
          val.percent = parseInt(val.percent * 100) + '%';
        })
        this.taskList = res.result;
        this.filterData();
      }).catch(() => {
        this.loading = true;
      })
    },
    pageChange(page) {
      this.curPage = page;
      this.showTaskList = this.filterTaskList.slice((this.curPage - 1) * this.pageSize, this.curPage * this.pageSize);
    },
    gotoWarning(row) {
      this.$router.push({name: 'ProgressAndWarning', query: {taskInfo: JSON.stringify({id: row.id, name: row.name})}})
    },
    filterData() {
      // 任务级别暂不支持搜索，暂时先取消任务级别的搜索
      let { name, state, progress, level } = this.search;
      this.filterTaskList = this.taskList.filter( val => {
        return val.name.includes(name) && val.state.includes(state) && val.percentRange.includes(progress);
      })
      this.curPage = 1;
      this.total = this.filterTaskList.length;
      this.showTaskList = this.filterTaskList.slice((this.curPage - 1) * this.pageSize, this.curPage * this.pageSize);
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
.search-container {
  height: 60px;
  display: flex;
  justify-content: space-around;
  line-height: 60px;
  .block {
    margin-left: 10px;
    margin-right: 10px;
    span {
      flex: 1 0 auto;
      width: 75px;
    }
  }
  .input {
    display: flex;
  }
}
.content-container {
  padding: 20px;
  .page-block {
    text-align: center;
    margin-top: 20px;
  }
}
</style>
<style lang="scss">
.search-container {
  .el-button {
    border-radius: 20px;
  }
}
</style>