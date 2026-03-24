import request from './request'

// 获取工单列表
export function getTicketList(params) {
  return request({
    url: '/api/ticket/list',
    method: 'get',
    params
  })
}

// 获取工单详情
export function getTicketDetail(id) {
  return request({
    url: `/api/ticket/${id}`,
    method: 'get'
  })
}

// 创建工单
export function createTicket(data) {
  return request({
    url: '/api/ticket',
    method: 'post',
    data
  })
}

// 更新工单
export function updateTicket(id, data) {
  return request({
    url: `/api/ticket/${id}`,
    method: 'put',
    data
  })
}

// 删除工单
export function deleteTicket(id) {
  return request({
    url: `/api/ticket/${id}`,
    method: 'delete'
  })
}

// 分配工单
export function assignTicket(id, data) {
  return request({
    url: `/api/ticket/${id}/assign`,
    method: 'post',
    data
  })
}

// 关闭工单
export function closeTicket(id, data) {
  return request({
    url: `/api/ticket/${id}/close`,
    method: 'post',
    data
  })
}

// 合并工单
export function mergeTickets(data) {
  return request({
    url: '/api/ticket/merge',
    method: 'post',
    data
  })
}

// 拆分工单
export function splitTicket(id, data) {
  return request({
    url: `/api/ticket/${id}/split`,
    method: 'post',
    data
  })
}

// 获取工单消息列表
export function getTicketMessages(ticketId, params) {
  return request({
    url: `/api/ticket/${ticketId}/messages`,
    method: 'get',
    params
  })
}

// 发送工单消息
export function sendTicketMessage(ticketId, data) {
  return request({
    url: `/api/ticket/${ticketId}/messages`,
    method: 'post',
    data
  })
}

// 上传工单附件
export function uploadTicketAttachment(ticketId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/api/ticket/${ticketId}/attachments`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取工单变更历史
export function getTicketHistory(ticketId) {
  return request({
    url: `/api/ticket/${ticketId}/history`,
    method: 'get'
  })
}

// 保存工单草稿
export function saveTicketDraft(data) {
  return request({
    url: '/api/ticket/draft',
    method: 'post',
    data
  })
}

// 获取工单草稿
export function getTicketDraft(customerId) {
  return request({
    url: '/api/ticket/draft',
    method: 'get',
    params: { customerId }
  })
}

// 删除工单草稿
export function deleteTicketDraft(id) {
  return request({
    url: `/api/ticket/draft/${id}`,
    method: 'delete'
  })
}

// 催单
export function remindTicket(id) {
  return request({
    url: `/api/ticket/${id}/remind`,
    method: 'post'
  })
}

// 评价工单
export function rateTicket(id, data) {
  return request({
    url: `/api/ticket/${id}/rate`,
    method: 'post',
    data
  })
}

// 导出工单
export function exportTickets(params) {
  return request({
    url: '/api/ticket/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取待办工单
export function getTodoTickets() {
  return request({
    url: '/api/ticket/todo',
    method: 'get'
  })
}

// 获取我创建的工单
export function getMyCreatedTickets(params) {
  return request({
    url: '/api/ticket/my/created',
    method: 'get',
    params
  })
}

// 获取我负责的工单
export function getMyAssignedTickets(params) {
  return request({
    url: '/api/ticket/my/assigned',
    method: 'get',
    params
  })
}
