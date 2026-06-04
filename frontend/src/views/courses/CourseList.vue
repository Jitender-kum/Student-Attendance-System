<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Courses</h1>
        <p>{{ store.courses.length }} courses for your workspace</p>
      </div>
      <button class="btn-add" @click="openModal()">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Add Course
      </button>
    </div>

    <div class="filters">
      <select v-model="selectedDepartmentId" class="filter-select">
        <option value="">All departments</option>
        <option v-for="department in departmentStore.departments" :key="department.id" :value="String(department.id)">
          {{ department.name }}
        </option>
      </select>
      <input v-model="search" type="text" class="search-input" placeholder="Search by course name or code…" />
    </div>

    <div class="table-card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Code</th>
              <th>Department</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredCourses.length === 0">
              <td colspan="5" class="empty-row">No courses found.</td>
            </tr>
            <tr v-for="(course, index) in filteredCourses" :key="course.id">
              <td>{{ index + 1 }}</td>
              <td>{{ course.name }}</td>
              <td>{{ course.code || '—' }}</td>
              <td>{{ course.departmentName }}</td>
              <td class="actions">
                <button class="action-btn edit" @click="openModal(course)">Edit</button>
                <button class="action-btn delete" @click="deleteCourse(course)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal">
          <div class="modal-header">
            <h2>{{ editingCourse ? 'Edit Course' : 'Add Course' }}</h2>
            <button class="modal-close" @click="closeModal">×</button>
          </div>
          <form class="modal-form" @submit.prevent="saveCourse">
            <div class="field">
              <label>Name</label>
              <input v-model="form.name" type="text" placeholder="e.g. Data Structures" />
            </div>
            <div class="field">
              <label>Code</label>
              <input v-model="form.code" type="text" placeholder="e.g. CS201" />
            </div>
            <div class="field">
              <label>Department</label>
              <select v-model="form.departmentId">
                <option value="">Select department</option>
                <option v-for="department in departmentStore.departments" :key="department.id" :value="department.id">
                  {{ department.name }}
                </option>
              </select>
            </div>
            <div v-if="error" class="api-error">{{ error }}</div>
            <div class="modal-actions">
              <button type="submit" class="btn-primary">{{ editingCourse ? 'Save Changes' : 'Add Course' }}</button>
              <button type="button" class="btn-secondary" @click="closeModal">Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useCourseStore } from '../../stores/courses'
import { useDepartmentStore } from '../../stores/departments'

const store = useCourseStore()
const departmentStore = useDepartmentStore()
const showModal = ref(false)
const editingCourse = ref(null)
const selectedDepartmentId = ref('')
const search = ref('')
const error = ref('')
const form = reactive({ name: '', code: '', departmentId: '' })

onMounted(async () => {
  await departmentStore.fetchDepartments()
  await store.fetchCourses()
})

watch(selectedDepartmentId, async (value) => {
  await store.fetchCourses(value ? { departmentId: value } : {})
})

const filteredCourses = computed(() => {
  const query = search.value.trim().toLowerCase()
  if (!query) return store.courses
  return store.courses.filter((course) =>
    course.name.toLowerCase().includes(query) || (course.code || '').toLowerCase().includes(query)
  )
})

function openModal(course = null) {
  editingCourse.value = course
  form.name = course?.name || ''
  form.code = course?.code || ''
  form.departmentId = course?.departmentId || ''
  error.value = ''
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function saveCourse() {
  try {
    const payload = {
      name: form.name.trim(),
      code: form.code.trim() || null,
      departmentId: Number(form.departmentId),
    }
    if (editingCourse.value) {
      await store.updateCourse(editingCourse.value.id, payload)
    } else {
      await store.addCourse(payload)
    }
    closeModal()
  } catch (e) {
    error.value = e.message
  }
}

async function deleteCourse(course) {
  try {
    await store.deleteCourse(course.id)
  } catch (e) {
    error.value = e.message
  }
}
</script>

<style scoped>
.page { padding: 4px 0; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; gap: 12px; }
.page-header h1 { margin: 0 0 4px; font-size: 24px; color: #111827; }
.page-header p { margin: 0; color: #6b7280; font-size: 13.5px; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.filter-select, .search-input { padding: 10px 12px; border: 1.5px solid #e5e7eb; border-radius: 9px; background: #fff; }
.search-input { min-width: 260px; }
.btn-add, .btn-primary { display: inline-flex; align-items: center; gap: 7px; padding: 9px 18px; background: #7c3aed; color: white; border: none; border-radius: 9px; cursor: pointer; font-weight: 600; }
.btn-secondary { padding: 9px 18px; background: white; border: 1.5px solid #e5e7eb; border-radius: 9px; cursor: pointer; }
.table-card { background: #fff; border-radius: 14px; border: 1px solid #e5e7eb; overflow: hidden; box-shadow: 0 1px 6px rgba(0,0,0,0.06); }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 14px 16px; text-align: left; border-bottom: 1px solid #f3f4f6; }
thead th { font-size: 11.5px; text-transform: uppercase; color: #6b7280; }
.actions { display: flex; gap: 8px; }
.action-btn { border: none; background: #f9fafb; border-radius: 8px; padding: 7px 10px; cursor: pointer; }
.action-btn.edit { color: #7c3aed; }
.action-btn.delete { color: #dc2626; }
.empty-row { text-align: center; color: #9ca3af; }
.modal-overlay { position: fixed; inset: 0; background: rgba(17,24,39,0.35); display: flex; align-items: center; justify-content: center; padding: 20px; }
.modal { background: #fff; border-radius: 16px; width: 100%; max-width: 460px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.18); }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px 16px; border-bottom: 1px solid #f3f4f6; }
.modal-form { padding: 20px 24px 24px; display: flex; flex-direction: column; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field input, .field select { padding: 10px 12px; border: 1.5px solid #e5e7eb; border-radius: 9px; }
.modal-actions { display: flex; gap: 10px; }
.modal-close { border: none; background: none; font-size: 22px; cursor: pointer; color: #9ca3af; }
.api-error { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; border-radius: 10px; padding: 11px 14px; font-size: 13px; }
</style>
