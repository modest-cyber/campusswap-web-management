import request from './http'

// 统计数据
export interface DashboardStats {
  userCount: number
  productCount: number
  orderCount: number
  totalAmount: number
  pendingReviewCount: number
}

// 用户管理
export interface AdminUser {
  id: number
  username: string
  nickname?: string
  realName?: string
  email?: string
  phone?: string
  department?: string
  role: number
  status: number
  createdAt: string
}

export interface UserQueryParams {
  page?: number
  size?: number
  keyword?: string
  department?: string
  status?: number
}

// 商品审核
export interface ReviewProduct {
  id: number
  name: string
  categoryId: number
  categoryName?: string
  price: number
  originalPrice?: number
  quality: string
  tradeMethod: string
  description: string
  images: string[]
  status: number
  userId: number
  username?: string
  userDepartment?: string
  createdAt: string
  reviewReason?: string
}

export interface ProductQueryParams {
  page?: number
  size?: number
  status?: number
  categoryId?: number
  keyword?: string
  startDate?: string
  endDate?: string
}

export interface ReviewRequest {
  productId: number
  status: number
  reason?: string
}

// 分类管理
export interface Category {
  id: number
  name: string
  parentId: number
  level: number
  sort: number
  status: number
  children?: Category[]
}

export interface CategoryForm {
  id?: number
  name: string
  parentId: number
  sort: number
  status: number
}

// 统计报表
export interface UserStats {
  date: string
  registerCount: number
  activeCount: number
}

export interface DepartmentStats {
  department: string
  count: number
}

export interface ProductStats {
  date: string
  publishCount: number
}

export interface CategoryDistribution {
  categoryName: string
  count: number
}

export interface TradeStats {
  date: string
  orderCount: number
  successCount: number
  totalAmount: number
}

export interface TradeMethodStats {
  method: string
  count: number
  percentage: number
}

export interface RankItem {
  userId: number
  username: string
  count: number
  amount?: number
}

export interface StatsQueryParams {
  startDate?: string
  endDate?: string
}

// API 接口

// 获取仪表盘统计数据
export function getDashboardStats() {
  return request<DashboardStats>({
    url: '/api/admin/stats/dashboard',
    method: 'get'
  })
}

// 用户管理
export function getUserList(params: UserQueryParams) {
  return request<{ list: AdminUser[]; total: number }>({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

export function getUserDetail(id: number) {
  return request<AdminUser>({
    url: `/api/admin/users/${id}`,
    method: 'get'
  })
}

export function updateUserStatus(id: number, status: number) {
  return request({
    url: `/api/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 商品审核
export function getPendingReviewProducts(params: ProductQueryParams) {
  return request<{ list: ReviewProduct[]; total: number }>({
    url: '/api/admin/products/pending',
    method: 'get',
    params
  })
}

export function getProductListForAdmin(params: ProductQueryParams) {
  return request<{ list: ReviewProduct[]; total: number }>({
    url: '/api/admin/products',
    method: 'get',
    params
  })
}

export function reviewProduct(data: ReviewRequest) {
  return request({
    url: '/api/admin/products/review',
    method: 'post',
    data
  })
}

export function batchReviewProducts(data: ReviewRequest[]) {
  return request({
    url: '/api/admin/products/review/batch',
    method: 'post',
    data
  })
}

export function deleteProduct(id: number) {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'delete'
  })
}

// 订单管理
export function getOrderListForAdmin(params: any) {
  return request<{ list: any[]; total: number }>({
    url: '/api/admin/orders',
    method: 'get',
    params
  })
}

export function getOrderDetailForAdmin(id: number) {
  return request<any>({
    url: `/api/admin/orders/${id}`,
    method: 'get'
  })
}

// 分类管理
export function getCategoryTree() {
  return request<Category[]>({
    url: '/api/admin/categories/tree',
    method: 'get'
  })
}

export function getCategoryList() {
  return request<Category[]>({
    url: '/api/admin/categories',
    method: 'get'
  })
}

export function createCategory(data: CategoryForm) {
  return request({
    url: '/api/admin/categories',
    method: 'post',
    data
  })
}

export function updateCategory(id: number, data: CategoryForm) {
  return request({
    url: `/api/admin/categories/${id}`,
    method: 'put',
    data
  })
}

export function deleteCategory(id: number) {
  return request({
    url: `/api/admin/categories/${id}`,
    method: 'delete'
  })
}

export function updateCategoryStatus(id: number, status: number) {
  return request({
    url: `/api/admin/categories/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 统计报表
export function getUserStats(params: StatsQueryParams) {
  return request<UserStats[]>({
    url: '/api/admin/stats/users',
    method: 'get',
    params
  })
}

export function getDepartmentStats() {
  return request<DepartmentStats[]>({
    url: '/api/admin/stats/departments',
    method: 'get'
  })
}

export function getProductStats(params: StatsQueryParams) {
  return request<ProductStats[]>({
    url: '/api/admin/stats/products',
    method: 'get',
    params
  })
}

export function getCategoryDistribution() {
  return request<CategoryDistribution[]>({
    url: '/api/admin/stats/categories',
    method: 'get'
  })
}

export function getTradeStats(params: StatsQueryParams) {
  return request<TradeStats[]>({
    url: '/api/admin/stats/trades',
    method: 'get',
    params
  })
}

export function getTradeMethodStats(params: StatsQueryParams) {
  return request<TradeMethodStats[]>({
    url: '/api/admin/stats/trade-methods',
    method: 'get',
    params
  })
}

export function getBuyerRank(params: StatsQueryParams) {
  return request<RankItem[]>({
    url: '/api/admin/stats/rank/buyers',
    method: 'get',
    params
  })
}

export function getSellerRank(params: StatsQueryParams) {
  return request<RankItem[]>({
    url: '/api/admin/stats/rank/sellers',
    method: 'get',
    params
  })
}

export function getHotProducts(params: StatsQueryParams) {
  return request<any[]>({
    url: '/api/admin/stats/hot-products',
    method: 'get',
    params
  })
}
