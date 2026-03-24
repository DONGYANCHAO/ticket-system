import request from './request'

// 获取报表列表
export function getReportList(params) {
  return request({
    url: '/api/report/list',
    method: 'get',
    params
  })
}

// 获取报表详情
export function getReportDetail(id) {
  return request({
    url: `/api/report/${id}`,
    method: 'get'
  })
}

// 创建报表
export function createReport(data) {
  return request({
    url: '/api/report',
    method: 'post',
    data
  })
}

// 更新报表
export function updateReport(id, data) {
  return request({
    url: `/api/report/${id}`,
    method: 'put',
    data
  })
}

// 删除报表
export function deleteReport(id) {
  return request({
    url: `/api/report/${id}`,
    method: 'delete'
  })
}

// 执行报表
export function executeReport(id, params) {
  return request({
    url: `/api/report/${id}/execute`,
    method: 'get',
    params
  })
}

// 导出报表
export function exportReport(id, params) {
  return request({
    url: `/api/report/${id}/export`,
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取订阅列表
export function getSubscriptionList() {
  return request({
    url: '/api/report/subscription/list',
    method: 'get'
  })
}

// 创建订阅
export function createSubscription(data) {
  return request({
    url: '/api/report/subscription',
    method: 'post',
    data
  })
}

// 更新订阅
export function updateSubscription(id, data) {
  return request({
    url: `/api/report/subscription/${id}`,
    method: 'put',
    data
  })
}

// 删除订阅
export function deleteSubscription(id) {
  return request({
    url: `/api/report/subscription/${id}`,
    method: 'delete'
  })
}

// 获取报表统计数据
export function getTicketStatistics(params) {
  return request({
    url: '/api/report/statistics/ticket',
    method: 'get',
    params
  })
}

// 获取客服绩效数据
export function getAgentPerformance(params) {
  return request({
    url: '/api/report/statistics/agent',
    method: 'get',
    params
  })
}

// 获取SLA统计数据
export function getSlaStatistics(params) {
  return request({
    url: '/api/report/statistics/sla',
    method: 'get',
    params
  })
}

// 获取客户分析数据
export function getCustomerAnalysis(params) {
  return request({
    url: '/api/report/statistics/customer',
    method: 'get',
    params
  })
}

// 获取知识库使用数据
export function getKnowledgeStatistics(params) {
  return request({
    url: '/api/report/statistics/knowledge',
    method: 'get',
    params
  })
}
