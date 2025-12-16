<template>
  <div class="product-detail-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
        <el-breadcrumb-item><router-link to="/products">商品分类</router-link></el-breadcrumb-item>
        <el-breadcrumb-item>{{ product?.categoryName }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product?.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="product-detail-content">
      <!-- 商品主信息区域 -->
      <div class="product-main">
        <!-- 商品图片轮播 -->
        <div class="product-images">
          <el-carousel height="400px" trigger="click">
            <el-carousel-item v-for="(image, index) in product?.images" :key="index">
              <img :src="image" alt="商品图片" class="product-image" />
            </el-carousel-item>
          </el-carousel>
          <!-- 缩略图列表 -->
          <div class="image-thumbnails" v-if="product?.images && product.images.length > 1">
            <div 
              v-for="(image, index) in product.images" 
              :key="index"
              class="thumbnail-item"
              :class="{ active: activeImageIndex === index }"
              @click="activeImageIndex = index"
            >
              <img :src="image" alt="缩略图" />
            </div>
          </div>
        </div>
        
        <!-- 商品信息 -->
        <div class="product-info">
          <!-- 商品标题和价格 -->
          <div class="product-header">
            <h1 class="product-title">{{ product?.title }}</h1>
            <div class="product-price">
              <span class="current-price">¥{{ product?.price }}</span>
              <span class="original-price" v-if="product?.originalPrice">¥{{ product?.originalPrice }}</span>
            </div>
          </div>
          
          <!-- 商品基本信息 -->
          <div class="product-meta">
            <div class="meta-item">
              <span class="meta-label">成色：</span>
              <el-tag :type="getConditionType(product?.condition)">{{ product?.condition }}</el-tag>
            </div>
            <div class="meta-item">
              <span class="meta-label">分类：</span>
              <span>{{ product?.categoryName }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">交易方式：</span>
              <span>{{ getTransactionTypeName(product?.transactionType) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">浏览：</span>
              <span>{{ product?.viewCount }}次</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">收藏：</span>
              <span>{{ product?.favoriteCount }}人</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">发布时间：</span>
              <span>{{ product?.createdAt }}</span>
            </div>
          </div>
          
          <!-- 卖家信息 -->
          <div class="seller-info">
            <div class="seller-header">
              <h3 class="seller-title">卖家信息</h3>
            </div>
            <div class="seller-detail">
              <div class="seller-avatar">
                <img :src="product?.sellerAvatar || 'https://via.placeholder.com/40x40?text=头像'" alt="卖家头像" />
              </div>
              <div class="seller-main">
                <div class="seller-name">{{ product?.sellerName }}</div>
                <div class="seller-department">{{ product?.sellerDepartment }}</div>
              </div>
              <el-button type="primary" plain @click="contactSeller">
                <el-icon><Message /></el-icon>
                联系卖家
              </el-button>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="large" 
              @click="handleOrder"
              class="order-btn"
            >
              <el-icon><Shop /></el-icon>
              立即购买
            </el-button>
            <el-button 
              type="success" 
              size="large" 
              @click="handleFavorite"
              :icon="product?.isFavorite ? StarFilled : Star"
            >
              {{ product?.isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button 
              type="warning" 
              size="large" 
              @click="reportProduct"
            >
              <el-icon><Warning /></el-icon>
              举报
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 商品描述区域 -->
      <div class="product-description">
        <h3 class="section-title">商品描述</h3>
        <div class="description-content" v-html="product?.description || '<p>暂无描述</p>'"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { toggleFavorite } from '../api/product'
import type { Product } from '../api/product'
import { Star, StarFilled, Message, Shop, Warning } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 商品ID
const productId = ref<number | string>(Number(Array.isArray(route.params.id) ? route.params.id[0] : route.params.id || 0))

// 商品详情数据
const product = ref<Product | null>(null)

// 当前激活的图片索引
const activeImageIndex = ref(0)

// 模拟商品详情数据
const mockProduct: Product = {
  id: 1,
  title: 'iPhone 13 Pro',
  description: '<p>全新未拆封 iPhone 13 Pro，256GB 远峰蓝色。</p><p>支持面交和邮寄，价格可小刀。</p>',
  price: 5999,
  originalPrice: 7999,
  images: [
    'https://via.placeholder.com/400x300?text=iPhone13Pro1',
    'https://via.placeholder.com/400x300?text=iPhone13Pro2',
    'https://via.placeholder.com/400x300?text=iPhone13Pro3'
  ],
  status: 1,
  condition: '99成新',
  transactionType: 1,
  viewCount: 120,
  favoriteCount: 20,
  categoryId: 1,
  categoryName: '数码产品',
  sellerName: '张三',
  sellerDepartment: '计算机学院',
  sellerAvatar: 'https://via.placeholder.com/40x40?text=张三',
  createdAt: '2024-01-15',
  isFavorite: false
}

// 获取商品详情
const fetchProductDetail = async () => {
  try {
    if (!productId.value) return
    
    // 实际开发中调用API
    // const data = await getProductDetail(productId.value)
    // product.value = data
    
    // 目前使用模拟数据
    product.value = mockProduct
  } catch (error) {
    console.error('Failed to fetch product detail:', error)
    ElMessage.error('获取商品详情失败')
  }
}

// 根据成色获取标签类型
const getConditionType = (condition?: string) => {
  switch (condition) {
    case '全新':
      return 'success'
    case '99成新':
    case '98成新':
      return 'primary'
    case '9成新':
    case '8成新':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取交易方式名称
const getTransactionTypeName = (type?: number) => {
  const typeMap: Record<number, string> = {
    1: '面交',
    2: '邮寄',
    3: '均可'
  }
  return typeMap[type || 3]
}

// 处理收藏
const handleFavorite = async () => {
  if (!product.value) return
  
  try {
    await toggleFavorite(product.value.id, !product.value.isFavorite)
    product.value.isFavorite = !product.value.isFavorite
    product.value.favoriteCount = product.value.isFavorite 
      ? (product.value.favoriteCount || 0) + 1 
      : (product.value.favoriteCount || 1) - 1
    ElMessage.success(product.value.isFavorite ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    console.error('Failed to toggle favorite:', error)
    ElMessage.error('操作失败')
  }
}

// 联系卖家
const contactSeller = () => {
  if (!product.value) return
  
  ElMessageBox.prompt('请输入您的留言：', '联系卖家', {
    confirmButtonText: '发送',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入您想对卖家说的话...',
    inputValidator: (value) => {
      if (!value.trim()) {
        return '留言不能为空'
      }
      return true
    }
  }).then(() => {
    ElMessage.success('留言发送成功')
  }).catch(() => {
    // 用户取消操作
  })
}

// 立即购买
const handleOrder = () => {
  if (!product.value) return
  
  // 跳转到订单确认页
  router.push({
    path: '/order/confirm',
    query: { productId: product.value.id }
  })
}

// 举报商品
const reportProduct = () => {
  if (!product.value) return
  
  ElMessageBox.prompt('请输入举报原因：', '举报商品', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请详细描述您举报的原因...',
    inputValidator: (value) => {
      if (!value.trim()) {
        return '举报原因不能为空'
      }
      if (value.trim().length < 10) {
        return '举报原因至少10个字符'
      }
      return true
    }
  }).then(() => {
    ElMessage.success('举报提交成功，我们会尽快处理')
  }).catch(() => {
    // 用户取消操作
  })
}

onMounted(() => {
  fetchProductDetail()
})
</script>

<style scoped>
.product-detail-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-header {
  margin-bottom: 20px;
}

.product-detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 商品主信息区域 */
.product-main {
  display: flex;
  gap: 24px;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

/* 商品图片区域 */
.product-images {
  width: 400px;
  flex-shrink: 0;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.image-thumbnails {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.thumbnail-item {
  width: 80px;
  height: 60px;
  border: 2px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.thumbnail-item.active {
  border-color: #67c23a;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息区域 */
.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  line-height: 1.3;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 12px;
}

.current-price {
  font-size: 32px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 18px;
  color: #909399;
  text-decoration: line-through;
}

/* 商品基本信息 */
.product-meta {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  color: #606266;
  font-size: 14px;
}

/* 卖家信息 */
.seller-info {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
}

.seller-header {
  margin-bottom: 12px;
}

.seller-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.seller-detail {
  display: flex;
  align-items: center;
  gap: 12px;
}

.seller-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.seller-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.seller-main {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.seller-department {
  font-size: 14px;
  color: #606266;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.order-btn {
  flex: 1;
}

/* 商品描述区域 */
.product-description {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 16px 0;
}

.description-content {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
}

.description-content p {
  margin: 0 0 12px 0;
}

.description-content p:last-child {
  margin-bottom: 0;
}
</style>