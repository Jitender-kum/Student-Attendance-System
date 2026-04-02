<template>
  <div class="attendance-page">

    <!-- ── Page Header ───────────────────────────────────────────────── -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor"
            stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 11l3 3L22 4"/>
            <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
          </svg>
        </div>
        <div>
          <h1 class="page-title">Attendance</h1>
          <p class="page-subtitle">Mark and manage daily student attendance</p>
        </div>
      </div>

      <!-- Controls -->
      <div class="header-controls">
        <div class="control-group">
          <label class="control-label">Date</label>
          <input id="att-date" type="date" class="date-input" v-model="selectedDate" />
        </div>
        <div class="control-group">
          <label class="control-label">Class</label>
          <select id="att-class" class="class-select" v-model="selectedClass">
            <option value="all">All Classes</option>
            <option v-for="cls in classes" :key="cls" :value="cls">{{ cls }}</option>
          </select>
        </div>
      </div>
    </div>

    <!-- ── Summary Cards ─────────────────────────────────────────────── -->
    <div class="summary-row">
      <div class="summary-card total">
        <div class="sc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
        <div>
          <div class="sc-value">{{ summary.total }}</div>
          <div class="sc-label">Total Students</div>
        </div>
      </div>

      <div class="summary-card present">
        <div class="sc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
          </svg>
        </div>
        <div>
          <div class="sc-value">{{ summary.present }}</div>
          <div class="sc-label">Present</div>
        </div>
      </div>

      <div class="summary-card absent">
        <div class="sc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
          </svg>
        </div>
        <div>
          <div class="sc-value">{{ summary.absent }}</div>
          <div class="sc-label">Absent</div>
        </div>
      </div>

      <div class="summary-card late">
        <div class="sc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="12 6 12 12 16 14"/>
          </svg>
        </div>
        <div>
          <div class="sc-value">{{ summary.late }}</div>
          <div class="sc-label">Late</div>
        </div>
      </div>

      <div class="summary-card leave">
        <div class="sc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
        <div>
          <div class="sc-value">{{ summary.leave }}</div>
          <div class="sc-label">Leave</div>
        </div>
      </div>

      <div class="summary-card halfday">
        <div class="sc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2v20"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H9"/>
          </svg>
        </div>
        <div>
          <div class="sc-value">{{ summary.halfday }}</div>
          <div class="sc-label">Half Day</div>
        </div>
      </div>

      <div class="summary-card unmarked">
        <div class="sc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
        </div>
        <div>
          <div class="sc-value">{{ summary.unmarked }}</div>
          <div class="sc-label">Unmarked</div>
        </div>
      </div>
    </div>

    <!-- Progress bar -->
    <div class="progress-bar-wrap">
      <div class="progress-bar-track">
        <div class="bar-present" :style="{ width: pctPresent + '%' }"></div>
        <div class="bar-late" :style="{ width: pctLate + '%' }"></div>
        <div class="bar-leave" :style="{ width: pctLeave + '%' }"></div>
        <div class="bar-halfday" :style="{ width: pctHalfDay + '%' }"></div>
        <div class="bar-absent" :style="{ width: pctAbsent + '%' }"></div>
      </div>
      <div class="progress-legend">
        <span class="leg leg-present">Present {{ pctPresent.toFixed(0) }}%</span>
        <span class="leg leg-late">Late {{ pctLate.toFixed(0) }}%</span>
        <span class="leg leg-leave">Leave {{ pctLeave.toFixed(0) }}%</span>
        <span class="leg leg-halfday">Half {{ pctHalfDay.toFixed(0) }}%</span>
        <span class="leg leg-absent">Absent {{ pctAbsent.toFixed(0) }}%</span>
      </div>
    </div>

    <!-- ── Bulk Actions ───────────────────────────────────────────────── -->
    <div class="bulk-bar">
      <span class="bulk-title">Bulk Mark:</span>
      <button class="bulk-btn bulk-present" id="bulk-present" @click="bulkMark('present')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
        All Present
      </button>
      <button class="bulk-btn bulk-absent" id="bulk-absent" @click="bulkMark('absent')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        All Absent
      </button>
      <button class="bulk-btn bulk-leave" id="bulk-leave" @click="bulkMark('leave')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        All Leave
      </button>
      <button class="bulk-btn bulk-late" id="bulk-late" @click="bulkMark('late')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        All Late
      </button>
      <button class="bulk-btn bulk-halfday" id="bulk-halfday" @click="bulkMark('halfday')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2v20"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H9"/></svg>
        All Half
      </button>
      <button class="bulk-btn bulk-clear" id="bulk-clear" @click="bulkMark(null)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-4.14"/></svg>
        Clear All
      </button>
    </div>

    <!-- ── Student Table ──────────────────────────────────────────────── -->
    <div class="table-card">
      <div class="table-wrap">
        <table class="att-table">
          <thead>
            <tr>
              <th class="th-no">#</th>
              <th class="th-roll">Roll No.</th>
              <th class="th-name">Student Name</th>
              <th class="th-class">Class</th>
              <th class="th-gender">Gender</th>
              <th class="th-status">Attendance Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="attStore.loading">
              <td colspan="6" class="empty-row">
                <div class="loader-wrap">
                  <div class="loader"></div>
                  <span>Loading attendance data...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="filteredStudents.length === 0">
              <td colspan="6" class="empty-row">No students found for this class.</td>
            </tr>
            <tr v-else v-for="(student, idx) in filteredStudents" :key="student.id"
              class="student-row" :class="getStatus(student.id)">
              <td class="td-no">{{ idx + 1 }}</td>
              <td class="td-roll">
                <span class="roll-badge">{{ student.roll }}</span>
              </td>
              <td class="td-name">
                <div class="student-info">
                  <div class="student-avatar" :class="'avatar-' + (student.id % 6)">
                    {{ student.firstName[0] }}{{ student.lastName[0] }}
                  </div>
                  <div>
                    <div class="student-name">
                      {{ student.firstName }} {{ student.lastName }}
                      <span v-if="getStatus(student.id) === 'absent'" class="absent-label">ABSENT</span>
                    </div>
                    <div class="student-email">{{ student.email }}</div>
                  </div>
                </div>
              </td>
              <td class="td-class">
                <span class="class-badge">{{ student.classSection }}</span>
              </td>
              <td class="td-gender">
                <span class="gender-chip" :class="student.gender.toLowerCase()">
                  {{ student.gender }}
                </span>
              </td>
              <td class="td-status">
                <div class="status-buttons">
                  <button
                    class="status-btn present-btn"
                    :class="{ active: getStatus(student.id) === 'present' }"
                    :id="'btn-present-' + student.id"
                    @click="mark(student.id, 'present')"
                    title="Mark Present"
                  >
                    Pres
                  </button>
                  <button
                    class="status-btn absent-btn"
                    :class="{ active: getStatus(student.id) === 'absent' }"
                    :id="'btn-absent-' + student.id"
                    @click="mark(student.id, 'absent')"
                    title="Mark Absent"
                  >
                    Abs
                  </button>
                  <button
                    class="status-btn leave-btn"
                    :class="{ active: getStatus(student.id) === 'leave' }"
                    :id="'btn-leave-' + student.id"
                    @click="mark(student.id, 'leave')"
                    title="Mark Leave"
                  >
                    Leave
                  </button>
                  <button
                    class="status-btn halfday-btn"
                    :class="{ active: getStatus(student.id) === 'halfday' }"
                    :id="'btn-halfday-' + student.id"
                    @click="mark(student.id, 'halfday')"
                    title="Mark Half Day"
                  >
                    Half
                  </button>
                  <button
                    class="status-btn late-btn"
                    :class="{ active: getStatus(student.id) === 'late' }"
                    :id="'btn-late-' + student.id"
                    @click="mark(student.id, 'late')"
                    title="Mark Late"
                  >
                    Late
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ── Save Toast ─────────────────────────────────────────────────── -->
    <transition name="toast-fade">
      <div v-if="toastVisible" class="toast" :class="'toast-' + toastType">
        <svg v-if="toastType === 'success'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
        {{ toastMsg }}
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useStudentStore }    from '../../stores/students'
import { useAttendanceStore } from '../../stores/attendance'

