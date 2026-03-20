<template>
  <div class="main-layout">
    <Sidebar />
    <div class="page-content">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
import Sidebar from '../components/Sidebar.vue'
import { RouterView } from 'vue-router'
</script>

<style scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 28px 32px;
  background: #f3f4f6;
}
</style>

