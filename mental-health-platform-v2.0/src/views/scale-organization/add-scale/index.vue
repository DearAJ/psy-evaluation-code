<template>
  <div class="add-scale-container">
    <el-form :model="dynamicScaleForm" label-width="150px" ref="addScaleForm">
      <div class="scale-basic-info">
        <el-form-item label="量表名称" prop="name" :rules="{ required: true, message: '量表名称为必填选项', trigger: 'blur'}">
          <el-input v-model="dynamicScaleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="量表说明（指导语）">
          <el-input v-model="dynamicScaleForm.introduction" type="textarea" :rows="3">
          </el-input>
        </el-form-item>
        <el-form-item label="结束语">
          <el-input v-model="dynamicScaleForm.conclusion" type="textarea" :rows="3">
          </el-input>
        </el-form-item>
        <el-form-item v-if="isShowAddProblemBtn" label="添加题目" class="add-stem-btn-group">
          <el-button size="medium" @click="addScaleProblem(1)">单选题</el-button>
          <el-button size="medium" @click="addScaleProblem(2)">多选题</el-button>
          <el-button size="medium" @click="addScaleProblem(3)">评分量表题</el-button>
          <el-button size="medium" @click="addScaleProblem(4)">填空题</el-button>
        </el-form-item>
        <div class="sumbit-btn">
          <el-button v-if="dynamicScaleForm.scaleList.length === 0" size="medium" type="primary">生成量表</el-button>
        </div>
      </div>
      <div class="add-scale-problem-container">
        <div class="problem-block" v-for="(item, id) in dynamicScaleForm.scaleList" :key="item.key">
          <!-- 编辑状态 -->
          <template v-if="item.state == 'edit'">
            <el-form-item label="题目类型">
              <el-select v-model="item.type">
                <el-option v-for="option in typeOptions" :key="'o' + option.key"
                  :label="option.label"
                  :value="option.key">
                </el-option>
              </el-select>
              <el-checkbox class="required-checkbox" v-model="item.required" :true-label="1" 
                :false-label="0">必选</el-checkbox>
            </el-form-item>
            <el-form-item label="题目描述">
              <el-input v-model="item.question" type="textarea" :rows="3">
              </el-input>
            </el-form-item>
            <!-- 单选题 -->
            <template v-if="item.type === 1">
              <el-form-item label="选项文字">
                <div class="option-label" v-for="(o, i) in item.options" :key="o.key">
                  <el-input class="option-input"  
                    v-model="o.value"
                    :placeholder="'选项' + (i + 1)">
                  </el-input>
                  <div>
                    <el-button icon="el-icon-minus" circle size="medium" @click="delOptions(item, i)"></el-button>
                  </div>
                </div>
              </el-form-item>
              <el-form-item class="add-stem-btn-group">
                <el-button size="medium" @click="addOptions(item, id)">添加选项</el-button>
              </el-form-item>
            </template>
            <template v-else-if="item.type === 2">
              <el-form-item label="选项文字">
                <div class="option-label" v-for="(o, i) in item.options" :key="o.key">
                  <el-input class="option-input"  
                    v-model="o.value"
                    :placeholder="'选项' + (i + 1)">
                  </el-input>
                  <div>
                    <el-button icon="el-icon-minus" circle size="medium" @click="delOptions(item, i)"></el-button>
                  </div>
                </div>
              </el-form-item>
              <el-form-item class="add-stem-btn-group">
                <el-button size="medium" @click="addOptions(item, id)">添加选项</el-button>
              </el-form-item>
              <el-form-item label="至少选" class="inline-form-item">
                <el-input v-model.number="item.min" type="number"></el-input>
              </el-form-item>
              <el-form-item label="最多选" class="inline-form-item">
                <el-input v-model.number="item.max" type="number"></el-input>
              </el-form-item>
            </template>
            <template v-else-if="item.type === 3">
              <el-form-item label="最低维度">
                <el-input v-model="item.min"></el-input>
              </el-form-item>
              <el-form-item label="最高维度">
                <el-input v-model="item.max"></el-input>
              </el-form-item>
              <el-form-item label="评分范围">
                <el-input-number v-model="item.gear" :min="1" :max="1000"></el-input-number>
              </el-form-item>
            </template>
            <el-form-item class="sumbit-btn" align="right">
              <el-button size="medium" type="primary" @click="finishProblem(item)">确定</el-button>
              <el-button size="medium" @click="cancelProblem">取消</el-button>
            </el-form-item>
          </template>
          <!-- 查看状态 -->
          <template v-else-if="item.state === 'finish'">
            <div class="finish-state">
              <p class="title">
                <span v-if="item.required" style="color: red; margin: 0 5px">*</span>
                {{(id + 1)}}.{{item.question}}
                <span v-if="item.type === 2" style="font-size: 14px">（至少选{{item.min}}个，最多选{{item.max}}个）</span>
              </p>
              <template v-if="item.type === 1">
                <div class="finish-state-options" v-for="o in item.options" :key="'s' + o.key">
                  <div class="circle option-square"></div>
                  <p>{{o.value}}</p>
                </div>
              </template>
              <template v-if="item.type === 2">
                <div class="finish-state-options" v-for="o in item.options" :key="'s' + o.key">
                  <div class="option-square"></div>
                  <p>{{o.value}}</p>
                </div>
              </template>
              <template v-if="item.type === 3">
                <div class="rate-scale-show">
                  <p>{{item.min}}</p>
                  <div class="finish-state-options" v-for="(o, i) in new Array(item.gear)" :key="'rateo' + i"> 
                    <div class="option-square circle"></div>
                    <p>{{i + 1}}</p>
                  </div>
                  <p>{{item.max}}</p>
                </div>
              </template>
              <div class="finish-btn-group add-stem-btn-group">
                <el-button size="medium" @click="updateProblem(item)">编辑</el-button>
                <el-button size="medium" @click="deleteProblem(item)">删除</el-button>
              </div>
            </div>
          </template>
        </div>
        <div v-if="dynamicScaleForm.scaleList.length > 0" class="sumbit-btn btn-group">
          <el-button type="primary" size="medium" @click="submitScale">提交量表</el-button>
          <el-button size="medium" @click="preview">预览</el-button>
        </div>
      </div>
    </el-form>
    <el-dialog title="量表预览" :visible.sync="dialogVisible"
      width="60%">
      <div class="dialog-container">
        <div class="title-des">
          <p class="title">{{dynamicScaleForm.name}}量表</p>
          <p class="des">{{dynamicScaleForm.introduction}}</p>
        </div>
        <div class="problem-block problem-dialog" v-for="(item, id) in dynamicScaleForm.scaleList" :key="'dp' + item.key">
          <div class="finish-state dialog-p-content">
            <p class="title">
              <span v-if="item.required" style="color: red; margin: 0 5px">*</span>
              <span>{{(id + 1)}}.{{item.question}}</span>
              <span v-if="item.type === 2" style="font-size: 14px">（至少选{{item.min}}个，最多选{{item.max}}个）</span>
            </p>
            <template v-if="item.type === 1">
              <div class="finish-state-options options-dialog" v-for="o in item.options" :key="'sd' + o.key">
                <div class="circle option-square"></div>
                <p>{{o.value}}</p>
              </div>
            </template>
            <template v-else-if="item.type === 2">
              <div class="finish-state-options options-dialog" v-for="o in item.options" :key="'sd' + o.key">
                <div class="option-square"></div>
                <p>{{o.value}}</p>
              </div>
            </template>
            <template v-else-if="item.type === 3">
              <div class="rate-scale-show">
                <p>{{item.min}}</p>
                <div class="finish-state-options options-dialog" v-for="(o, i) in new Array(item.gear)" :key="'drateod' + i"> 
                  <div class="option-square circle"></div>
                  <p>{{i + 1}}</p>
                </div>
                <p>{{item.max}}</p>
              </div>
            </template>
            <template v-else-if="item.type === 4">
              <el-input></el-input>
            </template>
          </div>
        </div>
        <div class="title-des">
          <p class="des">{{dynamicScaleForm.conclusion}}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addScale, getScale, updateScale } from "@/api/scale.js"
