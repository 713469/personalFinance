const accentPalette = [
  '#0ea5e9',
  '#16a34a',
  '#f97316',
  '#8b5cf6',
  '#06b6d4',
  '#ef4444',
  '#14b8a6',
  '#6366f1',
]

export function pickAccentColor(seed: string) {
  let hash = 0
  for (let index = 0; index < seed.length; index += 1) {
    hash = (hash << 5) - hash + seed.charCodeAt(index)
    hash |= 0
  }
  const normalized = Math.abs(hash) % accentPalette.length
  return accentPalette[normalized]
}
