import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/system/user',
    name: 'UserManage',
    component: () => import('../views/system/User.vue')
  },
  {
    path: '/system/role',
    name: 'RoleManage',
    component: () => import('../views/system/Role.vue')
  },
  {
    path: '/system/resource',
    name: 'ResourceManage',
    component: () => import('../views/system/Resource.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
