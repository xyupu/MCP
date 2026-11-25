import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import MainLayout from '@/layout/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      component: MainLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue')
        },
        {
          path: 'assistant',
          name: 'assistant',
          component: () => import('@/views/AssistantView.vue')
        },
        {
          path: 'alerts',
          name: 'alerts',
          component: () => import('@/views/AlertsView.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && auth.isAuthenticated) {
    return { name: 'dashboard' }
  }
  return true
})

export default router
