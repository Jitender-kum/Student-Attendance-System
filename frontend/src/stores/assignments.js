import { defineStore } from 'pinia'
import { ref } from 'vue'
import { assignmentService } from '../services/academic'

function normalizeAssignment(raw = {}) {
  return {
    ...raw,
    title: raw.title || '',
    description: raw.description || '',
    departmentName: raw.departmentName || '',
    courseName: raw.courseName || '',
    dueDate: raw.dueDate || '',
  }
}

export const useAssignmentStore = defineStore('assignments', () => {
  const assignments = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchAssignments(filters = {}) {
    loading.value = true
    error.value = ''
    try {
      const data = await assignmentService.list(filters)
      assignments.value = Array.isArray(data) ? data.map(normalizeAssignment) : []
      return assignments.value
    } catch (e) {
      error.value = e.message
      return []
    } finally {
      loading.value = false
    }
  }

  async function addAssignment(payload) {
    const created = normalizeAssignment(await assignmentService.create(payload))
    assignments.value.unshift(created)
    return created
  }

  async function updateAssignment(id, payload) {
    const updated = normalizeAssignment(await assignmentService.update(id, payload))
    const index = assignments.value.findIndex((assignment) => assignment.id === id)
    if (index !== -1) {
      assignments.value[index] = updated
    }
    return updated
  }

  async function deleteAssignment(id) {
    await assignmentService.delete(id)
    assignments.value = assignments.value.filter((assignment) => assignment.id !== id)
  }

  return {
    assignments,
    loading,
    error,
    fetchAssignments,
    addAssignment,
    updateAssignment,
    deleteAssignment,
  }
})