const studentStore = useStudentStore()
const attStore     = useAttendanceStore()

// Always load live data from backend for selected date
onMounted(async () => {
  const list = await attStore.fetchByDate(selectedDate.value)
  if (list) studentStore.students = list
})

/* ── Date & Class filter ───────────── */
const today = new Date().toISOString().split('T')[0]
const selectedDate = ref(today)
const selectedClass = ref('all')

// Fetch attendance when date changes
watch(selectedDate, async (newDate) => {
  const list = await attStore.fetchByDate(newDate)
  if (list) studentStore.students = list
})

const classes = computed(() => {
  const s = new Set(studentStore.students.map(s => s.classSection))
  return [...s].sort()
})

const filteredStudents = computed(() => {
  if (selectedClass.value === 'all') return studentStore.students
  return studentStore.students.filter(s => s.classSection === selectedClass.value)
})

/* ── Attendance helpers ────────────── */
function getStatus(studentId) {
  return attStore.getRecord(selectedDate.value, studentId)
}

async function mark(studentId, status) {
  const current = getStatus(studentId)
  // clicking same status again clears it (toggle)
  const next = current === status ? null : status
  
  const list = await attStore.markAttendance(selectedDate.value, studentId, next)
  if (list && !list._error) {
    // If successful, update the students list
    studentStore.students = list
    showToast(`Marked ${status || 'unmarked'}`, 'success')
  } else {
    showToast(`Failed to mark: ${list?._error || 'Unknown error'}`, 'error')
  }
}

