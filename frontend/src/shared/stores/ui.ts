import { ref } from 'vue'
import { defineStore } from 'pinia'

export type QuickCreateMode = 'bill' | 'category' | 'account' | null

export const useUiStore = defineStore('ui', () => {
  const quickCreateMode = ref<QuickCreateMode>(null)

  function openQuickCreate(mode: Exclude<QuickCreateMode, null>) {
    quickCreateMode.value = mode
  }

  function closeQuickCreate() {
    quickCreateMode.value = null
  }

  return {
    quickCreateMode,
    openQuickCreate,
    closeQuickCreate,
  }
})
