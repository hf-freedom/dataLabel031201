<template>
  <el-container class="main-layout">
    <el-aside width="200px">
      <div class="logo">
        <h3>权限管理</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/role">
          <el-icon><Avatar /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
        <el-menu-item index="/resource">
          <el-icon><Menu /></el-icon>
          <span>资源管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-content">
          <span>{{ currentRoute?.meta?.title || '' }}</span>
          <div class="user-info">
            <span>{{ userStore.nickname }}</span>
            <el-button type="text" @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)

const handleLogout = () => {
  userStore.clearUserInfo()
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo h3 {
  margin: 0;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-main {
  background-color: #f0f2f5;
}
</style>
