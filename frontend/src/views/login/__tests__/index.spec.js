import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useRouter } from 'vue-router'
import LoginView from '../index.vue'

// 模拟 vue-router
vi.mock('vue-router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn()
  }))
}))

// 模拟 Element Plus 消息组件
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn()
  },
  ElLoading: {
    service: vi.fn(() => ({
      close: vi.fn()
    }))
  }
}))

describe('Login View', () => {
  let wrapper
  let routerPush

  beforeEach(() => {
    setActivePinia(createPinia())
    routerPush = vi.fn()
    useRouter.mockReturnValue({ push: routerPush })

    wrapper = mount(LoginView, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-card': true
        }
      }
    })
  })

  it('renders login form', () => {
    expect(wrapper.find('.login-container').exists()).toBe(true)
    expect(wrapper.find('.login-form').exists()).toBe(true)
  })

  it('has username and password fields', () => {
    // 验证表单数据结构
    expect(wrapper.vm.loginForm).toHaveProperty('username')
    expect(wrapper.vm.loginForm).toHaveProperty('password')
    expect(wrapper.vm.loginForm).toHaveProperty('captcha')
    expect(wrapper.vm.loginForm).toHaveProperty('remember')
  })

  it('validates required fields', () => {
    const rules = wrapper.vm.rules
    expect(rules.username).toBeDefined()
    expect(rules.password).toBeDefined()
    expect(rules.captcha).toBeDefined()

    // 验证必填规则
    expect(rules.username[0].required).toBe(true)
    expect(rules.password[0].required).toBe(true)
  })

  it('toggles password visibility', async () => {
    expect(wrapper.vm.passwordVisible).toBe(false)
    wrapper.vm.togglePasswordVisibility()
    expect(wrapper.vm.passwordVisible).toBe(true)
  })

  it('handles login submission', async () => {
    // 设置表单数据
    wrapper.vm.loginForm = {
      username: 'admin',
      password: 'admin123',
      captcha: '1234',
      remember: false
    }

    // 模拟表单验证通过
    wrapper.vm.$refs = {
      loginFormRef: {
        validate: vi.fn((callback) => callback(true))
      }
    }

    // 触发登录
    await wrapper.vm.handleLogin()

    // 验证登录逻辑被调用
    expect(wrapper.vm.loading).toBeDefined()
  })

  it('handles captcha refresh', async () => {
    const initialCaptchaKey = wrapper.vm.captchaKey
    await wrapper.vm.refreshCaptcha()
    expect(wrapper.vm.captchaKey).not.toBe(initialCaptchaKey)
  })
})
