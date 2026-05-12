<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @click.self="emit('close')">
      <section class="modal-card modal-card--md" role="dialog" aria-modal="true">
        <header class="modal-header">
          <div>
            <p class="eyebrow">账单详情</p>
            <h2>账单明细</h2>
            <p class="modal-description">查看账单的完整信息。</p>
          </div>
          <button class="icon-button" type="button" @click="emit('close')" aria-label="关闭">
            <X :size="18" />
          </button>
        </header>

        <div v-if="bill" class="modal-body detail-grid">
          <div class="detail-item"><span>类型</span><strong>{{ bill.type }}</strong></div>
          <div class="detail-item"><span>金额</span><strong>{{ bill.amount }}</strong></div>
          <div class="detail-item"><span>分类</span><strong>{{ bill.categoryName }}</strong></div>
          <div class="detail-item"><span>账户</span><strong>{{ bill.accountName }}</strong></div>
          <div class="detail-item"><span>时间</span><strong>{{ bill.tradeTime }}</strong></div>
          <div class="detail-item detail-item--full"><span>备注</span><strong>{{ bill.remark || '无' }}</strong></div>
        </div>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { X } from 'lucide-vue-next'
import type { BackendBill } from '@/shared/types/backend'

defineProps<{
  open: boolean
  bill?: BackendBill | null
}>()

const emit = defineEmits<{
  (event: 'close'): void
}>()
</script>
