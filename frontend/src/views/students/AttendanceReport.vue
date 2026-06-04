<template>
  <div class="report-page">

    <!-- ── Header ───────────────────────────────────────────────────── -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor"
            stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
            <polyline points="10 9 9 9 8 9"/>
          </svg>
        </div>
        <div>
          <h1 class="page-title">Attendance Report</h1>
          <p class="page-subtitle">Session-wise attendance rows with subject and lecture slot details</p>
        </div>
      </div>

      <!-- Toolbar -->
      <div class="toolbar">
        <div class="control-group">
          <label class="control-label">Report Type</label>
          <select class="filter-select" v-model="reportType">
            <option value="weekly">Weekly</option>
            <option value="monthly">Monthly</option>
            <option value="semester">Semester-wise</option>
          </select>
        </div>
        <div class="control-group">
          <label class="control-label">Department</label>
          <select class="filter-select" v-model="selectedDepartment">
            <option value="">All Departments</option>
            <option v-for="department in departmentStore.departments" :key="department.id" :value="String(department.id)">{{ department.name }}</option>
          </select>
        </div>
        <div class="control-group">
          <label class="control-label">Course</label>
          <select class="filter-select" v-model="selectedCourse">
            <option value="">All Courses</option>
            <option v-for="course in courseStore.courses" :key="course.id" :value="String(course.id)">{{ course.name }}</option>
          </select>
        </div>
        <div class="control-group" v-if="reportType === 'weekly'">
          <label class="control-label">Week Start</label>
          <input v-model="selectedWeek" type="date" class="filter-select" />
        </div>
        <div class="control-group" v-if="reportType === 'monthly'">
          <label class="control-label">Month</label>
          <select class="filter-select" v-model="selectedMonth">
            <option v-for="month in 12" :key="month" :value="String(month)">{{ month }}</option>
          </select>
        </div>
        <div class="control-group" v-if="reportType === 'monthly'">
          <label class="control-label">Year</label>
          <input v-model="selectedYear" type="number" class="filter-select" />
        </div>
        <div class="control-group" v-if="reportType === 'semester'">
          <label class="control-label">Year</label>
          <input v-model="selectedStudentYear" type="number" min="1" max="10" class="filter-select" />
        </div>
        <div class="control-group" v-if="reportType === 'semester'">
          <label class="control-label">Semester</label>
          <input v-model="selectedSemester" type="number" min="1" max="12" class="filter-select" />
        </div>
        <div class="control-group">
          <label class="control-label">Sort by</label>
          <select id="sort-by" class="filter-select" v-model="sortBy">
            <option value="name">Name</option>
            <option value="pct-desc">Attendance % ↓</option>
            <option value="pct-asc">Attendance % ↑</option>
            <option value="absent-desc">Most Absent</option>
            <option value="total-desc">Most Days Recorded</option>
          </select>
        </div>
        <div class="search-wrap">
          <svg class="search-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input id="student-search" type="text" class="search-input" v-model="searchQuery" placeholder="Search student…" />
        </div>
        <div class="export-actions">
          <button class="export-btn" :disabled="downloadLoading || reportStore.loading" @click="downloadReport('weekly')">
            Download Weekly Excel
          </button>
          <button class="export-btn" :disabled="downloadLoading || reportStore.loading" @click="downloadReport('monthly')">
            Download Monthly Excel
          </button>
          <button class="export-btn" :disabled="downloadLoading || reportStore.loading" @click="downloadReport('semester')">
            Download Semester Excel
          </button>
        </div>
      </div>
    </div>

    <!-- ── Overall Snapshot ──────────────────────────────────────────── -->
    <div class="snapshot-row">
      <div class="snap-card snap-total">
        <div class="snap-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div>
          <div class="snap-value">{{ filteredRows.length }}</div>
          <div class="snap-label">Session Rows</div>
        </div>
      </div>

      <div class="snap-card snap-days">
        <div class="snap-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        </div>
        <div>
          <div class="snap-value">{{ totalDaysRecorded }}</div>
          <div class="snap-label">Days Recorded</div>
        </div>
      </div>

      <div class="snap-card snap-present">
        <div class="snap-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
        </div>
        <div>
          <div class="snap-value">{{ overallPct !== null ? overallPct + '%' : 'N/A' }}</div>
          <div class="snap-label">Avg Attendance</div>
        </div>
      </div>

      <div class="snap-card snap-risk">
        <div class="snap-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
        </div>
        <div>
          <div class="snap-value">{{ atRiskCount }}</div>
          <div class="snap-label">At Risk (&lt;75%)</div>
        </div>
      </div>
    </div>

    <!-- No data state -->
    <div v-if="totalDaysRecorded === 0" class="no-data-card">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#d1d5db" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
      <div class="nd-title">No attendance recorded yet</div>
      <div class="nd-sub">Go to the <strong>Attendance</strong> page and start marking students to see their totals here.</div>
    </div>

    <!-- ── Report Table ───────────────────────────────────────────────── -->
    <div v-else class="table-card">
      <div class="table-wrap">
        <table class="report-table">
          <thead>
            <tr>
              <th class="th-select">
                <input
                  type="checkbox"
                  class="row-checkbox"
                  :checked="allVisibleSelected"
                  :indeterminate.prop="someVisibleSelected && !allVisibleSelected"
                  @change="toggleSelectAllVisible($event.target.checked)"
                />
              </th>
              <th class="th-rank">#</th>
              <th class="th-name">Student</th>
              <th class="th-class">Class</th>
              <th class="th-session">Session</th>
              <th class="th-pct">Attendance %</th>
              <th class="th-bar">Progress</th>
              <th class="th-present">Present</th>
              <th class="th-late">Late</th>
              <th class="th-absent">Absent</th>
              <th class="th-absent">Leave</th>
              <th class="th-absent">Half Day</th>
              <th class="th-total">Total Days</th>
              <th class="th-status">Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="reportStore.loading">
              <td colspan="14" class="empty-row">Loading report...</td>
            </tr>
            <tr v-else-if="sortedRows.length === 0">
              <td colspan="14" class="empty-row">No students match the current filters.</td>
            </tr>
            <tr v-for="(row, idx) in sortedRows" :key="rowKey(row, idx)"
              class="report-row" :class="rowStatusClass(row)">
              <td class="td-select">
                <input
                  type="checkbox"
                  class="row-checkbox"
                  :checked="isSelected(row.studentId)"
                  @change="toggleRowSelection(row.studentId, $event.target.checked)"
                />
              </td>
              <td class="td-rank">{{ idx + 1 }}</td>

              <td class="td-name">
                <div class="student-info">
                  <div class="student-avatar" :class="'avatar-' + (row.studentId % 6)">
                    {{ row.studentName.split(' ')[0]?.[0] || '' }}{{ row.studentName.split(' ')[1]?.[0] || '' }}
                  </div>
                  <div>
                    <div class="student-name">{{ row.studentName }}</div>
                    <div class="student-meta">{{ row.rollNumber }} · Year {{ row.year ?? '—' }} · Semester {{ row.semester ?? '—' }}</div>
                  </div>
                </div>
              </td>

              <td class="td-class">
                <span class="class-badge">{{ row.departmentName }} · {{ row.courseName }}</span>
              </td>

              <td class="td-session">
                <div class="session-meta-block">
                  <div class="session-subject">{{ row.subjectName || 'N/A' }}</div>
                  <div class="session-time">
                    {{ row.periodLabel || 'N/A' }} · {{ formatSessionTime(row) }}
                  </div>
                </div>
              </td>

              <td class="td-pct">
                <span class="pct-pill" :class="pctPillClass(row.attendancePercentage)">
                  {{ row.attendancePercentage }}%
                </span>
              </td>

              <td class="td-bar">
                <div class="mini-bar-track" v-if="row.totalWorkingDays > 0">
                  <div class="mini-bar-present" :style="{ width: ((row.presentCount / row.totalWorkingDays) * 100) + '%' }"></div>
                  <div class="mini-bar-late"    :style="{ width: ((row.lateCount / row.totalWorkingDays) * 100) + '%' }"></div>
                  <div class="mini-bar-absent"  :style="{ width: ((row.absentCount / row.totalWorkingDays) * 100) + '%' }"></div>
                </div>
                <span v-else class="bar-na">No data</span>
              </td>

              <td class="td-present">
                <div class="count-chip present-chip">{{ row.presentCount }}</div>
              </td>
              <td class="td-late">
                <div class="count-chip late-chip">{{ row.lateCount }}</div>
              </td>
              <td class="td-absent">
                <div class="count-chip absent-chip">{{ row.absentCount }}</div>
              </td>
              <td class="td-absent">
                <div class="count-chip leave-chip">{{ row.leaveCount }}</div>
              </td>
              <td class="td-absent">
                <div class="count-chip halfday-chip">{{ row.halfDayCount }}</div>
              </td>
              <td class="td-total">
                <span class="total-days">{{ row.totalWorkingDays }}</span>
              </td>

              <td class="td-status">
                <span class="status-badge" :class="statusBadgeClass(row.attendancePercentage)">
                  {{ statusLabel(row.attendancePercentage) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Table footer summary -->
      <div class="table-footer">
        <span class="tf-info">Showing {{ sortedRows.length }} report rows</span>
        <div class="tf-legend">
          <span class="selection-summary">{{ selectedCountLabel }}</span>
          <span class="tfl tfl-excellent">Excellent ≥90%</span>
          <span class="tfl tfl-good">Good ≥75%</span>
          <span class="tfl tfl-warning">Warning ≥50%</span>
          <span class="tfl tfl-risk">At Risk &lt;50%</span>
          <span class="tfl tfl-none">No Data</span>
        </div>
      </div>
    </div>

    <!-- ── Student Cards View (alternative compact view) ─────────────── -->
    <div v-if="totalDaysRecorded > 0" class="cards-section">
      <div class="section-divider">
        <h2 class="section-title">Student Cards</h2>
        <span class="section-sub">Quick at-a-glance view</span>
      </div>

      <div class="cards-grid">
        <div v-for="(row, idx) in sortedRows" :key="'card-' + rowKey(row, idx)"
          class="student-card" :class="rowStatusClass(row)">

          <div class="sc-top">
            <div class="student-avatar lg" :class="'avatar-' + (row.studentId % 6)">
              {{ row.studentName.split(' ')[0]?.[0] || '' }}{{ row.studentName.split(' ')[1]?.[0] || '' }}
            </div>
            <div class="sc-info">
              <div class="sc-name">{{ row.studentName }}</div>
              <div class="sc-meta">{{ row.rollNumber }} · {{ row.departmentName }}</div>
              <div class="sc-meta">{{ row.subjectName || 'N/A' }} · {{ row.periodLabel || 'N/A' }} · {{ formatSessionTime(row) }}</div>
            </div>
            <span class="pct-pill lg-pill" :class="pctPillClass(row.attendancePercentage)">
              {{ row.attendancePercentage }}%
            </span>
          </div>

          <!-- mini progress bar -->
          <div class="sc-bar-track" v-if="row.totalWorkingDays > 0">
            <div class="sc-bar-present" :style="{ width: ((row.presentCount / row.totalWorkingDays)*100)+'%' }"></div>
            <div class="sc-bar-late"    :style="{ width: ((row.lateCount / row.totalWorkingDays)*100)+'%' }"></div>
            <div class="sc-bar-absent"  :style="{ width: ((row.absentCount / row.totalWorkingDays)*100)+'%' }"></div>
          </div>
          <div v-else class="sc-bar-track sc-no-data"></div>

          <!-- counts -->
          <div class="sc-counts">
            <div class="sc-count sc-p">
              <div class="scc-num">{{ row.presentCount }}</div>
              <div class="scc-lbl">Present</div>
            </div>
            <div class="sc-count sc-l">
              <div class="scc-num">{{ row.lateCount }}</div>
              <div class="scc-lbl">Late</div>
            </div>
            <div class="sc-count sc-a">
              <div class="scc-num">{{ row.absentCount }}</div>
              <div class="scc-lbl">Absent</div>
            </div>
            <div class="sc-count sc-h">
              <div class="scc-num">{{ row.halfDayCount }}</div>
              <div class="scc-lbl">Half</div>
            </div>
            <div class="sc-count sc-v">
              <div class="scc-num">{{ row.leaveCount }}</div>
              <div class="scc-lbl">Leave</div>
            </div>
            <div class="sc-count sc-t">
              <div class="scc-num">{{ row.totalWorkingDays }}</div>
              <div class="scc-lbl">Total</div>
            </div>
          </div>

          <div class="sc-bottom">
            <span class="status-badge" :class="statusBadgeClass(row.attendancePercentage)">
              {{ statusLabel(row.attendancePercentage) }}
            </span>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useDepartmentStore } from '../../stores/departments'
import { useCourseStore } from '../../stores/courses'
import { useReportStore } from '../../stores/reports'
import { reportService } from '../../services/academic'

const departmentStore = useDepartmentStore()
const courseStore = useCourseStore()
const reportStore = useReportStore()

const reportType = ref('weekly')
const selectedDepartment = ref('')
const selectedCourse = ref('')
const selectedWeek = ref(new Date().toISOString().split('T')[0])
const selectedMonth = ref(String(new Date().getMonth() + 1))
const selectedYear = ref(String(new Date().getFullYear()))
const selectedStudentYear = ref('1')
const selectedSemester = ref('1')
const sortBy = ref('pct-desc')
const searchQuery = ref('')
const selectedStudentIds = ref([])
const downloadLoading = ref(false)

onMounted(async () => {
  await Promise.all([departmentStore.fetchDepartments(), courseStore.fetchCourses()])
  await loadReport()
})

watch(selectedDepartment, async (value) => {
  selectedCourse.value = ''
  await courseStore.fetchCourses(value ? { departmentId: value } : {})
  await loadReport()
})

watch([reportType, selectedCourse, selectedWeek, selectedMonth, selectedYear, selectedStudentYear, selectedSemester], loadReport)
watch(
  () => reportStore.rows,
  (rows) => {
    const validIds = new Set(rows.map((row) => row.studentId))
    selectedStudentIds.value = selectedStudentIds.value.filter((id) => validIds.has(id))
  }
)

async function loadReport() {
  const common = baseFilters.value
  if (reportType.value === 'weekly') {
    await reportStore.fetchWeekly({ ...common, startDate: selectedWeek.value })
  } else if (reportType.value === 'monthly') {
    await reportStore.fetchMonthly({ ...common, year: selectedYear.value, month: selectedMonth.value })
  } else {
    await reportStore.fetchSemester({ ...common, year: selectedStudentYear.value || undefined, semester: selectedSemester.value })
  }
}

const totalDaysRecorded = computed(() => {
  return reportStore.rows.reduce((max, row) => Math.max(max, row.totalWorkingDays || 0), 0)
})

const filteredRows = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  if (!query) return reportStore.rows
  return reportStore.rows.filter((row) =>
    row.studentName.toLowerCase().includes(query) ||
    row.rollNumber.toLowerCase().includes(query) ||
    row.departmentName.toLowerCase().includes(query) ||
    row.courseName.toLowerCase().includes(query) ||
    (row.subjectName || '').toLowerCase().includes(query) ||
    (row.periodLabel || '').toLowerCase().includes(query) ||
    (row.sessionName || '').toLowerCase().includes(query) ||
    String(row.startTime || '').toLowerCase().includes(query) ||
    String(row.endTime || '').toLowerCase().includes(query)
  )
})

