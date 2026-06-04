import { defineStore } from 'pinia'
import { ref } from 'vue'
import { studentService } from '../services/academic'

function normalizeStudent(raw) {
  const parts = (raw.name || '').trim().split(' ')
  return {
    ...raw,
    rollNo: raw.rollNumber,
    department: raw.departmentName,
    course: raw.courseName,
    year: raw.year,
    semester: raw.semester,
    phoneNumber: raw.phone,
    firstName: parts[0] || '',
    lastName: parts.slice(1).join(' ') || '',
    roll: raw.rollNumber || '',
    phone: raw.phone || '',
    classSection: raw.departmentName
      ? `${raw.departmentName}${raw.year ? ` · Year ${raw.year}` : ''}${raw.semester ? ` · Sem ${raw.semester}` : ''}`
      : (raw.courseName || ''),
    gender: raw.gender || '',
  }
}

export const useStudentStore = defineStore('students', () => {
  const students = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchStudents(filters = {}) {
    loading.value = true
    error.value = ''
    try {
      const data = await studentService.list(filters)
      students.value = Array.isArray(data) ? data.map(normalizeStudent) : []
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function addStudent(studentData) {
    const saved = await studentService.create(studentData)
    students.value.unshift(normalizeStudent(saved))
    return saved
  }

  async function updateStudent(id, data) {
    const saved = await studentService.update(id, data)
    const idx = students.value.findIndex((student) => student.id === id)
    if (idx !== -1) {
      students.value[idx] = normalizeStudent(saved)
    }
    return saved
  }

  async function deleteStudent(id) {
    await studentService.delete(id)
    students.value = students.value.filter((student) => student.id !== id)
  }

  function setStudents(list) {
    students.value = Array.isArray(list) ? list.map(normalizeStudent) : []
  }

  return {
    students,
    loading,
    error,
    fetchStudents,
    addStudent,
    updateStudent,
    deleteStudent,
    setStudents,
    normalizeStudent,
  }
})
