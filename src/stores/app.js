import { defineStore } from 'pinia'

// Example Pinia store – replace with your own state logic
export const useAppStore = defineStore('app', {
  state: () => ({
    count: 0,
  }),
  getters: {
    doubleCount: (state) => state.count * 2,
  },
  actions: {
    increment() {
      this.count++
    },
  },
})