async function bulkMark(status) {
  const ids = filteredStudents.value.map(s => s.id)
  if (status === null) {
    const list = await attStore.markAll(selectedDate.value, ids, null)
    if (list && !list._error) {
      studentStore.students = list
      showToast('Attendance cleared', 'success')
    }
  } else {
    const list = await attStore.markAll(selectedDate.value, ids, status)
    if (list && !list._error) {
      studentStore.students = list
      showToast(`All marked ${status}`, 'success')
    } else {
      showToast(`Bulk marking failed: ${list?._error || 'Unknown error'}`, 'error')
    }
  }
}

/* ── Summary ───────────────────────── */
const summary = computed(() =>
  attStore.getDateSummary(selectedDate.value, filteredStudents.value)
)

const pctPresent = computed(() => summary.value.total ? (summary.value.present / summary.value.total) * 100 : 0)
const pctAbsent  = computed(() => summary.value.total ? (summary.value.absent  / summary.value.total) * 100 : 0)
const pctLate    = computed(() => summary.value.total ? (summary.value.late    / summary.value.total) * 100 : 0)
const pctLeave   = computed(() => summary.value.total ? (summary.value.leave   / summary.value.total) * 100 : 0)
const pctHalfDay = computed(() => summary.value.total ? (summary.value.halfday / summary.value.total) * 100 : 0)

/* ── Toast ─────────────────────────── */
const toastVisible = ref(false)
const toastMsg = ref('')
const toastType = ref('success')
let toastTimer = null

function showToast(msg, type = 'success') {
  toastMsg.value = msg
  toastType.value = type
  toastVisible.value = true
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toastVisible.value = false }, 2000)
}
</script>

<style scoped>
/* ── Layout ─────────────────────────────────────────────────────────── */
.attendance-page {
  padding: 28px 32px;
  max-width: 1200px;
  font-family: 'Inter', 'Outfit', system-ui, sans-serif;
}

/* ── Header ──────────────────────────────────────────────────────────── */
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  border-radius: 14px;
  display: grid;
  place-items: center;
  color: #fff;
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(124, 58, 237, 0.3);
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 2px;
}

.page-subtitle {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.header-controls {
  display: flex;
  align-items: flex-end;
  gap: 14px;
  flex-wrap: wrap;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.control-label {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  color: #9ca3af;
}

.date-input,
.class-select {
  height: 38px;
  padding: 0 12px;
  border: 1.5px solid #e5e7eb;
  border-radius: 9px;
  font-size: 13.5px;
  color: #111827;
  background: #fff;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
  cursor: pointer;
}

.date-input:focus,
.class-select:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.12);
}

/* ── Summary Cards ───────────────────────────────────────────────────── */
.summary-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  border-radius: 14px;
  border: 1.5px solid transparent;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  transition: transform 0.15s;
}

.summary-card:hover {
  transform: translateY(-2px);
}

.summary-card .sc-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.summary-card .sc-label {
  font-size: 12px;
  color: #6b7280;
  margin-top: 3px;
}

.summary-card .sc-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

