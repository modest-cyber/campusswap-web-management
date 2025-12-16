<script setup lang="ts">
import { ElCard, ElTag, ElButton } from 'element-plus'
import type { Product } from '../api/product'

const props = defineProps<{
  product: Product
  showFavorite?: boolean
}>()

const emits = defineEmits<{
  (e: 'favorite', id: number, nextValue: boolean): void
}>()

const handleFavorite = () => {
  emits('favorite', props.product.id, !props.product.isFavorite)
}
</script>

<template>
  <el-card shadow="hover" class="product-card" @click="$router.push({ name: 'productDetail', params: { id: product.id } })">
    <div class="cover">
      <img :src="product.images?.[0] || 'https://via.placeholder.com/200x150?text=Image'" alt="cover" />
      <el-tag v-if="product.condition" size="small" class="condition">{{ product.condition }}</el-tag>
    </div>
    <div class="info">
      <div class="title" :title="product.title">{{ product.title }}</div>
      <div class="price">
        <span class="sale">¥{{ product.price }}</span>
        <span class="origin" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
      </div>
      <div class="meta">
        <span>{{ product.categoryName || '未分类' }}</span>
        <span>{{ product.createdAt || '' }}</span>
      </div>
      <div class="meta">
        <span>浏览 {{ product.viewCount ?? 0 }}</span>
        <span>收藏 {{ product.favoriteCount ?? 0 }}</span>
      </div>
      <div class="actions" @click.stop>
        <el-button v-if="showFavorite" size="small" text type="primary" @click="handleFavorite">
          {{ product.isFavorite ? '已收藏' : '收藏' }}
        </el-button>
        <el-button size="small" text type="primary">详情</el-button>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.product-card {
  display: flex;
  gap: 12px;
  cursor: pointer;
}
.cover {
  position: relative;
  width: 200px;
  height: 150px;
  overflow: hidden;
  border-radius: 8px;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.condition {
  position: absolute;
  top: 8px;
  left: 8px;
}
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.title {
  font-weight: 600;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.price {
  display: flex;
  align-items: center;
  gap: 8px;
}
.sale {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 700;
}
.origin {
  color: #909399;
  text-decoration: line-through;
}
.meta {
  color: #909399;
  display: flex;
  gap: 12px;
  font-size: 12px;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>

