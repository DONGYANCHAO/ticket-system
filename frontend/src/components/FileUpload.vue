<template>
  <div class="file-upload">
    <el-upload
      ref="uploadRef"
      :action="action"
      :headers="headers"
      :data="data"
      :name="name"
      :accept="accept"
      :multiple="multiple"
      :limit="limit"
      :drag="drag"
      :disabled="disabled"
      :auto-upload="autoUpload"
      :show-file-list="showFileList"
      :file-list="fileList"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-progress="handleProgress"
      :on-change="handleChange"
      :on-exceed="handleExceed"
      :before-upload="beforeUpload"
      :before-remove="beforeRemove"
    >
      <template v-if="drag">
        <el-icon class="el-icon-upload">
          <Upload />
        </el-icon>
        <div class="el-upload__text">
          拖拽文件到此处，或<em>点击上传</em>
        </div>
      </template>
      <template v-else>
        <slot>
          <el-button type="primary" :disabled="disabled">
            <el-icon><Upload /></el-icon>
            {{ buttonText }}
          </el-button>
        </slot>
      </template>
      <template #tip>
        <div v-if="tip" class="el-upload__tip">
          {{ tip }}
        </div>
      </template>
    </el-upload>

    <!-- 上传进度 -->
    <div v-if="uploading" class="upload-progress">
      <el-progress :percentage="percentage" :status="progressStatus" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  action: {
    type: String,
    default: '/api/upload'
  },
  headers: {
    type: Object,
    default: () => ({})
  },
  data: {
    type: Object,
    default: () => ({})
  },
  name: {
    type: String,
    default: 'file'
  },
  accept: {
    type: String,
    default: ''
  },
  multiple: {
    type: Boolean,
    default: true
  },
  limit: {
    type: Number,
    default: 10
  },
  drag: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  autoUpload: {
    type: Boolean,
    default: true
  },
  showFileList: {
    type: Boolean,
    default: true
  },
  fileList: {
    type: Array,
    default: () => []
  },
  buttonText: {
    type: String,
    default: '上传文件'
  },
  tip: {
    type: String,
    default: ''
  },
  maxSize: {
    type: Number,
    default: 20 // MB
  },
  fileTypes: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits([
  'update:fileList',
  'preview',
  'remove',
  'success',
  'error',
  'progress',
  'change',
  'exceed'
])

const uploadRef = ref(null)
const uploading = ref(false)
const percentage = ref(0)
const progressStatus = ref('')

const handlePreview = (file) => {
  emit('preview', file)
}

const handleRemove = (file, fileList) => {
  emit('update:fileList', fileList)
  emit('remove', file, fileList)
}

const handleSuccess = (response, file, fileList) => {
  emit('update:fileList', fileList)
  emit('success', response, file, fileList)
}

const handleError = (error, file, fileList) => {
  uploading.value = false
  percentage.value = 0
  ElMessage.error('上传失败')
  emit('error', error, file, fileList)
}

const handleProgress = (event, file, fileList) => {
  uploading.value = true
  percentage.value = Math.round(event.percent) || 0
  emit('progress', event, file, fileList)
}

const handleChange = (file, fileList) => {
  emit('update:fileList', fileList)
  emit('change', file, fileList)
}

const handleExceed = (files, fileList) => {
  ElMessage.warning(`最多上传 ${props.limit} 个文件`)
  emit('exceed', files, fileList)
}

const beforeUpload = (file) => {
  // 检查文件大小
  const isLtMaxSize = file.size / 1024 / 1024 < props.maxSize
  if (!isLtMaxSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }

  // 检查文件类型
  if (props.fileTypes.length > 0) {
    const fileExt = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase()
    const isValidType = props.fileTypes.some(type => type.toLowerCase() === fileExt)
    if (!isValidType) {
      ElMessage.error(`文件类型必须是 ${props.fileTypes.join('、')} 中的一个`)
      return false
    }
  }

  uploading.value = true
  return true
}

const beforeRemove = (file, fileList) => {
  return new Promise((resolve) => {
    if (file.status === 'uploading') {
      ElMessageBox.confirm('文件正在上传，确定要删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        uploading.value = false
        resolve(true)
      }).catch(() => {
        resolve(false)
      })
    } else {
      resolve(true)
    }
  })
}

// 暴露方法
defineExpose({
  upload: () => uploadRef.value?.submit(),
  abort: () => uploadRef.value?.abort(),
  clearFiles: () => uploadRef.value?.clearFiles(),
  handleStart: (file) => uploadRef.value?.handleStart(file),
  handleRemove: (file) => uploadRef.value?.handleRemove(file)
})
</script>

<style lang="scss" scoped>
.file-upload {
  .upload-progress {
    margin-top: 16px;
  }
}
</style>
