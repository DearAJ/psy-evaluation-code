<template>
  <div class="role-organization" 
    v-loading.fullscreen.lock="loading"
    :element-loading-text="loadingText">
    <div class="search-container">
      <div class="search-box">
        <span>姓名&nbsp;</span>
        <el-input class="input" placeholder="请输入姓名" clearable
          @change="searchChange"
          v-model="searchForm.name"
          size="medium"></el-input>
      </div>
      <div class="search-box">
        <span>角色&nbsp;&nbsp;</span>
        <el-select class="input" placeholder="请选择角色" clearable
          v-model="searchForm.role"
          @change="searchChange"
          size="medium">
          <el-option label="心理老师" value="心理老师"></el-option>
          <el-option label="校长" value="校长"></el-option>
          <el-option label="学校管理员" value="学校管理员"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <span>手机号&nbsp;&nbsp;</span>
        <el-input class="input" placeholder="请输入手机号" clearable
          v-model="searchForm.phone"
          @change="searchChange"
          size="medium"></el-input>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="search">搜索</el-button>
    </div>
    <div class="btn-container">
      <el-button type="primary" size="medium" @click="addRole" style="margin-right: 10px;">新增</el-button>
      <el-popconfirm title="点击确认将导出已勾选内容" @confirm="exportRole">
        <el-button slot="reference" type="primary" size="medium">导出</el-button>
      </el-popconfirm>
      <el-popconfirm title="确认删除已选中的角色吗" @confirm="handleDelete">
        <el-button slot="reference" type="primary" size="medium" style="margin: 0 10px">删除</el-button>
      </el-popconfirm>
    </div>
    <div class="content-container">
      <el-table ref="roleTable" :data="roleList" :stripe="true"
        :border="true">
        <el-table-column width="30" align="center">
          <template slot="header" slot-scope="scope">
            <el-checkbox v-model="isSelectedAll" @change="selectAll(scope)"></el-checkbox>
          </template>
          <template slot-scope="scope">
            <el-checkbox v-model="scope.row.selected" @change="handleChange(scope.row)"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" min-width="80" align="center"></el-table-column>
        <el-table-column prop="sex" label="性别" min-width="80" align="center"></el-table-column>
        <el-table-column prop="role" label="角色" min-width="80" align="center"></el-table-column>
        <!-- <el-table-column prop="period" label="学段" min-width="80" align="center"></el-table-column>
        <el-table-column prop="grade" label="年级" min-width="80" align="center"></el-table-column>
        <el-table-column prop="classes" label="班级" min-width="80" align="center"></el-table-column> -->
        <el-table-column prop="username" label="登录账号" min-width="120" align="center"></el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120" align="center"></el-table-column>
        <el-table-column prop="createTime" label="生成时间" min-width="160" align="center"></el-table-column>
        <el-table-column align='center' width="90px"
          label="操作">
          <template slot-scope="scope">
            <el-button size="mini" @click="updateRole(scope.row)">编辑</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" @close="closeDialog"
      :close-on-click-modal="true">
      <el-form :model="roleForm" label-width="70px" :rules="roleRules" ref="roleForm">
        <div class="form-group">
          <p class="title">个人信息</p>
          <div class="form-block">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="roleForm.name"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="roleForm.sex">
                <el-radio label="男"></el-radio>
                <el-radio label="女"></el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="roleForm.phone"></el-input>
            </el-form-item>
          </div>
        </div>
        <div class="form-group">
          <p class="title">角色管理</p>
          <div class="form-block">
            <el-form-item label="角色" prop="role">
              <el-select v-model="roleForm.role" placeholder="请选择角色类别" :disabled="dialogTitle === '编辑角色'">
                <el-option label="心理老师" value="心理老师"></el-option>
                <el-option label="校长" value="校长"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        <div class="form-group" v-if="roleForm.role != '校长'">
          <p class="title">数据范围</p>
          <div class="form-block">
            <el-form-item label="范围" prop="classes">
              <el-cascader v-model="roleForm.classes" :options="rangeInfo.options" :props="rangeInfo.props" 
                collapse-tags 
                clearable>
              </el-cascader>
            </el-form-item>
          </div>
        </div>
        <div class="form-group">
          <p class="title">角色权限</p>
          <div class="form-block">
            <el-checkbox-group v-model="authorityOptions[this.roleForm.role]">
              <el-checkbox v-for="item in authorityOptions[this.roleForm.role]" :key="item"
                :label="item"
                :disabled="true">
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </div>
        <el-form-item class="footer-btn">
          <el-button size="medium" type="primary" @click="submitAdd('roleForm')">提交</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { getSchoolRoleData, addSchoolRole, updateSchoolRole, deleteSchoolRole, getClasses, getAllClasses } from '@/api/organization'
