<template>
  <div class="system-settings">
    <el-row :gutter="20">
      <!-- 左侧菜单 -->
      <el-col :span="4">
        <el-card>
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="user">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="role">
              <el-icon><Lock /></el-icon>
              <span>角色权限</span>
            </el-menu-item>
            <el-menu-item index="module">
              <el-icon><Tickets /></el-icon>
              <span>问题模块</span>
            </el-menu-item>
            <el-menu-item index="sla">
              <el-icon><Clock /></el-icon>
              <span>SLA配置</span>
            </el-menu-item>
            <el-menu-item index="template">
              <el-icon><Document /></el-icon>
              <span>工单模板</span>
            </el-menu-item>
            <el-menu-item index="quickReply">
              <el-icon><ChatDotRound /></el-icon>
              <span>快捷回复</span>
            </el-menu-item>
            <el-menu-item index="dingtalk">
              <el-icon><Connection /></el-icon>
              <span>钉钉集成</span>
            </el-menu-item>
            <el-menu-item index="log">
              <el-icon><Document /></el-icon>
              <span>操作日志</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="20">
        <!-- 用户管理 -->
        <el-card v-if="activeMenu === 'user'">
          <template #header>
            <div class="card-header">
              <span>用户管理</span>
              <el-button type="primary" @click="handleAddUser">
                <el-icon><Plus /></el-icon> 新建用户
              </el-button>
            </div>
          </template>
          <el-table :data="userList" v-loading="loading">
            <el-table-column prop="username" label="用户名" width="150" />
            <el-table-column prop="realName" label="姓名" width="120" />
            <el-table-column prop="email" label="邮箱" width="180" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column prop="roleName" label="角色" width="120" />
            <el-table-column prop="department" label="部门" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleUserStatusChange(row)" />
              </template>
            </el-table-column>
            <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditUser(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 角色权限 -->
        <el-card v-if="activeMenu === 'role'">
          <template #header>
            <div class="card-header">
              <span>角色权限</span>
              <el-button type="primary" @click="handleAddRole">
                <el-icon><Plus /></el-icon> 新建角色
              </el-button>
            </div>
          </template>
          <el-table :data="roleList">
            <el-table-column prop="name" label="角色名称" width="150" />
            <el-table-column prop="code" label="角色代码" width="150" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="userCount" label="用户数" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditRole(row)">编辑</el-button>
                <el-button type="primary" link @click="handleAssignPermission(row)">分配权限</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 问题模块 -->
        <el-card v-if="activeMenu === 'module'">
          <template #header>
            <div class="card-header">
              <span>问题模块</span>
              <el-button type="primary" @click="handleAddModule">
                <el-icon><Plus /></el-icon> 新建模块
              </el-button>
            </div>
          </template>
          <el-tree
            ref="moduleTreeRef"
            :data="moduleTreeData"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            default-expand-all
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ data.name }}</span>
                <span class="node-actions">
                  <el-button type="primary" link size="small" @click="handleEditModule(data)">编辑</el-button>
                  <el-button type="danger" link size="small" @click="handleDeleteModule(data)">删除</el-button>
                  <el-button type="primary" link size="small" @click="handleAddSubModule(data)">添加子模块</el-button>
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>

        <!-- SLA配置 -->
        <el-card v-if="activeMenu === 'sla'">
          <template #header>
            <div class="card-header">
              <span>SLA服务等级配置</span>
              <el-button type="primary" @click="handleAddSla">
                <el-icon><Plus /></el-icon> 新建SLA
              </el-button>
            </div>
          </template>
          <el-table :data="slaList">
            <el-table-column prop="name" label="SLA名称" width="150" />
            <el-table-column prop="level" label="服务等级" width="120">
              <template #default="{ row }">
                <el-tag :type="getSlaLevelType(row.level)">{{ getSlaLevelText(row.level) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="firstResponseTime" label="首次响应时限" width="120" />
            <el-table-column prop="resolveTime" label="解决时限" width="120" />
            <el-table-column prop="description" label="适用场景" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditSla(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteSla(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 快捷回复 -->
        <el-card v-if="activeMenu === 'quickReply'">
          <template #header>
            <div class="card-header">
              <span>快捷回复模板</span>
              <el-button type="primary" @click="handleAddTemplate">
                <el-icon><Plus /></el-icon> 新建模板
              </el-button>
            </div>
          </template>
          <el-table :data="templateList">
            <el-table-column prop="title" label="标题" width="200" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="usageCount" label="使用次数" width="100" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditTemplate(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteTemplate(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 钉钉集成 -->
        <el-card v-if="activeMenu === 'dingtalk'">
          <template #header>
            <span>钉钉集成配置</span>
          </template>
          <el-form :model="dingtalkForm" label-width="150px" style="max-width: 600px">
            <el-form-item label="启用钉钉集成">
              <el-switch v-model="dingtalkForm.enabled" />
            </el-form-item>
            <el-form-item label="钉钉应用AppKey">
              <el-input v-model="dingtalkForm.appKey" placeholder="请输入AppKey" />
            </el-form-item>
            <el-form-item label="钉钉应用AppSecret">
              <el-input v-model="dingtalkForm.appSecret" placeholder="请输入AppSecret" show-password />
            </el-form-item>
            <el-form-item label="启用@提醒">
              <el-switch v-model="dingtalkForm.atEnabled" />
            </el-form-item>
            <el-form-item label="被@提醒时通知">
              <el-select v-model="dingtalkForm.notifyType" multiple placeholder="请选择通知方式">
                <el-option label="站内信" value="INNER" />
                <el-option label="邮件" value="EMAIL" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveDingtalk">保存</el-button>
              <el-button @click="handleTestDingtalk">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 操作日志 -->
        <el-card v-if="activeMenu === 'log'">
          <template #header>
            <span>操作日志</span>
          </template>
          <el-form :inline="true" :model="logQueryForm" class="filter-form">
            <el-form-item label="操作人">
              <el-input v-model="logQueryForm.operator" placeholder="请输入操作人" clearable />
            </el-form-item>
            <el-form-item label="操作模块">
              <el-select v-model="logQueryForm.module" placeholder="请选择模块" clearable>
                <el-option label="系统管理" value="SYSTEM" />
                <el-option label="客户管理" value="CUSTOMER" />
                <el-option label="工单管理" value="TICKET" />
                <el-option label="知识库" value="KNOWLEDGE" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="logQueryForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLogQuery">搜索</el-button>
              <el-button @click="handleLogReset">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="logList">
            <el-table-column prop="operator" label="操作人" width="120" />
            <el-table-column prop="module" label="模块" width="120">
              <template #default="{ row }">
                {{ getModuleText(row.module) }}
              </template>
            </el-table-column>
            <el-table-column prop="action" label="操作" width="150" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="ip" label="IP地址" width="150" />
            <el-table-column prop="createTime" label="操作时间" width="180" />
          </el-table>
          <div class="pagination">
            <el-pagination
              v-model:current-page="logPagination.page"
              v-model:page-size="logPagination.pageSize"
              :total="logPagination.total"
              layout="total, prev, pager, next"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeMenu = ref('user')
const loading = ref(false)

const userList = ref([
  { id: 1, username: 'admin', realName: '管理员', email: 'admin@example.com', phone: '13800138000', roleName: '管理员', department: '技术支持部', status: 1, lastLoginTime: '2024-03-24 10:30:00' },
  { id: 2, username: 'zhangsan', realName: '张三', email: 'zhangsan@example.com', phone: '13800138001', roleName: '技术支持', department: '技术支持部', status: 1, lastLoginTime: '2024-03-24 09:15:00' },
  { id: 3, username: 'lisi', realName: '李四', email: 'lisi@example.com', phone: '13800138002', roleName: '客户成功', department: '客户成功部', status: 1, lastLoginTime: '2024-03-23 18:20:00' }
])

const roleList = ref([
  { id: 1, name: '管理员', code: 'ADMIN', description: '系统管理员，拥有所有权限', userCount: 1, status: 1 },
  { id: 2, name: '技术支持', code: 'SUPPORT', description: '技术支持人员，可以处理工单', userCount: 5, status: 1 },
  { id: 3, name: '客户成功', code: 'CS', description: '客户成功经理，可以查看客户数据', userCount: 3, status: 1 },
  { id: 4, name: '客户', code: 'CUSTOMER', description: '客户用户，可以创建和查看自己的工单', userCount: 50, status: 1 }
])

const moduleTreeRef = ref(null)
const moduleTreeData = ref([
  { id: 1, name: '产品功能', children: [
    { id: 11, name: '用户认证' },
    { id: 12, name: '数据管理' },
    { id: 13, name: '报表导出' }
  ]},
  { id: 2, name: '系统问题', children: [
    { id: 21, name: '登录异常' },
    { id: 22, name: '页面加载慢' }
  ]}
])

const slaList = ref([
  { id: 1, name: '标准服务', level: 'STANDARD', firstResponseTime: '8小时', resolveTime: '72小时', description: '适用于标准客户' },
  { id: 2, name: '高级服务', level: 'ADVANCED', firstResponseTime: '4小时', resolveTime: '24小时', description: '适用于高级客户' },
  { id: 3, name: '旗舰服务', level: 'PREMIUM', firstResponseTime: '1小时', resolveTime: '8小时', description: '适用于旗舰客户' }
])

const templateList = ref([
  { id: 1, title: '工单已收到', content: '您好，您的工单已收到，我们将尽快处理。', category: '通用', usageCount: 156 },
  { id: 2, title: '问题已解决', content: '您好，您反馈的问题已解决，请确认是否满意。', category: '通用', usageCount: 98 },
  { id: 3, title: '请提供更多信息', content: '您好，为了更好地解决您的问题，请提供以下信息...', category: '通用', usageCount: 67 }
])

const dingtalkForm = reactive({
  enabled: false,
  appKey: '',
  appSecret: '',
  atEnabled: false,
  notifyType: ['INNER']
})

const logQueryForm = reactive({
  operator: '',
  module: '',
  dateRange: []
})

const logList = ref([
  { id: 1, operator: 'admin', module: 'SYSTEM', action: '用户管理', description: '创建用户张三', ip: '192.168.1.100', createTime: '2024-03-24 10:30:00' },
  { id: 2, operator: 'admin', module: 'TICKET', action: '工单管理', description: '修改工单TK20240324001状态', ip: '192.168.1.100', createTime: '2024-03-24 09:15:00' }
])

const logPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 2
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const handleAddUser = () => ElMessage.info('新建用户功能开发中')
const handleEditUser = (row) => ElMessage.info('编辑用户功能开发中')
const handleDeleteUser = (row) => ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' }).then(() => ElMessage.success('删除成功'))
const handleUserStatusChange = (row) => ElMessage.success(`用户已${row.status ? '启用' : '禁用'}`)

const handleAddRole = () => ElMessage.info('新建角色功能开发中')
const handleEditRole = (row) => ElMessage.info('编辑角色功能开发中')
const handleAssignPermission = (row) => ElMessage.info('分配权限功能开发中')

const handleAddModule = () => ElMessage.info('新建模块功能开发中')
const handleEditModule = (data) => ElMessage.info('编辑模块功能开发中')
const handleDeleteModule = (data) => ElMessageBox.confirm('确定要删除该模块吗？', '提示', { type: 'warning' }).then(() => ElMessage.success('删除成功'))
const handleAddSubModule = (data) => ElMessage.info('添加子模块功能开发中')

const handleAddSla = () => ElMessage.info('新建SLA功能开发中')
const handleEditSla = (row) => ElMessage.info('编辑SLA功能开发中')
const handleDeleteSla = (row) => ElMessageBox.confirm('确定要删除该SLA吗？', '提示', { type: 'warning' }).then(() => ElMessage.success('删除成功'))

const handleAddTemplate = () => ElMessage.info('新建模板功能开发中')
const handleEditTemplate = (row) => ElMessage.info('编辑模板功能开发中')
const handleDeleteTemplate = (row) => ElMessageBox.confirm('确定要删除该模板吗？', '提示', { type: 'warning' }).then(() => ElMessage.success('删除成功'))

const handleSaveDingtalk = () => ElMessage.success('保存成功')
const handleTestDingtalk = () => ElMessage.success('连接测试成功')

const handleLogQuery = () => ElMessage.info('搜索日志')
const handleLogReset = () => {
  logQueryForm.operator = ''
  logQueryForm.module = ''
  logQueryForm.dateRange = []
}

const getSlaLevelType = (level) => {
  const types = { STANDARD: 'info', ADVANCED: 'warning', PREMIUM: 'success' }
  return types[level] || ''
}

const getSlaLevelText = (level) => {
  const texts = { STANDARD: '标准', ADVANCED: '高级', PREMIUM: '旗舰' }
  return texts[level] || level
}

const getModuleText = (module) => {
  const texts = { SYSTEM: '系统管理', CUSTOMER: '客户管理', TICKET: '工单管理', KNOWLEDGE: '知识库' }
  return texts[module] || module
}

onMounted(() => {})
</script>

<style lang="scss" scoped>
.system-settings {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .tree-node {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding-right: 16px;

    .node-actions {
      display: none;
    }

    &:hover .node-actions {
      display: block;
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
