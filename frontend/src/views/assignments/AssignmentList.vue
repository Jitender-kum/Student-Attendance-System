<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Assignments</h1>
        <p>{{ store.assignments.length }} assignments in your workspace</p>
      </div>
      <button class="btn-add" @click="openModal()">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Add Assignment
      </button>
    </div>

    <div v-if="store.error" class="state-banner error-banner">
      Could not reach backend: {{ store.error }}
    </div>

    <div class="filters">
      <select v-model="departmentFilter" class="filter-select">
        <option value="">All Departments</option>
        <option v-for="department in departmentStore.departments" :key="department.id" :value="String(department.id)">
          {{ department.name }}
        </option>
      </select>
      <select v-model="courseFilter" class="filter-select">
        <option value="">All Courses</option>
        <option v-for="course in filteredCourseOptions" :key="course.id" :value="String(course.id)">
          {{ course.name }}
        </option>
      </select>
      <input v-model="dueDateFilter" type="date" class="filter-select" />
      <input v-model="startDateFilter" type="date" class="filter-select" />
      <input v-model="endDateFilter" type="date" class="filter-select" />
      <input v-model="search" type="text" class="search-input" placeholder="Search assignment title…" />
    </div>

    <div class="table-card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Title</th>
              <th>Department</th>
              <th>Course</th>
              <th>Due Date</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredAssignments.length === 0">
              <td colspan="7" class="empty-row">No assignments found.</td>
            </tr>
            <tr v-for="(assignment, index) in filteredAssignments" :key="assignment.id">
              <td>{{ index + 1 }}</td>
              <td>{{ assignment.title }}</td>
              <td>{{ assignment.departmentName }}</td>
              <td>{{ assignment.courseName }}</td>
              <td>{{ assignment.dueDate || '—' }}</td>
              <td class="description-cell">{{ assignment.description || '—' }}</td>
              <td class="actions">
                <button class="action-btn edit" @click="openModal(assignment)">Edit</button>
                <button class="action-btn delete" @click="confirmDelete(assignment)">Delete</button>
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
            <h2>{{ editingAssignment ? 'Edit Assignment' : 'Add Assignment' }}</h2>
            <button class="modal-close" @click="closeModal">×</button>
          </div>
          <form class="modal-form" @submit.prevent="saveAssignment">
            <div class="field">
              <label>Department</label>
              <select v-model="form.departmentId">
                <option value="">Select department</option>
                <option v-for="department in departmentStore.departments" :key="department.id" :value="department.id">
                  {{ department.name }}
                </option>
              </select>
            </div>
            <div class="field">
              <label>Course</label>
              <select v-model="form.courseId">
                <option value="">Select course</option>
                <option v-for="course in modalCourseOptions" :key="course.id" :value="course.id">
                  {{ course.name }}
                </option>
              </select>
            </div>
            <div class="field">
              <label>Title</label>
              <input v-model="form.title" type="text" placeholder="Assignment title" />
            </div>
            <div class="field">
              <label>Description</label>
              <textarea v-model="form.description" rows="5" placeholder="Assignment details"></textarea>
            </div>
            <div class="field">
              <label>Due Date</label>
              <input v-model="form.dueDate" type="date" />
            </div>
            <div v-if="apiError" class="api-error">{{ apiError }}</div>
            <div class="modal-actions">
              <button type="submit" class="btn-primary">{{ editingAssignment ? 'Save Changes' : 'Add Assignment' }}</button>
              <button type="button" class="btn-secondary" @click="closeModal">Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div v-if="deleteTarget" class="modal-overlay" @click.self="deleteTarget = null">
        <div class="modal modal-sm">
          <div class="modal-header">
            <h2>Delete Assignment</h2>
            <button class="modal-close" @click="deleteTarget = null">×</button>
          </div>
          <p class="confirm-msg">Delete <strong>{{ deleteTarget?.title }}</strong>?</p>
          <div v-if="deleteError" class="api-error">{{ deleteError }}</div>
          <div class="modal-actions">
            <button class="btn-danger" @click="deleteAssignment">Yes, Delete</button>
            <button class="btn-secondary" @click="deleteTarget = null">Cancel</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useAssignmentStore } from '../../stores/assignments'
import { useDepartmentStore } from '../../stores/departments'
import { useCourseStore } from '../../stores/courses'

const store = useAssignmentStore()
const departmentStore = useDepartmentStore()
const courseStore = useCourseStore()

const search = ref('')
const departmentFilter = ref('')
const courseFilter = ref('')
const dueDateFilter = ref('')
const startDateFilter = ref('')
const endDateFilter = ref('')
const showModal = ref(false)
const editingAssignment = ref(null)
const deleteTarget = ref(null)
const apiError = ref('')
const deleteError = ref('')
const form = reactive({
  departmentId: '',
  courseId: '',
  title: '',
  description: '',
  dueDate: '',
})

onMounted(async () => {
  await Promise.all([
    departmentStore.fetchDepartments(),
    courseStore.fetchCourses(),
  ])
  await loadAssignments()
})

watch(departmentFilter, async (value) => {
  courseFilter.value = ''
  await courseStore.fetchCourses(value ? { departmentId: value } : {})
  await loadAssignments()
})

watch([courseFilter, dueDateFilter, startDateFilter, endDateFilter], loadAssignments)