const sortedRows = computed(() => {
  const rows = [...filteredRows.value]
  switch (sortBy.value) {
    case 'name': return rows.sort((a, b) => a.studentName.localeCompare(b.studentName))
    case 'pct-asc': return rows.sort((a, b) => a.attendancePercentage - b.attendancePercentage)
    case 'absent-desc': return rows.sort((a, b) => b.absentCount - a.absentCount)
    case 'total-desc': return rows.sort((a, b) => b.totalWorkingDays - a.totalWorkingDays)
    default: return rows.sort((a, b) => b.attendancePercentage - a.attendancePercentage)
  }
})

const overallPct = computed(() => {
  if (!filteredRows.value.length) return null
  return Math.round(filteredRows.value.reduce((sum, row) => sum + row.attendancePercentage, 0) / filteredRows.value.length)
})

const atRiskCount = computed(() => filteredRows.value.filter((row) => row.attendancePercentage < 75).length)
const baseFilters = computed(() => ({
  departmentId: selectedDepartment.value || undefined,
  courseId: selectedCourse.value || undefined,
}))
const visibleStudentIds = computed(() => sortedRows.value.map((row) => row.studentId))
const selectedVisibleIds = computed(() => visibleStudentIds.value.filter((id) => selectedStudentIds.value.includes(id)))
const allVisibleSelected = computed(() => visibleStudentIds.value.length > 0 && selectedVisibleIds.value.length === visibleStudentIds.value.length)
const someVisibleSelected = computed(() => selectedVisibleIds.value.length > 0)
const selectedCountLabel = computed(() => {
  if (selectedStudentIds.value.length === 0) {
    return 'No students selected; download uses all filtered rows'
  }
  return `${selectedStudentIds.value.length} student${selectedStudentIds.value.length === 1 ? '' : 's'} selected`
})

