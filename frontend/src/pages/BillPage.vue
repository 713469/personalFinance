<template>
  <div class="page-stack">
    <section class="page-header">
      <PageHeader title="账单管理" subtitle="分页、筛选、查看详情、编辑与删除都在这里。" />
      <button class="primary-action" type="button" v-ripple @click="openCreate">
        <Plus :size="18" />
        新增账单
      </button>
    </section>

    <section v-if="notice" class="page-banner" :class="`page-banner--${noticeTone}`">
      {{ notice }}
    </section>

    <section class="panel filter-panel">
      <div class="filter-grid filter-grid--bills">
        <label>
          <span>月份</span>
          <MonthPicker v-model="query.month" />
        </label>
        <label>
          <span>类型</span>
          <AppSelect v-model="query.type" :options="typeOptions" />
        </label>
        <label>
          <span>分类</span>
          <AppSelect v-model="query.categoryId" :options="categoryOptions" />
        </label>
        <label>
          <span>账户</span>
          <AppSelect v-model="query.accountId" :options="accountOptions" />
        </label>
        <label>
          <span>开始时间</span>
          <DateTimePicker v-model="query.startTime" />
        </label>
        <label>
          <span>结束时间</span>
          <DateTimePicker v-model="query.endTime" />
        </label>
      </div>

      <div class="filter-actions">
        <button class="ghost-button" type="button" @click="resetFilters">重置</button>
        <button class="primary-action" type="button" @click="loadBills">查询</button>
      </div>
    </section>

    <section class="panel">
      <div class="table-header bill-table-header">
        <span>时间</span>
        <span>分类 / 账户</span>
        <span>金额</span>
        <span>备注</span>
        <span>操作</span>
      </div>

      <EmptyState v-if="loading" title="正在加载账单..." variant="bill" />
      <template v-else-if="bills.length">
        <article v-for="bill in bills" :key="bill.id" class="table-row bill-row">
          <button class="row-link" type="button" @click="openDetail(bill)">
            {{ formatDateTime(bill.tradeTime) }}
          </button>
          <div class="bill-summary">
            <strong>{{ bill.categoryName }}</strong>
            <p>{{ bill.accountName }}</p>
          </div>
          <b :class="bill.type">{{ formatSignedCurrency(bill.amount, bill.type) }}</b>
          <span class="muted bill-remark">{{ bill.remark || '无备注' }}</span>
          <div class="inline-actions">
            <button class="ghost-button ghost-button--small" type="button" @click="openDetail(bill)">详情</button>
            <button class="ghost-button ghost-button--small" type="button" @click="openEdit(bill)">编辑</button>
            <button class="danger-button danger-button--small" type="button" @click="askDelete(bill)">删除</button>
          </div>
        </article>
      </template>
      <EmptyState v-else variant="bill" title="暂无账单数据" description="可以先新增一笔账单，看看账户余额和首页统计如何联动变化。" />

      <div class="table-footer bill-footer">
        <span>共 {{ total }} 条</span>
        <div class="pager">
          <button class="ghost-button ghost-button--small" type="button" :disabled="query.pageNum <= 1" @click="changePage(query.pageNum - 1)">
            上一页
          </button>
          <span>第 {{ query.pageNum }} / {{ totalPages }} 页</span>
          <button class="ghost-button ghost-button--small" type="button" :disabled="query.pageNum >= totalPages" @click="changePage(query.pageNum + 1)">
            下一页
          </button>
        </div>
      </div>
    </section>

    <BillFormDialog
      :open="formOpen"
      :saving="saving"
      :mode="formMode"
      :bill="editingBill"
      :accounts="store.accountsRaw"
      :categories="store.categoriesRaw"
      @close="closeForm"
      @save="submitBill"
    />

    <BillDetailDialog :open="detailOpen" :bill="detailBill" @close="detailOpen = false" />

    <ConfirmDialog
      :open="deleteOpen"
      title="删除账单"
      description="删除后会回滚对应账户余额，这个操作可以恢复统计的一致性。"
      confirm-text="确认删除"
      :loading="deleting"
      @cancel="deleteOpen = false"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { Plus } from 'lucide-vue-next'
import AppSelect from '@/shared/components/AppSelect.vue'
import BillDetailDialog from '@/shared/components/BillDetailDialog.vue'
import DateTimePicker from '@/shared/components/DateTimePicker.vue'
import MonthPicker from '@/shared/components/MonthPicker.vue'
import BillFormDialog from '@/shared/components/BillFormDialog.vue'
import ConfirmDialog from '@/shared/components/ConfirmDialog.vue'
import EmptyState from '@/shared/components/EmptyState.vue'
import PageHeader from '@/shared/components/PageHeader.vue'
import { useFinanceStore } from '@/shared/stores/finance'
import {
  createBill,
  deleteBill,
  fetchBills,
  updateBill,
} from '@/shared/api/finance'
import { formatDateTime, formatSignedCurrency } from '@/shared/utils/format'
import type { BackendBill, BillCreateInput } from '@/shared/types/backend'

