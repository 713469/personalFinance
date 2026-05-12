import { createRouter, createWebHistory } from 'vue-router'
import AccountPage from '@/pages/AccountPage.vue'
import BillPage from '@/pages/BillPage.vue'
import BudgetPage from '@/pages/BudgetPage.vue'
import CategoryPage from '@/pages/CategoryPage.vue'
import DashboardPage from '@/pages/DashboardPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'dashboard', component: DashboardPage },
    { path: '/bills', name: 'bills', component: BillPage },
    { path: '/budgets', name: 'budgets', component: BudgetPage },
    { path: '/categories', name: 'categories', component: CategoryPage },
    { path: '/accounts', name: 'accounts', component: AccountPage },
  ],
})

export default router

