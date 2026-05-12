import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type {
  AccountItem,
  BillItem,
  BillPageState,
  BudgetItem,
  BudgetOverview,
  CategoryExpenseItem,
  CategoryItem,
  DashboardSummary,
  TrendPoint,
} from '@/shared/types/finance'
import type {
  AccountCreateInput,
  BackendAccount,
  BackendAccountBalance,
  BackendBill,
  BackendBudget,
  BackendBudgetOverview,
  BackendCategory,
  BackendSummary,
  BillCreateInput,
  BudgetCreateInput,
  BudgetUpdateInput,
  CategoryCreateInput,
} from '@/shared/types/backend'
import {
  createAccount,
  createBill,
  createBudget,
  createCategory,
  deleteBudget,
  fetchAccounts,
  fetchBills,
  fetchBudgetOverview,
  fetchBudgetsByMonth,
  fetchCategories,
  fetchCategoryExpense,
  fetchAccountBalance,
  fetchMonthlyTrend,
  fetchSummary,
  updateBudget,
} from '@/shared/api/finance'
import { formatDateTime, formatMonthLabel, toNumber } from '@/shared/utils/format'
import { pickAccentColor } from '@/shared/utils/color'

const chartPalette = ['#14b8a6', '#f97316', '#8b5cf6', '#0ea5e9', '#ef4444', '#22c55e']

function currentMonth() {
  return new Intl.DateTimeFormat('en-CA', {
    year: 'numeric',
    month: '2-digit',
  }).format(new Date())
}

function normalizeError(error: unknown) {
  if (error instanceof Error && error.message) {
    return error.message
  }
  return '数据同步失败'
}

function mapAccount(account: BackendAccount): AccountItem {
  return {
    id: account.id,
    name: account.name,
    type: account.type,
    initialBalance: toNumber(account.initialBalance),
    currentBalance: toNumber(account.currentBalance),
    remark: account.remark ?? '',
  }
}

function mapCategory(category: BackendCategory, index: number): CategoryItem {
  return {
    id: category.id,
    name: category.name,
    type: category.type,
    icon: category.icon ?? 'circle',
    color: pickAccentColor(`${category.name}-${category.id}-${index}`),
  }
}

function mapBill(bill: BackendBill, index: number): BillItem {
  return {
    id: bill.id,
    type: bill.type,
    amount: toNumber(bill.amount),
    category: bill.categoryName,
    account: bill.accountName,
    tradeTime: formatDateTime(bill.tradeTime),
    remark: bill.remark ?? '',
    title: bill.remark?.trim() || bill.categoryName,
    dotColor: pickAccentColor(`${bill.categoryName}-${bill.accountName}-${bill.id}-${index}`),
  }
}

function mapSummary(month: string, summary: BackendSummary): DashboardSummary {
  return {
    month,
    income: toNumber(summary.income),
    expense: toNumber(summary.expense),
    balance: toNumber(summary.balance),
    totalAssets: toNumber(summary.totalAssets),
  }
}

function mapBudget(budget: BackendBudget): BudgetItem {
  return {
    id: budget.id,
    month: budget.month,
    type: budget.type,
    categoryId: budget.categoryId,
    categoryName: budget.categoryName,
    amount: toNumber(budget.amount),
    remark: budget.remark,
    usedAmount: toNumber(budget.usedAmount),
    remainingAmount: toNumber(budget.remainingAmount),
    usageRate: toNumber(budget.usageRate),
    overBudget: budget.overBudget,
  }
}

function mapBudgetOverview(overview: BackendBudgetOverview): BudgetOverview {
  return {
    month: overview.month,
    totalBudget: toNumber(overview.totalBudget),
    totalSpent: toNumber(overview.totalSpent),
    remainingAmount: toNumber(overview.remainingAmount),
    usageRate: toNumber(overview.usageRate),
    overBudget: overview.overBudget,
    categoryBudgets: overview.categoryBudgets.map(mapBudget),
  }
}

