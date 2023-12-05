<script setup>
import router from "@/router";
</script>

<template>
<div style="width: 100vw;height: 100vh;background-color:#f5f5f5;">
<!--  //顶部状态栏-->
  <div class="flex vertical-center mb-1" style="height: 60px;width: 100%;background-color:#fff; ">
    <div class="ml-2 font-weight-bold font-italic font-size-2">LangXEchO's Website</div>
    <div class="ml-4 font-weight-bold todolist" @click="router.push('/index/todolist')">待办与轨迹</div>
  </div>
<!--  //路由区-->
  <div class="center-horizontally" style="width: 1300px;">
    <Suspense>
      <template v-slot:default>
        <router-view></router-view>
      </template>
      <template v-slot:fallback>
        <h1 style="margin-left:50%;margin-top: 20%;">Loading.......</h1>
      </template>
    </Suspense>

  </div>

</div>

</template>

<style scoped>
.todolist:hover{
  color: gold;
}
</style>