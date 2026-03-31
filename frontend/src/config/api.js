/**
 * Central API configuration
 * ─────────────────────────────────────────────────────────────────────
 * Vite proxies all  /api/*  requests → http://localhost:8081/*
 * So we never hit cross-origin issues in the browser.
 *
 * To change the backend port: update BACKEND_PORT in vite.config.js
 * (the proxy target) — no other file needs to change.
 */
export const API_PREFIX = '/api'

export const API = {
  student: {
    list:   `${API_PREFIX}/student/list`,
    add:    `${API_PREFIX}/student/add`,
    update: (id) => `${API_PREFIX}/student/update/${id}`,
    delete: (id) => `${API_PREFIX}/student/delete/${id}`,
  },
}
