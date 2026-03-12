<template>
  <div>
    <el-card>
      <div slot="header" style="display: flex; justify-content: space-between; align-items: center;">
        <span>角色管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增</el-button>
      </div>
      <el-form :inline="true" style="margin-bottom: 20px;">
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.name" placeholder="请输入角色名称" size="small"></el-input>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryForm.code" placeholder="请输入角色编码" size="small"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
          <el-button size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="角色名称" width="120"></el-table-column>
        <el-table-column prop="code" label="角色编码" width="150"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="mini" @click="handleAssignResource(scope.row)">分配资源</el-button>
            <el-button type="warning" size="mini" @click="handleAssignUser(scope.row)">分配用户</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="form.code"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description"></el-input>
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

    <el-dialog title="分配资源" :visible.sync="resourceDialogVisible" width="600px">
      <el-tree
        ref="tree"
        :data="resourceTree"
        show-checkbox
        node-key="id"
        :default-expanded-keys="[1]"
        :default-checked-keys="checkedResourceIds"
        :props="{ label: 'name', children: 'children' }">
      </el-tree>
      <div slot="footer">
        <el-button @click="resourceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveResource">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分配用户" :visible.sync="userDialogVisible" width="600px">
      <el-table
        :data="userList"
        border
        style="width: 100%; margin-bottom: 20px;"
        @selection-change="handleUserSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="nickname" label="昵称" width="120"></el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveUser">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      queryForm: {
        name: '',
        code: ''
      },
      tableData: [],
      dialogVisible: false,
      resourceDialogVisible: false,
      userDialogVisible: false,
      isEdit: false,
      form: {
        id: null,
        name: '',
        code: '',
        description: '',
        status: 1
      },
      currentRoleId: null,
      resourceTree: [],
      checkedResourceIds: [],
      userList: [],
      selectedUserIds: []
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑角色' : '新增角色'
    }
  },
  created() {
    this.getList()
    this.getResourceTree()
    this.getUserList()
  },
  methods: {
    async getList() {
      const res = await this.$http.get('/role/list', { params: this.queryForm })
      if (res.code === 200) {
        this.tableData = res.data
      }
    },
    async getResourceTree() {
      const res = await this.$http.get('/resource/tree')
      if (res.code === 200) {
        this.resourceTree = res.data
      }
    },
    async getUserList() {
      const res = await this.$http.get('/user/list')
      if (res.code === 200) {
        this.userList = res.data
      }
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.queryForm = { name: '', code: '' }
      this.getList()
    },
    handleAdd() {
      this.isEdit = false
      this.form = { id: null, name: '', code: '', description: '', status: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      await this.$confirm(`确定要删除角色 ${row.name} 吗?`, '提示', { type: 'warning' })
      const res = await this.$http.delete(`/role/${row.id}`)
      if (res.code === 200) {
        this.$message.success('删除成功')
        this.getList()
      }
    },
    async handleSubmit() {
      const res = await this.$http.post('/role/save', this.form)
      if (res.code === 200) {
        this.$message.success(this.isEdit ? '更新成功' : '新增成功')
        this.dialogVisible = false
        this.getList()
      }
    },
    handleAssignResource(row) {
      this.currentRoleId = row.id
      this.checkedResourceIds = row.resourceIds || []
      this.resourceDialogVisible = true
      this.$nextTick(() => {
        this.$refs.tree.setCheckedKeys(this.checkedResourceIds)
      })
    },
    async handleSaveResource() {
      const checkedKeys = this.$refs.tree.getCheckedKeys()
      const res = await this.$http.post('/role/assignResources', { roleId: this.currentRoleId, resourceIds: checkedKeys })
      if (res.code === 200) {
        this.$message.success('分配成功')
        this.resourceDialogVisible = false
      }
    },
    handleAssignUser(row) {
      this.currentRoleId = row.id
      this.selectedUserIds = row.userIds || []
      this.userDialogVisible = true
    },
    handleUserSelectionChange(selection) {
      this.selectedUserIds = selection.map(item => item.id)
    },
    async handleSaveUser() {
      const res = await this.$http.post('/role/assignUsers', { roleId: this.currentRoleId, userIds: this.selectedUserIds })
      if (res.code === 200) {
        this.$message.success('分配成功')
        this.userDialogVisible = false
      }
    }
  }
}
</script>
