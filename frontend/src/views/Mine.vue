<template>
  <div class="mine-page">
    <div class="mine-container">
      <!-- 左侧导航 -->
      <aside class="mine-sidebar">
        <div class="user-card">
          <div class="user-avatar">
            <img :src="authStore.userInfo?.avatar || 'https://via.placeholder.com/80x80?text=Avatar'" alt="头像" />
          </div>
          <div class="user-name">{{ authStore.userInfo?.nickname || authStore.userInfo?.username || '未登录' }}</div>
          <div class="user-department">{{ authStore.userInfo?.department || '未设置院系' }}</div>
        </div>
        
        <el-menu :default-active="activeMenu" @select="handleMenuSelect">
          <el-menu-item index="info">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="products">
            <el-icon><Goods /></el-icon>
            <span>我的发布</span>
          </el-menu-item>
          <el-menu-item index="favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><ShoppingBag /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="logout" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </aside>
      
      <!-- 右侧内容区 -->
      <main class="mine-content">
        <router-view v-if="isSubRoute" />
        <UserInfo v-else-if="activeMenu === 'info'" />
        <div v-else class="placeholder">
          <el-empty description="请选择菜单项" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { User, Goods, Star, ShoppingBag, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth'
import UserInfo from './user/UserInfo.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = ref('info')

// 是否为子路由
const isSubRoute = computed(() => {
  return route.path !== '/mine'
})

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  
  const routeMap: Record<string, string> = {
    info: '/mine',
    products: '/mine/products',
    favorites: '/mine/favorites',
    orders: '/mine/orders'
  }
  
  const targetRoute = routeMap[index]
  if (targetRoute && route.path !== targetRoute) {
    router.push(targetRoute)
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确认要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消操作
  }
}

// 根据路由更新激活菜单
onMounted(() => {
  const path = route.path
  if (path.includes('/mine/products')) {
    activeMenu.value = 'products'
  } else if (path.includes('/mine/favorites')) {
    activeMenu.value = 'favorites'
  } else if (path.includes('/mine/orders')) {
    activeMenu.value = 'orders'
  } else {
    activeMenu.value = 'info'
  }
})
</script>

<style scoped>
.mine-page {
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.mine-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  padding: 20px;
}

/* 左侧导航 */
.mine-sidebar {
  width: 240px;
  flex-shrink: 0;
}

.user-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.user-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 12px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #f0f0f0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.user-department {
  font-size: 14px;
  color: #909399;
}

.el-menu {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: none;
}

/* 右侧内容区 */
.mine-content {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  padding: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  min-height: 600px;
}

.placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  min-height: 400px;
}
</style>