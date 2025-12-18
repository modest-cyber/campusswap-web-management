<template>
  <div class="statistics">
    <h2 class="page-title">统计报表</h2>
    
    <el-card style="margin-bottom: 20px">
      <el-form :inline="true" :model="dateRange">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange.dates"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAllStats">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="用户统计" name="user">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-card>
              <template #header>
                <span>用户注册趋势</span>
              </template>
              <div class="chart-container" ref="userTrendChart"></div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>院系分布</span>
              </template>
              <div class="chart-container" ref="departmentChart"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>院系用户数据</span>
              </template>
              <el-table :data="departmentStats" style="width: 100%" max-height="400">
                <el-table-column prop="department" label="院系" />
                <el-table-column prop="count" label="用户数" />
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
      
      <el-tab-pane label="商品统计" name="product">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-card>
              <template #header>
                <span>商品发布趋势</span>
              </template>
              <div class="chart-container" ref="productTrendChart"></div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>分类分布</span>
              </template>
              <div class="chart-container" ref="categoryChart"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>热门商品榜单</span>
              </template>
              <el-table :data="hotProducts" style="width: 100%" max-height="400">
                <el-table-column type="index" label="排名" width="60" />
                <el-table-column prop="name" label="商品名称" />
                <el-table-column prop="viewCount" label="浏览数" width="100" />
                <el-table-column prop="favoriteCount" label="收藏数" width="100" />
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
      
      <el-tab-pane label="交易统计" name="trade">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-card>
              <template #header>
                <span>交易趋势</span>
              </template>
              <div class="chart-container" ref="tradeTrendChart"></div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>买家排行榜</span>
              </template>
              <el-table :data="buyerRank" style="width: 100%" max-height="400">
                <el-table-column type="index" label="排名" width="60" />
                <el-table-column prop="username" label="用户名" />
                <el-table-column prop="count" label="购买次数" />
                <el-table-column label="消费金额">
                  <template #default="{ row }">
                    ¥{{ row.amount }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>卖家排行榜</span>
              </template>
              <el-table :data="sellerRank" style="width: 100%" max-height="400">
                <el-table-column type="index" label="排名" width="60" />
                <el-table-column prop="username" label="用户名" />
                <el-table-column prop="count" label="销售次数" />
                <el-table-column label="销售金额">
                  <template #default="{ row }">
                    ¥{{ row.amount }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  getUserStats,
  getDepartmentStats,
  getProductStats,
  getCategoryDistribution,
  getTradeStats,
  getBuyerRank,
  getSellerRank,
  getHotProducts,
  type UserStats,
  type DepartmentStats,
  type ProductStats,
  type CategoryDistribution,
  type TradeStats,
  type RankItem
} from '../../api/admin'

const activeTab = ref('user')
const userTrendChart = ref<HTMLElement>()
const departmentChart = ref<HTMLElement>()
const productTrendChart = ref<HTMLElement>()
const categoryChart = ref<HTMLElement>()
const tradeTrendChart = ref<HTMLElement>()

const dateRange = reactive({
  dates: [] as string[],
  startDate: '',
  endDate: ''
})

const departmentStats = ref<DepartmentStats[]>([])
const buyerRank = ref<RankItem[]>([])
const sellerRank = ref<RankItem[]>([])
const hotProducts = ref<any[]>([])

let userChart: echarts.ECharts | null = null
let deptChart: echarts.ECharts | null = null
let productChart: echarts.ECharts | null = null
let catChart: echarts.ECharts | null = null
let tradeChart: echarts.ECharts | null = null

const handleDateChange = (dates: string[]) => {
  if (dates && dates.length === 2) {
    dateRange.startDate = dates[0]
    dateRange.endDate = dates[1]
  } else {
    dateRange.startDate = ''
    dateRange.endDate = ''
  }
}

const handleReset = () => {
  dateRange.dates = []
  dateRange.startDate = ''
  dateRange.endDate = ''
  loadAllStats()
}

const loadUserStats = async () => {
  try {
    const data = await getUserStats({
      startDate: dateRange.startDate,
      endDate: dateRange.endDate
    })
    
    await nextTick()
    if (userTrendChart.value) {
      if (!userChart) {
        userChart = echarts.init(userTrendChart.value)
      }
      
      userChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['注册数', '活跃数'] },
        xAxis: { type: 'category', data: data.map((d: UserStats) => d.date) },
        yAxis: { type: 'value' },
        series: [
          { name: '注册数', type: 'line', data: data.map((d: UserStats) => d.registerCount) },
          { name: '活跃数', type: 'line', data: data.map((d: UserStats) => d.activeCount) }
        ]
      })
    }
    
    const deptData = await getDepartmentStats()
    departmentStats.value = deptData
    
    await nextTick()
    if (departmentChart.value) {
      if (!deptChart) {
        deptChart = echarts.init(departmentChart.value)
      }
      
      const chartData = deptData.map((d: DepartmentStats) => ({ name: d.department, value: d.count }))
      
      deptChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: chartData.map(d => d.name)
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          data: chartData,
          label: {
            show: true,
            formatter: '{b}: {d}%'
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }
  } catch (error) {
    ElMessage.error('加载用户统计失败')
  }
}

