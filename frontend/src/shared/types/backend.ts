export interface BackendPageResult<T> {
  total: number
  current: number
  size: number
  records: T[]
}

export interface BackendAccount {
  id: number
  name: string
  type: string
  initialBalance: number | string
  currentBalance: number | string
  remark: string | null
  createTime: string
  updateTime: string
}

export interface BackendCategory {
  id: number
  name: string
  type: 'INCOME' | 'EXPENSE'
  icon: string | null
  sort: number
  status: number
  createTime: string
  updateTime: string
}

export interface BackendBill {
  id: number
  type: 'INCOME' | 'EXPENSE'
  amount: number | string
  categoryId: number
  categoryName: string
  accountId: number
  accountName: string
  tradeTime: string
  remark: string | null
  createTime: string
  updateTime: string
}

export interface BackendSummary {
  month: string
  income: number | string
  expense: number | string
  balance: number | string
  totalAssets: number | string
}

export interface BackendCategoryExpense {
  categoryId: number
  categoryName: string
  amount: number | string
  percent: number | string
}

export interface BackendMonthlyTrend {
  month: string
  income: number | string
  expense: number | string
}

export interface BackendAccountBalance {
  accountId: number
  accountName: string
  balance: number | string
}

export interface AccountCreateInput {
  name: string
  type: string
  initialBalance: number
  remark?: string
}

export interface CategoryCreateInput {
  name: string
  type: 'INCOME' | 'EXPENSE'
  icon?: string
  sort: number
  status: number
}

export interface BillCreateInput {
  type: 'INCOME' | 'EXPENSE'
  amount: number
  categoryId: number
  accountId: number
  tradeTime: string
  remark?: string
}

export interface BackendBudget {
  id: number
  month: string
  type: 'TOTAL' | 'CATEGORY'
  categoryId: number | null
  categoryName: string | null
  amount: number | string
  remark: string | null
  usedAmount: number | string
  remainingAmount: number | string
  usageRate: number | string
  overBudget: boolean
  createTime: string
  updateTime: string
}

export interface BackendBudgetOverview {
  month: string
  totalBudget: number | string
  totalSpent: number | string
  remainingAmount: number | string
  usageRate: number | string
  overBudget: boolean
  categoryBudgets: BackendBudget[]
}

export interface BudgetCreateInput {
  month: string
  type: 'TOTAL' | 'CATEGORY'
  categoryId?: number
  amount: number
  remark?: string
}

export type BudgetUpdateInput = BudgetCreateInput

export type BillUpdateInput = BillCreateInput
export type CategoryUpdateInput = CategoryCreateInput
export type AccountUpdateInput = AccountCreateInput