/* Variants */
.summary-card.total  { border-color: #e5e7eb; }
.summary-card.total .sc-icon  { background: #f3f4f6; color: #6b7280; }
.summary-card.total .sc-value { color: #111827; }

.summary-card.present { border-color: #d1fae5; }
.summary-card.present .sc-icon  { background: #d1fae5; color: #059669; }
.summary-card.present .sc-value { color: #059669; }

.summary-card.absent { border-color: #fee2e2; }
.summary-card.absent .sc-icon  { background: #fee2e2; color: #dc2626; }
.summary-card.absent .sc-value { color: #dc2626; }

.summary-card.late { border-color: #fef3c7; }
.summary-card.late .sc-icon  { background: #fef3c7; color: #d97706; }
.summary-card.late .sc-value { color: #d97706; }

.summary-card.leave { border-color: #e0e7ff; }
.summary-card.leave .sc-icon  { background: #e0e7ff; color: #4f46e5; }
.summary-card.leave .sc-value { color: #4f46e5; }

.summary-card.halfday { border-color: #fce7f3; }
.summary-card.halfday .sc-icon  { background: #fce7f3; color: #db2777; }
.summary-card.halfday .sc-value { color: #db2777; }

.summary-card.unmarked { border-color: #f3f4f6; }
.summary-card.unmarked .sc-icon  { background: #f3f4f6; color: #9ca3af; }
.summary-card.unmarked .sc-value { color: #9ca3af; }

/* ── Progress Bar ────────────────────────────────────────────────────── */
.progress-bar-wrap {
  margin-bottom: 20px;
}

.progress-bar-track {
  height: 8px;
  background: #f3f4f6;
  border-radius: 99px;
  overflow: hidden;
  display: flex;
}

.bar-present { background: #10b981; height: 100%; transition: width 0.4s ease; }
.bar-late    { background: #f59e0b; height: 100%; transition: width 0.4s ease; }
.bar-leave   { background: #6366f1; height: 100%; transition: width 0.4s ease; }
.bar-halfday { background: #ec4899; height: 100%; transition: width 0.4s ease; }
.bar-absent  { background: #ef4444; height: 100%; transition: width 0.4s ease; }

.progress-legend {
  display: flex;
  gap: 18px;
  margin-top: 8px;
  flex-wrap: wrap;
}

.leg {
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 5px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.leg::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.leg-present { color: #059669; }
.leg-present::before { background: #10b981; }

.leg-late { color: #d97706; }
.leg-late::before { background: #f59e0b; }

.leg-leave { color: #4f46e5; }
.leg-leave::before { background: #6366f1; }

.leg-halfday { color: #db2777; }
.leg-halfday::before { background: #ec4899; }

.leg-absent { color: #dc2626; }
.leg-absent::before { background: #ef4444; }

/* ── Bulk Bar ────────────────────────────────────────────────────────── */
.bulk-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.bulk-title {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  margin-right: 4px;
}

.bulk-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  border-radius: 8px;
  font-size: 12.5px;
  font-weight: 600;
  border: 1.5px solid transparent;
  cursor: pointer;
  transition: all 0.15s;
}

.bulk-present { background: #d1fae5; color: #059669; border-color: #a7f3d0; }
.bulk-present:hover { background: #059669; color: #fff; }

.bulk-absent { background: #fee2e2; color: #dc2626; border-color: #fecaca; }
.bulk-absent:hover { background: #dc2626; color: #fff; }

.bulk-leave { background: #e0e7ff; color: #4f46e5; border-color: #c7d2fe; }
.bulk-leave:hover { background: #4f46e5; color: #fff; }

.bulk-late { background: #fef3c7; color: #d97706; border-color: #fde68a; }
.bulk-late:hover { background: #d97706; color: #fff; }

.bulk-halfday { background: #fce7f3; color: #db2777; border-color: #fbcfe8; }
.bulk-halfday:hover { background: #db2777; color: #fff; }

.bulk-clear { background: #f3f4f6; color: #6b7280; border-color: #e5e7eb; }
.bulk-clear:hover { background: #6b7280; color: #fff; }

/* ── Table Card ──────────────────────────────────────────────────────── */
.table-card {
  background: #fff;
  border-radius: 16px;
  border: 1.5px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow: hidden;
}

.table-wrap {
  overflow-x: auto;
}

.att-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13.5px;
}

.att-table thead {
  background: #fafafa;
  border-bottom: 1.5px solid #e5e7eb;
}

.att-table th {
  padding: 13px 16px;
  text-align: left;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.7px;
  color: #9ca3af;
  white-space: nowrap;
}

.att-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.student-row {
  transition: background 0.15s;
}

.student-row:last-child td { border-bottom: none; }
.student-row:hover { background: #fafafa; }

/* Row color tints by status */
.student-row.present { background: rgba(16, 185, 129, 0.04); }
.student-row.present:hover { background: rgba(16, 185, 129, 0.08); }

.student-row.absent { background: rgba(239, 68, 68, 0.08); border-left: 4px solid #ef4444; }
.student-row.absent:hover { background: rgba(239, 68, 68, 0.12); }

.student-row.late { background: rgba(245, 158, 11, 0.04); }
.student-row.late:hover { background: rgba(245, 158, 11, 0.08); }

/* Cells */
.td-no { color: #9ca3af; font-size: 13px; width: 44px; }

.roll-badge {
  background: #f3f4f6;
  color: #374151;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 9px;
  border-radius: 6px;
  font-family: monospace;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
  letter-spacing: 0.5px;
}

.avatar-0 { background: linear-gradient(135deg, #7c3aed, #a855f7); }
.avatar-1 { background: linear-gradient(135deg, #0ea5e9, #38bdf8); }
.avatar-2 { background: linear-gradient(135deg, #10b981, #34d399); }
.avatar-3 { background: linear-gradient(135deg, #f59e0b, #fbbf24); }
.avatar-4 { background: linear-gradient(135deg, #ef4444, #f87171); }
.avatar-5 { background: linear-gradient(135deg, #ec4899, #f472b6); }

.student-name { font-weight: 600; color: #111827; }
.student-email { font-size: 12px; color: #9ca3af; margin-top: 1px; }

.class-badge {
  background: #ede9fe;
  color: #6d28d9;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 6px;
}

.gender-chip {
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 6px;
}

.gender-chip.male   { background: #dbeafe; color: #1d4ed8; }
.gender-chip.female { background: #fce7f3; color: #9d174d; }

/* ── Status Buttons ──────────────────────────────────────────────────── */
.status-buttons {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.status-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  border: 1.5px solid transparent;
  cursor: pointer;
  transition: all 0.15s;
  background: #f3f4f6;
  color: #6b7280;
}

.status-btn:hover { transform: translateY(-1px); }

/* Present */
.present-btn { border-color: #d1fae5; color: #059669; background: #d1fae5; }
.present-btn:hover { background: #059669; color: #fff; box-shadow: 0 4px 10px rgba(5, 150, 105, 0.3); }
.present-btn.active { background: #059669; color: #fff; box-shadow: 0 4px 10px rgba(5, 150, 105, 0.3); }

/* Late */
.late-btn { border-color: #fde68a; color: #d97706; background: #fef3c7; }
.late-btn:hover { background: #d97706; color: #fff; box-shadow: 0 4px 10px rgba(217, 119, 6, 0.3); }
.late-btn.active { background: #d97706; color: #fff; box-shadow: 0 4px 10px rgba(217, 119, 6, 0.3); }

/* Leave */
.leave-btn { border-color: #e0e7ff; color: #4f46e5; background: #e0e7ff; }
.leave-btn:hover { background: #4f46e5; color: #fff; box-shadow: 0 4px 10px rgba(79, 70, 229, 0.3); }
.leave-btn.active { background: #4f46e5; color: #fff; box-shadow: 0 4px 10px rgba(79, 70, 229, 0.3); }

/* Half Day */
.halfday-btn { border-color: #fce7f3; color: #db2777; background: #fce7f3; }
.halfday-btn:hover { background: #db2777; color: #fff; box-shadow: 0 4px 10px rgba(219, 39, 119, 0.3); }
.halfday-btn.active { background: #db2777; color: #fff; box-shadow: 0 4px 10px rgba(219, 39, 119, 0.3); }

/* Row color tints */
.student-row.present { background: rgba(16, 185, 129, 0.04); }
.student-row.absent { background: rgba(239, 68, 68, 0.04); }
.student-row.late { background: rgba(245, 158, 11, 0.04); }
.student-row.leave { background: rgba(79, 70, 229, 0.04); }
.student-row.halfday { background: rgba(219, 39, 119, 0.04); }

/* Empty row */
.empty-row {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
  font-size: 14px;
}

.loader-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
}

.loader {
  width: 28px;
  height: 28px;
  border: 3px solid #f3f4f6;
  border-top-color: #7c3aed;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ── Toast ───────────────────────────────────────────────────────────── */
.toast {
  position: fixed;
  bottom: 28px;
  right: 28px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 13.5px;
  font-weight: 600;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  z-index: 9999;
  pointer-events: none;
}

.toast-success { background: #059669; color: #fff; }
.toast-error   { background: #dc2626; color: #fff; }

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
