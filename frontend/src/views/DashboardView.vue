<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTicketStore } from '@/stores/ticket'
import { useAuthStore } from '@/stores/auth'

const ticketStore = useTicketStore()
const authStore = useAuthStore()

const stats = ref({
  total: 0,
  new: 0,
  inProgress: 0,
  resolved: 0,
})

onMounted(async () => {
  await ticketStore.fetchTickets({ size: 100 })

  // 计算统计数据
  const tickets = ticketStore.tickets
  stats.value.total = ticketStore.totalElements
  stats.value.new = tickets.filter(t => t.status === 'NEW').length
  stats.value.inProgress = tickets.filter(t => t.status === 'IN_PROGRESS').length
  stats.value.resolved = tickets.filter(t => t.status === 'RESOLVED' || t.status === 'CLOSED').length
})
</script>

<template>
  <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
    <div class="px-4 sm:px-0">
      <h1 class="text-2xl font-semibold text-gray-900">工作台</h1>
      <p class="mt-1 text-sm text-gray-600">
        欢迎，{{ authStore.user?.fullName || authStore.user?.username }}
      </p>
    </div>

    <!-- Stats Cards -->
    <div class="mt-6 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
      <div class="card">
        <div class="flex items-center">
          <div class="flex-1">
            <p class="text-sm font-medium text-gray-500">总工单数</p>
            <p class="mt-1 text-3xl font-semibold text-gray-900">{{ stats.total }}</p>
          </div>
          <div class="p-3 bg-blue-100 rounded-full">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center">
          <div class="flex-1">
            <p class="text-sm font-medium text-gray-500">新建工单</p>
            <p class="mt-1 text-3xl font-semibold text-gray-900">{{ stats.new }}</p>
          </div>
          <div class="p-3 bg-green-100 rounded-full">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center">
          <div class="flex-1">
            <p class="text-sm font-medium text-gray-500">处理中</p>
            <p class="mt-1 text-3xl font-semibold text-gray-900">{{ stats.inProgress }}</p>
          </div>
          <div class="p-3 bg-yellow-100 rounded-full">
            <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center">
          <div class="flex-1">
            <p class="text-sm font-medium text-gray-500">已解决</p>
            <p class="mt-1 text-3xl font-semibold text-gray-900">{{ stats.resolved }}</p>
          </div>
          <div class="p-3 bg-purple-100 rounded-full">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="mt-8">
      <h2 class="text-lg font-medium text-gray-900">快捷操作</h2>
      <div class="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <router-link
          to="/tickets/new"
          class="card hover:shadow-lg transition-shadow cursor-pointer"
        >
          <div class="flex items-center">
            <div class="p-2 bg-primary-100 rounded-lg">
              <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
            </div>
            <span class="ml-3 font-medium text-gray-900">新建工单</span>
          </div>
        </router-link>

        <router-link
          to="/tickets"
          class="card hover:shadow-lg transition-shadow cursor-pointer"
        >
          <div class="flex items-center">
            <div class="p-2 bg-primary-100 rounded-lg">
              <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
              </svg>
            </div>
            <span class="ml-3 font-medium text-gray-900">查看工单</span>
          </div>
        </router-link>

        <router-link
          to="/my-tickets"
          class="card hover:shadow-lg transition-shadow cursor-pointer"
        >
          <div class="flex items-center">
            <div class="p-2 bg-primary-100 rounded-lg">
              <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <span class="ml-3 font-medium text-gray-900">我的工单</span>
          </div>
        </router-link>

        <router-link
          to="/assigned"
          class="card hover:shadow-lg transition-shadow cursor-pointer"
        >
          <div class="flex items-center">
            <div class="p-2 bg-primary-100 rounded-lg">
              <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <span class="ml-3 font-medium text-gray-900">待处理工单</span>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>
