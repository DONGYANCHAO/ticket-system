import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getUserInfo, login as loginApi, logout as logoutApi, refreshToken } from '@/api/auth'
import { removeToken, setToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const roles = ref([])

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => roles.value.includes('ADMIN'))
  const isSupport = computed(() => roles.value.includes('SUPPORT'))
  const isCs = computed(() => roles.value.includes('CS'))
  const isCustomer = computed(() => roles.value.includes('CUSTOMER'))

  // 登录
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.accessToken
    setToken(res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    return res
  }

  // 获取用户信息
  async function getUserInfoAction() {
    const res = await getUserInfo()
    userInfo.value = res.data
    roles.value = [res.data.role]
    return res
  }

  // 登出
  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = null
      roles.value = []
      removeToken()
      localStorage.removeItem('refreshToken')
    }
  }

  // 刷新Token
  async function refreshTokenAction() {
    const refreshTokenValue = localStorage.getItem('refreshToken')
    if (!refreshTokenValue) {
      return Promise.reject(new Error('No refresh token'))
    }
    try {
      const res = await refreshToken({ refreshToken: refreshTokenValue })
      token.value = res.data.accessToken
      setToken(res.data.accessToken)
      return res
    } catch (error) {
      logout()
      return Promise.reject(error)
    }
  }

  // 设置Token
  function setAccessToken(accessToken) {
    token.value = accessToken
    setToken(accessToken)
  }

  return {
    token,
    userInfo,
    roles,
    isLoggedIn,
    isAdmin,
    isSupport,
    isCs,
    isCustomer,
    login,
    getUserInfo: getUserInfoAction,
    logout,
    refreshToken: refreshTokenAction,
    setAccessToken
  }
})
