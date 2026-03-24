<template>
  <div class="ticket-list">
    <!-- 搜索筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="工单编号">
          <el-input v-model="queryForm.ticketNo" placeholder="请输入工单编号" clearable />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="请输入标题关键字" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="新建" value="NEW" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="待验证" value="PENDING_VERIFY" />
            <el-option label="已解决" value="SOLVED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="queryForm.priority" placeholder="请选择优先级" clearable>
            <el-option label="紧急" value="URGENT" />
            <el-option label="高" value="HIGH" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="请选择类型" clearable>
            <el-option label="Bug" value="BUG" />
            <el-option label="咨询" value="CONSULT" />
            <el-option label="需求" value="DEMAND" />
            <el-option label="投诉" value="COMPLAINT" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理人">
          <el-select v-model="queryForm.handlerId" placeholder="请选择处理人" clearable filterable>
            <el-option
              v-for="user in handlerList"
              :key="user.id"
              :label="user.realName"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="客户">
          <el-select v-model="queryForm.customerId" placeholder="请选择客户" clearable filterable>
            <el-option
              v-for="customer in customerList"
              :key="customer.id"
              :label="customer.name"
              :value="customer.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
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
          <el-icon><Plus /></el-icon> 创建工单
        </el-button>
        <el-button :disabled="!multipleSelection.length" @click="handleBatchAssign">
          <el-icon><User /></el-icon> 批量分配
        </el-button>
        <el-button :disabled="!multipleSelection.length" @click="handleBatchExport">
          <el-icon><Download /></el-icon> 导出
        </el-button>
      </div>
      <div class="right">
        <el-radio-group v-model="viewMode" size="default">
          <el-radio-button value="table">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
          <el-radio-button value="list">
            <el-icon><List /></el-icon>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 工单列表 -->
    <el-card class="table-card">
      <el-table
        ref="tableRef"
        :data="tableData"
        :row-key="row => row.id"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="ticketNo" label="工单编号" width="150" sortable="custom">
          <template #default="{ row }">
            <el-link type="primary" @click="goToDetail(row.id)">{{ row.ticketNo }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" sortable="custom" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="ticket-title">
              <el-tag v-if="row.slaWarning" type="danger" size="small" effect="plain">SLA</el-tag>
              {{ row.title }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }">
            <el-tag :type="getPriorityTagType(row.priority)" size="small" effect="dark">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户" width="120" show-overflow-tooltip />
        <el-table-column prop="handlerName" label="处理人" width="100">
          <template #default="{ row }">
            <span v-if="row.handlerName">{{ row.handlerName }}</span>
            <el-link v-else type="primary" @click="handleAssign(row)">待分配</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="moduleName" label="模块" width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" sortable="custom">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToDetail(row.id)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-dropdown trigger="click">
              <el-button type="primary" link>更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleAssign(row)">分配</el-dropdown-item>
                  <el-dropdown-item @click="handleMerge(row)">合并</el-dropdown-item>
                  <el-dropdown-item @click="handleSplit(row)">拆分</el-dropdown-item>
                  <el-dropdown-item divided @click="handleClose(row)">关闭</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 分配对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配工单" width="500px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="工单编号">
          <el-input v-model="assignForm.ticketNo" disabled />
        </el-form-item>
        <el-form-item label="处理人" required>
          <el-select v-model="assignForm.handlerId" placeholder="请选择处理人" filterable style="width: 100%">
            <el-option
              v-for="user in handlerList"
              :key="user.id"
              :label="`${user.realName} (${user.department})`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" :rows="3" placeholder="请输入分配备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const tableRef = ref(null)
const loading = ref(false)
const viewMode = ref('table')
const multipleSelection = ref([])
const tableData = ref([])

// 搜索表单
const queryForm = reactive({
  ticketNo: '',
  title: '',
  status: '',
  priority: '',
  type: '',
  handlerId: '',
  customerId: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 处理人列表
const handlerList = ref([
  { id: 1, realName: '张三', department: '技术支持部' },
  { id: 2, realName: '李四', department: '技术支持部' },
  { id: 3, realName: '王五', department: '客户成功部' }
])

// 客户列表
const customerList = ref([
  { id: 1, name: '某某科技有限公司' },
  { id: 2, name: '某某贸易有限公司' }
])

// 分配对话框
const assignDialogVisible = ref(false)
const assignForm = reactive({
  ticketNo: '',
  handlerId: '',
  remark: ''
})

// 加载数据
const loadData = async () => {
  loading.value = true
  // 模拟数据
  tableData.value = [
    {
      id: 1,
      ticketNo: 'TK20240324001',
      title: '用户无法登录系统，请尽快处理',
      type: 'BUG',
      priority: 'URGENT',
      status: 'PROCESSING',
      customerName: '某某科技有限公司',
      handlerName: '张三',
      moduleName: '用户认证',
      createTime: '2024-03-24 10:30:00',
      slaWarning: true
    },
    {
      id: 2,
      ticketNo: 'TK20240324002',
      title: '如何设置数据导出格式',
      type: 'CONSULT',
      priority: 'LOW',
      status: 'NEW',
      customerName: '某某贸易有限公司',
      handlerName: null,
      moduleName: '数据导出',
      createTime: '2024-03-24 11:00:00',
      slaWarning: false
    }
  ]
  pagination.total = 2
  loading.value = false
}

// 搜索
const handleQuery = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.keys(queryForm).forEach(key => {
    queryForm[key] = key === 'dateRange' ? [] : ''
  })
  pagination.page = 1
  loadData()
}

// 创建工单
const handleCreate = () => {
  router.push('/ticket/create')
}

// 批量分配
const handleBatchAssign = () => {
  ElMessage.info('批量分配功能开发中')
}

// 批量导出
const handleBatchExport = () => {
  ElMessage.info('导出功能开发中')
}

// 选择变化
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  console.log('sort', prop, order)
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/ticket/detail/${id}`)
}

// 编辑
const handleEdit = (row) => {
  router.push(`/ticket/edit/${row.id}`)
}

// 分配
const handleAssign = (row) => {
  assignForm.ticketNo = row.ticketNo
  assignForm.handlerId = ''
  assignForm.remark = ''
  assignDialogVisible.value = true
}

const confirmAssign = () => {
  if (!assignForm.handlerId) {
    ElMessage.warning('请选择处理人')
    return
  }
  ElMessage.success('分配成功')
  assignDialogVisible.value = false
  loadData()
}

// 合并
const handleMerge = (row) => {
  ElMessage.info('合并功能开发中')
}

// 拆分
const handleSplit = (row) => {
  ElMessage.info('拆分功能开发中')
}

// 关闭
const handleClose = (row) => {
  ElMessageBox.confirm('确定要关闭此工单吗？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('工单已关闭')
    loadData()
  }).catch(() => {})
}

// 获取类型标签类型
const getTypeTagType = (type) => {
  const types = { BUG: 'danger', CONSULT: 'info', DEMAND: 'success', COMPLAINT: 'warning' }
  return types[type] || ''
}

// 获取类型文本
const getTypeText = (type) => {
  const texts = { BUG: 'Bug', CONSULT: '咨询', DEMAND: '需求', COMPLAINT: '投诉' }
  return texts[type] || type
}

// 获取优先级标签类型
const getPriorityTagType = (priority) => {
  const types = { URGENT: 'danger', HIGH: 'warning', MEDIUM: 'info', LOW: '' }
  return types[priority] || ''
}

// 获取优先级文本
const getPriorityText = (priority) => {
  const texts = { URGENT: '紧急', HIGH: '高', MEDIUM: '中', LOW: '低' }
  return texts[priority] || priority
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const types = {
    NEW: 'info',
    CONFIRMED: 'primary',
    PROCESSING: 'warning',
    PENDING_VERIFY: 'warning',
    SOLVED: 'success',
    CLOSED: 'info',
    WITHDRAWN: 'info',
    MERGED: 'info'
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    NEW: '新建',
    CONFIRMED: '已确认',
    PROCESSING: '处理中',
    PENDING_VERIFY: '待验证',
    SOLVED: '已解决',
    CLOSED: '已关闭',
    WITHDRAWN: '已撤回',
    MERGED: '已合并'
  }
  return texts[status] || status
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return datetime
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.ticket-list {
  .filter-card {
    margin-bottom: 16px;
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;

    .left {
      display: flex;
      gap: 8px;
    }
  }

  .table-card {
    .ticket-title {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
