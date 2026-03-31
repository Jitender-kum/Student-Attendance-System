import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const STORAGE_KEY = 'attendease_attendance'

function loadFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : {}
  } catch {
    return {}
  }
}

export const useAttendanceStore = defineStore('attendance', () => {
  // attendance[date][studentId] = 'present' | 'absent' | 'late' | null
  const attendance = ref(loadFromStorage())

  // Auto-save to localStorage on every change
  watch(attendance, (val) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(val))
  }, { deep: true })

  /* ─── single record ─────────────────────────────── */
  function getRecord(date, studentId) {
    return attendance.value[date]?.[studentId] ?? null
  }

  /* ─── mark one student ──────────────────────────── */
  function markAttendance(date, studentId, status) {
    if (!attendance.value[date]) attendance.value[date] = {}
    if (status === null) {
      delete attendance.value[date][studentId]
    } else {
      attendance.value[date][studentId] = status
    }
    // trigger reactivity
    attendance.value = { ...attendance.value }
  }

  /* ─── bulk mark ─────────────────────────────────── */
  function markAll(date, studentIds, status) {
    if (!attendance.value[date]) attendance.value[date] = {}
    studentIds.forEach(id => {
      if (status === null) delete attendance.value[date][id]
      else attendance.value[date][id] = status
    })
    attendance.value = { ...attendance.value }
  }

  /* ─── day summary ───────────────────────────────── */
  function getDateSummary(date, students) {
    const record = attendance.value[date] || {}
    let present = 0, absent = 0, late = 0, unmarked = 0
    students.forEach(s => {
      const v = record[s.id]
      if (v === 'present') present++
      else if (v === 'absent') absent++
      else if (v === 'late') late++
      else unmarked++
    })
    return { present, absent, late, unmarked, total: students.length }
  }

  /* ─── monthly report for one student ────────────── */
  // Returns array of { date: 'YYYY-MM-DD', status } for every calendar day in given month
  function getMonthlyReport(studentId, year, month) {
    // month is 1-indexed (1 = January)
    const daysInMonth = new Date(year, month, 0).getDate()
    const result = []
    for (let d = 1; d <= daysInMonth; d++) {
      const date = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
      result.push({ date, day: d, status: attendance.value[date]?.[studentId] ?? null })
    }
    return result
  }

  /* ─── monthly stats per student ─────────────────── */
  function getMonthlyStats(studentId, year, month) {
    const days = getMonthlyReport(studentId, year, month)
    // only count school days (Mon–Sat, exclude Sundays)
    const schoolDays = days.filter(d => {
      const dow = new Date(d.date).getDay() // 0=Sun
      return dow !== 0
    })
    const marked = schoolDays.filter(d => d.status !== null)
    const present = marked.filter(d => d.status === 'present').length
    const absent  = marked.filter(d => d.status === 'absent').length
    const late    = marked.filter(d => d.status === 'late').length
    const pct = marked.length ? Math.round(((present + late) / marked.length) * 100) : null
    return { present, absent, late, marked: marked.length, schoolDays: schoolDays.length, pct }
  }

  /* ─── all dates that have records ───────────────── */
  const markedDates = () => Object.keys(attendance.value).sort()

  /* ─── all-time totals for a single student ───────── */
  function getStudentTotals(studentId) {
    let present = 0, absent = 0, late = 0
    for (const date of Object.keys(attendance.value)) {
      const st = attendance.value[date]?.[studentId]
      if (st === 'present') present++
      else if (st === 'absent') absent++
      else if (st === 'late') late++
    }
    const total    = present + absent + late
    const attended = present + late          // present or late = "attended"
    const pct      = total ? Math.round((attended / total) * 100) : null
    return { present, absent, late, total, attended, pct }
  }

  /* ─── all-time totals for every student ──────────── */
  function getAllStudentTotals(students) {
    return students.map(s => ({ student: s, ...getStudentTotals(s.id) }))
  }

  return {
    attendance,
    getRecord,
    markAttendance,
    markAll,
    getDateSummary,
    getMonthlyReport,
    getMonthlyStats,
    markedDates,
    getStudentTotals,
    getAllStudentTotals,
  }
})
