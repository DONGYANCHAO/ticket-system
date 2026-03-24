import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data

    // 业务状态码判断
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '操作失败',
        type: 'error',
        duration: 3000
      })

      // Token过期
      if (res.code === 401) {
        ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeToken()
          router.push('/login')
        })
      }

      return Promise.reject(new Error(res.message || '操作失败'))
    }

    return res
  },
  (error) => {
    console.error('响应错误:', error)

    let message = '网络错误，请稍后重试'

    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = error.response.data?.message || '请求参数错误'
          break
        case 401:
          message = '登录已过期，请重新登录'
          removeToken()
          router.push('/login')
          break
        case 403:
          message = '无权限访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器错误，请稍后重试'
          break
        default:
          message = error.response.data?.message || '请求失败'
      }
    }

    ElMessage({
      message,
      type: 'error',
      duration: 3000
    })

    return Promise.reject(error)
  }
)

export default service
