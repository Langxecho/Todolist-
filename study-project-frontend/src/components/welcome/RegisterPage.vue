<script setup>

import {Lock, User ,Message,EditPen} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive,ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";
const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const isEmailValid = ref(false);
const onValidate = (prop,isValid) =>{
  if(prop === 'email') isEmailValid.value = isValid
}

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'));
  } else if(!/^[A-Za-z\u4E00-\u9FA5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'));
  }else{
    callback();
  }}
const formRef = ref()
const register = () => {
    formRef.value.validate((isValid) => {
      if(isValid){
        post("/api/auth/register",{
          username :form.username,
          password: form.password,
          email: form.email,
          code: form.code
        },(message) => {
          ElMessage.success(message)
          router.push("/")
        })
      }else{
        ElMessage.error("请完整填写注册表单内容！")
      }
    })
}
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};
const rules = {
  username: [
    { validator: validateUsername, trigger: ['blur','change'] },
    { min: 3, max: 5, message: '长度在 3 到 8 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur','change'] }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },

  ]
}
const validateEmail = () => {
  post('api/auth/valid-email',{
    email: form.email
  },(message) => {
    ElMessage.success(message)
    coldTime.value = 60
    setInterval(() => coldTime.value--,1000)
  })
}

const coldTime = ref(0)
</script>

<template>
  <div style="text-align: center;margin: 0 20px;">
    <div style=";margin-top: 150px">
      <div style="font-size: 25px; font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: grey">欢迎注册我的学习平台，请在下方填写信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" :maxlength="8" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" :maxlength="16" type="password" placeholder="密码" style="">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="重复密码" style="">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="电子邮件地址" style="">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10">
            <el-col :span="17">
              <el-input v-model="form.code" :maxlength="6" type="text" placeholder="请输入邮件验证码">
                <template #prefix>
                  <el-icon><EditPen /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button :disabled="!isEmailValid || coldTime > 0" @click="validateEmail" type="success">
                {{ coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

    </div>

    <div style="margin-top: 80px">
      <el-button style="width: 270px" type="warning" @click="register" plain>立即注册</el-button>
    </div>
    <div style="font-size: 14px;margin-top: 10px;">
      <span style="color: grey">已有账号？ </span>
      <el-link @click="router.push('/')" type="primary" style="translate: 0 -2px">立即登录</el-link>
    </div>
  </div>
</template>

<style scoped>

</style>