<template>
  <div class="product-publish-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
        <el-breadcrumb-item><router-link to="/mine/products">我的发布</router-link></el-breadcrumb-item>
        <el-breadcrumb-item>{{ isEdit ? '编辑商品' : '发布商品' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="publish-content">
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="120px"
        class="publish-form"
      >
        <el-form-item label="商品名称" prop="title">
          <el-input 
            v-model="form.title" 
            placeholder="请输入商品名称（限50字）" 
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="商品分类" prop="categoryId">
          <el-cascader
            v-model="form.categoryId"
            :options="categories"
            :props="{ value: 'id', label: 'name', children: 'children', emitPath: false }"
            placeholder="请选择商品分类"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="商品价格" prop="price">
          <el-input-number 
            v-model="form.price" 
            :min="0" 
            :step="1" 
            :precision="2"
            placeholder="请输入价格"
          />
          <span class="form-tip">元</span>
        </el-form-item>
        
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number 
            v-model="form.originalPrice" 
            :min="0" 
            :step="1" 
            :precision="2"
            placeholder="选填"
          />
          <span class="form-tip">元（选填）</span>
        </el-form-item>
        
        <el-form-item label="成色" prop="condition">
          <el-select v-model="form.condition" placeholder="请选择成色">
            <el-option label="全新" value="全新" />
            <el-option label="99成新" value="99成新" />
            <el-option label="98成新" value="98成新" />
            <el-option label="9成新" value="9成新" />
            <el-option label="8成新" value="8成新" />
            <el-option label="7成新及以下" value="7成新及以下" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="交易方式" prop="transactionType">
          <el-radio-group v-model="form.transactionType">
            <el-radio :value="1">面交</el-radio>
            <el-radio :value="2">邮寄</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="6"
            placeholder="请详细描述商品信息（限500字）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="商品图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :limit="9"
            :accept="'image/*'"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="upload-tip">
                支持 jpg、png、gif 格式，单张不超过 5MB，最多 9 张
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '保存修改' : '发布商品' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" title="图片预览">
      <img :src="previewImageUrl" alt="预览图片" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps, UploadUserFile, FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import { publishProduct, updateProduct, getProductDetail, type PublishProductParams } from '../../api/product'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 表单引用
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  title: '',
  categoryId: undefined as number | undefined,
  price: undefined as number | undefined,
  originalPrice: undefined as number | undefined,
  condition: '',
  transactionType: 1,
  description: '',
  images: [] as string[]
})

// 文件列表
const fileList = ref<UploadUserFile[]>([])

// 分类数据
const categories = ref([
  { id: 1, name: '数码产品', children: [] },
  { id: 2, name: '图书音像', children: [] },
  { id: 3, name: '生活用品', children: [] },
  { id: 4, name: '体育用品', children: [] },
  { id: 5, name: '服饰鞋包', children: [] },
  { id: 6, name: '其他', children: [] }
])

// 上传地址
const uploadUrl = '/api/file/upload'

// 上传请求头
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`
}))

// 加载状态
const loading = ref(false)

// 图片预览
const previewVisible = ref(false)
const previewImageUrl = ref('')

// 表单校验规则
const rules: FormRules = {
  title: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '商品名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于 0', trigger: 'blur' }
  ],
  condition: [
    { required: true, message: '请选择成色', trigger: 'change' }
  ],
  transactionType: [
    { required: true, message: '请选择交易方式', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 10, max: 500, message: '商品描述长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  images: [
    { 
      type: 'array', 
      required: true, 
      message: '请至少上传 1 张商品图片', 
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (!value || value.length === 0) {
          callback(new Error('请至少上传 1 张商品图片'))
        } else {
          callback()
        }
      }
    }
  ]
}

// 上传前校验
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess: UploadProps['onSuccess'] = (response, file) => {
  if (response.code === 0) {
    form.images.push(response.data)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
    // 移除上传失败的文件
    const index = fileList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      fileList.value.splice(index, 1)
    }
  }
}

// 上传失败
const handleUploadError: UploadProps['onError'] = () => {
  ElMessage.error('上传失败，请重试')
}

// 移除图片
const handleRemove: UploadProps['onRemove'] = (file) => {
  const index = fileList.value.findIndex(item => item.uid === file.uid)
  if (index > -1) {
    form.images.splice(index, 1)
  }
}

// 预览图片
const handlePreview: UploadProps['onPreview'] = (file) => {
  previewImageUrl.value = file.url || ''
  previewVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const data: PublishProductParams = {
        title: form.title,
        description: form.description,
        price: form.price!,
        originalPrice: form.originalPrice,
        categoryId: form.categoryId!,
        condition: form.condition,
        transactionType: form.transactionType,
        images: form.images
      }
      
      if (isEdit.value) {
        await updateProduct(route.params.id as string, data)
      } else {
        await publishProduct(data)
      }
      
      ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
      router.push('/mine/products')
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      loading.value = false
    }
  })
}

// 取消
const handleCancel = () => {
  router.back()
}

// 加载商品详情（编辑模式）
const loadProductDetail = async () => {
  if (!isEdit.value) return
  
  try {
    // 实际开发中调用 API
    // const data = await getProductDetail(route.params.id)
    // form.title = data.title
    // ...
    
    // 模拟数据
    form.title = 'iPhone 13 Pro'
    form.categoryId = 1
    form.price = 5999
    form.originalPrice = 7999
    form.condition = '99成新'
    form.transactionType = 3
    form.description = '全新未拆封 iPhone 13 Pro，256GB 远峰蓝色。'
    form.images = ['https://via.placeholder.com/400x300?text=iPhone13Pro1']
    
    // 初始化文件列表
    fileList.value = form.images.map((url, index) => ({
      uid: String(Date.now() + index),
      name: `image-${index}.jpg`,
      url,
      status: 'success'
    }))
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  }
}

onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.product-publish-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.publish-content {
  max-width: 800px;
  margin: 0 auto;
}

.publish-form {
  padding: 20px 0;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
  margin-top: 4px;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
}
</style>
