import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guest: true },
    },
    {
      path: '/',
      name: 'Dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/tickets',
      name: 'TicketList',
      component: () => import('@/views/TicketListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/tickets/:id',
      name: 'TicketDetail',
      component: () => import('@/views/TicketDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/tickets/new',
      name: 'NewTicket',
      component: () => import('@/views/NewTicketView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/my-tickets',
      name: 'MyTickets',
      component: () => import('@/views/MyTicketsView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/assigned',
      name: 'AssignedTickets',
      component: () => import('@/views/AssignedTicketsView.vue'),
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else if (to.meta.guest && authStore.isAuthenticated) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});

export default router;
