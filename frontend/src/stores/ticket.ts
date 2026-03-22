import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Ticket, CreateTicketRequest, PaginatedResponse, Status } from '@/types';
import { ticketApi } from '@/api';

export const useTicketStore = defineStore('ticket', () => {
  const tickets = ref<Ticket[]>([]);
  const currentTicket = ref<Ticket | null>(null);
  const loading = ref(false);
  const totalElements = ref(0);
  const totalPages = ref(0);

  async function createTicket(data: CreateTicketRequest): Promise<Ticket> {
    loading.value = true;
    try {
      const ticket = await ticketApi.create(data);
      tickets.value.unshift(ticket);
      return ticket;
    } finally {
      loading.value = false;
    }
  }

  async function fetchTickets(params?: {
    status?: Status;
    priority?: string;
    type?: string;
    reporterId?: number;
    assigneeId?: number;
    departmentId?: number;
    page?: number;
    size?: number;
  }): Promise<void> {
    loading.value = true;
    try {
      const response: PaginatedResponse<Ticket> = await ticketApi.getAll(params);
      tickets.value = response.content;
      totalElements.value = response.totalElements;
      totalPages.value = response.totalPages;
    } finally {
      loading.value = false;
    }
  }

  async function fetchTicketById(id: number): Promise<Ticket> {
    loading.value = true;
    try {
      currentTicket.value = await ticketApi.getById(id);
      return currentTicket.value;
    } finally {
      loading.value = false;
    }
  }

  async function updateStatus(id: number, status: Status, comment?: string): Promise<Ticket> {
    loading.value = true;
    try {
      const ticket = await ticketApi.updateStatus(id, status, comment);
      const index = tickets.value.findIndex((t) => t.id === id);
      if (index !== -1) {
        tickets.value[index] = ticket;
      }
      if (currentTicket.value?.id === id) {
        currentTicket.value = ticket;
      }
      return ticket;
    } finally {
      loading.value = false;
    }
  }

  async function assignTicket(id: number, assigneeId: number): Promise<Ticket> {
    loading.value = true;
    try {
      const ticket = await ticketApi.assign(id, assigneeId);
      const index = tickets.value.findIndex((t) => t.id === id);
      if (index !== -1) {
        tickets.value[index] = ticket;
      }
      if (currentTicket.value?.id === id) {
        currentTicket.value = ticket;
      }
      return ticket;
    } finally {
      loading.value = false;
    }
  }

  function clearCurrentTicket(): void {
    currentTicket.value = null;
  }

  return {
    tickets,
    currentTicket,
    loading,
    totalElements,
    totalPages,
    createTicket,
    fetchTickets,
    fetchTicketById,
    updateStatus,
    assignTicket,
    clearCurrentTicket,
  };
});
