<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden; display: flex">
    <div style="flex: 1;background: dodgerblue">
      <el-image style="width: 100%;height: 100%;fit: cover;" src="https://pic.imgdb.cn/item/64e412ce661c6c8e546bec7e.jpg"/>
    </div>

    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我的学习平台</div>
      <div style="margin-top: 10px;">这个网站用于练习前端和后端</div>
    </div>

    <div style="width:400px;background-color:white; z-index: 1">
      <transition name="el-fade-in-linear"><router-view/></transition>
    </div>
  </div>
</template>
<script setup>

</script>
<style scoped>
.welcome-title{
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>