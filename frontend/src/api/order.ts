import { request } from './http'

export interface OrderAddress {
  receiverName: string
  receiverPhone: string
  receiverAddress: string
}

export interface CreateOrderRequest {
  productId: number
  quantity: number
  transactionType: number
  receiverName?: string
  receiverPhone?: string
  receiverAddress?: string
  remark?: string
}

export interface Order {
  id: number
  orderNo: string
  productId: number
  productTitle: string
  productImage?: string
  categoryName?: string
  condition?: string
  buyerId: number
  buyerName: string
  sellerId: number
  sellerName: string
  quantity: number
  totalAmount: number
  transactionType: number
  status: number
  address?: OrderAddress
  trackingNumber?: string
  remark?: string
  hasReviewed?: boolean
  createdAt: string
  timeline?: Array<{ time: string; content: string }>
}

export interface OrderQuery {
  viewType?: 'buyer' | 'seller'
  status?: number
  orderNo?: string
  startDate?: Date
  endDate?: Date
  pageNum?: number
  pageSize?: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface ReviewRequest {
  orderId: number
  rating: number
  comment: string
}

/**
 * 创建订单
 */
export function createOrder(data: CreateOrderRequest) {
  return request<Order>({
    method: 'POST',
    url: '/api/order',
    data
  })
}

/**
 * 获取订单列表
 */
export function getOrderList(params: OrderQuery) {
  return request<PageResult<Order>>({
    method: 'GET',
    url: '/api/order/list',
    params
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id: number | string) {
  return request<Order>({
    method: 'GET',
    url: `/api/order/${id}`
  })
}

/**
 * 取消订单
 */
export function cancelOrder(id: number | string) {
  return request<void>({
    method: 'DELETE',
    url: `/api/order/${id}`
  })
}

/**
 * 确认收货
 */
export function confirmReceive(id: number | string) {
  return request<void>({
    method: 'PUT',
    url: `/api/order/${id}/confirm`
  })
}

/**
 * 发货/确认面交
 */
export function deliverOrder(id: number | string) {
  return request<void>({
    method: 'PUT',
    url: `/api/order/${id}/deliver`
  })
}

/**
 * 创建评价
 */
export function createReview(data: ReviewRequest) {
  return request<void>({
    method: 'POST',
    url: '/api/review',
    data
  })
}
