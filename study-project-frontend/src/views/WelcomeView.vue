<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden; display: flex">
    <div style="flex: 1;background: dodgerblue">
      <el-image style="width: 100%;height: 100%;" fit="cover"  src="https://s2.loli.net/2023/11/29/F9GqraYPKAXWIhn.png"/>
    </div>

    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到LangXEchO的水桶网站</div>
      <div style="margin-top: 10px;">原神，启动！</div>
    </div>

    <div style="width:400px;background-color:white; z-index: 1">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear">
          <component :is="Component"  style="height: 100%"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>
<script setup>
import {ref} from "vue";

const fit = ref("contain")
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