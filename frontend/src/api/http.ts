import axios from 'axios'
import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { useAuthStore } from '../store/auth'
import router from '../router'
import { ElMessage } from 'element-plus'

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  traceId?: string
}

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 10000
})

service.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    // 后端成功状态码是200
    if (res.code === 200 || res.code === 0) {
      return res.data as any
    }

    if (response.status === 401 || res.code === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push({ name: 'login' })
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(new Error('Unauthorized'))
    }

    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push({ name: 'login' })
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export function request<T = any>(config: AxiosRequestConfig): Promise<T> {
  return service.request<any, T>(config)
}

export default service

