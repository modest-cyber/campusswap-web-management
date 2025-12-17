<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px">
        <!-- 校徽和校名 -->
        <div class="school-header">
          <img src="../img/校徽.png" alt="西安石油大学" class="school-logo" />
          <span class="school-name">西安石油大学</span>
        </div>
        <el-menu
          :default-active="currentRoute"
          class="admin-menu"
          router
        >
          <el-menu-item index="/admin/admin">
            <el-icon><DataLine /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/products/review">
            <el-icon><DocumentChecked /></el-icon>
            <span>商品审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <el-icon><Grid /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/stats">
            <el-icon><TrendCharts /></el-icon>
            <span>统计报表</span>
          </el-menu-item>
          <el-menu-item @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  DataLine,
  User,
  DocumentChecked,
  Goods,
  Document,
  Grid,
  TrendCharts,
  SwitchButton
} from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const currentRoute = computed(() => route.path)

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确认退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    authStore.logout()
    ElMessage.success('退出成功')
    router.replace('/login')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

.el-container {
  height: 100%;
  width: 100%;
}

.el-aside {
  background-color: #304156;
  height: 100%;
}

.school-header {
  display: flex;
  align-items: center;
  padding: 20px 10px;
  background-color: #304156;
  border-bottom: 1px solid #263445;
}

.school-logo {
  width: 40px;
  height: 40px;
  margin-right: 10px;
  border-radius: 50%;
}

.school-name {
  color: #ffffff;
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
}

.admin-menu {
  border: none;
  background-color: #304156;
  height: 100%;
}

.admin-menu .el-menu-item {
  color: #bfcbd9;
}

.admin-menu .el-menu-item:hover {
  background-color: #263445 !important;
  color: #ffffff;
}

.admin-menu .el-menu-item.is-active {
  background-color: #409eff !important;
  color: #ffffff;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  width: 100%;
  overflow-x: auto;
}

/* 强制所有子页面内容宽度为100% */
.el-main :deep(.el-card) {
  width: 100% !important;
  max-width: none !important;
}

.el-main :deep(.user-management),
.el-main :deep(.product-review),
.el-main :deep(.product-management),
.el-main :deep(.order-management),
.el-main :deep(.category-management),
.el-main :deep(.statistics) {
  width: 100% !important;
  max-width: none !important;
}
</style>