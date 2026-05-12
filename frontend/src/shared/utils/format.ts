export function toNumber(value: number | string | null | undefined, fallback = 0) {
  const result = typeof value === 'number' ? value : Number(value)
  return Number.isFinite(result) ? result : fallback
}

export function formatCurrency(value: number | string | null | undefined) {
  const amount = toNumber(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })
  return `¥${amount}`
}

export function formatSignedCurrency(
  value: number | string | null | undefined,
  type: 'INCOME' | 'EXPENSE',
) {
  return `${type === 'INCOME' ? '+' : '-'}${formatCurrency(value)}`
}

export function formatDateTime(value: string | null | undefined) {
  if (!value) {
    return '--'
  }

  return new Intl.DateTimeFormat('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(new Date(value))
}

export function formatMonthLabel(month: string) {
  const [, monthValue] = month.split('-')
  return `${Number(monthValue)}月`
}
