<template>
  <div>
    {{ msg }}
    <button class="forget_btn" v-on:click="startrecording" >开始录音</button>
    <button class="forget_btn" v-on:click="stoptrecording" >结束录音</button>
    <button class="forget_btn" v-on:click="playtape" v-if="show">播放录音</button>
  </div>
</template>

<script>

  import {getAction} from '@/api/manage'

  export default {
    data () {
      return {
        msg: "",
      }
    },

  methods: {
      hello () {
        var url = "/test/jeecgDemo/hello"
        getAction(url).then((res) => {
          if (res.success) {
            this.msg = res.result;
          }
        })
      },
    startrecording() {
      $App.startrecording("哈哈，我是js调用的,开始")
    },
    stoptrecording(){
      $App.stoptrecording("哈哈，我是js调用的，结束")
    },
    playtape(){
      $App.playtape("哈哈，我是js调用的，播放")
    }

    }
   ,
    created() {
      this.hello();
    }
  }


</script>


<style scoped>
  .avatar-uploader > .ant-upload {
    width:104px;
    height:104px;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }


</style>