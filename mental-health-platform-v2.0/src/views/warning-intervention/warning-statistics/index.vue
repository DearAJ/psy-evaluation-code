<template>
  <div class="warning-statistics-container" v-loading.fullscreen.lock="loading">
    <div class="search-container" v-for="box in searchBoxNum" :key="'box' + box.id">
      <div v-for="item in box.boxList" :key="item.key" class="search-box">
        <template v-if="item.type === 'input'">
          <span>{{item.chName}}&nbsp;</span>
          <el-input class="input" :placeholder="'请输入' + item.chName" clearable
            v-model="searchForm[item.key]">
          </el-input>
        </template>
        <template v-else-if="item.key === 'period'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.period" placeholder="请选择学段" 
            clearable  
            @change="getClasses">
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'grade'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.grade" :placeholder="searchForm.stuPeriod ? '请选择年级' : '请先选择学段'" 
            clearable  
            @change="getClasses">
            <el-option v-for="o in item.options[searchForm.period]" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'classes' && item.type === 'select'" >
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.classes" :placeholder="searchForm.classes ? '请选择班级' : '请先选择年级'" 
            clearable  >
            <template v-if="searchForm.grade != ''">
              <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
            </template>
          </el-select>
        </template>
        <template v-if="item.type === 'span'">
          <span>{{item.chName}}：&nbsp;{{item.val}}</span>
        </template>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" @click="search" size="medium">搜索</el-button>
    </div>
    <div class="content-container">
      <el-table :data="warningStatisticsData" border
        :stripe="true">
        <el-table-column v-for="item in tableColumnInfo.filter(v => v.role.includes(level))"
          align="center"
          :min-width="item.minWidth"
          :key="item.prop"
          :prop="item.prop"
          :label="item.label">
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
import { getWarningStatistics } from '@/api/psyInterveneTask'
import { mapGetters } from 'vuex'

// import dateFormat from '@/utils/dateFormat'
import dateFormat from "dateformat"
import { getClasses } from '@/api/organization'

export default {
  name: 'WarningStatistics',
  mounted() {
    if(this.level <= 3) {
      this.searchBox.filter(val => val.key == 'period')[0].options = this.extra.period;
    }else {
      this.searchBox.filter(val => val.key == 'period')[0].options = ['小学', '初中', '高中', '职高', '其他'];
    }
    this.getStatisticsData();
  },
  computed: {
    ...mapGetters([
      'extra',
      'level',
      'province',
      'city',
      'county',
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
      searchForm: {
        period: '',
        grade: '',
        classes: '',
      },
      msgLevelMap: {
        2: 'school',
        3: 'school',
        4: 'county',
        5: 'city',
        6: 'province'
      },
      //记录搜索表单都是啥，以省市区公用
      searchBox: [{
        key: 'dateTime',
        chName: '统计时间',
        type: 'span',
        role: [ 6, 5, 4, 3, 2 ],
        val: dateFormat(new Date(), 'yyyy-mm-dd hh:MM:ss')
      }, 
      {
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
        key: 'period',
        chName: '学段',
        type: 'select',
        options: [],
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
        prop: 'schoolName',
        label: '学校',
        role: [ 4, 5, 6 ]
      }, {
        prop: 'period',
        label: '学段',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '50px'
      }, {
        prop: 'grade',
        label: '年级',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }, {
        prop: 'classes',
        label: '班级',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }, {
        prop: 'intervationNums',
        label: '预警人数',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '50px'
      }, {
        prop: 'alevel',
        label: '一级预警人数',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }, {
        prop: 'blevel',
        label: '二级预警人数',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }, {
        prop: 'clevel',
        label: '三级预警人数',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }],
      warningStatisticsData: [],

      loading: false,
      // 与分页相关的
      total: 0,
      curPage: 1,
      pageSize: 10,
    }
  },
  methods: {
    search() {
      this.curPage = 1;
      this.getStatisticsData();
    },
    getStatisticsData() {
      this.loading = true;
      const data = {};
      data.pageNum = this.curPage;
      data.pageSize = this.pageSize;
      if(this.level === 6) {
        data.province = this.province;
        data.msgLevel = 'province';
      }else if(this.level === 5) {
        data.province = this.province;
        data.city = this.city;
        data.msgLevel = 'city'
      }else if(this.level === 4) {
        data.province = this.province;
        data.city = this.city;
        data.county = this.county;
        data.msgLevel = 'county'
      }else if(this.level === 3 || this.level === 2) {
        data.province = this.province;
        data.city = this.city;
        data.county = this.county;
        data.schoolName = this.extra.school;
        data.schoolId = this.extra.schoolId;
        data.msgLevel = 'school'
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      getWarningStatistics(data).then(res => {
        console.log(res)
        this.loading = false;
        if(res.success && res.result.records) {
          this.total = res.result.total;
          this.warningStatisticsData = res.result.records
        }else if(!res.result.records) {
          this.warningStatisticsData = [];
        }
      })
    },
    getClasses(val) {
      const data = {
        schoolId: this.extra.schoolId,
        period: this.searchForm.period,
        grade: this.searchForm.grade
      }
      getClasses(data).then(res => {
        this.searchBox.filter(val => val.key == 'classes')[0].options = [...new Set(res.result)];;
        this.searchForm.stuClass = '';
      })
    },
    pageChange(page) {
      this.curPage = page;
      this.getStatisticsData();
    },
  } 
}
</script>
<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";
.warning-statistics-container {
  @include container;
}
</style>