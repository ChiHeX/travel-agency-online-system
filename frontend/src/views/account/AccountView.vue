<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/modules'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const loading = ref(false)
const form = reactive({ nickname: '', realName: '', phone: '', email: '', avatar: '' })
onMounted(() => Object.assign(form, auth.user || {}))
async function submit() { loading.value = true; try { const user = await authApi.updateProfile(form); auth.user = user; localStorage.setItem('travel_agency_user', JSON.stringify(user)); ElMessage.success('资料已更新') } finally { loading.value = false } }
</script>

<template>
  <section class="page-section account-page"><div class="container account-layout"><aside class="account-nav"><span class="eyebrow">MY SPACE</span><h3>{{ auth.user?.nickname || '我的行迹' }}</h3><RouterLink to="/account/profile" class="active">个人资料</RouterLink><RouterLink to="/account/orders">我的订单</RouterLink><RouterLink to="/account/travelers">常用出行人</RouterLink><RouterLink to="/account/favorites">我的收藏</RouterLink><RouterLink to="/account/messages">消息中心</RouterLink><RouterLink to="/account/consultations">在线咨询</RouterLink></aside><main class="account-content"><div class="section-head"><div><span class="eyebrow">PROFILE</span><h2>个人资料</h2><p>账号安全与联系信息。</p></div></div><div class="admin-panel profile-panel"><div class="form-grid"><div class="form-field"><label>用户名</label><input :value="auth.user?.username" disabled /></div><div class="form-field"><label>昵称</label><input v-model="form.nickname" /></div><div class="form-field"><label>真实姓名</label><input v-model="form.realName" /></div><div class="form-field"><label>手机号</label><input v-model="form.phone" /></div><div class="form-field"><label>邮箱</label><input v-model="form.email" type="email" /></div></div><button class="primary-button" :disabled="loading" @click="submit">保存资料</button></div></main></div></section>
</template>

<style scoped>
.account-layout{display:grid;grid-template-columns:200px 1fr;gap:45px}.account-nav{display:grid;align-content:start;gap:5px}.account-nav h3{font:500 22px Georgia,serif;margin:10px 0 20px}.account-nav a{padding:10px 12px;border-radius:9px;color:var(--muted);font-size:13px}.account-nav a:hover,.account-nav a.active{background:var(--mint);color:var(--teal-dark);font-weight:700}.account-content{min-width:0}.profile-panel{max-width:700px}.profile-panel .form-grid{display:grid;grid-template-columns:1fr 1fr;gap:0 18px}.profile-panel button{margin-top:4px}@media(max-width:700px){.account-layout{grid-template-columns:1fr;gap:20px}.account-nav{display:flex;flex-wrap:wrap;gap:4px}.account-nav h3{width:100%;margin-bottom:5px}.profile-panel .form-grid{grid-template-columns:1fr}}
</style>
