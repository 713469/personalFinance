<template>
  <Teleport to="body">
    <div v-if="mode" class="modal-overlay" @click.self="close">
      <section class="modal-card" :class="modalSizeClass" role="dialog" aria-modal="true">
        <header class="modal-header">
          <div>
            <p class="eyebrow">{{ eyebrow }}</p>
            <h2>{{ title }}</h2>
            <p class="modal-description">{{ description }}</p>
          </div>
          <button class="icon-button" type="button" @click="close" aria-label="关闭">
            <X :size="18" />
          </button>
        </header>

        <form class="modal-body" @submit.prevent="submit">
          <template v-if="mode === 'bill'">
            <div class="modal-grid">
              <label class="modal-field">
                <span>类型</span>
                <AppSelect v-model="billForm.type" :options="billTypeOptions" />
              </label>
              <label class="modal-field">
                <span>金额</span>
                <input v-model="billForm.amount" type="number" min="0.01" step="0.01" placeholder="请输入金额" />
              </label>
              <label class="modal-field">
                <span>分类</span>
                <AppSelect v-model="billForm.categoryId" :options="billCategoryOptions" />
              </label>
              <label class="modal-field">
                <span>账户</span>
                <AppSelect v-model="billForm.accountId" :options="accountOptions" />
              </label>
              <label class="modal-field modal-field-span">
                <span>发生时间</span>
                <input v-model="billForm.tradeTime" type="datetime-local" />
              </label>
              <label class="modal-field modal-field-span">
                <span>备注</span>
                <textarea v-model="billForm.remark" rows="3" placeholder="可选备注" />
              </label>
            </div>
          </template>

          <template v-else-if="mode === 'category'">
            <div class="modal-grid">
              <label class="modal-field">
                <span>名称</span>
                <input v-model="categoryForm.name" type="text" placeholder="例如 餐饮" />
              </label>
              <label class="modal-field">
                <span>类型</span>
                <AppSelect v-model="categoryForm.type" :options="billTypeOptions" />
              </label>
              <label class="modal-field">
                <span>图标</span>
                <input v-model="categoryForm.icon" type="text" placeholder="utensils" />
              </label>
              <label class="modal-field">
                <span>排序</span>
                <input v-model.number="categoryForm.sort" type="number" min="0" step="1" />
              </label>
              <label class="modal-field">
                <span>状态</span>
                <AppSelect v-model="categoryForm.status" :options="statusOptions" />
              </label>
            </div>
          </template>

          <template v-else>
            <div class="modal-grid">
              <label class="modal-field">
                <span>名称</span>
                <input v-model="accountForm.name" type="text" placeholder="例如 微信" />
              </label>
              <label class="modal-field">
                <span>类型</span>
                <AppSelect v-model="accountForm.type" :options="accountTypeOptions" />
              </label>
              <label class="modal-field">
                <span>初始余额</span>
                <input v-model="accountForm.initialBalance" type="number" min="0" step="0.01" />
              </label>
              <label class="modal-field modal-field-span">
                <span>备注</span>
                <textarea v-model="accountForm.remark" rows="3" placeholder="可选备注" />
              </label>
            </div>
          </template>

          <p v-if="error" class="modal-error">{{ error }}</p>

          <footer class="modal-actions">
            <button class="ghost-button" type="button" @click="close">取消</button>
            <button class="primary-action" type="submit" :disabled="saving">
              {{ saving ? '保存中...' : submitLabel }}
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
import { useFinanceStore } from '@/shared/stores/finance'
import { useUiStore } from '@/shared/stores/ui'

type QuickCreateMode = 'bill' | 'category' | 'account'

const ui = useUiStore()
const store = useFinanceStore()
const saving = ref(false)
const error = ref('')

const billForm = reactive({
  type: 'EXPENSE' as 'INCOME' | 'EXPENSE',
  amount: '',
  categoryId: 0,
  accountId: 0,
  tradeTime: toDateTimeLocalValue(new Date()),
  remark: '',
})

const categoryForm = reactive({
  name: '',
  type: 'EXPENSE' as 'INCOME' | 'EXPENSE',
  icon: 'circle',
  sort: 0,
  status: 1,
})

const accountForm = reactive({
  name: '',
  type: 'CASH',
  initialBalance: '0',
  remark: '',
})

const mode = computed(() => ui.quickCreateMode)
const modalSizeClass = computed(() => {
  if (mode.value === 'bill') {
    return 'modal-card--lg'
  }
  return 'modal-card--md'
})

const eyebrow = computed(() => {
  switch (mode.value) {
    case 'bill':
      return '快速创建 · 账单'
    case 'category':
      return '快速创建 · 分类'
    case 'account':
      return '快速创建 · 账户'
    default:
      return ''
  }
})

