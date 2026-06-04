import { API } from '../config/api'
import { apiRequest } from './http'

function withQuery(url, params = {}) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      value.forEach((item) => {
        if (item !== null && item !== undefined && item !== '') {
          search.append(key, item)
        }
      })
    } else if (value !== null && value !== undefined && value !== '') {
      search.append(key, value)
    }
  })
  const query = search.toString()
  return query ? `${url}?${query}` : url
}

export const departmentService = {
  list() {
    return apiRequest(API.departments.list)
  },
  create(payload) {
    return apiRequest(API.departments.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  update(id, payload) {
    return apiRequest(API.departments.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  delete(id) {
    return apiRequest(API.departments.delete(id), { method: 'DELETE' })
  },
}

export const courseService = {
  list(filters = {}) {
    return apiRequest(withQuery(API.courses.list, filters))
  },
  create(payload) {
    return apiRequest(API.courses.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  update(id, payload) {
    return apiRequest(API.courses.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  delete(id) {
    return apiRequest(API.courses.delete(id), { method: 'DELETE' })
  },
}

export const subjectService = {
  list(filters = {}) {
    return apiRequest(withQuery(API.subjects.list, filters))
  },
  create(payload) {
    return apiRequest(API.subjects.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  update(id, payload) {
    return apiRequest(API.subjects.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  delete(id) {
    return apiRequest(API.subjects.delete(id), { method: 'DELETE' })
  },
}

export const studentService = {
  list(filters = {}) {
    return apiRequest(withQuery(API.students.list, filters))
  },
  create(payload) {
    return apiRequest(API.students.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  update(id, payload) {
    return apiRequest(API.students.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  delete(id) {
    return apiRequest(API.students.delete(id), { method: 'DELETE' })
  },
}

export const attendanceService = {
  getStudents(filters = {}) {
    return apiRequest(withQuery(API.attendance.students, filters))
  },
  list(filters = {}) {
    return apiRequest(withQuery(API.attendance.list, filters))
  },
  create(payload) {
    return apiRequest(API.attendance.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  update(id, payload) {
    return apiRequest(API.attendance.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  bulk(payload) {
    return apiRequest(API.attendance.bulk, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  clear(payload) {
    return apiRequest(API.attendance.clear, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  listSessions(filters = {}) {
    return apiRequest(withQuery(API.attendance.sessions.list, filters))
  },
  listSessionsByRange(filters = {}) {
    return apiRequest(withQuery(API.attendance.sessions.range, filters))
  },
  getSession(id) {
    return apiRequest(API.attendance.sessions.detail(id))
  },
  createSession(payload) {
    return apiRequest(API.attendance.sessions.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  updateSession(id, payload) {
    return apiRequest(API.attendance.sessions.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  updateSessionEntry(sessionId, entryId, payload) {
    return apiRequest(API.attendance.sessions.entryUpdate(sessionId, entryId), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  bulkUpsertSessionEntries(sessionId, payload) {
    return apiRequest(API.attendance.sessions.bulkEntries(sessionId), {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  deleteSession(id) {
    return apiRequest(API.attendance.sessions.delete(id), { method: 'DELETE' })
  },
}

export const assignmentService = {
  list(filters = {}) {
    return apiRequest(withQuery(API.assignments.list, filters))
  },
  create(payload) {
    return apiRequest(API.assignments.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  update(id, payload) {
    return apiRequest(API.assignments.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  delete(id) {
    return apiRequest(API.assignments.delete(id), { method: 'DELETE' })
  },
}

export const noteService = {
  list(filters = {}) {
    return apiRequest(withQuery(API.notes.list, filters))
  },
  create(payload) {
    return apiRequest(API.notes.create, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  update(id, payload) {
    return apiRequest(API.notes.update(id), {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  delete(id) {
    return apiRequest(API.notes.delete(id), { method: 'DELETE' })
  },
}

export const reportService = {
  weekly(filters = {}) {
    return apiRequest(withQuery(API.reports.weekly, filters))
  },
  monthly(filters = {}) {
    return apiRequest(withQuery(API.reports.monthly, filters))
  },
  semester(filters = {}) {
    return apiRequest(withQuery(API.reports.semester, filters))
  },
  weeklyExport(filters = {}) {
    return apiRequest(withQuery(API.reports.weeklyExport, filters), {
      responseType: 'blob',
    })
  },
  monthlyExport(filters = {}) {
    return apiRequest(withQuery(API.reports.monthlyExport, filters), {
      responseType: 'blob',
    })
  },
  semesterExport(filters = {}) {
    return apiRequest(withQuery(API.reports.semesterExport, filters), {
      responseType: 'blob',
    })
  },
}
