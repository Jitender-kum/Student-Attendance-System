<template>
  <div class="page">
    <div class="page-header">
      <h1>Dashboard</h1>
      <p>Welcome back, {{ teacherName }}! Here's an overview of the attendance system.</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-icon" :style="{ background: s.color }">
          <span v-html="s.icon"></span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useStudentStore } from '../stores/students'
import { useAttendanceStore } from '../stores/attendance'
import { useAuthStore } from '../stores/auth'

const studentStore = useStudentStore()
const attStore = useAttendanceStore()
const authStore = useAuthStore()

const today = new Date().toISOString().split('T')[0]

onMounted(async () => {
  // Fetch students first
  await studentStore.fetchStudents()
  // Fetch today's attendance
  await attStore.fetchByDate(today)
})

const teacherName = computed(() => authStore.teacher?.fullName || 'Teacher')

const summary = computed(() => {
  return attStore.getDateSummary(today, studentStore.students)
})

const stats = computed(() => [
  {
    label: 'Total Students',
    value: summary.value.total,
    color: 'rgba(170,59,255,0.12)',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#aa3bff" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
  },
  {
    label: 'Present Today',
    value: summary.value.present,
    color: 'rgba(16,185,129,0.12)',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>`,
  },
  {
    label: 'Absent Today',
    value: summary.value.absent,
    color: 'rgba(239,68,68,0.12)',
    icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`,
  },

])
</script>

<style scoped>
.page { padding: 4px 0; }

.page-header { margin-bottom: 32px; }
.page-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
}
.page-header p { font-size: 14px; color: #6b7280; margin: 0; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06);
}

.stat-icon {
  width: 50px; height: 50px;
  border-radius: 12px;
  display: grid; place-items: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #111827;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label { font-size: 13px; color: #6b7280; }
</style>
