<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @click.self="emit('close')">
      <section class="modal-card modal-card--md" role="dialog" aria-modal="true">
        <header class="modal-header">
          <div>
            <p class="eyebrow">账户详情</p>
            <h2>账户明细</h2>
            <p class="modal-description">查看账户的初始余额与当前余额。</p>
          </div>
          <button class="icon-button" type="button" @click="emit('close')" aria-label="关闭">
            <X :size="18" />
          </button>
        </header>

        <div v-if="account" class="modal-body detail-grid">
          <div class="detail-item"><span>名称</span><strong>{{ account.name }}</strong></div>
          <div class="detail-item"><span>类型</span><strong>{{ account.type }}</strong></div>
          <div class="detail-item"><span>初始余额</span><strong>{{ formatCurrency(account.initialBalance) }}</strong></div>
          <div class="detail-item"><span>当前余额</span><strong>{{ formatCurrency(account.currentBalance) }}</strong></div>
          <div class="detail-item detail-item--full"><span>备注</span><strong>{{ account.remark || '无' }}</strong></div>
        </div>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { X } from 'lucide-vue-next'
import { formatCurrency } from '@/shared/utils/format'
import type { BackendAccount } from '@/shared/types/backend'

defineProps<{
  open: boolean
  account?: BackendAccount | null
}>()

const emit = defineEmits<{
  (event: 'close'): void
}>()
</script>
