<template>
  <div class="login-page">

    <!-- Left Brand Panel -->
    <div class="brand-panel">
      <div class="brand-content">
        <div class="brand-logo">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect width="48" height="48" rx="14" fill="var(--accent)" />
            <path d="M14 34V18l10-6 10 6v16" stroke="white" stroke-width="2.5" stroke-linejoin="round" fill="none"/>
            <rect x="20" y="24" width="8" height="10" rx="1.5" fill="white" />
            <circle cx="24" cy="15" r="3" fill="white" />
          </svg>
          <span class="brand-name">AttendEase</span>
        </div>
        <h1 class="brand-headline">Track attendance,<br />the smart way.</h1>
        <p class="brand-sub">Manage student records, mark attendance, and generate reports — all in one place.</p>

        <div class="feature-list">
          <div class="feature-item" v-for="f in features" :key="f.label">
            <span class="feature-icon">{{ f.icon }}</span>
            <span>{{ f.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Right Login Form -->
    <div class="form-panel">
      <div class="form-card">
        <div class="form-header">
          <h2>Welcome back 👋</h2>
          <p>Sign in to your account to continue</p>
        </div>

        <!-- Demo Credentials Hint -->
        <!-- <div class="demo-hint">
          <span class="hint-badge">Demo</span>
          <span>Username: <strong>admin</strong> &nbsp;|&nbsp; Password: <strong>admin123</strong></span>
        </div> -->

        <form class="login-form" @submit.prevent="handleLogin" novalidate>

          <!-- Username -->
          <div class="field" :class="{ error: errors.username }">
            <label for="username">Username</label>
            <div class="input-wrap">
              <span class="input-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
              </span>
              <input
                id="username"
                v-model="form.username"
                type="text"
                placeholder="Enter your username"
                autocomplete="username"
                @input="clearError('username')"
              />
            </div>
            <span class="error-msg" v-if="errors.username">{{ errors.username }}</span>
          </div>

          <!-- Password -->
          <div class="field" :class="{ error: errors.password }">
            <label for="password">Password</label>
            <div class="input-wrap">
              <span class="input-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </span>
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Enter your password"
                autocomplete="current-password"
                @input="clearError('password')"
              />
              <button type="button" class="toggle-pw" @click="showPassword = !showPassword" tabindex="-1">
                <svg v-if="!showPassword" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </button>
            </div>
            <span class="error-msg" v-if="errors.password">{{ errors.password }}</span>
          </div>

          <!-- Global Error -->
          <div class="alert-error" v-if="loginError">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            {{ loginError }}
          </div>

          <!-- Submit -->
          <button type="submit" class="btn-login" :disabled="loading">
            <span v-if="!loading">Sign In</span>
            <span v-else class="spinner"></span>
          </button>

        </form>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// ── Dummy credentials (hardcoded) ──────────────────────────────────────────
const DUMMY_USER = { username: 'admin', password: 'admin123' }

// ── State ───────────────────────────────────────────────────────────────────
const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })
const loginError = ref('')
const loading = ref(false)
const showPassword = ref(false)

const features = [
  { icon: '📋', label: 'Mark daily attendance in seconds' },
  { icon: '📊', label: 'Visual reports & analytics' },
  { icon: '🔔', label: 'Instant absence notifications' },
  { icon: '🏫', label: 'Multi-class & multi-teacher support' },
]

// ── Actions ─────────────────────────────────────────────────────────────────
async function handleLogin() {
  // 1. Reset
  loginError.value = ''
  errors.username = ''
  errors.password = ''

  // 2. Validate
  if (!validate()) return

  // 3. Process
  loading.value = true
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 800))

  try {
    if (form.username === DUMMY_USER.username && form.password === DUMMY_USER.password) {
      // Success
      authStore.login({
        username: form.username,
        role: 'Administrator',
        avatar: 'A'
      })
      router.push('/app/home')
    } else {
      // Failed
      loginError.value = 'Invalid username or password. Please try again.'
    }
  } catch (err) {
    loginError.value = 'Something went wrong. Please try again later.'
  } finally {
    loading.value = false
  }
}

// ── Validation ──────────────────────────────────────────────────────────────
function validate() {
  let valid = true
  if (!form.username.trim()) {
    errors.username = 'Username is required.'
    valid = false
  }
  if (!form.password) {
    errors.password = 'Password is required.'
    valid = false
  }
  return valid
}

function clearError(field) {
  errors[field] = ''
  loginError.value = ''
}
</script>

