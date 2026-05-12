<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @click.self="emit('cancel')">
      <section class="modal-card modal-card--md confirm-card" role="dialog" aria-modal="true">
        <header class="modal-header">
          <div>
            <p class="eyebrow">{{ eyebrow }}</p>
            <h2>{{ title }}</h2>
            <p class="modal-description">{{ description }}</p>
          </div>
        </header>

        <footer class="modal-actions confirm-actions">
          <button class="ghost-button" type="button" @click="emit('cancel')">取消</button>
          <button class="danger-button" type="button" :disabled="loading" @click="emit('confirm')">
            {{ loading ? '处理中...' : confirmText }}
          </button>
        </footer>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
defineProps<{
  open: boolean
  title: string
  description: string
  confirmText?: string
  eyebrow?: string
  loading?: boolean
}>()

const emit = defineEmits<{
  (event: 'confirm'): void
  (event: 'cancel'): void
}>()
</script>
