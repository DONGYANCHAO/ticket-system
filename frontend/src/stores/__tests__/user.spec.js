import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../user.js'

// 模拟 localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock
})

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('initializes with default state', () => {
    const store = useUserStore()
    expect(store.token).toBeNull()
    expect(store.userInfo).toBeNull()
    expect(store.isLoggedIn).toBe(false)
  })

  it('sets token correctly', () => {
    const store = useUserStore()
    store.setToken('test-token')
    expect(store.token).toBe('test-token')
    expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'test-token')
  })

  it('sets user info correctly', () => {
    const store = useUserStore()
    const userInfo = {
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    }
    store.setUserInfo(userInfo)
    expect(store.userInfo).toEqual(userInfo)
    expect(localStorageMock.setItem).toHaveBeenCalledWith('userInfo', JSON.stringify(userInfo))
  })

  it('clears user data on logout', () => {
    const store = useUserStore()
    store.setToken('test-token')
    store.setUserInfo({ id: 1, username: 'test' })

    store.logout()

    expect(store.token).toBeNull()
    expect(store.userInfo).toBeNull()
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('token')
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('userInfo')
  })

  it('initializes from localStorage', () => {
    localStorageMock.getItem.mockImplementation((key) => {
      if (key === 'token') return 'stored-token'
      if (key === 'userInfo') return JSON.stringify({ id: 1, username: 'stored' })
      return null
    })

    const store = useUserStore()
    store.initFromStorage()

    expect(store.token).toBe('stored-token')
    expect(store.userInfo).toEqual({ id: 1, username: 'stored' })
  })

  it('computed isLoggedIn returns correct value', () => {
    const store = useUserStore()
    expect(store.isLoggedIn).toBe(false)

    store.setToken('test-token')
    expect(store.isLoggedIn).toBe(true)

    store.logout()
    expect(store.isLoggedIn).toBe(false)
  })
})
