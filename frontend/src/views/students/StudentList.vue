<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Student List</h1>
        <p>{{ store.students.length }} students registered</p>
      </div>
      <button class="btn-add" @click="openModal()">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Add Student
      </button>
    </div>

    <div v-if="store.error" class="state-banner error-banner">
      Could not reach backend: {{ store.error }}
    </div>

    <div class="table-card">
      <div class="toolbar">
        <div class="search-bar">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#9b93a5" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input type="text" placeholder="Search by name, roll or email…" v-model="search" />
        </div>
        <div class="toolbar-filters">
          <select v-model="departmentFilter" class="toolbar-select">
            <option value="">All Departments</option>
            <option v-for="department in departmentStore.departments" :key="department.id" :value="String(department.id)">
              {{ department.name }}
            </option>
          </select>
          <select v-model="courseFilter" class="toolbar-select">
            <option value="">All Courses</option>
            <option v-for="course in filteredCourseOptions" :key="course.id" :value="String(course.id)">
              {{ course.name }}
            </option>
          </select>
        </div>
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Roll No.</th>
              <th>Department</th>
              <th>Course</th>
              <th>Year</th>
              <th>Sem</th>
              <th>Phone</th>
              <th>Email</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filtered.length === 0">
              <td colspan="11" class="empty-row">No students found</td>
            </tr>
            <tr v-for="(s, i) in filtered" :key="s.id">
              <td class="col-num">{{ i + 1 }}</td>
              <td class="col-name">
                <div class="student-name-cell">
                  <div class="avatar-sm">{{ initials(s.name) }}</div>
                  <div>
                    <div class="name-text">{{ s.name }}</div>
                    <div class="sub-text">{{ s.address || 'No address' }}</div>
                  </div>
                </div>
              </td>
              <td><span class="badge-roll">{{ s.rollNo }}</span></td>
              <td>{{ s.department }}</td>
              <td><span class="badge-course">{{ s.course }}</span></td>
              <td><span class="badge-year">Year {{ s.year || '—' }}</span></td>
              <td><span class="badge-semester">Sem {{ s.semester || '—' }}</span></td>
              <td>{{ s.phoneNumber || '—' }}</td>
              <td class="col-email">{{ s.email || '—' }}</td>
              <td>
                <span class="badge-status" :class="s.status ? 'active' : 'inactive'">
                  {{ s.status ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td class="col-actions">
                <button class="action-btn edit" @click="openModal(s)" title="Edit">Edit</button>
                <button class="action-btn delete" @click="confirmDelete(s)" title="Delete">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <Teleport to="body">
      <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
        <div class="modal">
          <div class="modal-header">
            <h2>{{ editingStudent ? 'Edit Student' : 'Add Student' }}</h2>
            <button class="modal-close" @click="closeModal">×</button>
          </div>

          <form class="modal-form" @submit.prevent="saveStudent">
            <div class="modal-body">
              <div class="form-row">
                <div class="field" :class="{ error: err.name }">
                  <label>Full Name</label>
                  <input v-model="form.name" type="text" placeholder="e.g. Rahul Sharma" />
                  <span class="err-msg" v-if="err.name">{{ err.name }}</span>
                </div>
                <div class="field" :class="{ error: err.rollNumber }">
                  <label>Roll Number</label>
                  <input v-model="form.rollNumber" type="text" placeholder="e.g. CS101" />
                  <span class="err-msg" v-if="err.rollNumber">{{ err.rollNumber }}</span>
                </div>
              </div>
              <div class="form-row">
                <div class="field" :class="{ error: err.departmentId }">
                  <label>Department</label>
                  <select v-model="form.departmentId">
                    <option value="">Select department</option>
                    <option v-for="department in departmentStore.departments" :key="department.id" :value="department.id">
                      {{ department.name }}
                    </option>
                  </select>
                  <span class="err-msg" v-if="err.departmentId">{{ err.departmentId }}</span>
                </div>
                <div class="field" :class="{ error: err.courseId }">
                  <label>Course</label>
                  <select v-model="form.courseId">
                    <option value="">Select course</option>
                    <option v-for="course in modalCourseOptions" :key="course.id" :value="course.id">
                      {{ course.name }}
                    </option>
                  </select>
                  <span class="err-msg" v-if="err.courseId">{{ err.courseId }}</span>
                </div>
              </div>
              <div class="form-row">
                <div class="field" :class="{ error: err.year }">
                  <label>Year</label>
                  <input v-model.number="form.year" type="number" min="1" max="10" placeholder="1" />
                  <span class="err-msg" v-if="err.year">{{ err.year }}</span>
                </div>
                <div class="field" :class="{ error: err.semester }">
                  <label>Semester</label>
                  <input v-model.number="form.semester" type="number" min="1" max="12" placeholder="1" />
                  <span class="err-msg" v-if="err.semester">{{ err.semester }}</span>
                </div>
              </div>
              <div class="form-row">
                <div class="field" :class="{ error: err.phone }">
                  <label>Phone Number</label>
                  <input v-model="form.phone" type="text" placeholder="10-digit number" />
                  <span class="err-msg" v-if="err.phone">{{ err.phone }}</span>
                </div>
              </div>
              <div class="form-row">
                <div class="field" :class="{ error: err.email }">
                  <label>Email</label>
                  <input v-model="form.email" type="email" placeholder="student@example.com" />
                  <span class="err-msg" v-if="err.email">{{ err.email }}</span>
                </div>
                <div class="field" :class="{ error: err.gender }">
                  <label>Gender</label>
                  <select v-model="form.gender">
                    <option value="">Select gender</option>
                    <option>Male</option>
                    <option>Female</option>
                    <option>Other</option>
                  </select>
                  <span class="err-msg" v-if="err.gender">{{ err.gender }}</span>
                </div>
              </div>
              <div class="field" :class="{ error: err.address }">
                <label>Address</label>
                <input v-model="form.address" type="text" placeholder="City / Address" />
                <span class="err-msg" v-if="err.address">{{ err.address }}</span>
              </div>
              <div class="form-row">
                <div class="field">
                  <label>Father's Name</label>
                  <input v-model="form.fatherName" type="text" />
                </div>
                <div class="field">
                  <label>Father's Phone</label>
                  <input v-model="form.fatherPhone" type="text" />
                </div>
              </div>
              <div class="form-row">
                <div class="field">
                  <label>Mother's Name</label>
                  <input v-model="form.motherName" type="text" />
                </div>
                <div class="field">
                  <label>Mother's Phone</label>
                  <input v-model="form.motherPhone" type="text" />
                </div>
              </div>
              <div class="form-row">
                <div class="field">
                  <label>Father's Occupation</label>
                  <input v-model="form.fatherOccupation" type="text" />
                </div>
                <div class="field">
                  <label>Status</label>
                  <select v-model="form.status">
                    <option :value="true">Active</option>
                    <option :value="false">Inactive</option>
                  </select>
                </div>
              </div>
              <div class="api-error" v-if="apiError">{{ apiError }}</div>
            </div>

            <div class="modal-actions">
              <button type="submit" class="btn-primary" :disabled="saving">
                {{ saving ? 'Saving…' : (editingStudent ? 'Save Changes' : 'Add Student') }}
              </button>
              <button type="button" class="btn-secondary" @click="closeModal" :disabled="saving">Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div class="modal-overlay" v-if="deleteTarget" @click.self="deleteTarget = null">
        <div class="modal modal-sm">
          <div class="modal-header">
            <h2>Delete Student</h2>
            <button class="modal-close" @click="deleteTarget = null">×</button>
          </div>
          <p class="confirm-msg">Are you sure you want to delete <strong>{{ deleteTarget?.name }}</strong>?</p>
          <div v-if="deleteError" class="api-error delete-error">{{ deleteError }}</div>
          <div class="modal-actions">
            <button class="btn-danger" @click="doDelete">Yes, Delete</button>
            <button class="btn-secondary" @click="deleteTarget = null">Cancel</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useStudentStore } from '../../stores/students'
import { useDepartmentStore } from '../../stores/departments'
import { useCourseStore } from '../../stores/courses'

const store = useStudentStore()
const departmentStore = useDepartmentStore()
const courseStore = useCourseStore()

const search = ref('')
const departmentFilter = ref('')
const courseFilter = ref('')
const showModal = ref(false)
const editingStudent = ref(null)
const saving = ref(false)
const apiError = ref('')
const deleteTarget = ref(null)
const deleteError = ref('')

const emptyForm = () => ({
  name: '',
  rollNumber: '',
  email: '',
  phone: '',
  gender: '',
  address: '',
  year: 1,
  semester: 1,
  departmentId: '',
  courseId: '',
  fatherName: '',
  fatherPhone: '',
  fatherOccupation: '',
  motherName: '',
  motherPhone: '',
  status: true,
})

const form = reactive(emptyForm())
const createEmptyErrors = () => ({
  name: '',
  rollNumber: '',
  departmentId: '',
  courseId: '',
  year: '',
  semester: '',
  phone: '',
  email: '',
  gender: '',
  address: '',
})

const err = reactive(createEmptyErrors())

onMounted(async () => {
  await Promise.all([
    departmentStore.fetchDepartments(),
    courseStore.fetchCourses(),
    store.fetchStudents(),
  ])
})

watch(departmentFilter, async (value) => {
  courseFilter.value = ''
  await courseStore.fetchCourses(value ? { departmentId: value } : {})
  await fetchFilteredStudents()
})

watch(courseFilter, fetchFilteredStudents)
watch(() => form.departmentId, async (value) => {
  if (showModal.value) {
    await courseStore.fetchCourses(value ? { departmentId: value } : {})
    if (value && !modalCourseOptions.value.find((course) => course.id === Number(form.courseId))) {
      form.courseId = ''
    }
  }
})

async function fetchFilteredStudents() {
  const filters = {}
  if (departmentFilter.value) filters.departmentId = departmentFilter.value
  if (courseFilter.value) filters.courseId = courseFilter.value
  await store.fetchStudents(filters)
}

function initials(name = '') {
  const p = name.trim().split(' ')
  return (p[0]?.[0] ?? '') + (p[1]?.[0] ?? '')
}

const filtered = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return store.students
  return store.students.filter((s) =>
    (s.name || '').toLowerCase().includes(q) ||
    (s.rollNo || '').toLowerCase().includes(q) ||
    (s.email || '').toLowerCase().includes(q) ||
    (s.department || '').toLowerCase().includes(q) ||
    (s.course || '').toLowerCase().includes(q)
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

function openModal(student = null) {
  editingStudent.value = student
  apiError.value = ''
  Object.assign(err, createEmptyErrors())
  Object.assign(form, student ? {
    name: student.name,
    rollNumber: student.rollNo,
    email: student.email,
    phone: student.phoneNumber,
    gender: student.gender,
    address: student.address,
    year: student.year,
    semester: student.semester,
    departmentId: student.departmentId,
    courseId: student.courseId,
    fatherName: student.fatherName,
    fatherPhone: student.fatherPhone,
    fatherOccupation: student.fatherOccupation,
    motherName: student.motherName,
    motherPhone: student.motherPhone,
    status: student.status,
  } : emptyForm())
  courseStore.fetchCourses(form.departmentId ? { departmentId: form.departmentId } : {})
  showModal.value = true
}

function closeModal(force = false) {
  if (force || !saving.value) showModal.value = false
}

function validate() {
  const email = form.email?.trim() || ''
  const phone = form.phone?.trim() || ''
  const address = form.address?.trim() || ''

  err.name = form.name.trim() ? '' : 'Full name is required.'
  err.rollNumber = form.rollNumber.trim() ? '' : 'Roll number is required.'
  err.departmentId = form.departmentId ? '' : 'Department is required.'
  err.courseId = form.courseId ? '' : 'Course is required.'
  err.year = Number.isInteger(Number(form.year)) && Number(form.year) >= 1 && Number(form.year) <= 10
    ? ''
    : 'Year must be between 1 and 10.'
  err.semester = Number.isInteger(Number(form.semester)) && Number(form.semester) >= 1 && Number(form.semester) <= 12
    ? ''
    : 'Semester must be between 1 and 12.'
  err.phone = !phone
    ? 'Phone number is required.'
    : (/^\d{10,15}$/.test(phone) ? '' : 'Phone number must contain 10 to 15 digits.')
  err.email = !email
    ? 'Email is required.'
    : (/\S+@\S+\.\S+/.test(email) ? '' : 'Enter a valid email.')
  err.gender = form.gender ? '' : 'Gender is required.'
  err.address = address ? '' : 'Address is required.'
  return !Object.values(err).some(Boolean)
}

function applyServerErrors(payload) {
  if (!payload || typeof payload !== 'object' || !payload.errors) return false
  let matched = false
  const fieldMap = {
    name: 'name',
    rollNumber: 'rollNumber',
    departmentId: 'departmentId',
    courseId: 'courseId',
    year: 'year',
    semester: 'semester',
    phone: 'phone',
    email: 'email',
    gender: 'gender',
    address: 'address',
  }

  Object.entries(payload.errors).forEach(([field, message]) => {
    const targetField = fieldMap[field]
    if (targetField && targetField in err) {
      err[targetField] = message
      matched = true
    }
  })

  return matched
}

function toPayload() {
  return {
    name: form.name.trim(),
    rollNumber: form.rollNumber.trim(),
    email: form.email?.trim() || null,
    phone: form.phone?.trim() || null,
    gender: form.gender || null,
    address: form.address?.trim() || null,
    year: Number(form.year) || null,
    semester: Number(form.semester) || null,
    departmentId: Number(form.departmentId),
    courseId: Number(form.courseId),
    fatherName: form.fatherName?.trim() || null,
    fatherPhone: form.fatherPhone?.trim() || null,
    fatherOccupation: form.fatherOccupation?.trim() || null,
    motherName: form.motherName?.trim() || null,
    motherPhone: form.motherPhone?.trim() || null,
    status: !!form.status,
  }
}

async function saveStudent() {
  if (!validate()) return
  saving.value = true
  apiError.value = ''
  try {
    let savedStudent
    if (editingStudent.value) {
      savedStudent = await store.updateStudent(editingStudent.value.id, toPayload())
    } else {
      savedStudent = await store.addStudent(toPayload())
      search.value = ''
      if (!departmentFilter.value || Number(departmentFilter.value) !== savedStudent.departmentId) {
        departmentFilter.value = savedStudent.departmentId ? String(savedStudent.departmentId) : ''
      }
      if (!courseFilter.value || Number(courseFilter.value) !== savedStudent.courseId) {
        courseFilter.value = savedStudent.courseId ? String(savedStudent.courseId) : ''
      }
    }
    await fetchFilteredStudents()
    closeModal(true)
  } catch (e) {
    const hasFieldErrors = applyServerErrors(e.payload)
    apiError.value = hasFieldErrors
      ? 'Please correct the highlighted fields.'
      : (e.message || 'Something went wrong. Please try again.')
  } finally {
    saving.value = false
  }
}

function confirmDelete(student) {
  deleteTarget.value = student
  deleteError.value = ''
}

async function doDelete() {
  if (!deleteTarget.value) return
  try {
    await store.deleteStudent(deleteTarget.value.id)
    deleteTarget.value = null
    deleteError.value = ''
  } catch (e) {
    deleteError.value = e.message || 'Unable to delete student.'
  }
}
</script>

<style scoped>
.page { padding: 4px 0; }

/* ── Loading / Error banners ─────────────────────────────────────── */
.state-banner {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 18px; border-radius: 12px;
  font-size: 13.5px; font-weight: 500;
  margin-bottom: 16px;
}

.loading-banner {
  background: #eff6ff; border: 1px solid #bfdbfe; color: #1d4ed8;
}

.error-banner {
  background: #fef2f2; border: 1px solid #fecaca; color: #dc2626;
}

.retry-btn {
  margin-left: auto;
  padding: 5px 14px; font-size: 12.5px; font-weight: 600;
  border: 1.5px solid #dc2626; border-radius: 7px;
  background: transparent; color: #dc2626; cursor: pointer;
  transition: all 0.15s;
}
.retry-btn:hover { background: #dc2626; color: #fff; }

@keyframes spin { to { transform: rotate(360deg); } }
.spin { animation: spin 0.9s linear infinite; }

/* ── Header ─────────────────────────────────────────────────────── */
.page-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 24px;
}
.page-header h1 { font-size: 24px; font-weight: 700; color: #111827; margin: 0 0 3px; }
.page-header p  { font-size: 13.5px; color: #6b7280; margin: 0; }

.btn-add {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 9px 18px;
  background: #7c3aed;
  color: white; font-size: 13.5px; font-weight: 600;
  border: none; border-radius: 9px; cursor: pointer;
  box-shadow: 0 2px 8px rgba(124,58,237,0.28);
  transition: background 0.15s, transform 0.15s; font-family: inherit;
}
.btn-add:hover { background: #6d28d9; transform: translateY(-1px); }

/* ── Table Card ─────────────────────────────────────────────────── */
.table-card {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06);
}

.toolbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 18px;
  border-bottom: 1px solid #f3f4f6;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-filters { display: flex; gap: 10px; flex-wrap: wrap; }
.toolbar-select {
  min-width: 180px;
  padding: 8px 12px;
  border: 1.5px solid #e5e7eb;
  border-radius: 9px;
  background: #fff;
  font-size: 13px;
}

.search-bar {
  display: flex; align-items: center; gap: 8px;
  background: #f9fafb;
  border: 1.5px solid #e5e7eb;
  border-radius: 9px; padding: 8px 12px;
  width: 300px; transition: border-color 0.2s;
}
.search-bar:focus-within { border-color: #7c3aed; background: #fff; }
.search-bar input {
  border: none; outline: none; font-size: 13.5px;
  color: #111827; width: 100%; background: transparent; font-family: inherit;
}
.search-bar input::placeholder { color: #9ca3af; }
.clear-search {
  background: none; border: none; cursor: pointer;
  color: #9ca3af; display: flex; padding: 0;
}
.clear-search:hover { color: #6b7280; }

/* ── Table ──────────────────────────────────────────────────────── */
.table-wrap { overflow-x: auto; }

table {
  width: 100%; border-collapse: collapse;
  font-size: 13.5px;
}

thead tr { background: #f9fafb; }
thead th {
  padding: 11px 16px; text-align: left;
  font-size: 11.5px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.5px;
  color: #6b7280; border-bottom: 1px solid #e5e7eb;
  white-space: nowrap;
}

tbody tr {
  border-bottom: 1px solid #f3f4f6;
  transition: background 0.12s;
}
tbody tr:last-child { border-bottom: none; }
tbody tr:hover { background: #faf8ff; }

tbody td { padding: 12px 16px; color: #374151; vertical-align: middle; }

.col-num { color: #9ca3af; font-size: 12.5px; width: 40px; }
.col-email { color: #6b7280; font-size: 12.5px; }
.col-actions { width: 90px; }

/* ── Student name cell ──────────────────────────────────────────── */
.student-name-cell { display: flex; align-items: center; gap: 10px; }
.avatar-sm {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, #ede9fe, #ddd6fe);
  color: #7c3aed; font-size: 10.5px; font-weight: 700;
  display: grid; place-items: center; flex-shrink: 0;
}
.name-text { font-weight: 600; color: #111827; font-size: 13.5px; }
.sub-text   { font-size: 11.5px; color: #9ca3af; margin-top: 1px; }

/* ── Badges ─────────────────────────────────────────────────────── */
.badge-roll {
  background: #f3f4f6; color: #374151;
  font-size: 12px; font-weight: 600; font-family: monospace;
  padding: 3px 9px; border-radius: 6px;
}

.badge-course {
  background: #ede9fe; color: #7c3aed;
  font-size: 12px; font-weight: 600;
  padding: 3px 9px; border-radius: 6px;
}

.badge-year {
  background: #dbeafe; color: #1d4ed8;
  font-size: 12px; font-weight: 600;
  padding: 3px 9px; border-radius: 6px;
}

.badge-semester {
  background: #fef3c7; color: #d97706;
  font-size: 12px; font-weight: 600;
  padding: 3px 9px; border-radius: 6px;
}

.badge-status {
  font-size: 11.5px; font-weight: 700;
  padding: 3px 10px; border-radius: 99px;
}
.badge-status.active   { background: #d1fae5; color: #059669; }
.badge-status.inactive { background: #fee2e2; color: #dc2626; }

/* ── Action buttons ─────────────────────────────────────────────── */
.action-btn {
  background: none; border: none; cursor: pointer;
  padding: 6px; border-radius: 7px;
  display: inline-flex; align-items: center;
  transition: background 0.15s, color 0.15s;
}
.action-btn.edit  { color: #7c3aed; }
.action-btn.edit:hover  { background: #ede9fe; }
.action-btn.delete { color: #9ca3af; }
.action-btn.delete:hover { background: #fee2e2; color: #ef4444; }

/* ── Empty state ────────────────────────────────────────────────── */
.empty-row { text-align: center; padding: 48px 0 !important; }
.empty-state {
  display: flex; flex-direction: column; align-items: center; gap: 10px;
  color: #9ca3af; font-size: 14px;
}

/* ── Modal ──────────────────────────────────────────────────────── */
.modal-overlay {
  position: fixed; inset: 0; z-index: 1000;
  background: rgba(17,24,39,0.35);
  backdrop-filter: blur(3px);
  display: flex; align-items: center; justify-content: center;
  padding: 20px;
}

.modal {
  background: #fff;
  border-radius: 16px;
  width: 100%; max-width: 540px;
  max-height: min(92vh, 860px);
  box-shadow: 0 20px 60px rgba(0,0,0,0.18);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: modal-in 0.2s ease;
}
.modal-sm { max-width: 400px; }

@keyframes modal-in {
  from { opacity: 0; transform: translateY(12px) scale(0.97); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f3f4f6;
  flex-shrink: 0;
  background: #fff;
}
.modal-header h2 {
  font-size: 18px; font-weight: 700; color: #111827; margin: 0;
}

.modal-close {
  background: none; border: none; cursor: pointer; color: #9ca3af;
  display: flex; padding: 4px; border-radius: 6px;
  transition: color 0.15s, background 0.15s;
}
.modal-close:hover { color: #374151; background: #f3f4f6; }

/* ── Form inside modal ──────────────────────────────────────────── */
.modal-form {
  display: flex;
  flex-direction: column;
  min-height: 0;
  flex: 1;
}

.modal-body {
  padding: 20px 24px 12px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  overflow-y: auto;
  min-height: 0;
}

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }

.field { display: flex; flex-direction: column; gap: 5px; }
.field label { font-size: 12.5px; font-weight: 600; color: #374151; }
.field input, .field select {
  padding: 9px 12px;
  border: 1.5px solid #e5e7eb; border-radius: 9px;
  font-size: 13.5px; color: #111827; outline: none;
  transition: border-color 0.2s, box-shadow 0.2s; font-family: inherit;
  background: #fff;
}
.field input:focus, .field select:focus {
  border-color: #7c3aed; box-shadow: 0 0 0 3px rgba(124,58,237,0.1);
}
.field input::placeholder { color: #9ca3af; }
.field.error input, .field.error select { border-color: #ef4444; }
.err-msg { font-size: 11.5px; color: #ef4444; }

.confirm-msg { padding: 16px 24px; font-size: 14px; color: #374151; line-height: 1.6; margin: 0; }

.api-error {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 14px; border-radius: 9px;
  background: #fef2f2; border: 1px solid #fecaca;
  color: #dc2626; font-size: 12.5px; font-weight: 500;
  margin: 0 0 4px;
}

.modal-actions {
  display: flex; gap: 10px; padding: 16px 24px 20px;
  border-top: 1px solid #f3f4f6;
  background: #fff;
  flex-shrink: 0;
}

.btn-primary {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 9px 22px; background: #7c3aed;
  color: white; font-size: 13.5px; font-weight: 600;
  border: none; border-radius: 9px; cursor: pointer;
  transition: background 0.15s; font-family: inherit;
}
.btn-primary:hover:not(:disabled) { background: #6d28d9; }
.btn-primary:disabled { opacity: 0.65; cursor: not-allowed; }

.btn-secondary {
  padding: 9px 18px; background: white; color: #374151;
  font-size: 13.5px; font-weight: 500;
  border: 1.5px solid #e5e7eb; border-radius: 9px;
  cursor: pointer; transition: background 0.15s; font-family: inherit;
}
.btn-secondary:hover { background: #f9fafb; }

.btn-danger {
  padding: 9px 22px; background: #ef4444;
  color: white; font-size: 13.5px; font-weight: 600;
  border: none; border-radius: 9px; cursor: pointer;
  transition: background 0.15s; font-family: inherit;
}
.btn-danger:hover { background: #dc2626; }

@media (max-height: 760px) {
  .modal-overlay { padding: 12px; }
  .modal { max-height: calc(100vh - 24px); }
  .modal-header { padding: 16px 20px 14px; }
  .modal-body { padding: 16px 20px 10px; }
  .modal-actions { padding: 14px 20px 16px; }
}

@media (max-width: 640px) {
  .form-row { grid-template-columns: 1fr; }
  .modal { max-width: 100%; }
  .modal-actions { flex-direction: column-reverse; }
  .btn-primary, .btn-secondary { width: 100%; justify-content: center; }
}
</style>
