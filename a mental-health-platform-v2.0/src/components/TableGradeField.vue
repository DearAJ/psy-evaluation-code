<template>
    <span>{{ row.grade }}
        <span v-if="row.archived">&nbsp;<i style="color:#222" class="el-icon-time" title="存档数据"></i></span>
    </span>
</template>
<script>
export default {
//   name: 'TableGradeField',
  props:{
    row:Object
  },
  data() {
    return {
      count: 0
    }
  }
}
</script>