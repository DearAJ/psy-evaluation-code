<template>
  <div class="issue-tasks-container">
    <el-form ref="issueTaskForm" :model="taskForm" label-width="80px" 
      class="form-block"
      :rules="rules">
      <el-form-item label="任务名称" prop="name">
        <el-input class="input" v-model="taskForm.name" placeholder="请输入任务名称"></el-input>
      </el-form-item>
      <el-form-item label="任务内容" prop="content">
        <!-- <tinymce v-model="taskForm.content" :height="80" :toolbar="richTextToolbar"></tinymce> -->
        <el-input v-model="taskForm.content" type="textarea" :rows="5" placeholder="请输入任务描述"></el-input>
      </el-form-item>
      <!-- <el-form-item label="测评量表">
        <el-select v-model="taskForm.evaluation" multiple collapse-tags placeholder="请选择测评量表">
          <el-option v-for="option in formOptions.evaluation.options" :key="option" :label="option" :value="option">
          </el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="开始时间" style="width: 300px" prop="startDate">
        <el-date-picker type="date" @input="getSemester(taskForm.startDate)" placeholder="请选择开始时间" v-model="taskForm.startDate" style="width: 100%;"></el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endDate" style="width: 300px">
        <el-date-picker type="date" placeholder="请选择结束时间" v-model="taskForm.endDate" style="width: 100%;"></el-date-picker>
      </el-form-item>
      
      <el-form-item label="所属学期" prop="semester">
        <el-input class="input"  v-model="taskForm.semester" placeholder="所属学期" readonly></el-input>
      </el-form-item>
      <el-form-item label="任务对象" prop="crowd">
        <el-cascader v-model="taskForm.crowd" :options="formOptions.crowd.options" :props="formOptions.crowd.props" 
          @change="getRangeOptions"
          collapse-tags 
          clearable>
        </el-cascader>
      </el-form-item>
      <el-form-item label="任务范围" v-if="level > 3" prop="range">
        <el-cascader ref="range" v-model="taskForm.range" :options="formOptions.range.options"  placeholder="请选择任务范围"
          :props="formOptions.range.props" 
          collapse-tags 
          clearable>
        </el-cascader>
        <!-- <el-select v-model="taskForm.range" multiple collapse-tags placeholder="请选择下发范围">
          <el-option v-for="option in formOptions.range.options" :key="option" :label="option" :value="option">
          </el-option>
        </el-select> -->
      </el-form-item>
      <!-- <el-form-item v-for="item of formLabel" :key="item.name" :label="item.label">
        <template v-if="item.type == 'richText'">
          <tinymce v-model="taskForm[item.name]" :height="80" :toolbar="richTextToolbar"></tinymce>
        </template>
        <template v-else-if="item.type == 'date'" >
            <el-date-picker type="date" placeholder="请选择日期" v-model="taskForm[item.name]"></el-date-picker>
        </template>
        <template v-else-if="item.type == 'select-multi'">
          <el-select v-model="taskForm[item.name]" multiple collapse-tags :placeholder="'请选择' + item.label">
            <el-option v-for="option in item.options" :key="option" :label="option" :value="option">
            </el-option>
          </el-select>
        </template>
        <template v-else-if="item.type == 'cascader'">
          <el-cascader v-model="taskForm[item.name]" :options="item.options" :props="item.props" collapse-tags clearable></el-cascader>
        </template>
        <template v-else>
          <el-input class="input" v-model="taskForm[item.name]" :placeholder="'请输入' + item.label"></el-input>
        </template>
      </el-form-item> -->
      <el-form-item class="form-btn">
        <el-button type="primary" @click="nextStep">下一步，选择量表</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import Tinymce from '@/components/Tinymce'
