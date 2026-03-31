<template>
  <div class="page">

    <!-- Page Header -->
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

    <!-- Loading / Error states -->
    <div v-if="store.loading" class="state-banner loading-banner">
      <svg class="spin" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M21 12a9 9 0 1 1-6.219-8.56"/></svg>
      Loading students from server…
    </div>
    <div v-else-if="store.error" class="state-banner error-banner">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
      Could not reach backend: {{ store.error }}
      <button class="retry-btn" @click="store.fetchStudents()">Retry</button>
    </div>

    <!-- Table Card -->
    <div class="table-card">

      <!-- Search + filter bar -->
      <div class="toolbar">
        <div class="search-bar">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#9b93a5" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input type="text" placeholder="Search by name, roll or class…" v-model="search" />
          <button v-if="search" class="clear-search" @click="search = ''" title="Clear">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
      </div>

      <!-- Table -->
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Roll No.</th>
              <th>Class</th>
              <th>Gender</th>
              <th>Phone</th>
              <th>Email</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filtered.length === 0">
              <td colspan="8" class="empty-row">
                <div class="empty-state">
                  <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#d0ccd8" stroke-width="1.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                  <span>No students found</span>
                </div>
              </td>
            </tr>
            <tr v-for="(s, i) in filtered" :key="s.id">
              <td class="col-num">{{ i + 1 }}</td>
              <td class="col-name">
                <div class="student-name-cell">
                  <div class="avatar-sm">{{ s.firstName[0] }}{{ s.lastName[0] }}</div>
                  <div>
                    <div class="name-text">{{ s.firstName }} {{ s.lastName }}</div>
                  </div>
                </div>
              </td>
              <td><span class="badge-roll">{{ s.roll }}</span></td>
              <td>{{ s.classSection }}</td>
              <td>
                <span class="badge-gender" :class="s.gender === 'Female' ? 'female' : 'male'">{{ s.gender }}</span>
              </td>
              <td>{{ s.phone }}</td>
              <td class="col-email">{{ s.email }}</td>
              <td class="col-actions">
                <button class="action-btn edit" @click="openModal(s)" title="Edit">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.83 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                </button>
                <button class="action-btn delete" @click="confirmDelete(s)" title="Delete">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4h6v2"/></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ── Add / Edit Modal ──────────────────────────────────────── -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
        <div class="modal">

          <div class="modal-header">
            <h2>{{ editingStudent ? 'Edit Student' : 'Add Student' }}</h2>
            <button class="modal-close" @click="closeModal">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <form class="modal-form" @submit.prevent="saveStudent">
            <div class="form-row">
              <div class="field" :class="{ error: err.firstName }">
                <label>First Name</label>
                <input v-model="form.firstName" type="text" placeholder="e.g. Aarav" />
                <span class="err-msg" v-if="err.firstName">{{ err.firstName }}</span>
              </div>
              <div class="field" :class="{ error: err.lastName }">
                <label>Last Name</label>
                <input v-model="form.lastName" type="text" placeholder="e.g. Sharma" />
                <span class="err-msg" v-if="err.lastName">{{ err.lastName }}</span>
              </div>
            </div>
            <div class="form-row">
              <div class="field" :class="{ error: err.roll }">
                <label>Roll Number</label>
                <input v-model="form.roll" type="text" placeholder="e.g. S011" />
                <span class="err-msg" v-if="err.roll">{{ err.roll }}</span>
              </div>
              <div class="field" :class="{ error: err.classSection }">
                <label>Class / Section</label>
                <input v-model="form.classSection" type="text" placeholder="e.g. 10-A" />
                <span class="err-msg" v-if="err.classSection">{{ err.classSection }}</span>
              </div>
            </div>
            <div class="form-row">
              <div class="field">
                <label>Gender</label>
                <select v-model="form.gender">
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              <div class="field">
                <label>Phone</label>
                <input v-model="form.phone" type="text" placeholder="10-digit number" />
              </div>
            </div>
            <div class="field" :class="{ error: err.email }">
              <label>Email</label>
              <input v-model="form.email" type="email" placeholder="student@school.edu" />
              <span class="err-msg" v-if="err.email">{{ err.email }}</span>
            </div>

            <div class="modal-actions">
              <button type="submit" class="btn-primary">
                {{ editingStudent ? 'Save Changes' : 'Add Student' }}
              </button>
              <button type="button" class="btn-secondary" @click="closeModal">Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- ── Delete Confirm ────────────────────────────────────────── -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="deleteTarget" @click.self="deleteTarget = null">
        <div class="modal modal-sm">
          <div class="modal-header">
            <h2>Delete Student</h2>
            <button class="modal-close" @click="deleteTarget = null">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <p class="confirm-msg">Are you sure you want to delete <strong>{{ deleteTarget?.firstName }} {{ deleteTarget?.lastName }}</strong>? This action cannot be undone.</p>
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
import { ref, computed, reactive, onMounted } from 'vue'
import { useStudentStore } from '../../stores/students'

