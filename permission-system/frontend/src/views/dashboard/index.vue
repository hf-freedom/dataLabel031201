<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <span>欢迎使用权限管理系统</span>
      </template>
      <div class="welcome-content">
        <h3>你好，{{ userStore.username }}</h3>
        <p>这是基于 Vue 3 + Spring Boot 的权限管理系统</p>
        <div class="feature-list">
          <h4>系统功能：</h4>
          <ul>
            <li>用户管理 - 用户的增删改查</li>
            <li>角色管理 - 角色的增删改查，角色与资源、用户绑定</li>
            <li>资源管理 - 菜单、页面、按钮、API 的增删改查，启用/停用</li>
            <li>接口权限 - 访问后端接口时判断当前用户是否拥有该权限</li>
          </ul>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-content {
  padding: 20px 0;
}

.welcome-content h3 {
  margin-bottom: 20px;
  color: #303133;
}

.welcome-content p {
  color: #606266;
  margin-bottom: 30px;
}

.feature-list h4 {
  margin-bottom: 15px;
  color: #303133;
}

.feature-list ul {
  list-style: none;
  padding: 0;
}

.feature-list li {
  padding: 10px 0;
  color: #606266;
  border-bottom: 1px solid #ebeef5;
}

.feature-list li:last-child {
  border-bottom: none;
}

.feature-list li::before {
  content: '•';
  color: #409EFF;
  font-weight: bold;
  margin-right: 10px;
}
</style>
