<template>
  <div class="scale-list-container" v-loading.fullscreen.lock="loading">
    <div class="search-container">
      <div class="search-box">
        <span>量表名称&nbsp;</span>
        <el-input class="input" placeholder="请输入量表名称" clearable
          v-model="searchForm.name"></el-input>
      </div>
      <div class="search-box">
        <span>量表类型&nbsp;</span>
        <el-select v-model="searchForm.application" class="input" clearable>
          <el-option v-for="item in typeOptions" :key="item" :value="item" :label="item">
          </el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>是否预警&nbsp;</span>
        <el-select v-model="searchForm.warningOptions" class="input" clearable>
          <el-option :value="0" label="是"></el-option>
          <el-option :value="1" label="否"></el-option>
        </el-select>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" @click="search" size="medium">搜索</el-button>
    </div>
    <div class="content-container operation-btn">
      <el-table :data="scaleList" border :stripe="true">
        <el-table-column width="30" align="center">
          <template slot-scope="scope">
            <el-checkbox v-model="scope.row.selected" @change="handleChange(scope.row)"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column align="center" min-width="120px"
          prop="name"
          label="量表名称">
        </el-table-column>
        <el-table-column align="center"  min-width="90px"
          prop="type"
          label="量表属性">
        </el-table-column>
        <el-table-column align="center" min-width="80px"
          prop="application"
          label="量表类型">
        </el-table-column>
        <el-table-column align="center" min-width="60px"
          prop="warningOptions"
          label="是否预警">
        </el-table-column>
        <el-table-column align="center" min-width="100px"
          prop="crowd"
          label="适合人群">
        </el-table-column>
        <el-table-column align="center" width="280" label="操作">
          <template slot-scope="scope">
            <el-popover trigger="hover" :content="scope.row.introduction"
              width="500">
              <el-button slot="reference" size="mini" style="margin-right: 10px">查看</el-button>
            </el-popover>
            <template v-if="scope.row.type == '自定义量表'">
              <el-button size="mini">修改</el-button>
              <el-button size="mini">删除</el-button>
            </template>
            <el-button size="mini" @click="downloadScale(scope.row)">下载</el-button>
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
    <div class="selected-scale">
      <p>已选量表:</p>
      <p class="scale-name">{{selectedScale.name}}</p>
      <!-- <p v-for="item in selectedScales" :key="item">
        {{key}}
      </p> -->
    </div>
    <div class="confirm-btn">
      <el-button type="primary" size="medium" @click="issueTask">确定</el-button>
    </div>
    <el-dialog title="请您仔细确认下发任务信息" :visible.sync="dialogVisible" class="dialog-container">
      <el-table :data="comfirmInfo" border>
        <el-table-column prop="title" label="任务名称" align="center"></el-table-column>
        <el-table-column prop="content" :label="taskInfo.name" align="center"></el-table-column>
      </el-table>
      <el-button class="confirm-btn" size="medium" type="primary" @click="submit">确认下发</el-button>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getScaleList } from '@/api/scale.js'
import { getScale } from '@/api/scale.js'
import { addTask } from '@/api/task.js'

export default {
  name: 'SelectScale',
  computed: {
    ...mapGetters([
      'taskInfo',
      'level',
      'userId',
      'extra'
    ])
  },
  mounted() {
    this.getScaleList()
    if(!this.taskInfo.id) {
      this.$router.replace({name: 'IssueTasks'})
    }
  },
  data() {
    return {
      loading: false,
      scaleList: [],
      searchForm: {
        name: '',
        application: '',
        warningOptions: '',
      },
      // 与分页相关的
      total: 0,
      curPage: 1,
      pageSize: 10,
      typeOptions: ['心理健康', '人格', '学业', '职业倦怠', '其他'],
      // selectedScales: [],
      selectedScale: {},
      // dialog
      comfirmInfo: [],
      dialogVisible: false
    }
  },
  methods: {
    getScaleList() {
      this.loading = true;
      const data = {
        pageNum: this.curPage,
        pageSize: this.pageSize,
        userId: this.userId
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          if(key === 'warningOptions') {
            data[key] = this.searchForm[key] === '是' ? 0 : 1;
          }else {
            data[key] = this.searchForm[key];
          }
        }
      }
      getScaleList(data).then(res => {
        this.loading = false;
        if(res.success) {
          this.total = res.result.total;
          res.result.records.forEach(val => {
            val.type = val.type === 1 ? '系统量表' : '自定义量表';
            val.warningOptions = val.warningOptions === 1 ? '否' : '是';
            val.selected = false;
          })
          this.scaleList = res.result.records;
        }
      }).catch(() => {
        this.loading = false;
      })
    },
    search() {
      this.curPage = 1;
      this.getScaleList();
    },
    pageChange(page) {
      this.curPage = page;
      this.getScaleList();
    },
    downloadScale(row) {
      getScale({id: row.id}).then(res => {
        console.log(res);
      })
    },
    handleChange(row) {
      // this.scaleList.filter(v => v.name ===  this.selectedScale.name)[0].selected = false;
      if(row.selected) {
        this.selectedScale.selected = false;
        this.selectedScale = row;
      }else {
        this.selectedScale = {};
      }
    },
    issueTask() {
      if(!this.selectedScale.name) {
        this.$message('请选择一个量表')
      }else {
        this.comfirmInfo = [{
          title: '任务内容',
          content: this.taskInfo.content
        }, {
          title: '测评量表',
          content: this.selectedScale.name
        }, {
          title: '开始时间',
          content: this.taskInfo.startDate
        }, {
          title: '结束时间',
          content: this.taskInfo.endDate
        }, {
          title: '所属学期',
          content: this.taskInfo.semester
        }, {
          title: '任务对象',
          content: this.level <= 3 ? this.taskInfo.classes : this.taskInfo.grade
        }, {
          title: '下发范围',
          content: this.level > 3 ? this.taskInfo.schoolName : this.extra.school
        }]
        console.log(this.taskInfo.schoolName)
        this.dialogVisible = true;
      }
    },
    submit() {
      const data = JSON.parse(JSON.stringify(this.taskInfo));
      data.scaleId = this.selectedScale.id + '';
      addTask(data).then(res => {
        if(res.success) {
          this.$message.success('任务下发成功');
          if(this.level <= 3) {
            this.$router.replace({name: 'Task'})
          } else {
            this.$router.replace({name: 'MonitorTasks'})
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/common/organization.scss";
@import "@/styles/variables";
.scale-list-container {
  margin: 40px 40px;
  .search-box {
    width: 30%;
  }
  .search-btn {
    right: 60px;
  }
  .operation-btn {
    ::v-deep .el-button {
      border: 1px solid $themeColor;
      border-radius: 13px;
      color: $themeColor;
    }
  }
  .selected-scale {
    p {
      margin-bottom: 20px;
    }
    .scale-name {
      color: $themeColor;
    }
  }

  .confirm-btn {
    margin-top: 10px;
    ::v-deep .el-button {
      border-radius: 12px;
    }
  }
}
</style>

<style lang="scss">
.scale-list-container {
  .dialog-container {
    .el-table thead th {
      color: #606266;
      font-weight: normal !important;
    }
  }
}
</style>