import { getAllClasses, getAllSchool, getAllCounty, getAllCity } from '@/api/organization'
import { mapGetters } from 'vuex'
// import dateFormat from '@/utils/dateFormat'
import dateFormat from "dateformat"
export default {
  name: 'IssueTasks',
  components: {
    Tinymce
  },
  computed: {
    ...mapGetters([
      'extra',
      'level',
      'province',
      'city',
      'county',
      'level',
      'userId'
    ])
  },
  data() {
    return {
      formOptions: {
        evaluation: {
          options: ['量表1', '量表2', '量表3'],
        },
        crowd: {
          options: [],
          props: {multiple: true}
        },
        range: {
          options: [],
          props: {multiple: true},
          isAll: false,
        }
      },
      taskForm: {
        name: '',
        content: '',
        startDate: '',
        endDate: '',
        semester: '',
        crowd: [],
        range: [],
        selectedSchoolName: []
      },
      rules: {
        name: [{ required: true, message: '请输入任务名称', trigger: 'blur'}],
        content: [{ required: true, message: '请输入任务内容', trigger: 'blur'}],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change'}],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change'}],
        semester: [{ required: true, message: '所属学期', trigger: 'change'}],
        crowd: [{ required: true, message: '请选择任务对象', trigger: 'change'}],
        range: [{ required: true, message: '请选择任务范围', trigger: 'change'}],
      },
      richTextToolbar: ['bold italic underline strikethrough alignleft aligncenter alignright outdent indent  undo redo hr bullist numlist forecolor'],
      gradeOptions: {
        '小学': ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
        '初中': ['一年级', '二年级', '三年级'],
        '高中': ['一年级', '二年级', '三年级'],
        '职高': ['一年级', '二年级', '三年级'],
        '其他': ['一年级', '二年级', '三年级'],
      },
    }
  },
  async mounted() {
    if(this.level <= 3) {
      const allClasses = await this.getAllClasses();
      // console.log('------------>',this.gradeOptions)
      this.formOptions.crowd.options = this.extra.period.map(v => {
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
    }else {
      this.formOptions.crowd.options = [{
        value: '职高',
        label: '职高',
        children: this.gradeOptions['职高'].map(v => { return { value: v, label: v } })
      },{
        value: '高中',
        label: '高中',
        children: this.gradeOptions['高中'].map(v => { return { value: v, label: v } })
      }, {
        value: '初中',
        label: '初中',
        children: this.gradeOptions['初中'].map(v => { return { value: v, label: v } })
      }, {
        value: '小学',
        label: '小学',
        children: this.gradeOptions['小学'].map(v => { return { value: v, label: v } })
      },{
        value: '其他',
        label: '其他',
        children: this.gradeOptions['其他'].map(v => { return { value: v, label: v } })
      },]
    }
  },
  methods: {
    getSemester(date){
      let year = dateFormat(date, 'yyyy')
      let month = dateFormat(date, 'mm')
      let month_int = parseInt(month)
      let semester = '秋季学期'
      if ( month_int < 9 && month_int > 2 ){
        semester = '春季学期'
      }
      
      this.taskForm.semester = dateFormat(date, 'yyyy年') + semester

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
    nextStep() {
      this.$refs.issueTaskForm.validate((valid) => {
        if(valid) {
          let { name, content, startDate, endDate, semester, crowd, range } = this.taskForm;
        console.log(startDate, endDate)
          startDate = dateFormat(startDate, "yyyy-mm-dd");
          endDate = dateFormat(endDate, "yyyy-mm-dd");
        console.log(startDate, endDate)
          const data = { name, content, startDate, endDate , semester};
          if(this.level <= 3) {
            // 学校端
            data.schoolId = this.extra.schoolId;
            data.classes = crowd.map(v => v.join('')).join(',');
          }else {
            // 其它端，schoolId为逗号隔开的字符串格式
            // 还没写呢
            data.schoolId = range.map(v => v.slice(-1)[0].split(',')[0]).join(',');
            data.schoolName = range.map(v => v.slice(-1)[0].split(',')[1]).join(',');
          }
          data.grade = crowd.map(v => v.slice(0, 2).join('')).join(',');
          data.issuedId = this.userId;
          data.id = 1;
          this.$store.commit('issueTask/SET_TASKINFO', data);
          this.$router.push({name: 'SelectScale'});
        }else {
          return false;
        }
      })
    },
    async getRangeOptions() {
      let period = this.taskForm.crowd.map(v => v[0]);
      period = Array.from(new Set(period));
      let rangeOptions = [];
      if(this.level === 6) {
        let allCity = await getAllCity({ province: this.province});
        allCity = allCity.result
        rangeOptions = [{
          value: this.province,
          label: this.province,
          children: allCity
        }]
        const cityArr = rangeOptions[0].children;
        for(let i = 0; i < cityArr.length; i++) {
          let countyList = await getAllCounty({
            province: this.province,
            city: cityArr[i]
          })
          countyList = countyList.result;
          cityArr[i] = {
            value: cityArr[i],
            label: cityArr[i],
            children: countyList
          }
          for(let j = 0; j < countyList.length; j++) {
            let schoolList = await getAllSchool({
              province: this.province,
              city: cityArr[i].value,
              county: countyList[j],
              period: period
            })
            schoolList = schoolList.result.map(v => { return { value: `${v.id},${v.name}`, label: v.name } });
            cityArr[i].children[j] = {
              value: cityArr[i].children[j],
              label: cityArr[i].children[j],
              children: schoolList
            }
          } //for-j
        } // for-i
      }else if(this.level === 5) {
        let allCounty = await getAllCounty({
          province: this.province,
          city: this.city
        })
        allCounty = allCounty.result.map(v => {  return { value: v, label: v} })
        rangeOptions = [{
          value: this.city,
          label: this.city,
          children: allCounty
        }]
        for(let i = 0; i < allCounty.length; i++) {
          let schoolList = await getAllSchool({
            province: this.province,
            city: this.city,
            county: allCounty[i].label,
            period: period
          })
          schoolList = schoolList.result.map(v => { return { value: `${v.id},${v.name}`, label: v.name } });
          rangeOptions[0].children[i].children = schoolList;
        }
      }else if(this.level === 4) {
        let schoolList = await getAllSchool({
          province: this.province,
          city: this.city,
          county: this.county,
          period: period
        })
        schoolList = schoolList.result.map(v => { return { value: `${v.id},${v.name}`, label: v.name } });
        rangeOptions = [{
          value: this.county,
          label: this.county,
          children: schoolList
        }]
      }
      this.formOptions.range.options = rangeOptions;
    }
  }
}
</script>

<style lang="scss" scoped> 
@import "@/styles/mixin.scss";
@import "@/styles/variables.scss";

  .issue-tasks-container {
    height: calc(100% - #{$headerHeight} - #{$navbarHeight});
    margin: 40px 60px;
    display: flex;
    overflow: auto;
    .form-block {
      width: 90%;
      max-height: 95%;
      .input {
        width: 220px;
      }
      .form-btn {
        text-align: center;
        margin-top: 30px;
        ::v-deep .el-button {
          border-radius: 12px;
        }
      }
    }
  }
</style>