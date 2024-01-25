<template>
  <div class="student-organization" 
    v-loading.fullscreen.lock="loading"
    :element-loading-text="loadingText">
    <div class="search-container">
      <div class="search-box">
        <span>入学年份&nbsp;</span>
        <el-date-picker v-model="searchForm.entranceYear" type="year"
          placeholder="请选择年份"
          value-format="yyyy">
        </el-date-picker>
      </div>
      <div class="search-box">
        <span>学段&nbsp;&nbsp;</span>
        <el-select v-model="searchForm.period" placeholder="请选择学段" clearable>
          <el-option v-for="item in periodOptions" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>年级&nbsp;&nbsp;</span>
        <el-select v-model="searchForm.grade" :placeholder="searchForm.period ? '请选择年级' : '请先选择学段'" 
          clearable  
          @change="getClasses">
          <el-option v-for="item in gradeOptions[searchForm.period]" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>班级&nbsp;&nbsp;</span>
        <el-select v-model="searchForm.classes" :placeholder="searchForm.grade ? '请选择班级' : '请先选择年级'" clearable>
          <template v-if="searchForm.grade != ''">
            <el-option v-for="item in classesOptions" :key="item" :label="item" :value="item"></el-option>
          </template>
        </el-select>
      </div>
    </div>
    <div class="search-container">
      <div class="search-box">
        <span>学生姓名&nbsp;</span>
        <el-input class="input" placeholder="请输入学生姓名" clearable
          v-model="searchForm.name"></el-input>
      </div>
      <div class="search-box">
        <span>性别&nbsp;&nbsp;</span>
        <el-select v-model="searchForm.sex" placeholder="请选择性别" clearable>
          <el-option label="男" value="男"></el-option>
          <el-option label="女" value="女"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>学号&nbsp;&nbsp;</span>
        <el-input class="input" placeholder="请输入学号" clearable
          v-model="searchForm.studentId"></el-input>
      </div>
      <div class="search-box">
        <span>身份证号&nbsp;&nbsp;</span>
        <el-input class="input" placeholder="请输入身份证号" clearable
          v-model="searchForm.idNumber"></el-input>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="search">搜索</el-button>
    </div>
    <div class="btn-container">
      <el-button type="primary" @click="addStudent" size="medium">新增</el-button>
      <el-button type="primary" @click="templateDownload" size="medium">模板下载</el-button>
      <el-upload ref="upload"
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
          <el-button type="primary" size="medium">批量导入</el-button>
        </el-tooltip>
      </el-upload>
      <el-popconfirm title="点击确认将导出已勾选内容" @confirm="exportStudent">
        <el-button slot="reference" type="primary" size="medium">导出</el-button>
      </el-popconfirm>
      <el-popconfirm title="确认删除已选中的学生吗" @confirm="handleDelete">
        <el-button slot="reference" type="primary" style="margin: 0 10px" size="medium">删除</el-button>
      </el-popconfirm>
      <!-- <el-tooltip effect="light" content="将勾选中的学生升一年级" placement="bottom">
        <el-button type="primary" size="medium" @click="$message('该功能暂未开发')">升级</el-button>
      </el-tooltip> -->
    </div>
    <div class="content-container">
      <el-form :model="studentForm" :rules="studentRules" ref="studentForm">
        <el-table ref="studentTable" :data="studentList" :stripe="true"
          :border="true">
          <el-table-column width="30" align="center">
            <template slot="header" slot-scope="scope">
              <el-checkbox v-model="isSelectedAll" @change="selectAll(scope)"></el-checkbox>
            </template>
            <template slot-scope="scope">
               <el-checkbox v-model="scope.row.selected" @change="handleChange(scope.row)"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="入学年份" align="center" min-width="60px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.entranceYear}}</span>
              <template v-else>
                <el-form-item prop="entranceYear">
                  <el-date-picker v-model="studentForm.entranceYear" type="year" :disabled="scope.row.state == 'edit'"
                    placeholder="选择年份" 
                    size="small"
                    value-format="yyyy">
                  </el-date-picker>
                </el-form-item>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="学段" align="center" min-width="60px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.period}}</span>
              <el-form-item v-else prop="period">
                <el-select v-model="studentForm.period" placeholder="请选择学段" size="small">
                  <el-option v-for="item in periodOptions" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="年级" align="center" min-width="60px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.grade}}</span>
              <el-form-item v-else prop="grade">
                <el-select v-model="studentForm.grade" placeholder="请选择年级"
                  size="small"
                  @change="getClasses">
                  <el-option v-for="item in gradeOptions[studentForm.period]" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="班级" align="center" min-width="50px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.classes}}</span>
              <el-form-item v-else prop="classes">
                <el-input v-model="studentForm.classes" placeholder="请输入班级" size="small"></el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="姓名" align="center" min-width="60px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.name}}</span>
              <el-form-item v-else prop="name">
                <el-input v-model="studentForm.name" size="small" placeholder="请输入"></el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="性别" align="center" min-width="40px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.sex}}</span>
              <el-form-item v-else prop="sex">
                <el-select v-model="studentForm.sex" placeholder="请选择性别" size="small">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="学号" align="center" min-width="60px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.studentId}}</span>
              <el-form-item v-else prop="studentId">
                <el-input v-model="studentForm.studentId" size="small" placeholder="请输入"></el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="身份证号" align="center" min-width="120px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.idNumber}}</span>
              <el-form-item v-else prop="idNumber">
                <el-input v-model="studentForm.idNumber" size="small" placeholder="请输入" class="input-with-select" :disabled="scope.row.state == 'edit'">
                  <el-select v-model="studentForm.idType" slot="prepend" placeholder="请选择证件类型" :disabled="scope.row.state == 'edit'">
                    <el-option label="身份证" value="居民身份证"></el-option>
                    <el-option label="护照" value="护照"></el-option>
                    <el-option label="其他" value="其他"></el-option>
                  </el-select>
                </el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="学籍号" align="center" min-width="80px">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.studentCode}}</span>
              <el-form-item v-else prop="studentCode">
                <el-input v-model="studentForm.studentCode" :disabled="scope.row.state == 'exit'" size="small" placeholder="请输入"></el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column align='center' width="140px"
            label="操作">
            <template slot-scope="scope">
              <template v-if="scope.row.state === 'text'">
                <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              </template>
              <template v-if="scope.row.state === 'edit'">
                <el-button size="mini" @click="submitEdit('studentForm')">完成</el-button>
                <el-button size="mini" @click="cancelEdit(scope.row)">取消</el-button>
              </template>
              <template v-if="scope.row.state === 'add'">
                <el-button size="mini" @click="submitAdd('studentForm')">完成</el-button>
                <el-button size="mini" @click="cancelAdd(scope.row)">取消</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
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
    <el-dialog 
      title="上传失败，请按如下提示对表格进行修改"
      :visible.sync="notifyDialog"
      :show-close="false"
      :close-on-click-modal="true"
      width="70%"
      center>
      <div class="dialog-block">
        <template v-if="studentsDataNotify.existedData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下学生数据库中已存在, 请删除</p>
          <el-table :data="studentsDataNotify.existedData"
            border>
            <el-table-column align="left" min-width="130" label="错误提示">
              <template slot-scope="scope">
                <p v-for="item in scope.row.warn.split(';')" :key="item" class="warn-p">
                  <i class="el-icon-caret-right" style="margin-right: 5px"></i><span style="font-size: 13px">{{item}}</span>
                </p>
              </template>
            </el-table-column>
            <el-table-column align="center" width="50"
              prop="row"
              label="行数">
            </el-table-column>
            <el-table-column align="center"
              prop="name"
              label="姓名">
            </el-table-column>
            <el-table-column align="center"
              prop="studentId"
              label="学号">
            </el-table-column>
            <el-table-column align="center"
              prop="studentCode"
              label="学籍号">
            </el-table-column>
            <el-table-column align="center"
              prop="idNumber"
              label="身份证号">
            </el-table-column>
            <el-table-column align="center"
              prop="entranceYear"
              label="入学年份">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="period"
              label="学段">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="grade"
              label="年级">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="classes"
              label="班级">
            </el-table-column>
          </el-table>
        </template>
        <template v-if="studentsDataNotify.errorData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下学生数据格式错误, 请修改为正确格式</p>
          <el-table :data="studentsDataNotify.errorData"
            border>
            <el-table-column align="left" min-width="130" label="错误提示">
              <template slot-scope="scope">
                <p v-for="item in scope.row.warn.split(';')" :key="item" class="warn-p">
                  <i class="el-icon-caret-right" style="margin-right: 5px"></i><span style="font-size: 13px">{{item}}</span>
                </p>
              </template>
            </el-table-column>
            <el-table-column align="center" width="50"
              prop="row"
              label="行数">
            </el-table-column>
            <el-table-column align="center"
              prop="name"
              label="姓名">
            </el-table-column>
            <el-table-column align="center"
              prop="studentId"
              label="学号">
            </el-table-column>
            <el-table-column align="center"
              prop="studentCode"
              label="学籍号">
            </el-table-column>
            <el-table-column align="center"
              prop="idNumber"
              label="身份证号">
            </el-table-column>
            <el-table-column align="center"
              prop="entranceYear"
              label="入学年份">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="period"
              label="学段">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="grade"
              label="年级">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="classes"
              label="班级">
            </el-table-column>
          </el-table>
        </template>
        <template v-if="studentsDataNotify.repeatedData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下学生数据重复, 请去除重复数据</p>
          <el-table :data="studentsDataNotify.repeatedData"
            border>
            <el-table-column align="left" min-width="130" label="错误提示">
              <template slot-scope="scope">
                <p v-for="item in scope.row.warn.split(';')" :key="item" class="warn-p">
                  <i class="el-icon-caret-right" style="margin-right: 5px"></i><span style="font-size: 13px">{{item}}</span>
                </p>
              </template>
            </el-table-column>
            <el-table-column align="center" width="50"
              prop="row"
              label="行数">
            </el-table-column>
            <el-table-column align="center"
              prop="name"
              label="姓名">
            </el-table-column>
            <el-table-column align="center"
              prop="studentId"
              label="学号">
            </el-table-column>
            <el-table-column align="center"
              prop="studentCode"
              label="学籍号">
            </el-table-column>
            <el-table-column align="center"
              prop="idNumber"
              label="身份证号">
            </el-table-column>
            <el-table-column align="center"
              prop="entranceYear"
              label="入学年份">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="period"
              label="学段">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="grade"
              label="年级">
            </el-table-column>
            <el-table-column align="center" width="70"
              prop="classes"
              label="班级">
            </el-table-column>
          </el-table>
        </template>
        <el-button type="primary" class="btn" @click="notifyDialog = false">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentData, downloadStudentTemplate, verifyStudentExcel, addStudents, addStudent, updateStudent, deleteStudent, getClasses } from '@/api/organization'
