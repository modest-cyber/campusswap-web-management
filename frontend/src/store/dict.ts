import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface OptionItem {
  label: string
  value: string | number
}

export const useDictStore = defineStore('dict', () => {
  const categories = ref<OptionItem[]>([])
  const conditions = ref<OptionItem[]>([
    { label: '全新', value: 'new' },
    { label: '九成新', value: '90' },
    { label: '八成新', value: '80' }
  ])
  const transactionTypes = ref<OptionItem[]>([
    { label: '面交', value: 0 },
    { label: '邮寄', value: 1 },
    { label: '均可', value: 2 }
  ])

  function setCategories(list: OptionItem[]) {
    categories.value = list
  }

  return {
    categories,
    conditions,
    transactionTypes,
    setCategories
  }
})

