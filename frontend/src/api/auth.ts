import { request } from './http'

export interface LoginParams {
  account: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  email: string
  phone: string
  department?: string
}

export interface LoginResult {
  token: string
  user: {
    id: number
    username: string
    role: 'user' | 'admin'
    nickname?: string
    email?: string
    phone?: string
    department?: string
    avatar?: string
  }
}

export function login(params: LoginParams) {
  return request<LoginResult>({
    method: 'POST',
    url: '/api/user/login',
    data: params
  })
}

export function register(params: RegisterParams) {
  return request<void>({
    method: 'POST',
    url: '/api/user/register',
    data: params
  })
}

export function fetchUserInfo() {
  return request<LoginResult['user']>({
    method: 'GET',
    url: '/api/user/info'
  })
}

