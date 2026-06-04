<template>
  <div class="attendance-page">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 11l3 3L22 4"/>
            <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
          </svg>
        </div>
        <div>
          <h1 class="page-title">Attendance Sessions</h1>
          <p class="page-subtitle">Create, browse, and edit session-based attendance records</p>
        </div>
      </div>
    </div>

    <div class="setup-card">
      <div class="setup-grid">
        <div class="control-group">
          <label class="control-label">Date</label>
          <input v-model="selectedDate" type="date" class="field-input" />
        </div>
        <div class="control-group">
          <label class="control-label">Department</label>
          <select v-model="selectedDepartment" class="field-input">
            <option value="">All Departments</option>
            <option v-for="department in departmentStore.departments" :key="department.id" :value="String(department.id)">{{ department.name }}</option>
          </select>
        </div>
        <div class="control-group">
          <label class="control-label">Course</label>
          <select v-model="selectedCourse" class="field-input">
            <option value="">All Courses</option>
            <option v-for="course in courseStore.courses" :key="course.id" :value="String(course.id)">{{ course.name }}</option>
          </select>
        </div>
        <div class="control-group">
          <label class="control-label">Subject</label>
          <select v-model="selectedSubject" class="field-input">
            <option value="">All Subjects</option>
            <option v-for="subject in availableSubjects" :key="subject.id" :value="String(subject.id)">{{ subject.name }}</option>
          </select>
        </div>
        <div class="control-group">
          <label class="control-label">Start Time</label>
          <input v-model="sessionForm.startTime" type="time" class="field-input" />
        </div>
        <div class="control-group">
          <label class="control-label">End Time</label>
          <input v-model="sessionForm.endTime" type="time" class="field-input" />
        </div>
        <div class="control-group">
          <label class="control-label">Period Label</label>
          <input v-model="sessionForm.periodLabel" type="text" class="field-input" placeholder="Optional period / slot" />
        </div>
        <div class="control-group">
          <label class="control-label">Session Name</label>
          <input v-model="sessionForm.sessionName" type="text" class="field-input" placeholder="Session 1 / Lab / Tutorial" />
        </div>
        <div class="control-group">
          <label class="control-label">Subject / Topic</label>
          <input v-model="sessionForm.subjectOrTopic" type="text" class="field-input" placeholder="Optional topic" />
        </div>
        <div class="control-group control-group-wide">
          <label class="control-label">Remarks</label>
          <input v-model="sessionForm.remarks" type="text" class="field-input" placeholder="Optional remarks" />
        </div>
      </div>

      <div class="setup-actions">
        <button class="primary-btn" @click="createSession">Create Session</button>
        <button class="secondary-btn" :disabled="!hasActiveSession" @click="updateSessionMeta">Update Selected</button>
        <button class="secondary-btn" :disabled="!hasActiveSession" @click="removeSession">Delete Selected</button>
        <button class="ghost-btn" @click="resetSessionForm">Reset Form</button>
      </div>
      <div v-if="sessionFormError" class="state-banner error-banner session-form-banner">{{ sessionFormError }}</div>
    </div>

    <div class="history-card">
      <div class="history-toolbar">
        <div>
          <h2 class="section-title">Session History</h2>
          <p class="section-subtitle">Browse by selected date or switch to a date range</p>
        </div>
        <div class="history-filters">
          <select v-model="historySubjectId" class="field-input">
            <option value="">All Subjects</option>
            <option v-for="subject in availableSubjects" :key="subject.id" :value="String(subject.id)">{{ subject.name }}</option>
          </select>
          <input v-model="historySessionName" type="text" class="field-input search-input" placeholder="Filter by session name" />
          <input v-model="historyStartDate" type="date" class="field-input" />
          <input v-model="historyEndDate" type="date" class="field-input" />
          <button class="secondary-btn" @click="refreshHistory">Refresh</button>
        </div>
      </div>

      <div v-if="attStore.sessionError" class="state-banner error-banner">{{ attStore.sessionError }}</div>

      <div class="history-list">
        <button v-for="session in attStore.sessions" :key="session.id" class="history-item" :class="{ active: activeSessionId === session.id }" @click="openSession(session.id)">
          <div>
            <div class="history-name">{{ session.sessionName }}</div>
            <div class="history-meta">
              {{ session.sessionDate }}
              <span v-if="session.departmentName"> · {{ session.departmentName }}</span>
              <span v-if="session.courseName"> · {{ session.courseName }}</span>
              <span v-if="session.subjectName"> · {{ session.subjectName }}</span>
              <span> · {{ formatSessionSlot(session) }}</span>
            </div>
            <div v-if="session.subjectOrTopic" class="history-topic">{{ session.subjectOrTopic }}</div>
          </div>
          <span class="history-open">Open</span>
        </button>
        <div v-if="!attStore.sessionLoading && attStore.sessions.length === 0" class="empty-history">No sessions found for the current filters.</div>
      </div>
    </div>

    <template v-if="hasActiveSession">
      <div class="session-meta-bar">
        <div>
          <h2 class="section-title">{{ activeSession.sessionName }}</h2>
          <p class="section-subtitle">
            {{ activeSession.sessionDate }}
            <span v-if="activeSession.departmentName"> · {{ activeSession.departmentName }}</span>
            <span v-if="activeSession.courseName"> · {{ activeSession.courseName }}</span>
            <span v-if="activeSession.subjectName"> · {{ activeSession.subjectName }}</span>
            <span> · {{ formatSessionSlot(activeSession) }}</span>
            <span v-if="activeSession.subjectOrTopic"> · {{ activeSession.subjectOrTopic }}</span>
          </p>
        </div>
        <input v-model="studentSearch" type="text" class="field-input search-input" placeholder="Search student…" />
      </div>

      <div class="summary-row">
        <div class="summary-card total"><div class="sc-icon">#</div><div><div class="sc-value">{{ summary.total }}</div><div class="sc-label">Total Students</div></div></div>
        <div class="summary-card present"><div class="sc-icon">P</div><div><div class="sc-value">{{ summary.present }}</div><div class="sc-label">Present</div></div></div>
        <div class="summary-card absent"><div class="sc-icon">A</div><div><div class="sc-value">{{ summary.absent }}</div><div class="sc-label">Absent</div></div></div>
        <div class="summary-card late"><div class="sc-icon">L</div><div><div class="sc-value">{{ summary.late }}</div><div class="sc-label">Late</div></div></div>
        <div class="summary-card leave"><div class="sc-icon">Lv</div><div><div class="sc-value">{{ summary.leave }}</div><div class="sc-label">Leave</div></div></div>
        <div class="summary-card halfday"><div class="sc-icon">H</div><div><div class="sc-value">{{ summary.halfday }}</div><div class="sc-label">Half Day</div></div></div>
        <div class="summary-card unmarked"><div class="sc-icon">?</div><div><div class="sc-value">{{ summary.unmarked }}</div><div class="sc-label">Unmarked</div></div></div>
      </div>

      <div class="progress-bar-wrap">
        <div class="progress-bar-track">
          <div class="bar-present" :style="{ width: pctPresent + '%' }"></div>
          <div class="bar-late" :style="{ width: pctLate + '%' }"></div>
          <div class="bar-leave" :style="{ width: pctLeave + '%' }"></div>
          <div class="bar-halfday" :style="{ width: pctHalfDay + '%' }"></div>
          <div class="bar-absent" :style="{ width: pctAbsent + '%' }"></div>
        </div>
        <div class="progress-legend">
          <span class="leg leg-present" :class="{ active: pctPresent > 0 }">Present {{ pctPresent.toFixed(0) }}%</span>
          <span class="leg leg-late" :class="{ active: pctLate > 0 }">Late {{ pctLate.toFixed(0) }}%</span>
          <span class="leg leg-leave" :class="{ active: pctLeave > 0 }">Leave {{ pctLeave.toFixed(0) }}%</span>
          <span class="leg leg-halfday" :class="{ active: pctHalfDay > 0 }">Half {{ pctHalfDay.toFixed(0) }}%</span>
          <span class="leg leg-absent" :class="{ active: pctAbsent > 0 }">Absent {{ pctAbsent.toFixed(0) }}%</span>
        </div>
      </div>

      <div class="bulk-bar">
        <span class="bulk-title">Bulk Mark:</span>
        <button class="bulk-btn bulk-present" @click="bulkMark('present')">All Present</button>
        <button class="bulk-btn bulk-absent" @click="bulkMark('absent')">All Absent</button>
        <button class="bulk-btn bulk-leave" @click="bulkMark('leave')">All Leave</button>
        <button class="bulk-btn bulk-late" @click="bulkMark('late')">All Late</button>
        <button class="bulk-btn bulk-halfday" @click="bulkMark('halfday')">All Half</button>
      </div>

      <div class="table-card">
        <div class="table-wrap">
          <table class="att-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Roll No.</th>
                <th>Student Name</th>
                <th>Department</th>
                <th>Course</th>
                <th>Attendance Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="attStore.sessionLoading">
                <td colspan="6" class="empty-row">Loading session…</td>
              </tr>
              <tr v-else-if="visibleEntries.length === 0">
                <td colspan="6" class="empty-row">No students found for this session.</td>
              </tr>
              <tr v-for="(student, idx) in visibleEntries" :key="student.studentId" class="student-row" :class="getStatus(student)">
                <td class="td-no">{{ idx + 1 }}</td>
                <td><span class="roll-badge">{{ student.rollNo }}</span></td>
                <td>
                  <div class="student-info">
                    <div class="student-avatar" :class="'avatar-' + (student.studentId % 6)">
                      {{ initials(student.studentName) }}
                    </div>
                    <div>
                      <div class="student-name">{{ student.studentName }}</div>
                      <div class="student-email">Year {{ student.year || '—' }} / Sem {{ student.semester || '—' }}</div>
                    </div>
                  </div>
                </td>
                <td><span class="class-badge">{{ student.departmentName || '—' }}</span></td>
                <td><span class="course-chip">{{ student.courseName || '—' }}</span></td>
                <td>
                  <div class="status-buttons">
                    <button class="status-btn present-btn" :class="{ active: getStatus(student) === 'present' }" @click="mark(student, 'present')">Pres</button>
                    <button class="status-btn absent-btn" :class="{ active: getStatus(student) === 'absent' }" @click="mark(student, 'absent')">Abs</button>
                    <button class="status-btn leave-btn" :class="{ active: getStatus(student) === 'leave' }" @click="mark(student, 'leave')">Leave</button>
                    <button class="status-btn halfday-btn" :class="{ active: getStatus(student) === 'halfday' }" @click="mark(student, 'halfday')">Half</button>
                    <button class="status-btn late-btn" :class="{ active: getStatus(student) === 'late' }" @click="mark(student, 'late')">Late</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <div v-else class="empty-session-card">
      <h2 class="section-title">Select or create a session</h2>
      <p class="section-subtitle">Create a new session above or open one from the session history to mark attendance and review previous session records.</p>
    </div>

    <transition name="toast-fade">
      <div v-if="toastVisible" class="toast" :class="'toast-' + toastType">{{ toastMsg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAttendanceStore } from '../../stores/attendance'
import { useDepartmentStore } from '../../stores/departments'
import { useCourseStore } from '../../stores/courses'
import { useSubjectStore } from '../../stores/subjects'

const route = useRoute()
const attStore = useAttendanceStore()
const departmentStore = useDepartmentStore()
const courseStore = useCourseStore()
const subjectStore = useSubjectStore()

const today = new Date().toISOString().split('T')[0]
const defaultHistoryStart = new Date()
defaultHistoryStart.setDate(defaultHistoryStart.getDate() - 29)

const selectedDate = ref(today)
const selectedDepartment = ref('')
const selectedCourse = ref('')
const selectedSubject = ref('')
const historySessionName = ref('')
const historyStartDate = ref(formatDate(defaultHistoryStart))
const historyEndDate = ref(today)
const historySubjectId = ref('')
const studentSearch = ref('')
const isHydratingSession = ref(false)
const sessionFormError = ref('')
const historyQuery = reactive({
  date: today,
  departmentId: '',
  courseId: '',
  subjectId: '',
})

const sessionForm = reactive({
  sessionName: '',
  subjectOrTopic: '',
  startTime: '',
  endTime: '',
  periodLabel: '',
  remarks: '',
})

const activeSession = computed(() => attStore.sessionDetail)
const activeSessionId = computed(() => activeSession.value?.id ?? null)
const hasActiveSession = computed(() => !!activeSession.value?.id)
const availableSubjects = computed(() => subjectStore.subjects)

const visibleEntries = computed(() => {
  const query = studentSearch.value.trim().toLowerCase()
  const entries = attStore.sessionEntries
  if (!query) return entries
  return entries.filter((entry) =>
    entry.studentName.toLowerCase().includes(query) ||
    entry.rollNo.toLowerCase().includes(query) ||
    entry.departmentName.toLowerCase().includes(query) ||
    entry.courseName.toLowerCase().includes(query)
  )
})

const summary = computed(() => {
  const result = { total: visibleEntries.value.length, present: 0, absent: 0, late: 0, leave: 0, halfday: 0, unmarked: 0 }
  visibleEntries.value.forEach((student) => {
    const status = getStatus(student)
    if (status === 'present') result.present++
    else if (status === 'absent') result.absent++
    else if (status === 'late') result.late++
    else if (status === 'leave') result.leave++
    else if (status === 'halfday') result.halfday++
    else result.unmarked++
  })
  return result
})

function percentage(value) {
  return summary.value.total > 0 ? (value / summary.value.total) * 100 : 0
}

const pctPresent = computed(() => percentage(summary.value.present))
const pctAbsent = computed(() => percentage(summary.value.absent))
const pctLate = computed(() => percentage(summary.value.late))
const pctLeave = computed(() => percentage(summary.value.leave))
const pctHalfDay = computed(() => percentage(summary.value.halfday))

onMounted(async () => {
  await Promise.all([
    departmentStore.fetchDepartments(),
    courseStore.fetchCourses(),
  ])
  await applyRouteSelection(route.query)
  await loadSubjects()
  await loadSessions()
})

watch(selectedDepartment, async (value) => {
  if (isHydratingSession.value) return
  selectedCourse.value = ''
  selectedSubject.value = ''
  historySubjectId.value = ''
  await courseStore.fetchCourses(value ? { departmentId: value } : {})
  await loadSubjects()
})

watch(selectedCourse, async () => {
  if (isHydratingSession.value) return
  selectedSubject.value = ''
  historySubjectId.value = ''
  await loadSubjects()
})

watch([selectedDate, selectedDepartment, selectedCourse, historySessionName, historyStartDate, historyEndDate, historySubjectId], async () => {
  if (isHydratingSession.value) return
  await loadSessions()
})

watch(() => [route.query.departmentId, route.query.courseId, route.query.subjectId], async () => {
  await applyRouteSelection(route.query)
  await loadSessions()
})

async function loadSessions() {
  if (historyStartDate.value && historyEndDate.value) {
    await attStore.fetchSessionsByRange({
      startDate: historyStartDate.value,
      endDate: historyEndDate.value,
      departmentId: selectedDepartment.value || undefined,
      courseId: selectedCourse.value || undefined,
      subjectId: historySubjectId.value || undefined,
      sessionName: historySessionName.value || undefined,
    })
    return
  }

  await attStore.fetchSessions({
    date: historyQuery.date || undefined,
    departmentId: historyQuery.departmentId || undefined,
    courseId: historyQuery.courseId || undefined,
    subjectId: historyQuery.subjectId || undefined,
    sessionName: historySessionName.value || undefined,
  })
}

async function refreshHistory() {
  if (!historyStartDate.value || !historyEndDate.value) {
    historyQuery.date = selectedDate.value
    historyQuery.departmentId = selectedDepartment.value
    historyQuery.courseId = selectedCourse.value
    historyQuery.subjectId = historySubjectId.value
  }
  await loadSessions()
}

async function createSession() {
  try {
    const created = await attStore.createSession(buildSessionPayload())
    await refreshHistory()
    await openSession(created.id)
    showToast('Session created', 'success')
  } catch (error) {
    showToast(error.message || 'Failed to create session', 'error')
  }
}

async function openSession(sessionId) {
  const detail = await attStore.getSessionDetail(sessionId)
  if (detail) {
    await syncFormFromSession(detail)
    sessionFormError.value = ''
  } else {
    showToast(attStore.sessionError || 'Failed to load session', 'error')
  }
}

async function updateSessionMeta() {
  if (!hasActiveSession.value) return
  try {
    await attStore.updateSession(activeSession.value.id, buildSessionPayload())
    await openSession(activeSession.value.id)
    await refreshHistory()
    showToast('Session updated', 'success')
  } catch (error) {
    showToast(error.message || 'Failed to update session', 'error')
  }
}

async function removeSession() {
  if (!hasActiveSession.value) return
  try {
    await attStore.deleteSession(activeSession.value.id)
    attStore.clearSessionDetail()
    resetSessionForm()
    await refreshHistory()
    showToast('Session deleted', 'success')
  } catch (error) {
    showToast(error.message || 'Failed to delete session', 'error')
  }
}

async function mark(student, status) {
  if (!hasActiveSession.value) return
  try {
    await attStore.saveSessionEntry(activeSession.value.id, {
      entryId: student.id,
      studentId: student.studentId,
      status,
    })
  } catch (error) {
    showToast(error.message || 'Failed to save attendance', 'error')
  }
}

async function bulkMark(status) {
  if (!hasActiveSession.value || visibleEntries.value.length === 0) return
  try {
    await attStore.bulkUpsertSessionEntries(activeSession.value.id, visibleEntries.value.map((student) => ({
      studentId: student.studentId,
      status,
    })))
    showToast(`Marked ${visibleEntries.value.length} students`, 'success')
  } catch (error) {
    showToast(error.message || 'Bulk update failed', 'error')
  }
}

async function syncFormFromSession(session) {
  isHydratingSession.value = true
  try {
    selectedDate.value = session.sessionDate || today
    selectedDepartment.value = session.departmentId ? String(session.departmentId) : ''
    await courseStore.fetchCourses(selectedDepartment.value ? { departmentId: selectedDepartment.value } : {})
    selectedCourse.value = session.courseId ? String(session.courseId) : ''
    await loadSubjects()
    selectedSubject.value = session.subjectId ? String(session.subjectId) : ''
    sessionForm.sessionName = session.sessionName || ''
    sessionForm.subjectOrTopic = session.subjectOrTopic || ''
    sessionForm.startTime = session.startTime || ''
    sessionForm.endTime = session.endTime || ''
    sessionForm.periodLabel = session.periodLabel || ''
    sessionForm.remarks = session.remarks || ''
  } finally {
    isHydratingSession.value = false
  }
}

function resetSessionForm() {
  sessionFormError.value = ''
  selectedDate.value = today
  selectedDepartment.value = ''
  selectedCourse.value = ''
  selectedSubject.value = ''
  sessionForm.sessionName = ''
  sessionForm.subjectOrTopic = ''
  sessionForm.startTime = ''
  sessionForm.endTime = ''
  sessionForm.periodLabel = ''
  sessionForm.remarks = ''
}

function buildSessionPayload() {
  const sessionName = sessionForm.sessionName.trim()

  if (!sessionName) {
    sessionFormError.value = 'Session name is required.'
    throw new Error('Session name is required')
  }

  sessionFormError.value = ''

  return {
    departmentId: selectedDepartment.value ? Number(selectedDepartment.value) : null,
    courseId: selectedCourse.value ? Number(selectedCourse.value) : null,
    subjectId: selectedSubject.value ? Number(selectedSubject.value) : null,
    sessionDate: selectedDate.value,
    sessionName,
    subjectOrTopic: sessionForm.subjectOrTopic.trim() || null,
    startTime: sessionForm.startTime || null,
    endTime: sessionForm.endTime || null,
    periodLabel: sessionForm.periodLabel.trim() || null,
    remarks: sessionForm.remarks.trim() || null,
  }
}

async function loadSubjects() {
  await subjectStore.fetchSubjects({
    departmentId: selectedDepartment.value || undefined,
    courseId: selectedCourse.value || undefined,
  })
}

async function applyRouteSelection(query) {
  const departmentId = parseQueryId(query.departmentId)
  const courseId = parseQueryId(query.courseId)
  const subjectId = parseQueryId(query.subjectId)

  if (!departmentId && !courseId && !subjectId) return

  isHydratingSession.value = true
  try {
    attStore.clearSessionDetail()
    sessionFormError.value = ''
    selectedDepartment.value = departmentId ? String(departmentId) : ''
    await courseStore.fetchCourses(selectedDepartment.value ? { departmentId: selectedDepartment.value } : {})
    selectedCourse.value = courseId ? String(courseId) : ''
    await loadSubjects()
    selectedSubject.value = subjectId ? String(subjectId) : ''
    historySubjectId.value = subjectId ? String(subjectId) : ''
    sessionForm.sessionName = ''
    sessionForm.subjectOrTopic = ''
    sessionForm.startTime = ''
    sessionForm.endTime = ''
    sessionForm.periodLabel = ''
    sessionForm.remarks = ''
    historyQuery.departmentId = selectedDepartment.value
    historyQuery.courseId = selectedCourse.value
    historyQuery.subjectId = historySubjectId.value
  } finally {
    isHydratingSession.value = false
  }
}

function parseQueryId(value) {
  const raw = Array.isArray(value) ? value[0] : value
  if (!raw) return null
  const parsed = Number(raw)
  return Number.isFinite(parsed) ? parsed : null
}

function getStatus(student) {
  return attStore.normalizeStatus(student.status)
}

function initials(name = '') {
  return name.split(' ').filter(Boolean).slice(0, 2).map((part) => part[0] || '').join('')
}

function formatDate(date) {
  return date.toISOString().split('T')[0]
}

function formatSessionSlot(session) {
  const period = session?.periodLabel?.trim()
  const start = session?.startTime || ''
  const end = session?.endTime || ''
  const time = start && end ? `${start}–${end}` : start || end || ''
  if (period && time) return `${period} · ${time}`
  if (period) return period
  if (time) return time
  return 'N/A'
}

const toastVisible = ref(false)
const toastMsg = ref('')
const toastType = ref('success')
let toastTimer = null

function showToast(msg, type = 'success') {
  toastMsg.value = msg
  toastType.value = type
  toastVisible.value = true
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => {
    toastVisible.value = false
  }, 2200)
}
</script>

