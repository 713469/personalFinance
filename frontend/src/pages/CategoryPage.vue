<template>
  <div class="page-stack">
    <section class="page-header">
      <PageHeader title="分类管理" subtitle="收入与支出分类统一维护，支持启用、停用与删除保护。" />
      <button class="primary-action" type="button" v-ripple @click="openCreate">
        <Plus :size="18" />
        新增分类
      </button>
    </section>

    <section v-if="notice" class="page-banner" :class="`page-banner--${noticeTone}`">
      {{ notice }}
    </section>

    <section class="panel">
      <div class="segment-bar">
        <button
          v-for="item in typeTabs"
          :key="item.value"
          type="button"
          class="segment-button"
          :class="{ active: viewType === item.value }"
          @click="viewType = item.value"
        >
          {{ item.label }}
        </button>
      </div>

      <div class="category-card-list category-card-list--table">
        <article class="table-header category-table-header">
          <span>名称</span>
          <span>类型</span>
          <span>状态</span>
          <span>排序</span>
          <span>操作</span>
        </article>

        <EmptyState v-if="loading" title="正在加载分类..." variant="category" />
        <template v-else-if="visibleCategories.length">
          <article v-for="item in visibleCategories" :key="item.id" class="table-row category-row">
            <div class="category-name">
              <strong>{{ item.name }}</strong>
              <p>{{ item.icon || 'circle' }}</p>
            </div>
            <span>{{ item.type }}</span>
            <span>
              <b :class="['status-chip', item.status === 1 ? 'status-chip--success' : 'status-chip--muted']">
                {{ item.status === 1 ? '启用' : '停用' }}
              </b>
            </span>
            <span>{{ item.sort }}</span>
            <div class="inline-actions">
              <button class="ghost-button ghost-button--small" type="button" @click="openEdit(item)">编辑</button>
              <button class="ghost-button ghost-button--small" type="button" @click="toggleStatus(item)">
                {{ item.status === 1 ? '停用' : '启用' }}
              </button>
              <button class="danger-button danger-button--small" type="button" @click="askDelete(item)">删除</button>
            </div>
          </article>
        </template>
        <EmptyState v-else variant="category" title="暂无分类数据" description="可以先新增一个分类，然后在账单里选择它。" />
      </div>
    </section>

    <CategoryFormDialog
      :open="formOpen"
      :saving="saving"
      :mode="formMode"
      :category="editingCategory"
      @close="formOpen = false"
      @save="submitCategory"
    />

    <ConfirmDialog
      :open="deleteOpen"
      title="删除分类"
      description="如果该分类已经被账单使用，将无法删除，只能停用。"
      confirm-text="确认删除"
      :loading="deleting"
      @cancel="deleteOpen = false"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import ConfirmDialog from '@/shared/components/ConfirmDialog.vue'
import CategoryFormDialog from '@/shared/components/CategoryFormDialog.vue'
import EmptyState from '@/shared/components/EmptyState.vue'
import PageHeader from '@/shared/components/PageHeader.vue'
import { useFinanceStore } from '@/shared/stores/finance'
import { changeCategoryStatus, createCategory, deleteCategory, updateCategory } from '@/shared/api/finance'
import type { BackendCategory, CategoryCreateInput } from '@/shared/types/backend'

const store = useFinanceStore()

const viewType = ref<'ALL' | 'INCOME' | 'EXPENSE'>('ALL')
const notice = ref('')
const noticeTone = ref<'success' | 'error' | 'info'>('info')
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const formOpen = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const editingCategory = ref<BackendCategory | null>(null)
const deleteOpen = ref(false)
const deleteTarget = ref<BackendCategory | null>(null)

const typeTabs = [
  { label: '全部', value: 'ALL' },
  { label: '支出分类', value: 'EXPENSE' },
  { label: '收入分类', value: 'INCOME' },
] as const

const visibleCategories = computed(() =>
  store.categoriesRaw.filter((item) => viewType.value === 'ALL' || item.type === viewType.value),
)

onMounted(async () => {
  loading.value = true
  try {
    await store.initialize()
  } finally {
    loading.value = false
  }
})

function showNotice(message: string, tone: 'success' | 'error' | 'info' = 'info') {
  notice.value = message
  noticeTone.value = tone
  window.setTimeout(() => {
    if (notice.value === message) {
      notice.value = ''
    }
  }, 2600)
}

function openCreate() {
  formMode.value = 'create'
  editingCategory.value = null
  formOpen.value = true
}

function openEdit(category: BackendCategory) {
  formMode.value = 'edit'
  editingCategory.value = category
  formOpen.value = true
}

function askDelete(category: BackendCategory) {
  deleteTarget.value = category
  deleteOpen.value = true
}

async function submitCategory(payload: CategoryCreateInput) {
  saving.value = true
  try {
    if (formMode.value === 'edit' && editingCategory.value) {
      await updateCategory(editingCategory.value.id, payload)
      showNotice('分类已更新', 'success')
    } else {
      await createCategory(payload)
      showNotice('分类已新增', 'success')
    }
    formOpen.value = false
    await store.refresh()
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '保存分类失败', 'error')
  } finally {
    saving.value = false
  }
}

async function toggleStatus(category: BackendCategory) {
  try {
    await changeCategoryStatus(category.id, category.status === 1 ? 0 : 1)
    showNotice(category.status === 1 ? '分类已停用' : '分类已启用', 'success')
    await store.refresh()
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '状态变更失败', 'error')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) {
    return
  }
  deleting.value = true
  try {
    await deleteCategory(deleteTarget.value.id)
    showNotice('分类已删除', 'success')
    deleteOpen.value = false
    deleteTarget.value = null
    await store.refresh()
  } catch (error) {
    showNotice(error instanceof Error ? error.message : '删除分类失败', 'error')
  } finally {
    deleting.value = false
  }
}
</script>
