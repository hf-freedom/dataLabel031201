import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/user',
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/User.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('../views/Role.vue'),
        meta: { title: '角色管理', requiresAuth: true }
      },
      {
        path: 'resource',
        name: 'Resource',
        component: () => import('../views/Resource.vue'),
        meta: { title: '资源管理', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.userId) {
    next('/login')
  } else if (to.path === '/login' && userStore.userId) {
    next('/')
  } else {
    next()
  }
})

export default router
