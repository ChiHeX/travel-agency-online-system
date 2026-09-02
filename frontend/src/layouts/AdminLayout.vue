<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const isGuideOnly = computed(() => auth.hasRole('GUIDE') && !auth.hasRole('ADMIN') && !auth.hasRole('STAFF'))
const title = computed(() => isGuideOnly.value ? '导游工作台' : '旅行社管理后台')

function logout() {
  auth.logout()
  router.push('/auth/login')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink class="admin-brand" to="/">
        <span class="brand-mark">行</span><span><strong>{{ title }}</strong><small>TRAVEL AGENCY</small></span>
      </RouterLink>
      <div class="sidebar-caption">工作空间</div>
      <nav class="side-nav" v-if="isGuideOnly">
        <RouterLink to="/guide/dashboard">工作台概览</RouterLink>
        <RouterLink to="/guide/departures">我的团期</RouterLink>
      </nav>
      <nav class="side-nav" v-else>
        <RouterLink to="/admin/dashboard">Dashboard</RouterLink>
        <RouterLink to="/admin/routes">线路管理</RouterLink>
        <RouterLink to="/admin/departures">团期管理</RouterLink>
        <RouterLink to="/admin/orders">订单管理</RouterLink>
        <RouterLink to="/admin/refunds">退款审核</RouterLink>
        <RouterLink to="/admin/attractions">景点资料</RouterLink>
        <RouterLink to="/admin/hotels">酒店资料</RouterLink>
        <RouterLink to="/admin/guides">导游管理</RouterLink>
        <template v-if="auth.hasRole('ADMIN')">
          <div class="sidebar-caption nested-caption">系统管理</div>
          <RouterLink to="/admin/users">用户管理</RouterLink>
          <RouterLink to="/admin/staff">工作人员</RouterLink>
          <RouterLink to="/admin/logs">操作日志</RouterLink>
        </template>
      </nav>
      <RouterLink class="back-home" to="/">← 返回用户端</RouterLink>
    </aside>
    <section class="admin-main">
      <header class="admin-topbar">
        <div><span class="eyebrow">{{ route.meta?.section || 'OPERATIONS' }}</span><h1>{{ route.meta?.pageTitle || '业务工作台' }}</h1></div>
        <div class="admin-user"><span class="status-pill">{{ auth.roles.join(' / ') }}</span><span>{{ auth.user?.nickname || auth.user?.username }}</span><button class="link-button" @click="logout">退出</button></div>
      </header>
      <main class="admin-content"><RouterView /></main>
    </section>
  </div>
</template>
