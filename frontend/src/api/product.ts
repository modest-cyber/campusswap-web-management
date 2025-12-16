import { request } from './http'

export interface Category {
  id: number
  name: string
  parentId?: number
  status?: number
}

export interface Product {
  id: number
  title: string
  description?: string
  price: number
  originalPrice?: number
  images?: string[]
  status?: number
  condition?: string
  transactionType?: number
  viewCount?: number
  favoriteCount?: number
  categoryId?: number
  categoryName?: string
  sellerName?: string
  sellerDepartment?: string
  sellerAvatar?: string
  createdAt?: string
  isFavorite?: boolean
}

export interface ProductQuery {
  keyword?: string
  categoryId?: number
  minPrice?: number
  maxPrice?: number
  status?: number
  sort?: 'latest' | 'priceAsc' | 'priceDesc' | 'hot'
  sortBy?: string
  pageNum?: number
  pageSize?: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export function fetchCategories() {
  return request<Category[]>({
    method: 'GET',
    url: '/api/category/list'
  })
}

export function listProducts(params: ProductQuery) {
  return request<PageResult<Product>>({
    method: 'GET',
    url: '/api/product/list',
    params
  })
}

export function getProductDetail(id: number | string) {
  return request<Product>({
    method: 'GET',
    url: `/api/product/${id}`
  })
}

export function toggleFavorite(id: number | string, favorite: boolean) {
  if (favorite) {
    return request<void>({
      method: 'POST',
      url: `/api/product/${id}/favorite`
    })
  }
  return request<void>({
    method: 'DELETE',
    url: `/api/product/${id}/favorite`
  })
}

export interface PublishProductParams {
  title: string
  description: string
  price: number
  originalPrice?: number
  images: string[]
  categoryId: number
  condition: string
  transactionType: number
}

export function publishProduct(data: PublishProductParams) {
  return request<number>({
    method: 'POST',
    url: '/api/product',
    data
  })
}

export function updateProduct(id: number | string, data: PublishProductParams) {
  return request<void>({
    method: 'PUT',
    url: `/api/product/${id}`,
    data
  })
}

export function deleteProduct(id: number | string) {
  return request<void>({
    method: 'DELETE',
    url: `/api/product/${id}`
  })
}

export function getMyProducts(pageNum = 1, pageSize = 10, status?: number) {
  return request<PageResult<Product>>({
    method: 'GET',
    url: '/api/product/my',
    params: { pageNum, pageSize, status }
  })
}

export function updateProductStatus(id: number | string, status: number) {
  return request<void>({
    method: 'PUT',
    url: `/api/product/${id}/status`,
    params: { status }
  })
}

export function getFavoriteList(pageNum = 1, pageSize = 10) {
  return request<PageResult<Product>>({
    method: 'GET',
    url: '/api/product/favorite',
    params: { pageNum, pageSize }
  })
}
