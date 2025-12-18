<template>
  <div class="order-list-page">
    <div class="page-header">
      <h2>我的订单</h2>
    </div>
    
    <!-- 视角切换 -->
    <div class="view-tabs">
      <el-radio-group v-model="viewType" @change="handleViewChange">
        <el-radio-button label="buyer">买家订单</el-radio-button>
        <el-radio-button label="seller">卖家订单</el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-tabs v-model="statusFilter" @tab-change="handleStatusChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="待收货" name="2" />
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>
      
      <div class="filter-tools">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索订单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
          style="width: 240px"
        />
      </div>
    </div>
    
    <!-- 订单列表 -->
    <div class="order-list" v-loading="loading">
      <div v-if="orders.length === 0" class="empty-state">
        <el-empty description="暂无订单" />
      </div>
      
      <div v-else class="order-items">
        <div v-for="order in orders" :key="order.id" class="order-item">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ order.createdAt }}</span>
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
          
          <div class="order-content" @click="goToDetail(order.id)">
            <img :src="order.productImage || 'https://via.placeholder.com/80x80?text=No+Image'" alt="商品图片" class="order-image" />
            <div class="order-info">
              <div class="order-title">{{ order.productTitle }}</div>
              <div class="order-meta">
                <span v-if="viewType === 'buyer'">卖家：{{ order.sellerName }}</span>
                <span v-else>买家：{{ order.buyerName }}</span>
                <span class="separator">|</span>
                <span>{{ getTransactionTypeName(order.transactionType) }}</span>
              </div>
            </div>
            <div class="order-price">
              <div class="price">¥{{ order.totalAmount }}</div>
              <div class="quantity">×{{ order.quantity }}</div>
            </div>
          </div>
          
          <div class="order-actions">
            <!-- 买家视角 -->
            <template v-if="viewType === 'buyer'">
              <el-button 
                v-if="order.status === 0" 
                type="danger" 
                size="small" 
                @click="handleCancel(order)"
              >
                取消订单
              </el-button>
              <el-button 
                v-if="order.status === 2" 
                type="primary" 
                size="small" 
                @click="handleConfirmReceive(order)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="order.status === 3" 
                type="success" 
                size="small" 
                @click="goToReview(order)"
              >
                评价
              </el-button>
            </template>
            
            <!-- 卖家视角 -->
            <template v-else>
              <el-button 
                v-if="order.status === 1" 
                type="primary" 
                size="small" 
                @click="handleDeliver(order)"
              >
                {{ order.transactionType === 0 ? '确认面交' : '发货' }}
              </el-button>
            </template>
            
            <el-button size="small" @click="goToDetail(order.id)">查看详情</el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handlePageSizeChange"
        @current-change="handleCurrentPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getOrderList, cancelOrder, confirmReceive, deliverOrder } from '../../api/order'

const router = useRouter()

interface Order {
  id: number
  orderNo: string
  productId: number
  productTitle: string
  productImage?: string
  buyerId: number
  buyerName: string
  sellerId: number
  sellerName: string
  quantity: number
  totalAmount: number
  transactionType: number
  status: number
  createdAt: string
}

// 视角类型
const viewType = ref<'buyer' | 'seller'>('buyer')

// 状态筛选
const statusFilter = ref('all')

// 搜索关键词
const searchKeyword = ref('')

// 日期范围
const dateRange = ref<[Date, Date] | null>(null)

// 订单列表
const orders = ref<Order[]>([])

// 加载状态
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: '',
    4: 'info'
  }
  return typeMap[status] || 'info'
}

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

// 获取交易方式名称
const getTransactionTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    0: '面交',
    1: '邮寄',
    2: '均可'
  }
  return typeMap[type] || '未知'
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const result = await getOrderList({
      viewType: viewType.value,
      status: statusFilter.value === 'all' ? undefined : Number(statusFilter.value),
      orderNo: searchKeyword.value,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    orders.value = result.list || []
    total.value = result.total || 0
    
    // 调试日志
    console.log('订单列表 - 视角类型:', viewType.value)
    console.log('订单列表 - 订单数据:', orders.value)
    if (orders.value.length > 0 && orders.value[0]) {
      console.log('第一条订单 - 买家:', orders.value[0].buyerName, '卖家:', orders.value[0].sellerName)
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
    orders.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 视角切换
const handleViewChange = () => {
  currentPage.value = 1
  fetchOrders()
}

// 状态筛选变化
const handleStatusChange = () => {
  currentPage.value = 1
  fetchOrders()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchOrders()
}

// 日期变化
const handleDateChange = () => {
  currentPage.value = 1
  fetchOrders()
}

// 分页大小变化
const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchOrders()
}

// 当前页变化
const handleCurrentPageChange = (page: number) => {
  currentPage.value = page
  fetchOrders()
}

// 取消订单
const handleCancel = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确认要取消该订单吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelOrder(order.id)
    
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 确认收货
const handleConfirmReceive = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗?', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await confirmReceive(order.id)
    
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 发货/确认面交
const handleDeliver = async (order: Order) => {
  const action = order.transactionType === 0 ? '确认面交' : '发货'
  
  try {
    await ElMessageBox.confirm(`确认要${action}吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deliverOrder(order.id)
    
    ElMessage.success(`${action}成功`)
    fetchOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

// 跳转到详情
const goToDetail = (orderId: number) => {
  router.push(`/order/${orderId}`)
}

// 跳转到评价
const goToReview = (order: Order) => {
  router.push(`/order/${order.id}/review`)
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list-page {
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

.view-tabs {
  margin-bottom: 20px;
}

.filter-bar {
  margin-bottom: 20px;
}

.filter-tools {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.order-list {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.order-no {
  font-size: 14px;
  color: #606266;
  flex: 1;
}

.order-time {
  font-size: 14px;
  color: #909399;
}

.order-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.order-content:hover {
  background-color: #f5f7fa;
}

.order-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.order-info {
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

.separator {
  margin: 0 8px;
}

.order-price {
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

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  background-color: #fafafa;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
