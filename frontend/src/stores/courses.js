import { defineStore } from 'pinia'
import { ref } from 'vue'
import { courseService } from '../services/academic'

export const useCourseStore = defineStore('courses', () => {
  const courses = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchCourses(filters = {}) {
    loading.value = true
    error.value = ''
    try {
      const data = await courseService.list(filters)
      courses.value = Array.isArray(data) ? data : []
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function addCourse(payload) {
    const created = await courseService.create(payload)
    courses.value.push(created)
    courses.value.sort((a, b) => a.name.localeCompare(b.name))
    return created
  }

  async function updateCourse(id, payload) {
    const updated = await courseService.update(id, payload)
    const index = courses.value.findIndex((course) => course.id === id)
    if (index !== -1) courses.value[index] = updated
    courses.value.sort((a, b) => a.name.localeCompare(b.name))
    return updated
  }

  async function deleteCourse(id) {
    await courseService.delete(id)
    courses.value = courses.value.filter((course) => course.id !== id)
  }

  return {
    courses,
    loading,
    error,
    fetchCourses,
    addCourse,
    updateCourse,
    deleteCourse,
  }
})
