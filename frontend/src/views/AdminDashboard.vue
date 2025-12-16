<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="currentRoute"
          class="admin-menu"
          router
        >
          <el-menu-item index="/admin">
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
import { useRoute } from 'vue-router'
import {
  DataLine,
  User,
  DocumentChecked,
  Goods,
  Document,
  Grid,
  TrendCharts
} from '@element-plus/icons-vue'

const route = useRoute()
const currentRoute = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  height: 100%;
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
}
</style>