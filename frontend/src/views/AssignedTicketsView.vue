<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ticketApi } from '@/api'
import type { Ticket, PaginatedResponse } from '@/types'

const tickets = ref<Ticket[]>([])
const loading = ref(true)
const totalElements = ref(0)

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    'NEW': 'bg-blue-100 text-blue-800',
    'PENDING_EVAL': 'bg-yellow-100 text-yellow-800',
    'IN_PROGRESS': 'bg-orange-100 text-orange-800',
    'PENDING_TEST': 'bg-purple-100 text-purple-800',
    'RESOLVED': 'bg-green-100 text-green-800',
    'CLOSED': 'bg-gray-100 text-gray-800',
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    'NEW': '新建',
    'PENDING_EVAL': '待评估',
    'IN_PROGRESS': '开发中',
    'PENDING_TEST': '待测试',
    'RESOLVED': '已解决',
    'CLOSED': '已关闭',
  }
  return labels[status] || status
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

onMounted(async () => {
  try {
    const response: PaginatedResponse<Ticket> = await ticketApi.getAssignedTickets(0, 20)
    tickets.value = response.content
    totalElements.value = response.totalElements
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
    <div class="px-4 sm:px-0">
      <h1 class="text-2xl font-semibold text-gray-900">待处理工单</h1>
      <p class="mt-1 text-sm text-gray-600">分配给我的工单列表</p>
    </div>

    <div class="mt-6 card">
      <div v-if="loading" class="text-center py-10">
        <p class="text-gray-500">加载中...</p>
      </div>

      <div v-else-if="tickets.length === 0" class="text-center py-10">
        <p class="text-gray-500">暂无待处理工单</p>
      </div>

      <table v-else class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">工单编号</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">标题</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">类型</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">优先级</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">状态</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">提交人</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="ticket in tickets" :key="ticket.id" class="hover:bg-gray-50 cursor-pointer" @click="$router.push(`/tickets/${ticket.id}`)">
            <td class="px-6 py-4 text-sm font-medium text-primary-600">{{ ticket.ticketNo }}</td>
            <td class="px-6 py-4 text-sm text-gray-900">{{ ticket.title }}</td>
            <td class="px-6 py-4 text-sm text-gray-500">{{ ticket.type }}</td>
            <td class="px-6 py-4">
              <span :class="['px-2 py-1 text-xs font-medium rounded-full', getPriorityClass(ticket.priority)]">
                {{ ticket.priority }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span :class="['px-2 py-1 text-xs font-medium rounded-full', getStatusClass(ticket.status)]">
                {{ getStatusLabel(ticket.status) }}
              </span>
            </td>
            <td class="px-6 py-4 text-sm text-gray-500">{{ ticket.reporter?.fullName || ticket.reporter?.username }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
