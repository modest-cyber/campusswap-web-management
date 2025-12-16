import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import Home from '../views/Home.vue'
import ProductList from '../views/ProductList.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Mine from '../views/Mine.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import NotFound from '../views/NotFound.vue'
import { useAuthStore } from '../store/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: Home,
    meta: { requiresAuth: false }
  },
  {
    path: '/products',
    name: 'products',
    component: ProductList,
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/products/:id',
    name: 'productDetail',
    component: ProductDetail,
    meta: { requiresAuth: false }
  },
  {
    path: '/mine',
    name: 'mine',
    component: Mine,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'products',
        name: 'myProducts',
        component: () => import('../views/product/MyProducts.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'favorites',
        name: 'myFavorites',
        component: () => import('../views/product/FavoriteList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'myOrders',
        component: () => import('../views/order/OrderList.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/product/publish',
    name: 'productPublish',
    component: () => import('../views/product/ProductPublish.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/product/edit/:id',
    name: 'productEdit',
    component: () => import('../views/product/ProductPublish.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/confirm',
    name: 'orderConfirm',
    component: () => import('../views/order/OrderConfirm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/:id',
    name: 'orderDetail',
    component: () => import('../views/order/OrderDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/:id/review',
    name: 'orderReview',
    component: () => import('../views/order/OrderReview.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'notFound',
    component: NotFound,
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth ?? false
  const requiresAdmin = to.meta.requiresAdmin ?? false
  
  if (requiresAuth && !authStore.token) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (requiresAdmin && authStore.role !== 'admin') {
    ElMessage.warning('无权限访问')
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
