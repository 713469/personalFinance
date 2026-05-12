<template>
  <div class="page-stack">
    <section class="page-header">
      <PageHeader title="账户管理" subtitle="查看当前余额、初始余额，以及账户详情和维护操作。" />
      <button class="primary-action" type="button" v-ripple @click="openCreate">
        <Plus :size="18" />
        新增账户
      </button>
    </section>

    <section v-if="notice" class="page-banner" :class="`page-banner--${noticeTone}`">
      {{ notice }}
    </section>

    <section class="account-grid account-grid--table">
      <EmptyState v-if="loading" title="正在加载账户..." variant="account" />
      <template v-else-if="store.accountsRaw.length">
        <article v-for="account in store.accountsRaw" :key="account.id" class="account-card account-card--table">
          <button class="account-card-link" type="button" @click="openDetail(account)">
            <div class="account-head">
              <div>
                <span>{{ account.name }}</span>
                <p>{{ account.type }}</p>
              </div>
              <strong>{{ formatCurrency(account.currentBalance) }}</strong>
            </div>
          </button>
          <div class="account-meta">
            <span>初始余额 {{ formatCurrency(account.initialBalance) }}</span>
            <span>{{ account.remark || '无备注' }}</span>
          </div>
          <div class="inline-actions account-actions">
            <button class="ghost-button ghost-button--small" type="button" @click="openEdit(account)">编辑</button>
            <button class="ghost-button ghost-button--small" type="button" @click="openDetail(account)">详情</button>
            <button class="danger-button danger-button--small" type="button" @click="askDelete(account)">删除</button>
          </div>
        </article>
      </template>
      <EmptyState v-else variant="account" title="暂无账户数据" description="先创建一个账户，后续账单都会自动联动余额。" />
    </section>

    <AccountFormDialog
      :open="formOpen"
      :saving="saving"
      :mode="formMode"
      :account="editingAccount"
      @close="formOpen = false"
      @save="submitAccount"
    />

    <AccountDetailDialog :open="detailOpen" :account="detailAccount" @close="detailOpen = false" />

    <ConfirmDialog
      :open="deleteOpen"
      title="删除账户"
      description="如果该账户已有账单记录，将不允许删除。"
      confirm-text="确认删除"
      :loading="deleting"
      @cancel="deleteOpen = false"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import AccountDetailDialog from '@/shared/components/AccountDetailDialog.vue'
import AccountFormDialog from '@/shared/components/AccountFormDialog.vue'
import ConfirmDialog from '@/shared/components/ConfirmDialog.vue'
import EmptyState from '@/shared/components/EmptyState.vue'
import PageHeader from '@/shared/components/PageHeader.vue'
import { useFinanceStore } from '@/shared/stores/finance'
import { createAccount, deleteAccount, updateAccount } from '@/shared/api/finance'
import { formatCurrency } from '@/shared/utils/format'
import type { AccountCreateInput, BackendAccount } from '@/shared/types/backend'

const store = useFinanceStore()

const notice = ref('')
const noticeTone = ref<'success' | 'error' | 'info'>('info')
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const formOpen = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const editingAccount = ref<BackendAccount | null>(null)
const detailOpen = ref(false)
const detailAccount = ref<BackendAccount | null>(null)
const deleteOpen = ref(false)
const deleteTarget = ref<BackendAccount | null>(null)

onMounted(async () => {
  loading.value = true
  try {
    await store.initialize()
  } finally {
    loading.value = false
  }
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

function openCreate() {
  formMode.value = 'create'
  editingAccount.value = null
  formOpen.value = true
}

function openEdit(account: BackendAccount) {
  formMode.value = 'edit'
  editingAccount.value = account
  formOpen.value = true
}

function openDetail(account: BackendAccount) {
  detailAccount.value = account
  detailOpen.value = true
}

function askDelete(account: BackendAccount) {
  deleteTarget.value = account
  deleteOpen.value = true
}

async function submitAccount(payload: AccountCreateInput) {
  saving.value = true
  try {
    if (formMode.value === 'edit' && editingAccount.value) {
      await updateAccount(editingAccount.value.id, payload)
      showNotice('账户已更新', 'success')
    } else {
      await createAccount(payload)
      showNotice('账户已新增', 'success')
    }
    formOpen.value = false
    await store.refresh()
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '保存账户失败', 'error')
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
    await deleteAccount(deleteTarget.value.id)
    showNotice('账户已删除', 'success')
    deleteOpen.value = false
    deleteTarget.value = null
    await store.refresh()
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '删除账户失败', 'error')
  } finally {
    deleting.value = false
  }
}
</script>
