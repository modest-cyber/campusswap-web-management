<template>
  <div class="order-detail-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><router-link to="/">首页</router-link></el-breadcrumb-item>
        <el-breadcrumb-item><router-link to="/mine/orders">我的订单</router-link></el-breadcrumb-item>
        <el-breadcrumb-item>订单详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="detail-content" v-loading="loading">
      <div v-if="order" class="order-detail">
        <!-- 订单状态 -->
        <div class="status-section">
          <div class="status-icon">
            <el-icon size="48" :color="getStatusColor(order.status)">
              <component :is="getStatusIcon(order.status)" />
            </el-icon>
          </div>
          <div class="status-info">
            <div class="status-text">{{ getStatusText(order.status) }}</div>
            <div class="status-tip">{{ getStatusTip(order.status) }}</div>
          </div>
        </div>
        
        <!-- 状态流转记录（可选） -->
        <div class="timeline-section" v-if="order.timeline && order.timeline.length > 0">
          <h3 class="section-title">订单进度</h3>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in order.timeline"
              :key="index"
              :timestamp="item.time"
              placement="top"
            >
              {{ item.content }}
            </el-timeline-item>
          </el-timeline>
        </div>
        
        <!-- 商品信息 -->
        <div class="section">
          <h3 class="section-title">商品信息</h3>
          <div class="product-info">
            <img :src="order.productImage || 'https://via.placeholder.com/100x100?text=No+Image'" alt="商品图片" class="product-image" @click="goToProduct(order.productId)" />
            <div class="product-detail">
              <div class="product-name" @click="goToProduct(order.productId)">{{ order.productTitle }}</div>
              <div class="product-meta">
                <span>{{ order.categoryName }}</span>
                <span class="separator">·</span>
                <span>{{ order.condition }}</span>
              </div>
            </div>
            <div class="product-price">
              <div class="price">¥{{ order.totalAmount }}</div>
              <div class="quantity">×{{ order.quantity }}</div>
            </div>
          </div>
        </div>
        
        <!-- 交易信息 -->
        <div class="section">
          <h3 class="section-title">交易信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">订单号：</span>
              <span class="value">{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">下单时间：</span>
              <span class="value">{{ order.createdAt }}</span>
            </div>
            <div class="info-item">
              <span class="label">交易方式：</span>
              <span class="value">{{ getTransactionTypeName(order.transactionType) }}</span>
            </div>
            <div class="info-item" v-if="isCurrentUserBuyer">
              <span class="label">卖家：</span>
              <span class="value">{{ order.sellerName }}</span>
            </div>
            <div class="info-item" v-else>
              <span class="label">买家：</span>
              <span class="value">{{ order.buyerName }}</span>
            </div>
          </div>
        </div>
        
        <!-- 邮寄地址（如果是邮寄） -->
        <div class="section" v-if="order.transactionType === 1 && order.address">
          <h3 class="section-title">邮寄信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">收货人：</span>
              <span class="value">{{ order.address.receiverName }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span class="value">{{ order.address.receiverPhone }}</span>
            </div>
            <div class="info-item full-width">
              <span class="label">收货地址：</span>
              <span class="value">{{ order.address.receiverAddress }}</span>
            </div>
            <div class="info-item full-width" v-if="order.trackingNumber">
              <span class="label">物流单号：</span>
              <span class="value">{{ order.trackingNumber }}</span>
            </div>
          </div>
        </div>
        
        <!-- 订单备注 -->
        <div class="section" v-if="order.remark">
          <h3 class="section-title">订单备注</h3>
          <div class="remark-content">{{ order.remark }}</div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="action-section">
          <template v-if="isCurrentUserBuyer">
            <!-- 买家操作 -->
            <el-button v-if="order.status === 0" type="danger" @click="handleCancel">
              取消订单
            </el-button>
            <el-button v-if="order.status === 2" type="primary" @click="handleConfirmReceive">
              确认收货
            </el-button>
            <el-button v-if="order.status === 3 && !order.hasReviewed" type="success" @click="goToReview">
              评价
            </el-button>
          </template>
          
          <template v-else>
            <!-- 卖家操作 -->
            <el-button v-if="order.status === 1" type="primary" @click="handleDeliver">
              {{ order.transactionType === 0 ? '确认面交' : '发货' }}
            </el-button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { SuccessFilled, WarningFilled, InfoFilled, CircleCheckFilled } from '@element-plus/icons-vue'
import { getOrderDetail, cancelOrder, confirmReceive, deliverOrder } from '../../api/order'
import { useAuthStore } from '../../store/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

interface OrderAddress {
  receiverName: string
  receiverPhone: string
  receiverAddress: string
}

interface TimelineItem {
  time: string
  content: string
}

interface Order {
  id: number
  orderNo: string
  productId: number
  productTitle: string
  productImage?: string
  categoryName?: string
  condition?: string
  buyerId: number
  buyerName: string
  sellerId: number
  sellerName: string
  quantity: number
  totalAmount: number
  transactionType: number
  status: number
  address?: OrderAddress
  trackingNumber?: string
  remark?: string
  hasReviewed?: boolean
  createdAt: string
  timeline?: TimelineItem[]
}

// 订单ID
const orderId = ref<number>(Number(route.params.id || 0))

// 订单数据
const order = ref<Order | null>(null)

// 加载状态
const loading = ref(false)

// 当前用户ID
const currentUserId = computed(() => authStore.userInfo?.id)

// 是否为买家
const isCurrentUserBuyer = computed(() => {
  if (!order.value || !currentUserId.value) return false
  const isBuyer = order.value.buyerId === currentUserId.value
  console.log('订单详情 - 当前用户ID:', currentUserId.value, '买家ID:', order.value.buyerId, '卖家ID:', order.value.sellerId, '是买家:', isBuyer)
  return isBuyer
})

// 获取状态文本
const getStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: '待支付',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return textMap[status] || '未知'
}

// 获取状态提示
const getStatusTip = (status: number) => {
  const tipMap: Record<number, string> = {
    0: '请尽快完成支付',
    1: '等待卖家发货',
    2: '商品已发货，请注意查收',
    3: '交易已完成',
    4: '订单已取消'
  }
  return tipMap[status] || ''
}

// 获取状态图标
const getStatusIcon = (status: number) => {
  const iconMap: Record<number, any> = {
    0: WarningFilled,
    1: InfoFilled,
    2: InfoFilled,
    3: SuccessFilled,
    4: CircleCheckFilled
  }
  return iconMap[status] || InfoFilled
}

// 获取状态颜色
const getStatusColor = (status: number) => {
  const colorMap: Record<number, string> = {
    0: '#e6a23c',
    1: '#409eff',
    2: '#67c23a',
    3: '#67c23a',
    4: '#909399'
  }
  return colorMap[status] || '#909399'
}

// 获取交易方式名称
const getTransactionTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    0: '面交',
    1: '邮寄',
    2: '均可'
  }
  return typeMap[type] || '未知'
}

