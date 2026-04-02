import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // Public
  {
    path: '/',
    name: 'Login',
    component: () => import('../Login.vue'),
  },

  // Authenticated — wrapped in MainLayout (sidebar)
  {
    path: '/app',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: '/app/home' },

      // Student
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


    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Authentication Guard
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))
  const isAuthenticated = !!user

  // If trying to access /app but NOT logged in -> redirect to /
  if (to.path.startsWith('/app') && !isAuthenticated) {
    next('/')
  }
  // If trying to access / (login) but IS logged in -> redirect to /app/home
  else if (to.path === '/' && isAuthenticated) {
    next('/app/home')
  }
  else {
    next()
  }
})

export default router
