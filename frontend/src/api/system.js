import request from './request'

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/api/system/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/api/system/user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id) {
  return request({
    url: `/api/system/user/${id}/reset-password`,
    method: 'post'
  })
}

// 获取角色列表
export function getRoleList() {
  return request({
    url: '/api/system/role/list',
    method: 'get'
  })
}

// 获取角色详情
export function getRoleDetail(id) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/api/system/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'delete'
  })
}

// 获取角色权限
export function getRolePermissions(roleId) {
  return request({
    url: `/api/system/role/${roleId}/permissions`,
    method: 'get'
  })
}

// 分配角色权限
export function assignRolePermissions(roleId, data) {
  return request({
    url: `/api/system/role/${roleId}/permissions`,
    method: 'put',
    data
  })
}

// 获取模块列表
export function getModuleList() {
  return request({
    url: '/api/system/module/list',
    method: 'get'
  })
}

// 获取模块树
export function getModuleTree() {
  return request({
    url: '/api/system/module/tree',
    method: 'get'
  })
}

// 创建模块
export function createModule(data) {
  return request({
    url: '/api/system/module',
    method: 'post',
    data
  })
}

// 更新模块
export function updateModule(id, data) {
  return request({
    url: `/api/system/module/${id}`,
    method: 'put',
    data
  })
}

// 删除模块
export function deleteModule(id) {
  return request({
    url: `/api/system/module/${id}`,
    method: 'delete'
  })
}

// 获取SLA列表
export function getSlaList() {
  return request({
    url: '/api/system/sla/list',
    method: 'get'
  })
}

// 创建SLA
export function createSla(data) {
  return request({
    url: '/api/system/sla',
    method: 'post',
    data
  })
}

// 更新SLA
export function updateSla(id, data) {
  return request({
    url: `/api/system/sla/${id}`,
    method: 'put',
    data
  })
}

// 删除SLA
export function deleteSla(id) {
  return request({
    url: `/api/system/sla/${id}`,
    method: 'delete'
  })
}

// 获取快捷回复模板列表
export function getTemplateList(params) {
  return request({
    url: '/api/system/template/list',
    method: 'get',
    params
  })
}

// 创建快捷回复模板
export function createTemplate(data) {
  return request({
    url: '/api/system/template',
    method: 'post',
    data
  })
}

// 更新快捷回复模板
export function updateTemplate(id, data) {
  return request({
    url: `/api/system/template/${id}`,
    method: 'put',
    data
  })
}

// 删除快捷回复模板
export function deleteTemplate(id) {
  return request({
    url: `/api/system/template/${id}`,
    method: 'delete'
  })
}

// 获取钉钉配置
export function getDingtalkConfig() {
  return request({
    url: '/api/system/dingtalk/config',
    method: 'get'
  })
}

// 更新钉钉配置
export function updateDingtalkConfig(data) {
  return request({
    url: '/api/system/dingtalk/config',
    method: 'put',
    data
  })
}

// 测试钉钉连接
export function testDingtalkConnection() {
  return request({
    url: '/api/system/dingtalk/test',
    method: 'post'
  })
}

// 获取操作日志
export function getOperationLogList(params) {
  return request({
    url: '/api/system/log/list',
    method: 'get',
    params
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/api/system/user/current',
    method: 'get'
  })
}

// 更新当前用户信息
export function updateCurrentUser(data) {
  return request({
    url: '/api/system/user/current',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/api/system/user/change-password',
    method: 'post',
    data
  })
}

// 获取字典列表
export function getDictList(params) {
  return request({
    url: '/api/system/dict/list',
    method: 'get',
    params
  })
}
