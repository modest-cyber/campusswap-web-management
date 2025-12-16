<template>
  <div class="category-management">
    <h2 class="page-title">分类管理</h2>
    
    <el-card>
      <div style="margin-bottom: 20px">
        <el-button type="primary" @click="handleAdd()">添加一级分类</el-button>
      </div>
      
      <el-table 
        :data="categoryList" 
        v-loading="loading"
        row-key="id"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" width="200" />
        <el-table-column prop="level" label="层级" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.level === 1" type="primary">一级</el-tag>
            <el-tag v-else type="success">二级</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button 
              link 
              type="primary" 
              size="small" 
              @click="handleAdd(row)"
              v-if="row.level === 1"
            >
              添加子分类
            </el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              link 
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog
      v-model="formVisible"
      :title="formTitle"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类" v-if="form.parentId !== 0">
          <el-input :value="parentCategoryName" disabled />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { 
  getCategoryTree, 
  createCategory, 
  updateCategory, 
  deleteCategory,
  updateCategoryStatus,
  type Category,
  type CategoryForm 
} from '../../api/admin'

const loading = ref(false)
const categoryList = ref<Category[]>([])
const formVisible = ref(false)
const formRef = ref<FormInstance>()
const parentCategoryName = ref('')

const form = reactive<CategoryForm>({
  id: undefined,
  name: '',
  parentId: 0,
  sort: 0,
  status: 1
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}

const formTitle = computed(() => {
  if (form.id) {
    return '编辑分类'
  } else if (form.parentId !== 0) {
    return '添加子分类'
  } else {
    return '添加一级分类'
  }
})

const loadCategoryList = async () => {
  loading.value = true
  try {
    categoryList.value = await getCategoryTree()
  } catch (error) {
    ElMessage.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = (parent?: Category) => {
  formRef.value?.resetFields()
  form.id = undefined
  form.name = ''
  form.parentId = parent ? parent.id : 0
  form.sort = 0
  form.status = 1
  parentCategoryName.value = parent ? parent.name : ''
  formVisible.value = true
}

const handleEdit = (row: Category) => {
  form.id = row.id
  form.name = row.name
  form.parentId = row.parentId
  form.sort = row.sort
  form.status = row.status
  
  if (row.parentId !== 0) {
    const parent = categoryList.value.find(c => c.id === row.parentId)
    parentCategoryName.value = parent ? parent.name : ''
  }
  
  formVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateCategory(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createCategory(form)
          ElMessage.success('创建成功')
        }
        formVisible.value = false
        loadCategoryList()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleToggleStatus = async (row: Category) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}该分类吗?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await updateCategoryStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadCategoryList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该分类吗? 此操作不可恢复!',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    loadCategoryList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadCategoryList()
})
</script>

<style scoped>
.category-management {
  padding: 0;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
</style>
