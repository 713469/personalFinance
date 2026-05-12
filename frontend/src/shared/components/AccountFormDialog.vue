<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @click.self="emit('close')">
      <section class="modal-card modal-card--md" role="dialog" aria-modal="true">
        <header class="modal-header">
          <div>
            <p class="eyebrow">账户管理</p>
            <h2>{{ mode === 'edit' ? '编辑账户' : '新增账户' }}</h2>
            <p class="modal-description">账户余额由账单自动维护，编辑时不会直接改余额。</p>
          </div>
          <button class="icon-button" type="button" @click="emit('close')" aria-label="关闭">
            <X :size="18" />
          </button>
        </header>

        <form class="modal-body" @submit.prevent="submit">
          <div class="modal-grid">
            <label class="modal-field">
              <span>名称</span>
              <input v-model="form.name" type="text" placeholder="例如 微信" />
            </label>
            <label class="modal-field">
              <span>类型</span>
              <AppSelect v-model="form.type" :options="typeOptions" />
            </label>
            <label class="modal-field">
              <span>初始余额</span>
              <input v-model="form.initialBalance" type="number" min="0" step="0.01" :disabled="mode === 'edit'" />
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
              {{ saving ? '保存中...' : (mode === 'edit' ? '保存修改' : '保存账户') }}
            </button>
          </footer>
        </form>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { X } from 'lucide-vue-next'
import AppSelect from './AppSelect.vue'
import type { AccountCreateInput, BackendAccount } from '@/shared/types/backend'

const props = defineProps<{
  open: boolean
  saving?: boolean
  mode: 'create' | 'edit'
  account?: BackendAccount | null
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'save', payload: AccountCreateInput): void
}>()

const error = ref('')
const form = reactive({
  name: '',
  type: 'CASH',
  initialBalance: '0',
  remark: '',
})

const typeOptions = [
  { label: '现金', value: 'CASH' },
  { label: '微信', value: 'WECHAT' },
  { label: '银行卡', value: 'BANK' },
  { label: '支付宝', value: 'ALIPAY' },
  { label: '其他', value: 'OTHER' },
]

watch(
  () => [props.open, props.account],
  () => {
    error.value = ''
    if (!props.open) {
      return
    }
    form.name = props.account?.name ?? ''
    form.type = props.account?.type ?? 'CASH'
    form.initialBalance = String(props.account?.initialBalance ?? 0)
    form.remark = props.account?.remark ?? ''
  },
  { immediate: true },
)

function submit() {
  error.value = ''
  if (!form.name.trim()) {
    error.value = '请输入账户名称'
    return
  }
  emit('save', {
    name: form.name.trim(),
    type: form.type,
    initialBalance: Number(form.initialBalance),
    remark: form.remark.trim() || undefined,
  })
}
</script>
