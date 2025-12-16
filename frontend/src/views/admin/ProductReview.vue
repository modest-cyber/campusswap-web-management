<template>
  <div class="product-review">
    <h2 class="page-title">商品审核</h2>
    
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
      
      <div style="margin-bottom: 10px">
        <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
        <el-button 
          type="success" 
          size="small"
          :disabled="selectedIds.length === 0"
          @click="handleBatchReview(1)"
          style="margin-left: 10px"
        >
          批量通过
        </el-button>
        <el-button 
          type="danger" 
          size="small"
          :disabled="selectedIds.length === 0"
          @click="handleBatchReview(4)"
        >
          批量拒绝
        </el-button>
      </div>
      
      <el-table 
        :data="productList" 
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
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
        <el-table-column prop="quality" label="成色" min-width="80" />
        <el-table-column prop="tradeMethod" label="交易方式" min-width="100" />
        <el-table-column label="发布者" width="120">
          <template #default="{ row }">
            <div>{{ row.username }}</div>
            <div style="font-size: 12px; color: #909399">{{ row.userDepartment }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" min-width="160" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button link type="success" size="small" @click="handleReview(row, 1)">
              通过
            </el-button>
            <el-button link type="danger" size="small" @click="handleReview(row, 4)">
              拒绝
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
          <el-descriptions-item label="发布者">{{ currentProduct.username }}</el-descriptions-item>
          <el-descriptions-item label="发布者院系">{{ currentProduct.userDepartment }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ currentProduct.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="商品描述" :span="2">
            <div style="white-space: pre-wrap">{{ currentProduct.description }}</div>
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
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="success" @click="handleReview(currentProduct!, 1)">
          通过
        </el-button>
        <el-button type="danger" @click="handleReview(currentProduct!, 4)">
          拒绝
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="rejectVisible"
      title="拒绝原因"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因" required>
          <el-input 
            v-model="rejectForm.reason" 
            type="textarea"
            :rows="4"
            placeholder="请填写拒绝原因"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getPendingReviewProducts, 
  reviewProduct, 
  batchReviewProducts,
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
const rejectVisible = ref(false)
const currentProduct = ref<ReviewProduct | null>(null)
const selectAll = ref(false)
const selectedIds = ref<number[]>([])

const queryParams = reactive<ProductQueryParams>({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: undefined,
  status: 1
})

const rejectForm = reactive({
  reason: '',
  productId: 0,
  isBatch: false
})

const loadProductList = async () => {
  loading.value = true
  try {
    const res = await getPendingReviewProducts(queryParams)
    productList.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('加载待审核商品列表失败')
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
  queryParams.page = 1
  loadProductList()
}

const handleViewDetail = (row: ReviewProduct) => {
  currentProduct.value = row
  detailVisible.value = true
}

const handleSelectionChange = (selection: ReviewProduct[]) => {
  selectedIds.value = selection.map(item => item.id)
  selectAll.value = selection.length === productList.value.length && productList.value.length > 0
}

const handleSelectAll = (val: boolean) => {
  // Element Plus 的表格会自动处理全选
}

const handleReview = async (row: ReviewProduct, status: number) => {
  if (status === 4) {
    rejectForm.productId = row.id
    rejectForm.reason = ''
    rejectForm.isBatch = false
    rejectVisible.value = true
    return
  }
  
  try {
    await ElMessageBox.confirm(
      '确定要通过该商品的审核吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await reviewProduct({ productId: row.id, status })
    ElMessage.success('审核成功')
    detailVisible.value = false
    loadProductList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败')
    }
  }
}

const handleBatchReview = async (status: number) => {
  if (status === 4) {
    rejectForm.productId = 0
    rejectForm.reason = ''
    rejectForm.isBatch = true
    rejectVisible.value = true
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量通过选中的 ${selectedIds.value.length} 件商品吗?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const requests = selectedIds.value.map(id => ({ productId: id, status }))
    await batchReviewProducts(requests)
    ElMessage.success('批量审核成功')
    loadProductList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量审核失败')
    }
  }
}

const confirmReject = async () => {
  if (!rejectForm.reason.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  
  try {
    if (rejectForm.isBatch) {
      const requests = selectedIds.value.map(id => ({ 
        productId: id, 
        status: 4, 
        reason: rejectForm.reason 
      }))
      await batchReviewProducts(requests)
      ElMessage.success('批量拒绝成功')
    } else {
      await reviewProduct({ 
        productId: rejectForm.productId, 
        status: 4, 
        reason: rejectForm.reason 
      })
      ElMessage.success('拒绝成功')
      detailVisible.value = false
    }
    
    rejectVisible.value = false
    loadProductList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.product-review {
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
