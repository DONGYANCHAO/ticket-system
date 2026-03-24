<template>
  <div class="report-list">
    <!-- 操作按钮 -->
    <div class="toolbar">
      <div class="left">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon> 新建报表
        </el-button>
      </div>
    </div>

    <!-- 报表卡片 -->
    <el-row :gutter="16" class="report-grid">
      <el-col v-for="report in reportList" :key="report.id" :span="8">
        <el-card shadow="hover" class="report-card">
          <div class="report-header">
            <el-icon :size="32" :style="{ color: report.color }">
              <component :is="report.icon" />
            </el-icon>
            <div class="report-info">
              <h3>{{ report.name }}</h3>
              <p>{{ report.description }}</p>
            </div>
          </div>
          <div class="report-stats">
            <div class="stat-item">
              <span class="stat-label">使用次数</span>
              <span class="stat-value">{{ report.usageCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">创建时间</span>
              <span class="stat-value">{{ report.createTime }}</span>
            </div>
          </div>
          <div class="report-actions">
            <el-button type="primary" @click="handleView(report)">查看</el-button>
            <el-button @click="handleEdit(report)">编辑</el-button>
            <el-dropdown trigger="click">
              <el-button>更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleExport(report)">导出</el-dropdown-item>
                  <el-dropdown-item @click="handleSubscribe(report)">订阅</el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(report)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订阅管理 -->
    <el-card style="margin-top: 24px">
      <template #header>
        <div class="card-header">
          <span>订阅管理</span>
          <el-button type="primary" size="small" @click="handleAddSubscription">
            <el-icon><Plus /></el-icon> 添加订阅
          </el-button>
        </div>
      </template>
      <el-table :data="subscriptionList">
        <el-table-column prop="name" label="订阅名称" />
        <el-table-column prop="reportName" label="报表名称" />
        <el-table-column prop="scheduleType" label="周期" width="100">
          <template #default="{ row }">
            {{ getScheduleText(row.scheduleType) }}
          </template>
        </el-table-column>
        <el-table-column prop="recipients" label="接收人" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" @change="handleSubscriptionStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="lastRunTime" label="上次执行" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditSubscription(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDeleteSubscription(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 订阅对话框 -->
    <el-dialog v-model="subscriptionDialogVisible" title="订阅设置" width="600px">
      <el-form ref="subscriptionFormRef" :model="subscriptionForm" label-width="100px">
        <el-form-item label="订阅名称" prop="name" required>
          <el-input v-model="subscriptionForm.name" placeholder="请输入订阅名称" />
        </el-form-item>
        <el-form-item label="选择报表" prop="reportId" required>
          <el-select v-model="subscriptionForm.reportId" placeholder="请选择报表" style="width: 100%">
            <el-option
              v-for="report in reportList"
              :key="report.id"
              :label="report.name"
              :value="report.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发送周期" prop="scheduleType" required>
          <el-select v-model="subscriptionForm.scheduleType" placeholder="请选择发送周期" style="width: 100%">
            <el-option label="每日" value="DAILY" />
            <el-option label="每周" value="WEEKLY" />
            <el-option label="每月" value="MONTHLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送时间" prop="sendTime">
          <el-time-picker
            v-model="subscriptionForm.sendTime"
            placeholder="请选择发送时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="接收人" prop="recipients" required>
          <el-select
            v-model="subscriptionForm.recipientIds"
            multiple
            placeholder="请选择接收人"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.realName"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发送格式">
          <el-checkbox-group v-model="subscriptionForm.formats">
            <el-checkbox value="PDF">PDF</el-checkbox>
            <el-checkbox value="EXCEL">Excel</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="subscriptionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSubscription">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const reportList = ref([
  {
    id: 1,
    name: '工单统计报表',
    description: '按日/周/月统计工单数量、类型分布、处理时效',
    icon: 'DataLine',
    color: '#409EFF',
    usageCount: 156,
    createTime: '2024-01-15'
  },
  {
    id: 2,
    name: '客服绩效报表',
    description: '统计各客服处理工单数量、解决率、满意度评分',
    icon: 'User',
    color: '#67C23A',
    usageCount: 98,
    createTime: '2024-02-20'
  },
  {
    id: 3,
    name: 'SLA执行报表',
    description: '分析SLA达成率、响应时间、解决时间趋势',
    icon: 'Clock',
    color: '#E6A23C',
    usageCount: 67,
    createTime: '2024-03-10'
  },
  {
    id: 4,
    name: '客户分析报表',
    description: '按客户维度统计工单量、问题类型、响应时效',
    icon: 'OfficeBuilding',
    color: '#F56C6C',
    usageCount: 45,
    createTime: '2024-03-15'
  },
  {
    id: 5,
    name: '知识库使用报表',
    description: '统计知识库浏览量、搜索关键词、满意度',
    icon: 'Document',
    color: '#909399',
    usageCount: 32,
    createTime: '2024-03-18'
  },
  {
    id: 6,
    name: '自定义报表',
    description: '根据自定义条件生成统计报表',
    icon: 'Setting',
    color: '#909399',
    usageCount: 23,
    createTime: '2024-03-20'
  }
])

const subscriptionList = ref([
  {
    id: 1,
    name: '每日工单汇总',
    reportName: '工单统计报表',
    scheduleType: 'DAILY',
    recipients: '张三、李四',
    status: true,
    lastRunTime: '2024-03-24 09:00:00'
  },
  {
    id: 2,
    name: '每周绩效报告',
    reportName: '客服绩效报表',
    scheduleType: 'WEEKLY',
    recipients: '王五',
    status: true,
    lastRunTime: '2024-03-18 09:00:00'
  }
])

const userList = ref([
  { id: 1, realName: '张三' },
  { id: 2, realName: '李四' },
  { id: 3, realName: '王五' }
])

const subscriptionDialogVisible = ref(false)
const subscriptionFormRef = ref(null)

const subscriptionForm = reactive({
  id: '',
  name: '',
  reportId: '',
  scheduleType: 'DAILY',
  sendTime: '',
  recipientIds: [],
  formats: ['PDF']
})

const handleCreate = () => {
  ElMessage.info('自定义报表功能开发中')
}

const handleView = (report) => {
  router.push(`/report/view/${report.id}`)
}

const handleEdit = (report) => {
  ElMessage.info('编辑报表功能开发中')
}

const handleExport = (report) => {
  ElMessage.info('导出报表功能开发中')
}

const handleSubscribe = (report) => {
  subscriptionForm.reportId = report.id
  subscriptionDialogVisible.value = true
}

const handleDelete = (report) => {
  ElMessageBox.confirm('确定要删除该报表吗？', '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

const handleAddSubscription = () => {
  subscriptionForm.id = ''
  subscriptionForm.name = ''
  subscriptionForm.reportId = ''
  subscriptionForm.scheduleType = 'DAILY'
  subscriptionForm.sendTime = ''
  subscriptionForm.recipientIds = []
  subscriptionForm.formats = ['PDF']
  subscriptionDialogVisible.value = true
}

const handleEditSubscription = (row) => {
  Object.keys(subscriptionForm).forEach(key => {
    subscriptionForm[key] = row[key]
  })
  subscriptionDialogVisible.value = true
}

const handleDeleteSubscription = (row) => {
  ElMessageBox.confirm('确定要删除该订阅吗？', '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

const handleSubscriptionStatusChange = (row) => {
  ElMessage.success(`订阅已${row.status ? '启用' : '停用'}`)
}

const confirmSubscription = () => {
  ElMessage.success('订阅保存成功')
  subscriptionDialogVisible.value = false
}

const getScheduleText = (type) => {
  const texts = { DAILY: '每日', WEEKLY: '每周', MONTHLY: '每月' }
  return texts[type] || type
}

onMounted(() => {})
</script>

<style lang="scss" scoped>
.report-list {
  .toolbar {
    margin-bottom: 16px;
  }

  .report-card {
    margin-bottom: 16px;

    .report-header {
      display: flex;
      align-items: flex-start;
      gap: 16px;
      margin-bottom: 16px;

      .report-info {
        flex: 1;

        h3 {
          margin: 0 0 8px;
          font-size: 16px;
          color: #303133;
        }

        p {
          margin: 0;
          color: #909399;
          font-size: 14px;
        }
      }
    }

    .report-stats {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-top: 1px solid #ebeef5;
      border-bottom: 1px solid #ebeef5;
      margin-bottom: 16px;

      .stat-item {
        text-align: center;

        .stat-label {
          display: block;
          font-size: 12px;
          color: #909399;
          margin-bottom: 4px;
        }

        .stat-value {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }

    .report-actions {
      display: flex;
      gap: 8px;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