const store = useFinanceStore()
const route = useRoute()

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  month: currentMonth(),
  type: 0 as number | string,
  categoryId: 0,
  accountId: 0,
  startTime: '',
  endTime: '',
})

const bills = ref<BackendBill[]>([])
const total = ref(0)
const loading = ref(false)
const notice = ref('')
const noticeTone = ref<'success' | 'error' | 'info'>('info')
const formOpen = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const editingBill = ref<BackendBill | null>(null)
const detailOpen = ref(false)
const detailBill = ref<BackendBill | null>(null)
const deleteOpen = ref(false)
const deleteTarget = ref<BackendBill | null>(null)
const saving = ref(false)
const deleting = ref(false)

const typeOptions = [
  { label: '全部', value: 0 },
  { label: '支出', value: 'EXPENSE' },
  { label: '收入', value: 'INCOME' },
]

const categoryOptions = computed(() => [
  { label: '全部分类', value: 0 },
  ...store.categoriesRaw.map((item) => ({ label: `${item.name} · ${item.type}`, value: item.id })),
])

const accountOptions = computed(() => [
  { label: '全部账户', value: 0 },
  ...store.accountsRaw.map((item) => ({ label: item.name, value: item.id })),
])

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / query.pageSize)))

onMounted(async () => {
  if (route.query.month && typeof route.query.month === 'string') {
    query.month = route.query.month
  }
  if (route.query.categoryId && typeof route.query.categoryId === 'string') {
    const id = parseInt(route.query.categoryId, 10)
    if (!isNaN(id)) {
      query.categoryId = id
    }
  }
  if (route.query.type && typeof route.query.type === 'string') {
    query.type = route.query.type
  }
  await Promise.all([store.initialize(), loadBills()])
})

function currentMonth() {
  return new Intl.DateTimeFormat('en-CA', { year: 'numeric', month: '2-digit' }).format(new Date())
}

function toDateTimeQuery(value: string) {
  if (!value) {
    return undefined
  }
  return `${value.length === 16 ? `${value}:00` : value}`
}

function showNotice(message: string, tone: 'success' | 'error' | 'info' = 'info') {
  notice.value = message
  noticeTone.value = tone
  window.setTimeout(() => {
    if (notice.value === message) {
      notice.value = ''
    }
  }, 2600)
}

async function loadBills() {
  loading.value = true
  try {
    const result = await fetchBills({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      month: query.month || undefined,
      type: query.type ? String(query.type) : undefined,
      categoryId: query.categoryId || undefined,
      accountId: query.accountId || undefined,
      startTime: toDateTimeQuery(query.startTime),
      endTime: toDateTimeQuery(query.endTime),
    })
    bills.value = result.records
    total.value = result.total
    query.pageNum = Number(result.current)
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '加载账单失败', 'error')
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  query.pageNum = 1
  query.month = currentMonth()
  query.type = 0
  query.categoryId = 0
  query.accountId = 0
  query.startTime = ''
  query.endTime = ''
  void loadBills()
}

function changePage(pageNum: number) {
  if (pageNum < 1 || pageNum > totalPages.value) {
    return
  }
  query.pageNum = pageNum
  void loadBills()
}

function openCreate() {
  formMode.value = 'create'
  editingBill.value = null
  formOpen.value = true
}

function openEdit(bill: BackendBill) {
  formMode.value = 'edit'
  editingBill.value = bill
  formOpen.value = true
}

function openDetail(bill: BackendBill) {
  detailBill.value = bill
  detailOpen.value = true
}

function askDelete(bill: BackendBill) {
  deleteTarget.value = bill
  deleteOpen.value = true
}

function closeForm() {
  formOpen.value = false
}

async function submitBill(payload: BillCreateInput) {
  saving.value = true
  try {
    if (formMode.value === 'edit' && editingBill.value) {
      await updateBill(editingBill.value.id, payload)
      showNotice('账单已更新', 'success')
    } else {
      await createBill(payload)
      showNotice('账单已新增', 'success')
    }
    formOpen.value = false
    editingBill.value = null
    await Promise.all([loadBills(), store.refresh()])
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '保存账单失败', 'error')
  } finally {
    saving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) {
    return
  }
  deleting.value = true
  try {
    await deleteBill(deleteTarget.value.id)
    showNotice('账单已删除', 'success')
    deleteOpen.value = false
    deleteTarget.value = null
    await Promise.all([loadBills(), store.refresh()])
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '删除账单失败', 'error')
  } finally {
    deleting.value = false
  }
}
</script>
