import { useUserStore } from '@/store/user'

export const permission = {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()

    if (value && !userStore.hasPermission(value)) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

export default permission
