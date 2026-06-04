import { defineStore } from 'pinia'
import { ref } from 'vue'
import { subjectService } from '../services/academic'

function compareSubjects(a, b) {
  return String(a.name || '').localeCompare(String(b.name || ''))
}

export const useSubjectStore = defineStore('subjects', () => {
  const subjects = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchSubjects(filters = {}) {
    loading.value = true
    error.value = ''
    try {
      const data = await subjectService.list(filters)
      subjects.value = Array.isArray(data) ? data.sort(compareSubjects) : []
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function addSubject(payload) {
    const created = await subjectService.create(payload)
    subjects.value = [...subjects.value, created].sort(compareSubjects)
    return created
  }

  async function updateSubject(id, payload) {
    const updated = await subjectService.update(id, payload)
    const index = subjects.value.findIndex((subject) => subject.id === id)
    if (index !== -1) subjects.value[index] = updated
    subjects.value = [...subjects.value].sort(compareSubjects)
    return updated
  }

  async function deleteSubject(id) {
    await subjectService.delete(id)
    subjects.value = subjects.value.filter((subject) => subject.id !== id)
  }

  return {
    subjects,
    loading,
    error,
    fetchSubjects,
    addSubject,
    updateSubject,
    deleteSubject,
  }
})
