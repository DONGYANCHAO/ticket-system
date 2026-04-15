import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import Empty from '../Empty.vue'

describe('Empty.vue', () => {
  it('renders default text correctly', () => {
    const wrapper = mount(Empty)
    expect(wrapper.text()).toContain('暂无数据')
  })

  it('renders custom description when prop is provided', () => {
    const wrapper = mount(Empty, {
      props: {
        description: '自定义空状态描述'
      }
    })
    expect(wrapper.text()).toContain('自定义空状态描述')
  })

  it('applies correct size class when size prop is set', () => {
    const wrapper = mount(Empty, {
      props: {
        size: 'small'
      }
    })
    expect(wrapper.find('.empty-container').classes()).toContain('small')
  })

  it('renders default slot content', () => {
    const wrapper = mount(Empty, {
      slots: {
        default: '<button class="custom-btn">重新加载</button>'
      }
    })
    expect(wrapper.find('.custom-btn').exists()).toBe(true)
  })
})
