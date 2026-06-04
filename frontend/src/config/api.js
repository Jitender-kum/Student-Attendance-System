export const API_PREFIX = '/api'

export const API = {
  auth: {
    signup: `${API_PREFIX}/auth/signup`,
    login: `${API_PREFIX}/auth/login`,
  },
  departments: {
    list: `${API_PREFIX}/departments`,
    create: `${API_PREFIX}/departments`,
    update: (id) => `${API_PREFIX}/departments/${id}`,
    delete: (id) => `${API_PREFIX}/departments/${id}`,
  },
  courses: {
    list: `${API_PREFIX}/courses`,
    create: `${API_PREFIX}/courses`,
    update: (id) => `${API_PREFIX}/courses/${id}`,
    delete: (id) => `${API_PREFIX}/courses/${id}`,
  },
  subjects: {
    list: `${API_PREFIX}/subjects`,
    create: `${API_PREFIX}/subjects`,
    update: (id) => `${API_PREFIX}/subjects/${id}`,
    delete: (id) => `${API_PREFIX}/subjects/${id}`,
  },
  students: {
    list: `${API_PREFIX}/students`,
    create: `${API_PREFIX}/students`,
    update: (id) => `${API_PREFIX}/students/${id}`,
    delete: (id) => `${API_PREFIX}/students/${id}`,
  },
  attendance: {
    students: `${API_PREFIX}/attendance/students`,
    list: `${API_PREFIX}/attendance`,
    create: `${API_PREFIX}/attendance`,
    update: (id) => `${API_PREFIX}/attendance/${id}`,
    bulk: `${API_PREFIX}/attendance/bulk`,
    clear: `${API_PREFIX}/attendance/clear`,
    sessions: {
      list: `${API_PREFIX}/attendance/sessions`,
      create: `${API_PREFIX}/attendance/sessions`,
      range: `${API_PREFIX}/attendance/sessions/range`,
      detail: (id) => `${API_PREFIX}/attendance/sessions/${id}`,
      update: (id) => `${API_PREFIX}/attendance/sessions/${id}`,
      delete: (id) => `${API_PREFIX}/attendance/sessions/${id}`,
      entryUpdate: (sessionId, entryId) => `${API_PREFIX}/attendance/sessions/${sessionId}/entries/${entryId}`,
      bulkEntries: (sessionId) => `${API_PREFIX}/attendance/sessions/${sessionId}/entries/bulk`,
    },
  },
  assignments: {
    list: `${API_PREFIX}/assignments`,
    create: `${API_PREFIX}/assignments`,
    update: (id) => `${API_PREFIX}/assignments/${id}`,
    delete: (id) => `${API_PREFIX}/assignments/${id}`,
  },
  notes: {
    list: `${API_PREFIX}/notes`,
    create: `${API_PREFIX}/notes`,
    update: (id) => `${API_PREFIX}/notes/${id}`,
    delete: (id) => `${API_PREFIX}/notes/${id}`,
  },
  reports: {
    weekly: `${API_PREFIX}/reports/weekly`,
    weeklyExport: `${API_PREFIX}/reports/weekly/export`,
    monthly: `${API_PREFIX}/reports/monthly`,
    monthlyExport: `${API_PREFIX}/reports/monthly/export`,
    semester: `${API_PREFIX}/reports/semester`,
    semesterExport: `${API_PREFIX}/reports/semester/export`,
  },
}
