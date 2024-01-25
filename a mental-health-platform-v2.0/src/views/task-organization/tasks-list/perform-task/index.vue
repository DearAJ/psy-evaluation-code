<template>
  <div class="school-task-container" v-loading.fullscreen.lock="loading"
    :element-loading-text="loadingText">
    <div class="header-box">
      <span>{{taskName}}</span>
      <!-- <div class="btn-group">
        <el-button size="medium" type="primary" @click="templateDownload">下载模板</el-button>
        <el-upload ref="scaleUpload"
          action=""
          :auto-upload="false"
          :limit="1"
          :show-file-list="false"
          :on-change="fileChange"
          :http-request="appendFile"
          :file-list="fileList"
          accept=".xlsx,.xlc,.xls,.xlt"
          class="upload">
          <el-tooltip content="请选择表格" placement="bottom" effect="light">
            <el-button type="primary" size="medium" :disabled='isSubmit == 1'>本地上传</el-button>
          </el-tooltip>
        </el-upload>
      </div> -->
    </div>
    <!-- <div class="header-box">
      <span style="font-size: 14px">任务提醒：若多次上传/测评，系统只提交最新结果，过往测评见“测评报告”</span>
    </div> -->
    <div class="search-container">
      <div class="search-box">
        <span>学段</span>
        <el-select v-model="searchForm.period" placeholder="请选择学段" clearable 
          size="medium" 
          @change="autoSearch">
          <el-option v-for="item in periodOptions" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>年级</span>
        <el-select v-model="searchForm.grade" :placeholder="searchForm.period ? '请选择年级' : '请先选择学段'" clearable 
          size="medium" 
          @change="getClasses">
          <el-option v-for="item in gradeOptions[searchForm.period]" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>班级</span>
        <el-select v-model="searchForm.classes" :placeholder="searchForm.classes ? '请选择班级' : '请先选择年级'" clearable
          size="medium" 
          @change="autoSearch">
          <template v-if="searchForm.grade != ''">
            <el-option v-for="item in classesOptions" :key="item" :label="item" :value="item"></el-option>
          </template>
        </el-select>
      </div>
      <div class="search-box">
        <span>学生姓名</span>
        <el-input v-model="searchForm.name" placeholder="请输入学生名称" clearable size="medium" @change="autoSearch"></el-input>
      </div>
    </div>
    <div class="search-container">
      <div class="search-box">
        <span>学号</span>
        <el-input v-model="searchForm.student_id" placeholder="请输入学号" clearable size="medium" @change="autoSearch"></el-input>
      </div>
      <div class="search-box">
        <span>量表名称</span>
        <el-input v-model="searchForm.scaleName" placeholder="请输入量表名称" clearable size="medium" @change="autoSearch"></el-input>
      </div>
      <div class="search-box">
        <span>是否预警</span>
        <el-select v-model="searchForm.warning" placeholder="请选择是否预警" clearable size="medium" @change="autoSearch">
          <el-option label="是" value="是"></el-option>
          <el-option label="否" value="否"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>是否有效</span>
        <el-select v-model="searchForm.valid" placeholder="请选择是否有效" clearable size="medium" @change="autoSearch">
          <el-option label="是" value="是"></el-option>
          <el-option label="否" value="否"></el-option>
        </el-select>
      </div>
      
      <div class="search-box">
        <span>所属学期</span>
        <el-select v-model="searchForm.semester" placeholder="所属学期" clearable size="medium" @change="autoSearch">
        <el-option
        v-for="item in semesterList"
        :key="item.value"
        :label="item.value"
        :value="item.value"
      ></el-option>
        </el-select>
      </div>
    </div>
    <div class="search-btn">
      <el-button size="medium" type="primary" @click="search">搜索</el-button>
    </div>
    <div class="btn-container">
      <el-popconfirm title="将向测评结果无效的学生发回重测，确定吗" @confirm="handleReset">
        <el-button slot="reference" type="primary" size="medium" style="margin: 0 10px">发回重测</el-button>
      </el-popconfirm>
      
      <el-popconfirm title="点击确认将导出已勾选内容" @confirm="exportData">
        <el-button slot="reference" type="primary" size="medium">导出</el-button>
      </el-popconfirm>
    </div>
    <div class="content-container">
      <el-table :data="finishedTaskList" :stripe="true" :border="true">
        <el-table-column width="30" align="center">
          <template slot="header" slot-scope="scope">
            <el-checkbox v-model="isSelectedAll" @change="selectAll(scope)"></el-checkbox>
          </template>
          <template slot-scope="scope">
              <el-checkbox v-model="scope.row.selected" @change="handleChange(scope.row)"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column label="序号" prop="num" align="center" min-width="40px"></el-table-column>
        <el-table-column label="学段" prop="period" align="center"></el-table-column>
        <el-table-column label="年级" align="center"> 
          <template slot-scope="scope">
            <TableGradeField :row="scope.row" ></TableGradeField>
          </template>
        </el-table-column>
        <el-table-column label="班级" prop="classes" align="center"></el-table-column>
        <el-table-column label="学生姓名" prop="name" align="center"></el-table-column>
        <el-table-column label="学号" prop="student_id" align="center"></el-table-column>
        <el-table-column label="量表名称" prop="scaleName" align="center"></el-table-column>
        <el-table-column label="所属学期" prop="semester" align="center"></el-table-column>
        <el-table-column label="是否有效" prop="valid" align="center"></el-table-column>
        <el-table-column label="是否预警" prop="warning" align="center"></el-table-column>
        <el-table-column align='center' label="测评报告" class="operation">
          <template slot-scope="scope">
            <el-button size="medium" @click="generateReport(scope.row)" :label="scope" :v-if="scope.row.scaleType == 2"  :disabled="scope.row.scaleType != 1">查看</el-button>
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
        <!-- <el-popconfirm title="确认提交任务吗" @confirm="submit">
          <el-button slot="reference" type="primary" :disabled='isSubmit == 1'>确认提交</el-button>
        </el-popconfirm> -->
      </div>
      <el-dialog width="70vw"
        title="上传失败，请按如下提示对表格进行修改"
        :visible.sync="notifyDialog"
        :show-close="false"
        :close-on-click-modal="true"
        center>
        <div class="dialog-block">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下数据存在问题，请按照提示进行修改</p>
          <el-table :data="dataNotify"
            border>
            <el-table-column label="错误提示" align="left" header-align="center" 
              min-width="150px">
              <template slot-scope="scope">
                <p v-for="item in scope.row.warn.split(';')" :key="item" class="warn-p">
                  <i class="el-icon-caret-right" style="margin-right: 5px"></i><span>{{item}}</span>
                </p>
              </template>
            </el-table-column>
            <el-table-column prop="row" label="excel表格中行数" min-width="60px" align="center">
            </el-table-column>
            <el-table-column prop="studentCode" label="学籍号" min-width="60px" align="center">
            </el-table-column>
            <el-table-column prop="name" label="姓名" width="80px" align="center"></el-table-column>
            <el-table-column prop="grade" label="年级" min-width="80px" align="center"></el-table-column>
            <el-table-column prop="classes" label="班级" width="80px" align="center"></el-table-column>
          </el-table>
          <el-button type="primary" class="btn" @click="notifyDialog = false">确认</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { downloadScaleData, verifyScaleData, batchAddScaleData, resetTask, taskExecution, submitTask} from '@/api/task.js'
