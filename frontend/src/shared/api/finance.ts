import { http } from './http'
import type {
  AccountCreateInput,
  AccountUpdateInput,
  BackendAccount,
  BackendAccountBalance,
  BackendBill,
  BackendBudget,
  BackendBudgetOverview,
  BillCreateInput,
  BillUpdateInput,
  BudgetCreateInput,
  BudgetUpdateInput,
  CategoryCreateInput,
  BackendCategory,
  BackendCategoryExpense,
  BackendMonthlyTrend,
  BackendPageResult,
  BackendSummary,
  CategoryUpdateInput,
} from '@/shared/types/backend'

export interface BillPageQuery {
  pageNum?: number
  pageSize?: number
  month?: string
  type?: 'INCOME' | 'EXPENSE' | string
  categoryId?: number
  accountId?: number
  startTime?: string
  endTime?: string
}

export function fetchAccounts() {
  return http.get<BackendAccount[]>('/accounts').then((response) => response.data)
}

export function fetchAccountDetail(id: number) {
  return http.get<BackendAccount>(`/accounts/${id}`).then((response) => response.data)
}

export function fetchCategories() {
  return http.get<BackendCategory[]>('/categories').then((response) => response.data)
}

export function fetchCategoryDetail(id: number) {
  return http.get<BackendCategory>(`/categories/${id}`).then((response) => response.data)
}

export function fetchBills(query: BillPageQuery = {}) {
  return http.get<BackendPageResult<BackendBill>>('/bills', { params: query }).then((response) => response.data)
}

export function fetchBillDetail(id: number) {
  return http.get<BackendBill>(`/bills/${id}`).then((response) => response.data)
}

export function fetchSummary(month: string) {
  return http.get<BackendSummary>('/statistics/summary', { params: { month } }).then((response) => response.data)
}

export function fetchCategoryExpense(month: string) {
  return http.get<BackendCategoryExpense[]>('/statistics/category-expense', { params: { month } }).then((response) => response.data)
}

export function fetchMonthlyTrend(monthCount = 6) {
  return http.get<BackendMonthlyTrend[]>('/statistics/monthly-trend', { params: { monthCount } }).then((response) => response.data)
}

export function fetchAccountBalance() {
  return http.get<BackendAccountBalance[]>('/statistics/account-balance').then((response) => response.data)
}

export function createAccount(payload: AccountCreateInput) {
  return http.post<BackendAccount>('/accounts', payload).then((response) => response.data)
}

export function updateAccount(id: number, payload: AccountUpdateInput) {
  return http.put<BackendAccount>(`/accounts/${id}`, payload).then((response) => response.data)
}

export function deleteAccount(id: number) {
  return http.delete<void>(`/accounts/${id}`).then((response) => response.data)
}

export function createCategory(payload: CategoryCreateInput) {
  return http.post<BackendCategory>('/categories', payload).then((response) => response.data)
}

export function updateCategory(id: number, payload: CategoryUpdateInput) {
  return http.put<BackendCategory>(`/categories/${id}`, payload).then((response) => response.data)
}

export function changeCategoryStatus(id: number, status: number) {
  return http.put<void>(`/categories/${id}/status`, null, { params: { status } }).then((response) => response.data)
}

export function deleteCategory(id: number) {
  return http.delete<void>(`/categories/${id}`).then((response) => response.data)
}

export function createBill(payload: BillCreateInput) {
  return http.post<BackendBill>('/bills', payload).then((response) => response.data)
}

export function updateBill(id: number, payload: BillUpdateInput) {
  return http.put<BackendBill>(`/bills/${id}`, payload).then((response) => response.data)
}

export function deleteBill(id: number) {
  return http.delete<void>(`/bills/${id}`).then((response) => response.data)
}

export function createBudget(payload: BudgetCreateInput) {
  return http.post<BackendBudget>('/budgets', payload).then((response) => response.data)
}

export function updateBudget(id: number, payload: BudgetUpdateInput) {
  return http.put<BackendBudget>(`/budgets/${id}`, payload).then((response) => response.data)
}

export function deleteBudget(id: number) {
  return http.delete<void>(`/budgets/${id}`).then((response) => response.data)
}

export function fetchBudgetsByMonth(month: string) {
  return http.get<BackendBudget[]>('/budgets', { params: { month } }).then((response) => response.data)
}

export function fetchBudgetOverview(month: string) {
  return http.get<BackendBudgetOverview>('/budgets/overview', { params: { month } }).then((response) => response.data)
}
