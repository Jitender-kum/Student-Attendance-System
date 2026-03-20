<template>
  <div class="page">
    <div class="page-header">
      <h1>Employees</h1>
      <p>Manage your staff — teachers, coordinators, and administrators.</p>
    </div>

    <div class="coming-soon">
      <div class="icon-wrap">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#aa3bff" stroke-width="1.5">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>
      </div>
      <h2>Employee features coming soon</h2>
      <p>This section will include employee management — add, edit, and track staff attendance.</p>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.page { padding: 4px 0; }
.page-header { margin-bottom: 28px; }
.page-header h1 { font-size: 26px; font-weight: 700; color: #111827; margin: 0 0 6px; }
.page-header p { font-size: 14px; color: #6b7280; margin: 0; }

.coming-soon {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  padding: 60px 32px;
  text-align: center;
  display: flex; flex-direction: column; align-items: center; gap: 14px;
  max-width: 480px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06);
}

.icon-wrap {
  width: 72px; height: 72px;
  background: rgba(124, 58, 237, 0.08); /* subtle accent */
  border-radius: 18px;
  display: grid; place-items: center;
}

.coming-soon h2 {
  font-size: 19px; font-weight: 700; color: #111827; margin: 0;
}
.coming-soon p { font-size: 14px; color: #6b7280; margin: 0; line-height: 1.6; }
</style>
