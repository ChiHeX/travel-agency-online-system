<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { accountApi } from '@/api/modules'

const auth = useAuthStore()
const router = useRouter()
const unreadCount = ref(0)
const isBackoffice = computed(() => auth.hasRole('ADMIN') || auth.hasRole('STAFF') || auth.hasRole('GUIDE'))

onMounted(async () => {
  if (auth.isLoggedIn) {
    try { unreadCount.value = await accountApi.unreadCount() } catch (_) { /* handled by request interceptor */ }
  }
})

function logout() {
  auth.logout()
  router.push('/')
}
</script>

<template>
  <div class="public-shell">
    <header class="site-header">
      <div class="container header-inner">
        <RouterLink class="brand" to="/">
          <span class="brand-mark">行</span>
          <span><strong>行迹</strong><small>TRAVEL AGENCY</small></span>
        </RouterLink>
        <nav class="main-nav">
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/routes">跟团线路</RouterLink>
          <RouterLink to="/articles">旅行攻略</RouterLink>
        </nav>
        <div class="header-actions">
          <RouterLink v-if="auth.isLoggedIn" class="message-link" to="/account/messages">
            消息<span v-if="unreadCount" class="unread-dot">{{ unreadCount }}</span>
          </RouterLink>
          <RouterLink v-if="isBackoffice" class="backoffice-link" :to="auth.hasRole('GUIDE') && !auth.hasRole('STAFF') && !auth.hasRole('ADMIN') ? '/guide' : '/admin'">工作台</RouterLink>
          <RouterLink v-if="!auth.isLoggedIn" class="login-link" to="/auth/login">登录</RouterLink>
          <template v-else>
            <RouterLink class="user-chip" to="/account/profile">{{ auth.user?.nickname || auth.user?.username }}</RouterLink>
            <button class="link-button" type="button" @click="logout">退出</button>
          </template>
        </div>
      </div>
    </header>
    <main><RouterView /></main>
    <footer class="site-footer">
      <div class="container footer-inner">
        <div><strong>行迹 · 在线跟团游</strong><span>把每一次出发，变成值得回看的故事。</span></div>
        <span>课程项目 · 测试与演示数据</span>
      </div>
    </footer>
  </div>
</template>
