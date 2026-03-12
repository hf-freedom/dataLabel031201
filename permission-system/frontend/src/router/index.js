import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const componentMap = {
  'user': () => import('@/views/user/index.vue'),
  'role': () => import('@/views/role/index.vue'),
  'resource': () => import('@/views/resource/index.vue')
}

export const addDynamicRoutes = (menus) => {
  const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')

  menus.forEach(menu => {
    if (menu.children && menu.children.length > 0) {
      menu.children.forEach(child => {
        const routePath = `/${child.resourceCode}`
        if (!router.hasRoute(child.resourceCode)) {
          router.addRoute('Layout', {
            path: routePath,
            name: child.resourceCode,
            component: componentMap[child.resourceCode] || (() => import('@/views/dashboard/index.vue')),
            meta: { title: child.resourceName, icon: child.icon }
          })
        }
      })
    }
  })
}

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta && to.meta.public) {
    next()
    return
  }

  if (!userStore.token) {
    next('/login')
    return
  }

  if (userStore.menus.length > 0 && !router.hasRoute('user')) {
    addDynamicRoutes(userStore.menus)
    next({ ...to, replace: true })
    return
  }

  next()
})

export default router
