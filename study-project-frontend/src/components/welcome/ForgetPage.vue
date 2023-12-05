<template>
  <div>
    <div style="margin: 30px 20px;">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件"></el-step>
        <el-step title="重新设定密码"></el-step>
      </el-steps>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center;margin: 0 20px;height: 100%;" v-if="active === 0">

        <div style=";margin-top: 100px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请输入需要重置密码的电子邮件地址</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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
        <div style="margin-top: 70px;">
          <el-button @click="startReset()" style="width: 270px;" type="warning">
            开始重置密码
          </el-button>
        </div>
      </div>
    </transition>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center;margin: 0 20px;height: 100%;" v-if="active === 1">

        <div style=";margin-top: 100px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请填写您的新密码，务必牢记防止丢失</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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
          </el-form>
        </div>
        <el-button @click="doReset()" style="width: 270px;" type="danger" plain>
          立即重置密码
        </el-button>
      </div>
    </transition>
  </div>
</template>

<script setup>
 import {reactive,ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";
const active = ref(0)
const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''

})
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};
const coldTime = ref(0)
const isEmailValid = ref(false);
const formRef = ref()

const onValidate = (prop,isValid) =>{
  if(prop === 'email') isEmailValid.value = isValid
}
const validateEmail = () => {
  coldTime.value = 60
  post('/api/auth/valid-reset-email',{
    email: form.email
  },(message) => {
    ElMessage.success(message)
    setInterval(() => coldTime.value--,1000)
  },(message) => {
    ElMessage.warning(message)
    coldTime.value = 0
  })
}
const startReset = () => {
  formRef.value.validate((isValid) => {
    if(isValid){
      post('/api/auth/start-reset',{
            email: form.email,
            code: form.code
          },
          () => {
            active.value++
          })
    }else{
      ElMessage.error("请完整电子邮件地址和验证码")
    }
})
}
const doReset = () => {
  formRef.value.validate((isValid) => {
    if(isValid){
      post('/api/auth/do-reset',{
            password: form.password
          },
          (message) => {
            router.push('/')
            ElMessage.success(message)
          })
    }else{
      ElMessage.error("请填写新密码")
    }
})}

const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur','change'] }
  ]
}
</script>
<style scoped>

</style>