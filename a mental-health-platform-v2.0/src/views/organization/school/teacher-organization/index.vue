<template>
  <div class="teacher-organization" 
    v-loading.fullscreen.lock="loading"
    :element-loading-text="loadingText">
    <div class="search-container">
      <div class="search-box">
        <span>姓名&nbsp;</span>
        <el-input class="input" placeholder="请输入姓名" clearable
          v-model="searchForm.name"
          size="medium"></el-input>
      </div>
      <div class="search-box">
        <span>性别&nbsp;&nbsp;</span>
        <el-select v-model="searchForm.sex" placeholder="请选择性别" clearable  
          size="medium">
          <el-option label="男" value="男"></el-option>
          <el-option label="女" value="女"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>身份证号&nbsp;&nbsp;</span>
        <el-input class="input" placeholder="请输入身份证号" clearable
          v-model="searchForm.idNumber"
          size="medium"></el-input>
      </div>
      <div class="search-box">
        <span>手机号&nbsp;&nbsp;</span>
        <el-input class="input" placeholder="请输入手机号" clearable
          v-model="searchForm.phone"
          size="medium"></el-input>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="search">搜索</el-button>
    </div>
    <div class="btn-container">
      <el-button type="primary" size="medium" @click="addTeacher">新增</el-button>
      <el-button type="primary" size="medium" @click="templateDownload">模板下载</el-button>
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
      <el-popconfirm title="点击确认将导出已勾选内容" @confirm="exportTeacher">
        <el-button slot="reference" type="primary" size="medium">导出</el-button>
      </el-popconfirm>
      <el-popconfirm title="确认删除已选中的教师吗" @confirm="handleDelete">
        <el-button slot="reference" type="primary" size="medium" style="margin: 0 10px">删除</el-button>
      </el-popconfirm>
    </div>
    <div class="content-container">
      <el-form :model="teacherForm" :rules="teacherRules" ref="teacherForm">
        <el-table ref="teacherTable" :data="teacherList" :stripe="true"
          :border="true">
          <el-table-column width="30" align="center">
            <template slot="header" slot-scope="scope">
              <el-checkbox v-model="isSelectedAll" @change="selectAll(scope)"></el-checkbox>
            </template>
            <template slot-scope="scope">
               <el-checkbox v-model="scope.row.selected" @change="handleChange(scope.row)"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="姓名" align="center" min-width="80">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.name}}</span>
              <el-form-item v-else prop="name">
                <el-input v-model="teacherForm.name" size="small" placeholder="请输入"></el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="性别" align="center" min-width="80">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.sex}}</span>
              <el-form-item v-else prop="sex">
                <el-select v-model="teacherForm.sex" placeholder="请选择性别" size="small">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="身份证号" align="center" min-width="120">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.idNumber}}</span>
              <el-form-item v-else prop="idNumber">
                <el-input v-model="teacherForm.idNumber" size="small" placeholder="请输入" 
                  class="input-with-select"
                  :disabled="scope.row.state == 'edit'">
                  <el-select v-model="teacherForm.idType" slot="prepend" placeholder="请选择证件类型" 
                    :disabled="scope.row.state == 'edit'">
                    <el-option label="身份证" value="居民身份证"></el-option>
                    <el-option label="护照" value="护照"></el-option>
                  </el-select>
                </el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="手机号" align="center" min-width="120">
            <template slot-scope="scope">
              <span v-if="scope.row.state == 'text'">{{scope.row.phone}}</span>
              <el-form-item v-else prop="teacherCode">
                <el-input v-model="teacherForm.phone" :disabled="scope.row.state == 'exit'" size="small" placeholder="请输入"></el-input>
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
                <el-button size="mini" @click="submitEdit('teacherForm')">完成</el-button>
                <el-button size="mini" @click="cancelEdit(scope.row)">取消</el-button>
              </template>
              <template v-if="scope.row.state === 'add'">
                <el-button size="mini" @click="submitAdd('teacherForm')">完成</el-button>
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
        <template v-if="teachersDataNotify.existedData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下教师数据库中已存在, 请删除</p>
          <el-table :data="teachersDataNotify.existedData"
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
              prop="idNumber"
              label="身份证号">
            </el-table-column>
          </el-table>
        </template>
        <template v-if="teachersDataNotify.errorData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下教师数据格式错误, 请修改为正确格式</p>
          <el-table :data="teachersDataNotify.errorData"
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
              prop="idNumber"
              label="身份证号">
            </el-table-column>
          </el-table>
        </template>
        <template v-if="teachersDataNotify.repeatedData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下教师数据重复, 请去除重复数据</p>
          <el-table :data="teachersDataNotify.repeatedData"
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
              prop="idNumber"
              label="身份证号">
            </el-table-column>
          </el-table>
        </template>
        <el-button type="primary" class="btn" @click="notifyDialog = false">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getTeacherData, downloadTeacherTemplate, verifyTeacherExcel, addTeachers, addTeacher, updateTeacher, deleteTeacher } from '@/api/organization'
