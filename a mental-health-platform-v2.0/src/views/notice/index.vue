<template>
  <div class="notice-container" v-loading.fullscreen.lock="loading">
    <div class="operation-content">
      <div class="search-box">
        <span>公告标题</span>
        <el-input 
          v-model="searchForm.title" 
          placeholder="输入关键词进行搜索"
          class="input"></el-input>
      </div>
      <div class="search-box">
        <span>发布时间</span>
        <el-date-picker 
          v-model="searchForm.issuedTime" 
          type="date" 
          placeholder="请选择日期"
          class="input"></el-date-picker>
      </div>
      <div class="btn-group">
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button type="primary" @click="issueNotice">发布公告</el-button>
      </div>
    </div>
    <div class="notice-content">
      <el-table :data="noticeList" :stripe="true" :border="true">
        <el-table-column 
          align="center"
          prop="title"
          label="公告标题"></el-table-column>
        <el-table-column 
          align="center"
          prop="issuedTime"
          label="发布时间"></el-table-column>
        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <p class="watch-span" @click="watchNews(scope.row.id)">查看</p>
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
import { addNotice, noticeList, getNoticeById, uploadFile, deleteFile } from '@/api/notice.js'

// import dateFormat from '@/utils/dateFormat'
import dateFormat from "dateformat"

export default {
  name: 'Notice',
  mounted() {
    this.getNoticeList();
  },
  data() {
    return {
      loading: false,
      searchForm: {
        title: '',
        issuedTime: ''
      },
      noticeList: [],
      // 分页相关
      total: 1,
      curPage: 1,
      pageSize: 10,
      // end
    }
  },
  methods: {
    getNoticeList() {
      this.loading = true;
      const data = {
        pageNum: this.curPage,
        pageSize: this.pageSize
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          if(key === 'issuedTime') {
            let date = this.searchForm[key];
            data[key] = dateFormat(date, "yyyy-mm-dd");
          }else {
            data[key] = this.searchForm[key];
          }
        }
      }
      noticeList(data).then(res => {
        this.loading = false;
        if(res.success) {
          this.total = res.result.total;
          this.noticeList = res.result.records;
        }
      }).catch(() => {
        this.loading = false;
      })
    },
    pageChange(page) {
      this.curPage = page;
      this.getNoticeList();
    },
    watchNews(newsId) {
      console.log(newsId)
      this.$router.push({name: 'NoticeDetails', query: {id: newsId}})
    },
    issueNotice() {
      this.$router.push({name: 'IssueNotice'})
    },
    search() {
      this.curPage = 1;
      this.getNoticeList();
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/variables.scss";
.notice-container {
  @include container;
  .operation-content {
    display: flex;
    height: 80px;
    line-height: 80px;
    width: 90%;
    margin: 0 10px;
    justify-content: space-between;
    .search-box {
      margin: 0 20px;
      display: flex;
      justify-content: center;
      span {
        flex: 0 1 80px;
      }
      .input {
        flex: 1 0 180px
      }
    }
    .btn-group {
      margin: 0 30px;
      ::v-deep .el-button:last-of-type {
        margin-left: 30px;
      }
    }
  }

  .notice-content {
    margin: 20px 30px;
    .watch-span {
      color: $themeColor;
      cursor: pointer;
      &:hover {
        text-decoration: underline;
      }
    }
    .page-block {
      text-align: center;
      margin-top: 20px;
    }
  }
}
</style>