import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../Login.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/signup',
    name: 'Signup',
    component: () => import('../views/auth/SignupView.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/app',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/app/home' },
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'students/add',
        name: 'AddStudent',
        component: () => import('../views/students/AddStudent.vue'),
      },
      {
        path: 'students/edit',
        name: 'EditStudent',
        component: () => import('../views/students/EditStudent.vue'),
      },
      {
        path: 'students/list',
        name: 'StudentList',
        component: () => import('../views/students/StudentList.vue'),
      },
      {
        path: 'students/attendance',
        name: 'StudentAttendance',
        component: () => import('../views/students/StudentAttendance.vue'),
      },
      {
        path: 'students/report',
        name: 'AttendanceReport',
        component: () => import('../views/students/AttendanceReport.vue'),
      },
      {
        path: 'assignments',
        name: 'Assignments',
        component: () => import('../views/assignments/AssignmentList.vue'),
      },
      {
        path: 'notes',
        name: 'Notes',
        component: () => import('../views/notes/NoteList.vue'),
      },
      {
        path: 'departments',
        name: 'Departments',
        component: () => import('../views/departments/DepartmentList.vue'),
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('../views/courses/CourseList.vue'),
      },
      {
        path: 'subjects',
        name: 'Subjects',
        component: () => import('../views/subjects/SubjectList.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  if (to.meta.guestOnly && authStore.isAuthenticated) {
    next('/app/home')
    return
  }

  next()
})

export default router
