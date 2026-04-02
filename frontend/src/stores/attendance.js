import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { API } from '../config/api'

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
  // attendance[date][studentId] = 'present' | 'absent' | 'late' | 'leave' | 'halfday' | null
  const attendance = ref(loadFromStorage())
  // attendanceMeta[date][studentId] = { id, createdBy }
  const attendanceMeta = ref({})
  const loading = ref(false)
  const error = ref(null)

  async function fetchByDate(date) {
    loading.value = true
    error.value = null
    try {
      const res = await fetch(API.attendance.getByDate(date))
      if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`)
      const data = await res.json()
      
      // Update local storage/state with fetched data
      if (!attendance.value[date]) attendance.value[date] = {}
      if (!attendanceMeta.value[date]) attendanceMeta.value[date] = {}
      
      const studentsList = []
      data.forEach(item => {
        // Map convenience fields (copied logic from studentStore)
        const parts = (item.name || '').trim().split(' ')
        const mapped = {
          ...item,
          firstName:    parts[0]               || '',
          lastName:     parts.slice(1).join(' ')|| '',
          roll:         item.rollNo            || '',
          phone:        item.phoneNumber       || '',
          classSection: item.department
            ? `${item.department}${item.year ? ' Y' + item.year : ''}`
            : (item.course || ''),
          gender:       item.gender             || '',
        }
        studentsList.push(mapped)

        if (item.studentAttendance && item.studentAttendance.attendanceStatus) {
          const status = item.studentAttendance.attendanceStatus.toLowerCase().replace('_', '')
          // Backend might have HALF_DAY, frontend uses halfday
          const uiStatus = status === 'halfday' ? 'halfday' : status
          
          attendance.value[date][item.id] = uiStatus
          attendanceMeta.value[date][item.id] = {
            id: item.studentAttendance.id,
            createdBy: item.studentAttendance.createdBy || 'admin'
          }
        } else {
          // If null, it means attendance not yet done for this student
          attendance.value[date][item.id] = null
          attendanceMeta.value[date][item.id] = null
        }
      })
      
      // Trigger reactivity
      attendance.value = { ...attendance.value }
      attendanceMeta.value = { ...attendanceMeta.value }
      return studentsList
    } catch (e) {
      error.value = e.message
      console.error('[AttendanceStore] fetchByDate failed:', e)
    } finally {
      loading.value = false
    }
  }

  // Auto-save to localStorage on every change
  watch(attendance, (val) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(val))
  }, { deep: true })

  /* ─── single record ─────────────────────────────── */
  function getRecord(date, studentId) {
    return attendance.value[date]?.[studentId] ?? null
  }

  /* ─── mark one student (sync with API) ──────────── */
  async function markAttendance(date, studentId, status) {
    const statusMap = {
      present: 'PRESENT',
      absent:  'ABSENT',
      leave:   'LEAVE',
      halfday: 'HALF_DAY',
      late:    'LATE'
    }

    // Always update local state first for instant feedback (optimistic)
    if (!attendance.value[date]) attendance.value[date] = {}
    if (!attendanceMeta.value[date]) attendanceMeta.value[date] = {}

    const oldStatus = attendance.value[date][studentId]
    const oldMeta   = attendanceMeta.value[date][studentId]
    
    if (status === null) {
      delete attendance.value[date][studentId]
      delete attendanceMeta.value[date][studentId]
    } else {
      attendance.value[date][studentId] = status
    }
    attendance.value = { ...attendance.value }

    // Send to backend
    try {
      const payload = {
        studentId: Number(studentId),
        attendanceDate: date,
        attendanceStatus: status ? statusMap[status] : 'ABSENT',
        createdBy: oldMeta?.createdBy || 'admin'
      }
      
      // If we have an existing attendance ID, include it so backend updates instead of creates
      if (oldMeta?.id) {
        payload.id = oldMeta.id
      }

      const res = await fetch(API.attendance.mark, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`)
      
      // Re-fetch everything and return the students list for caller use
      return await fetchByDate(date)
    } catch (e) {
      console.error('[AttendanceStore] markAttendance failed:', e)
      // Rollback on error
      attendance.value[date][studentId] = oldStatus
      attendanceMeta.value[date][studentId] = oldMeta
      attendance.value = { ...attendance.value }
      // Throw or return error object. To maintain the success/error pattern:
      return { _error: e.message }
    }
  }

  /* ─── bulk mark ─────────────────────────────────── */
  async function markAll(date, studentIds, status) {
    try {
      // Perform all marks in parallel
      await Promise.all(studentIds.map(id => markAttendance(date, id, status)))
      // The markAttendance calls already fetchByDate internally, 
      // but to be absolutely sure we have the latest and to return it:
      return await fetchByDate(date)
    } catch (e) {
      console.error('[AttendanceStore] markAll failed:', e)
      return { _error: e.message }
    }
  }

  /* ─── day summary ───────────────────────────────── */
  function getDateSummary(date, students) {
    const record = attendance.value[date] || {}
    let present = 0, absent = 0, late = 0, leave = 0, halfday = 0, unmarked = 0
    students.forEach(s => {
      const v = record[s.id]
      if (v === 'present') present++
      else if (v === 'absent') absent++
      else if (v === 'late') late++
      else if (v === 'leave') leave++
      else if (v === 'halfday') halfday++
      else unmarked++
    })
    return { present, absent, late, leave, halfday, unmarked, total: students.length }
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
    loading,
    error,
    getRecord,
    markAttendance,
    markAll,
    fetchByDate,
    getDateSummary,
    getMonthlyReport,
    getMonthlyStats,
    markedDates,
    getStudentTotals,
    getAllStudentTotals,
  }
})
