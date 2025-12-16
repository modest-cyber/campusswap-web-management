<template>
  <div class="user-info-page">
    <div class="page-header">
      <h2>个人信息</h2>
    </div>
    
    <div class="info-content">
      <!-- 头像上传区域 -->
      <div class="avatar-section">
        <div class="avatar-label">头像</div>
        <div class="avatar-upload">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            accept="image/*"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="avatar-tip">支持 jpg、png 格式，大小不超过 2MB</div>
        </div>
      </div>
      
      <!-- 基本信息表单 -->
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
        class="info-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled />
          <span class="form-tip">用户名不可修改</span>
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" maxlength="20" />
        </el-form-item>
        
        <el-form-item label="院系" prop="department">
          <el-select v-model="form.department" placeholder="请选择院系" style="width: 100%">
            <el-option label="计算机学院" value="计算机学院" />
            <el-option label="电子工程学院" value="电子工程学院" />
            <el-option label="石油工程学院" value="石油工程学院" />
            <el-option label="化学化工学院" value="化学化工学院" />
            <el-option label="机械工程学院" value="机械工程学院" />
            <el-option label="经济管理学院" value="经济管理学院" />
            <el-option label="外国语学院" value="外国语学院" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存修改</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 密码修改 -->
      <el-divider />
      
      <div class="password-section">
        <h3>修改密码</h3>
        <el-form 
          ref="passwordFormRef" 
          :model="passwordForm" 
          :rules="passwordRules" 
          label-width="100px"
          class="password-form"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input 
              v-model="passwordForm.oldPassword" 
              type="password" 
              placeholder="请输入原密码" 
              show-password
              autocomplete="off"
            />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword" 
              type="password" 
              placeholder="请输入新密码（至少8位，含数字和字母）" 
              show-password
              autocomplete="new-password"
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword" 
              type="password" 
              placeholder="请再次输入新密码" 
              show-password
              autocomplete="new-password"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 注销账号 -->
      <el-divider />
      
      <div class="danger-section">
        <h3>危险操作</h3>
        <el-alert
          title="注销账号后将无法恢复，请谨慎操作"
          type="warning"
          :closable="false"
          show-icon
        />
        <el-button type="danger" plain @click="handleDeleteAccount" class="delete-account-btn">
          注销账号
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

// 表单引用
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 加载状态
const loading = ref(false)
const passwordLoading = ref(false)

// 基本信息表单
const form = reactive({
  username: '',
  nickname: '',
  realName: '',
  department: '',
  phone: '',
  email: '',
  avatar: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 上传地址
const uploadUrl = computed(() => `${import.meta.env.VITE_API_BASE_URL || ''}/api/user/avatar`)

// 上传请求头
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`
}))

// 基本信息校验规则
const rules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ]
}

// 密码校验规则
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少8位', trigger: 'blur' },
    { 
      pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/, 
      message: '密码必须包含数字和字母', 
      trigger: 'blur' 
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 上传前校验
const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 头像上传成功
const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 0) {
    form.avatar = response.data.url
    ElMessage.success('头像上传成功')
    
    // 更新 store 中的用户信息
    if (authStore.userInfo) {
      authStore.setUser({
        ...authStore.userInfo,
        avatar: response.data.url
      })
    }
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 提交基本信息
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      // 实际开发中调用 API
      // await updateUserInfo(form)
      
      // 模拟延迟
      await new Promise(resolve => setTimeout(resolve, 500))
      
      // 更新 store 中的用户信息
      authStore.setUser({
        ...authStore.userInfo,
        ...form
      })
      
      ElMessage.success('保存成功')
    } catch (error) {
      console.error('保存失败:', error)
      ElMessage.error('保存失败，请重试')
    } finally {
      loading.value = false
    }
  })
}

// 重置表单
const handleReset = () => {
  loadUserInfo()
  ElMessage.info('已重置')
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    passwordLoading.value = true
    try {
      // 实际开发中调用 API
      // await changePassword({
      //   oldPassword: passwordForm.oldPassword,
      //   newPassword: passwordForm.newPassword
      // })
      
      // 模拟延迟
      await new Promise(resolve => setTimeout(resolve, 500))
      
      ElMessage.success('密码修改成功，请重新登录')
      
      // 清空密码表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      
      // 退出登录
      setTimeout(() => {
        authStore.logout()
        router.push('/login')
      }, 1000)
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error('修改密码失败，请重试')
    } finally {
      passwordLoading.value = false
    }
  })
}

// 注销账号
const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '注销账号后所有数据将无法恢复，确认要注销账号吗？',
      '警告',
      {
        confirmButtonText: '确认注销',
        cancelButtonText: '取消',
        type: 'error',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    // 实际开发中调用 API
    // await deleteAccount()
    
    ElMessage.success('账号已注销')
    
    // 退出登录
    setTimeout(() => {
      authStore.logout()
      router.push('/login')
    }, 1000)
  } catch (error) {
    // 用户取消操作
  }
}

// 加载用户信息
const loadUserInfo = () => {
  if (authStore.userInfo) {
    form.username = authStore.userInfo.username || ''
    form.nickname = authStore.userInfo.nickname || ''
    form.realName = authStore.userInfo.realName || ''
    form.department = authStore.userInfo.department || ''
    form.phone = authStore.userInfo.phone || ''
    form.email = authStore.userInfo.email || ''
    form.avatar = authStore.userInfo.avatar || ''
  } else {
    // 模拟用户数据
    form.username = 'user001'
    form.nickname = '张三'
    form.realName = '张三'
    form.department = '计算机学院'
    form.phone = '13800138000'
    form.email = 'user001@example.com'
    form.avatar = 'https://via.placeholder.com/150x150?text=Avatar'
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.user-info-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.page-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.info-content {
  max-width: 600px;
}

/* 头像上传 */
.avatar-section {
  display: flex;
  align-items: flex-start;
  margin-bottom: 30px;
}

.avatar-label {
  width: 100px;
  padding-top: 8px;
  color: #606266;
  font-size: 14px;
}

.avatar-upload {
  flex: 1;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
}

.avatar-uploader-icon {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #8c939d;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
  color: #409eff;
}

.avatar-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

/* 表单 */
.info-form {
  margin-bottom: 30px;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}

/* 密码修改 */
.password-section {
  margin-bottom: 30px;
}

.password-section h3 {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 20px 0;
}

.password-form {
  max-width: 500px;
}

/* 危险操作 */
.danger-section h3 {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 16px 0;
}

.delete-account-btn {
  margin-top: 16px;
}

:deep(.el-upload) {
  display: inline-block;
}
</style>
