<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '@/api/modules'

const loading = ref(false)
const metrics = ref(null)
onMounted(async () => { loading.value = true; try { metrics.value = await adminApi.dashboard() } finally { loading.value = false } })
const money = (value) => value == null ? '暂无' : `¥${Number(value).toFixed(2)}`
</script>

<template>
  <div><div class="admin-page-head"><div><h2>业务概览</h2><p>统计数据来自系统实际业务记录，不使用硬编码经营数据。</p></div><span class="tag">实时查询</span></div><div v-if="loading" class="stats-grid"><div v-for="i in 8" :key="i" class="stat-card"><el-skeleton :rows="2" animated /></div></div><div v-else-if="metrics" class="stats-grid"><div class="stat-card"><span class="label">用户数量</span><div class="value">{{ metrics.userCount }}</div><span class="hint">已注册账号</span></div><div class="stat-card"><span class="label">有效线路</span><div class="value">{{ metrics.publishedRouteCount }}</div><span class="hint">当前已上架</span></div><div class="stat-card"><span class="label">可报名团期</span><div class="value">{{ metrics.openDepartureCount }}</div><span class="hint">状态 OPEN</span></div><div class="stat-card"><span class="label">今日订单</span><div class="value">{{ metrics.todayOrderCount }}</div><span class="hint">创建于今日</span></div><div class="stat-card"><span class="label">待确认订单</span><div class="value">{{ metrics.pendingConfirmCount }}</div><span class="hint">需要工作人员处理</span></div><div class="stat-card"><span class="label">待处理退款</span><div class="value">{{ metrics.pendingRefundCount }}</div><span class="hint">需要审核</span></div><div class="stat-card"><span class="label">有效报名人数</span><div class="value">{{ metrics.participantCount }}</div><span class="hint">排除取消与退款</span></div><div class="stat-card"><span class="label">订单金额</span><div class="value money-value">{{ money(metrics.grossOrderAmount) }}</div><span class="hint">已支付订单汇总</span></div></div><div v-else class="empty-box">暂无相关数据，数据库连接成功后显示 Dashboard 统计。</div><div class="dashboard-grid"><div class="admin-panel"><div class="panel-head"><div><span class="eyebrow">ORDER TRENDS</span><h3>订单趋势</h3></div><span class="muted-text">近 7 / 30 天</span></div><div class="chart-empty">接入真实订单记录后，在这里展示按日聚合的订单趋势。</div></div><div class="admin-panel"><div class="panel-head"><div><span class="eyebrow">POPULAR ROUTES</span><h3>热门线路</h3></div></div><div class="chart-empty">暂无相关数据。热门排序只基于有效报名订单。</div></div></div></div>
</template>

<style scoped>.dashboard-grid{display:grid;grid-template-columns:1.2fr .8fr;gap:16px}.panel-head{display:flex;justify-content:space-between;align-items:start}.panel-head h3{margin:7px 0 0;font-size:17px}.chart-empty{min-height:180px;display:grid;place-items:center;text-align:center;color:var(--muted);font-size:12px;border:1px dashed var(--line);border-radius:12px;margin-top:18px;padding:20px}.money-value{font-size:22px!important}@media(max-width:750px){.dashboard-grid{grid-template-columns:1fr}}
</style>
