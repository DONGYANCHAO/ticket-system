<template>
  <div class="settings-page">
    <el-row :gutter="20">
      <!-- 左侧菜单 -->
      <el-col :span="4">
        <el-card>
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
            <el-menu-item index="notification">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </el-menu-item>
            <el-menu-item index="preference">
              <el-icon><Setting /></el-icon>
              <span>偏好设置</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="20">
        <!-- 个人信息 -->
        <el-card v-if="activeMenu === 'profile'">
          <template #header>
            <span>个人信息</span>
          </template>
          <el-form ref="profileFormRef" :model="profileForm" label-width="100px" class="settings-form">
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar :size="80" :src="profileForm.avatar">
                  {{ profileForm.realName?.charAt(0) }}
                </el-avatar>
                <el-upload :show-file-list="false" :auto-upload="false">
                  <el-button size="small" style="margin-top: 12px">更换头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="部门">
              <el-input v-model="profileForm.department" placeholder="请输入部门" />
            </el-form-item>
            <el-form-item label="职位">
              <el-input v-model="profileForm.position" placeholder="请输入职位" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 安全设置 -->
        <el-card v-if="activeMenu === 'security'">
          <template #header>
            <span>安全设置</span>
          </template>
          <div class="security-list">
            <div class="security-item">
              <div class="item-info">
                <h4>登录密码</h4>
                <p>定期修改密码可以提高账号安全性</p>
              </div>
              <el-button @click="handleChangePassword">修改密码</el-button>
            </div>
            <el-divider />
            <div class="security-item">
              <div class="item-info">
                <h4>两步验证</h4>
                <p>启用后登录需要输入手机验证码</p>
              </div>
              <el-switch v-model="securityForm.twoFactorEnabled" />
            </div>
            <el-divider />
            <div class="security-item">
              <div class="item-info">
                <h4>登录设备管理</h4>
                <p>查看并管理已登录的设备</p>
              </div>
              <el-button @click="handleManageDevices">管理设备</el-button>
            </div>
            <el-divider />
            <div class="security-item">
              <div class="item-info">
                <h4>登录日志</h4>
                <p>查看账号的登录历史记录</p>
              </div>
              <el-button @click="handleViewLoginLogs">查看日志</el-button>
            </div>
          </div>
        </el-card>

        <!-- 通知设置 -->
        <el-card v-if="activeMenu === 'notification'">
          <template #header>
            <span>通知设置</span>
          </template>
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="工单通知">
              <el-switch v-model="notificationForm.ticketEnabled" />
            </el-form-item>
            <el-form-item label="通知方式">
              <el-checkbox-group v-model="notificationForm.channels">
                <el-checkbox value="EMAIL">邮件</el-checkbox>
                <el-checkbox value="SMS">短信</el-checkbox>
                <el-checkbox value="INNER">站内信</el-checkbox>
                <el-checkbox value="DINGTALK">钉钉</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-divider />
            <el-form-item label="工单分配通知">
              <el-switch v-model="notificationForm.assignEnabled" />
            </el-form-item>
            <el-form-item label="工单状态变更">
              <el-switch v-model="notificationForm.statusEnabled" />
            </el-form-item>
            <el-form-item label="工单回复通知">
              <el-switch v-model="notificationForm.replyEnabled" />
            </el-form-item>
            <el-form-item label="SLA预警通知">
              <el-switch v-model="notificationForm.slaWarningEnabled" />
            </el-form-item>
            <el-form-item label="满意度通知">
              <el-switch v-model="notificationForm.feedbackEnabled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveNotification">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 偏好设置 -->
        <el-card v-if="activeMenu === 'preference'">
          <template #header>
            <span>偏好设置</span>
          </template>
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="语言">
              <el-select v-model="preferenceForm.language" style="width: 200px">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>
            <el-form-item label="主题">
              <el-select v-model="preferenceForm.theme" style="width: 200px">
                <el-option label="浅色主题" value="light" />
                <el-option label="深色主题" value="dark" />
                <el-option label="跟随系统" value="auto" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期格式">
              <el-select v-model="preferenceForm.dateFormat" style="width: 200px">
                <el-option label="YYYY-MM-DD" value="YYYY-MM-DD" />
                <el-option label="YYYY/MM/DD" value="YYYY/MM/DD" />
                <el-option label="DD-MM-YYYY" value="DD-MM-YYYY" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间格式">
              <el-select v-model="preferenceForm.timeFormat" style="width: 200px">
                <el-option label="24小时制" value="HH:mm:ss" />
                <el-option label="12小时制" value="hh:mm:ss A" />
              </el-select>
            </el-form-item>
            <el-form-item label="每页条数">
              <el-select v-model="preferenceForm.pageSize" style="width: 200px">
                <el-option label="10条" :value="10" />
                <el-option label="20条" :value="20" />
                <el-option label="50条" :value="50" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSavePreference">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="当前密码" prop="oldPassword" required>
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword" required>
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" required>
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeMenu = ref('profile')

const profileFormRef = ref(null)
const passwordFormRef = ref(null)

const profileForm = reactive({
  username: 'admin',
  realName: '管理员',
  phone: '13800138000',
  email: 'admin@example.com',
  department: '技术支持部',
  position: '系统管理员',
  avatar: ''
})

const securityForm = reactive({
  twoFactorEnabled: false
})

const notificationForm = reactive({
  ticketEnabled: true,
  channels: ['INNER', 'EMAIL'],
  assignEnabled: true,
  statusEnabled: true,
  replyEnabled: true,
  slaWarningEnabled: true,
  feedbackEnabled: false
})

const preferenceForm = reactive({
  language: 'zh-CN',
  theme: 'light',
  dateFormat: 'YYYY-MM-DD',
  timeFormat: 'HH:mm:ss',
  pageSize: 10
})

const passwordDialogVisible = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const handleSaveProfile = () => {
  ElMessage.success('保存成功')
}

const handleChangePassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

const confirmChangePassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
    }
  })
}

const handleManageDevices = () => {
  ElMessage.info('设备管理功能开发中')
}

const handleViewLoginLogs = () => {
  ElMessage.info('登录日志功能开发中')
}

const handleSaveNotification = () => {
  ElMessage.success('保存成功')
}

const handleSavePreference = () => {
  ElMessage.success('保存成功')
}
</script>

<style lang="scss" scoped>
.settings-page {
  .settings-form {
    max-width: 600px;
  }

  .avatar-upload {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .security-list {
    .security-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 0;

      .item-info {
        h4 {
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
  }
}
</style>
