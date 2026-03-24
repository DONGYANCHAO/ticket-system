<template>
  <div class="customer-list">
    <!-- 搜索筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="客户名称">
          <el-input v-model="queryForm.name" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="queryForm.level" placeholder="请选择客户等级" clearable>
            <el-option label="标准" value="STANDARD" />
            <el-option label="高级" value="ADVANCED" />
            <el-option label="旗舰" value="PREMIUM" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属行业">
          <el-input v-model="queryForm.industry" placeholder="请输入行业" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <div class="left">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon> 新建客户
        </el-button>
        <el-button @click="handleImport">
          <el-icon><Upload /></el-icon> 导入
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon> 导出
        </el-button>
      </div>
      <div class="right">
        <el-button-group>
          <el-button :type="viewMode === 'table' ? 'primary' : ''" @click="viewMode = 'table'">
            <el-icon><Grid /></el-icon>
          </el-button>
          <el-button :type="viewMode === 'card' ? 'primary' : ''" @click="viewMode = 'card'">
            <el-icon><Menu /></el-icon>
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 客户列表 - 表格视图 -->
    <el-card v-if="viewMode === 'table'" class="table-card">
      <el-table
        :data="tableData"
        :row-key="row => row.id"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="客户名称" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="goToDetail(row.id)">{{ row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="客户等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.level)">{{ getLevelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="industry" label="所属行业" width="120" />
        <el-table-column prop="scale" label="企业规模" width="100">
          <template #default="{ row }">
            {{ getScaleText(row.scale) }}
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="ticketCount" label="工单数" width="80" sortable>
          <template #default="{ row }">
            <el-link type="primary">{{ row.ticketCount }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToDetail(row.id)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 客户列表 - 卡片视图 -->
    <el-row v-else :gutter="16" class="card-grid">
      <el-col v-for="item in tableData" :key="item.id" :span="8">
        <el-card shadow="hover" class="customer-card">
          <div class="customer-header">
            <h3>{{ item.name }}</h3>
            <el-tag :type="getLevelTagType(item.level)">{{ getLevelText(item.level) }}</el-tag>
          </div>
          <div class="customer-info">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ item.contactName }}</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>{{ item.contactPhone }}</span>
            </div>
            <div class="info-item">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ item.industry }}</span>
            </div>
          </div>
          <div class="customer-stats">
            <div class="stat-item">
              <span class="stat-value">{{ item.ticketCount }}</span>
              <span class="stat-label">工单数</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ item.solvedRate || 0 }}%</span>
              <span class="stat-label">解决率</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ item.avgScore || 0 }}</span>
              <span class="stat-label">满意度</span>
            </div>
          </div>
          <div class="customer-actions">
            <el-button type="primary" link @click="goToDetail(item.id)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(item)">编辑</el-button>
            <el-button type="primary" link @click="handleCreateTicket(item)">创建工单</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户名称" prop="name" required>
          <el-input v-model="form.name" placeholder="请输入客户名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户等级" prop="level" required>
              <el-select v-model="form.level" placeholder="请选择客户等级" style="width: 100%">
                <el-option label="标准" value="STANDARD" />
                <el-option label="高级" value="ADVANCED" />
                <el-option label="旗舰" value="PREMIUM" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业规模" prop="scale">
              <el-select v-model="form.scale" placeholder="请选择企业规模" style="width: 100%">
                <el-option label="小型" value="SMALL" />
                <el-option label="中型" value="MEDIUM" />
                <el-option label="大型" value="LARGE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属行业" prop="industry">
          <el-input v-model="form.industry" placeholder="请输入所属行业" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="客户地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入客户地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const loading = ref(false)
const viewMode = ref('table')
const tableData = ref([])
const multipleSelection = ref([])

const queryForm = reactive({
  name: '',
  level: '',
  industry: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('新建客户')
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  id: '',
  name: '',
  level: 'STANDARD',
  scale: '',
  industry: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  remark: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择客户等级', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  tableData.value = [
    {
      id: 1,
      name: '某某科技有限公司',
      level: 'PREMIUM',
      industry: '互联网',
      scale: 'LARGE',
      contactName: '张三',
      contactPhone: '13800138001',
      ticketCount: 15,
      solvedRate: 93,
      avgScore: 4.8,
      status: 1,
      createTime: '2024-01-15 10:30:00'
    },
    {
      id: 2,
      name: '某某贸易有限公司',
      level: 'ADVANCED',
      industry: '贸易',
      scale: 'MEDIUM',
      contactName: '李四',
      contactPhone: '13800138002',
      ticketCount: 8,
      solvedRate: 88,
      avgScore: 4.5,
      status: 1,
      createTime: '2024-02-20 14:20:00'
    }
  ]
  pagination.total = 2
  loading.value = false
}

const handleQuery = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  Object.keys(queryForm).forEach(key => {
    queryForm[key] = ''
  })
  pagination.page = 1
  loadData()
}

const handleCreate = () => {
  dialogTitle.value = '新建客户'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑客户'
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  dialogVisible.value = true
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.keys(form).forEach(key => {
    if (key === 'level') form[key] = 'STANDARD'
    else if (key === 'status') form[key] = 1
    else form[key] = ''
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该客户吗？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleStatusChange = (row) => {
  ElMessage.success(`已${row.status ? '启用' : '禁用'}客户`)
}

const goToDetail = (id) => {
  router.push(`/customer/detail/${id}`)
}

const handleCreateTicket = (customer) => {
  router.push({ path: '/ticket/create', query: { customerId: customer.id } })
}

const handleImport = () => {
  ElMessage.info('导入功能开发中')
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getLevelTagType = (level) => {
  const types = { STANDARD: 'info', ADVANCED: 'warning', PREMIUM: 'success' }
  return types[level] || ''
}

const getLevelText = (level) => {
  const texts = { STANDARD: '标准', ADVANCED: '高级', PREMIUM: '旗舰' }
  return texts[level] || level
}

const getScaleText = (scale) => {
  const texts = { SMALL: '小型', MEDIUM: '中型', LARGE: '大型' }
  return texts[scale] || scale
}

const formatDateTime = (datetime) => datetime

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.customer-list {
  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }

  .card-grid {
    .customer-card {
      margin-bottom: 16px;

      .customer-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h3 {
          margin: 0;
          font-size: 16px;
          color: #303133;
        }
      }

      .customer-info {
        margin-bottom: 16px;

        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #606266;
          font-size: 14px;
          margin-bottom: 8px;

          &:last-child {
            margin-bottom: 0;
          }
        }
      }

      .customer-stats {
        display: flex;
        justify-content: space-between;
        padding: 16px 0;
        border-top: 1px solid #ebeef5;
        border-bottom: 1px solid #ebeef5;
        margin-bottom: 16px;

        .stat-item {
          text-align: center;

          .stat-value {
            display: block;
            font-size: 18px;
            font-weight: 600;
            color: #303133;
          }

          .stat-label {
            font-size: 12px;
            color: #909399;
          }
        }
      }

      .customer-actions {
        display: flex;
        justify-content: space-between;
      }
    }
  }
}
</style>
