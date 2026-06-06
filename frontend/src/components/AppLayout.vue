<template>
  <div class="app-layout">
    <header class="app-header">
      <div class="header-inner container">
        <router-link to="/" class="header-logo" @click="closeMobileMenu">
          <span class="logo-icon">🏀</span>
          <span class="logo-text">AI篮球训练平台</span>
        </router-link>

        <nav class="header-nav" :class="{ 'nav-open': mobileMenuOpen }">
          <div class="nav-backdrop" @click="closeMobileMenu"></div>
          <div class="nav-links">
            <router-link to="/" class="nav-link" exact-active-class="active" @click="closeMobileMenu">
              <span class="nav-icon">🏠</span>
              <span>首页</span>
            </router-link>
            <router-link to="/actions" class="nav-link" active-class="active" @click="closeMobileMenu">
              <span class="nav-icon">🎯</span>
              <span>动作库</span>
            </router-link>
            <router-link to="/tactics" class="nav-link" active-class="active" @click="closeMobileMenu">
              <span class="nav-icon">📚</span>
              <span>战术学习</span>
            </router-link>
            <router-link to="/community" class="nav-link" active-class="active" @click="closeMobileMenu">
              <span class="nav-icon">💬</span>
              <span>社区</span>
            </router-link>
            <router-link to="/knowledge" class="nav-link" active-class="active" @click="closeMobileMenu">
              <span class="nav-icon">🧠</span>
              <span>新手入门与篮球知识库</span>
            </router-link>
            <router-link to="/news" class="nav-link" active-class="active" @click="closeMobileMenu">
              <span class="nav-icon">🏆</span>
              <span>赛事资讯</span>
            </router-link>
            <router-link to="/ai-qa" class="nav-link" active-class="active" @click="closeMobileMenu">
              <span class="nav-icon">🤖</span>
              <span>AI问答</span>
            </router-link>
            <template v-if="userStore.isLoggedIn">
              <router-link to="/plans" class="nav-link" active-class="active" @click="closeMobileMenu">
                <span class="nav-icon">📋</span>
                <span>训练计划</span>
              </router-link>
              <router-link to="/training" class="nav-link" active-class="active" @click="closeMobileMenu">
                <span class="nav-icon">💪</span>
                <span>训练打卡</span>
              </router-link>
              <router-link to="/profile" class="nav-link" active-class="active" @click="closeMobileMenu">
                <span class="nav-icon">👤</span>
                <span>个人中心</span>
              </router-link>
            </template>
          </div>

          <div class="nav-actions">
            <template v-if="userStore.isLoggedIn">
              <router-link v-if="userStore.isAdmin" to="/admin" class="nav-link admin-link" active-class="active">
                <span class="nav-icon">⚙️</span>
                <span>后台管理</span>
              </router-link>
              <div class="user-badge">
                <div class="avatar-wrapper">
                  <img v-if="userStore.user?.avatarUrl" :src="userStore.user.avatarUrl" class="user-avatar-img" />
                  <span v-else class="user-avatar">👤</span>
                </div>
                <span class="user-name">{{ userStore.user?.nickname || '用户' }}</span>
                <span v-if="userStore.isAdmin" class="admin-badge">管理员</span>
              </div>
              <button @click="handleLogout" class="btn-logout">
                <span>退出登录</span>
              </button>
            </template>
            <template v-else>
              <router-link to="/login" class="btn-login" @click="closeMobileMenu">
                <span>登录</span>
              </router-link>
              <router-link to="/register" class="btn-register" @click="closeMobileMenu">
                <span>注册</span>
              </router-link>
            </template>
          </div>
        </nav>

        <button class="mobile-menu-toggle" @click="toggleMobileMenu" :aria-label="mobileMenuOpen ? '关闭菜单' : '打开菜单'">
          <span class="hamburger-line" :class="{ open: mobileMenuOpen }"></span>
          <span class="hamburger-line" :class="{ open: mobileMenuOpen }"></span>
          <span class="hamburger-line" :class="{ open: mobileMenuOpen }"></span>
        </button>
      </div>
    </header>

    <main class="app-main">
      <slot />
    </main>

    <footer class="app-footer">
      <div class="footer-inner container">
        <div class="footer-grid">
          <div class="footer-brand">
            <div class="footer-logo">
              <span class="logo-icon">🏀</span>
              <span class="logo-text">AI篮球训练平台</span>
            </div>
            <p class="footer-desc">科学训练，智能提升。基于AI大模型的个性化篮球训练方案，助你快速提升球技。</p>
          </div>
          <div class="footer-links">
            <h4>快速链接</h4>
            <router-link to="/actions">动作库</router-link>
            <router-link to="/tactics">战术学习</router-link>
            <router-link to="/community">社区</router-link>
            <router-link to="/ai-qa">AI问答</router-link>
          </div>
          <div class="footer-links">
            <h4>用户服务</h4>
            <router-link to="/plans">训练计划</router-link>
            <router-link to="/training">训练打卡</router-link>
            <router-link to="/profile">个人中心</router-link>
          </div>
          <div class="footer-contact">
            <h4>联系我们</h4>
            <p>📧 contact@ai-basketball.com</p>
            <p>📱 400-888-8888</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 AI篮球训练平台 - 科学训练，智能提升</p>
        </div>
      </div>
    </footer>

    <button class="back-to-top" :class="{ visible: showBackToTop }" @click="scrollToTop" aria-label="回到顶部">
      <span>↑</span>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '../store'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const mobileMenuOpen = ref(false)
