import request from './request'

// 获取知识库文章列表
export function getArticleList(params) {
  return request({
    url: '/api/knowledge/list',
    method: 'get',
    params
  })
}

// 获取文章详情
export function getArticleDetail(id) {
  return request({
    url: `/api/knowledge/${id}`,
    method: 'get'
  })
}

// 创建文章
export function createArticle(data) {
  return request({
    url: '/api/knowledge',
    method: 'post',
    data
  })
}

// 更新文章
export function updateArticle(id, data) {
  return request({
    url: `/api/knowledge/${id}`,
    method: 'put',
    data
  })
}

// 删除文章
export function deleteArticle(id) {
  return request({
    url: `/api/knowledge/${id}`,
    method: 'delete'
  })
}

// 发布文章
export function publishArticle(id) {
  return request({
    url: `/api/knowledge/${id}/publish`,
    method: 'post'
  })
}

// 下架文章
export function unpublishArticle(id) {
  return request({
    url: `/api/knowledge/${id}/unpublish`,
    method: 'post'
  })
}

// 获取文章分类树
export function getCategoryTree() {
  return request({
    url: '/api/knowledge/categories',
    method: 'get'
  })
}

// 创建分类
export function createCategory(data) {
  return request({
    url: '/api/knowledge/category',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(id, data) {
  return request({
    url: `/api/knowledge/category/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/api/knowledge/category/${id}`,
    method: 'delete'
  })
}

// 获取文章标签
export function getTags() {
  return request({
    url: '/api/knowledge/tags',
    method: 'get'
  })
}

// 搜索文章
export function searchArticles(params) {
  return request({
    url: '/api/knowledge/search',
    method: 'get',
    params
  })
}

// AI智能推荐
export function aiRecommend(data) {
  return request({
    url: '/api/knowledge/ai/recommend',
    method: 'post',
    data
  })
}

// 标记文章有用/无用
export function rateArticle(id, helpful) {
  return request({
    url: `/api/knowledge/${id}/rate`,
    method: 'post',
    data: { helpful }
  })
}

// 获取推荐文章
export function getRecommendedArticles(params) {
  return request({
    url: '/api/knowledge/recommended',
    method: 'get',
    params
  })
}

// 导入文章
export function importArticles(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/knowledge/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出文章
export function exportArticles(params) {
  return request({
    url: '/api/knowledge/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
