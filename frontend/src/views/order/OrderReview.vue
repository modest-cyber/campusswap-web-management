<template>
  <div class="order-review-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
        <el-breadcrumb-item><router-link to="/mine/orders">我的订单</router-link></el-breadcrumb-item>
        <el-breadcrumb-item>订单评价</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="review-content">
      <!-- 订单信息 -->
      <div class="order-info-section">
        <h3 class="section-title">订单信息</h3>
        <div class="order-info">
          <img :src="order?.productImage || 'https://via.placeholder.com/80x80?text=No+Image'" alt="商品图片" class="order-image" />
          <div class="order-detail">
            <div class="order-title">{{ order?.productTitle }}</div>
            <div class="order-meta">
              <span>订单号：{{ order?.orderNo }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 评价表单 -->
      <div class="review-form-section">
        <h3 class="section-title">发表评价</h3>
        <el-form 
          ref="formRef" 
          :model="form" 
          :rules="rules" 
          label-width="100px"
        >
          <el-form-item label="评分" prop="rating">
            <el-rate 
              v-model="form.rating" 
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              show-text
              :texts="['非常差', '差', '一般', '满意', '非常满意']"
            />
          </el-form-item>
          
          <el-form-item label="评价内容" prop="comment">
            <el-input 
              v-model="form.comment" 
              type="textarea" 
              :rows="6"
              placeholder="请分享您的购物体验，帮助其他买家做出更好的选择（10-500字）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="loading">
              提交评价
            </el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const router = useRouter()

interface Order {
  id: number
  orderNo: string
  productId: number
  productTitle: string
  productImage?: string
}

// 订单ID
const orderId = ref<number>(Number(route.params.id || 0))

// 订单数据
const order = ref<Order | null>(null)

// 加载状态
const loading = ref(false)

// 表单引用
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  rating: 5,
  comment: ''
})

// 表单校验规则
const rules: FormRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  comment: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, max: 500, message: '评价内容长度在 10 到 500 个字符', trigger: 'blur' }
  ]
}

// 加载订单信息
const loadOrder = async () => {
  try {
    // 实际开发中调用 API
    // const data = await getOrderDetail(orderId.value)
    // order.value = data
    
    // 模拟数据
    order.value = {
      id: 1,
      orderNo: 'ORD20240115001',
      productId: 1,
      productTitle: 'iPhone 13 Pro',
      productImage: 'https://via.placeholder.com/80x80?text=iPhone13Pro'
    }
  } catch (error) {
    console.error('加载订单信息失败:', error)
    ElMessage.error('加载订单信息失败')
  }
}

// 提交评价
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      // 实际开发中调用 API
      // await createReview({
      //   orderId: orderId.value,
      //   rating: form.rating,
      //   comment: form.comment
      // })
      
      // 模拟延迟
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success('评价提交成功')
      
      // 跳转回订单列表
      router.push('/mine/orders')
    } catch (error) {
      console.error('提交评价失败:', error)
      ElMessage.error('提交评价失败，请重试')
    } finally {
      loading.value = false
    }
  })
}

// 取消
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  if (!orderId.value) {
    ElMessage.error('订单信息不存在')
    router.back()
    return
  }
  loadOrder()
})
</script>

<style scoped>
.order-review-page {
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

.review-content {
  max-width: 700px;
  margin: 0 auto;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 16px 0;
}

/* 订单信息 */
.order-info-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.order-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.order-detail {
  flex: 1;
  min-width: 0;
}

.order-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-meta {
  font-size: 14px;
  color: #909399;
}

/* 评价表单 */
.review-form-section {
  margin-bottom: 30px;
}
</style>
