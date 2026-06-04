<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Departments</h1>
        <p>{{ store.departments.length }} departments for your workspace</p>
      </div>
      <button class="btn-add" @click="openModal()">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Add Department
      </button>
    </div>

    <div v-if="store.error" class="state-banner error-banner">{{ store.error }}</div>

    <div class="table-card">
      <div class="toolbar">
        <div class="search-bar">
          <input v-model="search" type="text" placeholder="Search departments…" />
        </div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredDepartments.length === 0">
              <td colspan="3" class="empty-row">No departments found.</td>
            </tr>
            <tr v-for="(department, index) in filteredDepartments" :key="department.id">
              <td>{{ index + 1 }}</td>
              <td>{{ department.name }}</td>
              <td class="actions">
                <button class="action-btn edit" @click="openModal(department)">Edit</button>
                <button class="action-btn delete" @click="deleteDepartment(department)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal modal-sm">
          <div class="modal-header">
            <h2>{{ editingDepartment ? 'Edit Department' : 'Add Department' }}</h2>
            <button class="modal-close" @click="closeModal">×</button>
          </div>
          <form class="modal-form" @submit.prevent="saveDepartment">
            <div class="field">
              <label>Name</label>
              <input v-model="form.name" type="text" placeholder="e.g. Computer Science" />
            </div>
            <div v-if="error" class="api-error">{{ error }}</div>
            <div class="modal-actions">
              <button type="submit" class="btn-primary">{{ editingDepartment ? 'Save Changes' : 'Add Department' }}</button>
              <button type="button" class="btn-secondary" @click="closeModal">Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useDepartmentStore } from '../../stores/departments'

const store = useDepartmentStore()
const search = ref('')
const showModal = ref(false)
const editingDepartment = ref(null)
const error = ref('')
const form = reactive({ name: '' })

onMounted(() => store.fetchDepartments())

const filteredDepartments = computed(() => {
  const query = search.value.trim().toLowerCase()
  if (!query) return store.departments
  return store.departments.filter((department) => department.name.toLowerCase().includes(query))
})

function openModal(department = null) {
  editingDepartment.value = department
  form.name = department?.name || ''
  error.value = ''
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function saveDepartment() {
  try {
    if (editingDepartment.value) {
      await store.updateDepartment(editingDepartment.value.id, { name: form.name.trim() })
    } else {
      await store.addDepartment({ name: form.name.trim() })
    }
    closeModal()
  } catch (e) {
    error.value = e.message
  }
}

async function deleteDepartment(department) {
  try {
    await store.deleteDepartment(department.id)
  } catch (e) {
    error.value = e.message
  }
}
</script>

<style scoped>
.page { padding: 4px 0; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; gap: 12px; }
.page-header h1 { margin: 0 0 4px; font-size: 24px; color: #111827; }
.page-header p { margin: 0; color: #6b7280; font-size: 13.5px; }
.btn-add, .btn-primary { display: inline-flex; align-items: center; gap: 7px; padding: 9px 18px; background: #7c3aed; color: white; border: none; border-radius: 9px; cursor: pointer; font-weight: 600; }
.btn-secondary { padding: 9px 18px; background: white; border: 1.5px solid #e5e7eb; border-radius: 9px; cursor: pointer; }
.table-card { background: #fff; border-radius: 14px; border: 1px solid #e5e7eb; overflow: hidden; box-shadow: 0 1px 6px rgba(0,0,0,0.06); }
.toolbar { padding: 14px 18px; border-bottom: 1px solid #f3f4f6; }
.search-bar input { width: 280px; max-width: 100%; padding: 10px 12px; border: 1.5px solid #e5e7eb; border-radius: 9px; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 14px 16px; text-align: left; border-bottom: 1px solid #f3f4f6; }
thead th { font-size: 11.5px; text-transform: uppercase; color: #6b7280; }
.actions { display: flex; gap: 8px; }
.action-btn { border: none; background: #f9fafb; border-radius: 8px; padding: 7px 10px; cursor: pointer; }
.action-btn.edit { color: #7c3aed; }
.action-btn.delete { color: #dc2626; }
.empty-row { text-align: center; color: #9ca3af; }
.state-banner { margin-bottom: 16px; padding: 12px 16px; border-radius: 10px; }
.error-banner { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; }
.modal-overlay { position: fixed; inset: 0; background: rgba(17,24,39,0.35); display: flex; align-items: center; justify-content: center; padding: 20px; }
.modal { background: #fff; border-radius: 16px; width: 100%; max-width: 420px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.18); }
.modal-sm { max-width: 420px; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px 16px; border-bottom: 1px solid #f3f4f6; }
.modal-form { padding: 20px 24px 24px; display: flex; flex-direction: column; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field input { padding: 10px 12px; border: 1.5px solid #e5e7eb; border-radius: 9px; }
.modal-actions { display: flex; gap: 10px; }
.modal-close { border: none; background: none; font-size: 22px; cursor: pointer; color: #9ca3af; }
.api-error { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; border-radius: 10px; padding: 11px 14px; font-size: 13px; }
</style>
