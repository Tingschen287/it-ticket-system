<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  try {
    await authStore.login({
      username: username.value,
      password: password.value,
    })

    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (err: any) {
    error.value = err.response?.data || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          IT工单管理系统
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          请登录您的账户
        </p>
      </div>

      <form class="mt-8 space-y-6" @submit.prevent="handleLogin">
        <div v-if="error" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg">
          {{ error }}
        </div>

        <div class="rounded-md shadow-sm space-y-4">
          <div>
            <label for="username" class="label">用户名</label>
            <input
              id="username"
              v-model="username"
              name="username"
              type="text"
              required
              class="input"
              placeholder="请输入用户名"
            />
          </div>

          <div>
            <label for="password" class="label">密码</label>
            <input
              id="password"
              v-model="password"
              name="password"
              type="password"
              required
              class="input"
              placeholder="请输入密码"
            />
          </div>
        </div>

        <div>
          <button
            type="submit"
            :disabled="loading"
            class="w-full btn btn-primary py-3"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>

        <div class="text-center">
          <router-link to="/register" class="text-primary-600 hover:text-primary-500">
            还没有账户？立即注册
          </router-link>
        </div>

        <div class="text-center text-sm text-gray-500">
          <p>测试账户: admin / admin123</p>
        </div>
      </form>
    </div>
  </div>
</template>
