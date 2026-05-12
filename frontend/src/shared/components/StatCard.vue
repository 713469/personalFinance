<template>
  <section class="stat-card">
    <div class="stat-icon" :style="{ background: tone }">
      <slot name="icon" />
    </div>
    <div class="stat-copy">
      <p>{{ label }}</p>
      <strong class="stat-value">{{ displayValue }}</strong>
      <span>{{ hint }}</span>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps<{
  label: string
  value: string
  hint: string
  tone: string
  target?: number
}>()

const current = ref<number>(props.target ?? 0)
let animFrame: number | null = null

function formatCurrency(value: number) {
  return value.toLocaleString('zh-CN', {
    style: 'currency',
    currency: 'CNY',
    minimumFractionDigits: 2,
  })
}

const displayValue = computed(() => {
  if (props.target === undefined) {
    return props.value
  }
  return formatCurrency(current.value)
})

watch(
  () => props.target,
  (next) => {
    if (next === undefined) return

    const end = next

    if (animFrame !== null) {
      cancelAnimationFrame(animFrame)
    }

    const start = current.value
    const diff = end - start
    const duration = 600
    let startTime: number | null = null

    function easeOutExpo(t: number): number {
      return t === 1 ? 1 : 1 - Math.pow(2, -10 * t)
    }

    function tick(now: number) {
      if (startTime === null) {
        startTime = now
      }

      const elapsed = now - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = easeOutExpo(progress)
      current.value = start + diff * eased

      if (progress < 1) {
        animFrame = requestAnimationFrame(tick)
      } else {
        current.value = end
      }
    }

    animFrame = requestAnimationFrame(tick)
  },
  { immediate: true },
)
</script>