// 加载订单详情
const loadOrderDetail = async () => {
  loading.value = true
  try {
    const data: any = await getOrderDetail(orderId.value)
    
    // 处理地址信息
    if (data.address && typeof data.address === 'string') {
      // 解析地址字符串 "姓名 电话 地址"
      const addressStr = data.address as string
      const parts = addressStr.split(' ')
      if (parts.length >= 3) {
        data.address = {
          receiverName: parts[0],
          receiverPhone: parts[1],
          receiverAddress: parts.slice(2).join(' ')
        }
      }
    }
    
    order.value = data
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确认要取消该订单吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelOrder(orderId.value)
    
    ElMessage.success('订单已取消')
    router.push('/mine/orders')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 确认收货
const handleConfirmReceive = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗?', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await confirmReceive(orderId.value)
    
    ElMessage.success('确认收货成功')
    loadOrderDetail()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 发货/确认面交
const handleDeliver = async () => {
  if (!order.value) return
  
  const action = order.value.transactionType === 0 ? '确认面交' : '发货'
  
  try {
    await ElMessageBox.confirm(`确认要${action}吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deliverOrder(orderId.value)
    
    ElMessage.success(`${action}成功`)
    loadOrderDetail()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

// 跳转到商品详情
const goToProduct = (productId: number) => {
  router.push(`/products/${productId}`)
}

// 跳转到评价
const goToReview = () => {
  router.push(`/order/${orderId.value}/review`)
}

onMounted(() => {
  if (!orderId.value) {
    ElMessage.error('订单信息不存在')
    router.back()
    return
  }
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
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

.detail-content {
  max-width: 900px;
  margin: 0 auto;
  min-height: 400px;
}

/* 订单状态 */
.status-section {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  margin-bottom: 30px;
  color: #fff;
}

.status-icon {
  flex-shrink: 0;
}

.status-info {
  flex: 1;
}

.status-text {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.status-tip {
  font-size: 14px;
  opacity: 0.9;
}

/* 时间轴 */
.timeline-section {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

/* 通用section */
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
  cursor: pointer;
  transition: transform 0.3s;
}

.product-image:hover {
  transform: scale(1.05);
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
  cursor: pointer;
  transition: color 0.3s;
}

.product-name:hover {
  color: #409eff;
}

.product-meta {
  font-size: 14px;
  color: #909399;
}

.separator {
  margin: 0 8px;
}

.product-price {
  text-align: right;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 4px;
}

.quantity {
  font-size: 14px;
  color: #909399;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  font-size: 14px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.label {
  color: #909399;
  min-width: 80px;
  flex-shrink: 0;
}

.value {
  color: #303133;
  flex: 1;
}

/* 备注内容 */
.remark-content {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

/* 操作按钮 */
.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>
