<template>
  <div class="page">
    <div class="page-header">
      <h1>Edit Student</h1>
      <p>Search for a student and update their information.</p>
    </div>

    <div class="form-card">
      <div class="search-bar">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#9b93a5" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input type="text" placeholder="Search by name or roll number..." v-model="search" />
      </div>

      <div class="placeholder" v-if="!search">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#d0ccd8" stroke-width="1.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <p>Search for a student to edit their details</p>
      </div>

      <form @submit.prevent v-else>
        <div class="form-row">
          <div class="field">
            <label>First Name</label>
            <input type="text" placeholder="First name" />
          </div>
          <div class="field">
            <label>Last Name</label>
            <input type="text" placeholder="Last name" />
          </div>
        </div>
        <div class="form-row">
          <div class="field">
            <label>Roll Number</label>
            <input type="text" placeholder="Roll number" />
          </div>
          <div class="field">
            <label>Class / Section</label>
            <input type="text" placeholder="Class" />
          </div>
        </div>
        <div class="field">
          <label>Email</label>
          <input type="email" placeholder="Email" />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn-primary">Save Changes</button>
          <button type="button" class="btn-secondary">Cancel</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const search = ref('')
</script>

<style scoped>
.page { padding: 4px 0; }
.page-header { margin-bottom: 28px; }
.page-header h1 { font-size: 26px; font-weight: 700; color: #111827; margin: 0 0 6px; }
.page-header p { font-size: 14px; color: #6b7280; margin: 0; }

.form-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px 28px;
  border: 1px solid #e5e7eb;
  max-width: 600px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1.5px solid #e5e7eb;
  border-radius: 9px;
  padding: 9px 13px;
  margin-bottom: 24px;
}
.search-bar input {
  border: none; outline: none; font-size: 14px;
  color: #111827; width: 100%; font-family: inherit;
}
.search-bar input::placeholder { color: #9ca3af; }

.placeholder {
  text-align: center; padding: 40px 20px;
  display: flex; flex-direction: column; align-items: center; gap: 12px;
}
.placeholder p { font-size: 14px; color: #9ca3af; margin: 0; }

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.field label { font-size: 13px; font-weight: 600; color: #374151; }
.field input {
  padding: 10px 13px; border: 1.5px solid #e5e7eb;
  border-radius: 9px; font-size: 14px; outline: none;
  color: #111827; font-family: inherit;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.field input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124,58,237,0.1);
}
.field input::placeholder { color: #9ca3af; }

.form-actions { display: flex; gap: 12px; margin-top: 8px; }
.btn-primary {
  padding: 10px 24px; background: #7c3aed;
  color: white; font-size: 14px; font-weight: 600;
  border: none; border-radius: 9px; cursor: pointer;
  box-shadow: 0 2px 8px rgba(124,58,237,0.28);
  transition: opacity 0.2s, background 0.15s, transform 0.15s; font-family: inherit;
}
.btn-primary:hover { background: #6d28d9; transform: translateY(-1px); }
.btn-secondary {
  padding: 10px 20px; background: white; color: #4b5563;
  font-size: 14px; font-weight: 500;
  border: 1.5px solid #e5e7eb; border-radius: 9px;
  cursor: pointer; transition: background 0.15s; font-family: inherit;
}
.btn-secondary:hover { background: #f9fafb; }
</style>
