import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { attendanceService } from '../services/academic'

function normalizeStatus(value) {
  return String(value || '').toLowerCase().replace(/_/g, '')
}

function denormalizeStatus(value) {
  const map = {
    present: 'PRESENT',
    absent: 'ABSENT',
    late: 'LATE',
    leave: 'LEAVE',
    halfday: 'HALF_DAY',
  }
  return map[value] || value
}

function buildAttendanceRecord(source = {}) {
  return {
    id: source.id ?? null,
    studentId: source.studentId ?? null,
    studentName: source.studentName ?? null,
    rollNumber: source.rollNumber ?? null,
    date: source.date ?? source.attendanceDate ?? null,
    status: source.status ?? null,
  }
}

function normalizeAttendanceRow(raw = {}) {
  const attendanceSource = raw.attendance || (raw.status ? raw : null)
  return {
    ...raw,
    id: raw.studentId ?? raw.id ?? null,
    studentId: raw.studentId ?? raw.id ?? null,
    name: raw.studentName ?? raw.name ?? '',
    studentName: raw.studentName ?? raw.name ?? '',
    rollNo: raw.rollNumber ?? raw.rollNo ?? '',
    rollNumber: raw.rollNumber ?? raw.rollNo ?? '',
    department: raw.departmentName ?? raw.department ?? '',
    departmentName: raw.departmentName ?? raw.department ?? '',
    departmentId: raw.departmentId ?? null,
    course: raw.courseName ?? raw.course ?? '',
    courseName: raw.courseName ?? raw.course ?? '',
    courseId: raw.courseId ?? null,
    year: raw.year ?? null,
    semester: raw.semester ?? null,
    attendance: attendanceSource ? buildAttendanceRecord(attendanceSource) : null,
  }
}

function normalizeSession(raw = {}) {
  return {
    id: raw.id ?? null,
    departmentId: raw.departmentId ?? null,
    departmentName: raw.departmentName ?? '',
    courseId: raw.courseId ?? null,
    courseName: raw.courseName ?? '',
    subjectId: raw.subjectId ?? null,
    subjectName: raw.subjectName ?? '',
    sessionDate: raw.sessionDate ?? '',
    sessionName: raw.sessionName ?? '',
    subjectOrTopic: raw.subjectOrTopic ?? '',
    startTime: raw.startTime ?? '',
    endTime: raw.endTime ?? '',
    periodLabel: raw.periodLabel ?? '',
    remarks: raw.remarks ?? '',
    createdAt: raw.createdAt ?? null,
    updatedAt: raw.updatedAt ?? null,
  }
}

function normalizeSessionEntry(raw = {}) {
  return {
    id: raw.id ?? null,
    studentId: raw.studentId ?? null,
    studentName: raw.studentName ?? '',
    name: raw.studentName ?? '',
    rollNo: raw.rollNumber ?? '',
    rollNumber: raw.rollNumber ?? '',
    departmentId: raw.departmentId ?? null,
    department: raw.departmentName ?? '',
    departmentName: raw.departmentName ?? '',
    courseId: raw.courseId ?? null,
    course: raw.courseName ?? '',
    courseName: raw.courseName ?? '',
    year: raw.year ?? null,
    semester: raw.semester ?? null,
    status: raw.status ?? null,
    markedAt: raw.markedAt ?? null,
    updatedAt: raw.updatedAt ?? null,
  }
}

function compareSessions(a, b) {
  const dateCompare = String(b.sessionDate || '').localeCompare(String(a.sessionDate || ''))
  if (dateCompare !== 0) return dateCompare
  return String(b.updatedAt || b.createdAt || '').localeCompare(String(a.updatedAt || a.createdAt || ''))
}

