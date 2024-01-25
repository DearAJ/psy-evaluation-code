<template>
  <div class="report-container">
    <div class="report-content cover page0">
      <p class="title">测评报告</p>
      <p class="scale-name">{{coverInfo.scaleName}}</p>
      <div class="personal-info-block">
        <p>姓名：{{coverInfo.name}}</p>
        <p v-if="coverInfo.identity === 'student'">学号：{{coverInfo.studentId}}</p>
        <p v-if="coverInfo.identity === 'student'">班级：{{coverInfo.grade + coverInfo.classes}}</p>
        <p>学校：{{coverInfo.school}}</p>
      </div>
    </div>
    <div class="report-content inner page">
      <p class="title-part">第一部分</p>
      <p class="title-introduce">量表介绍</p>
      <div class="content">
        <p v-for="item in content.scaleIntroduce" :key="item" class="para">{{item}}</p>
      </div>
    </div>
    <template v-if="scaleType === 'mht'">
      <div class="report-content inner page">
        <p class="title-part">第二部分</p>
        <p class="title-introduce">测评结果</p>
        <div class="content">
          <p v-for="item in content.valid.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.total.explain" :key="item" class="para">{{item}}</p>
        </div>
      </div>
      <div class="report-content inner page"
        v-for="block in content.explainPage" :key="block+'e'">
        <p class="title-part">第三部分</p>
        <p class="title-introduce">结果解释</p>
        <div class="content">
          <div v-for="(item) in content.factor.filter(v => v.explainPage === block)" :key="item.key" class="para">
            <p class="content-title">{{item.id}}.{{item.key}}</p>
            <p v-for="s in item.explain" :key="s" class="explain">{{s}}</p>
          </div>
        </div>
      </div>
      <div class="report-content inner page"
        v-for="block in content.advicePage" :key="block+'a'">
        <p class="title-part">第四部分</p>
        <p class="title-introduce">指导建议</p>
        <div class="content">
          <div v-for="(item) in content.factor.filter(v => v.advicePage === block)" :key="item.key" class="para">
            <p class="content-title">{{item.id}}.{{item.key}}</p>
            <p v-for="s in item.advice" :key="s" class="explain">{{s}}</p>
          </div>
        </div>
      </div>
    </template>
    <template v-else-if="scaleType === 'sds'">
      <div class="report-content inner page">
        <p class="title-part">第二部分</p>
        <p class="title-introduce">测评结果</p>
        <div class="content">
          <p v-for="item in content.valid.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.total.explain" :key="item" class="para">{{item}}</p>
        </div>
      </div>
      <div class="report-content inner page"
        v-for="block in content.advicePage" :key="block+'sds'">
        <p class="title-part">第三部分</p>
        <p class="title-introduce">指导建议</p>
        <div class="content">
          <div v-for="item in content.total.advice.filter(v => v.page === block)" :key="item.para" class="para">
            <p class="para">{{item.para}}</p>
          </div>
        </div>
      </div>
    </template>
    <template v-else-if="scaleType === 'child'">
      <div class="report-content inner page">
        <p class="title-part">第二部分</p>
        <p class="title-introduce">测评结果</p>
        <div class="content">
          <p v-for="item in content.valid.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.total.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.module" :key="item" class="para">{{item}}</p>
        </div>
      </div>
    </template>
    <!-- <template v-else-if=" scaleType === 'gad'">
      <div class="report-content inner page">
        <p class="title-part">第二部分</p>
        <p class="title-introduce">测评结果</p>
        <div class="content">
          <p style="float: left">{{content.total.param1[0]}}</p>
          <p style="float: left;color: red">{{content.total.param2[0]}}</p>
          <p >{{content.total.param3[0]}}</p>
        </div>
      </div>
      <div class="report-content inner page" >
        <p class="title-part">第三部分</p>
        <p class="title-introduce">状况分析</p>
        <div class="content" style="line-height: 30px;">
          <p style="float: left;text-indent:20px;">{{content.total.situationAnalysis1 }}</p>
          <p style="float: left;color: red">{{content.total.zcqx }}</p>
          <p >{{content.total.situationAnalysis2 }}</p>
        </div>
      </div>
      <div class="report-content inner page" >
        <p class="title-part">第四部分</p>
        <p class="title-introduce">给您的建议</p>
        <div class="content" >
          <p style="line-height: 30px;text-indent:20px;white-space: pre-wrap;" v-html="content.total.advice"> </p>
        </div>
      </div>
    </template> -->
    <template v-else-if=" scaleType === 'epq'">
      <div class="report-content inner page">
        <p class="title-part">第二部分</p>
        <p class="title-introduce">测评结果</p>
        <div class="content">
          <p v-for="item in content.valid.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.total.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.module" :key="item" class="para">{{item}}</p>
        </div>
      </div>
    </template>
    <template v-else-if=" scaleType === 'MSSMHS'">
      <div class="report-content inner page">
        <p class="title-part">第二部分</p>
        <p class="title-introduce">测评结果</p>
        <div class="content">
          <p v-for="item in content.valid.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.total.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.module" :key="item" class="para">{{item}}</p>
        </div>
      </div>
    </template>
    <template v-else-if="scaleType = 'default'">
      <div class="report-content inner page">
        <p class="title-part">第二部分</p>
        <p class="title-introduce">测评结果</p>
        <div class="content">
          <p v-for="item in content.valid.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.total.explain" :key="item" class="para">{{item}}</p>
          <p v-for="item in content.module" :key="item" class="para">{{item}}</p>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { generatePersonalReport } from '@/api/reportOrganization'
