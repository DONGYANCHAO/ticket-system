import { describe, it, expect, vi, beforeEach } from 'vitest'

vi.mock('@/utils/auth', () => ({
  getToken: vi.fn(() => 'test-token'),
  setToken: vi.fn(),
  removeToken: vi.fn()
}))

vi.mock('@/router', () => ({
  default: {
    push: vi.fn()
  }
}))

describe('API Request Module', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should have correct base configuration', async () => {
    const { default: request } = await import('@/api/request')
    
    expect(request.defaults.timeout).toBe(30000)
    expect(request.defaults.headers['Content-Type']).toBe('application/json;charset=UTF-8')
  })

  it('should have interceptors configured', async () => {
    const { default: request } = await import('@/api/request')
    
    expect(request.interceptors.request).toBeDefined()
    expect(request.interceptors.response).toBeDefined()
  })
})
