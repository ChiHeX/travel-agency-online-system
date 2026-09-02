<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/modules'

const rows = ref([]); const total = ref(0); const page = ref(1); const loading = ref(false); const form = reactive({ keyword: '', status: '' })
const statusNames = { DRAFT: '草稿', PUBLISHED: '已上架', OFFLINE: '已下架' }
async function load() { loading.value = true; try { const data = await adminApi.routes({ ...form, page: page.value, size: 10 }); rows.value = data?.records || []; total.value = data?.total || 0 } finally { loading.value = false } }
async function publish(row) { const next = row.status === 'PUBLISHED' ? 'OFFLINE' : 'PUBLISHED'; await adminApi.updateRouteStatus(row.id, next); row.status = next; ElMessage.success(`线路已${next === 'PUBLISHED' ? '上架' : '下架'}`) }
onMounted(load)
</script>

<template><div><div class="admin-page-head"><div><h2>线路管理</h2><p>维护线路基本资料、行程内容与发布状态。</p></div><button class="primary-button" @click="ElMessage.info('线路编辑表单可在此接入，API 已准备就绪')">+ 新增线路</button></div><div class="admin-panel"><form class="toolbar" @submit.prevent="page=1;load()"><input v-model="form.keyword" placeholder="搜索线路名称或目的地" /><select v-model="form.status"><option value="">全部状态</option><option value="DRAFT">草稿</option><option value="PUBLISHED">已上架</option><option value="OFFLINE">已下架</option></select><button class="secondary-button">查询</button></form><div v-if="loading"><el-skeleton :rows="8" animated /></div><table v-else-if="rows.length" class="data-table"><thead><tr><th>线路名称</th><th>出发地</th><th>目的地</th><th>天数</th><th>评分</th><th>报名数</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id"><td><strong>{{ row.name }}</strong></td><td>{{ row.departureCity }}</td><td>{{ row.destination }}</td><td>{{ row.durationDays }} 天</td><td>{{ row.ratingCount ? row.ratingAvg : '暂无' }}</td><td>{{ row.validBookingCount || 0 }}</td><td><span class="tag" :class="row.status==='PUBLISHED'?'success':row.status==='OFFLINE'?'danger':''">{{ statusNames[row.status] || row.status }}</span></td><td><button class="text-button" @click="publish(row)">{{ row.status==='PUBLISHED' ? '下架' : '上架' }}</button></td></tr></tbody></table><div v-else class="empty-box">暂无相关数据，请先创建线路。</div><div v-if="total>10" class="pagination-wrap"><el-pagination v-model:current-page="page" background layout="prev, pager, next" :page-size="10" :total="total" @current-change="load" /></div></div></div></template>

<style scoped>.pagination-wrap{display:flex;justify-content:center;margin-top:20px}.toolbar input{flex:1;min-width:240px}</style>
