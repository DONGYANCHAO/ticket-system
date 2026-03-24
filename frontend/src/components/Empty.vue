<template>
  <div class="empty-state">
    <div class="empty-icon">
      <el-icon><component :is="icon" /></el-icon>
    </div>
    <div class="empty-title">{{ title }}</div>
    <div class="empty-description" v-if="description">{{ description }}</div>
    <div class="empty-action" v-if="$slots.action">
      <slot name="action" />
    </div>
  </div>
</template>

<script setup>
import { Folder } from '@element-plus/icons-vue'

defineProps({
  icon: {
    type: [String, Object],
    default: () => Folder
  },
  title: {
    type: String,
    default: '暂无数据'
  },
  description: {
    type: String,
    default: ''
  }
})
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;

  .empty-icon {
    font-size: 64px;
    color: #c0c4cc;
    margin-bottom: 16px;
  }

  .empty-title {
    font-size: 16px;
    color: #606266;
    margin-bottom: 8px;
  }

  .empty-description {
    font-size: 14px;
    color: #909399;
    margin-bottom: 16px;
    max-width: 400px;
  }

  .empty-action {
    margin-top: 8px;
  }
}
</style>
