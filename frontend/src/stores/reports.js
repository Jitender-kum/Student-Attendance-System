import { defineStore } from 'pinia'
import { ref } from 'vue'
import { reportService } from '../services/academic'

export const useReportStore = defineStore('reports', () => {
  const rows = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchWeekly(filters) {
    return fetchRows(() => reportService.weekly(filters))
  }

  async function fetchMonthly(filters) {
    return fetchRows(() => reportService.monthly(filters))
  }

  async function fetchSemester(filters) {
    return fetchRows(() => reportService.semester(filters))
  }

  async function fetchRows(loader) {
    loading.value = true
    error.value = ''
    try {
      const data = await loader()
      rows.value = Array.isArray(data) ? data : []
      return rows.value
    } catch (e) {
      error.value = e.message
      rows.value = []
      return []
    } finally {
      loading.value = false
    }
  }

  return {
    rows,
    loading,
    error,
    fetchWeekly,
    fetchMonthly,
    fetchSemester,
  }
})
