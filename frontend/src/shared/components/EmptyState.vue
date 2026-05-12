<template>
  <div class="empty-state-shell">
    <div class="empty-illustration" :class="`empty-illustration--${variant}`">
      <component :is="icon" :size="32" :stroke-width="1.5" />
    </div>
    <strong class="empty-title">{{ title }}</strong>
    <p v-if="description" class="empty-description">{{ description }}</p>
    <div v-if="$slots.default" class="empty-action">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { computed } from 'vue'
import { FileText, Inbox, Tag, WalletCards } from 'lucide-vue-next'

const props = withDefaults(
  defineProps<{
    title: string
    description?: string
    variant?: 'bill' | 'category' | 'account' | 'generic'
  }>(),
  {
    variant: 'generic',
    description: '',
  },
)

const iconMap: Record<string, Component> = {
  bill: FileText,
  category: Tag,
  account: WalletCards,
  generic: Inbox,
}

const icon = computed(() => iconMap[props.variant] ?? iconMap.generic)
</script>