function pctPillClass(pct) {
  if (pct >= 90) return 'pill-excellent'
  if (pct >= 75) return 'pill-good'
  if (pct >= 50) return 'pill-warning'
  return 'pill-risk'
}

function statusLabel(pct) {
  if (pct >= 90) return 'Excellent'
  if (pct >= 75) return 'Good'
  if (pct >= 50) return 'Warning'
  return 'At Risk'
}

function statusBadgeClass(pct) {
  if (pct >= 90) return 'badge-excellent'
  if (pct >= 75) return 'badge-good'
  if (pct >= 50) return 'badge-warning'
  return 'badge-risk'
}

function rowStatusClass(row) {
  if (row.attendancePercentage < 50) return 'row-risk'
  if (row.attendancePercentage < 75) return 'row-warn'
  return ''
}

function formatSessionTime(row) {
  const start = row.startTime || ''
  const end = row.endTime || ''
  if (start && end) return `${start}–${end}`
  if (start || end) return start || end
  return 'N/A'
}

function rowKey(row, index) {
  return [
    row.studentId ?? 'student',
    row.sessionDate ?? 'date',
    row.sessionName ?? 'session',
    row.subjectName ?? 'subject',
    row.startTime ?? 'start',
    row.endTime ?? 'end',
    index,
  ].join('-')
}