const loadProductStats = async () => {
  try {
    const data = await getProductStats({
      startDate: dateRange.startDate,
      endDate: dateRange.endDate
    })
    
    await nextTick()
    if (productTrendChart.value) {
      if (!productChart) {
        productChart = echarts.init(productTrendChart.value)
      }
      
      productChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.map((d: ProductStats) => d.date) },
        yAxis: { type: 'value' },
        series: [{ name: '发布数', type: 'bar', data: data.map((d: ProductStats) => d.publishCount) }]
      })
    }
    
    const catData = await getCategoryDistribution()
    
    await nextTick()
    if (categoryChart.value) {
      if (!catChart) {
        catChart = echarts.init(categoryChart.value)
      }
      
      const chartData = catData.map((d: CategoryDistribution) => ({ name: d.categoryName, value: d.count }))
      
      catChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: chartData.map(d => d.name)
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          data: chartData,
          label: {
            show: true,
            formatter: '{b}: {d}%'
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }
    
    hotProducts.value = await getHotProducts({
      startDate: dateRange.startDate,
      endDate: dateRange.endDate
    })
  } catch (error) {
    ElMessage.error('加载商品统计失败')
  }
}

const loadTradeStats = async () => {
  try {
    const data = await getTradeStats({
      startDate: dateRange.startDate,
      endDate: dateRange.endDate
    })
    
    await nextTick()
    if (tradeTrendChart.value) {
      if (!tradeChart) {
        tradeChart = echarts.init(tradeTrendChart.value)
      }
      
      tradeChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['订单数', '成功数', '成交额'] },
        xAxis: { type: 'category', data: data.map((d: TradeStats) => d.date) },
        yAxis: { type: 'value' },
        series: [
          { name: '订单数', type: 'line', data: data.map((d: TradeStats) => d.orderCount) },
          { name: '成功数', type: 'line', data: data.map((d: TradeStats) => d.successCount) },
          { name: '成交额', type: 'bar', data: data.map((d: TradeStats) => d.totalAmount) }
        ]
      })
    }
    
    buyerRank.value = await getBuyerRank({
      startDate: dateRange.startDate,
      endDate: dateRange.endDate
    })
    
    sellerRank.value = await getSellerRank({
      startDate: dateRange.startDate,
      endDate: dateRange.endDate
    })
  } catch (error) {
    ElMessage.error('加载交易统计失败')
  }
}

const loadAllStats = async () => {
  await Promise.all([
    loadUserStats(),
    loadProductStats(),
    loadTradeStats()
  ])
}

const handleTabChange = () => {
  // 切换标签页后重新调整图表大小
  setTimeout(() => {
    userChart?.resize()
    deptChart?.resize()
    productChart?.resize()
    catChart?.resize()
    tradeChart?.resize()
  }, 100)
}

onMounted(() => {
  loadAllStats()
  
  window.addEventListener('resize', () => {
    userChart?.resize()
    deptChart?.resize()
    productChart?.resize()
    catChart?.resize()
    tradeChart?.resize()
  })
})
</script>

<style scoped>
.statistics {
  padding: 0;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 400px;
}
</style>
