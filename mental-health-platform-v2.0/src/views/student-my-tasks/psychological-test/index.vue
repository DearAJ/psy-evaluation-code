<template>
  <div class="psychological-test-container">
    <h2 class="task-title">{{taskInfo.name}}-{{scale.scaleName}}</h2>
    <div class="descripe" v-show="titleIndex == -1">
      <p class="title">{{scale.descripeTitle}}</p>
      <div v-html="scale.descripe" class="content"></div>
      <div class="btn">
        <el-button @click="startAnswer" type="primary" :disabled="seconds > 0">开始答题&nbsp;
          <span v-show="seconds > 0">{{seconds}}s</span>
        </el-button>
      </div>
    </div>
    <div v-show="titleIndex >= 0" class="title-number">
      <div class="radius" style="margin-right: 10px" @click="gotoLeftPage" v-show="curPage > 0">
        <i class="el-icon-arrow-left"></i>
      </div>
      <div v-for="item in indexArr.slice(curPage * pageSize, (curPage + 1) * pageSize)" :key="item" 
        class="title-number-block"
        @click="gotoNumber(item)">
        <div :class="{radius: true, actived: item == titleIndex, 
          answered: answerIsNull(answerList[item].answer)}">
          {{item + 1}}
        </div>
      </div>
      <div class="radius" style="margin-right: 10px" @click="gotoRightPage" v-show="curPage < totalPage - 1">
        <i class="el-icon-arrow-right"></i>
      </div>
    </div>
    <div class="question" v-for="(item, index) in scale.content"
      :key="item.id"
      v-show="index == titleIndex">
      <p class="title">
        <span class="is-required" v-if="item.required">*</span>
        {{(index + 1) + '、' + item.main}}
        <span v-if="item.type === 2">（最少选{{item.min}}项，最多选{{item.max}}项）</span>
      </p>
      <!-- 单选题 -->
      <template v-if="item.type === 1">
        <el-radio-group v-model="answerList[index].answer" 
          class="options"
          @change="gotoNext(index)">
          <el-radio v-for="option in item.options" :key="option.id" :label="option.id"> 
            {{option.main}}
          </el-radio>
        </el-radio-group>
      </template>
      <!-- 多选题 -->
      <template v-else-if="item.type === 2">
        <el-checkbox-group v-model="answerList[index].answer" class="options">
          <el-checkbox v-for="option in item.options" :key="option.id" :label="option.id">
            {{option.main}}
          </el-checkbox>
        </el-checkbox-group>
      </template>
      <!-- 评分量表题 -->
      <template v-else-if="item.type === 3">
        <div class="rate-scale-show">
          <p>{{item.min}}</p>
          <el-radio-group v-model="answerList[index].answer" @change="gotoNext(index)">
            <el-radio v-for="(o, i) in new Array(item.gear)" :key="'drateod' + i" :label="i + 1">
              {{i + 1}}
            </el-radio>
          </el-radio-group>
          <p>{{item.max}}</p>
        </div>
      </template>
      <!-- 填空题 -->
      <template v-else-if="item.type === 4">
        <el-input v-model="answerList[index].answer" class="options"></el-input>
      </template>
      <div class="btn">
        <el-button v-show="index != scale.content.length - 1 && (item.type === 2 || item.type === 4)" type="primary" @click="gotoNext(index)">下一题</el-button>
        <el-button v-show="index == scale.content.length - 1" type="primary" @click="submitAnswer" :loading="loading">{{loading ? '正在提交' : '确认提交'}}</el-button>
         <!-- <el-button type="primary" @click="submitAnswer">确认提交</el-button> -->
      </div>
    </div>
  </div>
</template>