export default {
  name: 'Report',
  data() {
    return {
      coverInfo: {},
      content: {
        scaleIntroduce: [],
        total: {},
        module: [],
        factor: [],
        valid: {}
      },
      scaleType: '',
    }
  },
  mounted() {
    this.coverInfo = this.$route.query;
    // alert(this.coverInfo.name)
    console.log(this.coverInfo)
    this.getReportContent();
    if(this.coverInfo.scaleName.includes('MHT')) {
      this.scaleType = 'mht'
    }else if(this.coverInfo.scaleName.includes('SDS')) {
      this.scaleType = 'sds'
    }else if(this.coverInfo.scaleName.includes('儿童版')) {
      this.scaleType = 'child'
    }else if(this.coverInfo.scaleName.includes('EPQ')) {
      this.scaleType = 'epq'
    }else if(this.coverInfo.scaleName.includes('MSSMHS')) {
      this.scaleType = 'MSSMHS'
    }else {
      this.scaleType = 'default'
    }
  },
  methods: {
    getReportContent() {
      let { userId, taskId } = this.coverInfo;
      const data = { userId, taskId };
      generatePersonalReport(data).then(res => {
        if(res.success) {
          for(const key in res.result) {
            if(key === '量表介绍') {
              // console.log(res.result[key])
              // console.log(res.result[key].split('######'))
            try{
            let splits = res.result[key].split('######')
            if(splits.length == 2){
              this.content.scaleIntroduce = splits[1].split('\\n');
            }else{
              this.content.scaleIntroduce = splits[0].split('\\n');
            }}catch (err) {
              console.log(err)
            }  
              // this.content.scaleIntroduce = res.result[key].split('\\n');
            }else if(key === '总分') {
              this.content.total = {
                score: res.result[key].score,
                key: key,
                explain: ('测评总分为' + res.result[key].score + '分。' + res.result[key].explain).split('\\n'),
              };
              if(this.scaleType === 'sds') {
                this.content.total.advice = res.result[key].advice.split('\\n').map(v => {
                  return {
                    page: 0,
                    para: v
                  }
                })
              }
            }else if(key === '测试可信性') {
              this.content.valid = {
                key: key,
                explain: res.result[key].split('\\n'),
              }
            }else {
              
              let { explain, score, advice } = res.result[key];
              let tmp_str = ''
              if(!isNaN(score)){
                tmp_str = '此部分得为' + score + '分。';
              }
              explain = ( tmp_str + explain).split('\\n');
              // console.log(advice)
              if (advice != null && advice != "") {
                advice = advice.split('\\n');
              }
              const obj = { explain, score, advice };
              obj.key = key;
              obj.id = res.result[key].moduleId;
              this.content.factor.push(obj);
              this.content.module.push(('量表模块' + key + '得分为' + res.result[key].score + '分: ' + res.result[key].explain).split('\\n'));
              // if(this.scaleType != 'epq' && this.scaleType != 'child') {
              //   let { explain, score, advice } = res.result[key];
              //   explain = explain.split('\\n');
              //   advice = advice.split('\\n');
              //   const obj = { explain, score, advice };
              //   obj.key = key;
              //   obj.id = res.result[key].moduleId;
              //   this.content.factor.push(obj);
              // }
            }
          } //for
          // 分页
          // 只表示结果的解释页数和建议页数，一页占30行，一行占30字。
          let words = 28;
          let totalRow = Math.floor((document.body.clientHeight - 240) / 20);
          // mht各部分介绍分页
          if(this.scaleType === 'mht') {
            console.log("-----------------------")
            this.content.factor.sort((a, b) => a.id - b.id)
            let explainPage = 0, advicePage = 0, curExplainRow = 0, curAdviceRow = 0;
            for(let obj of this.content.factor) {
              const allExplain = obj.explain.join('\\n'), allAdvice = obj.advice.join('\\n');
              const explainRows = Math.ceil(allExplain.length / words + (obj.explain.length - 1) + 1); // 加1是加标题
              if(explainRows + curExplainRow > totalRow) {
                // 换页
                explainPage++;
                curExplainRow = explainRows + 1;
              }else {
                curExplainRow = curExplainRow + explainRows + 1;
              }
              const adviceRows = Math.ceil(allAdvice.length / words + (obj.advice.length - 1) + 1); // 加1是加标题
              if(adviceRows + curAdviceRow > totalRow) {
                advicePage++;
                curAdviceRow = adviceRows + 1;
              }else {
                curAdviceRow = curAdviceRow + adviceRows + 1;
              }
              obj.explainPage = explainPage;
              obj.advicePage = advicePage;
            }
            this.content.explainPage = new Array(explainPage + 1).fill(0).map((v, i) => i);
            this.content.advicePage = new Array(advicePage + 1).fill(0).map((v, i) => i)
          } // mht分页
          else if(this.scaleType === 'sds') {
            let advicePage = 0, curAdviceRow = 0;
            for(let obj of this.content.total.advice) {
              const rows = Math.ceil(obj.para.length / words);
              console.log(rows, curAdviceRow)
              if(rows + curAdviceRow > totalRow) {
                // 换页
                advicePage++;
                curAdviceRow = rows + 1;
              }else {
                curAdviceRow = curAdviceRow + rows + 1;
              }
              obj.page = advicePage;
            }
            this.content.advicePage = new Array(advicePage + 1).fill(0).map((v, i) => i);
            console.log(this.content)
          } // sds分页
          else if(this.scaleType === 'MSSMHS') {
            let modulePage = 0, curModuleRow = 0;
            for(let obj of this.content.module) {
              const rows = Math.ceil(obj.para.length / words);
              console.log(rows, curModuleRow)
              if(rows + curModuleRow > totalRow) {
                // 换页
                modulePage++;
                curModuleRow = rows + 1;
              }else {
                curModuleRow = curModuleRow + rows + 1;
              }
              obj.page = modulePage;
            }
            this.content.modulePage = new Array(modulePage + 1).fill(0).map((v, i) => i);
            console.log(this.content)
          } // MSSMHS分页
          else {
            console.log(this.content)
          }
        } 
      })
    },
  }

}
</script>

