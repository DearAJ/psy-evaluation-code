<template>
  <div class="student-tasks-container">
    <router-view></router-view>
    <el-tabs type="border-card">
      <el-tab-pane label="全部" class="task-pane">
        <template v-if="tasks.length == 0">
          <span>暂无数据</span>
        </template>
        <el-card v-for="item in tasks" shadow :key="item.id" class="task-card">
          <h3>
            <i v-if="item.state === 0" class="el-icon-warning not-finished-icon"></i>
            <i v-else-if="item.state === 1" class="el-icon-success finished-icon"></i>
            {{item.name}}</h3>
          <p>开始时间：{{item.startDate.split(' ')[0]}}</p>
          <p>结束时间：{{item.endDate.split(' ')[0]}}</p>
          <el-button type="primary" size="medium" class="button" 
            :disabled="item.disabled"
            @click="goto(item)">{{item.buttonText}}</el-button>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="未完成" class="task-pane">
        <template v-if="tasks.filter(val => val.state == 0).length == 0">
          <span>暂无数据</span>
        </template>
        <el-card v-for="item in tasks.filter(val => val.state == 0)" shadow :key="item.id" class="task-card">
          <h3>
            <i v-if="item.state === 0" style="color: #fa8b40" class="el-icon-warning"></i>
            <i v-else-if="item.state === 1" style="color: #2d96f1" class="el-icon-success"></i>
            {{item.name}}</h3>
          <p>开始时间：{{item.startDate.split(' ')[0]}}</p>
          <p>结束时间：{{item.endDate.split(' ')[0]}}</p>
          <el-button type="primary" size="medium" class="button" 
            :disabled="item.disabled"
            @click="goto(item)">{{item.buttonText}}</el-button>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="已完成" class="task-pane">
        <template v-if="tasks.filter(val => val.state == 1).length == 0">
          <span>暂无数据</span>
        </template>
        <el-card v-for="item in tasks.filter(val => val.state == 1)" shadow :key="item.id" class="task-card">
          <h3>
            <i v-if="item.state === 0" style="color: #fa8b40" class="el-icon-warning"></i>
            <i v-else-if="item.state === 1" style="color: #2d96f1" class="el-icon-success"></i>
            {{item.name}}</h3>
          <p>开始时间：{{item.startDate.split(' ')[0]}}</p>
          <p>结束时间：{{item.endDate.split(' ')[0]}}</p>
          <el-button type="primary" size="mini" class="button" 
            :disabled="item.disabled"
            @click="goto(item)">{{item.buttonText}}</el-button>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getTasks } from '@/api/scale.js'
import { mapGetters } from 'vuex'

export default {
  name: 'StudentMyTask',
  computed: {
    ...mapGetters([
      'extra',
      'userId',
    ])
  },
  data() {
    return {
      tasks: [],
    }
  },
  mounted() {
    this.getTasks();
  },
  methods: {
    goto(item) {
      // 将 item 存放到sessionStorage中
      window.sessionStorage.setItem('selectedTask', JSON.stringify(item));
      this.$router.push({path: '/s-my-task/psychological-test'});
    },
    getTasks() {
      let {schoolId, grade, classes, period} = this.extra;
      const data = {
        userId: this.userId,
        schoolId,
        grade,
        classes,
        period
      }
      getTasks(data).then(res => {
        res.result.forEach(val => {
          if(val.inTime == 0) {
            val.disabled = true;
            val.buttonText = '未开始'
          }else if(val.inTime == 2) {
            val.disabled = true;
            if(val.state == 0) {
              val.buttonText = '已结束'
            }else {
              val.buttonText = '已完成'
            }
          }else if(val.inTime == 1) {
            if(val.state == 0) {
              val.disabled = false;
              val.buttonText = '开始任务';
            }else if(val.state == 1) {
              val.disabled = true;
              val.buttonText = '已完成'
            }
          }
        })
        this.tasks = res.result;

      })
    }
  }
}
</script>

<style lang="scss" scoped> 
  @import "@/styles/mixin.scss";
  @import "@/styles/variables.scss";
  .student-tasks-container {
    @include container;
    .task-pane {
      display: flex;
      justify-content: left;
      flex-wrap: wrap;
      min-height: 100px;
      span {
        margin-top: 30px;
        color: #878787;
        width: 100%;
        text-align: center;
      }
      .task-card {
        width: 210px;
        text-align: center;
        height: 210px;
        margin: 10px 10px;
        position: relative;
        p {
          font-size: 13px;
        }
        p:first-of-type {
          margin-top: 40px;
        }
        i {
          margin-right: 5px;
        }
        .not-finished-icon {
          color: $errorColor;
        }
        .finished-icon {
          color: $themeColor;
        }
        .button {
          position: absolute;
          bottom: 10px;
        }
      }
    }
  }
</style>

<style lang="scss">
.student-tasks-container {
  .el-tabs__item {
    font-size: 18px;
    font-weight: bold;
  }

  .el-tabs--border-card{
    border-radius: 5px;
    & > .el-tabs__header {
      background-color: #f1f1f1;
    }
  } 
}
</style>