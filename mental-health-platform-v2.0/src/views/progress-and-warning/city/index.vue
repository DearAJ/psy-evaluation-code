<template>
  <div class="warning-container" v-loading.fullscreen.lock="loading">
    <div class="progress-block">
      <div class="progress-box">
        <div class="progress">
          <div class="title">
            <div class="outside"></div>
            <div class="inner"></div>
            <p>完成进度</p>
          </div>
          <circle-progress :radius="'36%'" :id="'cityCP'" :percent="warnStatistics.completePercent"
            class="circle-progress">
          </circle-progress>
        </div>
        <div class="info">
          <div class="info-block">
            <p class="info-title">已完成区县</p>
            <p class="info-data">
              <span class="data">{{warnStatistics.completeCounty}}</span>
              <span class="extra">个</span>
            </p>
          </div>
          <div class="info-block">
            <p class="info-title">已测人数</p>
            <p class="info-data">
              <span class="data">{{warnStatistics.completeNumbers}}</span>
              <span class="extra">人</span>
            </p>
          </div>
        </div>
      </div>
      <div class="progress-box">
        <div class="progress">
          <div class="title">
            <div class="outside"></div>
            <div class="inner"></div>
            <p>有效比例</p>
          </div>
          <circle-progress :radius="'36%'" :id="'cityVP'" :percent="warnStatistics.validPercent"
            class="circle-progress"></circle-progress>
        </div>
        <div class="info">
          <div class="info-block">
            <p class="info-title">有效数据</p>
            <p class="info-data">
              <span class="data">{{warnStatistics.validNumbers}}</span>
              <span class="extra">份</span>
            </p>
          </div>
        </div>
      </div>
      <div class="progress-box">
        <div class="progress">
          <div class="title">
            <div class="outside"></div>
            <div class="inner"></div>
            <p>预警比例</p>
          </div>
          <circle-progress :radius="'36%'" :id="'cityWP'" :percent="warnStatistics.warnPercent"
            class="circle-progress">
          </circle-progress>
        </div>
        <div class="info">
          <div class="info-block">
            <p class="info-title">预警人数</p>
            <p class="info-data">
              <span class="data warning">{{warnStatistics.warnNumbers}}</span>
              <span class="extra">人</span>
            </p>
          </div>
        </div>
      </div>
    </div>
    <div class="warning-block">
      <div class="search-container">
        <div class="search-box">
          <span>区县：&nbsp;</span>
          <el-input class="input" placeholder="请输入" clearable
            v-model="searchForm.county" size="medium">
          </el-input>
        </div>
        <div class="search-box">
          <span>学校：&nbsp;</span>
          <el-input class="input" placeholder="请输入" clearable
            v-model="searchForm.school" size="medium">
          </el-input>
        </div>
      </div>
      <div class="search-btn">
        <el-button type="primary" size="medium" @click="search">搜索</el-button>
      </div>
      
      <div class="content-container">
        <p class="title">实时进度和预警监控</p>
        <el-table border :data="tableData">
          <el-table-column prop="county" label="区县" align="center"></el-table-column>
          <el-table-column prop="school" label="学校" align="center"></el-table-column>
          <el-table-column label="进度" align="center" prop="completePercent"
            sortable 
            :sort-method="(a, b) => tableSortMethod(a, b, 'completePercent')">
          </el-table-column>
          <el-table-column prop="warnCount" label="预警人数" align="center" 
            sortable
            :sort-method="(a, b) => tableSortMethod(a, b, 'warnCount')">
          </el-table-column>
          <el-table-column label="预警比例" align="center" sortable prop="warnPercent"
            :sort-method="(a, b) => tableSortMethod(a, b, 'warnPercent')">
          </el-table-column>
          <el-table-column prop="chargePerson" label="负责人" align="center"></el-table-column>
          <el-table-column prop="phone" label="联系方式" align="center"></el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button size="mini" @click="urge(scope.row)">催办</el-button>
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
  </div>
</template>

<script>
import { taskProgress, taskStatistics } from '@/api/task.js'
import { mapGetters } from 'vuex'
import CircleProgress from '@/components/CircleProgress'

export default {
  name: 'CityProgressAndWarning',
  components: {
    CircleProgress
  },
  mounted() {
    this.taskInfo = JSON.parse(this.$route.query.taskInfo);
    this.getTableData();
    this.getStatisticsData();
  },
  data() {
    return {
      taskInfo: {},
      tableData: [],
      warnStatistics: [{
        completeCounty: 0,
        completeNumbers: 0, // 已测人数
        validNumbers: 0, // 有效人数
        warnNumber: 0, // 预警人数
        completePercent: 0, //完成进度
        validPercent: 0, //有效比例
        warnPercent: 0, // 预警比例
      }],
      loading: false,
      // 按区县-学校进行筛选
      searchForm: {
        county: '',
        school: ''
      },
      // 分页相关参数
      total: 0,
      curPage: 1,
      pageSize: 10
    }
  },
  computed: {
    ...mapGetters([
      'extra',
      'province',
      'city',
      'county',
    ])
  },
  methods: {
    getTableData() {
      this.loading = true;
      const data = {
        taskId: this.taskInfo.id,
        province: this.province,
        city: this.city,
        pageNum: this.curPage,
        pageSize: this.pageSize
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      taskProgress(data).then(res => {
        this.loading = false;
        if(res.success && res.result) {
          this.total = res.result.total;
          res.result.records.forEach(val => {
            val.warnPercent = parseInt(val.warnPercent * 100) + '%';
            val.completePercent = parseInt(val.completePercent * 100) + '%';
          })
          this.tableData = res.result.records;
        }else {
          this.total = 0
          this.tableData = [];
        }
      }).catch((err) => {
        console.log(err);
        this.loading = false;
      })
    },
    getStatisticsData() {
      this.loading = true;
      const data = {
        province: this.province,
        taskId: this.taskInfo.id,
        city: this.city
      }
      taskStatistics(data).then(res => {
        this.loading = false;
        if(res.success) {
          for(let key in res.result) {
            res.result[key] = parseFloat(res.result[key].toFixed(2));
          }
          this.warnStatistics = res.result;
        }
      }).catch((err) => {
        console.log(err)
        this.loading = false;
      })
    },
    urge(row) {
      console.log('催办', row)
    },
    tableSortMethod(a, b, key) {
      return parseInt(a[key]) - parseInt(b[key]);
    },
    pageChange(page) {
      this.curPage = page;
      this.getTableData();
    },
    search() {
      this.curPage = 1;
      this.getTableData();
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/common/warning-and-progress.scss";
@import "@/styles/common/organization.scss"
</style>
