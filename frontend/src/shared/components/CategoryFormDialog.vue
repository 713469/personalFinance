<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @click.self="emit('close')">
      <section class="modal-card modal-card--md" role="dialog" aria-modal="true">
        <header class="modal-header">
          <div>
            <p class="eyebrow">分类管理</p>
            <h2>{{ mode === 'edit' ? '编辑分类' : '新增分类' }}</h2>
            <p class="modal-description">分类用于账单归类和首页统计。</p>
          </div>
          <button class="icon-button" type="button" @click="emit('close')" aria-label="关闭">
            <X :size="18" />
          </button>
        </header>

        <form class="modal-body" @submit.prevent="submit">
          <div class="modal-grid">
            <label class="modal-field">
              <span>名称</span>
              <input v-model="form.name" type="text" placeholder="例如 餐饮" />
            </label>
            <label class="modal-field">
              <span>类型</span>
              <AppSelect v-model="form.type" :options="typeOptions" />
            </label>
            <label class="modal-field">
              <span>图标</span>
              <input v-model="form.icon" type="text" placeholder="utensils" />
            </label>
            <label class="modal-field">
              <span>排序</span>
              <input v-model="form.sort" type="number" min="0" step="1" />
            </label>
            <label class="modal-field">
              <span>状态</span>
              <AppSelect v-model="form.status" :options="statusOptions" />
            </label>
          </div>

          <p v-if="error" class="modal-error">{{ error }}</p>

          <footer class="modal-actions">
            <button class="ghost-button" type="button" @click="emit('close')">取消</button>
            <button class="primary-action" type="submit" :disabled="saving">
              {{ saving ? '保存中...' : (mode === 'edit' ? '保存修改' : '保存分类') }}
            </button>
          </footer>
        </form>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { X } from 'lucide-vue-next'
import AppSelect from './AppSelect.vue'
import type { BackendCategory, CategoryCreateInput } from '@/shared/types/backend'

const props = defineProps<{
  open: boolean
  saving?: boolean
  mode: 'create' | 'edit'
  category?: BackendCategory | null
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'save', payload: CategoryCreateInput): void
}>()

const error = ref('')
const form = reactive({
  name: '',
  type: 'EXPENSE' as 'INCOME' | 'EXPENSE',
  icon: 'circle',
  sort: 0,
  status: 1,
})

const typeOptions = [
  { label: '支出', value: 'EXPENSE' },
  { label: '收入', value: 'INCOME' },
]

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 },
]

watch(
  () => [props.open, props.category],
  () => {
    error.value = ''
    if (!props.open) {
      return
    }
    form.name = props.category?.name ?? ''
    form.type = props.category?.type ?? 'EXPENSE'
    form.icon = props.category?.icon ?? 'circle'
    form.sort = props.category?.sort ?? 0
    form.status = props.category?.status ?? 1
  },
  { immediate: true },
)

function submit() {
  error.value = ''
  if (!form.name.trim()) {
    error.value = '请输入分类名称'
    return
  }
  emit('save', {
    name: form.name.trim(),
    type: form.type,
    icon: form.icon.trim() || undefined,
    sort: Number(form.sort),
    status: Number(form.status),
  })
}
</script>
