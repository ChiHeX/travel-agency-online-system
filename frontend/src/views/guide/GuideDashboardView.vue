<script setup>
import { onMounted, ref } from 'vue'
import { guideApi } from '@/api/modules'
const data = ref({ upcoming: [], active: [], history: [] }); const loading = ref(false)
onMounted(async () => { loading.value = true; try { data.value = await guideApi.dashboard() || data.value } finally { loading.value = false } })
</script>

<template><div><div class="admin-page-head"><div><h2>导游工作台</h2><p>只展示当前导游负责的团期与必要游客信息。</p></div></div><div class="stats-grid"><div class="stat-card"><span class="label">即将出发</span><div class="value">{{ data.upcoming.length }}</div></div><div class="stat-card"><span class="label">当前带团</span><div class="value">{{ data.active.length }}</div></div><div class="stat-card"><span class="label">历史带团</span><div class="value">{{ data.history.length }}</div></div></div><div v-if="loading" class="admin-panel"><el-skeleton :rows="6" animated /></div><div v-else class="admin-panel"><div class="panel-head"><div><span class="eyebrow">NEXT DEPARTURES</span><h3>即将出发</h3></div></div><div v-if="data.upcoming.length" class="trip-list"><div v-for="trip in data.upcoming" :key="trip.id" class="trip-row"><div><strong>{{ trip.startDate }}</strong><span>至 {{ trip.endDate }}</span></div><div>线路 #{{ trip.routeId }}</div><span class="tag">{{ trip.status }}</span><RouterLink class="plain-link" :to="{ name: 'guide-trip-detail', params: { id: trip.id } }">查看 →</RouterLink></div></div><div v-else class="empty-box">暂无相关数据。</div></div></div></template>

<style scoped>.panel-head h3{margin:7px 0 18px}.trip-list{display:grid;gap:8px}.trip-row{display:grid;grid-template-columns:1fr 1fr auto auto;align-items:center;gap:15px;padding:14px;border:1px solid var(--line);border-radius:10px;font-size:12px}.trip-row span:not(.tag){color:var(--muted);margin-left:7px}@media(max-width:600px){.trip-row{grid-template-columns:1fr 1fr}.trip-row .plain-link{justify-self:end}}
</style>
