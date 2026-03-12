import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserMenus } from '../api/resource'

export const useUserStore = defineStore('user', () => {
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const menus = ref([])

  function setUserInfo(user) {
    userId.value = user.id
    username.value = user.username
    nickname.value = user.nickname
    localStorage.setItem('userId', user.id)
    localStorage.setItem('username', user.username)
    localStorage.setItem('nickname', user.nickname)
  }

  function clearUserInfo() {
    userId.value = ''
    username.value = ''
    nickname.value = ''
    menus.value = []
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
  }

  async function fetchMenus() {
    if (userId.value) {
      try {
        const res = await getUserMenus(userId.value)
        menus.value = res.data || []
      } catch (e) {
        console.error('获取菜单失败', e)
      }
    }
  }

  return {
    userId,
    username,
    nickname,
    menus,
    setUserInfo,
    clearUserInfo,
    fetchMenus
  }
})
