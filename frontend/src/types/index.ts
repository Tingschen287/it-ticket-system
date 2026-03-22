export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  phone?: string;
  role?: Role;
  department?: Department;
  isActive: boolean;
}

export interface Role {
  id: number;
  name: string;
  code: string;
  description?: string;
}

export interface Department {
  id: number;
  name: string;
  code: string;
  description?: string;
}

export interface Ticket {
  id: number;
  ticketNo: string;
  title: string;
  description?: string;
  type: TicketType;
  priority: Priority;
  status: Status;
  reporter?: UserSummary;
  assignee?: UserSummary;
  department?: DepartmentSummary;
  sla?: SlaSummary;
  dueDate?: string;
  resolvedAt?: string;
  closedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface UserSummary {
  id: number;
  username: string;
  fullName?: string;
}

export interface DepartmentSummary {
  id: number;
  name: string;
  code?: string;
}

export interface SlaSummary {
  id: number;
  name: string;
}

export type TicketType = 'BUG' | 'FEATURE' | 'TASK' | 'SUPPORT' | 'OTHER';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type Status = 'NEW' | 'PENDING_EVAL' | 'IN_PROGRESS' | 'PENDING_TEST' | 'RESOLVED' | 'CLOSED';

export interface CreateTicketRequest {
  title: string;
  description?: string;
  type: TicketType;
  priority: Priority;
  departmentId?: number;
  assigneeId?: number;
  slaId?: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName?: string;
  phone?: string;
  departmentId?: number;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface TicketHistory {
  id: number;
  action: string;
  fromStatus?: string;
  toStatus?: string;
  operator?: UserSummary;
  comment?: string;
  createdAt: string;
}