function isSelected(studentId) {
  return selectedStudentIds.value.includes(studentId)
}

function toggleRowSelection(studentId, checked) {
  const ids = new Set(selectedStudentIds.value)
  if (checked) {
    ids.add(studentId)
  } else {
    ids.delete(studentId)
  }
  selectedStudentIds.value = [...ids]
}

function toggleSelectAllVisible(checked) {
  const ids = new Set(selectedStudentIds.value)
  visibleStudentIds.value.forEach((studentId) => {
    if (checked) {
      ids.add(studentId)
    } else {
      ids.delete(studentId)
    }
  })
  selectedStudentIds.value = [...ids]
}

function buildFilters(type) {
  const filters = { ...baseFilters.value }
  if (type === 'weekly') {
    filters.startDate = selectedWeek.value
  } else if (type === 'monthly') {
    filters.year = selectedYear.value
    filters.month = selectedMonth.value
  } else {
    filters.year = selectedStudentYear.value || undefined
    filters.semester = selectedSemester.value
  }
  if (selectedStudentIds.value.length > 0) {
    filters.studentIds = selectedStudentIds.value
  }
  return filters
}

function exportFilename(type) {
  if (type === 'weekly') {
    return `attendance-weekly-${selectedWeek.value}.xlsx`
  }
  if (type === 'monthly') {
    return `attendance-monthly-${selectedYear.value}-${String(selectedMonth.value).padStart(2, '0')}.xlsx`
  }
  return `attendance-semester-year-${selectedStudentYear.value || 'all'}-sem-${selectedSemester.value}.xlsx`
}

