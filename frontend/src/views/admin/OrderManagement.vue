<template>
  <div class="order-management">
    <h2 class="page-title">订单管理</h2>
    
    <el-card>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="订单号">
          <el-input 
            v-model="queryParams.orderNo" 
            placeholder="请输入订单号"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="待支付" :value="1" />
            <el-option label="待发货" :value="2" />
            <el-option label="待收货" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="queryParams.startDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="queryParams.endDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table 
        :data="orderList" 
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="订单ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="商品信息" width="250">
          <template #default="{ row }">
            <div style="display: flex; gap: 10px; align-items: center">
              <el-image 
                v-if="row.productImage"
                :src="row.productImage" 
                fit="cover"
                style="width: 50px; height: 50px; border-radius: 4px"
              />
              <div>
                <div>{{ row.productName }}</div>
                <div style="color: #f56c6c; font-weight: 600">¥{{ row.amount }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="买家" width="120">
          <template #default="{ row }">
            {{ row.buyerName || row.buyerId }}
          </template>
        </el-table-column>
        <el-table-column label="卖家" width="120">
          <template #default="{ row }">
            {{ row.sellerName || row.sellerId }}
          </template>
        </el-table-column>
        <el-table-column label="交易方式" width="100">
          <template #default="{ row }">
            {{ row.tradeMethod }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="warning">待支付</el-tag>
            <el-tag v-else-if="row.status === 2" type="primary">待发货</el-tag>
            <el-tag v-else-if="row.status === 3" type="info">待收货</el-tag>
            <el-tag v-else-if="row.status === 4" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 5" type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
              查看详情
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
        @current-change="loadOrderList"
        @size-change="loadOrderList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="700px"
    >
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单ID">{{ currentOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag v-if="currentOrder.status === 1" type="warning">待支付</el-tag>
            <el-tag v-else-if="currentOrder.status === 2" type="primary">待发货</el-tag>
            <el-tag v-else-if="currentOrder.status === 3" type="info">待收货</el-tag>
            <el-tag v-else-if="currentOrder.status === 4" type="success">已完成</el-tag>
            <el-tag v-else-if="currentOrder.status === 5" type="danger">已取消</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="商品名称" :span="2">{{ currentOrder.productName }}</el-descriptions-item>
          <el-descriptions-item label="商品价格">¥{{ currentOrder.amount }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ currentOrder.quantity || 1 }}</el-descriptions-item>
          <el-descriptions-item label="买家">{{ currentOrder.buyerName || currentOrder.buyerId }}</el-descriptions-item>
          <el-descriptions-item label="卖家">{{ currentOrder.sellerName || currentOrder.sellerId }}</el-descriptions-item>
          <el-descriptions-item label="交易方式">{{ currentOrder.tradeMethod }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2" v-if="currentOrder.remark">
            {{ currentOrder.remark }}
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2" v-if="currentOrder.address">
            {{ currentOrder.address }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderListForAdmin, getOrderDetailForAdmin } from '../../api/admin'

const loading = ref(false)
const orderList = ref<any[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref<any>(null)

const queryParams = reactive({
  page: 1,
  size: 10,
  orderNo: '',
  status: undefined as number | undefined,
  startDate: '',
  endDate: ''
})

const loadOrderList = async () => {
  loading.value = true
  try {
    const res = await getOrderListForAdmin(queryParams)
    orderList.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  loadOrderList()
}

const handleReset = () => {
  queryParams.orderNo = ''
  queryParams.status = undefined
  queryParams.startDate = ''
  queryParams.endDate = ''
  queryParams.page = 1
  loadOrderList()
}

const handleViewDetail = async (row: any) => {
  try {
    currentOrder.value = await getOrderDetailForAdmin(row.id)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  }
}

onMounted(() => {
  loadOrderList()
})
</script>

<style scoped>
.order-management {
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