<script>
import { getScale, submitAnswer } from '@/api/scale.js'
import { mapGetters } from 'vuex'
export default {
  name: 'PsychologicalTest',
  computed: {
    ...mapGetters([
      'userId',
      'extra'
    ])
  },
  mounted() {
    // to-do 可能要做一个路由拦截，如果没有这个数据要返回到“我的任务”页面
    this.taskInfo = JSON.parse(window.sessionStorage.getItem('selectedTask'));
    this.getScale();
    let that = this;
    let interval = setInterval(() => {
      if(that.seconds > 0) {
        that.seconds--;
      }else {
        clearInterval(interval);
      }
    }, 1000);
  },
  data() {
    return {
      taskInfo: {},
      scale: {},
      titleIndex: -1,
      answerList: [],
      seconds: 5,
      // 题目标号
      curPage: 0,
      pageSize: 10,
      indexArr: [], // 存储页码的数组
      totalPage: 0,
      startTime: '',
      endTime: '',
      loading: false,
    }
  },
  methods: {
    // 获取某个任务的具体信息
    async getScale() {
      this.scale = {
        descripeTitle: '指导语',
        descripe:  '',
        scaleName: '',
        content: [],
      }
      try {
        const res = await getScale({id: parseInt(this.taskInfo.scaleId)})
        const introduction = res.result[0].introduction.split("######")[0].split('\\n');
        this.scale.descripe = '';
        introduction.forEach(v => {
          this.scale.descripe += `<p>${v}</p><p>&nbsp;</p>`;
        })
        this.scale.scaleName = res.result[0].scaleName;
        this.scale.content = res.result[0].content;
        for(let i = 0; i < this.scale.content.length; i++) {
          const question = this.scale.content[i];
          if(!question.hasOwnProperty('required')) {
            question.required = true;
          }
          this.answerList.push({
            id: question.id,
            answer: question.type === 2 ? [] : ''
          });
          this.indexArr.push(i)
        }
        this.totalPage = Math.ceil(this.scale.content.length / this.pageSize);
      } catch( error ) {
        console.log(error)
        this.$router.go(-1)
      }
    },
    startAnswer() {
      this.startTime = new Date();
      this.titleIndex++;
    },
    // // 上一题
    // gotoLast() {
    //   this.titleIndex--;
    //   if(this.titleIndex < this.curPage * this.pageSize) {
    //     this.curPage--;
    //   }
    // },
    gotoNext(index) {
      // 答完题自动跳转
      const curQuestion = this.scale.content[this.titleIndex];
      if(curQuestion.required && !this.answerIsNull(this.answerList[this.titleIndex].answer)) {
        this.$message('请先完成当前题目')
      }else if((curQuestion.required && curQuestion.type === 2) && (this.answerList[this.titleIndex].answer.length < curQuestion.min || this.answerList[this.titleIndex].answer.length > curQuestion.max)) {
        this.$message(`最少选${curQuestion.min}项，最多选${curQuestion.max}项`);
      }else{
        if(index < this.scale.content.length - 1) {
          let that = this;
          setTimeout(() => {
            that.titleIndex++;
            if(that.titleIndex >= (that.curPage + 1) * that.pageSize) {
              that.curPage++;
            }
          }, 200);
        }
      }
    },
    gotoNumber(index) {
      const curQuestion = this.scale.content[this.titleIndex];
      if(index > this.titleIndex && curQuestion.required && !this.answerIsNull(this.answerList[this.titleIndex].answer)) {
        this.$message('请先完成当前题目')
      }else if((curQuestion.required && curQuestion.type === 2) && (this.answerList[this.titleIndex].answer.length < curQuestion.min || this.answerList[this.titleIndex].answer.length > curQuestion.max)) {
        this.$message(`最少选${curQuestion.min}项，最多选${curQuestion.max}项`);
      }else if(index > 0 && !this.judgeIsGoto(index)) {
        this.$message('请按顺序作答')
      }else {
        this.titleIndex = index;
      }
    },
    // 更换题号页码
    gotoLeftPage() {
      if(this.curPage > 0) { 
        this.curPage--;
      }
    },
    gotoRightPage() {
      if(this.curPage < this.totalPage) {
        this.curPage++;
      }
    },
    submitAnswer() {
      let unfinished = this.answerList.filter((val, index) => (this.scale.content[index].required && (!this.answerIsNull(val.answer))));
      if(unfinished.length > 0) {
        this.$message('还有题目未选择');
      }else {
        // 上传答案
        this.loading = true;
        this.endTime = new Date();
        let duration = (this.endTime - this.startTime) / 1000;
        this.answerList.forEach(val => {
          if(val.answer instanceof Array) {
            val.answer = val.answer.join(',');
          }
        })
        console.log(this.answerList)
        const data = {
          userId: this.userId,
          taskId: this.taskInfo.id,
          // source: 1,
          // retest: this.taskInfo.retest, // 是否重做，回头要传过来
          answerList: this.answerList,
          schoolId: this.extra.schoolId,
          duration: duration
        }
        submitAnswer(data).then(res => {
          this.loading = false;
          this.$message.success('提交成功');
          window.sessionStorage.removeItem('selectedTask');
          this.$router.replace('/');
        }).catch(() => {
          this.loading = false;
        })
      }
    },
    answerIsNull(answer) {
      // 不为空，返回ture
      // 为空，返回false
      return (!(answer instanceof Array) && answer) || (answer instanceof Array && answer.length > 0)
    },
    // 判断某个index之前的所有必答题是否回答，从而控制跳转
    judgeIsGoto(index) {
      const arr = this.answerList.filter((val, i) => {
        return (i < index && this.scale.content[i].required && (!this.answerIsNull(val.answer)));
      })
      return arr.length > 0 ? false : true;
    }

  },
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/variables.scss";

.psychological-test-container {
  @include container;
  @include clearfix;
  height: calc(100% - 40px);
  .task-title {
    text-align: center;
    padding: 20px;
    border-bottom: 1px #c9c9c9 solid;
  }
  .descripe {
    padding: 20px 40px;
    position: relative;
    .title {
      font-size: 18px;
    }
    .content {
     font-size: 16px;
     padding: 20px 15px;
   }
  }
  .question {
    padding: 0 5%;
    margin-top: 50px;
    font-size: 18px;
    .title {
      padding-bottom: 20px;
    }
    .options {
      padding: 0 30px;
      ::v-deep .el-radio {
        display: block;
        margin: 20px 0;
      }
      ::v-deep .el-checkbox {
        display: block;
        margin: 20px 0;
      }
    }
    .is-required {
      color: red;
    }
    .rate-scale-show {
      display: flex;
      margin: 20px;
      align-items: center;
      p {
        margin: 0 20px;
        font-size: 14px;
      }
    }
  }
  .btn {
    text-align: center;
    margin: 60px 0 20px 0;
  }
  .title-number {
    margin-top: 40px;
    display: flex;
    padding: 0 5%;
    .title-number-block {
      width: 50px;
    }
    .radius {
      border: 1px #c4c4c4 solid;
      width: 35px;
      height: 35px;
      border-radius: 50%;
      line-height: 35px;
      text-align: center;
      color: #969696;
      font-weight: bold;
      cursor: pointer;
    }
    .actived {
      border: 1px $themeColor solid;
      color: $themeColor;
    }
    .answered {
      background-color: $themeColor;
      color: white;
      border: 1px #ffffff solid;
    }
  }
}
</style>

<style lang="scss">
.psychological-test-container {
  .descripe {
    .content {
      p {
        margin-bottom: 8px;
      }
    }
  }
}
</style>