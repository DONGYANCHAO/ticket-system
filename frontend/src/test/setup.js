import { config } from '@vue/test-utils'
import { vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

config.global.plugins = []

vi.mock('element-plus', () => ({
  default: {
    install: vi.fn()
  },
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  },
  ElMessageBox: {
    confirm: vi.fn().mockResolvedValue(true),
    alert: vi.fn().mockResolvedValue(true),
    prompt: vi.fn().mockResolvedValue({ value: 'test' })
  },
  ElNotification: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  }
}))

vi.mock('@/api/auth', () => ({
  getUserInfo: vi.fn().mockResolvedValue({ data: { id: 1, username: 'test', role: 'ADMIN' } }),
  login: vi.fn().mockResolvedValue({ data: { accessToken: 'test-token', refreshToken: 'test-refresh' } }),
  logout: vi.fn().mockResolvedValue({}),
  refreshToken: vi.fn().mockResolvedValue({ data: { accessToken: 'new-token' } })
}))

vi.mock('@/utils/auth', () => ({
  getToken: vi.fn(() => 'test-token'),
  setToken: vi.fn(),
  removeToken: vi.fn(),
  getRefreshToken: vi.fn(() => 'test-refresh'),
  setRefreshToken: vi.fn(),
  removeRefreshToken: vi.fn()
}))

global.ResizeObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn()
}))

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn()
  }))
})

const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
Object.defineProperty(window, 'localStorage', { value: localStorageMock })

beforeEach(() => {
  setActivePinia(createPinia())
  vi.clearAllMocks()
})
