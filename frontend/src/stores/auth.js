import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const AUTH_STORAGE_KEY = 'attendease_auth'

function loadAuth() {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const session = ref(loadAuth())

  const token = computed(() => session.value?.token || '')
  const teacher = computed(() => session.value?.teacher || null)
  const isAuthenticated = computed(() => !!token.value)

  function setSession(payload) {
    session.value = payload
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(payload))
  }

  function clearSession() {
    session.value = null
    localStorage.removeItem(AUTH_STORAGE_KEY)
  }

  function login(authResponse) {
    setSession(authResponse)
  }

  function logout() {
    clearSession()
  }

  return {
    session,
    token,
    teacher,
    isAuthenticated,
    login,
    logout,
    setSession,
    clearSession,
  }
})
