<template>
  <div class="login-container">
    <div class="login-left">
      <div class="login-slogan">
        <h1>客户工单平台</h1>
        <p>高效、智能的工单管理系统</p>
        <div class="features">
          <div class="feature-item">
            <el-icon><Clock /></el-icon>
            <span>SLA时限管理</span>
          </div>
          <div class="feature-item">
            <el-icon><DataAnalysis /></el-icon>
            <span>多维度数据报表</span>
          </div>
          <div class="feature-item">
            <el-icon><ChatDotRound /></el-icon>
            <span>实时工单沟通</span>
          </div>
          <div class="feature-item">
            <el-icon><Connection /></el-icon>
            <span>钉钉集成通知</span>
          </div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-box">
        <div class="login-header">
          <h2>用户登录</h2>
          <p>欢迎使用客户工单平台</p>
        </div>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              :loading="loading"
              type="primary"
              size="large"
              class="login-button"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <el-link type="primary" :underline="false" @click="handleForgotPassword">
            忘记密码？
          </el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login(loginForm)
      ElMessage.success('登录成功')
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}

const handleForgotPassword = () => {
  ElMessage.info('请联系管理员重置密码')
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.login-slogan {
  text-align: center;
  max-width: 500px;
  padding: 40px;

  h1 {
    font-size: 48px;
    font-weight: 600;
    margin-bottom: 16px;
  }

  p {
    font-size: 20px;
    opacity: 0.9;
    margin-bottom: 48px;
  }

  .features {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;

    .feature-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 12px;
      backdrop-filter: blur(10px);

      .el-icon {
        font-size: 24px;
      }

      span {
        font-size: 14px;
      }
    }
  }
}

.login-right {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.login-box {
  width: 360px;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #909399;
  }
}

.login-form {
  .login-button {
    width: 100%;
    height: 48px;
    font-size: 16px;
    border-radius: 8px;
  }
}

.login-footer {
  text-align: center;
  margin-top: 16px;
}
</style>
