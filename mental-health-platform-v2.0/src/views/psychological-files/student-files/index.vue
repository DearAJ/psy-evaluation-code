<template>
  <div class="student-files-container" v-loading.fullscreen.lock="loading">
    <div class="search-container" v-for="box in searchBoxNum" :key="'sfilesbox' + box.id">
      <div v-for="item in box.boxList" :key="item.key" class="search-box">
        <template v-if="item.type === 'input'">
          <span>{{item.chName}}&nbsp;</span>
          <el-input class="input" :placeholder="'请输入' + item.chName" clearable
            v-model="searchForm[item.key]"
            size="medium">
          </el-input>
        </template>
        <template v-else-if="item.key === 'period'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.period" placeholder="请选择学段" 
            clearable  
            size="medium"
            @change="getClasses">
            <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'grade'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.grade" :placeholder="searchForm.period ? '请选择年级' : '请先选择学段'" 
            clearable  
            size="medium"
            @change="getClasses">
            <el-option v-for="o in item.options[searchForm.period]" :key="o" :label="o" :value="o"></el-option>
          </el-select>
        </template>
        <template v-else-if="item.key === 'classes' && item.type === 'select'">
          <span>{{item.chName}}&nbsp;</span>
          <el-select v-model="searchForm.classes" :placeholder="searchForm.classes ? '请选择班级' : '请先选择年级'" 
            clearable  
            size="medium">
            <template v-if="searchForm.grade != ''">
              <el-option v-for="o in item.options" :key="o" :label="o" :value="o"></el-option>
            </template>
          </el-select>
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
      <el-table :data="studentFilesData" border
        :stripe="true">

        <el-table-column v-for="item in tableColumnInfo.filter((v, idx) => (v.role.includes(level) && idx < 6))" :key="item.prop"
          align="center" 
          :min-width="item.minWidth"
          :prop="item.prop"
          :label="item.label">
        </el-table-column>
        <el-table-column label="年级" align="center">
          <template slot-scope="scope">
            <TableGradeField :row="scope.row" ></TableGradeField>
          </template>
        </el-table-column>
        <el-table-column v-for="item in tableColumnInfo.filter((v, idx) => (v.role.includes(level) && idx > 6))" :key="item.prop"
          align="center" 
          :min-width="item.minWidth"
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
import { getClasses } from '@/api/organization'
import { getStudentFiles } from '@/api/files'
import TableGradeField from '@/components/TableGradeField'

export default {
  name: 'StudentFiles',
  components:{
    TableGradeField
  },
  data() {
    return {
      loading: false,
      searchForm: {
        city: '',
        county: '',
        schoolName: '',
        name: '',
        period: '',
        grade: '',
        classes: '',
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
        prop: 'name',
        label: '姓名',
        role: [ 2, 3, 4, 5, 6 ],
        minWidth: '60px'
      }, {
        prop: 'sex',
        label: '性别',
        role: [2, 3, 4, 5, 6],
        minWidth: '50px'
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
        prop: 'idcard',
        label: '身份证号',
        role: [2, 3, 4, 5, 6],
        minWidth: '120px'
      }, {
        prop: 'isGraduate',
        label: '是否毕业',
        role: [2, 3, 4, 5, 6],
        minWidth: '60px'
      }],
      studentFilesData: [],
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
    if(this.level <= 3) {
      this.searchBox.filter(val => val.key == 'period')[0].options = this.extra.period;
    }else {
      this.searchBox.filter(val => val.key == 'period')[0].options = ['小学', '初中', '高中', '职高', '其他'];
    }
    this.getData();
  },
  methods: {
    getClasses() {
      const data = {
        schoolId: this.extra.schoolId,
        period: this.searchForm.period,
        grade: this.searchForm.grade
      }
      getClasses(data).then(res => {
        this.searchBox.filter(val => val.key == 'classes')[0].options = res.result;
        this.searchForm.classes = '';
      })
    },
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
        identity: 'student'
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      getStudentFiles(data).then(res => {
        this.loading = false;
        // if(res.success) {
          this.total = res.result.total;
          res.result.records.forEach(v => {
            v.isGraduate = v.isGraduate === 1 ? '是' : '否';
          })
          this.studentFilesData = res.result.records;
        // }
        // else {
        //   this.total = 0;
        //   this.studentFilesData = [];
        // }
      }).catch(() => {
        this.total = 0;
        this.studentFilesData = [];
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
        studentId: row.studentId,
        identity: 'student',
        name: row.name,
        classes: row.classes,
        grade: row.grade,
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
.student-files-container {
  @include container;
}
</style>