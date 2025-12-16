<template>
  <div class="favorite-list-page">
    <div class="page-header">
      <h2>我的收藏</h2>
      <div class="header-info">
        共收藏 <span class="count">{{ total }}</span> 件商品
      </div>
    </div>
    
    <div class="favorite-list" v-loading="loading">
      <div class="product-grid">
        <div v-for="product in products" :key="product.id" class="product-item">
          <div class="product-card">
            <div class="product-image" @click="goToDetail(product.id)">
              <img :src="product.images?.[0] || 'https://via.placeholder.com/300x200?text=No+Image'" alt="商品图片" />
              <div class="product-status" v-if="product.status !== 1">
                <el-tag :type="getStatusType(product.status)">
                  {{ getStatusText(product.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="product-content">
              <div class="product-title" @click="goToDetail(product.id)">
                {{ product.title }}
              </div>
              
              <div class="product-meta">
                <span>{{ product.categoryName }}</span>
                <span class="separator">·</span>
                <span>{{ product.condition }}</span>
              </div>
              
              <div class="product-footer">
                <div class="product-price">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                </div>
                
                <div class="product-actions">
                  <el-button 
                    type="danger" 
                    size="small" 
                    :icon="Delete"
                    @click="handleUnfavorite(product)"
                  >
                    取消收藏
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="products.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无收藏商品">
          <el-button type="primary" @click="goToProducts">去逛逛</el-button>
        </el-empty>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 36]"
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
import { Delete } from '@element-plus/icons-vue'
import type { Product } from '../../api/product'
import { toggleFavorite } from '../../api/product'

const router = useRouter()

// 数据
const products = ref<Product[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 模拟数据
const mockProducts: Product[] = [
  {
    id: 1,
    title: 'iPhone 13 Pro',
    price: 5999,
    originalPrice: 7999,
    condition: '99成新',
    images: ['https://via.placeholder.com/300x200?text=iPhone13Pro'],
    viewCount: 120,
    favoriteCount: 20,
    categoryName: '数码产品',
    status: 1,
    createdAt: '2024-01-15',
    isFavorite: true
  },
  {
    id: 2,
    title: '全新未拆封 AirPods Pro',
    price: 1299,
    originalPrice: 1999,
    condition: '全新',
    images: ['https://via.placeholder.com/300x200?text=AirPodsPro'],
    viewCount: 80,
    favoriteCount: 15,
    categoryName: '数码产品',
    status: 1,
    createdAt: '2024-01-14',
    isFavorite: true
  },
  {
    id: 3,
    title: '《JavaScript高级程序设计》第4版',
    price: 80,
    originalPrice: 128,
    condition: '9成新',
    images: ['https://via.placeholder.com/300x200?text=JavaScriptBook'],
    viewCount: 50,
    favoriteCount: 8,
    categoryName: '图书音像',
    status: 1,
    createdAt: '2024-01-13',
    isFavorite: true
  },
  {
    id: 4,
    title: 'MacBook Pro 2022款',
    price: 12999,
    originalPrice: 16999,
    condition: '98成新',
    images: ['https://via.placeholder.com/300x200?text=MacBookPro'],
    viewCount: 200,
    favoriteCount: 35,
    categoryName: '数码产品',
    status: 3,
    createdAt: '2024-01-11',
    isFavorite: true
  }
]

// 获取状态类型
const getStatusType = (status?: number) => {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: '',
  }
  return typeMap[status ?? 1]
}

// 获取状态文本
const getStatusText = (status?: number) => {
  const textMap: Record<number, string> = {
    0: '待审核',
    1: '在售',
    2: '下架',
    3: '已售出',
  }
  return textMap[status ?? 1]
}

// 获取收藏列表
const fetchFavorites = async () => {
  loading.value = true
  try {
    // 实际开发中调用 API
    // const result = await getFavoriteList({
    //   pageNum: currentPage.value,
    //   pageSize: pageSize.value
    // })
    // products.value = result.list
    // total.value = result.total
    
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 300))
    products.value = mockProducts
    total.value = mockProducts.length
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 分页大小变化
const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchFavorites()
}

// 当前页变化
const handleCurrentPageChange = (page: number) => {
  currentPage.value = page
  fetchFavorites()
}

// 跳转到商品详情
const goToDetail = (id: number) => {
  router.push(`/products/${id}`)
}

// 跳转到商品列表
const goToProducts = () => {
  router.push('/products')
}

// 取消收藏
const handleUnfavorite = async (product: Product) => {
  try {
    await ElMessageBox.confirm('确认要取消收藏该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await toggleFavorite(product.id, false)
    ElMessage.success('取消收藏成功')
    
    // 从列表中移除
    products.value = products.value.filter(p => p.id !== product.id)
    total.value -= 1
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorite-list-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.header-info {
  font-size: 14px;
  color: #606266;
}

.count {
  color: #409eff;
  font-weight: bold;
}

.favorite-list {
  min-height: 400px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.product-item {
  transition: transform 0.3s;
}

.product-item:hover {
  transform: translateY(-4px);
}

.product-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-image:hover img {
  transform: scale(1.05);
}

.product-status {
  position: absolute;
  top: 8px;
  right: 8px;
}

.product-content {
  padding: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  cursor: pointer;
  transition: color 0.3s;
  line-height: 1.4;
  height: 2.8em;
}

.product-title:hover {
  color: #409eff;
}

.product-meta {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.separator {
  margin: 0 4px;
}

.product-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 12px;
  color: #909399;
  text-decoration: line-through;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
