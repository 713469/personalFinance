<template>
  <div class="picker">
    <select
      :value="year"
      class="select"
      :disabled="disabled"
      @change="onYearChange(Number(($event.target as HTMLSelectElement).value))"
    >
      <option v-for="y in years" :key="y" :value="y">{{ y }} 年</option>
    </select>
    <select
      :value="month"
      class="select"
      :disabled="disabled"
      @change="onMonthChange(Number(($event.target as HTMLSelectElement).value))"
    >
      <option v-for="m in 12" :key="m" :value="m">{{ m }} 月</option>
    </select>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  modelValue: string
  disabled?: boolean
}>(), {
  disabled: false,
})

const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void
}>()

const year = computed(() => {
  if (!props.modelValue) return new Date().getFullYear()
  return parseInt(props.modelValue.substring(0, 4), 10)
})

const month = computed(() => {
  if (!props.modelValue) return new Date().getMonth() + 1
  return parseInt(props.modelValue.substring(5, 7), 10)
})

const currentYear = new Date().getFullYear()
const years = computed(() => {
  const list: number[] = []
  for (let y = currentYear - 5; y <= currentYear + 5; y++) {
    list.push(y)
  }
  return list
})

function onYearChange(value: number) {
  emit('update:modelValue', `${value}-${String(month.value).padStart(2, '0')}`)
}

function onMonthChange(value: number) {
  emit('update:modelValue', `${year.value}-${String(value).padStart(2, '0')}`)
}
</script>

<style scoped>
.picker {
  display: flex;
  gap: 8px;
}

.select {
  min-height: auto;
  width: auto;
  height: auto;
  padding: 9px 32px 9px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-family: inherit;
  color: var(--color-text);
  background-color: var(--color-bg-surface);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%2364748b' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  appearance: none;
  -webkit-appearance: none;
  cursor: pointer;
  transition:
    border-color var(--transition-fast),
    box-shadow var(--transition-fast);
}

.select:hover {
  border-color: var(--color-primary-soft);
}

.select:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-ring);
}

.select:disabled {
  opacity: 0.55;
  cursor: not-allowed;
  background-color: var(--color-bg-hover);
}
</style>
