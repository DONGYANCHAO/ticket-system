import request from './request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 刷新Token
 */
export function refreshToken(data) {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return request({
    url: '/auth/password/change',
    method: 'post',
    data
  })
}

/**
 * 忘记密码
 */
export function forgotPassword(data) {
  return request({
    url: '/auth/password/forgot',
    method: 'post',
    data
  })
}

/**
 * 重置密码
 */
export function resetPassword(data) {
  return request({
    url: '/auth/password/reset',
    method: 'post',
    data
  })
}
