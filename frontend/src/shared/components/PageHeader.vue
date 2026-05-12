<template>
  <div class="header-copy">
    <p class="eyebrow">Personal Finance Tracker</p>
    <h1>{{ title }}</h1>
    <p class="header-description">{{ subtitle }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle: string
}>()
</script>

