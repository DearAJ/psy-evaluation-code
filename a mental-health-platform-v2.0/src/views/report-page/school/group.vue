<template xmlns="http://www.w3.org/1999/html">
  <div class="report-container" id="report-container">
    <div class="report-content cover page0">
      <div style="height: 3%;margin: 50px 94px;float: left">
        <img src="../../../assets/imgs/background/logo.png" />
        <p class="titleBottom" style="width: 80px;color: #5123A5;">赛可乐心理</p>
      </div>
      <p class="titleEn">Group Reports</p>
      <p class="title">&nbsp&nbsp&nbsp团&nbsp;体&nbsp;报&nbsp告</p>
      <p class="scale-name">{{coverInfo.scale}}</p>
      <p class="reportUnit">生成报告单位：{{coverInfo.school}}</p>
      <p class="reportDate">生成报告时间：{{coverInfo.createTime}}</p>
    </div>
    <div class="report-content inner page">
      <div style="display: flex;margin-top: 15px">
        <div style="flex: 1"></div>
        <p class="title-preface">前</p><p class="title-preface" style="color: black">言</p>
        <p class="title-part-preface"></p>
        <div style="flex: 1"></div>
      </div>
      <div class="content">
        <p class="preface-p">心理健康是学生德、智、体、美诸方面全面发展的基础和保证，没有健康的心理状态，很难形成良好的品德。对智力发展和学习来说，健康的心理可以促进学生智力的协调发展，有助于提高学习效率，而意志薄弱、厌学、自卑、粗心、神经衰弱等心理行为问题，则是影响学生学习的主要原因。因此，及时、定期掌握中小学生的心理健康状况，既是保障学生身心健康的需要，也是促进学生全面发展的一项重要工作。
          <br /><br />
          心理健康普查作为了解心理健康水平的最常用的一种手段，是通过心理健康的相关测查量表，来帮助教师了解学生的心理健康状况（如学校生活是否适应良好，生活压力是否过大，是否存在情绪方面的困扰等）的一种方式。
          <br /><br />
          各级教育系统及学校通过心理健康普查工作，可以了解和掌握辖区内学生的心理健康状态和水平，使学校心理健康工作开展有的放矢，为增强学生的心理调适能力、更好地适应其生活和学习、帮助他们培养良好的心理素质提供依据。
          <br /><br />
          通过科学的方法，有效地将可能存在心理问题的学生筛选出来，并根据其严重程度进行危机预警分类，进行早期预防诊断和危机干预，能够有效地减少学生心理健康危机事件发生的概率。
          <br /><br />
          通过建立学生心理健康档案，以心理普查为基础，形成筛查、诊断、干预、服务、跟踪一体化的工作模式，有针性地对做好可能有心理问题学生的后期支持工作，并对问题较严重的学生进行追踪、干预和转介。
        </p>
      </div>
    </div>
    <div class="report-content inner page">
      <div style="display: flex;margin-top: 15px">
        <div style="flex: 1"></div>
        <p class="title-testTool">测</p><p class="title-preface" style="color: black">评工具</p>
        <p class="title-part-preface"></p>
        <div style="flex: 1"></div>
      </div>

      <template v-if="scaleType === 'mht'">
        <div class="content" style="clear: both">
          <p style="width: 180px;height: 25px; background: #9378DB;color: white;float: left;padding-top: 3px">《中小学生心理健康量表》</p>
          <p class="testTool-text">，又称《心理健康测试（MHT）》，是在日本铃木清等人编制的《不安倾向诊断测试》 的基础上，由我国心理学工作者结合中国中小学生的实际情况修订而成，具有较高的信度和效度。本测验共有100 道项目，由8个内容量表（学习焦虑、对人焦虑、孤独倾向、自责倾向、过敏倾向、身体症状、恐怖倾向、冲动倾 向）和1个效度量表（即测谎量表）组成，题量适中，覆盖面广，筛选率高，是如今国内评估中小学生心理健康的 常用量表之一。
          </p>
          <br/><br/>
          <p class="testTool-remark">备注：本测验根据心理健康诊断测验的预警通用标准进行预警，对总分65分及以上，或因子8分及以上进行预警筛查。本测验未对预警程度作出区分，即无常见的二级、三级预警分类，测试者可根据维度分数大小了解症状的轻重程度。
          </p>
        </div>
      </template>
      <template v-if="scaleType === 'sds'">
        <div class="content" style="clear: both">
          <p style="width: 100px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 0px 0px 7px">抑郁自评量表</p>
          <p class="testTool-text">（Self-Rating Depression Scale，SDS）由美国杜克大学教授W.K.Zung编制于1965年，属于美国教育卫生福利部推荐的量表之一，国内版本由王春芳教授等人修订，现常用于心理咨询、抑郁症状筛查及严重程度评定和精神药理学研究。量表一共包含20个项目，能够相当直观地反映成人抑郁的主观感受，也能够了解其最近的心境。北医大精神卫生研究所以及山东大学特聘教授刘贤臣教授考察抑郁自评量表在中国人群中的信效度，结果表明抑郁自评量表在中国人群中的信度为0.862，效度为0.84，表明抑郁自评量表可以很好地作为一个抑郁测评的量表工具。
          </p>
          <br/><br/><br/><br/>
          <p class="testTool-remark">备注：本测验根据心理健康诊断测验的预警通用标准进行预警，对总分65分及以上，或因子8分及以上进行预警筛查。本测验未对预警程度作出区分，即无常见的二级、三级预警分类，测试者可根据维度分数大小了解症状的轻重程度。
          </p>
        </div>
      </template>
    </div>

      <div class="report-content inner page">
        <div style="display: flex;margin-top: 15px">
          <div style="flex: 1"></div>
          <p class="title-testTool">测</p><p class="title-preface" style="color: black">评对象</p>
          <p class="title-part-preface"></p>
          <div style="flex: 1"></div>
        </div>

        <div class="content" style="clear: both">
          <p class="title-part-round"></p>
        <p class="title-part-round2"></p>
        <div class="title-part-testObject" style="clear: both"><p class="testObject-p" style="margin-left: 5%;">本次报告的测评学生数量总计发放：</p>
          <p class="testObject-p testObject-sub">{{coverInfo.totals}}</p><p class="testObject-p">名，男生</p>
          <p class="testObject-p testObject-sub">{{coverInfo.boysNum}}</p><p class="testObject-p">人，女生</p>
          <p class="testObject-p testObject-sub">{{coverInfo.grilsNum}}</p>人。
        </div>

        <div style="clear: both;margin-top: 60px">
          <p class="testTool-remark" style="float: left">学生范围包含</p>
          <p v-if="this.extra.period.toString().split(',')[0]" style="width: 36px;height: 24px; background: #9378DB;color: white;float: left;padding: 4px 1px 3px 4px">{{ this.extra.period.toString().split(',')[0]}}</p><p v-if="this.extra.period.toString().split(',')[1]" style="width: 10px;height: 23px;float: left;padding-top: 5px">、</p>
          <p v-if="this.extra.period.toString().split(',')[1]" style="width: 36px;height: 24px; background: #9378DB;color: white;float: left;padding: 4px 1px 3px 4px">{{this.extra.period.toString().split(',')[1]}}</p><p v-if="this.extra.period.toString().split(',')[2]" style="width: 10px;height: 23px;float: left;padding-top: 5px">、</p>
          <p v-if="this.extra.period.toString().split(',')[2]" style="width: 36px;height: 24px; background: #9378DB;color: white;float: left;padding: 4px 1px 3px 4px">{{this.extra.period.toString().split(',')[2]}}</p><p v-if="this.extra.period.toString().split(',')[3]" style="width: 10px;height: 23px;float: left;padding-top: 5px">、</p>
          <p v-if="this.extra.period.toString().split(',')[3]" style="width: 36px;height: 24px; background: #9378DB;color: white;float: left;padding: 4px 1px 3px 4px">{{this.extra.period.toString().split(',')[3]}}</p><p style="width: 10px;height: 23px;float: left;padding-top: 5px">&nbsp;。</p>
        </div>
      </div>

    </div>
    <div class="report-content inner page">
      <div style="display: flex;margin-top: 35px">
        <div style="flex: 1"></div>
        <p class="title-testBaseInfo">测</p><p class="title-testBaseInfo" style="color: black">评基本情况</p>
        <p class="title-part-preface"></p>
        <div style="flex: 1"></div>
      </div>

      <el-table :data="tableData"  ref="tableData" stripe style="width: auto" border :header-cell-style="{background:'#E7D5F8FF',color:'#232225FF'}" >
        <el-table-column prop="start" label="开始时间" align="center" > </el-table-column>
        <el-table-column prop="end" label="结束时间" align="center"> </el-table-column>
        <el-table-column prop="taskName" label="测评情况" align="center"> </el-table-column>
        <el-table-column prop="scaleName" label="测评量表" align="center"> </el-table-column>
      </el-table>
      <br/><br/>
      <el-table :data="tableData" stripe style="width: auto;" border :header-cell-style="{background:'#E7D5F8FF',color:'#232225FF'}" >
        <el-table-column prop="completeNumbers" label="完成测评人数" align="center" > </el-table-column>
        <el-table-column prop="completePercent" label="完成率" align="center"> </el-table-column>
        <el-table-column prop="validNumbers" label="有效测评数量" align="center"> </el-table-column>
        <el-table-column prop="validPercent" label="有效率" align="center"> </el-table-column>
      </el-table>

      <div style="display: flex;margin-top: 75px">
        <div style="flex: 1"></div>
        <p class="title-test-result">测</p><p class="title-test-result" style="color: black">评结果</p>
        <p class="title-part-preface"></p>
        <div style="flex: 1"></div>
      </div>


      <div class="title-part-testObject" style="font-size: 15px;margin-top: 10px;">
        <p class="testObject-p">检出人数：</p>
        <p class="testObject-p testObject-sub">{{totalWarning}}</p>&nbsp;人

        <div style="font-size: 23px;margin-top: 40px; ">
          <p style="float: left;margin-left:35%">检出比例</p>
          <p style="margin-left: 105%;width: 100%">男女检出占比情况</p>
        </div>
        <circular-diagram :pieData="warnPercent" :title="'检出比例'" style="margin-left:-5%;float: left"></circular-diagram>
        <pie :id="'pWarningPie'" :pieData="bgPercent" :totalNum="totalWarning" :title="'男女检出占比情况'" style="margin-left: 80%; "></pie>
      </div>
    </div>
    <div class="report-content inner page">
      <template v-if="scaleType === 'mht'">
        <div style="display: flex;margin-top: 15px">
          <div style="flex: 1"></div>
          <p class="title-testBaseInfo">维</p><p class="title-testBaseInfo" style="color: black">度检出情况</p>
          <p class="title-part-preface"></p>
          <div style="flex: 1"></div>
        </div>
        <div style="font-size: 20px;">
          <LineBar :id="'dimensionWarningBar'" :xLabel="dimensionName" :yValue="dimensionNumber" :yValue2="dimensionNumber2" :title="''" :total="coverInfo.validNumbers+''"></LineBar>
        </div>
      </template>

      <div style="display: flex;margin-top: 35px;margin-left: 30px">
        <div style="flex: 1"></div>
        <p class="title-testBaseInfo">年</p><p class="title-testBaseInfo" style="color: black">级检出情况</p>
        <p class="title-part-preface"></p>
        <div style="flex: 1"></div>
      </div>
      <div style="font-size: 20px;">
        <LineBar :id="'gradeWarningBar'" :xLabel="gradeName" :yValue="gradeNumber" :yValue2="gradeNumber2" :title="'各年级总分检出人数及比例'" :total="gradeNumberFit + ''"></LineBar>
      </div>
    </div>