const store = useStudentStore()

// Fetch students from backend on page load
onMounted(() => store.fetchStudents())

// ── Search ─────────────────────────────────────────────────────────────────
const search = ref('')
const filtered = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return store.students
  return store.students.filter(s =>
    `${s.firstName} ${s.lastName}`.toLowerCase().includes(q) ||
    s.roll.toLowerCase().includes(q) ||
    s.classSection.toLowerCase().includes(q)
  )
})

// ── Modal state ─────────────────────────────────────────────────────────────
const showModal = ref(false)
const editingStudent = ref(null)

const emptyForm = () => ({ firstName: '', lastName: '', roll: '', classSection: '', gender: 'Male', phone: '', email: '' })
const form = reactive(emptyForm())
const err  = reactive({ firstName: '', lastName: '', roll: '', classSection: '', email: '' })

function openModal(student = null) {
  editingStudent.value = student
  Object.assign(err, { firstName: '', lastName: '', roll: '', classSection: '', email: '' })
  if (student) {
    Object.assign(form, { ...student })
  } else {
    Object.assign(form, emptyForm())
  }
  showModal.value = true
}

function closeModal() { showModal.value = false }

function validate() {
  let ok = true
  err.firstName = form.firstName.trim() ? '' : 'First name is required.'
  err.lastName  = form.lastName.trim()  ? '' : 'Last name is required.'
  err.roll      = form.roll.trim()      ? '' : 'Roll number is required.'
  err.classSection = form.classSection.trim() ? '' : 'Class is required.'
  err.email     = form.email.trim()     ? '' : 'Email is required.'
  ok = !Object.values(err).some(Boolean)
  return ok
}

function saveStudent() {
  if (!validate()) return
  const data = { ...form }
  if (editingStudent.value) {
    store.updateStudent(editingStudent.value.id, data)
  } else {
    store.addStudent(data)
  }
  closeModal()
}

// ── Delete ──────────────────────────────────────────────────────────────────
const deleteTarget = ref(null)
function confirmDelete(s) { deleteTarget.value = s }
function doDelete() {
  store.deleteStudent(deleteTarget.value.id)
  deleteTarget.value = null
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

/* ── Badges ─────────────────────────────────────────────────────── */
.badge-roll {
  background: #f3f4f6; color: #374151;
  font-size: 12px; font-weight: 600; font-family: monospace;
  padding: 3px 9px; border-radius: 6px;
}

.badge-gender {
  font-size: 11.5px; font-weight: 600;
  padding: 3px 10px; border-radius: 99px;
}
.badge-gender.male   { background: #eff6ff; color: #3b82f6; }
.badge-gender.female { background: #fdf2f8; color: #ec4899; }

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
  box-shadow: 0 20px 60px rgba(0,0,0,0.18);
  overflow: hidden;
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
.modal-form { padding: 20px 24px 24px; display: flex; flex-direction: column; gap: 14px; }

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

.modal-actions {
  display: flex; gap: 10px; padding: 0 24px 20px;
}

.btn-primary {
  padding: 9px 22px; background: #7c3aed;
  color: white; font-size: 13.5px; font-weight: 600;
  border: none; border-radius: 9px; cursor: pointer;
  transition: background 0.15s; font-family: inherit;
}
.btn-primary:hover { background: #6d28d9; }

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
</style>
