<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticket'
import type { TicketType, Priority } from '@/types'

const router = useRouter()
const ticketStore = useTicketStore()

const form = ref({
  title: '',
  description: '',
  type: 'TASK' as TicketType,
  priority: 'MEDIUM' as Priority,
})

const loading = ref(false)
const error = ref('')

const typeOptions: { value: TicketType; label: string }[] = [
  { value: 'BUG', label: 'Bug' },
  { value: 'FEATURE', label: '功能需求' },
  { value: 'TASK', label: '任务' },
  { value: 'SUPPORT', label: '支持' },
  { value: 'OTHER', label: '其他' },
]

const priorityOptions: { value: Priority; label: string }[] = [
  { value: 'LOW', label: '低' },
  { value: 'MEDIUM', label: '中' },
  { value: 'HIGH', label: '高' },
  { value: 'CRITICAL', label: '紧急' },
]

const handleSubmit = async () => {
  if (!form.value.title.trim()) {
    error.value = '请输入工单标题'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const ticket = await ticketStore.createTicket({
      title: form.value.title,
      description: form.value.description,
      type: form.value.type,
      priority: form.value.priority,
    })
    router.push(`/tickets/${ticket.id}`)
  } catch (err: any) {
    error.value = err.response?.data || '创建失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-3xl mx-auto py-6 sm:px-6 lg:px-8">
    <div class="px-4 sm:px-0">
      <h1 class="text-2xl font-semibold text-gray-900">新建工单</h1>
    </div>

    <form @submit.prevent="handleSubmit" class="mt-6 card">
      <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg">
        {{ error }}
      </div>

      <div class="space-y-6">
        <div>
          <label for="title" class="label">标题 *</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            required
            class="input"
            placeholder="请输入工单标题"
          />
        </div>

        <div>
          <label for="description" class="label">描述</label>
          <textarea
            id="description"
            v-model="form.description"
            rows="5"
            class="input"
            placeholder="请详细描述您的问题或需求"
          />
        </div>

        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <label for="type" class="label">类型 *</label>
            <select id="type" v-model="form.type" class="input">
              <option v-for="opt in typeOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
          </div>

          <div>
            <label for="priority" class="label">优先级 *</label>
            <select id="priority" v-model="form.priority" class="input">
              <option v-for="opt in priorityOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
          </div>
        </div>

        <div class="flex justify-end space-x-3">
          <button type="button" class="btn btn-secondary" @click="router.back()">
            取消
          </button>
          <button type="submit" :disabled="loading" class="btn btn-primary">
            {{ loading ? '提交中...' : '提交工单' }}
          </button>
        </div>
      </div>
    </form>
  </div>
</template>
