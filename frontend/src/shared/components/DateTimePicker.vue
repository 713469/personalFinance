<template>
  <div class="picker">
    <input
      type="datetime-local"
      :value="modelValue"
      class="input"
      @input="onInput"
    />
    <CalendarClock :size="16" class="icon" />
  </div>
</template>

<script setup lang="ts">
import { CalendarClock } from 'lucide-vue-next'

defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void
}>()

function onInput(event: Event) {
  emit('update:modelValue', (event.target as HTMLInputElement).value)
}
</script>

<style scoped>
.picker {
  position: relative;
  display: flex;
  align-items: center;
}

.input {
  width: 100%;
  min-height: 40px;
  padding: 0 36px 0 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-bg-surface);
  color: var(--color-text);
  font-size: 14px;
  font-family: inherit;
  transition:
    border-color var(--transition-fast),
    box-shadow var(--transition-fast);
}

.input::-webkit-calendar-picker-indicator {
  opacity: 0;
  position: absolute;
  right: 8px;
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-ring);
}

.icon {
  position: absolute;
  right: 10px;
  color: var(--color-text-muted);
  pointer-events: none;
}
</style>
