<template>
  <el-dialog :visible.sync="dialogVisible" title="干预表单"
    width="60%"
    :before-close="closeDialog"
    :close-on-click-modal="true">
    <div class="basic-info">
      <el-table :data="basicInfo" border>
        <el-table-column prop="period" label="学段" align="center"></el-table-column>
        <el-table-column prop="grade" label="年级" align="center"></el-table-column>
        <el-table-column prop="classes" label="班级" align="center"></el-table-column>
        <el-table-column prop="name" label="姓名" align="center"></el-table-column>
      </el-table>
    </div>
    <div class="btn-group" v-if="level <= 3">
      <el-button type="primary" size="medium" @click="addPlan" :disabled="!planBtnState">新增干预方案</el-button>
      <el-button size="medium" @click="addRecords" :disabled="!recordBtnState">新增干预记录</el-button>
    </div>
    <div class="record-container">
      <el-table :data="recordsList" border>
        <el-table-column prop="moudle" label="模块" align="center" min-width="90px"></el-table-column>
        <el-table-column prop="date" label="时间" align="center" min-width="90px"></el-table-column>
        <el-table-column label="内容" align="center" min-width="270px">
          <template slot-scope="scope">
            <div class="content-abs">
              <p class="inline c-block">心理老师: {{scope.row.psyTeacher}}</p>
              <p class="inline c-block" v-if="scope.row.moudle !== '干预方案'">干预方式: {{scope.row.methods}}</p>
              <p class="block c-block">记录：{{scope.row.content}}</p>
            </div>
            <div class="content-btn-group">
              <el-button size="mini" @click="checkDetails(scope.row)">查看详情</el-button>
              <el-button size="mini" v-if="!scope.row.isSubmit" @click="updateDetails(scope.row)">编辑</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-dialog>
</template>

<script>
import { getAllInterveneDataById } from '@/api/psyInterveneTask'
import { mapGetters  } from 'vuex';
export default {
  name: 'InterventionRecordList',
  data() {
    return {
      dialogVisible: false,
      recordsList: [],
      basicInfo: [],
      fileId: '',
    }
  },
  computed: {
    ...mapGetters([
      'userId',
      'level'
    ]),
    planBtnState() {
      // 按钮可用为true，不可用为false
      return this.recordsList.length >= 1 && this.recordsList[0].isSubmit;
      // return true;
    },
    recordBtnState() {
      // 可用为true，不可用为false，跟那个是相反的
      return (this.recordsList.length > 2 || (this.recordsList.length == 2 && this.recordsList[0].moudle == '干预方案')) && this.recordsList[0].isSubmit;
      // return true;
    }
  },
  methods: {
    openDialog(data, fileId) {
      this.dialogVisible = true;
      this.basicInfo = data;
      this.fileId = fileId;
      this.getData();
    },
    closeDialog() {
      this.dialogVisible = false;
      this.$emit('closeDialog')
    },
    getData() {
      const data = {
        fileId: this.fileId + '',
        userId: this.userId + ''
      };
      getAllInterveneDataById(data).then(res => {
        if(res.success) {
          this.recordsList = res.result.records;
        }else {
          this.recordsList = [];
        }
      }).catch(err => {
        console.log(err);
        this.recordsList = [];
      })
    },
    addPlan() {
      this.dialogVisible = false;
      this.$emit('addPlan')
    },
    addRecords() {
      this.dialogVisible = false;
      this.$emit('addRecord')
    },
    checkDetails(row) {
      this.dialogVisible = false;
      this.$emit('checkDetails', row);
    },
    updateDetails(row) {
      this.dialogVisible = false;
      this.$emit('updateDetails', row)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
.btn-group {
  margin: 20px 0;
  ::v-deep .el-button {
    border-radius: 18px;
  }
}

.record-container {
  margin-top: 10px;
  .content-abs {
    display: flex;
    flex-wrap: wrap;
    .c-block {
      text-align: left;
      margin: 0 30px 10px 30px;
    }
    .inline {
      width: 40%;
    }
    .block {
      width: 100%;
    }
  }
  .content-btn-group {
    padding: 0 30px;
    text-align: right;
    ::v-deep .el-button {
      border: 1px solid $themeColor;
      border-radius: 15px;
      color: $themeColor;
    }
  }
}
</style>