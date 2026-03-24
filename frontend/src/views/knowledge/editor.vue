<template>
  <div class="knowledge-editor">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <span>{{ isEdit ? '编辑文章' : '新建文章' }}</span>
          <div>
            <el-button @click="handlePreview">预览</el-button>
            <el-button @click="handleSaveDraft" :loading="saving">保存草稿</el-button>
            <el-button type="primary" @click="handlePublish" :loading="publishing">
              {{ articleForm.status === 'PUBLISHED' ? '更新' : '发布' }}
            </el-button>
          </div>
        </div>
      </template>

      <div class="editor-container">
        <!-- 左侧编辑区 -->
        <div class="editor-main">
          <el-form ref="formRef" :model="articleForm" :rules="rules" label-width="100px">
            <el-form-item label="文章标题" prop="title" required>
              <el-input
                v-model="articleForm.title"
                placeholder="请输入文章标题"
                maxlength="100"
                show-word-limit
                class="title-input"
              />
            </el-form-item>

            <el-form-item label="文章内容" prop="content" required>
              <div class="editor-toolbar">
                <el-button-group>
                  <el-button @click="execCommand('bold')"><el-icon><Bold /></el-icon></el-button>
                  <el-button @click="execCommand('italic')"><el-icon><Italic /></el-icon></el-button>
                  <el-button @click="execCommand('underline')"><el-icon><Underline /></el-icon></el-button>
                </el-button-group>
                <el-divider direction="vertical" />
                <el-button-group>
                  <el-button @click="execCommand('insertUnorderedList')"><el-icon><List /></el-icon></el-button>
                  <el-button @click="execCommand('insertOrderedList')"><el-icon><List /></el-icon></el-button>
                </el-button-group>
                <el-divider direction="vertical" />
                <el-button-group>
                  <el-button @click="insertLink">链接</el-button>
                  <el-button @click="insertImage">图片</el-button>
                  <el-button @click="insertTable">表格</el-button>
                </el-button-group>
                <el-divider direction="vertical" />
                <el-button-group>
                  <el-button @click="insertCodeBlock">代码块</el-button>
                  <el-button @click="insertQuote">引用</el-button>
                </el-button-group>
              </div>
              <div
                ref="editorRef"
                class="editor-content"
                contenteditable="true"
                @input="handleEditorInput"
              ></div>
            </el-form-item>
          </el-form>
        </div>

        <!-- 右侧设置区 -->
        <div class="editor-side">
          <el-card>
            <template #header>
              <span>基本信息</span>
            </template>
            <el-form label-width="80px" label-position="left">
              <el-form-item label="分类">
                <el-cascader
                  v-model="articleForm.categoryId"
                  :options="categoryTree"
                  :props="{ checkStrictly: true, label: 'name', value: 'id' }"
                  placeholder="请选择分类"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="标签">
                <el-select
                  v-model="articleForm.tagIds"
                  multiple
                  placeholder="请选择标签"
                  filterable
                  allow-create
                  default-first-option
                  style="width: 100%"
                >
                  <el-option
                    v-for="tag in tagList"
                    :key="tag.id"
                    :label="tag.name"
                    :value="tag.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="可见性">
                <el-radio-group v-model="articleForm.visibility">
                  <el-radio value="ALL">全部可见</el-radio>
                  <el-radio value="INTERNAL">仅对内</el-radio>
                  <el-radio value="EXTERNAL">对客户可见</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="置顶">
                <el-switch v-model="articleForm.isTop" />
              </el-form-item>
            </el-form>
          </el-card>

          <el-card style="margin-top: 16px">
            <template #header>
              <span>SEO设置</span>
            </template>
            <el-form label-width="80px" label-position="left">
              <el-form-item label="关键词">
                <el-input v-model="articleForm.keywords" placeholder="多个关键词用逗号分隔" />
              </el-form-item>

              <el-form-item label="描述">
                <el-input
                  v-model="articleForm.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入页面描述"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-form>
          </el-card>

          <el-card style="margin-top: 16px">
            <template #header>
              <span>AI智能推荐</span>
            </template>
            <div class="ai-recommend">
              <el-button type="primary" @click="handleAIRecommend" :loading="aiLoading">
                <el-icon><MagicStick /></el-icon>
                智能优化内容
              </el-button>
              <p class="ai-tip">基于AI分析，为您推荐更合适的标题和标签</p>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="文章预览" width="80%">
      <div class="preview-content" v-html="articleForm.content"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const editorRef = ref(null)
