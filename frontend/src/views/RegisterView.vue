<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  fullName: '',
  phone: '',
})

const error = ref('')
const loading = ref(false)

const handleRegister = async () => {
  error.value = ''

  if (form.value.password !== form.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  if (form.value.password.length < 6) {
    error.value = '密码长度至少6位'
    return
  }

  loading.value = true

  try {
    await authStore.register({
      username: form.value.username,
      password: form.value.password,
      email: form.value.email,
      fullName: form.value.fullName || undefined,
      phone: form.value.phone || undefined,
    })

    alert('注册成功！请登录')
    router.push('/login')
  } catch (err: any) {
    error.value = err.response?.data || '注册失败，请稍后重试'
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
          注册新账户
        </h2>
      </div>

      <form class="mt-8 space-y-6" @submit.prevent="handleRegister">
        <div v-if="error" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg">
          {{ error }}
        </div>

        <div class="rounded-md shadow-sm space-y-4">
          <div>
            <label for="username" class="label">用户名 *</label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              class="input"
              placeholder="请输入用户名"
            />
          </div>

          <div>
            <label for="email" class="label">邮箱 *</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="input"
              placeholder="请输入邮箱"
            />
          </div>

          <div>
            <label for="password" class="label">密码 *</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              class="input"
              placeholder="请输入密码（至少6位）"
            />
          </div>

          <div>
            <label for="confirmPassword" class="label">确认密码 *</label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              required
              class="input"
              placeholder="请再次输入密码"
            />
          </div>

          <div>
            <label for="fullName" class="label">姓名</label>
            <input
              id="fullName"
              v-model="form.fullName"
              type="text"
              class="input"
              placeholder="请输入姓名"
            />
          </div>

          <div>
            <label for="phone" class="label">电话</label>
            <input
              id="phone"
              v-model="form.phone"
              type="tel"
              class="input"
              placeholder="请输入电话"
            />
          </div>
        </div>

        <div>
          <button
            type="submit"
            :disabled="loading"
            class="w-full btn btn-primary py-3"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>

        <div class="text-center">
          <router-link to="/login" class="text-primary-600 hover:text-primary-500">
            已有账户？立即登录
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>
