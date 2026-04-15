import { config } from '@vue/test-utils'

global.ResizeObserver = class ResizeObserver {
  constructor(callback) {}
  observe() {}
  unobserve() {}
  disconnect() {}
}

global.IntersectionObserver = class IntersectionObserver {
  constructor() {}
  observe() {}
  unobserve() {}
  disconnect() {}
}

config.global.mocks = {
  $t: (key) => key
}

vi.stubGlobal('defineOptions', (options) => options)

document.execCommand = vi.fn()