import { mapGetters } from 'vuex'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'TeacherOrganization',
  computed: {
    ...mapGetters([
      'extra',
    ])
  },
  data() {
    var validateIdNumber = (rule, value, callback) => {
      if(this.teacherForm.idType === '居民身份证') {
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
        name: '',
        sex: '',
        phone: '',
        idNumber: '',
      },
      teacherForm: {
        name: '',
        sex: '',
        phone: '',
        idNumber: '',
      },
      teacherRules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        sex: [{ required: true, message: '请选择性别', trigger: 'blur' }],
        idNumber: [{ required: true, message: '请输入身份证号', trigger: 'blur' },
                   { validator: validateIdNumber, trigger: 'blur', type: 'idNumber'}],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
      },
      // 分页由后端完成
      teacherList: [],
      curPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      isSelectedAll: false,
      selectedTeachers: [],
      // 上传文件相关参数
      fileForm: {},
      teachersDataNotify: {},
      notifyDialog: false,
      fileList: [],
      // end
      isEdit: false, // 表格中是否有编辑或新增的数据，teacherForm只能进行一个操作
      loadingText: ''
    }
  },
  mounted() {
    this.getTeacherData();
  },
  methods: {
    // 勾选某个多选框
    handleChange(row) {
      if(row.selected) {
        this.selectedTeachers.push(row);
      }else {
        let index = this.selectedTeachers.indexOf(row);
        this.selectedTeachers.splice(index, 1);
      }
      if(this.selectedTeachers.length === this.teacherList.length) {
        this.isSelectedAll = true;
      }else {
        this.isSelectedAll = false;
      }
    },
    // 勾选全选框
    selectAll(val) {
      // 如果点击全选，则将所有的数据都放入selectedTeachers中
      if(this.isSelectedAll) {
        this.selectedTeachers = this.teacherList.slice();
        this.teacherList.forEach(val => {
          val.selected = true;
        })
      }else {
        this.selectedTeachers = [];
        this.teacherList.forEach(val => {
          val.selected = false;
        })
      }
    },
    search() {
      this.curPage = 1;
      this.getTeacherData();
    },
    pageChange(page) {
      this.curPage = page;
      this.getTeacherData();
    },
    getTeacherData() {
      this.loading = true;
      const data = {
        schoolId: this.extra.schoolId,
        pageSize: this.pageSize,
        pageNum: this.curPage
      }
      if(this.searchForm.name) {
        data.name = this.searchForm.name
      }
      if(this.searchForm.sex) {
        data.sex = this.searchForm.sex
      }
      if(this.searchForm.phone) {
        data.phone = this.searchForm.phone
      }
      if(this.searchForm.idNumber) {
        data.idNumber = this.searchForm.idNumber
      }
      getTeacherData(data).then(res => {
        this.loading = false;
        if(res.success) {
          this.total = res.result.total;
          res.result.records.forEach(val => {
            val.state = 'text';
            val.selected = this.isSelectedAll;
          })
          this.teacherList = res.result.records;
        }
      }).catch(() => {
        this.loading = false;
      })
    },
    handleEdit(row) {
      if(!this.isEdit) {
        row.state = 'edit';
        this.teacherForm = JSON.parse(JSON.stringify(row));
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
          let { id, username, name, schoolId, sex, idNumber, idType, phone} = this.teacherForm;
          const data = { id, username, name, schoolId, sex, idNumber, idType, phone }
          updateTeacher(data).then(res => {
            this.$message.success(res.message);
            this.loading = false;
            this.getTeacherData();
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
      if(this.selectedTeachers.length == 0) {
        this.$message('请勾选要删除的教师')
      }else {
        this.loading = true;
        this.loadingText = '删除中，请稍等';
        let data = {};
        data.id = this.selectedTeachers.map(val => val.id);
        data.isAll = this.isSelectedAll ? 1 : 0;
        data.schoolId = this.extra.schoolId;
        if (data.isAll == 1) {
          if (this.searchForm.name || this.searchForm.sex || this.searchForm.phone || this.searchForm.idNumber) {
            data.hasFilter = 1;
          }
          if(this.searchForm.name) {
            data.name = this.searchForm.name
          }
          if(this.searchForm.sex) {
            data.sex = this.searchForm.sex
          }
          if(this.searchForm.phone) {
            data.phone = this.searchForm.phone
          }
          if(this.searchForm.idNumber) {
            data.idNumber = this.searchForm.idNumber
          }
        }
        deleteTeacher(data).then(res => {
          if(res.success && !res.result) {
            this.$message.success('删除成功');
          }else if(res.result) {
            const str = res.result.join('\n ');
            this.$alert(`<span>身份证号为\n${str}\n的教师有关联数据，不可删除</span>`, '警告', {
              confirmButtonText: '知道了',
              type: 'warning',
               dangerouslyUseHTMLString: true,
              callback: action => {
                this.$message({
                  type: 'success',
                  message: '无关联数据的教师删除成功'
                });
              }
            });
          }
          this.curPage = 1;
          this.getTeacherData();
          this.loading = false;
          this.loadingText = '';
        }).catch(() => {
          this.loading = false;
          this.loadingText = '';
        })
      }
    },
    addTeacher() {
      if(!this.isEdit) {
        const teacher = {
          name: '',
          sex: '',
          phone: '',
          idNumber: '',
          state: 'add',
          idType: '居民身份证',
        };
        this.teacherForm = teacher;
        this.isEdit = true;
        this.teacherList.unshift(teacher);
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
          const data = JSON.parse(JSON.stringify(this.teacherForm));
          data.id = 1;
          data.username = this.teacherForm.idNumber;
          data.schoolId = this.extra.schoolId;
          delete data.state;
          addTeacher(data).then(res => {
            this.loading = false;
            this.$message.success(res.message);
            this.curPage = 1;
            this.getTeacherData();
            this.teacherForm.state = 'text';
          }).catch(() => {
            this.loading = false;
          })
        }else {
          return false;
        }
      })
    },
    cancelAdd() {
      this.teacherList.shift();
      this.isEdit = false;
    },
    templateDownload() {
      downloadTeacherTemplate();
    },
    async fileChange() {
      this.loading = true;
      this.loadingText = '正在上传中，数据量大时可能会使用较长时间，请您耐心等待~';
      this.fileForm = new FormData();
      this.fileForm.append('schoolId', this.extra.schoolId);
      this.$refs.upload.submit();
      try{
        let result = await verifyTeacherExcel(this.fileForm);
        result = result.result;
        if(result.baseExistTeacherList.length > 0 || result.errorTeacherList.length > 0 || result.existTeacherList.length > 0) {
          this.fileList = [];
          this.loading = false;
          this.loadingText = '';
          this.teachersDataNotify.existedData = result.baseExistTeacherList.length > 0 ? result.baseExistTeacherList : '';
          this.teachersDataNotify.errorData = result.errorTeacherList.length > 0 ? result.errorTeacherList : '';
          this.teachersDataNotify.repeatedData = result.existTeacherList.length > 0 ? result.existTeacherList : '';
          this.notifyDialog = true;
        }else {
          addTeachers(result.rightTeacherList).then(res => {
            this.loading = false;
            this.loadingText = '';
            this.$message.success('上传成功');
            this.getTeacherData();
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
    async getAllTeacherData() {
      const data = {
        schoolId: this.extra.schoolId,
        pageSize: -1,
        pageNum: 1
      };
      if(this.searchForm.name) {
        data.name = this.searchForm.name
      }
      if(this.searchForm.sex) {
        data.sex = this.searchForm.sex
      }
      if(this.searchForm.phone) {
        data.phone = this.searchForm.phone
      }
      if(this.searchForm.idNumber) {
        data.idNumber = this.searchForm.idNumber
      }
      try {
        const res = await getTeacherData(data);
        if(res.success) {
          return res.result.records;
        }
      }catch(err) {
        console.log(err)
      }
    },
    async exportTeacher() {
      if(this.selectedTeachers.length == 0) {
        this.$message('请勾选要导出的教师');
      }else {
        const headers = [ '姓名', '性别', '身份证号', '手机号' ];
        const val = ['name', 'sex', 'idNumber', 'phone'];
        const title = this.extra.school + '教师导出数据';
        let exportData = [];
        if(this.isSelectedAll) {
          try {
            exportData = await this.getAllTeacherData();
          }catch(err) {
            console.log(err)
          }
        }else {
          exportData = this.selectedTeachers;
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
.teacher-organization {
  @include container;
  .content-container {
    padding: 20px;
    .page-block {
      text-align: center;
      margin-top: 20px;
    }
  }
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
.teacher-organization {
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