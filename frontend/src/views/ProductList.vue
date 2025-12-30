<template>
  <div class="product-list-page">
    <div class="page-header">
      <h2>商品列表</h2>
    </div>
    
    <div class="product-content">
      <!-- 左侧过滤区域 -->
      <aside class="filter-sidebar">
        <div class="filter-section">
          <h3 class="filter-title">分类</h3>
          <div class="filter-items">
            <el-checkbox 
              v-for="category in categories" 
              :key="category.id" 
              v-model="selectedCategories" 
              :label="category.id"
              @change="handleFilterChange"
            >
              {{ category.name }}
            </el-checkbox>
          </div>
        </div>
        
        <div class="filter-section">
          <h3 class="filter-title">成色</h3>
          <div class="filter-items">
            <el-checkbox 
              v-for="condition in conditions" 
              :key="condition.value" 
              v-model="selectedConditions" 
              :label="condition.value"
              @change="handleFilterChange"
            >
              {{ condition.label }}
            </el-checkbox>
          </div>
        </div>
        
        <div class="filter-section">
          <h3 class="filter-title">价格区间</h3>
          <div class="price-range">
            <el-input-number 
              v-model="priceRange.min" 
              :min="0" 
              :step="100" 
              placeholder="最低价"
              @change="handleFilterChange"
              style="width: 100%"
            />
            <span class="price-separator">-</span>
            <el-input-number 
              v-model="priceRange.max" 
              :min="0" 
              :step="100" 
              placeholder="最高价"
              @change="handleFilterChange"
              style="width: 100%"
            />
          </div>
        </div>
        
        <el-button type="primary" @click="resetFilters" class="reset-btn">重置筛选</el-button>
      </aside>
      
      <!-- 右侧商品列表区域 -->
      <main class="product-main">
        <!-- 排序和筛选结果 -->
        <div class="product-header">
          <div class="result-info">
            共找到 <span class="result-count">{{ totalProducts }}</span> 件商品
          </div>
          <div class="sort-options">
            <span class="sort-label">排序：</span>
            <el-radio-group v-model="sortBy" @change="handleSortChange">
              <el-radio-button label="latest">最新</el-radio-button>
              <el-radio-button label="priceAsc">价格升序</el-radio-button>
              <el-radio-button label="priceDesc">价格降序</el-radio-button>
              <el-radio-button label="hot">热度</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        
        <!-- 商品列表 -->
        <div class="product-grid">
          <ProductCard 
            v-for="product in products" 
            :key="product.id" 
            :product="product" 
            :show-favorite="true"
          />
        </div>
        
        <!-- 空状态 -->
        <div v-if="products.length === 0" class="empty-state">
          <el-empty description="暂无商品" />
        </div>
        
        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 36]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalProducts"
            @size-change="handlePageSizeChange"
            @current-change="handleCurrentPageChange"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'
import type { Product, Category } from '../api/product'
import { listProducts } from '../api/product'

const route = useRoute()

// 商品列表数据
const products = ref<Product[]>([])
const totalProducts = ref(0)

// 分页参数
const currentPage = ref(1)
const pageSize = ref(12)

// 排序参数
const sortBy = ref<'latest' | 'priceAsc' | 'priceDesc' | 'hot'>('latest')

// 筛选参数
const selectedCategories = ref<number[]>([])
const selectedConditions = ref<string[]>([])
const priceRange = ref({ min: undefined, max: undefined })

// 分类数据
const categories = ref<Category[]>([
  { id: 1, name: '电子产品' },
  { id: 2, name: '图书教材' },
  { id: 3, name: '生活用品' },
  { id: 4, name: '服装配饰' },
  { id: 5, name: '运动用品' },
  { id: 6, name: '其他' }
])

// 成色枚举
const conditions = [
  { label: '全新', value: '全新' },
  { label: '99成新', value: '99成新' },
  { label: '9成新', value: '9成新' },
  { label: '8成新', value: '8成新' },
  { label: '7成新及以下', value: '7成新及以下' }
]

// 搜索关键词
const keyword = ref('')

// 获取商品列表
const fetchProducts = async () => {
  try {
    // 构建排序参数
    let sortBy_param: string | undefined
    let sortOrder: 'asc' | 'desc' | undefined
    
    if (sortBy.value === 'latest') {
      sortBy_param = 'create_time'
      sortOrder = 'desc'
    } else if (sortBy.value === 'priceAsc') {
      sortBy_param = 'price'
      sortOrder = 'asc'
    } else if (sortBy.value === 'priceDesc') {
      sortBy_param = 'price'
      sortOrder = 'desc'
    } else if (sortBy.value === 'hot') {
      sortBy_param = 'view_count'
      sortOrder = 'desc'
    }
    
    // 调用API获取商品列表，只显示在售状态的商品
    const result = await listProducts({
      keyword: keyword.value,
      categoryIds: selectedCategories.value.length > 0 ? selectedCategories.value : undefined,
      quality: selectedConditions.value.length > 0 ? selectedConditions.value : undefined,
      minPrice: priceRange.value.min,
      maxPrice: priceRange.value.max,
      status: 1,  // 只显示在售商品
      sortBy: sortBy_param,
      sortOrder: sortOrder,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    products.value = result.list || []
    totalProducts.value = result.total || 0
  } catch (error) {
    console.error('Failed to fetch products:', error)
    products.value = []
    totalProducts.value = 0
  }
}

// 处理筛选条件变化
const handleFilterChange = () => {
  currentPage.value = 1 // 筛选条件变化时重置到第一页
  fetchProducts()
}

// 处理排序变化
const handleSortChange = () => {
  fetchProducts()
}

// 处理分页大小变化
const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
}

// 处理当前页变化
const handleCurrentPageChange = (page: number) => {
  currentPage.value = page
  fetchProducts()
}

// 重置筛选条件
const resetFilters = () => {
  selectedCategories.value = []
  selectedConditions.value = []
  priceRange.value = { min: undefined, max: undefined }
  sortBy.value = 'latest'
  currentPage.value = 1
  fetchProducts()
}

// 监听路由参数变化
watch(
  () => route.query,
  (newQuery) => {
    // 从路由参数中获取搜索关键词
    if (newQuery.keyword) {
      keyword.value = newQuery.keyword as string
    }
    // 从路由参数中获取分类ID
    if (newQuery.categoryId) {
      selectedCategories.value = [Number(newQuery.categoryId)]
    }
    // 从路由参数中获取排序方式
    if (newQuery.sort) {
      sortBy.value = newQuery.sort as any
    }
    fetchProducts()
  },
  { immediate: true }
)

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.product-list-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.product-content {
  display: flex;
  gap: 20px;
}

/* 左侧过滤区域 */
.filter-sidebar {
  width: 240px;
  background-color: #fafafa;
  border-radius: 8px;
  padding: 16px;
  position: sticky;
  top: 80px;
  align-self: flex-start;
  height: fit-content;
}

.filter-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.filter-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.filter-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 12px;
}

.filter-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-separator {
  color: #909399;
}

.reset-btn {
  width: 100%;
  margin-top: 16px;
}

/* 右侧商品列表区域 */
.product-main {
  flex: 1;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.result-info {
  font-size: 14px;
  color: #606266;
}

.result-count {
  color: #409eff;
  font-weight: bold;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 14px;
  color: #606266;
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>