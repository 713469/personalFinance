<template>
  <div class="page-stack">
    <section class="page-header">
      <PageHeader title="首页" :subtitle="pageSubtitle" />
      <button class="primary-action" type="button" v-ripple @click="ui.openQuickCreate('bill')">
        <Plus :size="18" />
        新增账单
      </button>
    </section>

    <section class="stats-grid">
      <StatCard label="本月收入" :value="formatCurrency(store.monthIncome)" :target="store.monthIncome" hint="来自工资、转账等收入" tone="#dcfce7">
        <template #icon><TrendingUp :size="20" /></template>
      </StatCard>
      <StatCard label="本月支出" :value="formatCurrency(store.monthExpense)" :target="store.monthExpense" hint="餐饮、交通、购物等支出" tone="#ffedd5">
        <template #icon><TrendingDown :size="20" /></template>
      </StatCard>
      <StatCard label="本月结余" :value="formatCurrency(store.monthBalance)" :target="store.monthBalance" hint="收入减去支出" tone="#dbeafe">
        <template #icon><PiggyBank :size="20" /></template>
      </StatCard>
      <StatCard label="账户总资产" :value="formatCurrency(store.totalAssets)" :target="store.totalAssets" hint="所有账户当前余额汇总" tone="#e0e7ff">
        <template #icon><WalletCards :size="20" /></template>
      </StatCard>
    </section>

    <section class="budget-strip" v-if="store.budgetOverview" @click="router.push('/budgets')">
      <div class="budget-strip-header">
        <h3>本月预算</h3>
        <ChevronRight :size="16" />
      </div>
      <div class="budget-strip-body">
        <template v-if="store.budgetOverview.totalBudget > 0">
          <div class="budget-strip-item">
            <span>预算金额</span>
            <strong>{{ formatCurrency(store.budgetOverview.totalBudget) }}</strong>
          </div>
          <div class="budget-strip-item">
            <span>已支出</span>
            <strong :class="{ 'text-danger': store.budgetOverview.overBudget }">{{ formatCurrency(store.budgetOverview.totalSpent) }}</strong>
          </div>
          <div class="budget-strip-item">
            <span>剩余</span>
            <strong :class="{ 'text-danger': store.budgetOverview.remainingAmount < 0 }">{{ formatCurrency(store.budgetOverview.remainingAmount) }}</strong>
          </div>
          <div class="budget-strip-item">
            <span>使用率</span>
            <strong :class="{ 'text-danger': store.budgetOverview.overBudget }">{{ store.budgetOverview.usageRate }}%</strong>
          </div>
          <div v-if="store.budgetOverview.overBudget" class="budget-strip-alert">
            已超支！
          </div>
        </template>
        <template v-else>
          <span class="budget-strip-hint">暂未设置预算，点击前往设置</span>
        </template>
      </div>
      <div v-if="store.budgetOverview.totalBudget > 0" class="budget-strip-progress">
        <div class="budget-strip-progress-bar">
          <div
            class="budget-strip-progress-fill"
            :class="{ 'budget-strip-progress-fill--over': store.budgetOverview.overBudget }"
            :style="{ width: Math.min(store.budgetOverview.usageRate, 100) + '%' }"
          />
        </div>
      </div>
    </section>

    <section class="content-grid">
      <div class="panel panel-span-2">
        <div class="panel-header">
          <h2>近 6 个月收支趋势</h2>
          <span class="panel-subtitle">按月汇总收入与支出</span>
        </div>

        <div ref="chartRef" class="trend-chart trend-chart--line" @pointermove="moveTooltip">
          <svg
            class="trend-svg"
            viewBox="0 0 720 280"
            role="img"
            aria-label="近 6 个月收支趋势图"
            preserveAspectRatio="none"
          >
            <defs>
              <linearGradient id="incomeFill" x1="0" x2="0" y1="0" y2="1">
                <stop offset="0%" stop-color="#16a34a" stop-opacity="0.28" />
                <stop offset="100%" stop-color="#16a34a" stop-opacity="0.02" />
              </linearGradient>
              <linearGradient id="expenseFill" x1="0" x2="0" y1="0" y2="1">
                <stop offset="0%" stop-color="#f97316" stop-opacity="0.24" />
                <stop offset="100%" stop-color="#f97316" stop-opacity="0.02" />
              </linearGradient>
            </defs>

            <g v-for="gridY in gridLines" :key="gridY" class="trend-grid-line">
              <line x1="0" :y1="gridY" x2="720" :y2="gridY" />
            </g>

            <path :d="incomeAreaPath" class="trend-area income" />
            <path :d="expenseAreaPath" class="trend-area expense" />
            <path :d="incomeLinePath" class="trend-line income" />
            <path :d="expenseLinePath" class="trend-line expense" />

            <g v-for="(point, idx) in trendPoints" :key="point.month" class="trend-point-group">
              <circle :cx="point.x" :cy="point.incomeY" r="5" class="trend-point income" />
              <circle :cx="point.x" :cy="point.expenseY" r="5" class="trend-point expense" />
              <rect
                v-if="trendZones[idx]"
                :x="trendZones[idx].left"
                y="0"
                :width="trendZones[idx].width"
                height="280"
                fill="transparent"
                class="trend-zone"
                @pointerenter="showTooltip($event, point)"
                @pointerleave="hideTooltip"
              />
            </g>
          </svg>

          <div v-if="tooltip.show" class="trend-tooltip" :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }">
            <div class="trend-tooltip-label">{{ tooltip.label }}</div>
            <div class="trend-tooltip-row income">
              <span class="trend-tooltip-dot" style="background:#16a34a" />
              <span>收入</span>
              <strong>{{ formatCurrency(tooltip.income) }}</strong>
            </div>
            <div class="trend-tooltip-row expense">
              <span class="trend-tooltip-dot" style="background:#f97316" />
              <span>支出</span>
              <strong>{{ formatCurrency(tooltip.expense) }}</strong>
            </div>
          </div>

          <div class="trend-axis">
            <div v-for="point in trendPoints" :key="point.month" class="trend-axis-item">
              <strong>{{ point.label }}</strong>
              <p>{{ formatCompact(point.income) }} / {{ formatCompact(point.expense) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h2>最近账单</h2>
          <RouterLink to="/bills" class="panel-link" v-ripple="'rgba(23, 107, 135, 0.12)'">查看全部</RouterLink>
        </div>
        <div class="record-list">
          <article v-for="bill in store.recentBills" :key="bill.id" class="record-row">
            <span class="record-dot" :style="{ background: bill.dotColor }" />
            <div>
              <strong>{{ bill.title }}</strong>
              <p>{{ bill.account }} · {{ bill.tradeTime }}</p>
            </div>
            <b :class="bill.type">{{ formatSignedCurrency(bill.amount, bill.type) }}</b>
          </article>
        </div>
      </div>
    </section>

    <section class="content-grid bottom-grid">
      <div class="panel">
        <div class="panel-header">
          <h2>支出分类占比</h2>
          <span class="panel-subtitle">当前月支出结构</span>
        </div>

        <div class="pie-layout">
          <div class="pie-chart">
            <div class="pie-ring">
              <svg
                v-if="pieSectors.length"
                class="pie-chart-svg"
                viewBox="0 0 220 220"
                aria-label="支出分类扇形图"
              >
                <!-- background ring -->
                <circle cx="110" cy="110" r="110" fill="#f1f5f9" />
                <!-- sectors -->
                <g
                  v-for="sector in pieSectors"
                  :key="sector.categoryId"
                  class="pie-sector-group"
                  @click="navigateToCategory(sector.categoryId)"
                >
                  <title>{{ sector.name }} {{ sector.percent }}%</title>
                  <path
                    :d="sector.d"
                    :fill="sector.color"
                    stroke="#ffffff"
                    stroke-width="1.2"
                    stroke-linejoin="round"
                    class="pie-sector-path"
                  />
                </g>
                <!-- center hole -->
                <circle cx="110" cy="110" r="86" fill="#ffffff" stroke="#e2e8f0" stroke-width="1" />
              </svg>
              <div class="pie-hole">
                <span class="pie-center-label">支出</span>
                <strong class="pie-center-value">{{ formatCurrency(store.monthExpense) }}</strong>
              </div>
            </div>
          </div>

          <div class="pie-legend">
            <article
              v-for="item in categoryExpenseWithTotals"
              :key="item.name"
              class="pie-legend-row pie-legend-row--clickable"
              @click="navigateToCategory(item.categoryId)"
            >
              <div class="pie-legend-copy">
                <span class="pie-swatch" :style="{ background: item.color }" />
                <div>
                  <strong>{{ item.name }}</strong>
                  <p>{{ formatCurrency(item.amount) }}</p>
                </div>
              </div>
              <b>{{ item.percent }}%</b>
            </article>
            <p v-if="!categoryExpenseWithTotals.length" class="empty-hint">暂无支出数据</p>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h2>账户概览</h2>
          <RouterLink to="/accounts" class="panel-link" v-ripple="'rgba(23, 107, 135, 0.12)'">管理账户</RouterLink>
        </div>
        <div class="account-overview">
          <article v-for="account in store.accounts" :key="account.id" class="account-mini">
            <span>{{ account.name }}</span>
            <strong>{{ formatCurrency(account.currentBalance) }}</strong>
            <p>{{ account.type }} · {{ account.remark }}</p>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChevronRight, PiggyBank, Plus, TrendingDown, TrendingUp, WalletCards } from 'lucide-vue-next'
import PageHeader from '@/shared/components/PageHeader.vue'
import StatCard from '@/shared/components/StatCard.vue'
import { useFinanceStore } from '@/shared/stores/finance'
import { useUiStore } from '@/shared/stores/ui'
import { formatCurrency, formatSignedCurrency } from '@/shared/utils/format'

const store = useFinanceStore()
const ui = useUiStore()
const router = useRouter()

onMounted(() => {
  const month = new Intl.DateTimeFormat('en-CA', {
    year: 'numeric',
    month: '2-digit',
  }).format(new Date())
  void store.loadBudgetOverview(month)
})

function formatCompact(value: number) {
  return value.toLocaleString('zh-CN', { maximumFractionDigits: 0 })
}

const tooltip = reactive({
  show: false,
  x: 0,
  y: 0,
  label: '',
  income: 0,
  expense: 0,
})
const chartRef = ref<HTMLElement | null>(null)

function showTooltip(event: PointerEvent, point: { label: string; income: number; expense: number }) {
  tooltip.show = true
  tooltip.label = point.label
  tooltip.income = point.income
  tooltip.expense = point.expense
  updateTooltipPosition(event)
}

function updateTooltipPosition(event: PointerEvent) {
  if (!chartRef.value) return
  const rect = chartRef.value.getBoundingClientRect()
  tooltip.x = event.clientX - rect.left + 12
  tooltip.y = event.clientY - rect.top - 12
}

function moveTooltip(event: PointerEvent) {
  if (!tooltip.show) return
  updateTooltipPosition(event)
}

function hideTooltip() {
  tooltip.show = false
}

const trendZones = computed(() => {
  const points = trendPoints.value
  if (!points.length) return []
  return points.map((point, index) => {
    const left = index === 0 ? 0 : (points[index - 1].x + point.x) / 2
    const right = index === points.length - 1 ? 720 : (point.x + points[index + 1].x) / 2
    return { left: Math.round(left * 100) / 100, width: Math.max(0, Math.round((right - left) * 100) / 100) }
  })
})

function navigateToCategory(categoryId: number) {
  const month = store.summary?.month ?? new Intl.DateTimeFormat('en-CA', {
    year: 'numeric',
    month: '2-digit',
  }).format(new Date())
  router.push({ name: 'bills', query: { month, categoryId: String(categoryId), type: 'EXPENSE' } })
}

const pageSubtitle = computed(() => {
  if (!store.summary) {
    return '本月财务总览与关键趋势'
  }

  return `${store.summary.month} 财务总览与关键趋势`
})

const trendPoints = computed(() => {
  const items = store.monthlyTrend
  const width = 720
  const height = 280
  const paddingX = 56
  const paddingY = 24
  const maxValue = Math.max(1, ...items.flatMap((item) => [item.income, item.expense]))
  const innerWidth = width - paddingX * 2
  const innerHeight = height - paddingY * 2
  const denominator = Math.max(1, items.length - 1)

  return items.map((item, index) => {
    const x = paddingX + (innerWidth * index) / denominator
    const incomeY = height - paddingY - (item.income / maxValue) * innerHeight
    const expenseY = height - paddingY - (item.expense / maxValue) * innerHeight
    return {
      ...item,
      label: item.monthLabel,
      x,
      incomeY,
      expenseY,
    }
  })
})

const gridLines = computed(() => {
  const height = 280
  const paddingY = 24
  const innerHeight = height - paddingY * 2
  return [0, 0.25, 0.5, 0.75, 1].map((ratio) => paddingY + innerHeight * ratio)
})

function buildLinePath(points: Array<{ x: number; incomeY: number; expenseY: number }>, key: 'incomeY' | 'expenseY') {
  if (!points.length) {
    return ''
  }

  return points
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x.toFixed(1)} ${point[key].toFixed(1)}`)
    .join(' ')
}

function buildAreaPath(points: Array<{ x: number; incomeY: number; expenseY: number }>, key: 'incomeY' | 'expenseY') {
  if (!points.length) {
    return ''
  }

  const startX = points[0].x
  const endX = points[points.length - 1].x
  const baseline = 256
  const line = buildLinePath(points, key)
  return `${line} L ${endX.toFixed(1)} ${baseline} L ${startX.toFixed(1)} ${baseline} Z`
}

const incomeLinePath = computed(() => buildLinePath(trendPoints.value, 'incomeY'))
const expenseLinePath = computed(() => buildLinePath(trendPoints.value, 'expenseY'))
const incomeAreaPath = computed(() => buildAreaPath(trendPoints.value, 'incomeY'))
const expenseAreaPath = computed(() => buildAreaPath(trendPoints.value, 'expenseY'))

const categoryExpenseWithTotals = computed(() => store.categoryExpense)

const pieSectors = computed(() => {
  const items = categoryExpenseWithTotals.value
  const total = items.reduce((sum, item) => sum + item.amount, 0)
  if (!total || !items.length) return []

  const cx = 110
  const cy = 110
  const outerR = 110
  const innerR = 86

  let cursor = 0
  return items.map((item) => {
    const size = (item.amount / total) * 100
    const startPct = cursor
    const endPct = cursor + size
    cursor = endPct

    const startAngle = -Math.PI / 2 + (startPct / 100) * 2 * Math.PI
    const endAngle = -Math.PI / 2 + (endPct / 100) * 2 * Math.PI

    const large = endPct - startPct > 50 ? 1 : 0

    const ox1 = cx + outerR * Math.cos(startAngle)
    const oy1 = cy + outerR * Math.sin(startAngle)
    const ox2 = cx + outerR * Math.cos(endAngle)
    const oy2 = cy + outerR * Math.sin(endAngle)

    const ix1 = cx + innerR * Math.cos(startAngle)
    const iy1 = cy + innerR * Math.sin(startAngle)
    const ix2 = cx + innerR * Math.cos(endAngle)
    const iy2 = cy + innerR * Math.sin(endAngle)

    const d = [
      `M ${ox1.toFixed(2)} ${oy1.toFixed(2)}`,
      `A ${outerR} ${outerR} 0 ${large} 1 ${ox2.toFixed(2)} ${oy2.toFixed(2)}`,
      `L ${ix2.toFixed(2)} ${iy2.toFixed(2)}`,
      `A ${innerR} ${innerR} 0 ${large} 0 ${ix1.toFixed(2)} ${iy1.toFixed(2)}`,
      'Z',
    ].join(' ')

    return {
      categoryId: item.categoryId,
      name: item.name,
      percent: item.percent,
      color: item.color,
      d,
    }
  })
})
</script>
