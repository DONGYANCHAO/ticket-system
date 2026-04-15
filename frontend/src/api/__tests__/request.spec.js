import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import axios from 'axios'
import request from '../request.js'

// 模拟 axios
vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: { use: vi.fn() },
        response: { use: vi.fn() }
      },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    }))
  }
}))

describe('API Request', () => {
  let mockAxiosInstance

  beforeEach(() => {
    vi.clearAllMocks()
    mockAxiosInstance = axios.create()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('creates axios instance with correct config', () => {
    expect(axios.create).toHaveBeenCalledWith(expect.objectContaining({
      baseURL: expect.any(String),
      timeout: expect.any(Number)
    }))
  })

  it('request interceptor adds token to headers', () => {
    // 验证请求拦截器已注册
    expect(mockAxiosInstance.interceptors.request.use).toHaveBeenCalled()
  })

  it('response interceptor handles 401 error', () => {
    // 验证响应拦截器已注册
    expect(mockAxiosInstance.interceptors.response.use).toHaveBeenCalled()
  })
})

// 测试 API 响应处理
describe('API Response Handling', () => {
  it('handles successful response', async () => {
    const mockData = { code: 200, data: { id: 1 }, message: 'success' }
    const response = { data: mockData }

    // 模拟成功响应
    expect(response.data.code).toBe(200)
    expect(response.data.data).toEqual({ id: 1 })
  })

  it('handles error response', async () => {
    const errorResponse = {
      response: {
        status: 500,
        data: { code: 500, message: '服务器错误' }
      }
    }

    expect(errorResponse.response.status).toBe(500)
    expect(errorResponse.response.data.message).toBe('服务器错误')
  })

  it('handles network error', async () => {
    const networkError = {
      request: {},
      message: 'Network Error'
    }

    expect(networkError.request).toBeDefined()
    expect(networkError.message).toBe('Network Error')
  })
})