import { mapGetters } from 'vuex'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'StudentOrganization',
  computed: {
    ...mapGetters([
      'extra',
      'role',
      'level'
    ])
  },
  data() {
    var validateIdNumber = (rule, value, callback) => {
      if(this.studentForm.idType === '居民身份证') {
        let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/; 
        if(reg.test(value)) {
          callback();
        }else {
          callback(new Error('身份证号输入不合法'))
        }
      }else {
        callback();
      }
    }
    return {
      searchForm: {
        entranceYear: '',
        period: '',
        grade: '',
        classes: '',
        name: '',
        sex: '',
        studentId: '',
        idNumber: '',
      },
      periodOptions: [],
      gradeOptions: {
        '小学': ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
        '初中': ['一年级', '二年级', '三年级'],
        '高中': ['一年级', '二年级', '三年级'],
        '职高': ['一年级', '二年级', '三年级'],
        '其他': ['一年级', '二年级', '三年级'],
      },
      classesOptions: [],
      studentForm: {
        entranceYear: '',
        period: '',
        grade: '',
        classes: '',
        name: '',
        sex: '',
        studentId: '',
        idType: '',
        idNumber: '',
        studentCode: '', 
      },
      studentRules: {
        entranceYear: [{ required: true, message: '请输入入学年份', trigger: 'blur' }],
        period: [{ required: true, message: '请选择学段', trigger: 'blur' }],
        grade: [{ required: true, message: '请选择年级', trigger: 'blur' }],
        classes: [{ required: true, message: '请选择班级', trigger: 'blur' }, 
                  { pattern: /^[0-9]*$/, message: '班级需为数字'}],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        sex: [{ required: true, message: '请选择性别', trigger: 'blur' }],
        studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
        idNumber: [{ required: true, message: '请输入身份证号', trigger: 'blur' },
                   { validator: validateIdNumber, trigger: 'blur', type: 'idNumber'}],
        studentCode: [{ required: true, message: '请输入学籍号', trigger: 'blur' }],
        idType: [{ required: true, message: '请选择证件类型', trigger: 'blur' }],
      },
      // 分页相关
      studentList: [],
      curPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      isSelectedAll: false,
      selectedStudents: [],
      // 上传文件相关参数
      fileForm: {},
      studentsDataNotify: {},
      notifyDialog: false,
      fileList: [],
      // end
      isEdit: false, // 表格中是否有编辑或新增的数据，studentForm只能进行一个操作
      loadingText: ''
    }
  },
  mounted() {
    this.getStudentData();
    // 获取该学校的学段
    this.periodOptions = this.extra.period;
  },
  methods: {
    handleChange(row) {
      if(row.selected) {
        this.selectedStudents.push(row);
      }else {
        let index = this.selectedStudents.indexOf(row);
        this.selectedStudents.splice(index, 1);
      }
      if(this.selectedStudents.length === this.studentList.length) {
        this.isSelectedAll = true;
      }else {
        this.isSelectedAll = false;
      }
    },
    selectAll(val) {
      // 如果点击全选，则将所有的数据都放入selectedStudents中
      if(this.isSelectedAll) {
        this.selectedStudents = this.studentList.slice();
        this.studentList.forEach(val => {
          val.selected = true;
        })
      }else {
        this.selectedStudents = [];
        this.studentList.forEach(val => {
          val.selected = false;
        })
      }
    },
    search() {
      this.curPage = 1;
      this.getStudentData();
    },
    pageChange(page) {
      this.curPage = page;
      this.getStudentData();
    },
    getStudentData() {
      this.loading = true;
      console.log(this.extra);
      const data = {
        role: this.role,
        level: this.level,
        schoolId: this.extra.schoolId,
        pageSize: this.pageSize,
        pageNum: this.curPage
      }
      if (this.level == 2) {
        data.rangeClass = this.extra.classes;
      }
      if(this.searchForm.entranceYear) {
        data.entranceYear = this.searchForm.entranceYear
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
      if(this.searchForm.sex) {
        data.sex = this.searchForm.sex
      }
      if(this.searchForm.studentId) {
        data.studentId = this.searchForm.studentId
      }
      if(this.searchForm.idNumber) {
        data.idNumber = this.searchForm.idNumber
      }
      getStudentData(data).then(res => {
        this.loading = false;
        if(res.success) {
          this.total = res.result.total;
          res.result.records.forEach(val => {
            val.state = 'text';
            val.selected = this.isSelectedAll;
          })
          this.studentList = res.result.records;
        }
      }).catch(() => {
        this.loading = false;
      })
    },
    handleEdit(row) {
      if(!this.isEdit) {
        row.state = 'edit';
        this.studentForm = JSON.parse(JSON.stringify(row));
        this.studentForm.classes = parseInt(this.studentForm.classes)
        this.isEdit = true;
      }else {
        this.$message('请先完成当前操作')
      }
    },
    submitEdit(formName) {
      // 提交修改
      this.$refs[formName].validate((valid) => {
        if(valid) {
          this.loading = true;
          this.isEdit = false;
          let {id, username, name, schoolId, entranceYear, period, grade, classes, sex, studentId, studentCode, idNumber, idType} = this.studentForm;
          const data = {id, username, name, schoolId, entranceYear, period, grade, classes, sex, studentId, studentCode, idNumber, idType}
          data.classes = data.classes + '班';
          updateStudent(data).then(res => {
            this.$message.success(res.message);
            this.loading = false;
            this.getStudentData();
            row.state = 'text'
          }).catch(() => {
            this.loading = false;
          })
        }
      })
    },
    cancelEdit(row) {
      row.state = 'text';
      this.isEdit = false;
    },
    handleDelete() {
      if(this.selectedStudents.length == 0) {
        this.$message('请勾选要删除的学生')
      }else {
        this.loading = true;
        this.loadingText = '删除中，请稍等';
        let data = {};
        data.id = this.selectedStudents.map(val => val.id);
        data.isAll = this.isSelectedAll ? 1 : 0;
        data.schoolId = this.extra.schoolId;
        if (data.isAll == 1) {
          if (this.searchForm.entranceYear || this.searchForm.period || this.searchForm.grade || this.searchForm.classes || this.searchForm.name || this.searchForm.sex || this.searchForm.studentId || this.searchForm.idNumber) {
            data.hasFilter = 1;
          }
          if(this.searchForm.entranceYear) {
            data.entranceYear = this.searchForm.entranceYear
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
          if(this.searchForm.sex) {
            data.sex = this.searchForm.sex
          }
          if(this.searchForm.studentId) {
            data.studentId = this.searchForm.studentId
          }
          if(this.searchForm.idNumber) {
            data.idNumber = this.searchForm.idNumber
          }
        }
        deleteStudent(data).then(res => {
          if(res.success && !res.result) {
            this.$message.success('删除成功');
          }else if(res.result) {
            const str = res.result.join('\n ');
            this.$alert(`<span>身份证号为\n${str}\n的学生有关联数据，不可删除</span>`, '警告', {
              confirmButtonText: '知道了',
              type: 'warning',
               dangerouslyUseHTMLString: true,
              callback: action => {
                this.$message({
                  type: 'success',
                  message: '无关联数据的学生删除成功'
                });
              }
            });
          }
          this.curPage = 1;
          this.getStudentData();
          this.loading = false;
          this.loadingText = '';
        }).catch(() => {
          this.loading = false;
          this.loadingText = '';
        })
      }
    },
    addStudent() {
      if(!this.isEdit) {
        const student = {
          entranceYear: '',
          period: '',
          grade: '',
          classes: '',
          name: '',
          sex: '',
          studentId: '',
          idNumber: '',
          studentCode: '', 
          state: 'add',
          idType: '居民身份证',
        };
        this.studentForm = student;
        this.isEdit = true;
        this.studentList.unshift(student);
      }else {
        this.$message('请先完成当前操作')
      }
    },
    submitAdd(formName) {
      // 提交增加
      this.$refs[formName].validate((valid) => {
        if(valid) {
          this.isEdit = false;
          this.loading = true;
          const data = JSON.parse(JSON.stringify(this.studentForm));
          data.id = 1;
          data.username = this.studentForm.idNumber;
          data.schoolId = this.extra.schoolId;
          data.classes = data.classes + '班';
          delete data.state;
          addStudent(data).then(res => {
            this.loading = false;
            this.$message.success(res.message);
            this.curPage = 1;
            this.getStudentData();
            this.studentForm.state = 'text';
          }).catch(() => {
            this.loading = false;
          })
        }else {
          return false;
        }
      })
    },
    cancelAdd() {
      this.studentList.shift();
      this.isEdit = false;
    },
    templateDownload() {
      downloadStudentTemplate();
    },
    async fileChange() {
      this.loading = true;
      this.loadingText = '正在上传中，数据量大时可能会使用较长时间，请您耐心等待~';
      this.fileForm = new FormData();
      this.fileForm.append('schoolId', this.extra.schoolId);
      this.$refs.upload.submit();
      try{
        let result = await verifyStudentExcel(this.fileForm);
        result = result.result;
        if(result.baseExistStudentList.length > 0 || result.errorStudentList.length > 0 || result.existStudentList.length > 0) {
          this.fileList = [];
          this.loading = false;
          this.loadingText = '';
          this.studentsDataNotify.existedData = result.baseExistStudentList.length > 0 ? result.baseExistStudentList : '';
          this.studentsDataNotify.errorData = result.errorStudentList.length > 0 ? result.errorStudentList : '';
          this.studentsDataNotify.repeatedData = result.existStudentList.length > 0 ? result.existStudentList : '';
          this.notifyDialog = true;
        }else {
          addStudents(result.rightStudentList).then(res => {
            this.loading = false;
            this.loadingText = '';
            this.$message.success('上传成功');
            this.curPage = 1;
            this.getStudentData();
            this.fileList = [];
          }).catch(() => {
            this.loading = false;
            this.loadingText = '';
            this.fileList = [];
          })
        }
      } catch (error) {
        this.loading = false;
        this.loadingText = '';
        this.fileList = [];
      }
    },
    appendFile(file) {
      this.fileForm.append('file', file.file);
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    getClasses(val) {
      if(val) {
        const data = {
          schoolId: this.extra.schoolId,
          period: this.searchForm.period,
          grade: this.searchForm.grade
        }
        getClasses(data).then(res => {
          this.classesOptions = [...new Set(res.result)];
          this.searchForm.classes = '';
        })
      }
    },
    async getAllStudentData() {
      const data = {
        schoolId: this.extra.schoolId,
        pageSize: -1,
        pageNum: 1
      };
      if(this.searchForm.entranceYear) {
        data.entranceYear = this.searchForm.entranceYear
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
      if(this.searchForm.sex) {
        data.sex = this.searchForm.sex
      }
      if(this.searchForm.studentId) {
        data.studentId = this.searchForm.studentId
      }
      if(this.searchForm.idNumber) {
        data.idNumber = this.searchForm.idNumber
      }
      try {
        const res = await getStudentData(data);
        if(res.success) {
          return res.result.records;
        }
      }catch(err) {
        console.log(err)
      }
    },
    async exportStudent() {
      if(this.selectedStudents.length == 0) {
        this.$message('请勾选要导出的学生');
      }else {
        const headers = ['入学年份', '学段', '年级', '班级', '姓名', '性别', '学号', '身份证号', '学籍号'];
        const val = ['entranceYear', 'period', 'grade', 'classes', 'name', 'sex', 'studentId', 'idNumber', 'studentCode'];
        const title = this.extra.school + '学生导出数据';
        let exportData = [];
          if(this.isSelectedAll) {
            try {
              exportData = await this.getAllStudentData();
            }catch(err) {
              console.log(err)
            }
          }else {
            exportData = this.selectedStudents;
          }
          export2Excel(headers, val, exportData, title);
        }
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";
.student-organization {
  @include container;
  .dialog-block {
    margin: 0 auto;
    text-align: center;
    p {
      margin: 20px 0;
      font-size: 16px;
    }
    .btn {
      width: 200px;
      margin-top: 30px;
    }
  }
}
</style>
<style lang="scss">
.student-organization {
  .el-dialog__body {
    height: 60vh;
    overflow: auto;
  }
  .content-container {
    .el-date-editor.el-input {
      width: auto;
    }
    .input-with-select .el-input-group__prepend {
      background-color: #fff;
    }
    .el-select .el-input {
      width: 80px;
    }
  }

  .search-box {
    .el-date-picker {
      width: 220px;
    }
  }
}
</style>