<!--      <div class="report-content inner page">
        <div style="display: flex;margin-top: 35px">
          <div style="flex: 1"></div>
          <p class="title-testBaseInfo">总</p><p class="title-testBaseInfo" style="color: black">得分情况表</p>
          <p class="title-part-preface"></p>
          <div style="flex: 1"></div>
        </div>

        <el-table :data="tableTotalScore"  ref="tableData" stripe style="width: 100%;" border :header-cell-style="{background:'#E7D5F8FF',color:'#232225FF'}" >
          <el-table-column v-for="item in tableTotalScoreCols" align="center"
                           :prop="item.prop"
                           :key="item.prop"
                           :label="item.label">
          </el-table-column>
        </el-table> <br/>
        <p class="testTool-remark">注：心理健康得分越高，心理健康状况越不佳</p>
        <br/><br/>
        <div style="display: flex;margin-top: 35px">
          <div style="flex: 1"></div>
          <p class="title-testBaseInfo">各</p><p class="title-testBaseInfo" style="color: black">维度得分情况表</p>
          <p class="title-part-preface"></p>
          <div style="flex: 1"></div>
        </div>

        <el-table :data="tableDimensionScore"  ref="tableData" stripe style="width: 100%;padding: 0 0 0 0" border :header-cell-style="{background:'#E7D5F8FF',color:'#232225FF'}" >
          <el-table-column v-for="item in tableDimensionScoreCols" align="center" :prop="item.prop" :key="item.prop" :label="item.label" :width="77">
          </el-table-column>
        </el-table> <br/>
      </div>-->


    <template v-if="scaleType === 'mht'">
      <div class="report-content inner page">
        <div style="display: flex;margin-top: 35px">
          <div style="flex: 1"></div>
          <p class="title-testTool">指</p><p class="title-testTool" style="color: black">导建议</p>
          <p class="title-part-preface"></p>
          <div style="flex: 1"></div>
        </div>

        <p style="font-size: 15px;font-family: Microsoft YaHei;margin-top: 30px">本建议为参考建议，请结合学生实际情况开展工作。</p>
        <p class="guide-number">（1）总分</p>
        <p style="width: 60px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 10px;margin-top: 3vh;">0-55分</p>
        <p class="guide-desc">测评结果表明受测者总体上处于心理健康状态，分数越低健康状态越好。需注意单个方面。</p>

        <p style="width: 60px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 10px;margin-top: 3vh;">56-64分</p>
        <p class="guide-desc">测评结果表明受测者心理状态欠佳或有问题倾向，需要进一步分析，必要时开展心理辅导。</p>

        <p style="width: 60px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 10px;margin-top: 3vh;">65-90分</p>
        <p class="guide-desc">测评结果表明受测者存在较严重的心理问题倾向，需要进一步分析，同时开展心理辅导。</p>


        <p class="guide-number">（2）学习焦虑</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者保持放松的学习状态，明确考试只是对现有知识水平、当前掌握程度的测验，要将注意力放在自己真正学到的知识上，自己收获的成长中。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">建议受测者正确看待自己偶尔出现的焦虑情绪，学习呼吸放松、想象放松的方法来缓解紧张情绪。正确看待学习知识与考试之间的关系，考试知识对现有知识水平掌握程度的测验，而不是定义未来的东西。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者正确看待自己的学习焦虑，适当放松自己，然后运用适当的方法缓解焦虑，必要时为受测者提供心理咨询或心理辅导。在认知调节上，首先要客观的对待考试，考试只不过是对现有知识水平、掌握并运用知识解决问题的能力的一种综合测验。如果正确地认识这一点，精神上就不会太过紧张。其次不要把考试看成是自己成才的唯一出路，这样会加重心理负担。在训练方法上，采用深呼吸、想象放松法及肌肉放松训练都可以有效地缓解焦虑。</p>
      </div>
      <div class="report-content inner page">
        <p class="guide-number">（3）对人焦虑</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者在与人交往时保持自己热情大方的态度，正确看待自己的优缺点，能够接受自己的确定，不对自己期望过高，通过客观地认识自己来增强自信心。学习交往技巧，在与人交往时做到真诚，勇于表达自己的想法和感受。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">建议受测者正确看待自己偶尔出现的人际焦虑情况，加强自信心培养，正确看待自己的优缺点，能够接受自己的确定，不对自己期望过高，通过客观地认识自己来增强自信心。学习一些人际交往技巧，在与人交往时做到真诚，用于表达自己的想法和感受。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者正确看待自己的焦虑情绪，必要时寻求心理老师或心理咨询师的帮助。在自我调节方面，首先培养自信心，能够正视并接受自己的缺点，不要对自己的期望值过高。其次要掌握一些交往技巧，在与别人交往时真诚、勇于表达自己的看法和感受，就会获得别人的理解和接受。最后学习一些积极的心理暗示或者放松法，帮助自己树立信心，减少焦虑。</p>

        <p class="guide-number">（4）孤独倾向</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者在人际交往中保持积极乐观心态，真诚对待他人，更要真诚对待自己，在与人相处的过程中收获二三知己，收获友谊。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">测评结果表明受测者合群性适中，偶尔出现孤独、自闭的状态。建议受测者学习与人分享，无论开心的事还是伤心的事都能与他人分享自己的感受。遇到一些让自己感到紧张的场合，尽量训练自己能够参与进去，培养积极的心态和适应环境的能力。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者首先接纳自己现阶段的状态，在此基础上学习与人分享，无论开心的事还是伤心的事都能与他人分享自己的感受。同时，训练自己积极主动地关心、帮助别人的习惯。遇到一些让自己感到不安的场合时也不要回避，尽量参与进去，培养自己积极的心态和适应环境的能力。必要时，可以寻求心理老师或心理咨询师的帮助。</p>

      </div>

      <div class="report-content inner page">
        <p class="guide-number">（5）自责倾向</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者保持积极的心态，理性客观地看待生活中发生的各种事情，进一步培养自己的自信心，热爱生活，相信自己。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">建议受测者积极、客观地反思生活中自认为失败的事情，遇事不要自责，要跳出圈外，学会客观地分析问题，找出造成某一事件的所有可能的原因，并逐条列出，通过对可能原因的思辨，最终将会发现错误并非由自己一手造成。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者积极寻求心理老师或心理咨询师的帮助，不断觉察自己不正确的思维模式，纠正自己不正确的自动化思维，即将失败全部归咎于自己。生活事件是各种情境的组合体，每当自责的时候，跳出圈外，学会客观地分析问题，找出造成某一事件的所有可能的原因，并逐条列出，与不正确的自动化思维辩驳，最终将会发现错误并非由自己一手造成。</p>

        <p class="guide-number">（6）过敏倾向</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者全面客观地看待身边发生的事情，不过分敏感也不过分大意，保持理性客观的态度。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">建议受测者对一些小事不必太认真，太计较。当为某件小事而烦恼时，可以暂时把让人烦恼的事情放在一边，转移自己的注意力，比如可以听音乐、练书法、做运动或者其它愉悦身心的活动，等心情平复后再来处理问题。同时也要练就宽容的气度，不斤斤计较、钻牛角尖，这样就不会为一些小事而烦恼了。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者首先接纳自己现阶段的状态，在此基础上学习与人分享，无论开心的事还是伤心的事都能与他人分享自己的感受。同时，训练自己积极主动地关心、帮助别人的习惯。遇到一些让自己感到不安的场合时也不要回避，尽量参与进去，培养自己积极的心态和适应环境的能力。必要时，可以寻求心理老师或心理咨询师的帮助。</p>

      </div>

      <div class="report-content inner page">
        <p class="guide-number">（7）身体症状</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者保持科学、规律的作息习惯，健康饮食，加强身体锻炼，同时保持积极乐观的心态，积极乐观时对抗一切心理障碍的法宝。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">建议受测者养成良好的生活习惯，有规律的生活。注意科学作息、合理饮食，加强锻炼身体，身体健康是心理健康的基础，也不要忘记保持积极乐观的心态，积极乐观是对抗一切心理障碍的法宝。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者在感到身体不适时先去医院做一下相关检查，排除身体疾病等原因。确认身体没有问题或疾病时再寻求心理老师或心理咨询师的帮助，开展相关的心理训练，可以学习放松训练、减压训练、系统脱敏训练等内容。同时加强身体锻炼，养成良好的生活习惯及规律的作息是保持身体健康的重要条件。</p>

        <p class="guide-number">（8）恐怖倾向</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者全面客观地看待身边发生的事情，不过分敏感也不过分大意，保持理性客观的态度。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">建议受测者对一些小事不必太认真，太计较。当为某件小事而烦恼时，可以暂时把让人烦恼的事情放在一边，转移自己的注意力，比如可以听音乐、练书法、做运动或者其它愉悦身心的活动，等心情平复后再来处理问题。同时也要练就宽容的气度，不斤斤计较、钻牛角尖，这样就不会为一些小事而烦恼了。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者首先接纳自己现阶段的状态，在此基础上学习与人分享，无论开心的事还是伤心的事都能与他人分享自己的感受。同时，训练自己积极主动地关心、帮助别人的习惯。遇到一些让自己感到不安的场合时也不要回避，尽量参与进去，培养自己积极的心态和适应环境的能力。必要时，可以寻求心理老师或心理咨询师的帮助。</p>
      </div>

      <div class="report-content inner page">
        <p class="guide-number">（9）冲动倾向</p>
        <p class="guide-score">0≤因子分＜4</p>
        <p class="guide-desc">建议受测者多了解自制力差给人带来的危害，常反思冲动行事的后果，从而坚定培养自制力的决心。建议主动给自 己增加压力，承担领导角色，激发个体内在的积极性和责任感，培养内心的稳定性。</p>

        <p class="guide-score">4≤因子分＜8</p>
        <p class="guide-desc">建议受测者多了解自制力差给人带来的危害，常反思冲动行事的后果，从而坚定培养自制力的决心。学习一些调节情绪的方法，在自我训练的过程中，请家人或朋友帮忙监督。在生活中主动承担一些领导角色或重要任务，激发内在的责任感，培养内心的稳定性。</p>

        <p class="guide-score2">8≤因子分</p>
        <p class="guide-desc">建议受测者学习调节情绪的方法，必要时需求心理老师或心理咨询师的帮助，训练自我情绪调节能力，培养控制情绪的方法，如离开环境一分钟，或者在生气时先默数十个数再说话等。同时培养冷静分析事情的能力，遇到可能会失控的情况时，用理智脑代替情绪脑，分析事情，控制情绪。</p>

      </div>
    </template>

    <template v-if="scaleType === 'sds'">
        <div class="report-content inner page">
          <div style="display: flex;margin-top: 35px">
            <div style="flex: 1"></div>
            <p class="title-testTool">指</p><p class="title-testTool" style="color: black">导建议</p>
            <p class="title-part-preface"></p>
            <div style="flex: 1"></div>
          </div>

          <p style="font-size: 15px;font-family: Microsoft YaHei;margin-top: 30px;margin-left:12px">本建议为参考建议，请结合学生实际情况开展工作。</p>
          <p class="guide-number">（1）</p>
          <p style="width: 75px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 40px; margin-top: -18px">总分＜53 </p>
          <br/>
          <p style="font-size: 15px;margin-left:12px">保持身心愉悦，维持现有的生活状态即可~</p>

          <p class="guide-number">（2）</p>
          <p style="width: 105px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 40px; margin-top: -18px">53≤总分＜63</p>
          <br/>
          <p style="font-size: 15px;margin-left:12px">可能具有轻度抑郁症状，建议您做一个更为全面的心理检查。也可以做以下这些来帮助缓解症状：</p><br/>
          <p class="guide-desc-sds">1.寻求专业的帮助，找心理老师或心理咨询师；</p>
          <p class="guide-desc-sds">2.你要明白各式各样的抑郁正在困扰成千上万，形形色色的人，受害者无论是谁，机会是均等的；</p>
          <p class="guide-desc-sds">3.与家人、密友坦诚相待，绝对可以让你获得支持的；</p>
          <p class="guide-desc-sds">4.不要害怕抑郁症，充分了解它，学会跟它相处；</p>
          <p class="guide-desc-sds">5.尝试学点瑜伽、冥想和自然放松，学会如何适度休息和安定心神很重要，疲惫与压力是抑郁症的食粮，压力越大，抑郁越严重；</p>
          <p class="guide-desc-sds">6.偶尔出去走走路，跑跑步，尝试下任何形式的常规运动；</p>
          <p class="guide-desc-sds">7.坚持记录你的情绪，形成你的“情绪日记”会非常有用。将你的想法写下来，是一种深度释放，同时会让你更加具有洞见，思想深刻；</p>
          <p class="guide-desc-sds">8.设定一些符号，为你每天的心情评级；</p>
          <p class="guide-desc-sds">9.最重要记得，无论多糟糕，只要你采取了正确的步骤，日子会一天天好起来的。65-90分，测评结果表明受测者存在较严重的心理问题倾向，需要进一步分析，同时开展心理辅导。</p>
        </div>
        <div class="report-content inner page">
          <p class="guide-number">（3）</p>
          <p style="width: 107px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 40px; margin-top: -18px">63≤  总分＜73</p><br/>
          <p style="font-size: 15px;margin-left:12px">可能具有中度抑郁症状，建议到医院做进一步的诊断，并听从医生的安排。此外，还可以试试下面的方法：</p> <br/>

          <p class="guide-desc-sds">1.寻求专业的帮助，去看精神科医生，药物治疗可能是必要的手段；</p>
          <p class="guide-desc-sds">2.你要明白各式各样的抑郁正在困扰成千上万，形形色色的人，受害者无论是谁，机会是均等的；</p>
          <p class="guide-desc-sds">3.与家人、密友坦诚相待，绝对可以让你获得支持的；</p>
          <p class="guide-desc-sds">4.不要害怕抑郁症，充分了解它，学会跟它相处；</p>
          <p class="guide-desc-sds">5.尝试学点瑜伽、冥想和自然放松，学会如何适度休息和安定心神很重要，疲惫与压力是抑郁症的食粮，压力越大，抑郁越严重；</p>
          <p class="guide-desc-sds">6.偶尔出去走走路，跑跑步，尝试下任何形式的常规运动；</p>
          <p class="guide-desc-sds">7.坚持记录你的情绪，形成你的“情绪日记”会非常有用。将你的想法写下来，是一种深度释放，同时会让你更加具有洞见，思想深刻；</p>
          <p class="guide-desc-sds">8.设定一些符号，为你每天的心情评级；</p>
          <p class="guide-desc-sds">9.最重要记得，无论多糟糕，只要你采取了正确的步骤，日子会一天天好起来的。
          </p> <br/>
          <p style="font-size: 15px;margin-left:12px">另外，希望您给您的朋友、您的家人看看下面这段话：</p> <br/>
          <p style="font-size: 15px;margin-left:12px">我们相处过程中，我希望你能做这些：</p> <br/>
          <p class="guide-desc-sds">1.以适当的方法分享与抑郁症相关或者其他有用的信息，就比如上厕所的时候放在Ta手边；</p>
          <p class="guide-desc-sds">2.试着不要说话，只是倾听，真心地陪在Ta身边，不要表态或判断，这是你能给他们最好的礼物之一了；</p>
          <p class="guide-desc-sds">3.如果Ta能够接受，鼓励Ta去寻求专业的建议，提议帮着找一个好医生，甚至陪他们一起过去；</p>
          <p class="guide-desc-sds">4.不要试图打断他们的生活节奏，替他们做好一切，保持一定的常规生活对于Ta的自我评价至关重要，但如果可以，也试着让Ta偷点小懒，放松放松；</p>
          <p class="guide-desc-sds">5.鼓励Ta参与任何形式的常规运动；</p>
          <p class="guide-desc-sds">6.如果你真的担心Ta，组织Ta密友或者家人与Ta保持联系，哪怕只是简单了打个招呼；</p>
          <p class="guide-desc-sds">7.跟Ta一起制定简化生活的策略，少些压力，抑郁也会减轻；</p>

        </div>
      <div class="report-content inner page">

        <p style="font-size: 15px;margin-left:12px">我们相处中，请千万别这么做：</p>
        <p class="guide-desc-sds" style="margin-top: 5px">1.不要再重复说天气有多好之类令人厌烦又毫无意义的话语，如果Ta们可以摆脱抑郁，迅速振作起来，Ta们会的，没人愿意自己得抑郁症的；</p>
        <p class="guide-desc-sds">2.不要理所当然地指责Ta“你就是想的太多”，也不要跟Ta们“像个男子汉一样振作起来”，抑郁症是病，不是想的太多，也不是懦弱的表现；</p>
        <p class="guide-desc-sds">3.不要做个纸上谈兵的将军，给Ta一些毫无根据的建议和指令；</p>
        <p class="guide-desc-sds">4.体贴入微与善意好心总是不会错的，但请不要试图哄Ta一起开心，这样只会让事情更糟；</p>
        <p class="guide-desc-sds">5.“你不过是在吸引关注，博同情”请千万不要说类似的话来贬低和伤害Ta，Ta是真的需要你的关心；</p>
        <p class="guide-desc-sds">6.不要强调还有很多人比Ta过的还差，这样只会增加Ta的内疚和无望；</p>
        <p class="guide-desc-sds">7.不要强迫Ta去做Ta不想做的事情，事后又为Ta不如你所愿的行为找借口，这样只会让他们更加绝望和消极。</p>

        <p class="guide-number">（4）</p>
        <p style="width: 70px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 40px; margin-top: -18px">73≤总分</p><br/>
        <div style="font-size: 15px;margin-left:12px">
          <p style="float: left">可能具有重度抑郁症状，</p>
          <p style="color: red;float: left">强烈建议</p>
          <p>到医院做进一步的诊断，并听从医生的安排。此外，还可以试试下面的方法： </p>
        </div> <br/>

        <p class="guide-desc-sds" style="margin-top: -15px">1.寻求专业的帮助，去看精神科医生，药物治疗可能是必要的手段；</p>
        <p class="guide-desc-sds">2.你要明白各式各样的抑郁正在困扰成千上万，形形色色的人，受害者无论是谁，机会是均等的；</p>
        <p class="guide-desc-sds">3.与家人、密友坦诚相待，绝对可以让你获得支持的；</p>
        <p class="guide-desc-sds">4.不要害怕抑郁症，充分了解它，学会跟它相处；</p>
        <p class="guide-desc-sds">5.尝试学点瑜伽、冥想和自然放松，学会如何适度休息和安定心神很重要，疲惫与压力是抑郁症的食粮，压力越大，抑郁越严重；</p>
        <p class="guide-desc-sds">6.偶尔出去走走路，跑跑步，尝试下任何形式的常规运动；</p>
        <p class="guide-desc-sds">7.坚持记录你的情绪，形成你的“情绪日记”会非常有用。将你的想法写下来，是一种深度释放，同时会让你更加具有洞见，思想深刻；</p>
        <p class="guide-desc-sds">8.设定一些符号，为你每天的心情评级； </p>
        <p class="guide-desc-sds">9.最重要记得，无论多糟糕，只要你采取了正确的步骤，日子会一天天好起来的。</p> <br/>

        <p style="font-size: 15px;margin-left:12px">另外，希望您给您的朋友、您的家人看看下面这段话：</p> <br/>
        <p style="font-size: 15px;margin-left:12px">我们相处过程中，我希望你能做这些：</p> <br/>
        <p class="guide-desc-sds" style="margin-top: -20px">1.以适当的方法分享与抑郁症相关或者其他有用的信息，就比如上厕所的时候放在Ta手边；</p>
        <p class="guide-desc-sds">2.试着不要说话，只是倾听，真心地陪在Ta身边，不要表态或判断，这是你能给他们最好的礼物之一了；</p>
        <p class="guide-desc-sds">3.如果Ta能够接受，鼓励Ta去寻求专业的建议，提议帮着找一个好医生，甚至陪他们一起过去；</p>
      </div>
    </template>
  </div>
