<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/modules'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const activeStatus = ref('')
const labels = { WAIT_PAY: '待支付', PAID_WAIT_CONFIRM: '待确认', CONFIRMED: '已确认', TRAVELLING: '行程中', COMPLETED: '已完成', CANCELLED: '已取消', REFUND_APPLYING: '退款审核中', REFUND_PROCESSING: '退款处理中', REFUNDED: '已退款', REFUND_REJECTED: '退款未通过' }
const tagClass = (status) => ({ WAIT_PAY: 'warning', PAID_WAIT_CONFIRM: 'warning', REFUND_APPLYING: 'warning', REFUND_PROCESSING: 'warning', REFUNDED: 'danger', CANCELLED: 'danger', REFUND_REJECTED: 'danger', CONFIRMED: 'success', TRAVELLING: 'success', COMPLETED: 'success' }[status] || '')

async function load() { loading.value = true; try { orders.value = await orderApi.list(activeStatus.value ? { status: activeStatus.value } : {}) || [] } finally { loading.value = false } }
async function cancel(order) { await ElMessageBox.confirm('确认取消这个待支付订单吗？已支付订单需要申请退款。', '取消订单', { type: 'warning' }); await orderApi.cancel(order.orderNo); ElMessage.success('订单已取消'); load() }
async function pay(order) { const data = await orderApi.pay(order.orderNo); ElMessage.info(`${data.notice}，等待支付回调确认。`) }
onMounted(load)
</script>

<template>
  <section class="page-section account-page"><div class="container"><div class="section-head"><div><span class="eyebrow">YOUR JOURNEYS</span><h2>我的订单</h2><p>从下单、支付到出团，状态在这里持续更新。</p></div></div><div class="status-tabs"><button :class="{active: !activeStatus}" @click="activeStatus='';load()">全部</button><button v-for="status in ['WAIT_PAY','PAID_WAIT_CONFIRM','CONFIRMED','TRAVELLING','COMPLETED','REFUND_APPLYING','REFUNDED']" :key="status" :class="{active: activeStatus===status}" @click="activeStatus=status;load()">{{ labels[status] }}</button></div><div class="admin-panel order-list-panel"><div v-if="loading"><el-skeleton :rows="7" animated /></div><div v-else-if="orders.length" class="order-list"><article v-for="order in orders" :key="order.id" class="order-card"><div class="order-card-head"><div><span class="order-no">{{ order.orderNo }}</span><span class="order-time">{{ order.createdAt }}</span></div><span class="tag" :class="tagClass(order.status)">{{ labels[order.status] || order.status }}</span></div><div class="order-card-body"><div><h3>线路 #{{ order.routeId }}</h3><p>团期 #{{ order.departureId }} · 联系人 {{ order.contactName }} · {{ Number(order.adultCount || 0) + Number(order.childCount || 0) }} 位出行人</p></div><strong class="order-amount">¥{{ order.totalAmount }}</strong></div><div class="order-card-foot"><span>支付状态：{{ order.paymentStatus }}</span><div><button class="text-button" @click="router.push({ name: 'order-detail', params: { orderNo: order.orderNo } })">查看详情</button><button v-if="order.status==='WAIT_PAY'" class="secondary-button small-button" @click="pay(order)">去支付</button><button v-if="order.status==='WAIT_PAY'" class="secondary-button danger-button small-button" @click="cancel(order)">取消订单</button></div></div></article></div><div v-else class="empty-box">暂无相关数据，创建订单后会显示在这里。</div></div></div></section>
</template>

<style scoped>
.status-tabs { display: flex; flex-wrap: wrap; gap: 8px; margin: -3px 0 18px; }.status-tabs button { border: 0; background: transparent; color: var(--muted); padding: 7px 12px; border-radius: 16px; cursor: pointer; font-size: 12px; }.status-tabs button.active { background: var(--teal); color: white; }.order-list-panel { padding: 14px; }.order-list { display: grid; gap: 12px; }.order-card { border: 1px solid var(--line); border-radius: 13px; padding: 17px 18px; }.order-card-head, .order-card-body, .order-card-foot { display: flex; justify-content: space-between; align-items: center; gap: 15px; }.order-card-head { padding-bottom: 13px; border-bottom: 1px solid #edf3f0; }.order-no { font-weight: 700; font-size: 13px; margin-right: 13px; }.order-time, .order-card-foot { color: var(--muted); font-size: 11px; }.order-card-body { padding: 18px 0; }.order-card-body h3 { margin: 0 0 7px; font-size: 16px; }.order-card-body p { margin: 0; color: var(--muted); font-size: 12px; }.order-amount { color: var(--coral); font-size: 20px; }.order-card-foot { border-top: 1px solid #edf3f0; padding-top: 13px; }.small-button { min-height: 30px; padding: 0 10px; margin-left: 8px; font-size: 11px; }@media(max-width:600px){.order-card-body{align-items:start}.order-card-foot{align-items:end}.order-card-head,.order-card-body,.order-card-foot{flex-wrap:wrap}.order-card-foot > div{width:100%;text-align:right}}
</style>
