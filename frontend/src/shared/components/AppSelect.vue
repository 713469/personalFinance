<template>
  <div
    ref="root"
    class="app-select"
    :class="[
      size === 'sm' ? 'app-select--sm' : 'app-select--md',
      { 'is-open': open, 'is-disabled': disabled },
    ]"
  >
    <button
      type="button"
      class="app-select-trigger"
      :disabled="disabled"
      @click="toggle"
      @keydown.down.prevent="openMenu"
      @keydown.up.prevent="openMenu"
      @keydown.enter.prevent="toggle"
      @keydown.space.prevent="toggle"
      @keydown.esc.stop.prevent="close"
    >
      <span class="app-select-label" :class="{ placeholder: !selectedOption }">
        {{ selectedLabel }}
      </span>
      <ChevronDown :size="16" class="app-select-icon" />
    </button>

    <transition name="select-pop">
      <div v-if="open" class="app-select-menu" :class="menuPositionClass" role="listbox">
        <button
          v-for="option in options"
          :key="optionKey(option)"
          type="button"
          class="app-select-option"
          :class="{ selected: isSelected(option), disabled: option.disabled }"
          :disabled="option.disabled"
          role="option"
          :aria-selected="isSelected(option)"
          @click="select(option)"
        >
          <span class="app-select-option-copy">
            <strong>{{ option.label }}</strong>
            <span v-if="option.description">{{ option.description }}</span>
          </span>
          <Check v-if="isSelected(option)" :size="16" class="app-select-check" />
        </button>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { Check, ChevronDown } from 'lucide-vue-next'

export type SelectValue = string | number

export interface SelectOption {
  label: string
  value: SelectValue
  description?: string
  disabled?: boolean
}

const props = withDefaults(
  defineProps<{
    modelValue: SelectValue | null
    options: SelectOption[]
    placeholder?: string
    size?: 'sm' | 'md'
    disabled?: boolean
  }>(),
  {
    placeholder: '请选择',
    size: 'md',
    disabled: false,
  },
)

const emit = defineEmits<{
  (event: 'update:modelValue', value: SelectValue): void
  (event: 'change', value: SelectValue): void
}>()

const root = ref<HTMLElement | null>(null)
const open = ref(false)
const menuPosition = ref<'bottom' | 'top'>('bottom')

const selectedOption = computed(
  () => props.options.find((option) => option.value === props.modelValue) ?? null,
)

const selectedLabel = computed(() => selectedOption.value?.label ?? props.placeholder)

const menuPositionClass = computed(() =>
  menuPosition.value === 'top' ? 'app-select-menu--top' : 'app-select-menu--bottom',
)

function optionKey(option: SelectOption) {
  return String(option.value)
}

function isSelected(option: SelectOption) {
  return option.value === props.modelValue
}

function close() {
  open.value = false
}

function computeMenuPosition() {
  const trigger = root.value
  if (!trigger) {
    menuPosition.value = 'bottom'
    return
  }

  const rect = trigger.getBoundingClientRect()
  const spaceBelow = window.innerHeight - rect.bottom
  const spaceAbove = rect.top
  const preferredMenuHeight = Math.min(Math.max(props.options.length * 48 + 16, 180), 320)

  menuPosition.value =
    spaceBelow < preferredMenuHeight && spaceAbove > spaceBelow ? 'top' : 'bottom'
}

async function openMenu() {
  if (props.disabled) {
    return
  }

  open.value = true
  await nextTick()
  computeMenuPosition()
}

function toggle() {
  if (props.disabled) {
    return
  }

  if (open.value) {
    close()
    return
  }

  void openMenu()
}

function select(option: SelectOption) {
  if (option.disabled) {
    return
  }

  emit('update:modelValue', option.value)
  emit('change', option.value)
  close()
}

function onDocumentMouseDown(event: MouseEvent) {
  if (!open.value || !root.value) {
    return
  }

  const target = event.target as Node | null
  if (target && !root.value.contains(target)) {
    close()
  }
}

onMounted(() => {
  document.addEventListener('mousedown', onDocumentMouseDown, true)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', onDocumentMouseDown, true)
})
</script>
