import { defineStore } from 'pinia'
import { ref } from 'vue'
import { departmentService } from '../services/academic'

export const useDepartmentStore = defineStore('departments', () => {
  const departments = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchDepartments() {
    loading.value = true
    error.value = ''
    try {
      const data = await departmentService.list()
      departments.value = Array.isArray(data) ? data : []
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function addDepartment(payload) {
    const created = await departmentService.create(payload)
    departments.value.push(created)
    departments.value.sort((a, b) => a.name.localeCompare(b.name))
    return created
  }

  async function updateDepartment(id, payload) {
    const updated = await departmentService.update(id, payload)
    const index = departments.value.findIndex((department) => department.id === id)
    if (index !== -1) departments.value[index] = updated
    departments.value.sort((a, b) => a.name.localeCompare(b.name))
    return updated
  }

  async function deleteDepartment(id) {
    await departmentService.delete(id)
    departments.value = departments.value.filter((department) => department.id !== id)
  }

  return {
    departments,
    loading,
    error,
    fetchDepartments,
    addDepartment,
    updateDepartment,
    deleteDepartment,
  }
})