watch(() => form.departmentId, async (value) => {
  await courseStore.fetchCourses(value ? { departmentId: value } : {})
  if (value && !modalCourseOptions.value.find((course) => course.id === Number(form.courseId))) {
    form.courseId = ''
  }
})

const filteredAssignments = computed(() => {
  const query = search.value.trim().toLowerCase()
  if (!query) return store.assignments
  return store.assignments.filter((assignment) =>
    assignment.title.toLowerCase().includes(query) ||
    assignment.departmentName.toLowerCase().includes(query) ||
    assignment.courseName.toLowerCase().includes(query)
  )
})

const filteredCourseOptions = computed(() => {
  if (!departmentFilter.value) return courseStore.courses
  return courseStore.courses.filter((course) => String(course.departmentId) === departmentFilter.value)
})

const modalCourseOptions = computed(() => {
  if (!form.departmentId) return courseStore.courses
  return courseStore.courses.filter((course) => course.departmentId === Number(form.departmentId))
})

async function loadAssignments() {
  await store.fetchAssignments({
    departmentId: departmentFilter.value || undefined,
    courseId: courseFilter.value || undefined,
    dueDate: dueDateFilter.value || undefined,
    startDate: startDateFilter.value || undefined,
    endDate: endDateFilter.value || undefined,
  })
}

function openModal(assignment = null) {
  editingAssignment.value = assignment
  apiError.value = ''
  Object.assign(form, {
    departmentId: assignment?.departmentId || '',
    courseId: assignment?.courseId || '',
    title: assignment?.title || '',
    description: assignment?.description || '',
    dueDate: assignment?.dueDate || '',
  })
  courseStore.fetchCourses(form.departmentId ? { departmentId: form.departmentId } : {})
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function saveAssignment() {
  apiError.value = ''
  try {
    const payload = {
      departmentId: Number(form.departmentId),
      courseId: Number(form.courseId),
      title: form.title.trim(),
      description: form.description.trim() || null,
      dueDate: form.dueDate,
    }
    if (editingAssignment.value) {
      await store.updateAssignment(editingAssignment.value.id, payload)
    } else {
      await store.addAssignment(payload)
    }
    await loadAssignments()
    closeModal()
  } catch (e) {
    apiError.value = e.message
  }
}

function confirmDelete(assignment) {
  deleteTarget.value = assignment
  deleteError.value = ''
}

async function deleteAssignment() {
  if (!deleteTarget.value) return
  try {
    await store.deleteAssignment(deleteTarget.value.id)
    deleteTarget.value = null
  } catch (e) {
    deleteError.value = e.message
  }
}
</script>

<style scoped>
.page { padding: 4px 0; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; gap: 12px; }
.page-header h1 { margin: 0 0 4px; font-size: 24px; color: #111827; }
.page-header p { margin: 0; color: #6b7280; font-size: 13.5px; }
.btn-add, .btn-primary { display: inline-flex; align-items: center; gap: 7px; padding: 9px 18px; background: #7c3aed; color: white; border: none; border-radius: 9px; cursor: pointer; font-weight: 600; }
.btn-secondary { padding: 9px 18px; background: white; border: 1.5px solid #e5e7eb; border-radius: 9px; cursor: pointer; }
.btn-danger { padding: 9px 18px; background: #ef4444; color: white; border: none; border-radius: 9px; cursor: pointer; font-weight: 600; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.filter-select, .search-input { padding: 10px 12px; border: 1.5px solid #e5e7eb; border-radius: 9px; background: #fff; }
.search-input { min-width: 260px; }
.table-card { background: #fff; border-radius: 14px; border: 1px solid #e5e7eb; overflow: hidden; box-shadow: 0 1px 6px rgba(0,0,0,0.06); }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 14px 16px; text-align: left; border-bottom: 1px solid #f3f4f6; vertical-align: top; }
thead th { font-size: 11.5px; text-transform: uppercase; color: #6b7280; }
.actions { display: flex; gap: 8px; }
.action-btn { border: none; background: #f9fafb; border-radius: 8px; padding: 7px 10px; cursor: pointer; }
.action-btn.edit { color: #7c3aed; }
.action-btn.delete { color: #dc2626; }
.empty-row { text-align: center; color: #9ca3af; }
.description-cell { min-width: 240px; color: #6b7280; }
.state-banner { margin-bottom: 16px; padding: 12px 16px; border-radius: 10px; }
.error-banner { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; }
.modal-overlay { position: fixed; inset: 0; background: rgba(17,24,39,0.35); display: flex; align-items: center; justify-content: center; padding: 20px; z-index: 1000; }
.modal { background: #fff; border-radius: 16px; width: 100%; max-width: 520px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.18); }
.modal-sm { max-width: 420px; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px 16px; border-bottom: 1px solid #f3f4f6; }
.modal-form { padding: 20px 24px 24px; display: flex; flex-direction: column; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field input, .field select, .field textarea { padding: 10px 12px; border: 1.5px solid #e5e7eb; border-radius: 9px; font: inherit; }
.field textarea { resize: vertical; }
.modal-actions { display: flex; gap: 10px; }
.modal-close { border: none; background: none; font-size: 22px; cursor: pointer; color: #9ca3af; }
.api-error { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; border-radius: 10px; padding: 11px 14px; font-size: 13px; }
.confirm-msg { padding: 16px 24px; font-size: 14px; color: #374151; line-height: 1.6; margin: 0; }
</style>
