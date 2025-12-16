<template>
  <div class="my-products-page">
    <div class="page-header">
      <h2>我的发布</h2>
      <el-button type="primary" @click="goToPublish">
        <el-icon><Plus /></el-icon>
        发布商品
      </el-button>
    </div>
    
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="handleFilterChange">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="0">待审核</el-radio-button>
        <el-radio-button value="1">在售</el-radio-button>
        <el-radio-button value="2">下架</el-radio-button>
        <el-radio-button value="3">已售出</el-radio-button>
        <el-radio-button value="-1">审核拒绝</el-radio-button>
      </el-radio-group>
    </div>
    
    <div class="product-list">
      <el-table :data="products" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />
        
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="product-info">
              <img :src="row.images?.[0] || 'https://via.placeholder.com/60x60?text=No+Image'" alt="商品图片" class="product-thumb" />
              <div class="product-detail">
                <div class="product-title">{{ row.title }}</div>
                <div class="product-meta">
                  <span>{{ row.categoryName }}</span>
                  <span class="separator">|</span>
                  <span>{{ row.condition }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="数据" width="160">
          <template #default="{ row }">
            <div class="stats">
              <span>浏览: {{ row.viewCount }}</span>
              <span>收藏: {{ row.favoriteCount }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="发布时间" width="160" />
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click="handleEdit(row)" v-if="row.status !== 3">
                编辑
              </el-button>
              <el-button 
                link 
                :type="row.status === 1 ? 'warning' : 'success'" 
                @click="handleToggleStatus(row)"
                v-if="row.status === 1 || row.status === 2"
              >
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
              <el-button link type="danger" @click="handleDelete(row)" v-if="row.status !== 3">
                删除
              </el-button>
              <el-button link type="info" @click="handleView(row)">
                查看
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
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
import { Plus } from '@element-plus/icons-vue'
import type { Product } from '../../api/product'

const router = useRouter()

// 数据
const products = ref<Product[]>([])
const loading = ref(false)
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 模拟数据
const mockProducts: Product[] = [
  {
    id: 1,
    title: 'iPhone 13 Pro',
    price: 5999,
    originalPrice: 7999,
    condition: '99成新',
    images: ['https://via.placeholder.com/60x60?text=iPhone13Pro'],
    viewCount: 120,
    favoriteCount: 20,
    categoryName: '数码产品',
    status: 1,
    createdAt: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    title: '全新未拆封 AirPods Pro',
    price: 1299,
    originalPrice: 1999,
    condition: '全新',
    images: ['https://via.placeholder.com/60x60?text=AirPodsPro'],
    viewCount: 80,
    favoriteCount: 15,
    categoryName: '数码产品',
    status: 0,
    createdAt: '2024-01-14 14:20:00'
  },
  {
    id: 3,
    title: '《JavaScript高级程序设计》第4版',
    price: 80,
    originalPrice: 128,
    condition: '9成新',
    images: ['https://via.placeholder.com/60x60?text=JavaScriptBook'],
    viewCount: 50,
    favoriteCount: 8,
    categoryName: '图书音像',
    status: 2,
    createdAt: '2024-01-13 09:15:00'
  }
]

// 获取状态类型
const getStatusType = (status?: number) => {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: '',
    '-1': 'danger'
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
    '-1': '审核拒绝'
  }
  return textMap[status ?? 1]
}

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    // 实际开发中调用 API
    // const result = await getMyProducts({
    //   status: statusFilter.value ? Number(statusFilter.value) : undefined,
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
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选变化
const handleFilterChange = () => {
  currentPage.value = 1
  fetchProducts()
}

// 分页大小变化
const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
}

// 当前页变化
const handleCurrentPageChange = (page: number) => {
  currentPage.value = page
  fetchProducts()
}

// 跳转到发布页面
const goToPublish = () => {
  router.push('/product/publish')
}

// 编辑
const handleEdit = (product: Product) => {
  router.push(`/product/edit/${product.id}`)
}

// 上下架
const handleToggleStatus = async (product: Product) => {
  const action = product.status === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确认要${action}该商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 实际开发中调用 API
    // await updateProductStatus(product.id, product.status === 1 ? 2 : 1)
    
    ElMessage.success(`${action}成功`)
    fetchProducts()
  } catch (error) {
    // 用户取消操作
  }
}

// 删除
const handleDelete = async (product: Product) => {
  try {
    await ElMessageBox.confirm('确认要删除该商品吗？此操作不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 实际开发中调用 API
    // await deleteProduct(product.id)
    
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error) {
    // 用户取消操作
  }
}

// 查看
const handleView = (product: Product) => {
  router.push(`/products/${product.id}`)
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.my-products-page {
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

.filter-bar {
  margin-bottom: 20px;
}

.product-list {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.product-detail {
  flex: 1;
  min-width: 0;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  font-size: 12px;
  color: #909399;
}

.separator {
  margin: 0 8px;
}

.price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.stats {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #606266;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