<style scoped>
/* ── Layout ──────────────────────────────────────────────────────────────── */
.login-page {
  display: flex;
  min-height: 100vh;
  font-family: var(--sans, system-ui, sans-serif);
  background: #f9fafb;
}

/* ── Brand Panel ─────────────────────────────────────────────────────────── */
.brand-panel {
  flex: 1;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 48px;
  position: relative;
  overflow: hidden;
  border-right: 1px solid #e5e7eb;
}

.brand-panel::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.05) 0%, transparent 70%);
  top: -100px;
  right: -100px;
  pointer-events: none;
}

.brand-panel::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.03) 0%, transparent 70%);
  bottom: -60px;
  left: -60px;
  pointer-events: none;
}

.brand-content {
  position: relative;
  z-index: 1;
  max-width: 420px;
  color: #111827;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 48px;
}

.brand-name {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: #111827;
}

.brand-headline {
  font-size: 42px;
  font-weight: 700;
  line-height: 1.15;
  letter-spacing: -1px;
  margin: 0 0 20px;
  color: #111827;
}

.brand-sub {
  font-size: 16px;
  line-height: 1.6;
  color: #6b7280;
  margin-bottom: 44px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 15px;
  color: #4b5563;
  font-weight: 500;
}

.feature-icon {
  font-size: 18px;
  width: 36px;
  height: 36px;
  background: rgba(124, 58, 237, 0.08); /* subtle accent */
  border-radius: 10px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

/* ── Form Panel ──────────────────────────────────────────────────────────── */
.form-panel {
  width: 480px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9fafb;
  padding: 48px 40px;
}

.form-card {
  width: 100%;
  max-width: 380px;
}

.form-header {
  margin-bottom: 28px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
  letter-spacing: -0.5px;
  font-family: var(--heading, system-ui, sans-serif);
}

.form-header p {
  font-size: 15px;
  color: #6b7280;
}

/* ── Demo hint ───────────────────────────────────────────────────────────── */
/* .demo-hint { ... } (User commented this out earlier) */

/* ── Fields ──────────────────────────────────────────────────────────────── */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.field label {
  font-size: 13.5px;
  font-weight: 600;
  color: #374151;
  letter-spacing: 0.1px;
}

.input-wrap {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 13px;
  color: #9ca3af;
  display: flex;
  pointer-events: none;
}

.input-wrap input {
  width: 100%;
  padding: 11px 44px;
  font-size: 14.5px;
  border: 1.5px solid #e5e7eb;
  border-radius: 10px;
  background: white;
  color: #111827;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
  font-family: inherit;
}

.input-wrap input::placeholder {
  color: #9ca3af;
}

.input-wrap input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.12);
}

.field.error .input-wrap input {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.toggle-pw {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  cursor: pointer;
  color: #9ca3af;
  display: flex;
  padding: 4px;
  border-radius: 6px;
  transition: color 0.2s;
}

.toggle-pw:hover {
  color: #7c3aed;
}

.error-msg {
  font-size: 12.5px;
  color: #ef4444;
  margin-top: -2px;
}

/* ── Global alert ────────────────────────────────────────────────────────── */
.alert-error {
  display: flex;
  align-items: center;
  gap: 9px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 10px;
  padding: 11px 14px;
  font-size: 13.5px;
  color: #b91c1c;
}

/* ── Submit button ───────────────────────────────────────────────────────── */
.btn-login {
  margin-top: 4px;
  padding: 13px;
  background: #7c3aed;
  color: white;
  font-size: 15px;
  font-weight: 600;
  border: none;
  border-radius: 11px;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.15s, box-shadow 0.2s, background 0.15s;
  letter-spacing: 0.2px;
  box-shadow: 0 4px 14px rgba(124, 58, 237, 0.25);
  font-family: inherit;
}

.btn-login:hover:not(:disabled) {
  background: #6d28d9;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(124, 58, 237, 0.35);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* ── Spinner ─────────────────────────────────────────────────────────────── */
.spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2.5px solid rgba(255, 255, 255, 0.4);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ── Responsive ──────────────────────────────────────────────────────────── */
@media (max-width: 860px) {
  .login-page {
    flex-direction: column;
  }

  .brand-panel {
    padding: 40px 28px;
    border-right: none;
    border-bottom: 1px solid #e5e7eb;
  }

  .brand-headline {
    font-size: 30px;
  }

  .form-panel {
    width: 100%;
    box-sizing: border-box;
    padding: 40px 24px;
  }
}
</style>
 

