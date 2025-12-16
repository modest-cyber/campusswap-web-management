import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export type UserRole = 'user' | 'admin'

export interface UserInfo {
  id?: number
  username?: string
  nickname?: string
  email?: string
  phone?: string
  department?: string
  role?: UserRole
  avatar?: string
}

const TOKEN_KEY = 'campus_swap_token'
const USER_KEY = 'campus_swap_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const userInfo = ref<UserInfo | null>(
    localStorage.getItem(USER_KEY) ? JSON.parse(localStorage.getItem(USER_KEY) as string) : null
  )

  const role = computed<UserRole>(() => userInfo.value?.role ?? 'user')

  function setToken(value: string | null) {
    token.value = value
    if (value) {
      localStorage.setItem(TOKEN_KEY, value)
    } else {
      localStorage.removeItem(TOKEN_KEY)
    }
  }

  function setUser(value: UserInfo | null) {
    userInfo.value = value
    if (value) {
      localStorage.setItem(USER_KEY, JSON.stringify(value))
    } else {
      localStorage.removeItem(USER_KEY)
    }
  }

  function logout() {
    setToken(null)
    setUser(null)
  }

  return {
    token,
    userInfo,
    role,
    setToken,
    setUser,
    logout
  }
})

