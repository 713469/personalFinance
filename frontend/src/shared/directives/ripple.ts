import type { Directive } from 'vue'

const RIPPLE_CLASS = 'ui-ripple'

function createRipple(el: HTMLElement, event: MouseEvent, color: string) {
  const rect = el.getBoundingClientRect()
  const size = Math.max(rect.width, rect.height)
  const ripple = document.createElement('span')

  ripple.className = RIPPLE_CLASS
  ripple.style.width = ripple.style.height = `${size}px`
  ripple.style.left = `${event.clientX - rect.left - size / 2}px`
  ripple.style.top = `${event.clientY - rect.top - size / 2}px`
  ripple.style.background = color

  el.appendChild(ripple)
  ripple.addEventListener('animationend', () => ripple.remove(), { once: true })
}

export const rippleDirective: Directive<HTMLElement, string | undefined> = {
  mounted(el, binding) {
    const host = el as HTMLElement & { __rippleHandler?: (event: MouseEvent) => void }

    el.classList.add('ripple-host')
    const handler = (event: MouseEvent) => {
      if (event.button !== 0) {
        return
      }
      if (el.matches(':disabled, [aria-disabled="true"]')) {
        return
      }
      createRipple(el, event, binding.value || 'rgba(255, 255, 255, 0.4)')
    }

    host.__rippleHandler = handler
    el.addEventListener('click', handler)
  },
  unmounted(el) {
    const host = el as HTMLElement & { __rippleHandler?: (event: MouseEvent) => void }
    if (host.__rippleHandler) {
      el.removeEventListener('click', host.__rippleHandler)
      delete host.__rippleHandler
    }
  },
}
