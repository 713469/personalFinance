<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">¥</div>
        <div class="brand-copy">
          <strong>Finance Tracker</strong>
          <span>个人财务记账系统</span>
        </div>
      </div>

      <nav class="nav-list">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-item"
          v-ripple="'rgba(23, 107, 135, 0.16)'"
        >
          <component :is="item.icon" :size="18" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

    </aside>

    <main class="main-panel">
      <RouterView v-slot="{ Component, route }">
        <div :key="route.fullPath" class="route-page">
          <component :is="Component" />
        </div>
      </RouterView>
    </main>
  </div>
  <QuickCreateModal />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { mainNavigation } from '@/shared/navigation'
import QuickCreateModal from '@/shared/components/QuickCreateModal.vue'
import { useFinanceStore } from '@/shared/stores/finance'

const store = useFinanceStore()
const navItems = mainNavigation

onMounted(() => {
  void store.initialize()
})
</script>
