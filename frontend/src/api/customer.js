import request from './request'

// 获取客户列表
export function getCustomerList(params) {
  return request({
    url: '/api/customer/list',
    method: 'get',
    params
  })
}

// 获取客户详情
export function getCustomerDetail(id) {
  return request({
    url: `/api/customer/${id}`,
    method: 'get'
  })
}

// 创建客户
export function createCustomer(data) {
  return request({
    url: '/api/customer',
    method: 'post',
    data
  })
}

// 更新客户
export function updateCustomer(id, data) {
  return request({
    url: `/api/customer/${id}`,
    method: 'put',
    data
  })
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/api/customer/${id}`,
    method: 'delete'
  })
}

// 获取客户联系人列表
export function getCustomerContacts(customerId) {
  return request({
    url: `/api/customer/${customerId}/contacts`,
    method: 'get'
  })
}

// 创建客户联系人
export function createContact(customerId, data) {
  return request({
    url: `/api/customer/${customerId}/contacts`,
    method: 'post',
    data
  })
}

// 更新客户联系人
export function updateContact(id, data) {
  return request({
    url: `/api/contact/${id}`,
    method: 'put',
    data
  })
}

// 删除客户联系人
export function deleteContact(id) {
  return request({
    url: `/api/contact/${id}`,
    method: 'delete'
  })
}

// 获取客户工单列表
export function getCustomerTickets(customerId, params) {
  return request({
    url: `/api/customer/${customerId}/tickets`,
    method: 'get',
    params
  })
}

// 获取客户统计信息
export function getCustomerStatistics(customerId) {
  return request({
    url: `/api/customer/${customerId}/statistics`,
    method: 'get'
  })
}

// 导入客户
export function importCustomers(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/customer/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出客户
export function exportCustomers(params) {
  return request({
    url: '/api/customer/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
