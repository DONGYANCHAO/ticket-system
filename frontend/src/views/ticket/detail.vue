<template>
  <div class="ticket-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <div class="header-actions">
            <el-button @click="handlePrint">打印</el-button>
            <el-button @click="handleExportPdf">导出PDF</el-button>
            <el-dropdown trigger="click">
              <el-button type="primary">
                更多操作 <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleMerge">合并工单</el-dropdown-item>
                  <el-dropdown-item @click="handleSplit">拆分工单</el-dropdown-item>
                  <el-dropdown-item @click="handleCopy">复制工单</el-dropdown-item>
                  <el-dropdown-item divided @click="handleClose">关闭工单</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <div class="detail-container">
        <!-- 左侧工单信息 -->
        <div class="main-content">
          <!-- 基本信息 -->
          <div class="info-section">
            <div class="ticket-header">
              <div class="ticket-no">{{ ticketData.ticketNo }}</div>
              <el-tag :type="getStatusTagType(ticketData.status)">
                {{ getStatusText(ticketData.status) }}
              </el-tag>
            </div>
            <h2 class="ticket-title">{{ ticketData.title }}</h2>
            <div class="ticket-meta">
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ ticketData.customerName }} / {{ ticketData.creatorName }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ ticketData.createTime }}
              </span>
              <span class="meta-item">
                <el-tag :type="getTypeTagType(ticketData.type)" size="small">
                  {{ getTypeText(ticketData.type) }}
                </el-tag>
              </span>
              <span class="meta-item">
                <el-tag :type="getPriorityTagType(ticketData.priority)" size="small" effect="dark">
                  {{ getPriorityText(ticketData.priority) }}
                </el-tag>
              </span>
            </div>
          </div>

          <!-- 问题描述 -->
          <div class="info-section">
            <h3 class="section-title">问题描述</h3>
            <div class="description-content" v-html="ticketData.content"></div>
            <div v-if="ticketData.attachments?.length" class="attachments">
              <h4>附件</h4>
              <div class="attachment-list">
                <div v-for="file in ticketData.attachments" :key="file.id" class="attachment-item">
                  <el-icon><Document /></el-icon>
                  <a :href="file.url" target="_blank">{{ file.name }}</a>
                  <span class="file-size">({{ formatFileSize(file.size) }})</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 标签 -->
          <div v-if="ticketData.tags?.length" class="info-section">
            <h3 class="section-title">标签</h3>
            <div class="tags-container">
              <el-tag v-for="tag in ticketData.tags" :key="tag.id" effect="plain">
                {{ tag.name }}
              </el-tag>
            </div>
          </div>

          <!-- 处理记录 -->
          <div class="info-section">
            <h3 class="section-title">处理记录</h3>
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in ticketData.history"
                :key="index"
                :timestamp="item.createTime"
                :type="item.type"
                :hollow="item.hollow"
              >
                <div class="timeline-content">
                  <span class="timeline-operator">{{ item.operator }}</span>
                  <span class="timeline-action">{{ item.action }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>

          <!-- 回复对话 -->
          <div class="info-section">
            <h3 class="section-title">
              回复对话
              <el-switch v-model="showInternal" active-text="显示内部备注" />
            </h3>
            <div class="message-list">
              <div
                v-for="msg in filteredMessages"
                :key="msg.id"
                :class="['message-item', msg.senderType]"
              >
                <div class="message-header">
                  <el-avatar :size="32" :src="msg.senderAvatar">
                    {{ msg.senderName?.charAt(0) }}
                  </el-avatar>
                  <div class="message-info">
                    <span class="sender-name">{{ msg.senderName }}</span>
                    <el-tag v-if="msg.type === 'INTERNAL'" type="warning" size="small">
                      内部备注
                    </el-tag>
                  </div>
                  <span class="message-time">{{ msg.createTime }}</span>
                </div>
                <div class="message-content" v-html="msg.content"></div>
                <div v-if="msg.attachments?.length" class="message-attachments">
                  <a v-for="file in msg.attachments" :key="file.id" :href="file.url" target="_blank">
                    <el-icon><Document /></el-icon> {{ file.name }}
                  </a>
                </div>
              </div>
            </div>

            <!-- 回复输入 -->
            <div class="reply-form">
              <el-tabs v-model="replyType">
                <el-tab-pane label="公开回复" name="PUBLIC" />
                <el-tab-pane label="内部备注" name="INTERNAL" />
              </el-tabs>
              <div class="editor-toolbar">
                <el-button-group>
                  <el-button @click="insertTemplate">快捷回复</el-button>
                  <el-button @click="insertKnowledge">知识库</el-button>
                </el-button-group>
              </div>
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="4"
                placeholder="请输入回复内容..."
              />
              <div class="reply-actions">
                <el-upload
                  ref="uploadRef"
                  :auto-upload="false"
                  :limit="5"
                  multiple
                >
                  <el-button>
                    <el-icon><Upload /></el-icon> 添加附件
                  </el-button>
                </el-upload>
                <el-button type="primary" @click="handleSendReply" :loading="sending">
                  发送回复
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧信息栏 -->
        <div class="side-content">
          <!-- 处理人 -->
          <el-card class="side-card">
            <template #header>
              <span>处理信息</span>
            </template>
            <div class="info-list">
              <div class="info-item">
                <label>当前处理人</label>
                <div class="value">
                  <span v-if="ticketData.handlerName">{{ ticketData.handlerName }}</span>
                  <el-link v-else type="primary" @click="handleAssign">分配</el-link>
                </div>
              </div>
              <div class="info-item">
                <label>问题模块</label>
                <div class="value">{{ ticketData.moduleName || '未分类' }}</div>
              </div>
              <div class="info-item">
                <label>服务等级</label>
                <div class="value">{{ ticketData.serviceLevel || '标准' }}</div>
              </div>
            </div>
          </el-card>

          <!-- SLA信息 -->
          <el-card class="side-card">
            <template #header>
              <span>SLA状态</span>
            </template>
            <div class="sla-info">
              <div class="sla-item">
                <span class="sla-label">首次响应</span>
                <div class="sla-progress">
                  <el-progress
                    :percentage="ticketData.firstResponsePercent || 0"
                    :status="ticketData.firstResponseWarning ? 'warning' : 'success'"
                  />
                </div>
                <span class="sla-time">
                  {{ ticketData.firstResponseTime || '0h' }} / {{ ticketData.firstResponseLimit || '4h' }}
                </span>
              </div>
              <div class="sla-item">
                <span class="sla-label">问题解决</span>
                <div class="sla-progress">
                  <el-progress
                    :percentage="ticketData.resolvePercent || 0"
                    :status="ticketData.resolveWarning ? 'warning' : 'success'"
                  />
                </div>
                <span class="sla-time">
                  {{ ticketData.resolveTime || '0h' }} / {{ ticketData.resolveLimit || '24h' }}
                </span>
              </div>
            </div>
          </el-card>

          <!-- 抄送人 -->
          <el-card class="side-card">
            <template #header>
              <span>抄送人</span>
              <el-button type="primary" link @click="handleAddCc">添加</el-button>
            </template>
            <div v-if="ticketData.ccList?.length" class="cc-list">
              <div v-for="user in ticketData.ccList" :key="user.id" class="cc-item">
                <el-avatar :size="24">{{ user.name?.charAt(0) }}</el-avatar>
                <span>{{ user.name }}</span>
              </div>
            </div>
            <el-empty v-else description="暂无抄送人" :image-size="60" />
          </el-card>

          <!-- 操作日志 -->
          <el-card class="side-card">
            <template #header>
              <span>操作日志</span>
            </template>
            <div class="log-list">
              <div v-for="log in ticketData.operationLogs" :key="log.id" class="log-item">
                <span class="log-time">{{ log.createTime }}</span>
                <span class="log-content">{{ log.description }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <!-- 分配对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配工单" width="500px">
      <el-form :model="assignForm" label-width="80px">
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
          <el-input v-model="assignForm.remark" type="textarea" :rows="3" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const showInternal = ref(false)
const replyType = ref('PUBLIC')
const replyContent = ref('')
const sending = ref(false)
const uploadRef = ref(null)

const assignDialogVisible = ref(false)
const assignForm = reactive({
  handlerId: '',
  remark: ''
})

const handlerList = ref([
  { id: 1, realName: '张三', department: '技术支持部' },
  { id: 2, realName: '李四', department: '技术支持部' }
])

// 工单数据
const ticketData = ref({
  id: 1,
  ticketNo: 'TK20240324001',
  title: '用户无法登录系统',
  content: '<p>用户反映今天早上开始无法登录系统，提示用户名或密码错误，但确认密码是正确的。</p>',
  type: 'BUG',
  priority: 'URGENT',
  status: 'PROCESSING',
  customerName: '某某科技有限公司',
  creatorName: '张三',
  handlerName: '李四',
  moduleName: '用户认证',
  serviceLevel: '高级',
  createTime: '2024-03-24 10:30:00',
  attachments: [
    { id: 1, name: 'error_log.png', url: '#', size: 1024000 },
    { id: 2, name: 'screenshot.jpg', url: '#', size: 512000 }
  ],
  tags: [
    { id: 1, name: '高频问题' },
    { id: 2, name: '需要回访' }
  ],
  history: [
    { createTime: '2024-03-24 10:30:00', operator: '张三', action: '创建了工单', type: 'primary', hollow: false },
    { createTime: '2024-03-24 10:35:00', operator: '系统', action: '自动分配给李四', type: '', hollow: true },
    { createTime: '2024-03-24 11:00:00', operator: '李四', action: '开始处理', type: 'warning', hollow: false }
  ],
  ccList: [
    { id: 1, name: '王五' }
  ],
  operationLogs: [
    { id: 1, createTime: '10:30', description: '张三创建了工单' },
    { id: 2, createTime: '10:35', description: '系统自动分配给李四' }
  ],
  firstResponsePercent: 75,
  firstResponseTime: '3h',
  firstResponseLimit: '4h',
  firstResponseWarning: false,
  resolvePercent: 50,
  resolveTime: '12h',
  resolveLimit: '24h',
  resolveWarning: false
})

// 筛选消息
const filteredMessages = computed(() => {
  if (showInternal.value) {
    return ticketData.value.messages || []
  }
  return (ticketData.value.messages || []).filter(m => m.type !== 'INTERNAL')
})

// 标签类型映射
const getTypeTagType = (type) => {
  const types = { BUG: 'danger', CONSULT: 'info', DEMAND: 'success', COMPLAINT: 'warning' }
  return types[type] || ''
}

const getTypeText = (type) => {
  const texts = { BUG: 'Bug', CONSULT: '咨询', DEMAND: '需求', COMPLAINT: '投诉' }
  return texts[type] || type
}

const getPriorityTagType = (priority) => {
  const types = { URGENT: 'danger', HIGH: 'warning', MEDIUM: 'info', LOW: '' }
  return types[priority] || ''
}

const getPriorityText = (priority) => {
  const texts = { URGENT: '紧急', HIGH: '高', MEDIUM: '中', LOW: '低' }
  return texts[priority] || priority
}

const getStatusTagType = (status) => {
  const types = {
    NEW: 'info',
    CONFIRMED: 'primary',
    PROCESSING: 'warning',
    PENDING_VERIFY: 'warning',
    SOLVED: 'success',
    CLOSED: 'info'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    NEW: '新建',
    CONFIRMED: '已确认',
    PROCESSING: '处理中',
    PENDING_VERIFY: '待验证',
    SOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return texts[status] || status
}

const formatFileSize = (size) => {
  if (!size) return ''
  if (size < 1024) return size + 'B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + 'KB'
  return (size / (1024 * 1024)).toFixed(1) + 'MB'
}

const goBack = () => {
  router.back()
}

const handlePrint = () => {
  window.print()
}

const handleExportPdf = () => {
  ElMessage.info('导出PDF功能开发中')
}

const handleMerge = () => {
  ElMessage.info('合并工单功能开发中')
}

const handleSplit = () => {
  ElMessage.info('拆分工单功能开发中')
}

const handleCopy = () => {
  ElMessage.info('复制工单功能开发中')
}

const handleClose = () => {
  ElMessageBox.confirm('确定要关闭此工单吗？', '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('工单已关闭')
    })
    .catch(() => {})
}

const handleAssign = () => {
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
}

const handleAddCc = () => {
  ElMessage.info('添加抄送人功能开发中')
}

const insertTemplate = () => {
  ElMessage.info('快捷回复功能开发中')
}

const insertKnowledge = () => {
  ElMessage.info('知识库功能开发中')
}

const handleSendReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  sending.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('回复发送成功')
    replyContent.value = ''
  } catch (error) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  // 加载工单详情
})
</script>

