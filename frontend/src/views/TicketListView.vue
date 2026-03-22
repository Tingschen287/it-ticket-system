<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useTicketStore } from '@/stores/ticket'
import type { Status, TicketType, Priority } from '@/types'

const ticketStore = useTicketStore()

const filters = ref({
  status: '' as Status | '',
  type: '' as TicketType | '',
  priority: '' as Priority | '',
})

const page = ref(0)
const size = ref(10)

const statusOptions: { value: Status; label: string }[] = [
  { value: 'NEW', label: '新建' },
  { value: 'PENDING_EVAL', label: '待评估' },
  { value: 'IN_PROGRESS', label: '开发中' },
  { value: 'PENDING_TEST', label: '待测试' },
  { value: 'RESOLVED', label: '已解决' },
  { value: 'CLOSED', label: '已关闭' },
]

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

const getPriorityClass = (priority: Priority) => {
  const classes: Record<Priority, string> = {
    'LOW': 'bg-gray-100 text-gray-800',
    'MEDIUM': 'bg-blue-100 text-blue-800',
    'HIGH': 'bg-orange-100 text-orange-800',
    'CRITICAL': 'bg-red-100 text-red-800',
  }
  return classes[priority] || 'bg-gray-100 text-gray-800'
}

const fetchTickets = async () => {
  await ticketStore.fetchTickets({
    status: filters.value.status || undefined,
    type: filters.value.type || undefined,
    priority: filters.value.priority || undefined,
    page: page.value,
    size: size.value,
  })
}

watch(filters, () => {
  page.value = 0
  fetchTickets()
}, { deep: true })

onMounted(fetchTickets)
</script>

<template>
  <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
    <div class="px-4 sm:px-0 flex justify-between items-center">
      <h1 class="text-2xl font-semibold text-gray-900">工单列表</h1>
      <router-link to="/tickets/new" class="btn btn-primary">
        新建工单
      </router-link>
    </div>

    <!-- Filters -->
    <div class="mt-4 card">
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-4">
        <div>
          <label class="label">状态</label>
          <select v-model="filters.status" class="input">
            <option value="">全部</option>
            <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>
        </div>

        <div>
          <label class="label">类型</label>
          <select v-model="filters.type" class="input">
            <option value="">全部</option>
            <option v-for="opt in typeOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>
        </div>

        <div>
          <label class="label">优先级</label>
          <select v-model="filters.priority" class="input">
            <option value="">全部</option>
            <option v-for="opt in priorityOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Ticket List -->
    <div class="mt-4 card overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              工单编号
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              标题
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              类型
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              优先级
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              状态
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              创建时间
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="ticket in ticketStore.tickets" :key="ticket.id" class="hover:bg-gray-50 cursor-pointer" @click="$router.push(`/tickets/${ticket.id}`)">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-primary-600">
              {{ ticket.ticketNo }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ ticket.title }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ ticket.type }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="['px-2 py-1 text-xs font-medium rounded-full', getPriorityClass(ticket.priority)]">
                {{ ticket.priority }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="['px-2 py-1 text-xs font-medium rounded-full', getStatusClass(ticket.status)]">
                {{ getStatusLabel(ticket.status) }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ new Date(ticket.createdAt).toLocaleString() }}
            </td>
          </tr>
          <tr v-if="ticketStore.tickets.length === 0">
            <td colspan="6" class="px-6 py-4 text-center text-gray-500">
              暂无数据
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
