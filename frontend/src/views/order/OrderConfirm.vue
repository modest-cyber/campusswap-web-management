<template>
  <div class="order-confirm-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
        <el-breadcrumb-item><router-link to="/products">商品列表</router-link></el-breadcrumb-item>
        <el-breadcrumb-item>订单确认</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="confirm-content">
      <!-- 商品信息 -->
      <div class="section">
        <h3 class="section-title">商品信息</h3>
        <div class="product-info">
          <img :src="product?.images?.[0] || 'https://via.placeholder.com/100x100?text=No+Image'" alt="商品图片" class="product-image" />
          <div class="product-detail">
            <div class="product-name">{{ product?.title }}</div>
            <div class="product-meta">
              <span>{{ product?.categoryName }}</span>
              <span class="separator">·</span>
              <span>{{ product?.condition }}</span>
            </div>
            <div class="product-price">¥{{ product?.price }}</div>
          </div>
          <div class="product-quantity">
            <span class="quantity-label">数量：</span>
            <el-input-number v-model="quantity" :min="1" :max="1" disabled />
          </div>
        </div>
      </div>
      
      <!-- 交易方式 -->
      <div class="section">
        <h3 class="section-title">交易方式</h3>
        <el-radio-group v-model="form.transactionType" @change="handleTransactionTypeChange">
          <el-radio :value="1" :disabled="!isTransactionTypeAvailable(1)">面交</el-radio>
          <el-radio :value="2" :disabled="!isTransactionTypeAvailable(2)">邮寄</el-radio>
        </el-radio-group>
      </div>
      
      <!-- 邮寄地址 -->
      <div class="section" v-if="form.transactionType === 2">
        <h3 class="section-title">邮寄地址</h3>
        <el-form 
          ref="addressFormRef" 
          :model="form.address" 
          :rules="addressRules" 
          label-width="100px"
        >
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="form.address.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          
          <el-form-item label="联系电话" prop="receiverPhone">
            <el-input v-model="form.address.receiverPhone" placeholder="请输入联系电话" maxlength="11" />
          </el-form-item>
          
          <el-form-item label="详细地址" prop="receiverAddress">
            <el-input 
              v-model="form.address.receiverAddress" 
              type="textarea" 
              :rows="3"
              placeholder="请输入详细地址（省市区+街道门牌号）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 订单备注 -->
      <div class="section">
        <h3 class="section-title">订单备注</h3>
        <el-input 
          v-model="form.remark" 
          type="textarea" 
          :rows="3"
          placeholder="选填，给卖家留言（限200字）"
          maxlength="200"
          show-word-limit
        />
      </div>
      
      <!-- 订单金额 -->
      <div class="section">
        <div class="amount-info">
          <div class="amount-item">
            <span>商品金额：</span>
            <span class="amount">¥{{ product?.price || 0 }}</span>
          </div>
          <div class="amount-item total">
            <span>应付总额：</span>
            <span class="amount total-amount">¥{{ product?.price || 0 }}</span>
          </div>
        </div>
      </div>
      
      <!-- 提交按钮 -->
      <div class="submit-section">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading" size="large">
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { Product } from '../../api/product'

const route = useRoute()
const router = useRouter()

// 商品ID
const productId = ref<number | string>(Number(route.params.productId || route.query.productId || 0))

// 商品数据
const product = ref<Product | null>(null)

// 数量（固定为1）
const quantity = ref(1)

// 加载状态
const loading = ref(false)

// 表单引用
const addressFormRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  productId: 0,
  quantity: 1,
  transactionType: 1, // 1-面交 2-邮寄
  address: {
    receiverName: '',
    receiverPhone: '',
    receiverAddress: ''
  },
  remark: ''
})

// 地址校验规则
const addressRules: FormRules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  receiverAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 10, message: '地址至少10个字符', trigger: 'blur' }
  ]
}

// 判断交易方式是否可用
const isTransactionTypeAvailable = (type: number) => {
  if (!product.value) return false
  const productType = product.value.transactionType || 3
  
  // 3表示均可
  if (productType === 3) return true
  
  // 否则只能选择商品支持的方式
  return productType === type
}

// 交易方式变化
const handleTransactionTypeChange = (value: number) => {
  // 如果切换到面交，清空地址信息
  if (value === 1) {
    form.address = {
      receiverName: '',
      receiverPhone: '',
      receiverAddress: ''
    }
  }
}

// 加载商品信息
const loadProduct = async () => {
  try {
    // 实际开发中调用 API
    // const data = await getProductDetail(productId.value)
    // product.value = data
    
    // 模拟数据
    product.value = {
      id: 1,
      title: 'iPhone 13 Pro',
      description: '全新未拆封 iPhone 13 Pro，256GB 远峰蓝色。',
      price: 5999,
      originalPrice: 7999,
      images: ['https://via.placeholder.com/100x100?text=iPhone13Pro'],
      condition: '99成新',
      transactionType: 3, // 均可
      categoryName: '数码产品',
      sellerName: '张三',
      createdAt: '2024-01-15'
    }
    
    form.productId = product.value.id
    
    // 根据商品支持的交易方式设置默认值
    if (product.value.transactionType === 2) {
      form.transactionType = 2
    } else {
      form.transactionType = 1
    }
  } catch (error) {
    console.error('加载商品信息失败:', error)
    ElMessage.error('加载商品信息失败')
  }
}

// 提交订单
const handleSubmit = async () => {
  // 如果是邮寄，需要校验地址
  if (form.transactionType === 2) {
    if (!addressFormRef.value) return
    
    const valid = await addressFormRef.value.validate().catch(() => false)
    if (!valid) {
      ElMessage.warning('请完善邮寄地址信息')
      return
    }
  }
  
  loading.value = true
  try {
    // 实际开发中调用 API
    // await createOrder({
    //   productId: form.productId,
    //   quantity: form.quantity,
    //   transactionType: form.transactionType,
    //   receiverName: form.transactionType === 2 ? form.address.receiverName : undefined,
    //   receiverPhone: form.transactionType === 2 ? form.address.receiverPhone : undefined,
    //   receiverAddress: form.transactionType === 2 ? form.address.receiverAddress : undefined,
    //   remark: form.remark
    // })
    
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('订单提交成功')
    
    // 跳转到订单列表
    router.push('/mine/orders')
  } catch (error) {
    console.error('提交订单失败:', error)
    ElMessage.error('提交订单失败，请重试')
  } finally {
    loading.value = false
  }
}

// 取消
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  if (!productId.value) {
    ElMessage.error('商品信息不存在')
    router.back()
    return
  }
  loadProduct()
})
</script>

<style scoped>
.order-confirm-page {
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

.confirm-content {
  max-width: 800px;
  margin: 0 auto;
}

.section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section:last-of-type {
  border-bottom: none;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 16px 0;
}

/* 商品信息 */
.product-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.product-detail {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.separator {
  margin: 0 8px;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.product-quantity {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-label {
  font-size: 14px;
  color: #606266;
}

/* 金额信息 */
.amount-info {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.amount-item:last-child {
  margin-bottom: 0;
}

.amount-item.total {
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
  font-size: 16px;
  color: #303133;
  font-weight: bold;
}

.amount {
  color: #f56c6c;
}

.total-amount {
  font-size: 24px;
}

/* 提交按钮 */
.submit-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}
</style>
