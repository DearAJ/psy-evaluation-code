<template>
  <div class="notice-details-container">
    <p class="title">{{noticeDetails.title}}</p>
    <div class="sub-title">
      <span>下发单位：{{noticeDetails.issuedUnit}}</span>
      <span>下发时间：{{noticeDetails.issuedTime}}</span>
    </div>
    <div class="content" v-html="noticeDetails.content">
    </div>
    <div class="appendix" v-if="noticeDetails.appendix.length > 0">
      <p class="appendix-title">附件</p>
      <p class="appendix-content" v-for="item in noticeDetails.appendix" :key="item.fileName" @click="downloadFile(item.fileUrl)">
        {{item.fileName}}
      </p>
    </div>
  </div>
</template>

<script>
import { getNoticeById  } from '@/api/notice';
export default {
  name: 'NoticeDetails',
  data() {
    return {
      id: '',
      noticeDetails: {
        appendix: []
      }
    }
  },
  mounted() {
    this.id = this.$route.query.id;
    this.getNoticeDetails();
  },
  methods: {
    getNoticeDetails() {
      getNoticeById({id: this.id}).then(res => {
        if(res.success) {
          const appendix = [];
          res.result.appendix.split(',').forEach(val => {
            if(val) {
              appendix.push({
                fileUrl: val,
                fileName: val.split('/').pop()
              })
            }
          })
          res.result.appendix = appendix;
          this.noticeDetails = res.result;
          console.log(this.noticeDetails)
        }
      }).catch(res => {
        console.log(res);
      })
    },
    downloadFile(url) {
      window.open(url);
    }
  },
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables";
.notice-details-container {
  margin: 60px 40px;
  text-align: center;
  .title {
    font-size: 30px;
    font-weight: bold;
  }
  .sub-title {
    margin: 30px 0 40px 0;
    span {
      margin: 0 30px;
    }
  }

  .content {
    text-align: left;
    width: 86%;
    margin: 0 auto;
    overflow: hidden;
  }

  .appendix {
    width: 86%;
    margin: 0 auto;
    text-align: left;
    .appendix-title {
      margin-top: 30px;
      font-size: 22px;
      font-weight: bold;
    }
    .appendix-content {
      margin-top: 15px;
      cursor: pointer;
      &:hover {
        text-decoration: underline;
        color: $themeColor;
      }
    }
  }
}
</style>