<template>
  <div class="teacher-files-container" v-loading.fullscreen.lock="loading">
    <div class="search-container" v-for="box in searchBoxNum" :key="'sfilesbox' + box.id">
      <div v-for="item in box.boxList" :key="item.key" class="search-box">
        <template v-if="item.type === 'input'">
          <span>{{item.chName}}&nbsp;</span>
          <el-input class="input" :placeholder="'请输入' + item.chName" clearable
            v-model="searchForm[item.key]"
            size="medium">
          </el-input>
        </template>
        <template v-else-if="item.type === 'select'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm[item.key]" :placeholder="'请选择' + item.chName" clearable size="medium">
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="search">搜索</el-button>
    </div>
    <div class="content-container">
      <el-table :data="teacherFilesData" border
        :stripe="true">
        <el-table-column v-for="item in tableColumnInfo.filter(v => v.role.includes(level))"
          align="center"
          :min-width="item.minWidth"
          :key="item.prop"
          :prop="item.prop"
          :label="item.label">
        </el-table-column>
        <el-table-column align="center" min-width="70px" label="操作">
          <template slot-scope="scope">
            <el-button size="mini" @click="checkDetails(scope.row)">查看详情</el-button>
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
import { getTeacherFiles } from '@/api/files'

export default {
  name: 'TeacherFiles',
  data() {
    return {
      loading: false,
      searchForm: {
        city: '',
        county: '',
        schoolName: '',
        name: '',
        phoneNumber: '',
        idcard: ''
      },
      searchBox: [{
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
        key: 'name',
        chName: '姓名',
        type: 'input',
        role: [2, 3, 4, 5, 6],
      }, {
        key: 'phoneNumber',
        chName: '手机号',
        type: 'input',
        role: [2, 3, 4, 5, 6]
      }, {
        key: 'idcard',
        chName: '身份证',
        type: 'input',
        role: [2, 3, 4, 5, 6]
      }],
      tableColumnInfo: [{
        prop: 'city',
        label: '市级',
        role: [ 6 ],
        minWidth: '70px'
      }, {
        prop: 'county',
        label: '区县',
        role: [ 5, 6 ],
        minWidth: '70px'
      }, {
        prop: 'schoolName',
        label: '学校',
        role: [ 4, 5, 6 ],
        minWidth: '70px',
      }, {
        prop: 'name',
        label: '姓名',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '70px'
      }, {
        prop: 'sex',
        label: '性别',
        role: [2, 3, 4, 5, 6],
        minWidth: '50px'
      }, {
        prop: 'phoneNumber',
        label: '手机号',
        role: [2, 3, 4, 5, 6],
        minWidth: '120px'
      }, {
        prop: 'idcard',
        label: '身份证号',
        role: [2, 3, 4, 5, 6],
        minWidth: '120px'
      }],
      teacherFilesData: [],
      // 与分页相关的
      total: 0,
      curPage: 1,
      pageSize: 10,
      msgLevelMap: {
        2: 'school',
        3: 'school',
        4: 'county',
        5: 'city',
        6: 'province'
      }
    }
  },
  computed: {
    ...mapGetters([
      'extra',
      'level',
      'province',
      'city',
      'county'
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
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      this.loading = true;
      const data = {
        pageNum: this.curPage,
        pageSize: this.pageSize,
        province: this.province,
        city: this.city,
        county: this.county,
        schoolName: this.extra.school,
        msgLevel: this.msgLevelMap[this.level],
        identity: 'teacher'
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      getTeacherFiles(data).then(res => {
        this.loading = false;
        // if(res.success) {
          this.total = res.result.total;
          this.teacherFilesData = res.result.records;
        // }
      }).catch(() => {
        this.total = 0;
        this.teacherFilesData = [];
        this.loading = false;
      })
    },
    search() {
      this.curPage = 1;
      this.getData();
    },
    pageChange(page) {
      this.curPage = page;
      this.getData();
    },
    checkDetails(row) {
      const data = {
        teacherId: row.teacherId,
        identity: 'teacher',
        name: row.name,
        userId: row.userId,
        school: this.level > 3 ? row.schoolName : this.extra.school
      }
      this.$router.push({name: 'FileDetails', query: data});
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";
.teacher-files-container {
  @include container;
}
</style>