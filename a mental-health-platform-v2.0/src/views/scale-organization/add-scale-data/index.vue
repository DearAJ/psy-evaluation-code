<template>
  <div class="add-scaledata-container" v-loading.fullscreen.lock="loading">
    <div class="search-container">
      <div class="search-box">
        <span>量表名称&nbsp;</span>
        <el-input class="input" placeholder="请输入量表名称" clearable
          v-model="searchForm.scaleName"></el-input>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" @click="search" size="medium">搜索</el-button>
    </div>
    <div class="content-container operation-btn">
      <el-table :data="customScaleList" border :stripe="true">
        <el-table-column align="center"
          prop="taskName"
          label="任务名称">
        </el-table-column>
        <el-table-column align="center"  min-width="90px"
          prop="scaleName"
          label="量表名称">
        </el-table-column>
        <el-table-column align="center" min-width="80px"
          prop="startDate"
          label="开始时间">
        </el-table-column>
        <el-table-column align="center" min-width="80px"
          prop="endDate"
          label="结束时间">
        </el-table-column>
        <el-table-column align="center" width="100" label="操作">
          <template slot-scope="scope">
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
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getCustomScaleList } from '@/api/scale.js'
import { getTaskRawData } from '@/api/task.js'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'AddScaleData',
  computed: {
    ...mapGetters([
      'province',
      'userId'
    ])
  },
  mounted() {
    this.getcustomScaleList()
  },
  data() {
    return {
      loading: false,
      customScaleList: [],
      searchForm: {
        scaleName: ''
      },
      // 与分页相关的
      total: 0,
      curPage: 1,
      pageSize: 10,
    }
  },
  methods: {
    getcustomScaleList() {
      this.loading = true;
      const data = {
        pageNum: this.curPage,
        pageSize: this.pageSize,
        province: this.province,
        userId: this.userId
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      getCustomScaleList(data).then(res => {
        this.loading = false;
        if(res.success && res.result) {
          this.total = res.result.total;
          this.customScaleList = res.result.records;
        }else {
          this.total = 0;
          this.customScaleList = [];
        }
      }).catch(() => {
        this.loading = false;
      })
    },
    search() {
      this.curPage = 1;
      this.getcustomScaleList();
    },
    pageChange(page) {
      this.curPage = page;
      this.getcustomScaleList();
    },
    downloadScale(row) {
      getTaskRawData({taskId: row.taskId}).then(res => {
        if(res.success) {
          if (res.result.length == 0) {
            this.$message.info('暂无数据');
          }
          let headers = ['序号', '姓名', '学段', '年级', '班级'];
          let val = ['序号', '姓名', '学段', '年级', '班级'];
          for(let key in res.result.questionList) {
            headers.push(`${key}.${res.result.questionList[key]}`);
            val.push(res.result.questionList[key])
          }
          const title = row.scaleName + '测评数据';
          export2Excel(headers, val, res.result.records, title);
        } else {
          this.$message.error('数据导出失败');
        }
      }).catch(err => {
        console.log(err);
      })
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/common/organization.scss";
@import "@/styles/variables";
.add-scaledata-container {
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
}
</style>