<template>
  <div class="notice-container">
    <div class="notice-header">
      <span>通知公告</span>
      <span>更多</span>
    </div>
    <ul class="content-block">
      <!-- 无论传过来多少，只能显示4个 -->
      <li class="content-li" v-for="item in news.slice(0,4)" :key="item.id">
        <p class="title">{{item.title}}</p>
        <p class="department">{{item.department}}</p>
        <p class="date">{{item.date}}</p>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'newsShow',
  props: {
    news: {
      type: Array,
      default: function() {
        return []
      },
      required: true
    },
  },
  methods: {
    
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
@import "@/styles/variables.scss";

.notice-container {
  @include container;
  padding: 10px 20px;
  height: 25vh;
  .notice-header {
    height: 40px;
    line-height: 40px;
    color: #878787;
    font-size: 18px;
    display: flex;
    justify-content: space-between;
    border-bottom: 1px #b6b6b6 solid;
    span:last-of-type {
      color: black;
      font-size: 14px;
      cursor: pointer;
    }
    span:last-of-type:hover {
      color: $themeColor;
    }
  }
  .content-block {
    margin: 15px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: calc(100% - 80px);
    overflow: hidden;
    .content-li {
      flex: 1 1 auto;
      display: flex;
      justify-content: space-between;
      .date {
        flex: 0 0 100px;
      }
      .department {
        width: 30%;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
      .title {
        width: 40%;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        &:hover {
          text-decoration: underline;
          color: $themeColor;
        }
      }
    }
  }
}
</style>