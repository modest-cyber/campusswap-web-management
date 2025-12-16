<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel height="300px" class="home-carousel" indicator-position="outside">
      <el-carousel-item v-for="item in carouselItems" :key="item.id">
        <div class="carousel-item" :style="{ backgroundImage: `url(${item.image})` }">
          <div class="carousel-content">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    
    <!-- 快捷分类导航 -->
    <div class="category-nav">
      <h3 class="section-title">商品分类</h3>
      <div class="category-list">
        <div 
          v-for="category in categories" 
          :key="category.id" 
          class="category-item"
          @click="goToCategory(category.id)"
        >
          <div class="category-icon">{{ category.name.charAt(0) }}</div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="product-section">
      <!-- 最新商品 -->
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">最新商品</h3>
          <router-link to="/products?sort=latest" class="more-link">查看更多</router-link>
        </div>
        <div class="product-list">
          <ProductCard 
            v-for="product in latestProducts" 
            :key="product.id" 
            :product="product" 
            :show-favorite="true"
          />
        </div>
      </div>
      
      <!-- 热门商品 -->
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">热门商品</h3>
          <router-link to="/products?sort=hot" class="more-link">查看更多</router-link>
        </div>
        <div class="product-list">
          <ProductCard 
            v-for="product in hotProducts" 
            :key="product.id" 
            :product="product" 
            :show-favorite="true"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'
import { listProducts } from '../api/product'
import type { Category, Product } from '../api/product'

const router = useRouter()

// 轮播图数据
const carouselItems = ref([
  { id: 1, image: 'https://via.placeholder.com/1200x300?text=促销活动1', title: '开学季大促销', description: '全场商品8折起' },
  { id: 2, image: 'https://via.placeholder.com/1200x300?text=促销活动2', title: '毕业季清仓', description: '低价出售各类闲置物品' },
  { id: 3, image: 'https://via.placeholder.com/1200x300?text=促销活动3', title: '新用户专享', description: '注册即送10元优惠券' }
])

// 分类数据
const categories = ref<Category[]>([
  { id: 1, name: '数码产品' },
  { id: 2, name: '图书音像' },
  { id: 3, name: '生活用品' },
  { id: 4, name: '体育用品' },
  { id: 5, name: '服饰鞋包' },
  { id: 6, name: '其他' }
])

// 最新商品数据
const latestProducts = ref<Product[]>([])

// 热门商品数据
const hotProducts = ref<Product[]>([])

// 跳转到分类商品列表
const goToCategory = (categoryId: number) => {
  router.push({ 
    name: 'products', 
    query: { categoryId: categoryId.toString() }
  })
}

// 获取分类数据
const loadCategories = async () => {
  try {
    // 实际开发中调用API
    // const data = await fetchCategories()
    // categories.value = data
    // 目前使用模拟数据
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

// 获取商品数据
const loadProducts = async () => {
  try {
    // 获取最新商品（按创建时间排序，只显示在售状态）
    const latestRes = await listProducts({ 
      sortBy: 'create_time', 
      pageSize: 4,
      status: 1  // 只显示在售商品
    })
    latestProducts.value = latestRes.list || []
    
    // 获取热门商品（按浏览量排序）
    const hotRes = await listProducts({ 
      sortBy: 'view_count', 
      pageSize: 4,
      status: 1  // 只显示在售商品
    })
    hotProducts.value = hotRes.list || []
  } catch (error) {
    console.error('Failed to load products:', error)
    latestProducts.value = []
    hotProducts.value = []
  }
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<style scoped>
.home {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 轮播图样式 */
.home-carousel {
  margin-bottom: 24px;
}

.carousel-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-content {
  text-align: center;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.carousel-content h3 {
  font-size: 32px;
  margin-bottom: 12px;
  font-weight: bold;
}

.carousel-content p {
  font-size: 18px;
  margin: 0;
}

/* 分类导航样式 */
.category-nav {
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 16px;
  text-align: left;
}

.category-list {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  background-color: #ecf5ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.category-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.category-name {
  font-size: 14px;
  color: #303133;
}

/* 商品列表样式 */
.product-section {
  margin-top: 32px;
}

.section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.more-link {
  font-size: 14px;
  color: #409eff;
  text-decoration: none;
  transition: color 0.3s;
}

.more-link:hover {
  color: #66b1ff;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}
</style>

