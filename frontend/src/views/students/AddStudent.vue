<template>
  <div class="page">
    <div class="page-header">
      <h1>Add Student</h1>
      <p>Fill in the details below to register a new student.</p>
    </div>

    <div class="form-card">
      <form @submit.prevent>
        <div class="form-row">
          <div class="field">
            <label>First Name</label>
            <input type="text" placeholder="e.g. John" />
          </div>
          <div class="field">
            <label>Last Name</label>
            <input type="text" placeholder="e.g. Doe" />
          </div>
        </div>
        <div class="form-row">
          <div class="field">
            <label>Roll Number</label>
            <input type="text" placeholder="e.g. S001" />
          </div>
          <div class="field">
            <label>Class / Section</label>
            <input type="text" placeholder="e.g. 10-A" />
          </div>
        </div>
        <div class="field">
          <label>Email</label>
          <input type="email" placeholder="student@example.com" />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn-primary">Add Student</button>
          <button type="button" class="btn-secondary">Clear</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.page { padding: 4px 0; }
.page-header { margin-bottom: 28px; }
.page-header h1 { font-size: 26px; font-weight: 700; color: #111827; margin: 0 0 6px; }
.page-header p { font-size: 14px; color: #6b7280; margin: 0; }

.form-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 28px;
  border: 1px solid #e5e7eb;
  max-width: 600px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06);
}

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.field label { font-size: 13px; font-weight: 600; color: #374151; }
.field input {
  padding: 10px 13px;
  border: 1.5px solid #e5e7eb;
  border-radius: 9px;
  font-size: 14px;
  outline: none;
  color: #111827;
  transition: border-color 0.2s, box-shadow 0.2s;
  font-family: inherit;
}
.field input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124,58,237,0.1);
}
.field input::placeholder { color: #9ca3af; }

.form-actions { display: flex; gap: 12px; margin-top: 8px; }

.btn-primary {
  padding: 10px 24px;
  background: #7c3aed;
  color: white; font-size: 14px; font-weight: 600;
  border: none; border-radius: 9px; cursor: pointer;
  box-shadow: 0 2px 8px rgba(124,58,237,0.28);
  transition: opacity 0.2s, background 0.15s, transform 0.15s;
  font-family: inherit;
}
.btn-primary:hover { background: #6d28d9; transform: translateY(-1px); }

.btn-secondary {
  padding: 10px 20px;
  background: white; color: #4b5563;
  font-size: 14px; font-weight: 500;
  border: 1.5px solid #e5e7eb; border-radius: 9px;
  cursor: pointer; transition: background 0.15s;
  font-family: inherit;
}
.btn-secondary:hover { background: #f9fafb; }
</style>
