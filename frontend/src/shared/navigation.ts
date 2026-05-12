import type { Component } from 'vue'
import { LayoutDashboard, PiggyBank, ReceiptText, Tags, WalletCards } from 'lucide-vue-next'

export interface NavigationItem {
  to: string
  label: string
  icon: Component
}

export const mainNavigation: NavigationItem[] = [
  { to: '/', label: '首页', icon: LayoutDashboard },
  { to: '/bills', label: '账单管理', icon: ReceiptText },
  { to: '/budgets', label: '预算管理', icon: PiggyBank },
  { to: '/categories', label: '分类管理', icon: Tags },
  { to: '/accounts', label: '账户管理', icon: WalletCards },
]
