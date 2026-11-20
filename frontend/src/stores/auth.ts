import { defineStore } from 'pinia'

const TOKEN_KEY = 'aicops_token'

interface LoginResult {
  accessToken: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    username: localStorage.getItem('aicops_username') || ''
  }),
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  actions: {
    setAuth(result: LoginResult) {
      this.token = result.accessToken
      this.username = result.username
      localStorage.setItem(TOKEN_KEY, result.accessToken)
      localStorage.setItem('aicops_username', result.username)
    },
    logout() {
      this.token = ''
      this.username = ''
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('aicops_username')
    }
  }
})