import { mapGetters  } from "vuex"
export default {
  name: 'AddScale',
  data() {
    return {
      dynamicScaleForm: {
        name: '', // 量表名称
        introduction: '', // 量表说明（指导语）
        conclusion: '', // 结束语
        scaleList: []
      },
      typeOptions: [{
        key: 1,
        label: '单选题'
      }, {
        key: 2,
        label: '多选题'
      }, {
        key: 3,
        label: '评分量表题'
      }, {
        key: 4,
        label: '填空题'
      }, ],
      isShowAddProblemBtn: true,
      dialogVisible: false,
      state: 'add',
      scaleId: ''
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ])
  },
  mounted() {
    if(this.$route.query.scaleId) {
      this.scaleId = this.$route.query.scaleId;
      this.state = 'update'
      this.getScaleData();
    }
  },
  methods: {
    getScaleData() {
      getScale({id: parseInt(this.scaleId)}).then(res => {
        if(res.success) {
          const obj = res.result[0];
          this.dynamicScaleForm.name = obj.scaleName;
          this.dynamicScaleForm.introduction = obj.introduction;
          this.dynamicScaleForm.conclusion = obj.conclusion;
          for(let i = 0; i < obj.content.length; i++) {
            const problem = obj.content[i];
            this.dynamicScaleForm.scaleList.push({
              key: 'p' + (this.dynamicScaleForm.scaleList.length + 1),
              type: problem.type,
              required: problem.required,
              state: 'finish',
              question: problem.main,
              max: problem.max,
              min: problem.min,
              gear: problem.gear,
              options: problem.type <= 2 ? problem.options.map((v, i) => { return {
                key: this.dynamicScaleForm.scaleList.length + '选项' + i,
                value: v.main
              } }) : []
            })
          }
          console.log(this.dynamicScaleForm)
        }else {
          this.$message.error('量表数据获取失败')
        }
      }).catch(err => {
        console.log(err)
      })
    },
    addScaleProblem(type) {
      const problem = {
        key: 'p' + (this.dynamicScaleForm.scaleList.length + 1),
        required: true,
        question: '', // 问题描述
        type: type,
        state: 'edit' // 有两种状态，edit表示编辑状态，finish表示查看状态
      }
      // if(type === 1) {
        // 添加单选题
        problem.options = [{
          key: (this.dynamicScaleForm.scaleList.length) + '选项1',
          value: ''
        }]
      // }else if(type === 2) {
        // 添加多选题
        // problem.options = [{
        //   key: (this.dynamicScaleForm.scaleList.length) + '选项1',
        //   value: ''
        // }]
       
        problem.min = '';
        problem.max = '';
      // }else if(type === 3) {
        // 添加评分量表题
        // problem.min = '';
        // problem.max = '';
        problem.gear = 5; // 评分量表题的等级
      // }
      this.dynamicScaleForm.scaleList.push(problem)
      this.isShowAddProblemBtn = false;
    },
    cancelProblem() {
      this.dynamicScaleForm.scaleList.pop();
      this.isShowAddProblemBtn = true;
    },
    addOptions(item, id) {
      item.options.push({
        key: id + '选项' + (item.options.length + 1)
      })
      if(item.type === 2) {
        item.max = item.options.length;
      }
    },
    finishProblem(item) {
      this.$refs.addScaleForm.validate(valid => {
        if(valid) {
          item.state = 'finish';
          this.addScaleProblem(item.type);
        }else {
          return false;
        }
      })
    },
    updateProblem(item) {
      item.state = 'edit';
      this.dynamicScaleForm.scaleList[this.dynamicScaleForm.scaleList.length - 1].state = 'finish';
    },
    deleteProblem(item) {
      this.dynamicScaleForm.scaleList = this.dynamicScaleForm.scaleList.filter(v => v.key !== item.key)
    },
    preview() {
      this.dialogVisible = true;
    },
    submitScale() {
      this.$refs.addScaleForm.validate(valid => {
        if(valid) {
          const { name, introduction, conclusion } = this.dynamicScaleForm;
          const data = { name, introduction, conclusion };
          data.userId = this.userId;
          data.scaleList = this.dynamicScaleForm.scaleList.map(v => {
            const obj = {
              question: v.question,
              type: v.type,
              required: v.required ? 1 : 0,
            }
            if(v.type === 1) {
              obj.options = v.options.map(o => o.value);
            }else if(v.type === 2) {
              obj.options = v.options.map(o => o.value);
              obj.min = v.min;
              obj.max = v.max;
            }else if(v.type === 3) {
              obj.min = v.min;
              obj.max = v.max;
              obj.gear = v.gear
            }
            return obj;
          })
          if(this.state === 'add') {
            // 添加
            addScale(data).then(res => {
              if(res.success) {
                this.$message.success('添加成功');
                this.dynamicScaleForm = {
                  name: '', // 量表名称
                  introduction: '', // 量表说明（指导语）
                  conclusion: '', // 结束语
                  scaleList: []
                }
                this.$router.replace({name: 'ScaleList'})
              }
            }).catch(err => {
              console.log(err);
            })
          }else if(this.state === 'update') {
            data.scaleId = this.scaleId;
            updateScale(data).then(res => {
              if(res.success) {
                if(!res.result) {
                  this.$message.success('编辑成功');
                  this.dynamicScaleForm = {
                    name: '', // 量表名称
                    introduction: '', // 量表说明（指导语）
                    conclusion: '', // 结束语
                    scaleList: []
                  }
                  this.$router.replace({name: 'ScaleList'})
                }else {
                  this.$message('存在关联数据，无法编辑')
                }
              }
              console.log(res)
            })
          }
        }else {
          return false;
        }
      })
    },
    delOptions(item, id) {
      item.options.splice(id, 1);
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/variables.scss";
.add-scale-container {
  background-color: $backgroundColor;
  min-height: calc(100vh - #{$headerHeight} - #{$navbarHeight});
  position: relative;

  .scale-basic-info {
    padding: 40px 60px 20px 60px;
    background-color: white; 
  }
  .add-stem-btn-group {
    ::v-deep .el-button {
      border-color: $themeColor;
      border-radius: 13px;
      color: $themeColor;
    }
  }
  .sumbit-btn {
    ::v-deep .el-button {
      border-radius: 12px;
    }
  }
  .add-scale-problem-container {
    margin-top: 20px;
    background-color: white;
  }

  .problem-block {
    border-bottom: 1px solid #E0E0E0;;
    padding: 20px 60px;
    position: relative;
    .inline-form-item {
      display: inline-block;
    }
  }
  .required-checkbox {
    margin-left: 30px;
  }
  .option-label {
    display: flex;
    align-items: flex-end;
  }
  .option-input {
    width: 220px;
    display: block;
    margin-top: 10px;
    margin-right: 10px;
  }
  .btn-group {
    padding: 20px 60px;
  }
  .finish-state {
    margin: 0 60px;
    position: relative;
    .title {
      font-size: 18px;
    }
    .finish-state-options {
      margin: 20px;
      display: flex;
      height: 17px;
      align-items: center;
      .circle {
        border-radius: 50%;
      }
      .option-square{
        width: 12px;
        height: 12px;
        background: rgba(133, 78, 216, 0);
        border: 1px solid #854ED8;
      }
      p {
        height: 100%;
        line-height: 15px;
        margin-left: 10px;
      }
    }
    .rate-scale-show {
      display: flex;
      margin: 0 10px;
      align-items: center;
    }
    .finish-btn-group {
      position: absolute;
      right: 30px;
      bottom: 0;
    }
  }

  .dialog-container {
    color: black;
    .title-des {
      text-align: center;
      .title {
        font-size: 24px;
        font-weight: bold;
      }
      .des {
        text-align: left;
        padding: 20px 40px;
        font-size: 16px;
        font-family: Microsoft YaHei;
        font-weight: 400;
        color: #999999;
      }
    }
    .dialog-p-content {
      margin: 0 40px;
      .title {
        margin-bottom: 20px;
      }
    }
    .problem-dialog {
      padding: 20px 0;
    }
    .options-dialog {
      margin: 10px;
    }
  }
}
</style>