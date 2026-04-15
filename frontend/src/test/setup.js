import { config } from '@vue/test-utils'
import { vi } from 'vitest'

// 全局配置 Vue Test Utils
config.global.stubs = {
  // 忽略 Element Plus 组件的渲染
  'el-button': true,
  'el-input': true,
  'el-form': true,
  'el-form-item': true,
  'el-table': true,
  'el-table-column': true,
  'el-pagination': true,
  'el-dialog': true,
  'el-select': true,
  'el-option': true,
  'el-date-picker': true,
  'el-tag': true,
  'el-card': true,
  'el-row': true,
  'el-col': true,
  'el-menu': true,
  'el-menu-item': true,
  'el-sub-menu': true,
  'el-breadcrumb': true,
  'el-breadcrumb-item': true,
  'el-dropdown': true,
  'el-dropdown-menu': true,
  'el-dropdown-item': true,
  'el-avatar': true,
  'el-badge': true,
  'el-tooltip': true,
  'el-popover': true,
  'el-tabs': true,
  'el-tab-pane': true,
  'el-steps': true,
  'el-step': true,
  'el-upload': true,
  'el-icon': true
}

// 模拟 window 对象
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

// 模拟 IntersectionObserver
class IntersectionObserverMock {
  constructor(callback) {
    this.callback = callback
  }
  observe() { }
  unobserve() { }
  disconnect() { }
}

Object.defineProperty(window, 'IntersectionObserver', {
  writable: true,
  value: IntersectionObserverMock
})

// 模拟 ResizeObserver
class ResizeObserverMock {
  constructor(callback) {
    this.callback = callback
  }
  observe() { }
  unobserve() { }
  disconnect() { }
}

Object.defineProperty(window, 'ResizeObserver', {
  writable: true,
  value: ResizeObserverMock
})

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

// 模拟 sessionStorage
const sessionStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock
})

// 模拟 location
Object.defineProperty(window, 'location', {
  value: {
    href: 'http://localhost:3000',
    pathname: '/',
    search: '',
    hash: ''
  },
  writable: true
})
