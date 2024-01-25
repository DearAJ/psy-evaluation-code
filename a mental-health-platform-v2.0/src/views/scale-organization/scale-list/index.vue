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
          <el-option value="是" label="是"></el-option>
          <el-option value="否" label="否"></el-option>
        </el-select>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" @click="search" size="medium">搜索</el-button>
    </div>
    <div class="content-container operation-btn">
      <el-table :data="scaleList" border :stripe="true">
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
            <el-popover trigger="hover"
              width="300">
              <p v-for="item in scope.row.introduction" :key="item">{{item}}</p>
              <el-button slot="reference" size="mini" style="margin-right: 10px">查看</el-button>
            </el-popover>
            <template v-if="scope.row.type == '自定义量表'">
              <el-button size="mini" @click="updateScale(scope.row)">修改</el-button>
              <el-popconfirm title="确认删除？" @confirm="deleteScale(scope.row)">
                <el-button slot="reference" size="mini" style="margin: 0 10px">删除</el-button>
              </el-popconfirm>
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
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getScaleList, deleteScale, getScale } from '@/api/scale.js'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'ScaleList',
  computed: {
    ...mapGetters([
      'userId'
    ])
  },
  mounted() {
    this.getScaleList()
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
      typeOptions: ['心理健康', '人格', '学业', '职业倦怠', '其他']
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
            // val.introduction = val.introduction.split('\\n');
            try{
            let splits = val.introduction.split('######')
            if(splits.length == 2){
              val.introduction = splits[1].split('\\n');
            }else{
              val.introduction = splits[0].split('\\n');
            }
            
            val.type = val.type === 1 ? '系统量表' : '自定义量表';
            val.warningOptions = val.warningOptions === 1 ? '否' : '是';
            }catch (err) {
              console.log(err)
            }  
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
      this.loading = true;
      getScale({id: row.id}).then(res => {
        this.loading = false;
        if(res.success) {
          const scale = res.result[0];
          const data = [];
          let maxNum = 0;
          for(let problem of scale.content) {
            if(!problem.hasOwnProperty('required')) {
              problem.required = true;
            }
            if(problem.type <= 2) {
              maxNum = Math.max(maxNum, problem.options.length);
            }else if(problem.type === 3) {
              maxNum = Math.max(maxNum, problem.gear + 2);
            }
          }
          let headers = ['量表名称', '量表介绍', '量表说明（指导语）', '题号', '题目类型', '题目描述', '是否必填'];
          headers = headers.concat(new Array(maxNum).fill('').map((v, i) => '选项' + (i + 1)));
          let val = ['scaleName', 'introduction', 'conclusion', 'id', 'type', 'main', 'required'];
          val = val.concat(new Array(maxNum).fill('').map((v, i) => 'option' + (i + 1)));
          const typeMap = {
            1: '单选题',
            2: '多选题',
            3: '量表题',
            4: '填空题'
          }
          for(let i = 0; i < scale.content.length; i++) {
            const problem = scale.content[i];
            const column = {
              id: i + 1,
              type: typeMap[problem.type],
              main: problem.main,
              required: problem.required ? '是' : '否'
            }
            if(i === 0) {
              column.scaleName = scale.scaleName;
              column.introduction = scale.introduction;
              column.conclusion = scale.conclusion;
            }
            if(problem.type <= 2) {
              for(let j = 0; j < problem.options.length; j++) {
                column['option' + (j + 1)] = problem.options[j].main;
              }
            }else if(problem.type === 3) {
              column.option1 = problem.min;
              for(let j = 1; j <= problem.gear; j++) {
                column['option' + (j + 1)] = j;
              }
              column['option' + (2 + problem.gear)] = problem.max;
            }
            data.push(column)
          }
          const title = scale.scaleName;
          export2Excel(headers, val, data, title);
        }
      }).catch(err => {
        this.loading = false;
        this.$message.error('获取量表数据失败')
      })
    },
    deleteScale(row) {
      deleteScale({scaleId: row.id}).then(res => {
        if(res.success) {
          if(res.result) {
            this.$message(res.result);
          }else {
            this.$message.success('删除成功')
            this.curPage = 1;
            this.getScaleList()
          }
        }
      })
    },
    updateScale(row) {
      this.$router.push({name: 'AddScale', query: {scaleId: row.id}})
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
}
</style>