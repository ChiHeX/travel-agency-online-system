<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { accountApi, routeApi } from '@/api/modules'
import { useAuthStore } from '@/stores/auth'
import MapPreview from '@/components/MapPreview.vue'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const data = ref(null)
const loading = ref(true)
const favorite = ref(false)
const selectedDeparture = ref(null)
const departures = computed(() => data.value?.departures || [])

async function load() {
  loading.value = true
  try { data.value = await routeApi.detail(route.params.id) } finally { loading.value = false }
}
async function toggleFavorite() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (favorite.value) await accountApi.removeFavorite(route.params.id)
  else await accountApi.addFavorite(route.params.id)
  favorite.value = !favorite.value
  ElMessage.success(favorite.value ? '已收藏线路' : '已取消收藏')
}
function book(departure) {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  router.push({ name: 'order-create', query: { departureId: departure.id, routeId: data.value.route.id } })
}
onMounted(load)
</script>

<template>
  <section v-if="loading" class="page-section container"><el-skeleton :rows="10" animated /></section>
  <section v-else-if="data" class="route-detail-page">
    <div class="detail-hero"><div class="container detail-hero-inner"><div><span class="eyebrow">{{ data.route.departureCity }} → {{ data.route.destination }}</span><h1>{{ data.route.name }}</h1><p>{{ data.route.description }}</p><div class="detail-meta"><span>{{ data.route.durationDays }} 日行程</span><span v-if="data.route.ratingCount">★ {{ data.route.ratingAvg }}（{{ data.route.ratingCount }} 条评价）</span><span v-else>暂无评分</span></div></div><button class="favorite-button" :class="{ active: favorite }" @click="toggleFavorite">{{ favorite ? '♥ 已收藏' : '♡ 收藏线路' }}</button></div></div>
    <div class="container detail-layout"><main>
      <section class="detail-block"><div class="section-head compact"><div><span class="eyebrow">AVAILABLE DEPARTURES</span><h2>选择团期</h2></div><span class="muted-text">价格为创建订单时的快照</span></div><div v-if="departures.length" class="departure-list"><div v-for="item in departures" :key="item.id" class="departure-row"><div><strong>{{ item.startDate }}</strong><span>至 {{ item.endDate }}</span></div><div><span class="departure-label">成人</span><strong class="departure-price">¥{{ item.adultPrice }}</strong><span class="departure-label">儿童</span><strong class="departure-price">¥{{ item.childPrice }}</strong></div><div class="remaining">剩余 {{ (item.maxPeople || 0) - (item.confirmedPeople || 0) - (item.reservedPeople || 0) }} 人</div><button class="primary-button" :disabled="(item.maxPeople || 0) - (item.confirmedPeople || 0) - (item.reservedPeople || 0) <= 0" @click="book(item)">{{ (item.maxPeople || 0) - (item.confirmedPeople || 0) - (item.reservedPeople || 0) > 0 ? '立即报名' : '已满' }}</button></div></div><div v-else class="empty-box">暂无相关数据，当前没有可报名团期。</div></section>
      <section class="detail-block"><div class="section-head compact"><div><span class="eyebrow">DAY BY DAY</span><h2>详细行程</h2></div></div><div v-if="data.itinerary?.length" class="itinerary-list"><article v-for="item in data.itinerary" :key="item.day.id" class="itinerary-day"><div class="day-number">DAY<br /><strong>{{ String(item.day.dayNumber).padStart(2, '0') }}</strong></div><div><h3>{{ item.day.title }}</h3><p>{{ item.day.description }}</p><div class="itinerary-items"><span v-for="point in item.items" :key="point.id">{{ point.name }}</span></div><small>交通：{{ item.day.transportation || '以出团通知为准' }} · 餐食：{{ item.day.meals || '请查看团期说明' }}</small></div></article></div><div v-else class="empty-box">暂无相关数据，行程正在整理中。</div></section>
      <section class="detail-block"><div class="section-head compact"><div><span class="eyebrow">MAP &amp; SOURCES</span><h2>行程地图</h2></div></div><MapPreview :itinerary="data.itinerary" /></section>
      <section class="detail-block two-columns"><div><h3>费用包含</h3><p class="detail-copy">{{ data.route.included || '暂无相关数据' }}</p></div><div><h3>费用不包含</h3><p class="detail-copy">{{ data.route.excluded || '暂无相关数据' }}</p></div><div class="wide"><h3>预订须知</h3><p class="detail-copy">{{ data.route.bookingNotice || '暂无相关数据' }}</p></div></section>
    </main><aside class="detail-aside"><div class="sticky-card"><span class="eyebrow">ROUTE SNAPSHOT</span><h3>{{ data.route.departureCity }} · {{ data.route.destination }}</h3><div class="aside-line"><span>行程天数</span><strong>{{ data.route.durationDays }} 天</strong></div><div class="aside-line"><span>可选团期</span><strong>{{ departures.length }} 个</strong></div><div class="aside-line"><span>线路评分</span><strong>{{ data.route.ratingCount ? data.route.ratingAvg : '暂无' }}</strong></div><p>登录后可收藏线路、选择团期并填写实名出行人信息。</p></div></aside></div>
  </section>
  <section v-else class="page-section container"><div class="empty-box">线路不存在或暂未上架。</div></section>
