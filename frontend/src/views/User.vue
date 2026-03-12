<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="用户名/昵称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column label="角色">
          <template #default="{ row }">
            <el-tag v-for="roleId in row.roleIds" :key="roleId" style="margin-right: 5px">
              {{ getRoleName(roleId) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handleBindRole(row)">绑定角色</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="roleDialogVisible" title="绑定角色" width="400px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">
          {{ role.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, createUser, updateUser, deleteUser, bindRoles } from '../api/user'
import { getRoleList } from '../api/role'

const loading = ref(false)
const tableData = ref([])
const roleList = ref([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑用户' : '新增用户'))
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const roleDialogVisible = ref(false)
const selectedRoleIds = ref([])
const currentUserId = ref(null)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    tableData.value = res.data.records
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchRoleList = async () => {
  try {
    const res = await getRoleList()
    roleList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const getRoleName = (roleId) => {
  const role = roleList.value.find(r => r.id === roleId)
  return role ? role.name : roleId
}

const resetForm = () => {
  form.id = null
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.email = ''
  form.phone = ''
}

const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    username: row.username,
    password: row.password,
    nickname: row.nickname,
    email: row.email,
    phone: row.phone
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await updateUser(form)
      ElMessage.success('更新成功')
    } else {
      await createUser(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该用户？', '提示', { type: 'warning' })
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleBindRole = (row) => {
  currentUserId.value = row.id
  selectedRoleIds.value = row.roleIds || []
  roleDialogVisible.value = true
}

const handleBindRoleSubmit = async () => {
  try {
    await bindRoles(currentUserId.value, selectedRoleIds.value)
    ElMessage.success('绑定成功')
    roleDialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchData()
  fetchRoleList()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