<style lang="scss" scoped>
@import "@/styles/variables.scss";
.report-container {
  width: 100vw;
  background-color: rgb(240, 240, 240);
  .report-content {
    width: 72vh;
    height: 100vh;
    margin: 0 auto;
    position: relative;
    overflow: hidden;
    font-family: "微软雅黑"
  }
  .cover {
    background: url(../../assets/imgs/background/1.png) no-repeat center center;
    background-size: 100% 100%;
  }

  .inner {
    background: url(../../assets/imgs/background/2.png) no-repeat center center;
    background-size: 100% 100%;
  }

  .page0 {
    color: rgb(77, 77, 77);
    text-align: center;
    .title {
      font-size: 46px;
      font-weight: bold;
      margin-top: 15vh;
    }
    .scale-name {
      margin-top: 50px;
      font-size: 24px;
      font-weight: 600;
    }
    .personal-info-block {
      margin-top: 70px;
      width: 120px;
      position: absolute;
      left: 50%;
      margin-left: -60px;
      text-align: left;
      white-space:nowrap;
      font-size: 14px;
      p {
        margin: 10px 0;
      }
    }
  }

  .page {
    padding: 35px 40px;
    box-sizing: border-box;
    .title-part {
      font-size: 20px;
      color: $reportFontColor;
    }
    .title-introduce {
      font-size: 30px;
      font-weight: 400;
      color: $reportFontColor;
      margin: 20px 0;
    }
    .content {
      padding: 20px 8%;
      font-size: 15px;
      height: calc(100vh - 190px);
      .para {
        margin-bottom: 20px;
      }
      .explain {
        margin: 10px 0;
        font-size: 14px;
      }
    }
  }
}
</style>