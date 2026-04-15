import { describe, it, expect } from 'vitest'
import { formatDate, formatDateTime, debounce, throttle, isEmpty } from '../index.js'

describe('Utils - formatDate', () => {
  it('formats date with default format (YYYY-MM-DD)', () => {
    const date = new Date('2024-01-15 10:30:00')
    expect(formatDate(date)).toBe('2024-01-15')
  })

  it('formats date with custom format', () => {
    const date = new Date('2024-01-15 10:30:00')
    expect(formatDate(date, 'YYYY-MM-DD')).toBe('2024-01-15')
    expect(formatDate(date, 'YYYY/MM/DD')).toBe('2024/01/15')
  })

  it('formats date time correctly', () => {
    const date = new Date('2024-01-15 10:30:00')
    expect(formatDateTime(date)).toBe('2024-01-15 10:30:00')
  })

  it('handles string date input', () => {
    expect(formatDate('2024-01-15')).toBe('2024-01-15')
  })

  it('handles timestamp input', () => {
    const timestamp = new Date('2024-01-15 10:30:00').getTime()
    expect(formatDate(timestamp)).toBe('2024-01-15')
  })

  it('returns empty string for invalid date', () => {
    expect(formatDate(null)).toBe('')
    expect(formatDate(undefined)).toBe('')
    expect(formatDate('invalid')).toBe('')
  })
})

describe('Utils - debounce', () => {
  it('delays function execution', async () => {
    let count = 0
    const debouncedFn = debounce(() => {
      count++
    }, 100)

    debouncedFn()
    debouncedFn()
    debouncedFn()

    expect(count).toBe(0)

    await new Promise(resolve => setTimeout(resolve, 150))
    expect(count).toBe(1)
  })

  it('executes with correct arguments', async () => {
    let result = null
    const debouncedFn = debounce((arg) => {
      result = arg
    }, 50)

    debouncedFn('test')
    await new Promise(resolve => setTimeout(resolve, 100))
    expect(result).toBe('test')
  })
})

describe('Utils - throttle', () => {
  it('limits function execution rate', async () => {
    let count = 0
    const throttledFn = throttle(() => {
      count++
    }, 100)

    throttledFn()
    throttledFn()
    throttledFn()

    expect(count).toBe(1)

    await new Promise(resolve => setTimeout(resolve, 150))
    throttledFn()
    expect(count).toBe(2)
  })
})

describe('Utils - isEmpty', () => {
  it('returns true for empty values', () => {
    expect(isEmpty(null)).toBe(true)
    expect(isEmpty(undefined)).toBe(true)
    expect(isEmpty('')).toBe(true)
    expect(isEmpty([])).toBe(true)
    expect(isEmpty({})).toBe(true)
  })

  it('returns false for non-empty values', () => {
    expect(isEmpty('test')).toBe(false)
    expect(isEmpty([1, 2, 3])).toBe(false)
    expect(isEmpty({ key: 'value' })).toBe(false)
    expect(isEmpty(0)).toBe(false)
    expect(isEmpty(false)).toBe(false)
  })
})
