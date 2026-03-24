<template>
  <el-image
    :src="src"
    :alt="alt"
    :fit="fit"
    :lazy="lazy"
    :preview-src-list="previewList"
    :preview-teleported="previewTeleported"
    :class="['image-preview', { 'image-preview-circle': circle }]"
    @click="handleClick"
  >
    <template #error>
      <div class="image-error">
        <el-icon><Picture /></el-icon>
      </div>
    </template>
  </el-image>
</template>

<script setup>
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  fit: {
    type: String,
    default: 'cover'
  },
  lazy: {
    type: Boolean,
    default: true
  },
  circle: {
    type: Boolean,
    default: false
  },
  preview: {
    type: Boolean,
    default: true
  },
  previewSrcList: {
    type: Array,
    default: () => []
  },
  previewTeleported: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const previewList = computed(() => {
  if (props.previewSrcList.length > 0) {
    return props.previewSrcList
  }
  if (props.preview) {
    return [props.src]
  }
  return []
})

const handleClick = (e) => {
  emit('click', e)
}
</script>

<style lang="scss" scoped>
.image-preview {
  display: block;
  width: 100%;
  height: 100%;

  &.image-preview-circle {
    border-radius: 50%;
  }

  :deep(.el-image__inner) {
    width: 100%;
    height: 100%;
  }

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background: #f5f7fa;
    color: #909399;
    font-size: 24px;
  }
}
</style>
