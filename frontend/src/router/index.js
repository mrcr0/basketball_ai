import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPassword.vue')
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/ResetPassword.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/plans',
    name: 'TrainingPlans',
    component: () => import('../views/TrainingPlans.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/actions',
    name: 'ActionLibrary',
    component: () => import('../views/ActionLibrary.vue')
  },
  {
    path: '/training',
    name: 'Training',
    component: () => import('../views/Training.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/tactics',
    name: 'Tactics',
    component: () => import('../views/Tactics.vue')
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('../views/Community.vue')
  },
  {
    path: '/ai-qa',
    name: 'AIQA',
    component: () => import('../views/AIQA.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminHome.vue')
  },
  {
    path: '/test-api',
    name: 'TestApi',
    component: () => import('../views/TestApi.vue')
  },
  {
    path: '/match',
    name: 'MatchSimulator',
    component: () => import('../views/MatchSimulator.vue')
  },
  {
    path: '/beginner',
    name: 'Beginner',
    redirect: '/knowledge'
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: () => import('../views/Knowledge.vue')
  },
  {
    path: '/news',
    name: 'News',
    component: () => import('../views/News.vue')
  },
  {
    path: '/topic/:id',
    name: 'TopicDetail',
    component: () => import('../views/TopicDetail.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录用户访问需认证的页面时重定向
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta?.requiresAuth)
  
  // 检查管理员路由权限
  if (to.path === '/admin') {
    if (!userStore.isLoggedIn) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    if (!userStore.isAdmin) {
      next({ path: '/', query: { redirect: to.fullPath } })
      return
    }
  }
  
  if (requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
