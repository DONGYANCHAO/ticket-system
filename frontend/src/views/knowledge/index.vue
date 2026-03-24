<template>
  <div class="knowledge-list">
    <!-- 搜索筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="请输入关键词搜索" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-cascader
            v-model="queryForm.categoryId"
            :options="categoryTree"
            :props="{ checkStrictly: true, label: 'name', value: 'id' }"
            placeholder="请选择分类"
            clearable
          />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="queryForm.tagIds" multiple placeholder="请选择标签" clearable>
            <el-option
              v-for="tag in tagList"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="可见性">
          <el-select v-model="queryForm.visibility" placeholder="请选择可见性" clearable>
            <el-option label="全部可见" value="ALL" />
            <el-option label="仅对内" value="INTERNAL" />
            <el-option label="对客户可见" value="EXTERNAL" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <div class="left">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon> 新建文章
        </el-button>
        <el-button @click="handleImport">
          <el-icon><Upload /></el-icon> 导入
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon> 导出
        </el-button>
      </div>
    </div>

    <!-- 文章列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="title" label="标题" min-width="250">
          <template #default="{ row }">
            <el-link type="primary" @click="goToDetail(row.id)">{{ row.title }}</el-link>
            <el-tag v-if="row.visibility === 'INTERNAL'" type="warning" size="small" effect="plain" style="margin-left: 8px">内部</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="tagNames" label="标签" width="200">
          <template #default="{ row }">
            <el-tag v-for="tag in row.tags" :key="tag.id" size="small" style="margin-right: 4px">
              {{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" sortable />
        <el-table-column prop="helpfulCount" label="有用" width="80" sortable />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToDetail(row.id)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-dropdown trigger="click">
              <el-button type="primary" link>更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleCopyLink(row)">复制链接</el-dropdown-item>
                  <el-dropdown-item @click="handlePublish(row)" v-if="row.status === 'DRAFT'">发布</el-dropdown-item>
                  <el-dropdown-item @click="handleArchive(row)" v-if="row.status === 'PUBLISHED'">归档</el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(row)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

const queryForm = reactive({
  keyword: '',
  categoryId: '',
  tagIds: [],
  visibility: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const categoryTree = ref([
  { id: 1, name: '产品使用', children: [{ id: 11, name: '快速入门' }, { id: 12, name: '常见问题' }] },
  { id: 2, name: '技术文档', children: [{ id: 21, name: 'API文档' }, { id: 22, name: '开发指南' }] },
  { id: 3, name: '最佳实践' }
])

const tagList = ref([
  { id: 1, name: '热门' },
  { id: 2, name: '推荐' },
  { id: 3, name: 'FAQ' }
])

const loadData = async () => {
  loading.value = true
  tableData.value = [
    {
      id: 1,
      title: '如何创建第一个工单',
      categoryName: '快速入门',
      tags: [{ id: 1, name: '热门' }, { id: 3, name: 'FAQ' }],
      viewCount: 1520,
      helpfulCount: 98,
      status: 'PUBLISHED',
      visibility: 'ALL',
      updateTime: '2024-03-20 14:30:00'
    },
    {
      id: 2,
      title: '系统API接口说明',
      categoryName: 'API文档',
      tags: [{ id: 2, name: '推荐' }],
      viewCount: 856,
      helpfulCount: 76,
      status: 'PUBLISHED',
      visibility: 'INTERNAL',
      updateTime: '2024-03-18 10:20:00'
    }
  ]
  pagination.total = 2
  loading.value = false
}

const handleQuery = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  Object.keys(queryForm).forEach(key => {
    queryForm[key] = key === 'tagIds' ? [] : ''
  })
  pagination.page = 1
  loadData()
}

const handleCreate = () => {
  router.push('/knowledge/create')
}

const handleEdit = (row) => {
  router.push(`/knowledge/edit/${row.id}`)
}

const goToDetail = (id) => {
  router.push(`/knowledge/detail/${id}`)
}

const handleCopyLink = (row) => {
  navigator.clipboard.writeText(`${window.location.origin}/knowledge/detail/${row.id}`)
  ElMessage.success('链接已复制')
}

const handlePublish = (row) => {
  ElMessage.success('文章已发布')
  loadData()
}

const handleArchive = (row) => {
  ElMessage.success('文章已归档')
  loadData()
}

const handleDelete = (row) => {
  ElMessage.success('文章已删除')
  loadData()
}

const handleImport = () => {
  ElMessage.info('导入功能开发中')
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleSelectionChange = (selection) => {
  console.log(selection)
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getStatusTagType = (status) => {
  const types = { DRAFT: 'info', PUBLISHED: 'success', ARCHIVED: '' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { DRAFT: '草稿', PUBLISHED: '已发布', ARCHIVED: '已归档' }
  return texts[status] || status
}

const formatDateTime = (datetime) => datetime

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.knowledge-list {
  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
