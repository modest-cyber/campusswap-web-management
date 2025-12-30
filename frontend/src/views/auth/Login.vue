<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, type LoginResult } from '../../api/auth'
import { useAuthStore } from '../../store/auth'
import backgroundImg from '../../img/school.jpg'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)

const form = reactive({
  account: '',
  password: '',
  role: 'student' // 默认选择学生
})

const rules = {
  account: [{ required: true, message: '请输入用户名/邮箱/手机号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const formRef = ref()

const onSubmit = () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await login({ account: form.account, password: form.password }) as unknown as LoginResult
      
      // 验证用户角色是否匹配
      if (form.role === 'admin' && res.userInfo.role !== 'admin') {
        ElMessage.error('当前账号不是管理员账号')
        loading.value = false
        return
      }
      if (form.role === 'student' && res.userInfo.role === 'admin') {
        ElMessage.error('管理员请选择“管理员”身份登录')
        loading.value = false
        return
      }
      
      authStore.setToken(res.token)
      authStore.setUser(res.userInfo)
      
      console.log('登录成功后的用户信息:', res.userInfo)
      console.log('authStore.role:', authStore.role)
      
      ElMessage.success('登录成功')
      
      // 等待下一个tick确保状态更新
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 根据用户角色跳转
      if (res.userInfo.role === 'admin') {
        console.log('管理员登录，跳转到 /admin')
        await router.replace('/admin')
      } else {
        const redirect = (route.query.redirect as string) || '/'
        console.log('普通用户登录，跳转到', redirect)
        await router.replace(redirect)
      }
    } catch (error: any) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="身份选择" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="student">学生</el-radio>
            <el-radio value="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名/邮箱/手机号" prop="account">
          <el-input v-model="form.account" placeholder="请输入" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入" show-password autocomplete="current-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit" style="width: 100%">登录</el-button>
        </el-form-item>
        <div class="switch">
          还没有账号？
          <el-link type="primary" @click="router.push({ name: 'register' })">去注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('../../img/school.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  margin: 0;
  padding: 0;
}

.auth-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 0;
}

.auth-card {
  width: 380px;
  padding: 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.switch {
  text-align: center;
  color: #606266;
}
</style>