import { getClasses } from '@/api/organization'
import { mapGetters } from 'vuex'
import { export2Excel } from '@/utils/excelExport.js'
import TableGradeField from '@/components/TableGradeField'
export default {
  name: 'PerformTask',
  components:{
    TableGradeField
  },
  mounted() {
    for (let i = 0; i < 50; i++) {
      this.semesterList.push({
        "value": 2010 + i + "年春季学期"
      }),
      this.semesterList.push({
        "value": 2010 + i + "年秋季学期"
      })
    }
    const params = this.$route.query;
    this.taskId = parseInt(params.DFXCE) / 18;
    this.scaleId = parseInt(params.CGXSG) / 18;
    // this.isSubmit = parseInt(params.HCGDS) / 18;
    this.searchForm.period = params.period ? params.period : '';
    this.searchForm.grade = params.grade ? params.grade : '';
    this.searchForm.classes = params.classes ? params.classes : '';
    this.taskName = params.NN;
    this.periodOptions = this.extra.period;
    this.getFinishedTaskList();
  },
  computed: {
    ...mapGetters([
      'extra'
    ])
  },
  data() {
    return {
      semesterList: [],
      taskId: '',
      scaleId: '',
      taskName: '',
      isSubmit: '',
      searchForm: {
        period: '',
        grade: '',
        classes: '',
        name: '',
        semester: '',
        student_id: '',
        scaleName: '',
        warning: '',
        valid: '',
        loadingText: ''
      },

      finishedTaskList: [], // 全部数据

      isSelectedAll: false,
      selectedRecords: [],

      // 分页相关data
      curPage: 1,
      pageSize: 10,
      total: 0,
      // end
      // 文件上传相关参数
      fileForm: {},
      dataNotify: [],
      notifyDialog: false,
      fileList: [],
      //end
      loading: false,
      loadingText: '',
      periodOptions: [],
      gradeOptions: {
        '小学': ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
        '初中': ['一年级', '二年级', '三年级'],
        '高中': ['一年级', '二年级', '三年级'],
        '职高': ['一年级', '二年级', '三年级'],
        '其他': ['一年级', '二年级', '三年级'],
      },
      classesOptions: [],
    }
  },
  methods: {
    // 勾选某个多选框
    handleChange(row) {
      if(row.selected) {
        this.selectedRecords.push(row);
      }else {
        let index = this.selectedRecords.indexOf(row);
        this.selectedRecords.splice(index, 1);
      }
      if(this.selectedRecords.length === this.finishedTaskList.length) {
        this.isSelectedAll = true;
      }else {
        this.isSelectedAll = false;
      }
    },
    // 勾选全选框
    selectAll(val) {
      console.log(this.isSelectedAll)
      // 如果点击全选，则将所有的数据都放入selectedRecords中
      if(this.isSelectedAll) {
        this.selectedRecords = this.finishedTaskList.slice();
        this.finishedTaskList.forEach(val => {
          val.selected = true;
        })
        console.log(this.selectedRecords)
      }else {
        this.selectedRecords = [];
        this.finishedTaskList.forEach(val => {
          val.selected = false;
        })
      }
    },
    handleReset() {
      if(this.selectedRecords.length == 0) {
        this.$message('请勾选要发回重测的记录')
      }else {
        this.loading = true;
        this.loadingText = '发回重测中，请稍等';
        let data = {};
        data.isAll = this.isSelectedAll ? 1 : 0;
        data.schoolId = this.extra.schoolId;
        data.taskId = this.taskId;
        if (data.isAll == 1) {
          data.total = this.total;
          if (this.searchForm.period || this.searchForm.grade || this.searchForm.classes || this.searchForm.name || this.searchForm.student_id || this.searchForm.warning || this.searchForm.valid) {
            data.hasFilter = 1;
          }
          if(this.searchForm.period) {
            data.period = this.searchForm.period
          }
          if(this.searchForm.grade) {
            data.grade = this.searchForm.grade
          }
          if(this.searchForm.classes) {
            data.classes = this.searchForm.classes
          }
          if(this.searchForm.name) {
            data.name = this.searchForm.name
          }
          if(this.searchForm.student_id) {
            data.studentId = this.searchForm.student_id
          }
          if(this.searchForm.warning) {
            data.warning = this.searchForm.warning
          }
          if(this.searchForm.valid) {
            data.valid = this.searchForm.valid
          }
        } else {
          data.records = this.selectedRecords;
        }
        resetTask(data).then(res => {
          if(res.success && !res.result) {
            this.$message.success('发回重测成功');
          }else if(res.result) {
            const str = res.result.join('\n ');
            this.$alert(`<span>姓名为\n${str}\n的学生有已重测，不可重测</span>`, '警告', {
                confirmButtonText: '知道了',
                type: 'warning',
                dangerouslyUseHTMLString: true,
              callback: action => {
                this.$message({
                  type: 'success',
                  message: '可重测学生发回重测成功'
                });
              }
            });
          }
          this.curPage = 1;
          this.getFinishedTaskList();
          this.loading = false;
          this.loadingText = '';
        }).catch(() => {
          this.loading = false;
          this.loadingText = '';
        })
      }
    },
    templateDownload() {
      downloadScaleData();
    },
    pageChange(page) {
      this.curPage = page;
      this.getFinishedTaskList();
    },
    getFinishedTaskList() {
      this.loading = true;
      // this.finishedTaskList = [];
      this.loadingText = '数据加载中，请稍后';
      const data = {
        taskId: this.taskId,
        schoolId: this.extra.schoolId,
        pageNum: this.curPage,
        pageSize: this.pageSize
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      taskExecution(data).then(res => {
        this.loading = false;
        this.loadingText = '';
        if(res.success && res.result) {
          this.total = res.result.total;
          res.result.records.forEach((val, index) => {
            val.num = index + 1;
            if (val.scaleType == 2) {
              val.valid = '';
              val.warning = '';
            } else {
              val.valid = val.valid == 0 ? '否' : '是';
              val.warning = val.warning == 0 ? '否' : '是';
            }
            val.selected = this.isSelectedAll;
          })
          // console.log(res.result)
          this.finishedTaskList = res.result.records;
        }else {
          this.total = 0;
          this.finishedTaskList = [];
        }
      }).catch(err => {
        this.loading = false;
        this.loadingText = '';
        console.log(err)
      })
    },
    async getAllFinishedTaskList() {
      this.loading = true;
      // this.finishedTaskList = [];
      this.loadingText = '数据加载中，请稍后';
      const data = {
        taskId: this.taskId,
        schoolId: this.extra.schoolId,
        pageNum: this.curPage,
        pageSize: -1
      }
      for(let key in this.searchForm) {
        if(this.searchForm[key]) {
          data[key] = this.searchForm[key];
        }
      }
      let res = await taskExecution(data);
        this.loading = false;
        this.loadingText = '';
        if(res.success && res.result) {
          this.total = res.result.total;
          res.result.records.forEach((val, index) => {
            val.num = index + 1;
            if (val.scaleType == 2) {
              val.valid = '';
              val.warning = '';
            } else {
              val.valid = val.valid == 0 ? '否' : '是';
              val.warning = val.warning == 0 ? '否' : '是';
            }
            val.selected = this.isSelectedAll;
          })
          // console.log(res.result)
          return res.result.records;
        }else {
          return [];
        }
    },
    async fileChange() {
      this.dataNotify = [];
      this.loading = true;
      this.loadingText = '正在上传中，数据量大时可能会使用较长时间，请您耐心等待~';
      this.fileForm = new FormData();
      this.fileForm.append('taskId', this.taskId);
      this.fileForm.append('scaleId', this.scaleId);
      this.$refs.scaleUpload.submit();
      try {
        let result = await verifyScaleData(this.fileForm);
        this.fileList = [];
        result = result.result;
        if(result.errorData.length > 0 || result.existData.length > 0 || result.notExistData.length > 0) {
          this.loading = false;
          this.loadingText = '';
          this.fileList = [];
          this.dataNotify.push(...result.errorData, ...result.existData, ...result.notExistData);
          this.notifyDialog = true;
        }else {
          let { rightData, scaleId, taskId, schoolId } = result;
          const data = { rightData, scaleId, taskId, schoolId }
          batchAddScaleData(data).then(res => {
            this.$message.success('提交成功');
            this.getFinishedTaskList();
            this.loading = false;
            this.loadingText = '';
            this.fileList = [];
          }).catch(() => {
            this.loading = false;
            this.loadingText = '';
            this.fileList = [];
          });
        }
      } catch(err){
        this.loading = false;
        this.loadingText = '';
        this.fileList = [];
        console.log(err)
      };
    },
    appendFile(file) {
      this.fileForm.append('file', file.file);
    },
    search() {
      this.curPage = 1;
      this.getFinishedTaskList();
    },
    autoSearch(val) {
      if(!val) {
        this.search();
      }
    },
    getClasses(val) {
      if(val) {
        this.searchForm.classes = '';
        const data = {
          schoolId: this.extra.schoolId,
          period: this.searchForm.period,
          grade: this.searchForm.grade
        }
        getClasses(data).then(res => {
          this.classesOptions = [...new Set(res.result)];
        }).catch(err => {
          console.log(err)
        })
      }else {
        this.search();
      }
    },
    submit() {
      console.log('submit');
      const data = {
        taskId: this.taskId,
        schoolId: this.extra.schoolId
      }
      submitTask(data).then(res => {
        this.$message.success('提交成功');
        this.isSubmit = 1;
      }).catch(err => {
        console.log(err)
      })
    },
    async exportData() {
      let toExportData = []
      if(this.isSelectedAll){
        toExportData = await this.getAllFinishedTaskList();
      }else{
        toExportData = this.selectedRecords;
      }
       const headers = ["学生姓名", "学号", "学段", "年级", "班级", "量表名称", "是否有效", "是否预警", "所属学期"];
        const val = ["name", "student_id", "period", "grade", "classes", "scaleName","valid", "warning", "semester"];
        // const headers = original_headers;
        // const val = original_headers;

        const title = this.taskName + '任务执行列表数据';

          export2Excel(headers, val, toExportData, title);
    },
    generateReport(row) {
      let { userId, scaleName, name, classes, grade } = row;
      const data = { userId, scaleName, name, classes, grade };
      data.studentId = row.student_id;
      data.taskId = this.taskId;
      data.school = this.extra.school;
      data.type = 'personal';
      data.identity = 'student';
      const routeUrl = this.$router.resolve({
        path: '/report',
        query: data
      })
      window.open(routeUrl.href, "_blank");
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/common/school-task.scss";
@import "@/styles/common/organization.scss";
.content-container {
  .page-block {
    text-align: center;
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>

<style lang="scss">
.school-task-container {
  .header-box, .search-container {
    .el-button {
      border-radius: 40px;
      display: inline-block;
    }
    .el-select {
      width: 100%;
    }
  }

  .el-dialog__body {
    height: 60vh;
    overflow: auto;
  }
}
</style>