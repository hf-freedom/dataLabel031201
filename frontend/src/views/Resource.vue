<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资源列表</span>
          <el-button type="primary" @click="handleAdd">新增资源</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="资源名称/编码" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable>
            <el-option label="菜单" value="MENU" />
            <el-option label="页面" value="PAGE" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="API" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe v-loading="loading" row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="资源名称" />
        <el-table-column prop="code" label="资源编码" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="apiPath" label="API路径" />
        <el-table-column prop="apiMethod" label="API方法" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="资源名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="资源编码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="资源类型" prop="type">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="菜单" value="MENU" />
            <el-option label="页面" value="PAGE" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="API" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级资源" prop="parentId">
          <el-cascader
            v-model="form.parentId"
            :options="parentOptions"
            :props="{ value: 'id', label: 'name', checkStrictly: true, emitPath: false }"
            clearable
            placeholder="选择父级资源"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="路径" prop="path" v-if="form.type !== 'API'">
          <el-input v-model="form.path" placeholder="/path" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.type === 'MENU'">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="API路径" prop="apiPath" v-if="form.type === 'API'">
          <el-input v-model="form.apiPath" placeholder="/api/xxx" />
        </el-form-item>
        <el-form-item label="API方法" prop="apiMethod" v-if="form.type === 'API'">
          <el-select v-model="form.apiMethod" style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResourcePage, createResource, updateResource, deleteResource, enableResource, disableResource } from '../api/resource'

const loading = ref(false)
const tableData = ref([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  type: ''
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑资源' : '新增资源'))
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  code: '',
  type: 'MENU',
  parentId: null,
  path: '',
  icon: '',
  sort: 0,
  apiPath: '',
  apiMethod: 'GET'
})

const rules = {
  name: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入资源编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择资源类型', trigger: 'change' }]
}

const parentOptions = computed(() => {
  const options = tableData.value
    .filter(r => r.type === 'MENU' || r.type === 'PAGE')
    .map(r => ({ id: r.id, name: r.name }))
  return [{ id: null, name: '无' }, ...options]
})

const getTypeTagType = (type) => {
  const map = {
    MENU: 'primary',
    PAGE: 'success',
    BUTTON: 'warning',
    API: 'danger'
  }
  return map[type] || ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getResourcePage(queryParams)
    tableData.value = res.data.records
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.code = ''
  form.type = 'MENU'
  form.parentId = null
  form.path = ''
  form.icon = ''
  form.sort = 0
  form.apiPath = ''
  form.apiMethod = 'GET'
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
    type: row.type,
    parentId: row.parentId,
    path: row.path,
    icon: row.icon,
    sort: row.sort,
    apiPath: row.apiPath,
    apiMethod: row.apiMethod
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await updateResource(form)
      ElMessage.success('更新成功')
    } else {
      await createResource(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该资源？', '提示', { type: 'warning' })
  try {
    await deleteResource(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

const handleToggleStatus = async (row) => {
  try {
    if (row.status === 1) {
      await disableResource(row.id)
      ElMessage.success('已停用')
    } else {
      await enableResource(row.id)
      ElMessage.success('已启用')
    }
    fetchData()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchData()
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
