<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function submit() {
  loading.value = true
  try {
    await auth.login(form)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/')
  } catch (error) {
    ElMessage.error(error.message)
  } finally { loading.value = false }
}
</script>

<template>
  <section class="auth-page"><div class="auth-card"><span class="eyebrow">WELCOME BACK</span><h1>登录行迹</h1><p>登录后管理你的出行人、订单与收藏。</p><form @submit.prevent="submit"><div class="form-field"><label>用户名</label><input v-model="form.username" autocomplete="username" placeholder="请输入用户名" required /></div><div class="form-field"><label>密码</label><input v-model="form.password" type="password" autocomplete="current-password" placeholder="请输入密码" required /></div><button class="primary-button full-button" :disabled="loading">{{ loading ? '登录中…' : '登录' }}</button></form><div class="form-foot"><span>还没有账号？</span><RouterLink to="/auth/register">立即注册 →</RouterLink></div></div></section>
</template>
