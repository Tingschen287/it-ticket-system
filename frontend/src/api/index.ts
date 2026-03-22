import axios from 'axios';
import type {
  User,
  Ticket,
  CreateTicketRequest,
  LoginRequest,
  RegisterRequest,
  JwtResponse,
  PaginatedResponse,
  TicketHistory,
  Status
} from '@/types';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器 - 添加JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理401错误
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 认证相关API
export const authApi = {
  login: async (data: LoginRequest): Promise<JwtResponse> => {
    const response = await api.post<JwtResponse>('/auth/login', data);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<string> => {
    const response = await api.post<string>('/auth/register', data);
    return response.data;
  },

  getCurrentUser: async (): Promise<User> => {
    const response = await api.get<User>('/auth/me');
    return response.data;
  },
};

// 工单相关API
export const ticketApi = {
  create: async (data: CreateTicketRequest): Promise<Ticket> => {
    const response = await api.post<Ticket>('/tickets', data);
    return response.data;
  },

  getAll: async (params?: {
    status?: Status;
    priority?: string;
    type?: string;
    reporterId?: number;
    assigneeId?: number;
    departmentId?: number;
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: string;
  }): Promise<PaginatedResponse<Ticket>> => {
    const response = await api.get<PaginatedResponse<Ticket>>('/tickets', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Ticket> => {
    const response = await api.get<Ticket>(`/tickets/${id}`);
    return response.data;
  },

  getByTicketNo: async (ticketNo: string): Promise<Ticket> => {
    const response = await api.get<Ticket>(`/tickets/no/${ticketNo}`);
    return response.data;
  },

  updateStatus: async (id: number, status: Status, comment?: string): Promise<Ticket> => {
    const response = await api.put<Ticket>(`/tickets/${id}/status`, null, {
      params: { status, comment },
    });
    return response.data;
  },

  assign: async (id: number, assigneeId: number): Promise<Ticket> => {
    const response = await api.put<Ticket>(`/tickets/${id}/assign`, null, {
      params: { assigneeId },
    });
    return response.data;
  },

  getHistory: async (id: number): Promise<TicketHistory[]> => {
    const response = await api.get<TicketHistory[]>(`/tickets/${id}/history`);
    return response.data;
  },

  getMyTickets: async (page = 0, size = 10): Promise<PaginatedResponse<Ticket>> => {
    const response = await api.get<PaginatedResponse<Ticket>>('/tickets/my', {
      params: { page, size },
    });
    return response.data;
  },

  getAssignedTickets: async (page = 0, size = 10): Promise<PaginatedResponse<Ticket>> => {
    const response = await api.get<PaginatedResponse<Ticket>>('/tickets/assigned', {
      params: { page, size },
    });
    return response.data;
  },
};

export default api;
