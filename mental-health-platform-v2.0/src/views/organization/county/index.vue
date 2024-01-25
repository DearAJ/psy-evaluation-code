<template>
  <div class="county-organization">
    <div class="search-container">
      <div class="search-box">
        <span>学校&nbsp;&nbsp;&nbsp;</span>
        <el-input class="input" placeholder="请输入学校关键词进行搜索" v-model="search" 
          clearable
          size="medium"
          @change="searchChange">
        </el-input>
      </div>
    </div>
    <div class="search-btn">
      <el-button type="primary" size="medium" @click="getSchoolData">搜索</el-button>
    </div>
    <div class="btn-container">
      <el-button type="primary" size="medium" @click="addSchool">新增</el-button>
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
      <el-button slot="reference" type="primary" size="medium" @click="exportSchool">导出</el-button>
    </div>
    <div class="content-container">
      <el-form :model="schoolForm" ref="schoolForm">
        <el-table :data="schoolList" v-loading="loading" border>
          <el-table-column v-for="item in schoolProps"
            align="center"
            :key="item.prop"
            :min-width="item.minWidth"
            :label="item.label">
            <template slot-scope="scope">
              <span v-if="scope.row.state === 'text'">{{scope.row[item.prop]}}</span>
              <template v-else>
                <el-form-item :rules="item.rules" :prop="item.prop">
                  <el-checkbox-group v-if="item.type === 'checkbox'" v-model="schoolForm[item.prop]">
                    <el-checkbox v-for="box in item.options" :key="box" :label="box"></el-checkbox>
                  </el-checkbox-group>
                  <el-input v-else-if="item.type === 'number'" v-model.number="schoolForm[item.prop]" 
                    :disabled="item.disabled" 
                    :placeholder="item.placeholder"></el-input>
                  <el-input v-else v-model="schoolForm[item.prop]" :disabled="item.disabled" :placeholder="item.placeholder"></el-input>
                </el-form-item>
              </template>
            </template>
          </el-table-column>
          <el-table-column align='center' width="140px"
            label="操作">
            <template slot-scope="scope">
              <template v-if="scope.row.state === 'text'">
                <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
                <el-popconfirm title="确定删除吗" style="margin-left: 5px" @confirm="handleDelete(scope.row)">
                  <el-button slot="reference" size="mini">删除</el-button>
                </el-popconfirm>
              </template>
              <template v-if="scope.row.state === 'edit'">
                <el-button size="mini" @click="submitEdit(scope.row, 'schoolForm')">完成</el-button>
                <el-button size="mini" @click="cancelEdit(scope.row)">取消</el-button>
              </template>
              <template v-if="scope.row.state === 'add'">
                <el-button size="mini" @click="submitAdd(scope.row, 'schoolForm')">完成</el-button>
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
      title="错误提示"
      :visible.sync="notifyDialog"
      :show-close="false"
      :close-on-click-modal="true"
      center>
      <div class="dialog-block">
        <template v-if="schoolsDataNotify.existedData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下学校数据库中已存在, 请删除</p>
          <el-table :data="schoolsDataNotify.existedData"
            border>
            <el-table-column align="left" label="错误提示">
              <template slot-scope="scope">
                <p v-for="item in scope.row.warn.split(';')" :key="item" class="warn-p">
                  <i class="el-icon-caret-right" style="margin-right: 5px"></i><span style="font-size: 14px">{{item}}</span>
                </p>
              </template>
            </el-table-column>
            <el-table-column align="center"
              prop="row"
              label="行号">
            </el-table-column>
            <el-table-column align="center"
              prop="name"
              label="学校名称">
            </el-table-column>
          </el-table>
        </template>
        <template v-if="schoolsDataNotify.errorData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下学生数据格式错误, 请修改为正确格式</p>
          <el-table :data="schoolsDataNotify.errorData"
            border>
            <el-table-column align="left" label="错误提示">
              <template slot-scope="scope">
                <p v-for="item in scope.row.warn.split(';')" :key="item" class="warn-p">
                  <i class="el-icon-caret-right" style="margin-right: 5px"></i><span style="font-size: 14px">{{item}}</span>
                </p>
              </template>
            </el-table-column>
            <el-table-column align="center"
              prop="row"
              label="行号">
            </el-table-column>
            <el-table-column align="center"
              prop="name"
              label="学校名称">
            </el-table-column>
          </el-table>
        </template>
        <template v-if="schoolsDataNotify.repeatedData">
          <p><i class="el-icon-warning" style="color: #F56C6C; margin-right: 10px"></i>以下学生数据重复, 请去除重复数据</p>
          <el-table :data="schoolsDataNotify.repeatedData"
            border>
            <el-table-column align="left" label="错误提示">
              <template slot-scope="scope">
                <p v-for="item in scope.row.warn.split(';')" :key="item" class="warn-p">
                  <i class="el-icon-caret-right" style="margin-right: 5px"></i><span style="font-size: 14px">{{item}}</span>
                </p>
              </template>
            </el-table-column>
            <el-table-column align="center"
              prop="row"
              label="行号">
            </el-table-column>
            <el-table-column align="center"
              prop="name"
              label="学校名称">
            </el-table-column>
          </el-table>
        </template>
        <el-button type="primary" class="btn" @click="notifyDialog = false">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSchoolData, addSchool, deleteSchool, updateSchool, verifySchoolExcel, addSchools, downloadSchoolTemplate } from '@/api/organization'
