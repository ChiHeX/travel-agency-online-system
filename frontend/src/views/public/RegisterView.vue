<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '', nickname: '', phone: '', email: '' })

async function submit() {
  loading.value = true
  try {
    await auth.register(form)
    ElMessage.success('注册成功，欢迎开始探索')
    router.replace('/')
  } catch (error) { ElMessage.error(error.message) } finally { loading.value = false }
}
</script>

<template>
  <section class="auth-page"><div class="auth-card"><span class="eyebrow">START YOUR JOURNEY</span><h1>创建账号</h1><p>实名信息只用于报名业务，历史订单保存数据快照。</p><form @submit.prevent="submit"><div class="form-field"><label>用户名</label><input v-model="form.username" autocomplete="username" placeholder="4-32 位字符" required /></div><div class="form-field"><label>密码</label><input v-model="form.password" type="password" autocomplete="new-password" placeholder="至少 8 位" minlength="8" required /></div><div class="form-field"><label>昵称</label><input v-model="form.nickname" placeholder="怎么称呼你？" required /></div><div class="form-field"><label>手机号（可选）</label><input v-model="form.phone" inputmode="tel" placeholder="用于订单联系" /></div><div class="form-field"><label>邮箱（可选）</label><input v-model="form.email" type="email" placeholder="用于接收出行信息" /></div><button class="primary-button full-button" :disabled="loading">{{ loading ? '创建中…' : '创建账号' }}</button></form><div class="form-foot"><span>已经有账号？</span><RouterLink to="/auth/login">返回登录 →</RouterLink></div></div></section>
</template>