<style scoped>
.attendance-page { padding: 4px 0; color: #111827; }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 24px; }
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon { width: 48px; height: 48px; border-radius: 14px; display: grid; place-items: center; color: #fff; background: linear-gradient(135deg, #7c3aed, #a855f7); box-shadow: 0 4px 14px rgba(124, 58, 237, 0.3); }
.page-title { font-size: 22px; font-weight: 700; margin: 0 0 2px; }
.page-subtitle { font-size: 13px; color: #6b7280; margin: 0; }
.setup-card, .history-card, .table-card, .empty-session-card { background: #fff; border-radius: 16px; border: 1px solid #e5e7eb; box-shadow: 0 1px 6px rgba(0,0,0,0.06); }
.setup-card, .history-card, .empty-session-card { padding: 18px; margin-bottom: 18px; }
.setup-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; }
.control-group { display: flex; flex-direction: column; gap: 6px; }
.control-group-wide { grid-column: span 2; }
.control-label { font-size: 11px; font-weight: 700; letter-spacing: 0.6px; text-transform: uppercase; color: #9ca3af; }
.field-input { height: 40px; padding: 0 12px; border: 1.5px solid #e5e7eb; border-radius: 10px; font: inherit; background: #fff; }
.search-input { min-width: 220px; }
.setup-actions, .history-filters, .bulk-bar { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.setup-actions { margin-top: 14px; }
.primary-btn, .secondary-btn, .ghost-btn, .bulk-btn { border: none; border-radius: 10px; padding: 10px 14px; cursor: pointer; font-size: 13px; font-weight: 700; }
.primary-btn { background: #7c3aed; color: #fff; }
.secondary-btn { background: #f3f4f6; color: #374151; }
.secondary-btn:disabled { cursor: not-allowed; opacity: 0.55; }
.ghost-btn { background: transparent; color: #6b7280; border: 1px solid #e5e7eb; }
.history-toolbar, .session-meta-bar { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; flex-wrap: wrap; margin-bottom: 14px; }
.section-title { margin: 0; font-size: 18px; font-weight: 700; }
.section-subtitle { margin: 3px 0 0; font-size: 13px; color: #6b7280; }
.history-list { display: grid; gap: 10px; }
.history-item { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 14px 16px; border: 1px solid #e5e7eb; border-radius: 12px; background: #fff; cursor: pointer; text-align: left; }
.history-item.active { border-color: #c4b5fd; background: #faf5ff; }
.history-name { font-size: 14px; font-weight: 700; color: #111827; }
.history-meta, .history-topic, .history-open, .empty-history { font-size: 12px; color: #6b7280; }
.empty-history { padding: 14px 4px; }
.summary-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 14px; margin-bottom: 18px; }
.summary-card { display: flex; align-items: center; gap: 14px; padding: 16px 18px; border-radius: 14px; border: 1.5px solid transparent; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.summary-card .sc-icon { width: 40px; height: 40px; border-radius: 10px; display: grid; place-items: center; font-size: 13px; font-weight: 800; }
.summary-card .sc-value { font-size: 24px; font-weight: 700; line-height: 1; }
.summary-card .sc-label { font-size: 12px; color: #6b7280; margin-top: 3px; }
.summary-card.total { border-color: #e5e7eb; } .summary-card.total .sc-icon { background: #f3f4f6; color: #6b7280; }
.summary-card.present { border-color: #d1fae5; } .summary-card.present .sc-icon { background: #d1fae5; color: #059669; }
.summary-card.absent { border-color: #fee2e2; } .summary-card.absent .sc-icon { background: #fee2e2; color: #dc2626; }
.summary-card.late { border-color: #fef3c7; } .summary-card.late .sc-icon { background: #fef3c7; color: #d97706; }
.summary-card.leave { border-color: #e0e7ff; } .summary-card.leave .sc-icon { background: #e0e7ff; color: #4f46e5; }
.summary-card.halfday { border-color: #fce7f3; } .summary-card.halfday .sc-icon { background: #fce7f3; color: #db2777; }
.summary-card.unmarked { border-color: #f3f4f6; } .summary-card.unmarked .sc-icon { background: #f3f4f6; color: #9ca3af; }
.progress-bar-wrap { margin-bottom: 18px; }
.progress-bar-track { height: 8px; background: #f3f4f6; border-radius: 99px; overflow: hidden; display: flex; }
.bar-present { background: #10b981; height: 100%; transition: width 0.3s ease; }
.bar-late { background: #f59e0b; height: 100%; transition: width 0.3s ease; }
.bar-leave { background: #6366f1; height: 100%; transition: width 0.3s ease; }
.bar-halfday { background: #ec4899; height: 100%; transition: width 0.3s ease; }
.bar-absent { background: #ef4444; height: 100%; transition: width 0.3s ease; }
.progress-legend { display: flex; gap: 18px; margin-top: 8px; flex-wrap: wrap; }
.leg { font-size: 11px; font-weight: 600; display: flex; align-items: center; gap: 5px; opacity: 0.55; }
.leg.active { opacity: 1; }
.leg::before { content: ''; width: 8px; height: 8px; border-radius: 50%; display: inline-block; }
.leg-present { color: #059669; } .leg-present::before { background: #10b981; }
.leg-late { color: #d97706; } .leg-late::before { background: #f59e0b; }
.leg-leave { color: #4f46e5; } .leg-leave::before { background: #6366f1; }
.leg-halfday { color: #db2777; } .leg-halfday::before { background: #ec4899; }
.leg-absent { color: #dc2626; } .leg-absent::before { background: #ef4444; }
.bulk-title { font-size: 13px; font-weight: 700; color: #6b7280; }
.bulk-present { background: #d1fae5; color: #059669; }
.bulk-absent { background: #fee2e2; color: #dc2626; }
.bulk-leave { background: #e0e7ff; color: #4f46e5; }
.bulk-late { background: #fef3c7; color: #d97706; }
.bulk-halfday { background: #fce7f3; color: #db2777; }
.table-card { overflow: hidden; margin-bottom: 22px; }
.table-wrap { overflow-x: auto; }
.att-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
.att-table thead { background: #fafafa; border-bottom: 1px solid #e5e7eb; }
.att-table th { padding: 13px 16px; text-align: left; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.7px; color: #9ca3af; }
.att-table td { padding: 14px 16px; border-bottom: 1px solid #f3f4f6; vertical-align: middle; }
.student-row.present { background: rgba(16, 185, 129, 0.04); }
.student-row.absent { background: rgba(239, 68, 68, 0.08); border-left: 4px solid #ef4444; }
.student-row.late { background: rgba(245, 158, 11, 0.04); }
.student-row.leave { background: rgba(79, 70, 229, 0.04); }
.student-row.halfday { background: rgba(219, 39, 119, 0.04); }
.empty-row { text-align: center; padding: 40px; color: #9ca3af; font-size: 14px; }
.td-no { color: #9ca3af; width: 44px; }
.roll-badge { background: #f3f4f6; color: #374151; font-size: 12px; font-weight: 600; padding: 3px 9px; border-radius: 6px; font-family: monospace; }
.student-info { display: flex; align-items: center; gap: 12px; }
.student-avatar { width: 36px; height: 36px; border-radius: 50%; display: grid; place-items: center; font-size: 12px; font-weight: 700; color: #fff; }
.avatar-0 { background: linear-gradient(135deg, #7c3aed, #a855f7); }
.avatar-1 { background: linear-gradient(135deg, #0ea5e9, #38bdf8); }
.avatar-2 { background: linear-gradient(135deg, #10b981, #34d399); }
.avatar-3 { background: linear-gradient(135deg, #f59e0b, #fbbf24); }
.avatar-4 { background: linear-gradient(135deg, #ef4444, #f87171); }
.avatar-5 { background: linear-gradient(135deg, #ec4899, #f472b6); }
.student-name { font-weight: 600; color: #111827; }
.student-email { font-size: 12px; color: #9ca3af; margin-top: 1px; }
.class-badge { background: #ede9fe; color: #6d28d9; font-size: 12px; font-weight: 600; padding: 3px 10px; border-radius: 6px; }
.course-chip { background: #dbeafe; color: #1d4ed8; font-size: 12px; font-weight: 600; padding: 3px 10px; border-radius: 6px; }
.status-buttons { display: flex; gap: 6px; flex-wrap: wrap; }
.status-btn { padding: 6px 12px; border-radius: 8px; font-size: 12px; font-weight: 600; border: 1.5px solid transparent; cursor: pointer; background: #f3f4f6; color: #6b7280; }
.present-btn { border-color: #d1fae5; color: #059669; background: #d1fae5; }
.present-btn.active { background: #059669; color: #fff; }
.absent-btn { border-color: #fee2e2; color: #dc2626; background: #fee2e2; }
.absent-btn.active { background: #dc2626; color: #fff; }
.leave-btn { border-color: #e0e7ff; color: #4f46e5; background: #e0e7ff; }
.leave-btn.active { background: #4f46e5; color: #fff; }
.halfday-btn { border-color: #fce7f3; color: #db2777; background: #fce7f3; }
.halfday-btn.active { background: #db2777; color: #fff; }
.late-btn { border-color: #fde68a; color: #d97706; background: #fef3c7; }
.late-btn.active { background: #d97706; color: #fff; }
.empty-session-card { text-align: center; }
.state-banner { margin-bottom: 14px; padding: 12px 16px; border-radius: 10px; }
.session-form-banner { margin-top: 14px; margin-bottom: 0; }
.error-banner { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; }
.toast { position: fixed; bottom: 28px; right: 28px; padding: 12px 20px; border-radius: 12px; font-size: 13.5px; font-weight: 600; box-shadow: 0 8px 24px rgba(0,0,0,0.12); z-index: 9999; color: #fff; }
.toast-success { background: #059669; }
.toast-error { background: #dc2626; }
.toast-fade-enter-active, .toast-fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.toast-fade-enter-from, .toast-fade-leave-to { opacity: 0; transform: translateY(10px); }

@media (max-width: 900px) {
  .control-group-wide { grid-column: span 1; }
  .history-toolbar, .session-meta-bar { flex-direction: column; }
}
</style>
