<template>
  <div class="login-page">
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
        <h1 class="brand-headline">Create your teacher workspace.</h1>
        <p class="brand-sub">Set up your departments, courses, students, and reports in one secure dashboard.</p>
      </div>
    </div>

    <div class="form-panel">
      <div class="form-card">
        <div class="form-header">
          <h2>Teacher Signup</h2>
          <p>Create your account to start managing attendance</p>
        </div>

        <form class="login-form" @submit.prevent="handleSignup" novalidate>
          <div class="field" :class="{ error: errors.fullName }">
            <label for="fullName">Full name</label>
            <div class="input-wrap">
              <span class="input-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
              </span>
              <input id="fullName" v-model="form.fullName" type="text" placeholder="Enter your full name" @input="clearError('fullName')" />
            </div>
            <span class="error-msg" v-if="errors.fullName">{{ errors.fullName }}</span>
          </div>

          <div class="field" :class="{ error: errors.email }">
            <label for="email">Email</label>
            <div class="input-wrap">
              <span class="input-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16v16H4z"/><path d="m4 7 8 6 8-6"/></svg>
              </span>
              <input id="email" v-model="form.email" type="email" placeholder="Enter your email" @input="clearError('email')" />
            </div>
            <span class="error-msg" v-if="errors.email">{{ errors.email }}</span>
          </div>

          <div class="field" :class="{ error: errors.password }">
            <label for="password">Password</label>
            <div class="input-wrap">
              <span class="input-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </span>
              <input id="password" v-model="form.password" :type="showPassword ? 'text' : 'password'" placeholder="Create a password" @input="clearError('password')" />
            </div>
            <span class="error-msg" v-if="errors.password">{{ errors.password }}</span>
          </div>

          <div class="field" :class="{ error: errors.confirmPassword }">
            <label for="confirmPassword">Confirm password</label>
            <div class="input-wrap">
              <span class="input-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </span>
              <input id="confirmPassword" v-model="form.confirmPassword" :type="showPassword ? 'text' : 'password'" placeholder="Confirm your password" @input="clearError('confirmPassword')" />
              <button type="button" class="toggle-pw" @click="showPassword = !showPassword" tabindex="-1">
                <svg v-if="!showPassword" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </button>
            </div>
            <span class="error-msg" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
          </div>

          <div class="alert-error" v-if="signupError">{{ signupError }}</div>

          <button type="submit" class="btn-login" :disabled="loading">
            <span v-if="!loading">Create Account</span>
            <span v-else class="spinner"></span>
          </button>

          <p class="switch-auth">
            Already have an account?
            <RouterLink to="/login">Sign in</RouterLink>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { authService } from '../../services/auth'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const errors = reactive({
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const loading = ref(false)
const signupError = ref('')
const showPassword = ref(false)

function clearError(field) {
  errors[field] = ''
  signupError.value = ''
}

function validate() {
  let valid = true
  errors.fullName = form.fullName.trim() ? '' : 'Full name is required.'
  errors.email = /\S+@\S+\.\S+/.test(form.email) ? '' : 'Enter a valid email address.'
  errors.password = form.password.length >= 8 ? '' : 'Password must be at least 8 characters.'
  errors.confirmPassword = form.confirmPassword === form.password ? '' : 'Passwords do not match.'
  Object.values(errors).forEach((value) => {
    if (value) valid = false
  })
  return valid
}

async function handleSignup() {
  signupError.value = ''
  if (!validate()) return
  loading.value = true
  try {
    const payload = await authService.signup({
      fullName: form.fullName.trim(),
      email: form.email.trim(),
      password: form.password,
    })
    authStore.login(payload)
    router.push('/app/home')
  } catch (error) {
    signupError.value = error.message || 'Unable to create account.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import '../../style.css';

.login-page {
  display: flex;
  min-height: 100vh;
  background: #f9fafb;
}

.brand-panel {
  flex: 1;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 48px;
  border-right: 1px solid #e5e7eb;
}

.brand-content { max-width: 420px; }
.brand-logo { display: flex; align-items: center; gap: 14px; margin-bottom: 40px; }
.brand-name { font-size: 22px; font-weight: 700; color: #111827; }
.brand-headline { font-size: 40px; font-weight: 700; line-height: 1.12; color: #111827; margin: 0 0 18px; }
.brand-sub { font-size: 16px; line-height: 1.6; color: #6b7280; }

.form-panel {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
}

.form-card { width: 100%; max-width: 390px; }
.form-header { margin-bottom: 24px; }
.form-header h2 { font-size: 28px; margin: 0 0 6px; color: #111827; }
.form-header p { margin: 0; color: #6b7280; }

.login-form { display: flex; flex-direction: column; gap: 18px; }
.field { display: flex; flex-direction: column; gap: 7px; }
.field label { font-size: 13.5px; font-weight: 600; color: #374151; }
.input-wrap { position: relative; display: flex; align-items: center; }
.input-icon { position: absolute; left: 13px; color: #9ca3af; display: flex; }
.input-wrap input {
  width: 100%;
  padding: 11px 44px;
  font-size: 14.5px;
  border: 1.5px solid #e5e7eb;
  border-radius: 10px;
  outline: none;
}
.field.error .input-wrap input { border-color: #ef4444; box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1); }
.input-wrap input:focus { border-color: #7c3aed; box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.12); }
.toggle-pw { position: absolute; right: 12px; background: none; border: none; color: #9ca3af; cursor: pointer; }
.error-msg { font-size: 12.5px; color: #ef4444; }
.alert-error { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; border-radius: 10px; padding: 11px 14px; font-size: 13.5px; }
.btn-login {
  padding: 13px;
  background: #7c3aed;
  color: white;
  font-size: 15px;
  font-weight: 600;
  border: none;
  border-radius: 11px;
  cursor: pointer;
}
.btn-login:disabled { opacity: 0.7; cursor: not-allowed; }
.spinner { display: inline-block; width: 18px; height: 18px; border: 2.5px solid rgba(255,255,255,0.4); border-top-color: white; border-radius: 50%; animation: spin 0.7s linear infinite; }
.switch-auth { margin: 6px 0 0; font-size: 13px; color: #6b7280; text-align: center; }
.switch-auth a { color: #7c3aed; font-weight: 600; text-decoration: none; }

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 860px) {
  .login-page { flex-direction: column; }
  .brand-panel { padding: 40px 28px; border-right: none; border-bottom: 1px solid #e5e7eb; }
  .brand-headline { font-size: 30px; }
  .form-panel { width: 100%; box-sizing: border-box; padding: 40px 24px; }
}
</style>
