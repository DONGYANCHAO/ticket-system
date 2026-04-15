import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'

const EmptyComponent = {
  name: 'Empty',
  template: `
    <div class="empty-container">
      <div class="empty-description">{{ description }}</div>
    </div>
  `,
  props: {
    description: {
      type: String,
      default: '暂无数据'
    }
  }
}

describe('Empty Component', () => {
  let pinia
  let router

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)

    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/', component: { template: '<div>Home</div>' } }
      ]
    })
  })

  it('should render empty component', () => {
    const wrapper = mount(EmptyComponent, {
      global: {
        plugins: [pinia, router]
      }
    })

    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.empty-container').exists()).toBe(true)
  })

  it('should display default description', () => {
    const wrapper = mount(EmptyComponent, {
      global: {
        plugins: [pinia, router]
      }
    })

    expect(wrapper.text()).toContain('暂无数据')
  })

  it('should display custom description', () => {
    const description = 'No data available'
    const wrapper = mount(EmptyComponent, {
      props: {
        description
      },
      global: {
        plugins: [pinia, router]
      }
    })

    expect(wrapper.text()).toContain(description)
  })
})
