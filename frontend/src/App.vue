<template>
  <div id="app">
    <router-view v-if="isLoginPage" />
    <el-container v-else style="height: 100vh;">
      <el-header style="background: #409EFF; color: white; display: flex; align-items: center; justify-content: space-between; padding: 0 20px;">
        <div style="font-size: 20px; font-weight: bold;">权限管理系统</div>
        <div>
          <span>{{ userInfo.nickname }}</span>
          <el-button type="text" style="color: white; margin-left: 20px;" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" style="background: #f4f4f4;">
          <el-menu
            :default-active="$route.path"
            router
            style="border-right: none;"
          >
            <MenuItem v-for="item in menuList" :key="item.path" :item="item" />
          </el-menu>
        </el-aside>
        <el-main style="padding: 20px;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import MenuItem from './components/MenuItem.vue'

export default {
  components: { MenuItem },
  computed: {
    isLoginPage() {
      return this.$route.path === '/login'
    },
    userInfo() {
      return this.$store.state.userInfo
    },
    menuList() {
      return this.$store.state.menuList
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      this.$store.commit('clearUserInfo')
      this.$router.push('/login')
    }
  },
  created() {
    const token = localStorage.getItem('token')
    if (token && !this.isLoginPage) {
      this.$store.dispatch('getUserInfo', token)
      this.$store.dispatch('getMenuList', token)
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
}
</style>
