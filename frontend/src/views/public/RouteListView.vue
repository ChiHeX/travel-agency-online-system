<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { routeApi } from '@/api/modules'
import RouteCard from '@/components/RouteCard.vue'

const currentRoute = useRoute()
const router = useRouter()
const form = reactive({ keyword: currentRoute.query.keyword || '', departureCity: '', destination: '', durationDays: '', minPrice: '', maxPrice: '', departureMonth: '', hasDeparture: true })
const routes = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const params = { ...form, page: page.value, size: 9 }
    Object.keys(params).forEach((key) => { if (params[key] === '' || params[key] == null) delete params[key] })
    const data = await routeApi.list(params)
    routes.value = data?.records || []
    total.value = data?.total || 0
  } finally { loading.value = false }
}

function submit() { page.value = 1; router.replace({ query: form.keyword ? { keyword: form.keyword } : {} }); load() }
function reset() { Object.assign(form, { keyword: '', departureCity: '', destination: '', durationDays: '', minPrice: '', maxPrice: '', departureMonth: '', hasDeparture: true }); submit() }
onMounted(load)
</script>

<template>
  <section class="page-section routes-page"><div class="container">
    <div class="section-head"><div><span class="eyebrow">DISCOVER YOUR NEXT STOP</span><h2>跟团线路</h2><p>按目的地、预算与出发时间找到适合你的行程。</p></div></div>
    <form class="filter-panel" @submit.prevent="submit">
      <input v-model="form.keyword" class="search-input keyword" placeholder="搜索线路、目的地、景点" />
      <input v-model="form.departureCity" class="search-input" placeholder="出发城市" />
      <input v-model="form.destination" class="search-input" placeholder="目的地" />
      <select v-model="form.durationDays" class="search-input"><option value="">出游天数</option><option value="3">3 天</option><option value="4">4 天</option><option value="5">5 天</option><option value="6">6 天</option><option value="7">7 天</option></select>
      <select v-model="form.departureMonth" class="search-input"><option value="">出发月份</option><option v-for="i in 12" :key="i" :value="i">{{ i }} 月</option></select>
      <input v-model="form.minPrice" class="search-input price-input" type="number" min="0" placeholder="最低价" />
      <input v-model="form.maxPrice" class="search-input price-input" type="number" min="0" placeholder="最高价" />
      <label class="check-control"><input v-model="form.hasDeparture" type="checkbox" /> 仅看可报名</label>
      <button class="primary-button" type="submit">筛选</button><button class="secondary-button" type="button" @click="reset">重置</button>
    </form>
    <div class="result-bar"><span>共 {{ total }} 条线路</span><span class="result-note">评分与报名人数均来自真实业务记录</span></div>
    <div v-if="loading" class="card-grid"><div v-for="i in 6" :key="i" class="route-card"><div class="route-cover"></div><div class="route-card-body"><el-skeleton :rows="3" animated /></div></div></div>
    <div v-else-if="routes.length" class="card-grid"><RouteCard v-for="route in routes" :key="route.id" :route="route" /></div>
    <div v-else class="empty-box">暂无相关数据，请调整筛选条件或等待旅行社发布线路。</div>
    <div v-if="total > 9" class="pagination-wrap"><el-pagination v-model:current-page="page" background layout="prev, pager, next" :page-size="9" :total="total" @current-change="load" /></div>
  </div></section>
</template>

<style scoped>
.routes-page { min-height: 680px; }
.filter-panel { display: flex; gap: 10px; flex-wrap: wrap; padding: 18px; border-radius: 16px; border: 1px solid var(--line); background: white; margin-bottom: 20px; }
.filter-panel .search-input { flex: 1 1 120px; min-width: 110px; }
.filter-panel .keyword { flex-basis: 235px; }
.filter-panel .price-input { flex-basis: 90px; }
.check-control { display: inline-flex; align-items: center; gap: 5px; color: var(--muted); font-size: 12px; white-space: nowrap; }
.result-bar { display: flex; justify-content: space-between; color: var(--muted); font-size: 12px; margin: 18px 0; }.result-note { color: #9aa9a6; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 30px; }
</style>
