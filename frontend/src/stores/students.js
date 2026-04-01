import { defineStore } from 'pinia'
import { ref } from 'vue'
import { API } from '../config/api'

/**
 * Backend API fields (stored as-is, no mapping):
 *  id, name, rollNo, department, course, year,
 *  phoneNumber, email, address,
 *  fatherName, fatherPhone, fatherOccupation,
 *  motherName, motherPhone,
 *  attendancePercentage, status,
 *  createdOn, updatedOn, createdBy, updatedBy
 *
 * Convenience getters used in templates:
 *  s.firstName  →  first word of s.name
 *  s.lastName   →  rest of s.name
 *  s.roll       →  s.rollNo
 *  s.phone      →  s.phoneNumber
 *  s.classSection → s.department + ' Y' + s.year
 */
function addConvenienceFields(raw) {
  const parts     = (raw.name || '').trim().split(' ')
  return {
    ...raw,
    // shorthand aliases so existing templates keep working
    firstName:    parts[0]               || '',
    lastName:     parts.slice(1).join(' ')|| '',
    roll:         raw.rollNo             || '',
    phone:        raw.phoneNumber        || '',
    classSection: raw.department
      ? `${raw.department}${raw.year ? ' Y' + raw.year : ''}`
      : (raw.course || ''),
    gender:       raw.gender             || '',
  }
}

export const useStudentStore = defineStore('students', () => {
  const students = ref([])
  const loading  = ref(false)
  const error    = ref(null)

  /* ── Fetch list from backend ─────────────────────── */
  async function fetchStudents() {
    loading.value = true
    error.value   = null
    try {
      const res = await fetch(API.student.list)
      if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`)
      const data = await res.json()
      students.value = Array.isArray(data) ? data.map(addConvenienceFields) : []
    } catch (e) {
      error.value = e.message
      console.error('[StudentStore] fetchStudents failed:', e)
    } finally {
      loading.value = false
    }
  }

  /* ── POST full Student entity to /student/add ─────── */
  async function addStudent(studentData) {
    try {
      const res = await fetch(API.student.add, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(studentData),
      })
      if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`)
      const saved = await res.json()
      // Push the returned (persisted) entity into the list
      students.value.push(addConvenienceFields(saved))
      return { success: true }
    } catch (e) {
      console.error('[StudentStore] addStudent failed:', e)
      return { success: false, error: e.message }
    }
  }

  /* ── POST full Student entity to /student/update ──── */
  /* id is included inside the entity body — backend expects the whole object */
  async function updateStudent(id, data) {
    try {
      // Merge with existing record so no field is accidentally lost
      const existing = students.value.find(s => s.id === id) || {}
      const payload  = { ...existing, ...data, id }

      const res = await fetch(API.student.update, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })
      if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`)
      const saved = await res.json()
      // Replace the record in the local list with what the server returned
      const idx = students.value.findIndex(s => s.id === id)
      if (idx !== -1) students.value[idx] = addConvenienceFields(saved)
      return { success: true }
    } catch (e) {
      console.error('[StudentStore] updateStudent failed:', e)
      return { success: false, error: e.message }
    }
  }

  function deleteStudent(id) {
    students.value = students.value.filter(s => s.id !== id)
  }

  return { students, loading, error, fetchStudents, addStudent, updateStudent, deleteStudent }
})