import { mapGetters } from 'vuex'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'RoleOrganization',
  computed: {
    ...mapGetters([
      'extra',
    ]),
  },
  data() {
    return {
      searchForm: {
        name: '',
        role: '',
        phone: '',
      },
      roleForm: {
        name: '',
        sex: '',
        role: '',
        // period: [],
        // grade: [],
        classes: [],
        username: '',
        phone: '',
        generationTime: '', // 生成时间
      },
      roleRules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        sex: [{ required: true, message: '请选择性别', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        role: [{ required: true, message: '请选择角色', trigger: 'blur' }],
        // period: [{ required: true, message: '请先选择学段', trigger: 'blur' }],
        // grade: [{ required: true, message: '请选择年级', trigger: 'blur' }],
        classes: [{ required: true, message: '请选择班级', trigger: 'blur' }],
      },
      // 分页相关参数
      roleList: [],
      curPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      isSelectedAll: false,
      selectedRoles: [],
      // 上传文件相关参数
      // end
      loadingText: '',
      // 新增、编辑dialog
      dialogTitle: '新增角色管理',
      dialogVisible: false,
      gradeOptions: {
        '小学': ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
        '初中': ['一年级', '二年级', '三年级'],
        '高中': ['一年级', '二年级', '三年级'],
        '职高': ['一年级', '二年级', '三年级'],
        '其他': ['一年级', '二年级', '三年级'],
      },
      classesOptions: [],
      authorityOptions: [],
      rangeInfo: {
        options: [],
        props: {multiple: true},
        isAll: false,
      },
      authorityOptions: {
        '心理老师': ['任务管理', '量表管理', '报告管理', '危机干预', '心理档案'],
        '校长': ['查看首页']
      }
    }
  },
  mounted: async function () {
    this.getRoleData();
    const allClasses = await this.getAllClasses();
    this.rangeInfo.options = this.extra.period.map(v => {
      return {
        value: v,
        label: v,
        children: this.gradeOptions[v].map(g => {
          let child = [];
          if(allClasses.hasOwnProperty(v + g)) {
            child = allClasses[v + g];
          }
          return {
            value: g,
            label: g, 
            children: child.map(c => {
              return {
                value: c,
                label: c
              }
            })
          }
        })
      }
    });
  },
  methods: {
    handleChange(row) {
      if(row.selected) {
        this.selectedRoles.push(row);
      }else {
        let index = this.selectedRoles.indexOf(row);
        this.selectedRoles.splice(index, 1);
      }
      if(this.selectedRoles.length === this.roleList.length) {
        this.isSelectedAll = true;
      }else {
        this.isSelectedAll = false;
      }
    },
    getClasses() {
      const data = {
        schoolId: this.extra.schoolId,
        period: this.roleForm.period,
        grade: this.roleForm.grade
      }
      getClasses(data).then(res => {
        this.classesOptions = res.result.slice();
        this.classesOptions.unshift('全部')
        this.searchForm.classes = '';
      })
    },
    async getAllClasses() {
      const data = {
        schoolId: this.extra.schoolId,
      }
      try {
        let res = await getAllClasses(data);
        if(res.success) {
          return res.result;
        }
      }catch (err) {
        console.log(err)
      } 
    },
    selectAll(val) {
      // 如果点击全选，则将所有的数据都放入selectedRoles中
      if(this.isSelectedAll) {
        this.selectedRoles = this.roleList.slice();
        this.roleList.forEach(val => {
          val.selected = true;
        })
      }else {
        this.selectedRoles = [];
        this.roleList.forEach(val => {
          val.selected = false;
        })
      }
    },
    search() {
      this.curPage = 1;
      this.getRoleData();
    },
    pageChange(page) {
      this.curPage = page;
      this.getRoleData();
    },
    getRoleData() {
      this.loading = true;
      const data = {
        schoolId: this.extra.schoolId,
        pageNum: this.curPage,
        pageSize: this.pageSize,
      }
      if(this.searchForm.name) {
        data.name = this.searchForm.name
      }
      if(this.searchForm.role) {
        data.role = this.searchForm.role
      }
      if(this.searchForm.phone) {
        data.phone = this.searchForm.phone
      }
      getSchoolRoleData(data).then(res => {
        this.loading = false;
        if(res.success) {
          this.total = res.result.total;
          res.result.records.forEach(val => {
            val.selected = this.isSelectedAll;
          })
          this.roleList = res.result.records;
        }
      }).catch(() => {
        this.loading = false;
      })
    },
    updateRole(row) {
      this.dialogVisible = true;
      this.dialogTitle = '编辑角色'
      const { name, sex, role, phone, classes, id, username, schoolId } = row;
      this.roleForm = { name, sex, role, phone, classes, id, username, schoolId };
      this.roleForm.classes = this.roleForm.classes.split(',').map(v => {
        return [v.slice(0, 2), v.slice(2, 5), v.slice(5)];
      })
    },
    handleDelete() {
      if(this.selectedRoles.length == 0) {
        this.$message('请勾选要删除的角色')
      }else {
        if(this.selectedRoles.find(v => v.role === '学校管理员')) {
          this.$message.error('管理员不能被删除');
        }else {
          this.loading = true;
          this.loadingText = '删除中，请稍等';
          let data = {};
          data.id = this.selectedRoles.map(val => val.id);
          data.isAll = this.isSelectedAll ? 1 : 0;
          data.schoolId = this.extra.schoolId;
          if (data.isAll == 1) {
            if (this.searchForm.name || this.searchForm.role || this.searchForm.phone) {
              data.hasFilter = 1;
            }
            if(this.searchForm.name) {
              data.name = this.searchForm.name
            }
            if(this.searchForm.role) {
              data.role = this.searchForm.role
            }
            if(this.searchForm.phone) {
              data.phone = this.searchForm.phone
            }
          }
          deleteSchoolRole(data).then(res => {
            this.$message.success('删除成功');
            this.curPage = 1;
            this.getRoleData();
            this.loading = false;
            this.loadingText = '';
          }).catch(() => {
            this.loading = false;
            this.loadingText = '';
          })
        }
      }
    },
    addRole() {
      this.dialogVisible = true;
      this.dialogTitle = '新增角色';
    },
    submitAdd(formName) {
      // 提交增加
      this.$refs[formName].validate((valid) => {
        if(valid) {
          this.loading = true;
          const data = JSON.parse(JSON.stringify(this.roleForm));
          if (this.roleForm.role == '校长') {
            data.classes = '';
          } else {
            data.classes = data.classes.map(v => v.join('')).join(',');
          }
          if(this.dialogTitle === '新增角色') {
            data.id = 1;
            data.username = data.phone;
            data.schoolId = this.extra.schoolId;
            data.unit = this.extra.school;
            addSchoolRole(data).then(res => {
              this.loading = false;
              this.$message.success(res.message);
              this.curPage = 1;
              this.getRoleData();
              this.dialogVisible = false;
            }).catch(() => {
              this.loading = false;
            })
          }else if(this.dialogTitle === '编辑角色') {
            updateSchoolRole(data).then(res => {
              this.loading = false;
              this.$message.success(res.message);
              this.getRoleData();
              this.dialogVisible = false;
            }).catch(() => {
              this.loading = false;
            })
          }
        }else {
          return false;
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    getClasses() {
      const data = {
        schoolId: this.extra.schoolId,
        period: this.searchForm.period,
        grade: this.searchForm.grade
      }
      getClasses(data).then(res => {
        this.classesOptions = [...new Set(res.result)];
        this.searchForm.classes = '';
      })
    },
    exportRole() {
      if(this.selectedRoles.length == 0) {
        this.$message('请勾选要导出的角色');
      }else {
        const headers = ['姓名', '性别', '角色', '登陆账号', '手机号', '生成时间'];
        const val = ['name', 'sex', 'role', 'username', 'phone', 'createTime'];
        const title = this.extra.school + '角色导出数据';
        export2Excel(headers, val, this.selectedRoles, title);
      }
    },
    searchChange(val) {
      if(!val) {
        this.getRoleData();
      }
    },
    closeDialog() {
      this.roleForm = {
        name: '',
        sex: '',
        role: '',
        classes: [],
        username: '',
        phone: '',
        generationTime: '', // 生成时间
      }
    },
    // roleChange() {
    //   if(this.roleForm.role == '心理老师') {
    //     this.authorityOptions = ['任务管理', '量表管理', '报告管理', '危机干预', '心理档案']
    //   }else if(this.roleForm.role == '校长') {
    //     this.authorityOptions = ['查看首页']
    //   }else {
    //     this.authorityOptions = []
    //   }
    // }, 
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";
.role-organization {
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
  .form-group {
    margin: 10px;
    .title {
      font-size: 18px;
      font-weight: 400;
      margin: 10px 0 20px 0;
    }
    .form-block {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      ::v-deep .el-form-item {
        width: 45%;
      }
      ::v-deep .el-input {
        width: 220px;
      }
    }
  }
  .footer-btn {
    margin-top: 40px;
    margin-left: -60px;
  }
}
</style>
<style lang="scss">
.role-organization {
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