export const useFinanceStore = defineStore('finance', () => {
  const summary = ref<DashboardSummary | null>(null)
  const bills = ref<BillItem[]>([])
  const accounts = ref<AccountItem[]>([])
  const categories = ref<CategoryItem[]>([])
  const accountsRaw = ref<BackendAccount[]>([])
  const categoriesRaw = ref<BackendCategory[]>([])
  const accountBalances = ref<BackendAccountBalance[]>([])
  const categoryExpense = ref<CategoryExpenseItem[]>([])
  const monthlyTrend = ref<TrendPoint[]>([])
  const billPage = ref<BillPageState>({ total: 0, current: 1, size: 10 })
  const budgetList = ref<BudgetItem[]>([])
  const budgetOverview = ref<BudgetOverview | null>(null)
  const loading = ref(false)
  const ready = ref(false)
  const error = ref('')

  async function refresh() {
    if (loading.value) {
      return
    }

    loading.value = true
    error.value = ''

    try {
      const month = currentMonth()
      const [summaryData, accountData, categoryData, billData, expenseData, trendData] = await Promise.all([
        fetchSummary(month),
        fetchAccounts(),
        fetchCategories(),
        fetchBills({ pageNum: 1, pageSize: 10 }),
        fetchCategoryExpense(month),
        fetchMonthlyTrend(6),
      ])

      summary.value = mapSummary(month, summaryData)
      accountsRaw.value = accountData
      accounts.value = accountData.map(mapAccount)
      categoriesRaw.value = categoryData
      categories.value = categoryData.map(mapCategory)
      bills.value = billData.records.map(mapBill)
      billPage.value = {
        total: billData.total,
        current: billData.current,
        size: billData.size,
      }
      categoryExpense.value = expenseData.map((item, index) => ({
        categoryId: item.categoryId,
        name: item.categoryName,
        amount: toNumber(item.amount),
        percent: toNumber(item.percent),
        color: chartPalette[index % chartPalette.length] ?? pickAccentColor(item.categoryName),
      }))
      monthlyTrend.value = trendData.map((item) => ({
        month: item.month,
        monthLabel: formatMonthLabel(item.month),
        income: toNumber(item.income),
        expense: toNumber(item.expense),
      }))
      accountBalances.value = await fetchAccountBalance()
      ready.value = true
    } catch (caught) {
      error.value = normalizeError(caught)
    } finally {
      loading.value = false
    }
  }

  async function initialize() {
    if (!ready.value) {
      await refresh()
    }
  }

  async function createAccountRecord(payload: AccountCreateInput) {
    await createAccount(payload)
    await refresh()
  }

  async function createCategoryRecord(payload: CategoryCreateInput) {
    await createCategory(payload)
    await refresh()
  }

  async function createBillRecord(payload: BillCreateInput) {
    await createBill(payload)
    await refresh()
  }

  async function loadBudgets(month: string) {
    const data = await fetchBudgetsByMonth(month)
    budgetList.value = data.map(mapBudget)
  }

  async function loadBudgetOverview(month: string) {
    const data = await fetchBudgetOverview(month)
    budgetOverview.value = mapBudgetOverview(data)
  }

  async function createBudgetRecord(payload: BudgetCreateInput) {
    await createBudget(payload)
  }

  async function updateBudgetRecord(id: number, payload: BudgetUpdateInput) {
    await updateBudget(id, payload)
  }

  async function deleteBudgetRecord(id: number) {
    await deleteBudget(id)
  }

  const monthBudget = computed(() => budgetList.value.find((b) => b.type === 'TOTAL') ?? null)

  const monthIncome = computed(() => summary.value?.income ?? 0)
  const monthExpense = computed(() => summary.value?.expense ?? 0)
  const monthBalance = computed(() => summary.value?.balance ?? monthIncome.value - monthExpense.value)
  const totalAssets = computed(() => summary.value?.totalAssets ?? accounts.value.reduce((sum, account) => sum + account.currentBalance, 0))
  const recentBills = computed(() => bills.value.slice(0, 5))
  const expenseCategories = computed(() =>
    categories.value.filter((category) => category.type === 'EXPENSE'),
  )
  const incomeCategories = computed(() =>
    categories.value.filter((category) => category.type === 'INCOME'),
  )

  return {
    summary,
    bills,
    accounts,
    categories,
    accountsRaw,
    categoriesRaw,
    accountBalances,
    billPage,
    loading,
    ready,
    error,
    initialize,
    refresh,
    createAccountRecord,
    createCategoryRecord,
    createBillRecord,
    budgetList,
    budgetOverview,
    monthBudget,
    loadBudgets,
    loadBudgetOverview,
    createBudgetRecord,
    updateBudgetRecord,
    deleteBudgetRecord,
    monthIncome,
    monthExpense,
    monthBalance,
    totalAssets,
    recentBills,
    categoryExpense,
    monthlyTrend,
    expenseCategories,
    incomeCategories,
  }
})
