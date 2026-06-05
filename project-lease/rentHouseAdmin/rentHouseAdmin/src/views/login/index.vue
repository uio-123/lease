<template>
  <div class="qingyu-login">
    <div class="login-card">
      <div class="login-header">
        <svg-icon name="qingyu-logo" size="40px" style="color: #0F766E" />
        <h1 class="login-title">青寓运营后台</h1>
        <p class="login-desc">公寓运营管理系统</p>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="captchaCode">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="14">
              <el-input
                v-model="loginForm.captchaCode"
                placeholder="请输入验证码"
                :prefix-icon="Key"
                maxlength="4"
              />
            </el-col>
            <el-col :span="10">
              <img :src="codeUrl" class="login-code" @click="getCodeHandle" alt="验证码" />
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <el-button type="primary" size="large" class="login-btn" @click="onSubmitHandle">登 录</el-button>
      <p class="login-footer">默认账号：user / 123456</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getCode, getUserInfo, login } from '@/api/user'
import { User, Lock, Key } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const codeUrl = ref('')
const captchaKey = ref('')

const loginForm = ref({
  username: 'user',
  password: '123456',
  captchaCode: '0000',
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

const getCodeHandle = async () => {
  try {
    const res = await getCode()
    codeUrl.value = res.data.image
    captchaKey.value = res.data.key
  } catch (_) {}
}

const onSubmitHandle = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    const tokenRes = await login({
      username: loginForm.value.username,
      password: loginForm.value.password,
      captchaCode: loginForm.value.captchaCode,
      captchaKey: captchaKey.value,
    })
    userStore.setToken(tokenRes.data)
    const userInfoRes = await getUserInfo()
    userStore.setUserInfo(userInfoRes.data)
    await router.replace(route.query.redirect ? decodeURIComponent(route.query.redirect as string) : '/')
  } catch (_) {}
}

onMounted(() => { getCodeHandle() })
</script>

<style lang="scss" scoped>
.qingyu-login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #F0FDFA 0%, #ECFEFF 50%, #E0F2FE 100%);
}

.login-card {
  width: 420px;
  max-width: 90vw;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(15, 118, 110, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  margin: 12px 0 4px;
  font-size: 24px;
  font-weight: 800;
  color: #134E4A;
}

.login-desc {
  margin: 0;
  font-size: 14px;
  color: #64748B;
}

.login-code {
  width: 100%;
  height: 40px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid #D6F5EF;
}

.login-btn {
  width: 100%;
  margin-top: 24px;
  height: 44px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 8px;
  background-color: #0F766E;
  border-color: #0F766E;

  &:hover {
    background-color: #0D6B63;
    border-color: #0D6B63;
  }
}

.login-footer {
  margin-top: 16px;
  text-align: center;
  font-size: 12px;
  color: #94A3B8;
}
</style>