export const useAttendanceStore = defineStore('attendance', () => {
  const rows = ref([])
  const records = ref([])
  const loading = ref(false)
  const error = ref('')

  const sessions = ref([])
  const sessionDetail = ref(null)
  const sessionLoading = ref(false)
  const sessionError = ref('')

  const sessionEntries = computed(() => sessionDetail.value?.entries || [])

  async function fetchStudents(filters) {
    loading.value = true
    error.value = ''
    try {
      const data = await attendanceService.getStudents(filters)
      rows.value = Array.isArray(data) ? data.map(normalizeAttendanceRow) : []
      return rows.value
    } catch (e) {
      error.value = e.message
      return []
    } finally {
      loading.value = false
    }
  }

  async function fetchAttendance(filters) {
    loading.value = true
    error.value = ''
    try {
      const data = await attendanceService.list(filters)
      records.value = Array.isArray(data) ? data.map(buildAttendanceRecord) : []
      if (rows.value.length) {
        const byStudentId = new Map(records.value.map((record) => [record.studentId, record]))
        rows.value = rows.value.map((row) => ({
          ...row,
          attendance: byStudentId.get(row.studentId) ?? null,
        }))
      }
      return records.value
    } catch (e) {
      error.value = e.message
      return []
    } finally {
      loading.value = false
    }
  }

  async function fetchByDate(date) {
    return fetchStudents({ date })
  }

  async function markAttendance({ studentId, date, status }) {
    const saved = await attendanceService.create({
      studentId,
      date,
      status: denormalizeStatus(status),
    })
    applyAttendanceToStore(studentId, buildAttendanceRecord(saved))
    return saved
  }

  async function updateAttendance(id, { studentId, date, status }) {
    const saved = await attendanceService.update(id, {
      studentId,
      date,
      status: denormalizeStatus(status),
    })
    applyAttendanceToStore(studentId, buildAttendanceRecord(saved))
    return saved
  }

  async function bulkMark(recordsPayload) {
    const saved = await attendanceService.bulk({
      records: recordsPayload.map((item) => ({
        studentId: item.studentId,
        date: item.date,
        status: denormalizeStatus(item.status),
      })),
    })
    if (Array.isArray(saved)) {
      saved.forEach((record) => {
        applyAttendanceToStore(record.studentId, buildAttendanceRecord(record))
      })
    }
    return saved
  }

  async function clearAttendance({ studentIds, date }) {
    const result = await attendanceService.clear({
      studentIds,
      date,
    })
    if (Array.isArray(studentIds) && studentIds.length) {
      const studentIdSet = new Set(studentIds)
      records.value = records.value.filter((record) =>
        !(studentIdSet.has(record.studentId) && record.date === date)
      )
      rows.value.forEach((row) => {
        if (studentIdSet.has(row.studentId)) {
          row.attendance = null
        }
      })
    }
    return result
  }

  async function fetchSessions(filters = {}) {
    sessionLoading.value = true
    sessionError.value = ''
    try {
      const data = await attendanceService.listSessions(filters)
      sessions.value = Array.isArray(data) ? data.map(normalizeSession).sort(compareSessions) : []
      return sessions.value
    } catch (e) {
      sessionError.value = e.message
      return []
    } finally {
      sessionLoading.value = false
    }
  }

  async function fetchSessionsByRange(filters = {}) {
    sessionLoading.value = true
    sessionError.value = ''
    try {
      const data = await attendanceService.listSessionsByRange(filters)
      sessions.value = Array.isArray(data) ? data.map(normalizeSession).sort(compareSessions) : []
      return sessions.value
    } catch (e) {
      sessionError.value = e.message
      return []
    } finally {
      sessionLoading.value = false
    }
  }

  async function createSession(payload) {
    sessionError.value = ''
    const created = normalizeSession(await attendanceService.createSession(payload))
    upsertSession(created)
    return created
  }

  async function getSessionDetail(id) {
    sessionLoading.value = true
    sessionError.value = ''
    try {
      const detail = await attendanceService.getSession(id)
      sessionDetail.value = normalizeSessionDetail(detail)
      upsertSession(sessionDetail.value)
      return sessionDetail.value
    } catch (e) {
      sessionDetail.value = null
      sessionError.value = e.message
      return null
    } finally {
      sessionLoading.value = false
    }
  }

  async function updateSession(id, payload) {
    sessionError.value = ''
    const updated = normalizeSession(await attendanceService.updateSession(id, payload))
    upsertSession(updated)
    if (sessionDetail.value?.id === id) {
      sessionDetail.value = {
        ...sessionDetail.value,
        ...updated,
      }
    }
    return updated
  }

  async function saveSessionEntry(sessionId, { entryId, studentId, status }) {
    const normalized = denormalizeStatus(status)
    if (entryId) {
      const saved = normalizeSessionEntry(
        await attendanceService.updateSessionEntry(sessionId, entryId, { status: normalized })
      )
      patchSessionEntry(saved)
      return saved
    }
    const detail = normalizeSessionDetail(
      await attendanceService.bulkUpsertSessionEntries(sessionId, {
        entries: [{ studentId, status: normalized }],
      })
    )
    sessionDetail.value = detail
    upsertSession(detail)
    return detail.entries.find((entry) => entry.studentId === studentId) || null
  }

  async function bulkUpsertSessionEntries(sessionId, entries) {
    const detail = normalizeSessionDetail(
      await attendanceService.bulkUpsertSessionEntries(sessionId, {
        entries: entries.map((entry) => ({
          studentId: entry.studentId,
          status: denormalizeStatus(entry.status),
        })),
      })
    )
    sessionDetail.value = detail
    upsertSession(detail)
    return detail
  }

  async function deleteSession(id) {
    await attendanceService.deleteSession(id)
    sessions.value = sessions.value.filter((session) => session.id !== id)
    if (sessionDetail.value?.id === id) {
      sessionDetail.value = null
    }
  }

  function normalizeSessionDetail(raw = {}) {
    return {
      ...normalizeSession(raw),
      entries: Array.isArray(raw.entries) ? raw.entries.map(normalizeSessionEntry) : [],
    }
  }

  function patchSessionEntry(entry) {
    if (!sessionDetail.value) return
    sessionDetail.value = {
      ...sessionDetail.value,
      entries: sessionDetail.value.entries.map((current) =>
        current.studentId === entry.studentId ? { ...current, ...entry } : current
      ),
    }
  }

  function upsertSession(session) {
    const normalized = normalizeSession(session)
    const index = sessions.value.findIndex((item) => item.id === normalized.id)
    if (index === -1) {
      sessions.value = [normalized, ...sessions.value].sort(compareSessions)
    } else {
      const next = [...sessions.value]
      next[index] = {
        ...next[index],
        ...normalized,
      }
      sessions.value = next.sort(compareSessions)
    }
  }

  function getStatusForRow(row) {
    return normalizeStatus(row.attendance?.status ?? row.status)
  }

  function applyAttendanceToStore(studentId, attendance) {
    upsertRecord(attendance)

    rows.value.forEach((row) => {
      if (row.studentId === studentId) {
        row.attendance = attendance
      }
    })
  }

  function upsertRecord(attendance) {
    if (!attendance?.studentId) return
    const index = records.value.findIndex(
      (record) =>
        record.studentId === attendance.studentId &&
        String(record.date || '') === String(attendance.date || '')
    )

    if (index >= 0) {
      records.value[index] = {
        ...records.value[index],
        ...attendance,
      }
      return
    }

    records.value = [...records.value, attendance]
  }

  function getDateSummary(date, students = []) {
    const sourceRows = rows.value?.length
      ? rows.value
      : (students || []).map((student) => ({
          studentId: student.id,
          attendance: null,
        }))

    const summary = {
      total: sourceRows.length,
      present: 0,
      absent: 0,
      late: 0,
      leave: 0,
      halfday: 0,
      unmarked: 0,
    }

    sourceRows.forEach((row) => {
      const status = getStatusForRow(row)
      if (status === 'present') summary.present++
      else if (status === 'absent') summary.absent++
      else if (status === 'late') summary.late++
      else if (status === 'leave') summary.leave++
      else if (status === 'halfday') summary.halfday++
      else summary.unmarked++
    })

    return summary
  }

  function clearSessionDetail() {
    sessionDetail.value = null
    sessionError.value = ''
  }

  return {
    rows,
    records,
    loading,
    error,
    sessions,
    sessionDetail,
    sessionEntries,
    sessionLoading,
    sessionError,
    fetchStudents,
    fetchAttendance,
    fetchByDate,
    markAttendance,
    updateAttendance,
    bulkMark,
    clearAttendance,
    fetchSessions,
    fetchSessionsByRange,
    createSession,
    getSessionDetail,
    updateSession,
    saveSessionEntry,
    bulkUpsertSessionEntries,
    deleteSession,
    clearSessionDetail,
    getStatusForRow,
    getDateSummary,
    normalizeStatus,
    denormalizeStatus,
    normalizeAttendanceRow,
    normalizeSession,
    normalizeSessionEntry,
  }
})
