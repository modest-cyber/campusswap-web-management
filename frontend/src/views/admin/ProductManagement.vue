<template>
  <div class="product-management">
    <h2 class="page-title">商品管理</h2>
    
    <el-card>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键词">
          <el-input 
            v-model="queryParams.keyword" 
            placeholder="商品名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="在售" :value="1" />
            <el-option label="下架" :value="2" />
            <el-option label="已售出" :value="3" />
            <el-option label="拒绝" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryParams.categoryId" placeholder="全部" clearable>
            <el-option 
              v-for="cat in categories" 
              :key="cat.id" 
              :label="cat.name" 
              :value="cat.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table 
        :data="productList" 
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image 
              v-if="row.images && row.images.length > 0"
              :src="row.images[0]" 
              :preview-src-list="row.images"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="categoryName" label="分类" min-width="100" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">在售</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">下架</el-tag>
            <el-tag v-else-if="row.status === 3">已售出</el-tag>
            <el-tag v-else-if="row.status === 4" type="danger">拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布者" width="120">
          <template #default="{ row }">
            <div>{{ row.username }}</div>
            <div style="font-size: 12px; color: #909399">{{ row.userDepartment }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" min-width="160" />
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
            <el-button 
              link 
              type="warning" 
              size="small" 
              @click="handleOffline(row)"
              v-if="row.status === 1"
            >
              下架
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadProductList"
        @size-change="loadProductList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <el-dialog
      v-model="detailVisible"
      title="商品详情"
      width="800px"
    >
      <div v-if="currentProduct">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商品ID">{{ currentProduct.id }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ currentProduct.name }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentProduct.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="价格">
            <span style="color: #f56c6c; font-weight: 600">¥{{ currentProduct.price }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="原价">
            {{ currentProduct.originalPrice ? `¥${currentProduct.originalPrice}` : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="成色">{{ currentProduct.quality }}</el-descriptions-item>
          <el-descriptions-item label="交易方式">{{ currentProduct.tradeMethod }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="currentProduct.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="currentProduct.status === 1" type="success">在售</el-tag>
            <el-tag v-else-if="currentProduct.status === 2" type="info">下架</el-tag>
            <el-tag v-else-if="currentProduct.status === 3">已售出</el-tag>
            <el-tag v-else-if="currentProduct.status === 4" type="danger">拒绝</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布者">{{ currentProduct.username }}</el-descriptions-item>
          <el-descriptions-item label="发布者院系">{{ currentProduct.userDepartment }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ currentProduct.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="商品描述" :span="2">
            <div style="white-space: pre-wrap">{{ currentProduct.description }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="审核理由" :span="2" v-if="currentProduct.reviewReason">
            <div style="color: #f56c6c">{{ currentProduct.reviewReason }}</div>
          </el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-top: 20px">
          <h4>商品图片</h4>
          <div style="display: flex; gap: 10px; flex-wrap: wrap">
            <el-image 
              v-for="(img, index) in currentProduct.images" 
              :key="index"
              :src="img" 
              :preview-src-list="currentProduct.images"
              :initial-index="index"
              fit="cover"
              style="width: 150px; height: 150px; border-radius: 4px"
            />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getProductListForAdmin, 
  deleteProduct,
  reviewProduct,
  type ReviewProduct, 
  type ProductQueryParams 
} from '../../api/admin'
import { useDictStore } from '../../store/dict'

const dictStore = useDictStore()
const categories = ref(dictStore.categories)

const loading = ref(false)
const productList = ref<ReviewProduct[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentProduct = ref<ReviewProduct | null>(null)

const queryParams = reactive<ProductQueryParams>({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: undefined,
  status: undefined
})

const loadProductList = async () => {
  loading.value = true
  try {
    const res = await getProductListForAdmin(queryParams)
    productList.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  loadProductList()
}

const handleReset = () => {
  queryParams.keyword = ''
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.page = 1
  loadProductList()
}

const handleViewDetail = (row: ReviewProduct) => {
  currentProduct.value = row
  detailVisible.value = true
}

const handleOffline = async (row: ReviewProduct) => {
  try {
    await ElMessageBox.confirm(
      '确定要下架该商品吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await reviewProduct({ productId: row.id, status: 4, reason: '管理员下架' })
    ElMessage.success('下架成功')
    loadProductList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('下架失败')
    }
  }
}

const handleDelete = async (row: ReviewProduct) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该商品吗? 此操作不可恢复!',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadProductList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.product-management {
  padding: 0;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  margin-bottom: 20px;
}
</style>
