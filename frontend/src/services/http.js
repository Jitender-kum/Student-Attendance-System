import { useAuthStore } from '../stores/auth'

async function parseResponse(response) {
  const contentType = response.headers.get('content-type') || ''
  const isJson = contentType.includes('application/json')
  const data = isJson ? await response.json() : await response.text()

  if (!response.ok) {
    const error = new Error(
      typeof data === 'object' && data?.message
        ? data.message
        : `Request failed with status ${response.status}`
    )
    error.status = response.status
    error.payload = data
    throw error
  }

  return data
}

export async function apiRequest(url, options = {}) {
  const authStore = useAuthStore()
  const responseType = options.responseType || 'auto'
  const headers = {
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers || {}),
  }

  if (authStore.token) {
    headers.Authorization = `Bearer ${authStore.token}`
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    })
    if (responseType === 'blob') {
      if (!response.ok) {
        return await parseResponse(response)
      }
      return await response.blob()
    }
    return await parseResponse(response)
  } catch (error) {
    if (error.status === 401) {
      authStore.clearSession()
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    throw error
  }
}
