<template>
  <div class="page-stack">
    <section class="page-header">
      <PageHeader title="预算管理" subtitle="设定月度预算，实时跟踪支出进度。" />
      <button class="primary-action" type="button" v-ripple @click="openCreate">
        <Plus :size="18" />
        新增预算
      </button>
    </section>

    <section v-if="notice" class="page-banner" :class="`page-banner--${noticeTone}`">
      {{ notice }}
    </section>

    <section class="panel">
      <div class="budget-month-bar">
        <label>
          <span>预算月份</span>
          <MonthPicker v-model="selectedMonth" @update:model-value="loadData" />
        </label>
      </div>
    </section>

    <section class="budget-overview-card" v-if="!loading && overview">
      <div class="budget-overview-header">
        <h3>总预算</h3>
        <span v-if="overview.totalBudget > 0" class="budget-status" :class="overview.overBudget ? 'budget-status--over' : 'budget-status--ok'">
          {{ overview.overBudget ? '已超支' : '预算内' }}
        </span>
        <span v-else class="budget-status budget-status--none">未设置</span>
        <button v-if="store.monthBudget" class="ghost-button ghost-button--small budget-edit-btn" type="button" @click="openEdit(store.monthBudget!)">
          <Pencil :size="14" />
          编辑
        </button>
      </div>
      <div class="budget-overview-grid" v-if="overview.totalBudget > 0">
        <div class="budget-metric">
          <span class="budget-metric-label">预算金额</span>
          <strong class="budget-metric-value">{{ formatCurrency(overview.totalBudget) }}</strong>
        </div>
        <div class="budget-metric">
          <span class="budget-metric-label">已支出</span>
          <strong class="budget-metric-value" :class="{ 'text-danger': overview.overBudget }">{{ formatCurrency(overview.totalSpent) }}</strong>
        </div>
        <div class="budget-metric">
          <span class="budget-metric-label">剩余</span>
          <strong class="budget-metric-value" :class="{ 'text-danger': overview.remainingAmount < 0 }">{{ formatCurrency(overview.remainingAmount) }}</strong>
        </div>
        <div class="budget-metric">
          <span class="budget-metric-label">使用率</span>
          <strong class="budget-metric-value" :class="{ 'text-danger': overview.overBudget }">{{ overview.usageRate }}%</strong>
        </div>
      </div>
      <div class="budget-progress" v-if="overview.totalBudget > 0">
        <div class="budget-progress-bar">
          <div class="budget-progress-fill" :class="{ 'budget-progress-fill--over': overview.overBudget }" :style="{ width: Math.min(overview.usageRate, 100) + '%' }" />
        </div>
      </div>
    </section>

    <section class="panel">
      <div class="panel-header">
        <h2>分类预算</h2>
        <span class="panel-subtitle">各支出分类预算与使用情况</span>
      </div>

      <EmptyState v-if="loading" title="正在加载预算..." variant="bill" />
      <template v-else-if="budgets.length">
        <div class="table-header budget-table-header">
          <span>分类</span>
          <span>预算金额</span>
          <span>已使用</span>
          <span>剩余</span>
          <span>使用率</span>
          <span>操作</span>
        </div>
        <article v-for="budget in budgets" :key="budget.id" class="table-row budget-row" :class="{ 'budget-row--over': budget.overBudget }">
          <strong>{{ budget.categoryName }}</strong>
          <span>{{ formatCurrency(budget.amount) }}</span>
          <span :class="{ 'text-danger': budget.overBudget }">{{ formatCurrency(budget.usedAmount) }}</span>
          <span :class="{ 'text-danger': budget.remainingAmount < 0 }">{{ formatCurrency(budget.remainingAmount) }}</span>
          <span class="budget-usage-cell">
            <span class="budget-usage-text" :class="{ 'text-danger': budget.overBudget }">{{ budget.usageRate }}%</span>
            <span v-if="budget.overBudget" class="budget-over-tag">超支</span>
          </span>
          <div class="inline-actions">
            <button class="ghost-button ghost-button--small" type="button" @click="openEdit(budget)">编辑</button>
            <button class="danger-button danger-button--small" type="button" @click="askDelete(budget)">删除</button>
          </div>
        </article>
      </template>
      <EmptyState v-else variant="category" title="暂无分类预算" description="新增一笔分类预算，开始跟踪各分类支出。" />
    </section>

    <BudgetFormDialog
      :open="formOpen"
      :saving="saving"
      :mode="formMode"
      :budget="editingBudget"
      @close="closeForm"
      @save="submitBudget"
    />

    <ConfirmDialog
      :open="deleteOpen"
      title="删除预算"
      description="删除预算不会影响账单数据，仅移除预算记录。"
      confirm-text="确认删除"
      :loading="deleting"
      @cancel="deleteOpen = false"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Pencil, Plus } from 'lucide-vue-next'
