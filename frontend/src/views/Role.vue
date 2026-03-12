<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="角色名称/编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="资源数量">
          <template #default="{ row }">
            {{ row.resourceIds ? row.resourceIds.length : 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handleBindResource(row)">绑定资源</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="resourceDialogVisible" title="绑定资源" width="600px">
      <el-tree
        ref="treeRef"
        :data="resourceTree"
        :props="{ label: 'name', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="selectedResourceIds"
      />
      <template #footer>
        <el-button @click="resourceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindResourceSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRolePage, createRole, updateRole, deleteRole, bindResources } from '../api/role'
import { getResourceList } from '../api/resource'

const loading = ref(false)
const tableData = ref([])
const resourceList = ref([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑角色' : '新增角色'))
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  code: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const resourceDialogVisible = ref(false)
const selectedResourceIds = ref([])
const currentRoleId = ref(null)
const treeRef = ref(null)

const resourceTree = computed(() => {
  const buildTree = (parentId) => {
    return resourceList.value
      .filter(r => r.parentId === parentId)
      .map(r => ({
        ...r,
        children: buildTree(r.id)
      }))
  }
  return buildTree(null)
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRolePage(queryParams)
    tableData.value = res.data.records
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchResourceList = async () => {
  try {
    const res = await getResourceList()
    resourceList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.code = ''
  form.description = ''
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
    name: row.name,
    code: row.code,
    description: row.description
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await updateRole(form)
      ElMessage.success('更新成功')
    } else {
      await createRole(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该角色？', '提示', { type: 'warning' })
  try {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleBindResource = (row) => {
  currentRoleId.value = row.id
  selectedResourceIds.value = row.resourceIds || []
  resourceDialogVisible.value = true
}

const handleBindResourceSubmit = async () => {
  const checkedIds = treeRef.value.getCheckedKeys()
  try {
    await bindResources(currentRoleId.value, checkedIds)
    ElMessage.success('绑定成功')
    resourceDialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchData()
  fetchResourceList()
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