const showBackToTop = ref(false)
let scrollTimeout = null

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
  document.body.style.overflow = mobileMenuOpen.value ? 'hidden' : ''
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
  document.body.style.overflow = ''
}

const handleLogout = () => {
  userStore.logout()
  closeMobileMenu()
  router.push('/login')
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleScroll = () => {
  showBackToTop.value = window.scrollY > 400
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  if (scrollTimeout) clearTimeout(scrollTimeout)
  document.body.style.overflow = ''
})
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  background: rgba(10, 14, 39, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  transition: all var(--transition-base);
  height: var(--header-height);
  transform: translateY(0);
}



.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  gap: var(--space-8);
}

.header-logo {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  text-decoration: none;
  flex-shrink: 0;
}

.header-logo .logo-icon {
  font-size: 1.5rem;
}

.header-logo .logo-text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #fff;
  letter-spacing: var(--letter-spacing-wide);
}

.header-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
  gap: var(--space-4);
}

.nav-backdrop {
  display: none;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  color: var(--color-neutral-400);
  text-decoration: none;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
  white-space: nowrap;
  position: relative;
}

.nav-link:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
}

.nav-link.active {
  color: var(--color-accent-400);
  background: rgba(74, 222, 128, 0.1);
}

.nav-link .nav-icon {
  font-size: var(--font-size-base);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-shrink: 0;
}

.user-badge {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-3);
  background: rgba(255, 255, 255, 0.06);
  border-radius: var(--radius-full);
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  font-size: var(--font-size-lg);
}

.user-avatar-img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  color: var(--color-neutral-300);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.admin-badge {
  padding: 2px 8px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  border-radius: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.admin-link {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(217, 119, 6, 0.15));
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.admin-link:hover {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.25), rgba(217, 119, 6, 0.25));
}

.admin-link.active {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.3), rgba(217, 119, 6, 0.3));
  border-color: rgba(245, 158, 11, 0.5);
}

.btn-login,
.btn-logout {
  padding: var(--space-2) var(--space-5);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  transition: all var(--transition-base);
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
}

.btn-login {
  background: transparent;
  border: 1.5px solid var(--color-accent-400);
  color: var(--color-accent-400);
}

.btn-login:hover {
  background: var(--color-accent-400);
  color: var(--color-dark-900);
  box-shadow: var(--shadow-glow-accent);
}

.btn-register {
  padding: var(--space-2) var(--space-5);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  background: var(--gradient-accent);
  color: var(--color-dark-900);
  text-decoration: none;
  transition: all var(--transition-base);
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
}

.btn-register:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-glow-accent);
}

.btn-logout {
  background: rgba(239, 68, 68, 0.15);
  color: #fca5a5;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.btn-logout:hover {
  background: rgba(239, 68, 68, 0.25);
  color: #fecaca;
}

.mobile-menu-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: var(--space-2);
  background: none;
  border: none;
  cursor: pointer;
  z-index: 10;
}

.hamburger-line {
  display: block;
  width: 22px;
  height: 2px;
  background: #fff;
  border-radius: var(--radius-full);
  transition: all var(--transition-base);
}

