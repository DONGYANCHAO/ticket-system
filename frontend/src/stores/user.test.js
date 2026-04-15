import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from '@/stores/user'

vi.mock('@/api/auth', () => ({
  getUserInfo: vi.fn().mockResolvedValue({ data: { id: 1, username: 'test', role: 'ADMIN' } }),
  login: vi.fn().mockResolvedValue({ data: { accessToken: 'test-token', refreshToken: 'test-refresh' } }),
  logout: vi.fn().mockResolvedValue({}),
  refreshToken: vi.fn().mockResolvedValue({ data: { accessToken: 'new-token' } })
}))

vi.mock('@/utils/auth', () => ({
  getToken: vi.fn(() => ''),
  setToken: vi.fn(),
  removeToken: vi.fn(),
  getRefreshToken: vi.fn(() => ''),
  setRefreshToken: vi.fn(),
  removeRefreshToken: vi.fn()
}))

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should initialize with default state', () => {
    const store = useUserStore()

    expect(store.token).toBe('')
    expect(store.userInfo).toBeNull()
    expect(store.roles).toEqual([])
    expect(store.isLoggedIn).toBe(false)
  })

  it('should check if user is admin', () => {
    const store = useUserStore()
    
    expect(store.isAdmin).toBe(false)
    
    store.roles = ['ADMIN']
    expect(store.isAdmin).toBe(true)
  })

  it('should check if user is logged in', () => {
    const store = useUserStore()
    
    expect(store.isLoggedIn).toBe(false)
    
    store.token = 'some-token'
    expect(store.isLoggedIn).toBe(true)
  })

  it('should have correct role checks', () => {
    const store = useUserStore()
    
    store.roles = ['SUPPORT']
    expect(store.isSupport).toBe(true)
    expect(store.isAdmin).toBe(false)
    
    store.roles = ['CS']
    expect(store.isCs).toBe(true)
    
    store.roles = ['CUSTOMER']
    expect(store.isCustomer).toBe(true)
  })
})
