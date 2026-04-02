/**
 * Central API configuration
 * ─────────────────────────────────────────────────────────────────────
 * Vite proxies all  /api/*  requests → http://localhost:8081/*
 * So we never hit cross-origin issues in the browser.
 *
 * To change the backend port: update BACKEND_PORT in vite.config.js
 * (the proxy target) — no other file needs to change.
 *
 * Backend controller: @RequestMapping("/student")
 *   POST /student/add     → @RequestBody Student
 *   POST /student/update  → @RequestBody Student  (id included in body)
 *   GET  /student/list
 *   DELETE /student/delete/{id}  (if applicable)
 */
export const API_PREFIX = '/api'

export const API = {
  student: {
    list:   `${API_PREFIX}/student/list`,
    add:    `${API_PREFIX}/student/add`,
    update: `${API_PREFIX}/student/update`,
    delete: (id) => `${API_PREFIX}/student/delete/${id}`,
  },
  attendance: {
    getByDate: (date) => `${API_PREFIX}/attendance/get_student_attendance?date=${date}`,
    mark:      `${API_PREFIX}/attendance/mark`,
  },
}
