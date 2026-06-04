<template>
  <aside class="sidebar" :class="{ collapsed }">

    <!-- Logo / Brand -->
    <div class="sidebar-header">
      <div class="sb-logo">
        <svg width="32" height="32" viewBox="0 0 48 48" fill="none">
          <rect width="48" height="48" rx="12" fill="var(--accent, #aa3bff)" />
          <path d="M14 34V18l10-6 10 6v16" stroke="white" stroke-width="2.5" stroke-linejoin="round" fill="none"/>
          <rect x="20" y="24" width="8" height="10" rx="1.5" fill="white" />
          <circle cx="24" cy="15" r="3" fill="white" />
        </svg>
        <span class="sb-brand" v-show="!collapsed">AttendEase</span>
      </div>
      <button class="collapse-btn" @click="collapsed = !collapsed" :title="collapsed ? 'Expand' : 'Collapse'">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
          <path v-if="!collapsed" d="M15 18l-6-6 6-6" />
          <path v-else d="M9 18l6-6-6-6" />
        </svg>
      </button>
    </div>

    <!-- Navigation -->
    <nav class="sb-nav">

      <!-- ── Student Section ──────────────────────────────────── -->
      <div class="sb-section">
        <div class="sb-section-label" v-show="!collapsed">Student</div>
        <div class="sb-divider" v-show="collapsed"></div>

        <router-link v-for="item in studentNav" :key="item.to"
          :to="item.to" class="sb-item" :title="collapsed ? item.label : ''"
          active-class="active">
          <span class="sb-icon" v-html="item.icon"></span>
          <span class="sb-label" v-show="!collapsed">{{ item.label }}</span>
        </router-link>
      </div>



    </nav>

    <!-- Footer / User info -->
    <div class="sb-footer">
      <!-- Expanded: user card + logout row -->
      <div class="sb-user" v-show="!collapsed">
        <div class="sb-avatar">{{ teacherInitials }}</div>
        <div class="sb-user-info">
          <span class="sb-user-name">{{ teacher?.fullName || 'Teacher' }}</span>
          <span class="sb-user-role">{{ teacher?.email || 'Authenticated teacher' }}</span>
        </div>
        <button class="logout-btn" @click="logout" title="Logout">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </button>
      </div>

      <!-- Collapsed: avatar + logout icon stacked -->
      <div class="collapsed-footer" v-show="collapsed">
        <div class="sb-avatar centered">{{ teacherInitials }}</div>
        <button class="logout-btn-collapsed" @click="logout" title="Logout">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </button>
      </div>
    </div>

  </aside>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const collapsed = ref(false)

function logout() {
  authStore.logout()
  router.push('/login')
}

const teacher = computed(() => authStore.teacher)
const teacherInitials = computed(() => {
  const fullName = teacher.value?.fullName || 'Teacher'
  return fullName
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() || '')
    .join('')
})

const studentNav = [
  {
    label: 'Home',
    to: '/app/home',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`,
  },
  {
    label: 'Student List',
    to: '/app/students/list',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>`,
  },
  {
    label: 'Departments',
    to: '/app/departments',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 21h18"/><path d="M5 21V7l7-4 7 4v14"/><path d="M9 9h.01"/><path d="M9 13h.01"/><path d="M9 17h.01"/><path d="M15 9h.01"/><path d="M15 13h.01"/><path d="M15 17h.01"/></svg>`,
  },
  {
    label: 'Courses',
    to: '/app/courses',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 7l10-4 10 4-10 4-10-4z"/><path d="M5 10.5v5l7 3 7-3v-5"/><path d="M19 8v8"/></svg>`,
  },
  {
    label: 'Subjects',
    to: '/app/subjects',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5z"/><line x1="8" y1="7" x2="16" y2="7"/><line x1="8" y1="11" x2="16" y2="11"/></svg>`,
  },
  {
    label: 'Attendance',
    to: '/app/students/attendance',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>`,
  },
  {
    label: 'Assignments',
    to: '/app/assignments',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h9"/><path d="M16 3h5v5"/></svg>`,
  },
  {
    label: 'Notes',
    to: '/app/notes',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5V4a2 2 0 0 1 2-2h8.5L20 7.5V19.5a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2z"/><polyline points="14 2 14 8 20 8"/><line x1="8" y1="13" x2="16" y2="13"/><line x1="8" y1="17" x2="13" y2="17"/></svg>`,
  },
  {
    label: 'Att. Report',
    to: '/app/students/report',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>`,
  },
]


</script>

<style scoped>
/* ── Shell ─────────────────────────────────────────────────────────────── */
.sidebar {
  width: 240px;
  min-height: 100vh;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: width 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.sidebar.collapsed {
  width: 64px;
}

/* ── Header ────────────────────────────────────────────────────────────── */
.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 14px 14px;
  border-bottom: 1px solid #e5e7eb;
}

.sb-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
}

.sb-brand {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
  white-space: nowrap;
  letter-spacing: -0.3px;
}

.collapse-btn {
  background: none;
  border: 1px solid #e5e7eb;
  border-radius: 7px;
  padding: 5px;
  cursor: pointer;
  color: #9ca3af;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
}

.collapse-btn:hover {
  background: #f9fafb;
  color: #111827;
  border-color: #d1d5db;
}

/* ── Navigation ────────────────────────────────────────────────────────── */
.sb-nav {
  flex: 1;
  padding: 10px 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  overflow-y: auto;
  overflow-x: hidden;
}

.sb-section {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-bottom: 8px;
}

.sb-section-label {
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: 0.8px;
  text-transform: uppercase;
  color: #9ca3af;
  padding: 10px 10px 4px;
  white-space: nowrap;
}

.sb-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 8px 4px;
}

/* ── Nav item ──────────────────────────────────────────────────────────── */
.sb-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 9px;
  text-decoration: none;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  transition: background 0.15s, color 0.15s;
}

.sb-item:hover {
  background: #f9fafb;
  color: #111827;
}

.sb-item.active {
  background: rgba(124, 58, 237, 0.08); /* subtle accent */
  color: #7c3aed;
  font-weight: 600;
}

.sb-icon {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  color: inherit;
}

.sb-label {
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ── Footer ────────────────────────────────────────────────────────────── */
.sb-footer {
  padding: 12px 10px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sb-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 6px;
  border-radius: 9px;
  background: #f4f2fa;
}

.sb-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #aa3bff, #7c3aed);
  color: white;
  font-size: 13px;
  font-weight: 700;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

.sb-avatar.centered {
  margin: 0 auto;
}

.sb-user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sb-user-name {
  font-size: 13px;
  font-weight: 600;
  color: #1a0a3a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sb-user-role {
  font-size: 11.5px;
  color: #9b93a5;
  white-space: nowrap;
}

/* Logout button — expanded mode */
.logout-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #b0a8be;
  display: flex;
  align-items: center;
  padding: 5px;
  border-radius: 7px;
  flex-shrink: 0;
  transition: color 0.15s, background 0.15s;
}

.logout-btn:hover {
  color: #e53e3e;
  background: rgba(229, 62, 62, 0.08);
}

/* Collapsed footer stack */
.collapsed-footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

/* Logout button — collapsed mode */
.logout-btn-collapsed {
  background: none;
  border: none;
  cursor: pointer;
  color: #b0a8be;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 7px;
  border-radius: 8px;
  width: 34px;
  height: 34px;
  transition: color 0.15s, background 0.15s;
}

.logout-btn-collapsed:hover {
  color: #e53e3e;
  background: rgba(229, 62, 62, 0.08);
}
</style>