import { mapGetters } from 'vuex'
import { export2Excel } from '@/utils/excelExport.js'

export default {
  name: 'CountyOrganization',
  computed: {
    ...mapGetters([
      'province',
      'city',
      'county'
    ])
  },
  data() {
    let phone = (rule, value, callback) => {
      let re = /^1(3[0-9]|4[5,7]|5[0,1,2,3,5,6,7,8,9]|6[2,5,6,7]|7[0,1,7,8]|8[0-9]|9[1,8,9])\d{8}$/;
      if (re.test(value)) {
        callback();
      } else {
        callback(new Error('请输入正确的手机号格式'));
      }
    }
    return {
      schoolProps: [{
        prop: 'name',
        label: '学校名称',
        type: 'string',
        rules: [{
          required: true, message: '学校名称不能为空', trigger: 'blur'
        }]
      }, {
        prop: 'address',
        label: '地址',
        type: 'string',
        rules: [{
          required: true, message: '地址不能为空', trigger: 'blur'
        }],
        minWidth: 120
      }, {
        prop: 'schoolLogin',
        label: '登录账号',
        type: 'string',
        disabled: true,
        placeholder: '将由系统自动生成',
        minWidth: 80
      }, {
        prop: 'period',
        label: '所含学段',
        type: 'checkbox',
        options: ['小学', '初中', '高中', '职高', '其他'],
        rules: [{
          required: true, type: 'array', message: '请至少选择一个学段', trigger: 'blur'
        }]
      }, {
        prop: 'studentNumbers',
        label: '学生总人数',
        disabled: true,
        type: 'number',
        placeholder: '将根据学校端上传人数生成',
        minWidth: 50,
        // rules: [{
        //   required: true, message: '学生总人数不能为空', trigger: 'blur'
        // }, {
        //   type: 'number', message: '学生总人数必须为数字'
        // }]
      }, {
        prop: 'studentAccountNumbers',
        label: '生成账号人数',
        disabled: true,
        type: 'number',
        placeholder: '将由系统自动生成',
        minWidth: 80,
        // rules: [{
        //   required: true, message: '生成账号人数不能为空', trigger: 'blur'
        // }, {
        //   type: 'number', message: '生成账号人数必须为数字'
        // }]
      }, {
        prop: 'chargePerson',
        label: '负责人',
        type: 'string',
        rules: [{
          required: true, message: '负责人不能为空', trigger: 'blur'
        }]
      }, {
        prop: 'phone',
        label: '联系电话',
        type: 'string',
        rules: [{required: true, message: '手机号码为必选', trigger: 'blur'}, 
                { validator: phone, trigger: 'blur'}],
      }],
      schoolForm: {},
      schoolList: [],
      curPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      // 文件上传相关参数
      fileForm: {},
      schoolsDataNotify: {},
      notifyDialog: false,
      fileList: [],
      // end  
      isEdit: false, // 表格中是否有编辑或新增的数据，studentForm只能进行一个操作
      search: '', //学校关键词
    }
  },
  mounted() {
    this.getSchoolData();
  },
  methods: {
    getSchoolData() {
      const params = {
        province: this.province,
        city: this.city,
        county: this.county,
        pageNum: this.curPage,
        pageSize: this.pageSize
      }
      this.loading = true;
      if(this.search) {
        params.school = this.search;
      }
      getSchoolData(params).then(res => {
        this.loading = false;
        this.total = res.result.total;
        res.result.records.forEach(val => {
          // state用来标记显示状态
          // text:文字；edit:编辑；add:增加
          val.state = 'text';
        })
        this.schoolList = res.result.records;
      }).catch(() => {
        this.loading = false;
      })
    },
    pageChange(page) {
      this.curPage = page;
      this.getSchoolData();
    },
    handleEdit(row) {
      if(!this.isEdit) {
        this.isEdit = true;
        row.state = 'edit';
        this.schoolForm = JSON.parse(JSON.stringify(row));
        for(let item of this.schoolProps) {
          if (item.type == 'checkbox') {
            this.schoolForm[item.prop] = this.parseString(this.schoolForm[item.prop]);
          }
        }
      }else {
        this.$message('请先完成当前操作')
      }
    },
    handleDelete(row) {
      const data = {id: row.id};
      deleteSchool(data).then(res => {
        if(res.success) {
          this.$message.success('删除成功');
          this.getSchoolData();
        }
      }).catch()
    },
    submitEdit(row, formName) {
      // 提交修改
      this.$refs[formName].validate((valid) => {
        if(valid) {
          for(let item of this.schoolProps) {
            if (item.type == 'checkbox') {
              row[item.prop] = this.parseArray(this.schoolForm[item.prop]);
            }else {
              row[item.prop] = this.schoolForm[item.prop]
            }
          }
          const data = {
            id: row.id,
            name: row.name,
            province: this.province,
            city: this.city,
            county: this.county,
            address: row.address,
            period: row.period,
            studentNumbers: row.studentNumbers,
            chargePerson: row.chargePerson,
            phone: row.phone,
            studentAccountNumbers: row.studentAccountNumbers,
            schoolLogin: row.schoolLogin
          }
          updateSchool(data).then(res => {
            if(res.success) {
              this.$message.success('修改成功');
              this.getSchoolData();
              this.isEdit = false;
            }
          }).catch()
        }else {
          return false;
        }
      })
    },
    cancelEdit(row) {
      row.state = 'text';
      this.isEdit = false;
    },
    addSchool() {
      if(!this.isEdit) {
        this.isEdit = true;
        const school = {};
        for(let item of this.schoolProps) {
          if(item.type == 'checkbox') {
            school[item.prop] = [];
          }else{
            school[item.prop] = '';
          }
        }
        school.state = 'add';
        this.schoolForm = school;
        this.schoolList.unshift(school);
      }else {
        this.$message('请先完成当前操作')
      }
    },
    submitAdd(row, formName) {
      // 提交增加
      this.$refs[formName].validate((valid) => {
        if(valid) {
          for(let item of this.schoolProps) {
            if (item.type == 'checkbox') {
              row[item.prop] = this.parseArray(row[item.prop]);
            }
          }
          const data = {
            id: 0,
            name: row.name,
            province: this.province,
            city: this.city,
            county: this.county,
            address: row.address,
            period: row.period,
            studentNumbers: 0,
            chargePerson: row.chargePerson,
            phone: row.phone,
            studentAccountNumbers: 0,
            schoolLogin: row.schoolLogin
          }
          addSchool(data).then(res => {
            if(res.success) {
              this.$message.success('增加成功');
              this.getSchoolData();
              this.isEdit = false;
            }else {
              for(let item of this.schoolProps) {
                if (item.type == 'checkbox') {
                  row[item.prop] = this.parseString(row[item.prop]);
                }
                item.state = 'add';
              }
            }
          }).catch()
        }else {
          return false;
        }
      })
    },
    cancelAdd() {
      this.schoolList.shift();
      this.isEdit = false;
    },
    // 将数组解析成字符串的形式，以,分隔
    parseArray(arr) {
      return arr.join(',');
    },
    // 将以,分隔的字符串拆分成数组
    parseString(str) {
      return str.split(',');
    },
    templateDownload() {
      downloadSchoolTemplate();
    },
    async fileChange() {
      this.loading = true;
      this.fileForm = new FormData();
      this.fileForm.append('province', this.province);
      this.fileForm.append('city', this.city);
      this.fileForm.append('county', this.county);
      this.$refs.upload.submit();
      try {
        let result = await verifySchoolExcel(this.fileForm);
        result = result.result;
        if(result.baseExistSchoolList.length > 0 || result.errorSchoolList.length > 0 || result.existSchoolList.length > 0) {
          this.fileList = [];
          this.$message.error('上传失败');
          this.loading = false;
          this.schoolsDataNotify.existedData = result.baseExistSchoolList.length > 0 ? result.baseExistSchoolList : '';
          this.schoolsDataNotify.errorData = result.errorSchoolList.length > 0 ? result.errorSchoolList : '';
          this.schoolsDataNotify.repeatedData = result.existSchoolList.length > 0 ? result.existSchoolList : '';
          this.notifyDialog = true;
        }else {
          addSchools(result.rightSchoolList).then(res => {
            this.loading = false;
            this.$message.success('上传成功');
            this.getSchoolData();
            this.fileList = [];
          }).catch(() => {
            this.loading = false;
          })
        }
      } catch(error) {
        this.loading = false;
      }
    },
    appendFile(file) {
      this.fileForm.append('file', file.file);
    },

    exportSchool() {
      const params = {
        province: this.province,
        city: this.city,
        county: this.county,
        pageNum: this.curPage,
        pageSize: -1
      }
      this.loading = true;
      if(this.search) {
        params.school = this.search;
      }
      getSchoolData(params).then(res => {
        console.log(res)
        this.loading = false;
        const headers = this.schoolProps.map(val => val.label);
        const val = this.schoolProps.map(val => val.prop);
        const title = this.county + '学校导出数据';
        export2Excel(headers, val, res.result.records, title);
      }).catch(() => {
        this.loading = false;
      })
    },
    searchChange(val) {
      if(!val) {
        this.getSchoolData();
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/common/organization.scss";

.search-btn {
  right: 20px;
}
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
</style>
