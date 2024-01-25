<template>
  <div class="province-organization"
    v-loading.fullscreen.lock="loading">
    <div class="search-container">
      <div class="search-box">
        <span>市&nbsp;</span>
        <el-input class="input" placeholder="请输入市" clearable
          v-model="searchForm.city"
          size="medium"></el-input>
      </div>
      <div class="search-box">
        <span>区县&nbsp;</span>
        <el-input class="input" placeholder="请输入区县" clearable
          v-model="searchForm.county"
          size="medium"></el-input>
      </div>
      <div class="search-box">
        <span>学校&nbsp;</span>
        <el-input class="input" placeholder="请输入学校名称" clearable
          v-model="searchForm.school"
          size="medium"></el-input>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="search">搜索</el-button>
    </div>
    <div class="content-container">
      <el-table ref="schoolTable" :data="schoolList" :stripe="true"
        :border="true">
        <el-table-column prop="city" label="市" min-width="80" align="center"></el-table-column>
        <el-table-column prop="county" label="区县" min-width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="学校" min-width="80" align="center"></el-table-column>
        <el-table-column prop="chargePerson" label="负责人" min-width="120" align="center"></el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="120" align="center"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="160" align="center"></el-table-column>
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
import { getSchoolData } from '@/api/organization'
import { mapGetters } from 'vuex'
export default {
  name: 'ProvinceOrganization',
  computed: {
    ...mapGetters([
      'province',
    ]),
  },
  data() {
    return {
      searchForm: {
        city: '',
        county: '',
        school: '',
      },
      // 分页相关参数
      schoolList: [],
      curPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
    }  
  },
  async mounted() {
    this.getSchoolData();
  },
  methods: {
    search() {
      this.curPage = 1;
      this.getSchoolData();
    },
    pageChange(page) {
      this.curPage = page;
      this.getSchoolData();
    },
    getSchoolData() {
      this.loading = true;
      const data = {
        province: this.province,
        pageNum: this.curPage,
        pageSize: this.pageSize,
      }
      if(this.searchForm.city) {
        data.city = this.searchForm.city
      }
      if(this.searchForm.county) {
        data.county = this.searchForm.county
      }
      if(this.searchForm.school) {
        data.school = this.searchForm.school
      }
      getSchoolData(data).then(res => {
        this.loading = false;
        if(res.success) {
          this.total = res.result.total;
          res.result.records.forEach(val => {
            val.selected = this.isSelectedAll;
          })
          this.schoolList = res.result.records;
        }
      }).catch(() => {
        this.loading = false;
      })
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/common/organization.scss";
.province-organization {
  
}
</style>
