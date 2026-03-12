<template>
  <div class="resource-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资源管理</span>
          <el-button type="primary" @click="handleAdd">新增资源</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="资源名称/编码" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable>
            <el-option label="菜单" :value="1" />
            <el-option label="页面" :value="2" />
            <el-option label="按钮" :value="3" />
            <el-option label="接口" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="resourceName" label="资源名称" />
        <el-table-column prop="resourceCode" label="资源编码" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeType(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="url" label="URL" />
        <el-table-column prop="method" label="请求方式" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="form.resourceName" />
        </el-form-item>
        <el-form-item label="资源编码" prop="resourceCode">
          <el-input v-model="form.resourceCode" />
        </el-form-item>
        <el-form-item label="资源类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="菜单" :value="1" />
            <el-option label="页面" :value="2" />
            <el-option label="按钮" :value="3" />
            <el-option label="接口" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级资源" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="resourceTree"
            :props="{ label: 'resourceName', value: 'id', children: 'children' }"
            placeholder="请选择父级资源"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="URL" prop="url" v-if="form.type === 4">
          <el-input v-model="form.url" placeholder="/api/xxx" />
        </el-form-item>
        <el-form-item label="请求方式" prop="method" v-if="form.type === 4">
          <el-select v-model="form.method" placeholder="请选择">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.type === 1 || form.type === 2">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getResourceList,
  getResourceTree,
  createResource,
  updateResource,
  deleteResource,
  enableResource,
  disableResource
} from '@/api/resource'

const loading = ref(false)
const tableData = ref([])
const resourceTree = ref([])
const searchForm = reactive({
  keyword: '',
  type: null
})
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  resourceName: '',
  resourceCode: '',
  type: 1,
  url: '',
  method: '',
  parentId: null,
  icon: '',
  sort: 0,
  status: 1
})

const formRules = {
  resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  resourceCode: [{ required: true, message: '请输入资源编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择资源类型', trigger: 'change' }]
}

const getTypeText = (type) => {
  const map = { 1: '菜单', 2: '页面', 3: '按钮', 4: '接口' }
  return map[type] || '未知'
}

const getTypeType = (type) => {
  const map = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info' }
  return map[type] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getResourceList({
      keyword: searchForm.keyword,
      type: searchForm.type,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const loadResourceTree = async () => {
  const res = await getResourceTree()
  if (res.code === 200) {
    resourceTree.value = res.data
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = null
  pagination.pageNum = 1
  loadData()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增资源'
  Object.assign(form, {
    id: null,
    resourceName: '',
    resourceCode: '',
    type: 1,
    url: '',
    method: '',
    parentId: null,
    icon: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑资源'
  Object.assign(form, {
    id: row.id,
    resourceName: row.resourceName,
    resourceCode: row.resourceCode,
    type: row.type,
    url: row.url || '',
    method: row.method || '',
    parentId: row.parentId,
    icon: row.icon || '',
    sort: row.sort || 0,
    status: row.status
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const api = isEdit.value ? updateResource : createResource
  const res = await api({ ...form })
  if (res.code === 200) {
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    loadData()
    loadResourceTree()
  }
}

const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(`确定要${action}该资源吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const api = row.status === 1 ? disableResource : enableResource
    const res = await api(row.id)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该资源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteResource(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
      loadResourceTree()
    }
  })
}

onMounted(() => {
  loadData()
  loadResourceTree()
})
</script>

<style scoped>
.resource-management {
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

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