const isEdit = ref(false)
const saving = ref(false)
const publishing = ref(false)
const aiLoading = ref(false)
const previewVisible = ref(false)

const articleForm = reactive({
  title: '',
  content: '',
  categoryId: '',
  tagIds: [],
  visibility: 'ALL',
  isTop: false,
  keywords: '',
  description: '',
  status: 'DRAFT'
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
}

const categoryTree = ref([
  { id: 1, name: '产品使用', children: [{ id: 11, name: '快速入门' }, { id: 12, name: '常见问题' }] },
  { id: 2, name: '技术文档', children: [{ id: 21, name: 'API文档' }, { id: 22, name: '开发指南' }] }
])

const tagList = ref([
  { id: 1, name: '热门' },
  { id: 2, name: '推荐' },
  { id: 3, name: 'FAQ' }
])

const execCommand = (command) => {
  document.execCommand(command, false, null)
  editorRef.value?.focus()
}

const insertLink = () => {
  const url = prompt('请输入链接地址：')
  if (url) {
    document.execCommand('createLink', false, url)
  }
}

const insertImage = () => {
  ElMessage.info('图片上传功能开发中')
}

const insertTable = () => {
  ElMessage.info('表格插入功能开发中')
}

const insertCodeBlock = () => {
  const selection = window.getSelection()
  const text = selection.toString()
  if (text) {
    document.execCommand('insertHTML', false, `<pre><code>${text}</code></pre>`)
  }
}

const insertQuote = () => {
  const selection = window.getSelection()
  const text = selection.toString()
  if (text) {
    document.execCommand('insertHTML', false, `<blockquote>${text}</blockquote>`)
  }
}

const handleEditorInput = () => {
  articleForm.content = editorRef.value?.innerHTML || ''
}

const goBack = () => {
  router.back()
}

const handlePreview = () => {
  previewVisible.value = true
}

const handleSaveDraft = async () => {
  saving.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    articleForm.status = 'DRAFT'
    ElMessage.success('草稿保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handlePublish = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  publishing.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    articleForm.status = 'PUBLISHED'
    ElMessage.success('发布成功')
    router.push('/knowledge')
  } catch (error) {
    ElMessage.error('发布失败')
  } finally {
    publishing.value = false
  }
}

const handleAIRecommend = async () => {
  if (!articleForm.title && !articleForm.content) {
    ElMessage.warning('请先输入标题或内容')
    return
  }
  aiLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('AI推荐完成')
  } catch (error) {
    ElMessage.error('AI推荐失败')
  } finally {
    aiLoading.value = false
  }
}

onMounted(() => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    // 加载文章数据
    articleForm.title = '示例文章标题'
    articleForm.content = '<p>示例内容...</p>'
    if (editorRef.value) {
      editorRef.value.innerHTML = articleForm.content
    }
  }
})
</script>

<style lang="scss" scoped>
.knowledge-editor {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .editor-container {
    display: flex;
    gap: 20px;
  }

  .editor-main {
    flex: 1;
    min-width: 0;
  }

  .editor-side {
    width: 320px;
    flex-shrink: 0;
  }

  .title-input {
    :deep(.el-input__inner) {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .editor-toolbar {
    padding: 8px;
    background: #f5f7fa;
    border: 1px solid #dcdfe6;
    border-bottom: none;
    border-radius: 4px 4px 0 0;
  }

  .editor-content {
    min-height: 400px;
    padding: 16px;
    border: 1px solid #dcdfe6;
    border-radius: 0 0 4px 4px;
    outline: none;

    &:focus {
      border-color: #409eff;
    }

    pre {
      background: #f5f7fa;
      padding: 12px;
      border-radius: 4px;
      overflow-x: auto;
    }

    blockquote {
      border-left: 4px solid #409eff;
      padding-left: 16px;
      margin: 16px 0;
      color: #606266;
    }
  }

  .ai-recommend {
    text-align: center;

    .ai-tip {
      margin-top: 12px;
      color: #909399;
      font-size: 12px;
    }
  }

  .preview-content {
    padding: 20px;
    line-height: 1.8;

    pre {
      background: #f5f7fa;
      padding: 12px;
      border-radius: 4px;
    }

    blockquote {
      border-left: 4px solid #409eff;
      padding-left: 16px;
      margin: 16px 0;
      color: #606266;
    }
  }
}
</style>
