import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { vi } from 'vitest'
import Empty from '../Empty.vue'

// 模拟 Element Plus 图标
vi.mock('@element-plus/icons-vue', () => ({
  Folder: 'Folder'
}))

describe('Empty Component', () => {
  it('renders with default props', () => {
    const wrapper = mount(Empty, {
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })
    expect(wrapper.find('.empty-state').exists()).toBe(true)
    expect(wrapper.text()).toContain('暂无数据')
  })

  it('renders with custom title', () => {
    const wrapper = mount(Empty, {
      props: {
        title: '自定义标题'
      },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })
    expect(wrapper.text()).toContain('自定义标题')
  })

  it('renders with custom description', () => {
    const wrapper = mount(Empty, {
      props: {
        description: '自定义空状态描述'
      },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })
    expect(wrapper.text()).toContain('自定义空状态描述')
  })

  it('renders action slot content', () => {
    const wrapper = mount(Empty, {
      slots: {
        action: '<button>操作按钮</button>'
      },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })
    expect(wrapper.find('button').exists()).toBe(true)
    expect(wrapper.text()).toContain('操作按钮')
  })
})
