<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: #f0f2f5;">
    <el-card style="width: 400px; padding: 20px;">
      <h2 style="text-align: center; margin-bottom: 30px;">权限管理系统</h2>
      <el-form :model="form" label-width="80px" @keyup.enter.native="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%;" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        username: 'admin',
        password: '123456'
      }
    }
  },
  methods: {
    async handleLogin() {
      try {
        const res = await this.$http.post('/auth/login', this.form)
        if (res.code === 200) {
          localStorage.setItem('token', res.data.token)
          this.$store.commit('setUserInfo', res.data.user)
          await this.$store.dispatch('getMenuList', res.data.token)
          this.$router.push('/home')
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        this.$message.error('登录失败')
      }
    }
  }
}
</script>
