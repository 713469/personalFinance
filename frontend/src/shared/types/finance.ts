export interface BillItem {
  id: number
  type: 'INCOME' | 'EXPENSE'
  amount: number
  category: string
  account: string
  tradeTime: string
  remark: string
  title: string
  dotColor: string
}

export interface AccountItem {
  id: number
  name: string
  type: string
  initialBalance: number
  currentBalance: number
  remark: string
}

export interface CategoryItem {
  id: number
  name: string
  type: 'INCOME' | 'EXPENSE'
  icon: string
  color: string
}

export interface TrendPoint {
  month: string
  monthLabel: string
  income: number
  expense: number
}

export interface CategoryExpenseItem {
  categoryId: number
  name: string
  amount: number
  percent: number
  color: string
}

export interface DashboardSummary {
  month: string
  income: number
  expense: number
  balance: number
  totalAssets: number
}

export interface BudgetItem {
  id: number
  month: string
  type: 'TOTAL' | 'CATEGORY'
  categoryId: number | null
  categoryName: string | null
  amount: number
  remark: string | null
  usedAmount: number
  remainingAmount: number
  usageRate: number
  overBudget: boolean
}

export interface BudgetOverview {
  month: string
  totalBudget: number
  totalSpent: number
  remainingAmount: number
  usageRate: number
  overBudget: boolean
  categoryBudgets: BudgetItem[]
}

export interface BillPageState {
  total: number
  current: number
  size: number
}