<style lang="scss" scoped>
.ticket-detail {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .detail-container {
    display: flex;
    gap: 20px;
  }

  .main-content {
    flex: 1;
    min-width: 0;
  }

  .side-content {
    width: 320px;
    flex-shrink: 0;
  }

  .info-section {
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }
  }

  .ticket-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;

    .ticket-no {
      font-size: 14px;
      color: #909399;
    }
  }

  .ticket-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .ticket-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    color: #909399;
    font-size: 14px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .description-content {
    color: #606266;
    line-height: 1.8;
  }

  .attachments {
    margin-top: 16px;

    h4 {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }

    .attachment-list {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
    }

    .attachment-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background: #f5f7fa;
      border-radius: 4px;

      a {
        color: #409eff;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }

      .file-size {
        color: #909399;
        font-size: 12px;
      }
    }
  }

  .tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .timeline-content {
    .timeline-operator {
      font-weight: 600;
      color: #303133;
    }

    .timeline-action {
      color: #606266;
      margin-left: 8px;
    }
  }

  .message-list {
    margin-bottom: 20px;
  }

  .message-item {
    padding: 16px;
    margin-bottom: 16px;
    background: #f5f7fa;
    border-radius: 8px;

    &.CUSTOMER {
      background: #ecf5ff;
    }

    &.SUPPORT {
      background: #f0f9eb;
    }

    .message-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;

      .message-info {
        flex: 1;
        display: flex;
        align-items: center;
        gap: 8px;

        .sender-name {
          font-weight: 600;
          color: #303133;
        }
      }

      .message-time {
        color: #909399;
        font-size: 12px;
      }
    }

    .message-content {
      color: #606266;
      line-height: 1.6;
    }

    .message-attachments {
      margin-top: 12px;
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      a {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        background: #fff;
        border-radius: 4px;
        color: #409eff;
        font-size: 12px;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }

  .reply-form {
    .editor-toolbar {
      margin-bottom: 8px;
    }

    .reply-actions {
      display: flex;
      justify-content: space-between;
      margin-top: 12px;
    }
  }

  .side-card {
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .info-list {
    .info-item {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      label {
        color: #909399;
        font-size: 14px;
      }

      .value {
        color: #303133;
        font-size: 14px;
      }
    }
  }

  .sla-info {
    .sla-item {
      margin-bottom: 16px;

      &:last-child {
        margin-bottom: 0;
      }

      .sla-label {
        display: block;
        color: #909399;
        font-size: 12px;
        margin-bottom: 8px;
      }

      .sla-progress {
        margin-bottom: 4px;
      }

      .sla-time {
        font-size: 12px;
        color: #606266;
      }
    }
  }

  .cc-list {
    .cc-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 0;

      span {
        color: #303133;
        font-size: 14px;
      }
    }
  }

  .log-list {
    .log-item {
      display: flex;
      flex-direction: column;
      padding: 8px 0;
      border-bottom: 1px dashed #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .log-time {
        color: #909399;
        font-size: 12px;
      }

      .log-content {
        color: #606266;
        font-size: 14px;
      }
    }
  }
}

@media print {
  .header-actions,
  .reply-form,
  .side-content {
    display: none;
  }
}
</style>
