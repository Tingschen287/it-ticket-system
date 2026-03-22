import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { User, JwtResponse, LoginRequest, RegisterRequest } from '@/types';
import { authApi } from '@/api';

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null);
  const token = ref<string | null>(localStorage.getItem('token'));

  const isAuthenticated = computed(() => !!token.value);
  const userRole = computed(() => {
    if (!user.value?.role) return 'USER';
    return user.value.role.code;
  });

  const isAdmin = computed(() => userRole.value === 'ADMIN');
  const isPM = computed(() => userRole.value === 'PM');
  const isDeveloper = computed(() => userRole.value === 'DEVELOPER');

  async function login(credentials: LoginRequest): Promise<void> {
    try {
      const response: JwtResponse = await authApi.login(credentials);
      token.value = response.token;
      localStorage.setItem('token', response.token);

      // 获取用户信息
      await fetchCurrentUser();
    } catch (error) {
      throw error;
    }
  }

  async function register(data: RegisterRequest): Promise<void> {
    try {
      await authApi.register(data);
    } catch (error) {
      throw error;
    }
  }

  async function fetchCurrentUser(): Promise<void> {
    try {
      user.value = await authApi.getCurrentUser();
    } catch (error) {
      logout();
      throw error;
    }
  }

  function logout(): void {
    user.value = null;
    token.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  // 初始化时获取用户信息
  if (token.value) {
    fetchCurrentUser().catch(() => {
      logout();
    });
  }

  return {
    user,
    token,
    isAuthenticated,
    userRole,
    isAdmin,
    isPM,
    isDeveloper,
    login,
    register,
    logout,
    fetchCurrentUser,
  };
});