import BudgetFormDialog from '@/shared/components/BudgetFormDialog.vue'
import ConfirmDialog from '@/shared/components/ConfirmDialog.vue'
import MonthPicker from '@/shared/components/MonthPicker.vue'
import EmptyState from '@/shared/components/EmptyState.vue'
import PageHeader from '@/shared/components/PageHeader.vue'
import { useFinanceStore } from '@/shared/stores/finance'
import { formatCurrency } from '@/shared/utils/format'
import type { BudgetCreateInput, BudgetUpdateInput } from '@/shared/types/backend'
import type { BudgetItem } from '@/shared/types/finance'

const store = useFinanceStore()

const selectedMonth = ref(currentMonth())
const loading = ref(false)
const notice = ref('')
const noticeTone = ref<'success' | 'error' | 'info'>('info')
const formOpen = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const editingBudget = ref<BudgetItem | null>(null)
const deleteOpen = ref(false)
const deleteTarget = ref<BudgetItem | null>(null)
const saving = ref(false)
const deleting = ref(false)

const budgets = computed(() => store.budgetList.filter((b) => b.type === 'CATEGORY'))
const overview = computed(() => store.budgetOverview)

function currentMonth() {
  return new Intl.DateTimeFormat('en-CA', { year: 'numeric', month: '2-digit' }).format(new Date())
}

onMounted(async () => {
  await store.initialize()
  await loadData()
})

function showNotice(message: string, tone: 'success' | 'error' | 'info' = 'info') {
  notice.value = message
  noticeTone.value = tone
  window.setTimeout(() => {
    if (notice.value === message) {
      notice.value = ''
    }
  }, 2600)
}

async function loadData() {
  loading.value = true
  try {
    await Promise.all([
      store.loadBudgets(selectedMonth.value),
      store.loadBudgetOverview(selectedMonth.value),
    ])
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '加载预算失败', 'error')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  formMode.value = 'create'
  editingBudget.value = null
  formOpen.value = true
}

function openEdit(budget: BudgetItem) {
  formMode.value = 'edit'
  editingBudget.value = budget
  formOpen.value = true
}

function closeForm() {
  formOpen.value = false
}

function askDelete(budget: BudgetItem) {
  deleteTarget.value = budget
  deleteOpen.value = true
}

async function submitBudget(payload: BudgetCreateInput) {
  saving.value = true
  try {
    if (formMode.value === 'edit' && editingBudget.value) {
      const updatePayload: BudgetUpdateInput = {
        month: editingBudget.value.month,
        type: editingBudget.value.type,
        categoryId: editingBudget.value.categoryId ?? undefined,
        amount: payload.amount,
        remark: payload.remark,
      }
      await store.updateBudgetRecord(editingBudget.value.id, updatePayload)
      showNotice('预算已更新', 'success')
    } else {
      await store.createBudgetRecord(payload)
      showNotice('预算已新增', 'success')
    }
    formOpen.value = false
    await loadData()
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '保存预算失败', 'error')
  } finally {
    saving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await store.deleteBudgetRecord(deleteTarget.value.id)
    showNotice('预算已删除', 'success')
    deleteOpen.value = false
    await loadData()
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '删除预算失败', 'error')
  } finally {
    deleting.value = false
  }
}
</script>