const title = computed(() => {
  switch (mode.value) {
    case 'bill':
      return '新增账单'
    case 'category':
      return '新增分类'
    case 'account':
      return '新增账户'
    default:
      return ''
  }
})

const description = computed(() => {
  switch (mode.value) {
    case 'bill':
      return '填完就能直接入库，并同步刷新首页和统计数据。'
    case 'category':
      return '用于账单归类，创建后会立刻出现在分类列表里。'
    case 'account':
      return '用于记录初始余额，创建后会刷新账户总览。'
    default:
      return ''
  }
})

const submitLabel = computed(() => {
  switch (mode.value) {
    case 'bill':
      return '保存账单'
    case 'category':
      return '保存分类'
    case 'account':
      return '保存账户'
    default:
      return '保存'
  }
})

const billCategories = computed(() =>
  store.categories.filter((category) => category.type === billForm.type),
)

const billTypeOptions = [
  { label: '支出', value: 'EXPENSE' },
  { label: '收入', value: 'INCOME' },
]

const billCategoryOptions = computed(() =>
  billCategories.value.map((category) => ({ label: category.name, value: category.id })),
)

const accountOptions = computed(() =>
  store.accounts.map((account) => ({ label: account.name, value: account.id })),
)

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 },
]

const accountTypeOptions = [
  { label: '现金', value: 'CASH' },
  { label: '微信', value: 'WECHAT' },
  { label: '银行卡', value: 'BANK' },
  { label: '支付宝', value: 'ALIPAY' },
  { label: '其他', value: 'OTHER' },
]

watch(
  () => mode.value,
  (nextMode) => {
    error.value = ''
    saving.value = false
    if (!nextMode) {
      return
    }

    if (nextMode === 'bill') {
      billForm.type = 'EXPENSE'
      billForm.amount = ''
      billForm.tradeTime = toDateTimeLocalValue(new Date())
      billForm.remark = ''
      syncBillCategory()
      syncBillAccount()
      return
    }

    if (nextMode === 'category') {
      categoryForm.name = ''
      categoryForm.type = 'EXPENSE'
      categoryForm.icon = 'circle'
      categoryForm.sort = 0
      categoryForm.status = 1
      return
    }

    accountForm.name = ''
    accountForm.type = 'CASH'
    accountForm.initialBalance = '0'
    accountForm.remark = ''
  },
  { immediate: true },
)

watch(
  () => billForm.type,
  () => {
    syncBillCategory()
  },
)

watch(
  () => billCategories.value,
  () => {
    syncBillCategory()
  },
)

watch(
  () => store.accounts,
  () => {
    syncBillAccount()
  },
)

function syncBillCategory() {
  if (!billCategories.value.length) {
    billForm.categoryId = 0
    return
  }

  const matched = billCategories.value.find((category) => category.id === billForm.categoryId)
  if (!matched) {
    billForm.categoryId = billCategories.value[0].id
  }
}

function syncBillAccount() {
  if (!store.accounts.length) {
    billForm.accountId = 0
    return
  }

  const matched = store.accounts.find((account) => account.id === billForm.accountId)
  if (!matched) {
    billForm.accountId = store.accounts[0].id
  }
}

function close() {
  ui.closeQuickCreate()
}

function toDateTimeLocalValue(date: Date) {
  const offset = date.getTimezoneOffset()
  const local = new Date(date.getTime() - offset * 60_000)
  return local.toISOString().slice(0, 16)
}

function toDateTimeString(value: string) {
  return `${value.length === 16 ? `${value}:00` : value}`
}

async function submit() {
  if (!mode.value) {
    return
  }

  error.value = ''
  saving.value = true

  try {
    if (mode.value === 'bill') {
      if (!billForm.categoryId || !billForm.accountId) {
        throw new Error('请先选择分类和账户')
      }
      await store.createBillRecord({
        type: billForm.type,
        amount: Number(billForm.amount),
        categoryId: billForm.categoryId,
        accountId: billForm.accountId,
        tradeTime: toDateTimeString(billForm.tradeTime),
        remark: billForm.remark || undefined,
      })
    } else if (mode.value === 'category') {
      await store.createCategoryRecord({
        name: categoryForm.name.trim(),
        type: categoryForm.type,
        icon: categoryForm.icon.trim() || undefined,
        sort: Number(categoryForm.sort),
        status: Number(categoryForm.status),
      })
    } else {
      await store.createAccountRecord({
        name: accountForm.name.trim(),
        type: accountForm.type,
        initialBalance: Number(accountForm.initialBalance),
        remark: accountForm.remark.trim() || undefined,
      })
    }
    close()
  } catch (caught) {
    error.value = caught instanceof Error ? caught.message : '保存失败'
  } finally {
    saving.value = false
  }
}
</script>
