<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409EFF;">
            <el-icon><Ticket /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.todayTickets }}</div>
            <div class="stat-label">今日新增</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #E6A23C;">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.processingTickets }}</div>
            <div class="stat-label">处理中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67C23A;">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.solvedTickets }}</div>
            <div class="stat-label">已解决</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #F56C6C;">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.slaWarning }}</div>
            <div class="stat-label">SLA预警</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作和待办事项 -->
    <el-row :gutter="16" class="main-content">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>工单趋势（近7天）</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="todo-card">
          <template #header>
            <div class="card-header">
              <span>我的待办</span>
              <el-button type="primary" link @click="$router.push('/ticket')">
                查看全部 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="todo-list">
            <el-empty v-if="todoList.length === 0" description="暂无待办事项" />
            <div v-for="item in todoList" :key="item.id" class="todo-item" @click="goToTicket(item.id)">
              <el-tag :type="getPriorityType(item.priority)" size="small">{{ getPriorityText(item.priority) }}</el-tag>
              <span class="todo-title">{{ item.title }}</span>
              <span class="todo-customer">{{ item.customerName }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分布统计 -->
    <el-row :gutter="16" class="distribution-section">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>工单类型分布</span>
          </template>
          <div ref="typeChartRef" class="chart-container-small"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>处理人分布</span>
          </template>
          <div ref="handlerChartRef" class="chart-container-small"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>满意度分布</span>
          </template>
          <div ref="satisfactionChartRef" class="chart-container-small"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const router = useRouter()

const trendChartRef = ref(null)
const typeChartRef = ref(null)
const handlerChartRef = ref(null)
const satisfactionChartRef = ref(null)

let trendChart = null
let typeChart = null
let handlerChart = null
let satisfactionChart = null

const statistics = reactive({
  todayTickets: 0,
  processingTickets: 0,
  solvedTickets: 0,
  slaWarning: 0
})

const todoList = ref([])

// 初始化图表
const initCharts = () => {
  // 趋势图
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增', '解决'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value' },
    series: [
      { name: '新增', type: 'line', data: [12, 8, 15, 10, 18, 5, 3], smooth: true },
      { name: '解决', type: 'line', data: [10, 12, 8, 14, 16, 6, 4], smooth: true }
    ]
  })

  // 类型分布
  typeChart = echarts.init(typeChartRef.value)
  typeChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14 } },
      data: [
        { value: 45, name: 'Bug', itemStyle: { color: '#F56C6C' } },
        { value: 30, name: '咨询', itemStyle: { color: '#409EFF' } },
        { value: 15, name: '需求', itemStyle: { color: '#67C23A' } },
        { value: 10, name: '投诉', itemStyle: { color: '#E6A23C' } }
      ]
    }]
  })

  // 处理人分布
  handlerChart = echarts.init(handlerChartRef.value)
  handlerChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: ['张三', '李四', '王五', '赵六'] },
    series: [{
      type: 'bar',
      data: [12, 8, 15, 5],
      itemStyle: { color: '#409EFF', borderRadius: [0, 4, 4, 0] }
    }]
  })

  // 满意度分布
  satisfactionChart = echarts.init(satisfactionChartRef.value)
  satisfactionChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '70%',
      data: [
        { value: 60, name: '满意', itemStyle: { color: '#67C23A' } },
        { value: 30, name: '一般', itemStyle: { color: '#E6A23C' } },
        { value: 10, name: '不满意', itemStyle: { color: '#F56C6C' } }
      ]
    }]
  })
}

// 加载统计数据
const loadStatistics = async () => {
  // 模拟数据
  statistics.todayTickets = 5
  statistics.processingTickets = 12
  statistics.solvedTickets = 28
  statistics.slaWarning = 2

  // 模拟待办事项
  todoList.value = [
    { id: 1, title: '用户无法登录系统', priority: 'URGENT', customerName: '张三' },
    { id: 2, title: '报表导出功能异常', priority: 'HIGH', customerName: '李四' },
    { id: 3, title: '数据同步延迟', priority: 'MEDIUM', customerName: '王五' }
  ]
}

// 跳转到工单详情
const goToTicket = (id) => {
  router.push(`/ticket/detail/${id}`)
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const types = { URGENT: 'danger', HIGH: 'warning', MEDIUM: 'info', LOW: '' }
  return types[priority] || ''
}

// 获取优先级文本
const getPriorityText = (priority) => {
  const texts = { URGENT: '紧急', HIGH: '高', MEDIUM: '中', LOW: '低' }
  return texts[priority] || priority
}

// 窗口调整时重绘图表
const handleResize = () => {
  trendChart?.resize()
  typeChart?.resize()
  handlerChart?.resize()
  satisfactionChart?.resize()
}

onMounted(() => {
  loadStatistics()
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  typeChart?.dispose()
  handlerChart?.dispose()
  satisfactionChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 0;
}

.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 16px;

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;

    .el-icon {
      font-size: 28px;
      color: #fff;
    }
  }

  .stat-content {
    .stat-value {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.main-content {
  margin-bottom: 16px;
}

.chart-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.chart-container {
  height: 300px;
}

.chart-container-small {
  height: 250px;
}

.todo-card {
  height: 100%;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.todo-list {
  max-height: 300px;
  overflow-y: auto;
}

.todo-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
    margin: 0 -20px;
    padding-left: 20px;
    padding-right: 20px;
  }

  &:last-child {
    border-bottom: none;
  }

  .todo-title {
    flex: 1;
    margin-left: 12px;
    color: #303133;
    font-size: 14px;
  }

  .todo-customer {
    color: #909399;
    font-size: 12px;
    margin-left: 12px;
  }
}

.distribution-section {
  .el-card {
    height: 100%;
  }
}
</style>