</template>

<script>
import Vue from 'vue'
import { viewGroupReport } from '@/api/reportOrganization'
import MyMap from "@/components/Map";
import CircleProgress from "@/components/CircleProgress";
import MyPolarBar from "@/components/PolarBar";
import MyLine from "@/components/Line";
import HotWords from "@/components/HotWords";
import circularDiagram from "@/components/RoundedPie/circularDiagram";
import Pie from "@/components/RoundedPie/pie";
import LineBar from "@/components/Bar/lineBar";
import {mapGetters} from "vuex";
import html2Canvas from 'html2canvas'
import JsPDF from 'jspdf'

export default {
  name: 'Report',
  computed: {
    ...mapGetters([
      
    ]),},
  data() {
    return {
      extra: {period:[]},
      level: 3,
      coverInfo: {},
      content: {
        scaleIntroduce: [],
        total: {},
        factor: [],
      },
      scaleType: '',
      warningPercent: '',
      bgPercent: [],
      totalWarning: '0',
      warnPercent: '0',
      tableObj: {},
      tableData: [],
      dimensionNames: ['学习焦虑', '对人焦虑', '孤独倾向', '自责倾向', '过敏倾向', '身体症状', '恐怖倾向', '冲动倾向'],
      dimensionName: [],
      dimensionNumber: [],
      dimensionNumberFit: 0,
      dimensionNumber2: [],
      gradeName: [],
      gradeNumber: [],
      gradeNumberFit: 0,
      gradeNumber2: [],
      tableTotalScore: [],
      tableDimensionScore:[],
      tableTotalScoreCols: [
        {prop: "grade", label: "年级"},
        {prop: "numbers", label: "人数"},
        {prop: "boy", label: "男"},
        {prop: "girl", label: "女"},
        {prop: "total", label: "总计"},
      ],
      tableDimensionScoreCols: [
        {prop: "grade", label: "年级"},
        {prop: "sex", label: "性别"},
        {prop: "learnAnxiety", label: "学习焦虑得分"},
        {prop: "socialAnxiety", label: "社交焦虑得分"},
        {prop: "lonelyTendency", label: "孤独倾向得分"},
        {prop: "remorseTendency", label: "自责倾向得分"},
        {prop: "allergicTendency", label: "过敏倾向得分"},
        {prop: "physicalSymptoms", label: "身体症状得分"},
        {prop: "horrorTendency", label: "恐怖倾向得分"},
        {prop: "impulsiveness", label: "冲动倾向得分"},
        {prop: "validityScale", label: "效度量表得分"},
      ],
    }
  },
  components: {
    circularDiagram,
    Pie,
    LineBar
  },
  mounted() {
    this.coverInfo = this.$route.query;
    this.extra.period = [];
    if(this.coverInfo.period.indexOf("小学")!=-1){
      this.extra.period.push("小学")
    }else if(this.coverInfo.period.indexOf("初中")!=-1){
      this.extra.period.push("初中")
    }else if(this.coverInfo.period.indexOf("高中")!=-1){
      this.extra.period.push("高中")
    }else if(this.coverInfo.period.indexOf("中职")!=-1){
      this.extra.period.push("中职")
    }

    this.getReportContent();
    if(this.coverInfo.scale.includes('MHT')) {
      this.scaleType = 'mht'
    }else if(this.coverInfo.scale.includes('SDS')) {
      this.scaleType = 'sds'
    }
  },
  methods: {
    getReportContent() {
      let { id,reportName, scale, period, createTime, createPerson,schoolId,taskId,scaleId,userId } = this.coverInfo;
      const data = { id,reportName, scale, period, createTime, createPerson,schoolId,taskId,scaleId,userId};
      data.level=this.level,
      viewGroupReport(data).then(res => {
        if(res.success) {
          this.warningPercent = res.result.warningPercent;
          for(const key in res.result) {
            this.coverInfo[key] = res.result[key];
            if(key=="warningBoys"){
              this.bgPercent.push({name: '男', value: res.result[key]});
            }else if(key=="warningGrils"){
              this.bgPercent.push({name: '女', value: res.result[key]});
            }else if(key=="warnNumbers"){
              this.totalWarning = res.result[key] + '';
              let warn = (this.totalWarning/res.result["validNumbers"])*100  + '';
              this.warnPercent = parseFloat(warn).toFixed(0);
            }else if(key=="start"){
              this.tableObj.start = res.result[key] + '';
            }else if(key=="end"){
              this.tableObj.end = res.result[key] + '';
            }else if(key=="taskName"){
              this.tableObj.taskName = res.result[key] + '';
            }else if(key=="scaleName"){
              this.tableObj.scaleName = res.result[key] + '';
            }else if(key=="completeNumbers"){
              this.tableObj.completeNumbers = res.result[key] + '';
            }else if(key=="completePercent"){
              this.tableObj.completePercent = res.result[key] + '';
            }else if(key=="validNumbers"){
              this.tableObj.validNumbers = res.result[key] + '';
            }else if(key=="validPercent"){
              this.tableObj.validPercent = res.result[key] + '';
            }else if(key=="gradeWarningMap"){
              const gradeWarningMap = res.result[key];
              this.gradeNumberFit = 0;
              for(let i=0;i<gradeWarningMap.length;i++) {
                this.gradeName.push(gradeWarningMap[i].grade);
                this.gradeNumberFit = Math.max(this.gradeNumberFit, gradeWarningMap[i].num);
                this.gradeNumber.push(gradeWarningMap[i].num);
                let gradeNumber2 = parseFloat((gradeWarningMap[i].num/res.result["validNumbers"])*100+'').toFixed(0);
                this.gradeNumber2.push(gradeNumber2);
              }

              this.gradeNumberFit = Math.min(this.gradeNumberFit * 3, res.result["validNumbers"]);


            }else if(key=="dimensionWarningMap"){
              const dimensionWarningMap = res.result[key];
              for(const key in this.dimensionNames) {
                this.dimensionName.push(this.dimensionNames[key]);
                if(dimensionWarningMap[this.dimensionNames[key]]){
                  this.dimensionNumber.push(dimensionWarningMap[this.dimensionNames[key]]);
                  let dimensionNumber2 = parseFloat((dimensionWarningMap[this.dimensionNames[key]]/res.result["validNumbers"])*100+'').toFixed(0);
                  this.dimensionNumber2.push(dimensionNumber2);
                }else {
                  this.dimensionNumber.push(0);
                  this.dimensionNumber2.push(0);
                }
              }
            }else if(key=="periodMap"){
              let tabList = res.result[key];
              for(const key in tabList) {
                let tb = tabList[key]
                let obj = Object.create(null);
                obj["grade"] = key;
                for(const k in tb) {
                  if(k==0){
                    obj["numbers"] = tb[k];
                  }else if(k==1){
                    obj["boy"] = tb[k];
                  }else if(k==2){
                    obj["girl"] = tb[k];
                  }
                }
                obj["total"] = obj["boy"]+obj["girl"];
                this.tableTotalScore.push(obj);
              }

            }
          }
          this.tableData.push(this.tableObj);

        }
      })
    },
    exportPDF(reportName, doc, selector) {
      //alert(document.getElementById("report-container"))
      html2Canvas(doc.querySelector(selector), {
        allowTaint: false
      }).then(function (canvas) {
            let contentWidth = canvas.width
            let contentHeight = canvas.height
            let pageHeight = contentWidth / 592.28 * 841.89
            let leftHeight = contentHeight
            let position = 0
            let imgWidth = 595.28
            let imgHeight = 592.28 / contentWidth * contentHeight
            canvas.setAttribute("crossOrigin",'anonymous')
            let pageData = canvas.toDataURL('image/jpeg', 1.0)
            let PDF = new JsPDF('', 'pt', 'a4')

            if (leftHeight < pageHeight) {
              PDF.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight)
            } else {
              while (leftHeight > 0) {
                PDF.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
                leftHeight -= pageHeight
                position -= 841.89
                if (leftHeight > 0) {
                  PDF.addPage();
                }
              }
            }
            PDF.save(reportName + '.pdf')
          }
      )
    }
  }

}
</script>

