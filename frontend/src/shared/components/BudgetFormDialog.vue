<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-card modal-card--md">
        <div class="modal-header">
          <div>
            <p class="modal-eyebrow">预算管理</p>
            <h3>{{ mode === 'create' ? '新增预算' : '编辑预算' }}</h3>
          </div>
        </div>

        <div class="modal-body">
          <div class="modal-grid">
            <label class="modal-field">
              <span>预算月份</span>
              <MonthPicker v-model="form.month" :disabled="mode === 'edit'" />
            </label>

            <label class="modal-field">
              <span>预算类型</span>
              <AppSelect v-model="form.type" :options="typeOptions" :disabled="mode === 'edit'" />
            </label>

            <label v-if="form.type === 'CATEGORY'" class="modal-field">
              <span>分类</span>
              <AppSelect v-model="form.categoryId" :options="categoryOptions" :disabled="mode === 'edit'" />
            </label>

            <label class="modal-field">
              <span>预算金额</span>
              <input v-model="form.amount" type="number" step="0.01" min="0.01" placeholder="请输入预算金额" />
            </label>

            <label class="modal-field modal-field--span">
              <span>备注</span>
              <input v-model="form.remark" type="text" placeholder="可选备注" />
            </label>
          </div>

          <p v-if="error" class="modal-error">{{ error }}</p>
        </div>

        <div class="modal-actions">
          <button class="ghost-button" type="button" @click="$emit('close')" :disabled="saving">取消</button>
          <button class="primary-action" type="button" :disabled="saving" @click="submit">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import AppSelect from '@/shared/components/AppSelect.vue'
import MonthPicker from '@/shared/components/MonthPicker.vue'
import { useFinanceStore } from '@/shared/stores/finance'
import type { BudgetItem } from '@/shared/types/finance'
import type { BudgetCreateInput } from '@/shared/types/backend'

const props = defineProps<{
  open: boolean
  saving?: boolean
  mode: 'create' | 'edit'
  budget?: BudgetItem | null
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'save', payload: BudgetCreateInput): void
}>()

const store = useFinanceStore()
const error = ref('')

const form = reactive({
  month: currentMonth(),
  type: 'TOTAL' as 'TOTAL' | 'CATEGORY',
  categoryId: 0,
  amount: '',
  remark: '',
})

const typeOptions = [
  { label: '总预算', value: 'TOTAL' },
  { label: '分类预算', value: 'CATEGORY' },
]

const categoryOptions = computed(() => [
  { label: '请选择分类', value: 0 },
  ...store.categoriesRaw
    .filter((item) => item.type === 'EXPENSE')
    .map((item) => ({ label: item.name, value: item.id })),
])

function currentMonth() {
  return new Intl.DateTimeFormat('en-CA', { year: 'numeric', month: '2-digit' }).format(new Date())
}

watch(
  () => [props.open, props.budget],
  () => {
    if (!props.open) return
    error.value = ''
    if (props.mode === 'edit' && props.budget) {
      form.month = props.budget.month
      form.type = props.budget.type
      form.categoryId = props.budget.categoryId ?? 0
      form.amount = String(props.budget.amount)
      form.remark = props.budget.remark ?? ''
    } else {
      form.month = currentMonth()
      form.type = 'TOTAL'
      form.categoryId = 0
      form.amount = ''
      form.remark = ''
    }
  },
)

function submit() {
  error.value = ''
  if (!form.amount || Number(form.amount) <= 0) {
    error.value = '预算金额必须大于 0'
    return
  }
  if (form.type === 'CATEGORY' && !form.categoryId) {
    error.value = '分类预算必须选择分类'
    return
  }

  const payload: BudgetCreateInput = {
    month: form.month,
    type: form.type,
    amount: Number(form.amount),
    remark: form.remark || undefined,
  }
  if (form.type === 'CATEGORY' && form.categoryId) {
    payload.categoryId = form.categoryId
  }
  emit('save', payload)
}
</script>
