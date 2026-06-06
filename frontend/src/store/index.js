import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    userId: (state) => state.user?.id || null,
    role: (state) => state.user?.role || 'user',
    isAdmin: (state) => state.user?.role === 'admin',
    isSuperAdmin: (state) => state.user?.role === 'super_admin'
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUser(user) {
      this.user = user
    },
    updateAvatar(avatarUrl) {
      if (this.user) {
        this.user = { ...this.user, avatarUrl }
      }
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
    },
    getAuthorization() {
      return `Bearer ${this.token}`
    }
  }
})