<style lang="css">

body { 
  -webkit-print-color-adjust: exact; 
  print-color-adjust: exact; 
}

.el-table table{
  width: auto !important;
}

</style>

<style lang="scss" scoped>
@import "@/styles/variables.scss";

@media print{
  .report-container {
    background-color: #fff  !important;
  }

  .report-content{
  page-break-after: always;
  width:auto !important;
  }
}


.report-container {
  width: 100%;
  background-color: rgb(240, 240, 240);
  .report-content {
    width: 72%;
    height: 100%;
    margin: 0 auto;
    position: relative;
    overflow: hidden;
    font-family: "微软雅黑"
  }
  .cover {
    background: url(../../../assets/imgs/background/bg.png) no-repeat center center;
    background-size: 100% 100%;
  }

  .inner {
    background: url(../../../assets/imgs/background/bg2.png) no-repeat center center;
    background-size: 100% 100%;
  }


  .page0 {
    color: rgb(77, 77, 77);
    text-align: center;

    .titleEn{
      width: 600px;
      height: 90px;
      font-size: 45px;
      font-family: FZZhengHeiS-B-GB;
      color: #5123A5;
      line-height: 14px;
      margin-top: 15vh;
    }
    .title {
      width: 540px;
      height: 100px;
      font-size: 90px;
      font-family: FZZhengHeiS-B-GB;
      color: #5123A5;
      line-height: 1px;
      font-weight: bold;
    }
    .scale-name {
      width: 560px;
      height: 133px;
      font-size: 28px;
      font-family: FZZhengHeiS-B-GB;
      color: #5123A5;
      margin-top: 7vh;
      font-weight: bold;
    }
    .reportUnit {
      height: 109px;
      font-size: 23px;
      font-family: FZZhengHeiS-B-GB;
      color: #5123A5;
      line-height: 100px;
      margin-top: 22vh;
      margin-left: 90px;
      text-align: left;
    }
    .reportDate{
      font-size: 23px;
      font-family: FZZhengHeiS-B-GB;
      color: #5123A5;
      line-height: 26px;
      margin-left: 90px;
      text-align: left;
    }
  }

  .title-part-preface{
    width: 5px;
    height: 5px;
    border: 8px solid rgba(114,24,197,0.79);
    border-radius: 50%;
    margin-top: -20px;
  }
  .preface-p{
    height: 127px;
    font-size: 15px;
    font-family: Microsoft YaHei;
    font-weight: 400;
    color: #000000;
    line-height: 36px;
  }

  .title-preface{
    height: 48px;
    font-size: 41px;
    font-family: Microsoft YaHei;
    font-weight: bold;
    color: #6F40C7;
    line-height: 16px;
  }
  .title-test-result-circle{
    width: 5px;
    height: 5px;
    border: 8px solid rgba(114,24,197,0.79);
    border-radius: 50%;
    margin-left: 440px;
    margin-top: 60px;
  }
  .title-testTool{
    height: 48px;
    font-size: 41px;
    font-family: Microsoft YaHei;
    font-weight: bold;
    color: #6F40C7;
    line-height: 16px;
    text-align: center;
  }
  .title-testBaseInfo{
    height: 40px;
    font-size: 38px;
    font-family: Microsoft YaHei;
    font-weight: bold;
    color: #6F40C7;
    line-height: 16px;
    text-align: center;
  }
  .title-test-result{
    height: 40px;
    font-size: 38px;
    font-family: Microsoft YaHei;
    font-weight: bold;
    color: #6F40C7;
    line-height: 16px;
    text-align: center;
  }
  .testTool-text{
    font-size: 15px;
    font-family: Microsoft YaHei;
    line-height: 30px;
  }
  .testTool-remark{
    line-height: 30px;
    font-size: 15px;
    font-family: Microsoft YaHei;
    color: #797777;
  }
  .title-part-round{
    width: 15px;
    height: 15px;
    background: #9378DB;
    opacity: 0.2;
    border-radius: 50%;
    float: left;
  }
  .title-part-round2{
    width: 7px;
    height: 7px;
    background: #9378DB;
    border-radius: 50%;
    float: left;
    margin-top: 4px;
    margin-left: -11px;
  }
  .title-part-testObject{
    font-size: 18px;
    font-family: Microsoft YaHei;
    color: #000000;
    line-height: 20px;
    float: left;
    margin-top: -18px;
   }
   .testObject-p{
     float: left;
   }
   .testObject-sub{
     color: #6F40C7;
   }

  .guide-number{
    width: 570px;
    height: 20px;
    font-size: 18px;
    font-family: Microsoft YaHei;
    line-height: 30px;
    margin-top: 3vh;
  }
  .guide-desc{
    font-size: 15px;
    font-family: Microsoft YaHei;
    color: #969595;
    margin-top: 7vh;
    margin-left: 10px;
  }
  .guide-desc-sds{
    font-size: 15px;
    font-family: Microsoft YaHei;
    color: #969595;
    margin-left: 10px;
    line-height: 3vh
  }
  .guide-score{
    width: 100px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 10px;margin-top: 3vh;
  }
  .guide-score2{
    width: 80px;height: 25px; background: #9378DB;color: white;float: left;padding: 3px 2px 0px 9px;margin-left: 10px;margin-top: 3vh;
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
      padding: 40px 8%;
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