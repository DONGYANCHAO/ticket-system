<template>
  <div class="ticket-create">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <span>创建工单</span>
          <div></div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="ticket-form"
      >
        <el-row :gutter="40">
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId" required>
              <el-select
                v-model="form.customerId"
                placeholder="请选择客户"
                filterable
                style="width: 100%"
                @change="handleCustomerChange"
              >
                <el-option
                  v-for="customer in customerList"
                  :key="customer.id"
                  :label="customer.name"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="联系人" prop="contactId">
              <el-select
                v-model="form.contactId"
                placeholder="请选择联系人"
                filterable
                style="width: 100%"
                :disabled="!form.customerId"
              >
                <el-option
                  v-for="contact in contactList"
                  :key="contact.id"
                  :label="`${contact.name} (${contact.phone})`"
                  :value="contact.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="工单类型" prop="type" required>
              <el-radio-group v-model="form.type">
                <el-radio value="BUG">Bug</el-radio>
                <el-radio value="CONSULT">咨询</el-radio>
                <el-radio value="DEMAND">需求</el-radio>
                <el-radio value="COMPLAINT">投诉</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="问题模块" prop="moduleId">
              <el-cascader
                v-model="form.moduleId"
                :options="moduleTree"
                :props="{ checkStrictly: true, label: 'name', value: 'id' }"
                placeholder="请选择问题模块"
                clearable
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="优先级" prop="priority" required>
              <el-radio-group v-model="form.priority">
                <el-radio value="URGENT">
                  <el-tag type="danger" effect="dark">紧急</el-tag>
                </el-radio>
                <el-radio value="HIGH">
                  <el-tag type="warning" effect="dark">高</el-tag>
                </el-radio>
                <el-radio value="MEDIUM">
                  <el-tag type="info" effect="dark">中</el-tag>
                </el-radio>
                <el-radio value="LOW">
                  <el-tag type="info">低</el-tag>
                </el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="处理人" prop="handlerId">
              <el-select
                v-model="form.handlerId"
                placeholder="请选择处理人"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="user in handlerList"
                  :key="user.id"
                  :label="`${user.realName} (${user.department})`"
                  :value="user.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="抄送人">
              <el-select
                v-model="form.ccIds"
                placeholder="请选择抄送人"
                multiple
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="user in handlerList"
                  :key="user.id"
                  :label="user.realName"
                  :value="user.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="标题" prop="title" required>
              <el-input
                v-model="form.title"
                placeholder="请输入工单标题"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="问题描述" prop="content" required>
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="8"
                placeholder="请详细描述问题..."
                maxlength="2000"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="附件">
              <el-upload
                ref="uploadRef"
                :auto-upload="false"
                :limit="5"
                :on-exceed="handleExceed"
                accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.xls,.xlsx,.zip,.rar"
              >
                <template #trigger>
                  <el-button>
                    <el-icon><Upload /></el-icon> 选择文件
                  </el-button>
                </template>
                <template #tip>
                  <div class="el-upload__tip">
                    支持jpg、png、gif、pdf、doc、xls、xlsx、zip、rar格式，单个文件不超过20MB，最多上传5个文件
                  </div>
                </template>
              </el-upload>
            </el-form-item>

            <el-form-item label="标签">
              <el-select
                v-model="form.tagIds"
                placeholder="请选择标签"
                multiple
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

            <el-form-item label="是否私有">
              <el-switch v-model="form.isPrivate" />
              <span class="form-tip">私有工单仅处理人和管理员可见</span>
            </el-form-item>

            <el-form-item label="通知方式">
              <el-checkbox-group v-model="form.notifyChannels">
                <el-checkbox value="EMAIL">邮件</el-checkbox>
                <el-checkbox value="SMS">短信</el-checkbox>
                <el-checkbox value="INNER">站内信</el-checkbox>
                <el-checkbox value="DINGTALK">钉钉</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />

        <div class="form-footer">
          <el-button @click="handleSaveDraft">保存草稿</el-button>
          <el-button @click="goBack">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交工单
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const uploadRef = ref(null)
const submitting = ref(false)

// 表单数据
const form = reactive({
  customerId: '',
  contactId: '',
  type: 'BUG',
  moduleId: '',
  priority: 'MEDIUM',
  handlerId: '',
  ccIds: [],
  title: '',
  content: '',
  tagIds: [],
  isPrivate: false,
  notifyChannels: ['INNER']
})

// 表单验证规则
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  type: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  title: [
    { required: true, message: '请输入工单标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度为5-100个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入问题描述', trigger: 'blur' },
    { min: 10, message: '描述至少10个字符', trigger: 'blur' }
  ]
}

// 客户列表
const customerList = ref([
  { id: 1, name: '某某科技有限公司' },
  { id: 2, name: '某某贸易有限公司' }
])

// 联系人列表
const contactList = ref([])

// 问题模块树
const moduleTree = ref([
  {
    id: 1,
    name: '产品功能',
    children: [
      { id: 11, name: '用户认证' },
      { id: 12, name: '数据管理' },
      { id: 13, name: '报表导出' }
    ]
  },
  {
    id: 2,
    name: '系统问题',
    children: [
      { id: 21, name: '登录异常' },
      { id: 22, name: '页面加载慢' }
    ]
  }
])

// 处理人列表
const handlerList = ref([
  { id: 1, realName: '张三', department: '技术支持部' },
  { id: 2, realName: '李四', department: '技术支持部' }
])

// 标签列表
const tagList = ref([
  { id: 1, name: '高频问题' },
  { id: 2, name: '需要回访' },
  { id: 3, name: 'VIP客户' }
])

// 客户变更
const handleCustomerChange = (customerId) => {
  form.contactId = ''
  if (customerId) {
    // 模拟加载联系人
    contactList.value = [
      { id: 1, name: '张三', phone: '13800138001', email: 'zhangsan@example.com' },
      { id: 2, name: '李四', phone: '13800138002', email: 'lisi@example.com' }
    ]
  } else {
    contactList.value = []
  }
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning('最多上传5个文件')
}

// 保存草稿
const handleSaveDraft = async () => {
  ElMessage.success('草稿保存成功')
}

// 提交工单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('工单提交成功')
    router.push('/ticket')
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  // 加载客户列表等数据
})
</script>

<style lang="scss" scoped>
.ticket-create {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .ticket-form {
    max-width: 1200px;

    .form-tip {
      margin-left: 12px;
      color: #909399;
      font-size: 12px;
    }
  }

  .form-footer {
    display: flex;
    justify-content: center;
    gap: 12px;
    padding-top: 20px;
  }
}
</style>
