import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserMenus, getUserPermissions } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const menus = ref([])
  const permissions = ref([])

  const token = computed(() => userInfo.value?.userId)
  const username = computed(() => userInfo.value?.username)

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const setMenus = (menuList) => {
    menus.value = menuList
    localStorage.setItem('menus', JSON.stringify(menuList))
  }

  const setPermissions = (permissionList) => {
    permissions.value = permissionList
    localStorage.setItem('permissions', JSON.stringify(permissionList))
  }

  const initFromStorage = () => {
    const storedUserInfo = localStorage.getItem('userInfo')
    const storedMenus = localStorage.getItem('menus')
    const storedPermissions = localStorage.getItem('permissions')

    if (storedUserInfo) {
      userInfo.value = JSON.parse(storedUserInfo)
    }
    if (storedMenus) {
      menus.value = JSON.parse(storedMenus)
    }
    if (storedPermissions) {
      permissions.value = JSON.parse(storedPermissions)
    }
  }

  const loginAction = async (loginData) => {
    const res = await login(loginData)
    if (res.code === 200) {
      setUserInfo(res.data)
      await loadUserResources()
      return true
    }
    return false
  }

  const loadUserResources = async () => {
    const [menusRes, permissionsRes] = await Promise.all([
      getUserMenus(),
      getUserPermissions()
    ])

    if (menusRes.code === 200) {
      setMenus(menusRes.data)
    }
    if (permissionsRes.code === 200) {
      setPermissions(permissionsRes.data)
    }
  }

  const logout = () => {
    userInfo.value = null
    menus.value = []
    permissions.value = []
    localStorage.removeItem('userInfo')
    localStorage.removeItem('menus')
    localStorage.removeItem('permissions')
  }

  const hasPermission = (permission) => {
    return permissions.value.includes(permission)
  }

  return {
    userInfo,
    menus,
    permissions,
    token,
    username,
    setUserInfo,
    setMenus,
    setPermissions,
    initFromStorage,
    loginAction,
    loadUserResources,
    logout,
    hasPermission
  }
})