async function downloadReport(type) {
  downloadLoading.value = true
  try {
    const filters = buildFilters(type)
    const blob = type === 'weekly'
      ? await reportService.weeklyExport(filters)
      : type === 'monthly'
        ? await reportService.monthlyExport(filters)
        : await reportService.semesterExport(filters)

    const url = URL.createObjectURL(blob)
    const anchor = document.createElement('a')
    anchor.href = url
    anchor.download = exportFilename(type)
    document.body.appendChild(anchor)
    anchor.click()
    anchor.remove()
    URL.revokeObjectURL(url)
  } finally {
    downloadLoading.value = false
  }
}
</script>

<style scoped>
/* ─── Page ────────────────────────────────────────────────────────── */
.report-page {
  padding: 28px 32px;
  max-width: 1300px;
  font-family: 'Inter', 'Outfit', system-ui, sans-serif;
}

/* ─── Header ──────────────────────────────────────────────────────── */
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 24px;
}

.header-left   { display: flex; align-items: center; gap: 14px; }

.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #0ea5e9, #38bdf8);
  border-radius: 14px;
  display: grid; place-items: center;
  color: #fff; flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(14,165,233,0.28);
}

.page-title    { font-size: 22px; font-weight: 700; color: #111827; margin: 0 0 2px; }
.page-subtitle { font-size: 13px; color: #6b7280; margin: 0; }

/* toolbar */
.toolbar {
  display: flex; align-items: flex-end; gap: 12px; flex-wrap: wrap;
}

.export-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 18px;
  flex-wrap: wrap;
}

.export-btn {
  height: 38px;
  padding: 0 14px;
  border: 1.5px solid #bfdbfe;
  border-radius: 10px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, transform 0.15s;
}

.export-btn:hover:not(:disabled) {
  background: #dbeafe;
  border-color: #93c5fd;
  transform: translateY(-1px);
}

.export-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.control-group { display: flex; flex-direction: column; gap: 4px; }

.control-label {
  font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.6px; color: #9ca3af;
}

.filter-select {
  height: 38px; padding: 0 12px;
  border: 1.5px solid #e5e7eb; border-radius: 9px;
  font-size: 13.5px; color: #111827; background: #fff;
  outline: none; cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.filter-select:focus {
  border-color: #0ea5e9;
  box-shadow: 0 0 0 3px rgba(14,165,233,0.12);
}

.search-wrap {
  position: relative;
  display: flex; align-items: center;
  margin-top: 18px;
}

.search-icon {
  position: absolute; left: 11px;
  color: #9ca3af; pointer-events: none;
}

.search-input {
  height: 38px; padding: 0 12px 0 34px;
  border: 1.5px solid #e5e7eb; border-radius: 9px;
  font-size: 13.5px; color: #111827; background: #fff;
  outline: none; width: 200px;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.search-input:focus {
  border-color: #0ea5e9;
  box-shadow: 0 0 0 3px rgba(14,165,233,0.12);
}

/* ─── Snapshot Cards ──────────────────────────────────────────────── */
.snapshot-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 14px;
  margin-bottom: 22px;
}

.snap-card {
  display: flex; align-items: center; gap: 14px;
  padding: 18px 20px; border-radius: 14px;
  background: #fff; border: 1.5px solid transparent;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  transition: transform 0.15s;
}

.snap-card:hover { transform: translateY(-2px); }

.snap-icon {
  width: 42px; height: 42px; border-radius: 11px;
  display: grid; place-items: center; flex-shrink: 0;
}

.snap-value { font-size: 26px; font-weight: 700; line-height: 1; }
.snap-label { font-size: 12px; color: #6b7280; margin-top: 3px; }

.snap-total   { border-color: #e5e7eb; }
.snap-total .snap-icon   { background: #f3f4f6; color: #6b7280; }
.snap-total .snap-value  { color: #111827; }

.snap-days    { border-color: #dbeafe; }
.snap-days .snap-icon    { background: #dbeafe; color: #2563eb; }
.snap-days .snap-value   { color: #2563eb; }

.snap-present { border-color: #d1fae5; }
.snap-present .snap-icon { background: #d1fae5; color: #059669; }
.snap-present .snap-value{ color: #059669; }

.snap-risk    { border-color: #fee2e2; }
.snap-risk .snap-icon    { background: #fee2e2; color: #dc2626; }
.snap-risk .snap-value   { color: #dc2626; }

/* ─── No Data ────────────────────────────────────────────────────── */
.no-data-card {
  display: flex; flex-direction: column; align-items: center;
  gap: 14px; padding: 60px 24px;
  background: #fff; border-radius: 16px;
  border: 1.5px dashed #e5e7eb;
  text-align: center;
}

.nd-title { font-size: 16px; font-weight: 700; color: #374151; }
.nd-sub   { font-size: 13.5px; color: #9ca3af; max-width: 380px; line-height: 1.6; }

/* ─── Table ──────────────────────────────────────────────────────── */
.table-card {
  background: #fff; border-radius: 16px;
  border: 1.5px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow: hidden; margin-bottom: 32px;
}

.table-wrap { overflow-x: auto; }

.report-table { width: 100%; border-collapse: collapse; font-size: 13.5px; }

.report-table thead {
  background: #fafafa; border-bottom: 1.5px solid #e5e7eb;
  position: sticky; top: 0; z-index: 1;
}

.report-table th {
  padding: 13px 14px; text-align: left;
  font-size: 11px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.7px;
  color: #9ca3af; white-space: nowrap;
}

.report-table td {
  padding: 13px 14px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.report-row { transition: background 0.15s; }
.report-row:last-child td { border-bottom: none; }
.report-row:hover { background: #fafafa; }

.th-select,
.td-select {
  width: 40px;
  text-align: center;
}

.row-checkbox {
  width: 16px;
  height: 16px;
  accent-color: #2563eb;
  cursor: pointer;
}

/* row risk tints */
.row-risk { background: rgba(239,68,68,0.03); }
.row-risk:hover { background: rgba(239,68,68,0.07); }
.row-warn { background: rgba(245,158,11,0.03); }
.row-warn:hover { background: rgba(245,158,11,0.07); }

.td-rank { color: #9ca3af; font-size: 13px; width: 36px; }

/* student info */
.student-info  { display: flex; align-items: center; gap: 11px; }
.student-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  display: grid; place-items: center;
  font-size: 12px; font-weight: 700; color: #fff; flex-shrink: 0;
}

.student-avatar.lg { width: 42px; height: 42px; font-size: 14px; }
.student-name  { font-weight: 600; color: #111827; font-size: 13.5px; }
.student-meta  { font-size: 11.5px; color: #9ca3af; margin-top: 1px; }

.avatar-0 { background: linear-gradient(135deg,#7c3aed,#a855f7); }
.avatar-1 { background: linear-gradient(135deg,#0ea5e9,#38bdf8); }
.avatar-2 { background: linear-gradient(135deg,#10b981,#34d399); }
.avatar-3 { background: linear-gradient(135deg,#f59e0b,#fbbf24); }
.avatar-4 { background: linear-gradient(135deg,#ef4444,#f87171); }
.avatar-5 { background: linear-gradient(135deg,#ec4899,#f472b6); }

.class-badge {
  background: #ede9fe; color: #6d28d9;
  font-size: 12px; font-weight: 600; padding: 3px 10px; border-radius: 6px;
}

.td-session {
  min-width: 220px;
}

.session-meta-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.session-subject {
  font-weight: 600;
  color: #111827;
}

.session-time {
  font-size: 11.5px;
  color: #9ca3af;
}

/* pct pill */
.pct-pill {
  display: inline-block;
  font-size: 13px; font-weight: 700;
  padding: 4px 12px; border-radius: 99px;
  white-space: nowrap;
}

.pct-pill.lg-pill { font-size: 12px; padding: 3px 10px; }

.pill-excellent { background: #d1fae5; color: #059669; }
.pill-good      { background: #dbeafe; color: #1d4ed8; }
.pill-warning   { background: #fef3c7; color: #d97706; }
.pill-risk      { background: #fee2e2; color: #dc2626; }
.pill-none      { background: #f3f4f6; color: #9ca3af; }

/* mini bar */
.mini-bar-track {
  height: 8px; background: #f3f4f6;
  border-radius: 99px; overflow: hidden; display: flex;
  min-width: 120px;
}

.mini-bar-present { background: #10b981; height: 100%; transition: width 0.4s ease; }
.mini-bar-late    { background: #f59e0b; height: 100%; transition: width 0.4s ease; }
.mini-bar-absent  { background: #ef4444; height: 100%; transition: width 0.4s ease; }
.bar-na           { font-size: 12px; color: #d1d5db; }

/* count chips */
.count-chip {
  display: inline-block; text-align: center;
  font-size: 14px; font-weight: 700;
  padding: 3px 10px; border-radius: 8px;
  min-width: 36px;
}

.present-chip { background: #d1fae5; color: #059669; }
.late-chip    { background: #fef3c7; color: #d97706; }
.absent-chip  { background: #fee2e2; color: #dc2626; }
.leave-chip   { background: #e0e7ff; color: #4f46e5; }
.halfday-chip { background: #fce7f3; color: #db2777; }

.total-days   { font-size: 14px; font-weight: 600; color: #374151; }

/* status badge */
.status-badge {
  display: inline-block;
  font-size: 11.5px; font-weight: 700;
  padding: 4px 11px; border-radius: 99px;
  white-space: nowrap;
}

.badge-excellent { background: #d1fae5; color: #059669; }
.badge-good      { background: #dbeafe; color: #1d4ed8; }
.badge-warning   { background: #fef3c7; color: #d97706; }
.badge-risk      { background: #fee2e2; color: #dc2626; }
.badge-none      { background: #f3f4f6; color: #9ca3af; }

.empty-row { text-align: center; padding: 40px; color: #9ca3af; font-size: 14px; }

/* table footer */
.table-footer {
  display: flex; align-items: center;
  justify-content: space-between; flex-wrap: wrap; gap: 12px;
  padding: 12px 18px;
  border-top: 1px solid #f3f4f6;
  background: #fafafa;
}

.tf-info { font-size: 12.5px; color: #9ca3af; }

.selection-summary {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  margin-right: 8px;
}

.tf-legend { display: flex; gap: 14px; flex-wrap: wrap; }

.tfl {
  font-size: 11.5px; font-weight: 600;
  display: flex; align-items: center; gap: 5px;
}

.tfl::before {
  content: ''; display: inline-block;
  width: 8px; height: 8px; border-radius: 50%;
}

.tfl-excellent::before { background: #10b981; } .tfl-excellent { color: #059669; }
.tfl-good::before      { background: #3b82f6; } .tfl-good      { color: #1d4ed8; }
.tfl-warning::before   { background: #f59e0b; } .tfl-warning   { color: #d97706; }
.tfl-risk::before      { background: #ef4444; } .tfl-risk      { color: #dc2626; }
.tfl-none::before      { background: #e5e7eb; } .tfl-none      { color: #9ca3af; }

/* ─── Cards Section ──────────────────────────────────────────────── */
.section-divider {
  display: flex; align-items: baseline; gap: 10px;
  margin-bottom: 16px;
}

.section-title { font-size: 17px; font-weight: 700; color: #111827; margin: 0; }
.section-sub   { font-size: 13px; color: #9ca3af; }

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.student-card {
  background: #fff; border-radius: 16px;
  border: 1.5px solid #e5e7eb;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  padding: 18px;
  display: flex; flex-direction: column; gap: 12px;
  transition: transform 0.15s, box-shadow 0.15s;
}

.student-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.09);
}

.student-card.row-risk { border-color: #fecaca; }
.student-card.row-warn { border-color: #fde68a; }

.sc-top  { display: flex; align-items: center; gap: 10px; }
.sc-info { flex: 1; overflow: hidden; }
.sc-name { font-size: 14px; font-weight: 700; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.sc-meta { font-size: 11.5px; color: #9ca3af; }

/* card progress bar */
.sc-bar-track {
  height: 6px; background: #f3f4f6; border-radius: 99px; overflow: hidden; display: flex;
}

.sc-no-data { background: #e5e7eb; }
.sc-bar-present { background: #10b981; height: 100%; transition: width 0.4s ease; }
.sc-bar-late    { background: #f59e0b; height: 100%; transition: width 0.4s ease; }
.sc-bar-absent  { background: #ef4444; height: 100%; transition: width 0.4s ease; }

/* counts row */
.sc-counts {
  display: grid; grid-template-columns: repeat(6, 1fr);
  gap: 6px;
}

.sc-count {
  display: flex; flex-direction: column; align-items: center;
  padding: 8px 4px; border-radius: 10px;
  background: #f9fafb;
}

.scc-num { font-size: 18px; font-weight: 700; line-height: 1; }
.scc-lbl { font-size: 10px; color: #9ca3af; margin-top: 3px; font-weight: 600; text-transform: uppercase; }

.sc-p .scc-num { color: #059669; }
.sc-l .scc-num { color: #d97706; }
.sc-a .scc-num { color: #dc2626; }
.sc-t .scc-num { color: #374151; }
.sc-h .scc-num { color: #db2777; }
.sc-v .scc-num { color: #4f46e5; }

.sc-p { background: #f0fdf4; }
.sc-l { background: #fffbeb; }
.sc-a { background: #fef2f2; }
.sc-t { background: #f3f4f6; }
.sc-h { background: #fdf2f8; }
.sc-v { background: #eef2ff; }

.sc-bottom { display: flex; justify-content: flex-end; }
</style>
