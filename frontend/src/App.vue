<template>
  <div id="app">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="logo">校园二手交易平台</router-link>
          <nav class="nav">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/products" class="nav-item">商品分类</router-link>
          </nav>
        </div>
        
        <!-- 全局搜索 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
        
        <div class="header-right">
          <el-button 
            v-if="authStore.token" 
            type="success" 
            @click="goToPublish"
            class="publish-btn"
          >
            <el-icon><Plus /></el-icon>
            发布商品
          </el-button>
          
          <router-link v-if="authStore.token" to="/mine" class="user-btn">
            <el-icon><User /></el-icon>
            我的
          </router-link>
          
          <router-link v-else to="/login" class="user-btn">
            <el-icon><User /></el-icon>
            登录
          </router-link>
        </div>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="main">
      <div class="container">
        <router-view />
      </div>
    </main>
    
    <!-- 底部 -->
    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 校园二手交易平台</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, User, Plus } from '@element-plus/icons-vue'
import { useAuthStore } from './store/auth'

const router = useRouter()
const authStore = useAuthStore()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      name: 'products',
      query: { keyword: searchKeyword.value.trim() }
    })
  }
}

const goToPublish = () => {
  router.push('/product/publish')
}
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #67c23a;
  text-decoration: none;
}

.nav {
  display: flex;
  margin-left: 40px;
}

.nav-item {
  margin-right: 24px;
  color: #303133;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #67c23a;
}

.search-box {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-input {
  width: 100%;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.publish-btn {
  /* Element Plus button styles */
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #303133;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s;
}

.user-btn:hover {
  color: #67c23a;
}

.main {
  flex: 1;
  padding: 20px 0;
  background-color: #f5f7fa;
}

.footer {
  background-color: #fff;
  padding: 20px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
  border-top: 1px solid #ebeef5;
}
</style>