</template>

<style scoped>
.detail-hero { background: var(--teal-dark); color: white; padding: 64px 0 58px; }.detail-hero-inner { display: flex; justify-content: space-between; gap: 30px; align-items: end; }.detail-hero h1 { font: 500 clamp(34px, 5vw, 56px) Georgia, serif; margin: 14px 0; max-width: 780px; }.detail-hero p { color: #b9d4ce; max-width: 700px; line-height: 1.8; font-size: 14px; }.detail-meta { display: flex; gap: 20px; color: #cce4dd; font-size: 12px; }.favorite-button { white-space: nowrap; background: transparent; border: 1px solid #76b7a6; color: #d7eee8; border-radius: 9px; min-height: 40px; padding: 0 15px; cursor: pointer; }.favorite-button.active { background: #e77d61; border-color: #e77d61; color: #fff; }.detail-layout { display: grid; grid-template-columns: minmax(0, 1fr) 280px; gap: 32px; padding-top: 40px; padding-bottom: 70px; }.detail-block { margin-bottom: 40px; }.section-head.compact { margin-bottom: 18px; }.section-head.compact h2 { font-size: 27px; }.muted-text { color: var(--muted); font-size: 12px; }.departure-list { display: grid; gap: 9px; }.departure-row { display: grid; grid-template-columns: 1.25fr 1.4fr .7fr auto; gap: 15px; align-items: center; border: 1px solid var(--line); border-radius: 12px; background: white; padding: 14px 16px; font-size: 13px; }.departure-row > div:first-child span { color: var(--muted); margin-left: 7px; }.departure-price { color: var(--coral); margin-right: 15px; }.departure-label { color: var(--muted); font-size: 11px; margin-right: 4px; }.remaining { color: var(--teal); font-size: 12px; }.itinerary-list { display: grid; gap: 16px; }.itinerary-day { display: grid; grid-template-columns: 62px 1fr; gap: 18px; background: white; border: 1px solid var(--line); border-radius: 14px; padding: 20px; }.day-number { color: var(--coral); font-size: 10px; letter-spacing: 1px; line-height: 1.35; }.day-number strong { font-size: 25px; letter-spacing: 0; }.itinerary-day h3 { margin: 0 0 7px; font-size: 17px; }.itinerary-day p { margin: 0; color: var(--muted); font-size: 13px; line-height: 1.7; }.itinerary-items { display: flex; flex-wrap: wrap; gap: 7px; margin: 13px 0; }.itinerary-items span { background: var(--mint); border-radius: 13px; padding: 5px 9px; color: var(--teal-dark); font-size: 11px; }.itinerary-day small { color: #93a39f; }.map-preview { min-height: 330px; border-radius: 15px; overflow: hidden; background: #dceee8; position: relative; border: 1px solid var(--line); }.map-canvas { height: 330px; }.map-empty { position: absolute; inset: 0; display: grid; place-content: center; justify-items: center; text-align: center; color: var(--teal-dark); padding: 20px; }.map-empty p { margin: 8px 0; color: var(--muted); font-size: 12px; }.map-empty small { color: #77928a; font-size: 10px; }.map-pin { font-size: 46px; color: var(--teal); }.sticky-card { position: sticky; top: 105px; background: white; border: 1px solid var(--line); border-radius: 16px; padding: 22px; box-shadow: var(--shadow); }.sticky-card h3 { font: 500 22px Georgia, serif; margin: 15px 0 23px; }.aside-line { display: flex; justify-content: space-between; padding: 11px 0; border-top: 1px solid #edf3f0; color: var(--muted); font-size: 12px; }.aside-line strong { color: var(--ink); }.sticky-card p { color: var(--muted); line-height: 1.7; font-size: 12px; margin: 22px 0 0; }.two-columns { display: grid; grid-template-columns: 1fr 1fr; gap: 25px; background: white; border: 1px solid var(--line); padding: 22px; border-radius: 15px; }.two-columns h3 { margin: 0 0 9px; font-size: 15px; }.detail-copy { color: var(--muted); font-size: 13px; line-height: 1.8; margin: 0; }.wide { grid-column: 1 / -1; border-top: 1px solid var(--line); padding-top: 20px; }@media(max-width:800px){.detail-layout{grid-template-columns:1fr}.detail-aside{order:-1}.sticky-card{position:static}.departure-row{grid-template-columns:1fr 1fr}.departure-row .primary-button{grid-column:2;justify-self:end}.detail-hero-inner{display:block}.favorite-button{margin-top:20px}.two-columns{grid-template-columns:1fr}.wide{grid-column:auto}}
</style>
