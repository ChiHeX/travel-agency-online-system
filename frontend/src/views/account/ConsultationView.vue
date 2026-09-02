<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { accountApi } from '@/api/modules'
const items = ref([]); const form = reactive({ title: '', content: '' }); const loading = ref(false); const submitting = ref(false)
async function load() { loading.value = true; try { items.value = await accountApi.consultations() || [] } finally { loading.value = false } }
async function submit() { if (!form.title || !form.content) return ElMessage.warning('请填写问题标题和内容'); submitting.value = true; try { await accountApi.createConsultation(form); Object.assign(form, { title: '', content: '' }); ElMessage.success('咨询已提交'); load() } finally { submitting.value = false } }
onMounted(load)
</script>

<template><section class="page-section account-page"><div class="container consultation-layout"><div><div class="section-head"><div><span class="eyebrow">ASK THE TEAM</span><h2>在线咨询</h2><p>采用工单模式，工作人员回复后会在这里显示。</p></div></div><div class="admin-panel"><div class="form-field"><label>问题标题</label><input v-model="form.title" placeholder="例如：团期是否可以调整出发人？" /></div><div class="form-field"><label>问题内容</label><textarea v-model="form.content" rows="5" placeholder="请描述你的问题"></textarea></div><button class="primary-button" :disabled="submitting" @click="submit">提交咨询</button></div></div><div><h3 class="side-title">历史咨询</h3><div v-if="loading"><el-skeleton :rows="6" animated /></div><div v-else-if="items.length" class="consultation-list"><article v-for="item in items" :key="item.consultation.id" class="consultation-card"><span class="tag">{{ item.consultation.status }}</span><h3>{{ item.consultation.title }}</h3><p>{{ item.consultation.content }}</p><div v-for="reply in item.replies" :key="reply.id" class="reply">{{ reply.content }}</div></article></div><div v-else class="empty-box">暂无相关数据。</div></div></div></section></template>

<style scoped>.consultation-layout{display:grid;grid-template-columns:minmax(0,1.2fr) minmax(260px,.8fr);gap:40px}.side-title{font-size:17px;margin:53px 0 18px}.consultation-list{display:grid;gap:10px}.consultation-card{background:white;border:1px solid var(--line);border-radius:13px;padding:16px}.consultation-card h3{font-size:15px;margin:12px 0 6px}.consultation-card p{font-size:12px;line-height:1.7;color:var(--muted);margin:0}.reply{border-left:2px solid var(--teal);padding:9px 12px;margin-top:13px;color:var(--teal-dark);font-size:12px;background:#f1f8f5}@media(max-width:750px){.consultation-layout{grid-template-columns:1fr}.side-title{margin-top:0}}
</style>
