<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @click.self="emit('close')">
      <section class="modal-card modal-card--lg" role="dialog" aria-modal="true">
        <header class="modal-header">
          <div>
            <p class="eyebrow">账单管理</p>
            <h2>{{ mode === 'edit' ? '编辑账单' : '新增账单' }}</h2>
            <p class="modal-description">保存后会自动同步账户余额与首页统计。</p>
          </div>
          <button class="icon-button" type="button" @click="emit('close')" aria-label="关闭">
            <X :size="18" />
          </button>
        </header>

        <form class="modal-body" @submit.prevent="submit">
          <div class="modal-grid">
            <label class="modal-field">
              <span>类型</span>
              <AppSelect v-model="form.type" :options="typeOptions" />
            </label>
            <label class="modal-field">
              <span>金额</span>
              <input v-model="form.amount" type="number" min="0.01" step="0.01" placeholder="请输入金额" />
            </label>
            <label class="modal-field">
              <span>分类</span>
              <AppSelect v-model="form.categoryId" :options="categoryOptions" />
            </label>
            <label class="modal-field">
              <span>账户</span>
              <AppSelect v-model="form.accountId" :options="accountOptions" />
            </label>
            <label class="modal-field modal-field-span">
              <span>交易时间</span>
              <input v-model="form.tradeTime" type="datetime-local" />
            </label>
            <label class="modal-field modal-field-span">
              <span>备注</span>
              <textarea v-model="form.remark" rows="3" placeholder="可选备注"></textarea>
            </label>
          </div>

          <p v-if="error" class="modal-error">{{ error }}</p>

          <footer class="modal-actions">
            <button class="ghost-button" type="button" @click="emit('close')">取消</button>
            <button class="primary-action" type="submit" :disabled="saving">
              {{ saving ? '保存中...' : (mode === 'edit' ? '保存修改' : '保存账单') }}
            </button>
          </footer>
        </form>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { X } from 'lucide-vue-next'
import AppSelect from './AppSelect.vue'
import type { BackendAccount, BackendBill, BackendCategory, BillCreateInput } from '@/shared/types/backend'

const props = defineProps<{
  open: boolean
  saving?: boolean
  mode: 'create' | 'edit'
  bill?: BackendBill | null
  accounts: BackendAccount[]
  categories: BackendCategory[]
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'save', payload: BillCreateInput): void
}>()

const error = ref('')
const form = reactive({
  type: 'EXPENSE' as 'INCOME' | 'EXPENSE',
  amount: '',
  categoryId: 0,
  accountId: 0,
  tradeTime: toLocalDatetime(new Date()),
  remark: '',
})

const typeOptions = [
  { label: '支出', value: 'EXPENSE' },
  { label: '收入', value: 'INCOME' },
]

const categoryOptions = computed(() =>
  props.categories
    .filter((item) => item.type === form.type)
    .map((item) => ({ label: item.name, value: item.id })),
)

const accountOptions = computed(() =>
  props.accounts.map((item) => ({ label: item.name, value: item.id })),
)

watch(
  () => [props.open, props.bill],
  () => {
    error.value = ''
    if (!props.open) {
      return
    }
    const bill = props.bill
    form.type = bill?.type ?? 'EXPENSE'
    form.amount = bill ? String(bill.amount) : ''
    form.categoryId = bill?.categoryId ?? categoryOptions.value[0]?.value ?? 0
    form.accountId = bill?.accountId ?? accountOptions.value[0]?.value ?? 0
    form.tradeTime = bill ? toLocalDatetime(new Date(bill.tradeTime)) : toLocalDatetime(new Date())
    form.remark = bill?.remark ?? ''
    if (!categoryOptions.value.some((item) => item.value === form.categoryId)) {
      form.categoryId = categoryOptions.value[0]?.value ?? 0
    }
    if (!accountOptions.value.some((item) => item.value === form.accountId)) {
      form.accountId = accountOptions.value[0]?.value ?? 0
    }
  },
  { immediate: true },
)

watch(
  () => form.type,
  () => {
    if (!categoryOptions.value.some((item) => item.value === form.categoryId)) {
      form.categoryId = categoryOptions.value[0]?.value ?? 0
    }
  },
)

function toLocalDatetime(date: Date) {
  const offset = date.getTimezoneOffset()
  const local = new Date(date.getTime() - offset * 60_000)
  return local.toISOString().slice(0, 16)
}

function submit() {
  error.value = ''
  const amount = Number(form.amount)
  if (!form.categoryId || !form.accountId) {
    error.value = '请选择分类和账户'
    return
  }
  if (!amount || amount <= 0) {
    error.value = '金额必须大于 0'
    return
  }
  emit('save', {
    type: form.type,
    amount,
    categoryId: Number(form.categoryId),
    accountId: Number(form.accountId),
    tradeTime: `${form.tradeTime.length === 16 ? `${form.tradeTime}:00` : form.tradeTime}`,
    remark: form.remark.trim() || undefined,
  })
}
</script>
