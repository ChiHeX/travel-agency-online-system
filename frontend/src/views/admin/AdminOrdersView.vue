<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/modules'
const rows = ref([]); const total = ref(0); const page = ref(1); const status = ref(''); const loading = ref(false)
const names = { WAIT_PAY: '待支付', PAID_WAIT_CONFIRM: '待确认', CONFIRMED: '已确认', TRAVELLING: '行程中', COMPLETED: '已完成', CANCELLED: '已取消', REFUND_APPLYING: '退款审核中', REFUNDED: '已退款' }
async function load() { loading.value = true; try { const data = await adminApi.orders({ page: page.value, size: 20, status: status.value || undefined }); rows.value = data?.records || []; total.value = data?.total || 0 } finally { loading.value = false } }
async function confirm(row) { await ElMessageBox.confirm('确认审核通过该订单吗？系统会再次校验团期名额。', '确认报名', { type: 'warning' }); await adminApi.confirmOrder(row.orderNo); ElMessage.success('订单已确认'); load() }
onMounted(load)
</script>

<template><div><div class="admin-page-head"><div><h2>订单管理</h2><p>支付成功后仍需工作人员确认，避免异常报名直接进入出团流程。</p></div></div><div class="admin-panel"><div class="toolbar"><select v-model="status" @change="page=1;load()"><option value="">全部状态</option><option value="PAID_WAIT_CONFIRM">待确认</option><option value="CONFIRMED">已确认</option><option value="TRAVELLING">行程中</option><option value="COMPLETED">已完成</option><option value="REFUND_APPLYING">退款审核中</option></select></div><div v-if="loading"><el-skeleton :rows="9" animated /></div><table v-else-if="rows.length" class="data-table"><thead><tr><th>订单号</th><th>用户</th><th>线路 / 团期</th><th>人数</th><th>金额</th><th>支付</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id"><td>{{ row.orderNo }}</td><td>{{ row.userId }}</td><td>#{{ row.routeId }} / #{{ row.departureId }}</td><td>{{ Number(row.adultCount||0)+Number(row.childCount||0) }}</td><td class="amount">¥{{ row.totalAmount }}</td><td>{{ row.paymentStatus }}</td><td><span class="tag" :class="row.status==='CONFIRMED'?'success':row.status==='PAID_WAIT_CONFIRM'?'warning':row.status==='REFUNDED'?'danger':''">{{ names[row.status] || row.status }}</span></td><td><button v-if="row.status==='PAID_WAIT_CONFIRM'" class="text-button" @click="confirm(row)">确认报名</button><span v-else class="muted-text">—</span></td></tr></tbody></table><div v-else class="empty-box">暂无相关数据。</div><div v-if="total>20" class="pagination-wrap"><el-pagination v-model:current-page="page" background layout="prev, pager, next" :page-size="20" :total="total" @current-change="load" /></div></div></div></template>

<style scoped>.amount{color:var(--coral);font-weight:700}.pagination-wrap{display:flex;justify-content:center;margin-top:20px}</style>
