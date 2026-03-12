<template>
  <div>
    <el-card>
      <div slot="header" style="display: flex; justify-content: space-between; align-items: center;">
        <span>用户管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增</el-button>
      </div>
      <el-form :inline="true" style="margin-bottom: 20px;">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" size="small"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="queryForm.nickname" placeholder="请输入昵称" size="small"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
          <el-button size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="nickname" label="昵称" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input type="password" v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      queryForm: {
        username: '',
        nickname: ''
      },
      tableData: [],
      dialogVisible: false,
      isEdit: false,
      form: {
        id: null,
        username: '',
        nickname: '',
        password: '',
        email: '',
        phone: '',
        status: 1
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑用户' : '新增用户'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      const res = await this.$http.get('/user/list', { params: this.queryForm })
      if (res.code === 200) {
        this.tableData = res.data
      }
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.queryForm = { username: '', nickname: '' }
      this.getList()
    },
    handleAdd() {
      this.isEdit = false
      this.form = { id: null, username: '', nickname: '', password: '', email: '', phone: '', status: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      await this.$confirm(`确定要删除用户 ${row.username} 吗?`, '提示', { type: 'warning' })
      const res = await this.$http.delete(`/user/${row.id}`)
      if (res.code === 200) {
        this.$message.success('删除成功')
        this.getList()
      }
    },
    async handleSubmit() {
      const res = await this.$http.post('/user/save', this.form)
      if (res.code === 200) {
        this.$message.success(this.isEdit ? '更新成功' : '新增成功')
        this.dialogVisible = false
        this.getList()
      }
    }
  }
}
</script>
