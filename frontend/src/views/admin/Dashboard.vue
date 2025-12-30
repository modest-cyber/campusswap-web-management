<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.userCount }}</div>
              <div class="stats-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon product-icon">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.productCount }}</div>
              <div class="stats-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon order-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.orderCount }}</div>
              <div class="stats-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon amount-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">¥{{ stats.totalAmount }}</div>
              <div class="stats-label">成交总额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待审核商品</span>
              <el-badge :value="stats.pendingReviewCount" class="badge" />
            </div>
          </template>
          <div class="pending-info">
            <p>当前有 <strong>{{ stats.pendingReviewCount }}</strong> 件商品待审核</p>
            <el-button type="primary" @click="goToReview">前往审核</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Goods, Document, Money } from '@element-plus/icons-vue'
import { getDashboardStats, type DashboardStats } from '../../api/admin'

const router = useRouter()

const stats = ref<DashboardStats>({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
  totalAmount: 0,
  pendingReviewCount: 0
})

const loadStats = async () => {
  try {
    const data: any = await getDashboardStats()
    // 将 totalAmount 从字符串转为数字
    stats.value.userCount = data.userCount || 0
    stats.value.productCount = data.productCount || 0
    stats.value.orderCount = data.orderCount || 0
    stats.value.totalAmount = typeof data.totalAmount === 'string' ? parseFloat(data.totalAmount) : (data.totalAmount || 0)
    stats.value.pendingReviewCount = data.pendingReviewCount || 0
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

const goToReview = () => {
  router.push('/admin/products/review')
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stats-row {
  margin-top: 20px;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.product-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.amount-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 5px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pending-info {
  text-align: center;
  padding: 20px;
}

.pending-info p {
  margin-bottom: 20px;
  font-size: 16px;
  color: #606266;
}

.pending-info strong {
  color: #409eff;
  font-size: 20px;
}
</style>
