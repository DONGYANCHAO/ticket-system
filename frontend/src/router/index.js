import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/user'

NProgress.configure({ showSpinner: false })

// 路由白名单
const whiteList = ['/login', '/404', '/403']

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer' }
      },
      {
        path: '/ticket',
        name: 'Ticket',
        redirect: '/ticket/list',
        meta: { title: '工单管理', icon: 'Ticket' },
        children: [
          {
            path: '/ticket/list',
            name: 'TicketList',
            component: () => import('@/views/ticket/list.vue'),
            meta: { title: '工单列表' }
          },
          {
            path: '/ticket/create',
            name: 'TicketCreate',
            component: () => import('@/views/ticket/create.vue'),
            meta: { title: '创建工单' }
          },
          {
            path: '/ticket/detail/:id',
            name: 'TicketDetail',
            component: () => import('@/views/ticket/detail.vue'),
            meta: { title: '工单详情', hidden: true }
          }
        ]
      },
      {
        path: '/customer',
        name: 'Customer',
        component: () => import('@/views/customer/index.vue'),
        meta: { title: '客户管理', icon: 'OfficeBuilding' },
        hidden: true
      },
      {
        path: '/knowledge',
        name: 'Knowledge',
        component: () => import('@/views/knowledge/index.vue'),
        meta: { title: '知识库', icon: 'Reading' },
        hidden: true
      },
      {
        path: '/report',
        name: 'Report',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '报表中心', icon: 'DataAnalysis' },
        hidden: true
      },
      {
        path: '/system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting' },
        hidden: true,
        children: [
          {
            path: '/system/user',
            name: 'SystemUser',
            component: () => import('@/views/system/user.vue'),
            meta: { title: '用户管理' }
          },
          {
            path: '/system/module',
            name: 'SystemModule',
            component: () => import('@/views/system/module.vue'),
            meta: { title: '模块管理' }
          },
          {
            path: '/system/config',
            name: 'SystemConfig',
            component: () => import('@/views/system/config.vue'),
            meta: { title: '系统配置' }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    hidden: true
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  const userStore = useUserStore()
  const hasToken = !!userStore.token

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      if (!userStore.userInfo) {
        try {
          await userStore.getUserInfo()
        } catch (error) {
          userStore.logout()
          next(`/login?redirect=${to.path}`)
        }
      }
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
