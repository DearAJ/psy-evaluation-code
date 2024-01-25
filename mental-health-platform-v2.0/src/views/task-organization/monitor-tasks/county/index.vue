<template>
  <div v-loading.fullscreen.lock="loading">
    <div class="search-container">
      <div class="search-box">
        <span>任务名称：&nbsp;</span>
        <el-input placeholder="请输入任务关键词" v-model="search.name" clearable></el-input>
      </div>
      <div class="search-box">
        <span>任务状态：&nbsp;</span>
        <el-select v-model="search.state" clearable>
          <el-option label="进行中" value="进行中"></el-option>
          <el-option label="未开始" value="未开始"></el-option>
          <el-option label="已结束" value="已结束"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>任务级别：&nbsp;</span>
        <el-select v-model="search.level" clearable="">
          <el-option label="省级" value="省级"></el-option>
          <el-option label="市级" value="市级"></el-option>
          <el-option label="区县级" value="区县级"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>下发单位：&nbsp;</span>
        <el-input placeholder="请输入任务关键词" v-model="search.issued_unit" clearable></el-input>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="filterData">搜索</el-button>
    </div>
    <div class="content-container">
      <el-table :data="showTaskList" border>
        <el-table-column v-for="item in taskProps" align="center"
          :key="item.prop"
          :label="item.label"
          :prop="item.prop">
        </el-table-column>
        <el-table-column align='center' label="操作">
          <template slot-scope="scope">
            <el-button size="mini" @click="updateTime(scope.row)">修改时间</el-button>
          </template>
        </el-table-column>
        <el-table-column align='center' label="监控">
          <template slot-scope="scope">
            <el-button size="mini" @click="gotoWarning(scope.row)">进度和预警</el-button>
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
    <el-dialog :visible.sync="dialogVisible" title="修改结束时间" width="35%" class="update-end-time-dialog">
      <el-form :model="updateForm" ref="updateEndTimeForm" :rules="rules" label-width="120px">
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="updateForm.startTime" type="date" placeholder="选择新的结束时间"
            disabled>
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="updateForm.endTime" type="date" placeholder="选择新的结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item class="form-btn">
          <el-button type="primary" @click="submitUpdate('updateEndTimeForm')" size="medium">提交修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { taskList, updateEndTime } from '@/api/task.js'
import { mapGetters } from 'vuex'
import dateFormat from 'dateformat';

export default {
  name: 'countyMonitorTask',
  computed: {
    ...mapGetters([
      'province',
      'city',
      'county',
      'userId'
    ])
  },
  data() {
    var validateDate = (rule, value, callback) => {
      if(this.updateForm.startTime >= value) {
        callback(new Error('结束日期需大于开始日期'))
      }else {
        callback();
      }
    };
    return {
      search: { // 搜索框对应字段
        name: '', // 任务名称
        state: '', // 任务状态
        issued_unit: '', // 任务进度
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
        prop: 'issued_name',
        label: '下发人',
        type: 'string'
      }, {
        prop: 'issued_unit',
        label: '下发单位',
        type: 'string',
      },{
        prop: 'state',
        label: '任务状态',
        type: 'string',
      }, {
        prop: 'percent',
        label: '任务进度',
        type: 'slider', // 滑块
        min: 0,
        max: 100
      }],
      taskList: [],
      showTaskList: [],
      filterTaskList: [],
      taskForm: {},
      curPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      // 修改时间dialog
      updateForm: {
        startTime: '',
        endTime: '',
        taskId: ''
      },
      dialogVisible: false,
      rules: {
        endTime: [
          { required: true, message: '必填', trigger: 'blur' },
          { validator: validateDate, trigger: 'change'}
        ]
      }
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
        county: this.county
      }
      taskList(data).then(res => {
        this.loading = false;
        res.result.forEach(val => {
          val.state = val.state == 0 ? '未开始' : ( val.state == 1 ? '进行中' : '已结束');
          val.percent = parseInt(val.percent * 100) + '%';
        })
        this.taskList = res.result;
        this.filterData();
      }).catch(() => {
        this.loading = false;
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
      console.log(this.taskList)
      let { name, state, issued_unit, level } = this.search;
      this.filterTaskList = this.taskList.filter( val => {
        return val.name.includes(name) && val.state.includes(state) && val.issued_unit.includes(issued_unit) && val.level.includes(level);
      })
      this.curPage = 1;
      this.total = this.filterTaskList.length;
      this.showTaskList = this.filterTaskList.slice((this.curPage - 1) * this.pageSize, this.curPage * this.pageSize);
    },
    updateTime(row) {
      this.dialogVisible = true;
      this.updateForm.startTime = new Date(row.start_date);
      this.updateForm.endTime = new Date(row.end_date);
      this.updateForm.taskId = row.task_id;
    },
    submitUpdate(formName) {
      this.$refs[formName].validate((valid) => {
        if(valid) {
          const data = {
            taskId: this.updateForm.taskId,
            userId: this.userId,
            endDate: dateFormat(this.updateForm.endTime,'yyyy-mm-dd')
          }
          updateEndTime(data).then(res => {
            if(res.success) {
              this.$message.success(res.message);
              this.dialogVisible = false;
              this.getTaskList()
            }else {
              this.$message(res.message)
            }
          }).catch(err => {
            console.log(err)
          })
        }else {
          return false;
        }
      })
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";
.update-end-time-dialog {
  ::v-deep .el-dialog__body {
    height: 280px;
  }
  .form-btn {
    margin-top: 70px;
  }
}
</style>
