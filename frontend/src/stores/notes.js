import { defineStore } from 'pinia'
import { ref } from 'vue'
import { noteService } from '../services/academic'

function normalizeNote(raw = {}) {
  return {
    ...raw,
    title: raw.title || '',
    content: raw.content || '',
    departmentName: raw.departmentName || '',
    courseName: raw.courseName || '',
    noteDate: raw.noteDate || '',
  }
}

export const useNoteStore = defineStore('notes', () => {
  const notes = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchNotes(filters = {}) {
    loading.value = true
    error.value = ''
    try {
      const data = await noteService.list(filters)
      notes.value = Array.isArray(data) ? data.map(normalizeNote) : []
      return notes.value
    } catch (e) {
      error.value = e.message
      return []
    } finally {
      loading.value = false
    }
  }

  async function addNote(payload) {
    const created = normalizeNote(await noteService.create(payload))
    notes.value.unshift(created)
    return created
  }

  async function updateNote(id, payload) {
    const updated = normalizeNote(await noteService.update(id, payload))
    const index = notes.value.findIndex((note) => note.id === id)
    if (index !== -1) {
      notes.value[index] = updated
    }
    return updated
  }

  async function deleteNote(id) {
    await noteService.delete(id)
    notes.value = notes.value.filter((note) => note.id !== id)
  }

  return {
    notes,
    loading,
    error,
    fetchNotes,
    addNote,
    updateNote,
    deleteNote,
  }
})