.hamburger-line.open:nth-child(1) {
  transform: rotate(45deg) translate(5px, 5px);
}

.hamburger-line.open:nth-child(2) {
  opacity: 0;
}

.hamburger-line.open:nth-child(3) {
  transform: rotate(-45deg) translate(5px, -5px);
}

.app-main {
  flex: 1;
}

.back-to-top {
  position: fixed;
  bottom: var(--space-8);
  right: var(--space-8);
  width: 44px;
  height: 44px;
  border-radius: var(--radius-full);
  background: var(--color-accent-500);
  color: #fff;
  font-size: var(--font-size-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-fixed);
  opacity: 0;
  transform: translateY(16px);
  pointer-events: none;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-lg);
  cursor: pointer;
  border: none;
}

.back-to-top.visible {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

.back-to-top:hover {
  background: var(--color-accent-600);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-accent);
}

.app-footer {
  background: var(--color-dark-900);
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  padding: var(--space-16) 0 var(--space-8);
}

.footer-inner {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: var(--space-8);
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-3);
}

.footer-logo .logo-icon {
  font-size: 1.5rem;
}

.footer-logo .logo-text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #fff;
}

.footer-desc {
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
  line-height: var(--line-height-relaxed);
  max-width: 320px;
}

.footer-links h4,
.footer-contact h4 {
  color: #fff;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--space-4);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.footer-links a {
  display: block;
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
  padding: var(--space-1) 0;
  transition: color var(--transition-fast);
}

.footer-links a:hover {
  color: var(--color-accent-400);
}

.footer-contact p {
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
  padding: var(--space-1) 0;
}

.footer-bottom {
  padding-top: var(--space-6);
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  text-align: center;
}

.footer-bottom p {
  color: var(--color-neutral-500);
  font-size: var(--font-size-xs);
}

@media (max-width: 1024px) {
  .footer-grid {
    grid-template-columns: 1fr 1fr;
    gap: var(--space-8) var(--space-4);
  }

  .nav-links {
    gap: 0;
  }

  .nav-link {
    padding: var(--space-2) var(--space-2);
  }
}

@media (max-width: 768px) {
  .app-header {
    height: var(--header-height-mobile);
  }

  .mobile-menu-toggle {
    display: flex;
  }

  .nav-backdrop {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: -1;
    opacity: 0;
    transition: opacity var(--transition-base);
  }

  .header-nav {
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    width: 300px;
    max-width: 85vw;
    background: var(--color-dark-800);
    flex-direction: column;
    justify-content: flex-start;
    padding: calc(var(--header-height-mobile) + var(--space-4)) var(--space-6) var(--space-8);
    gap: var(--space-6);
    transform: translateX(100%);
    transition: transform var(--transition-base);
    z-index: 5;
    overflow-y: auto;
  }

  .header-nav.nav-open {
    transform: translateX(0);
  }

  .header-nav.nav-open .nav-backdrop {
    opacity: 1;
    z-index: -1;
    left: -100vw;
    right: 300px;
  }

  .nav-links {
    flex-direction: column;
    width: 100%;
    gap: var(--space-1);
  }

  .nav-link {
    width: 100%;
    padding: var(--space-3) var(--space-4);
    font-size: var(--font-size-base);
    border-radius: var(--radius-xl);
  }

  .admin-link {
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.2), rgba(217, 119, 6, 0.2));
    border: 1px solid rgba(245, 158, 11, 0.4);
    margin-bottom: var(--space-2);
  }

  .nav-actions {
    flex-direction: column;
    width: 100%;
    gap: var(--space-3);
    padding-top: var(--space-4);
    border-top: 1px solid rgba(255, 255, 255, 0.08);
  }

  .btn-login,
  .btn-register,
  .btn-logout {
    width: 100%;
    justify-content: center;
    padding: var(--space-3);
  }

  .user-badge {
    width: 100%;
    justify-content: center;
    padding: var(--space-3);
    flex-wrap: wrap;
  }

  .admin-badge {
    margin-top: var(--space-1);
  }

  .back-to-top {
    bottom: var(--space-6);
    right: var(--space-4);
    width: 40px;
    height: 40px;
  }
}

@media (max-width: 480px) {
  .footer-grid {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .header-logo .logo-text {
    font-size: var(--font-size-base);
  }
}
</style>