<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticket'
import { useAuthStore } from '@/stores/auth'
import { ticketApi } from '@/api'
import type { Status, TicketHistory } from '@/types'

const route = useRoute()
const router = useRouter()
const ticketStore = useTicketStore()
const authStore = useAuthStore()

const ticket = computed(() => ticketStore.currentTicket)
const loading = ref(true)
const history = ref<TicketHistory[]>([])

const statusOptions: { value: Status; label: string }[] = [
  { value: 'NEW', label: '新建' },
  { value: 'PENDING_EVAL', label: '待评估' },
  { value: 'IN_PROGRESS', label: '开发中' },
  { value: 'PENDING_TEST', label: '待测试' },
  { value: 'RESOLVED', label: '已解决' },
  { value: 'CLOSED', label: '已关闭' },
]

const getStatusLabel = (status: Status) => {
  return statusOptions.find(s => s.value === status)?.label || status
}

const getStatusClass = (status: Status) => {
  const classes: Record<Status, string> = {
    'NEW': 'bg-blue-100 text-blue-800',
    'PENDING_EVAL': 'bg-yellow-100 text-yellow-800',
    'IN_PROGRESS': 'bg-orange-100 text-orange-800',
    'PENDING_TEST': 'bg-purple-100 text-purple-800',
    'RESOLVED': 'bg-green-100 text-green-800',
    'CLOSED': 'bg-gray-100 text-gray-800',
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getPriorityClass = (priority: string) => {
  const classes: Record<string, string> = {
    'LOW': 'bg-gray-100 text-gray-800',
    'MEDIUM': 'bg-blue-100 text-blue-800',
    'HIGH': 'bg-orange-100 text-orange-800',
    'CRITICAL': 'bg-red-100 text-red-800',
  }
  return classes[priority] || 'bg-gray-100 text-gray-800'
}

const canUpdateStatus = computed(() => {
  if (!ticket.value) return false
  const isAssignee = ticket.value.assignee?.id === authStore.user?.id
  const isReporter = ticket.value.reporter?.id === authStore.user?.id
  return authStore.isAdmin || isAssignee || isReporter
})

const updateStatus = async (newStatus: Status) => {
  if (!ticket.value) return

  const comment = prompt('请输入备注（可选）')
  try {
    await ticketStore.updateStatus(ticket.value.id, newStatus, comment || undefined)
    await loadHistory()
  } catch (err) {
    alert('更新失败')
  }
}

const loadHistory = async () => {
  if (!ticket.value) return
  try {
    history.value = await ticketApi.getHistory(ticket.value.id)
  } catch (err) {
    console.error('Failed to load history:', err)
  }
}

onMounted(async () => {
  const id = Number(route.params.id)
  try {
    await ticketStore.fetchTicketById(id)
    await loadHistory()
  } catch (err) {
    router.push('/tickets')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
    <div v-if="loading" class="text-center py-10">
      <p class="text-gray-500">加载中...</p>
    </div>

    <template v-else-if="ticket">
      <div class="px-4 sm:px-0 flex justify-between items-start">
        <div>
          <p class="text-sm text-primary-600 font-medium">{{ ticket.ticketNo }}</p>
          <h1 class="text-2xl font-semibold text-gray-900 mt-1">{{ ticket.title }}</h1>
        </div>
        <router-link to="/tickets" class="btn btn-secondary">
          返回列表
        </router-link>
      </div>

      <div class="mt-6 grid grid-cols-1 gap-6 lg:grid-cols-3">
        <!-- Main Content -->
        <div class="lg:col-span-2 space-y-6">
          <div class="card">
            <h2 class="text-lg font-medium text-gray-900 mb-4">描述</h2>
            <p class="text-gray-600 whitespace-pre-wrap">{{ ticket.description || '暂无描述' }}</p>
          </div>

          <!-- Status Update -->
          <div v-if="canUpdateStatus" class="card">
            <h2 class="text-lg font-medium text-gray-900 mb-4">状态变更</h2>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="opt in statusOptions"
                :key="opt.value"
                @click="updateStatus(opt.value)"
                :disabled="opt.value === ticket.status"
                :class="[
                  'px-3 py-1 text-sm rounded-full transition-colors',
                  opt.value === ticket.status
                    ? 'bg-primary-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                {{ opt.label }}
              </button>
            </div>
          </div>

          <!-- History -->
          <div class="card">
            <h2 class="text-lg font-medium text-gray-900 mb-4">操作历史</h2>
            <div class="space-y-4">
              <div v-for="h in history" :key="h.id" class="flex items-start space-x-3 text-sm">
                <div class="flex-1">
                  <p class="text-gray-900">
                    <span class="font-medium">{{ h.operator?.fullName || h.operator?.username }}</span>
                    {{ h.action }}
                    <span v-if="h.fromStatus" class="text-gray-500">({{ h.fromStatus }} → {{ h.toStatus }})</span>
                  </p>
                  <p v-if="h.comment" class="text-gray-600 mt-1">{{ h.comment }}</p>
                  <p class="text-gray-400 text-xs mt-1">{{ new Date(h.createdAt).toLocaleString() }}</p>
                </div>
              </div>
              <div v-if="history.length === 0" class="text-gray-500">
                暂无历史记录
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="space-y-6">
          <div class="card">
            <h2 class="text-lg font-medium text-gray-900 mb-4">详情</h2>
            <dl class="space-y-4">
              <div>
                <dt class="text-sm font-medium text-gray-500">状态</dt>
                <dd class="mt-1">
                  <span :class="['px-2 py-1 text-xs font-medium rounded-full', getStatusClass(ticket.status)]">
                    {{ getStatusLabel(ticket.status) }}
                  </span>
                </dd>
              </div>

              <div>
                <dt class="text-sm font-medium text-gray-500">优先级</dt>
                <dd class="mt-1">
                  <span :class="['px-2 py-1 text-xs font-medium rounded-full', getPriorityClass(ticket.priority)]">
                    {{ ticket.priority }}
                  </span>
                </dd>
              </div>

              <div>
                <dt class="text-sm font-medium text-gray-500">类型</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ ticket.type }}</dd>
              </div>

              <div>
                <dt class="text-sm font-medium text-gray-500">提交人</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ ticket.reporter?.fullName || ticket.reporter?.username }}</dd>
              </div>

              <div v-if="ticket.assignee">
                <dt class="text-sm font-medium text-gray-500">处理人</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ ticket.assignee?.fullName || ticket.assignee?.username }}</dd>
              </div>

              <div v-if="ticket.department">
                <dt class="text-sm font-medium text-gray-500">部门</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ ticket.department.name }}</dd>
              </div>

              <div>
                <dt class="text-sm font-medium text-gray-500">创建时间</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ new Date(ticket.createdAt).toLocaleString() }}</dd>
              </div>

              <div>
                <dt class="text-sm font-medium text-gray-500">更新时间</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ new Date(ticket.updatedAt).toLocaleString() }}</dd>
              </div>
            </dl>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
