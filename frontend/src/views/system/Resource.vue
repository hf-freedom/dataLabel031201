<template>
  <div>
    <el-card>
      <div slot="header" style="display: flex; justify-content: space-between; align-items: center;">
        <span>资源管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增</el-button>
      </div>
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        ref="tree"
        highlight-current
        style="margin-top: 20px;"
      >
        <span slot-scope="{ node, data }">
          <span>{{ data.name }}</span>
          <span style="margin-left: 10px; color: #999; font-size: 12px;">
            [{{ typeMap[data.type] }}] {{ data.permission || '' }}
          </span>
          <el-tag v-if="data.status === 1" type="success" size="mini" style="margin-left: 10px;">启用</el-tag>
          <el-tag v-else type="danger" size="mini" style="margin-left: 10px;">停用</el-tag>
          <span style="margin-left: 10px;">
            <el-button size="mini" type="primary" @click.stop="handleEdit(data)">编辑</el-button>
            <el-button size="mini" type="danger" @click.stop="handleDelete(data)">删除</el-button>
            <el-button size="mini" :type="data.status === 1 ? 'warning' : 'success'" @click.stop="handleToggleStatus(data)">
              {{ data.status === 1 ? '停用' : '启用' }}
            </el-button>
          </span>
        </span>
      </el-tree>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" label-width="100px" ref="form">
        <el-form-item label="资源名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="资源编码">
          <el-input v-model="form.code"></el-input>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="菜单" value="menu"></el-option>
            <el-option label="页面" value="page"></el-option>
            <el-option label="按钮" value="button"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上级资源">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            check-strictly
            style="width: 100%"
            placeholder="请选择上级资源"
          >
          </el-tree-select>
        </el-form-item>
        <el-form-item label="路由路径" v-if="form.type === 'menu' || form.type === 'page'">
          <el-input v-model="form.path" placeholder="如：/system/user"></el-input>
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.type === 'menu' || form.type === 'page'">
          <el-input v-model="form.component" placeholder="如：views/system/User"></el-input>
        </el-form-item>
        <el-form-item label="权限标识" v-if="form.type === 'button' || form.type === 'page'">
          <el-input v-model="form.permission" placeholder="如：system:user:add"></el-input>
        </el-form-item>
        <el-form-item label="图标" v-if="form.type === 'menu'">
          <el-input v-model="form.icon" placeholder="如：el-icon-menu"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%"></el-input-number>
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
      treeData: [],
      treeProps: {
        children: 'children',
        label: 'name'
      },
      typeMap: {
        menu: '菜单',
        page: '页面',
        button: '按钮'
      },
      dialogVisible: false,
      isEdit: false,
      form: {
        id: null,
        name: '',
        code: '',
        type: 'menu',
        path: '',
        component: '',
        icon: '',
        sort: 1,
        parentId: null,
        status: 1,
        permission: ''
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑资源' : '新增资源'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      const res = await this.$http.get('/resource/tree')
      if (res.code === 200) {
        this.treeData = res.data
      }
    },
    handleAdd() {
      this.isEdit = false
      this.form = { 
        id: null, 
        name: '', 
        code: '', 
        type: 'menu', 
        path: '', 
        component: '', 
        icon: '', 
        sort: 1, 
        parentId: null, 
        status: 1,
        permission: ''
      }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      await this.$confirm(`确定要删除资源 ${row.name} 吗?`, '提示', { type: 'warning' })
      const res = await this.$http.delete(`/resource/${row.id}`)
      if (res.code === 200) {
        this.$message.success('删除成功')
        this.getList()
      }
    },
    handleEdit(data) {
      this.isEdit = true
      this.form = { ...data }
      this.dialogVisible = true
    },
    async handleToggleStatus(data) {
      const status = data.status === 1 ? 0 : 1
      const res = await this.$http.post('/resource/updateStatus', { id: data.id, status })
      if (res.code === 200) {
        this.$message.success('状态更新成功')
        this.getList()
      }
    },
    async handleSubmit() {
      const res = await this.$http.post('/resource/save', this.form)
      if (res.code === 200) {
        this.$message.success(this.isEdit ? '更新成功' : '新增成功')
        this.dialogVisible = false
        this.getList()
      }
    }
  }
}
</script>
