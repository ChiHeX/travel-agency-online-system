<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { accountApi } from '@/api/modules'
const messages = ref([]); const loading = ref(false)
async function load() { loading.value = true; try { messages.value = await accountApi.messages() || [] } finally { loading.value = false } }
async function read(item) { if (item.readFlag) return; await accountApi.readMessage(item.id); item.readFlag = 1; ElMessage.success('已标记为已读') }
onMounted(load)
</script>

<template><section class="page-section account-page"><div class="container"><div class="section-head"><div><span class="eyebrow">INBOX</span><h2>消息中心</h2><p>支付、审核、退款与出行提醒会在这里同步。</p></div></div><div v-if="loading" class="admin-panel"><el-skeleton :rows="8" animated /></div><div v-else-if="messages.length" class="message-list"><article v-for="item in messages" :key="item.id" class="message-card" :class="{unread: !item.readFlag}" @click="read(item)"><div class="message-icon">{{ item.readFlag ? '○' : '●' }}</div><div><div class="message-head"><h3>{{ item.title }}</h3><span>{{ item.createdAt }}</span></div><p>{{ item.content }}</p></div></article></div><div v-else class="empty-box">暂无相关数据，新的业务通知会显示在这里。</div></div></section></template>

<style scoped>.message-list{display:grid;gap:10px;max-width:850px}.message-card{display:grid;grid-template-columns:28px 1fr;gap:13px;padding:18px;background:white;border:1px solid var(--line);border-radius:13px;cursor:pointer}.message-card.unread{border-left:3px solid var(--coral)}.message-icon{color:var(--coral);font-size:12px;padding-top:4px}.message-head{display:flex;justify-content:space-between;gap:15px}.message-head h3{margin:0;font-size:15px}.message-head span{color:var(--muted);font-size:11px}.message-card p{color:var(--muted);font-size:13px;line-height:1.7;margin:9px 0 0}</style>
