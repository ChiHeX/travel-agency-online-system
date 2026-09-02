<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { routeApi } from '@/api/modules'
import RouteCard from '@/components/RouteCard.vue'

const router = useRouter()
const keyword = ref('')
const routes = ref([])
const loading = ref(false)

async function loadRoutes() {
  loading.value = true
  try {
    const data = await routeApi.list({ page: 1, size: 6, hasDeparture: true })
    routes.value = data?.records || []
  } finally {
    loading.value = false
  }
}

function search() {
  router.push({ name: 'routes', query: keyword.value ? { keyword: keyword.value } : {} })
}

onMounted(loadRoutes)
</script>

<template>
  <section class="home-hero">
    <div class="container hero-inner">
      <div class="hero-copy">
        <span class="eyebrow">CURATED GROUP TOURS</span>
        <h1>去远方，<em>一起走。</em></h1>
        <p>精选跟团线路、清晰团期与完整行程，让每一次出发都从容而有期待。</p>
        <form class="hero-search" @submit.prevent="search">
          <span>⌕</span><input v-model="keyword" placeholder="搜索线路、目的地或景点" /><button class="primary-button">开始探索</button>
        </form>
        <div class="hero-notes"><span>✓ 实名报名</span><span>✓ 团期透明</span><span>✓ 站内消息同步</span></div>
      </div>
      <div class="hero-art" aria-hidden="true"><div class="sun"></div><div class="mountain back"></div><div class="mountain front"></div><div class="road"></div><span class="art-label">LET'S GO<br /><strong>从一条线路开始</strong></span></div>
    </div>
  </section>

  <section class="page-section">
    <div class="container">
      <div class="section-head"><div><span class="eyebrow">OPEN FOR BOOKING</span><h2>近期可报名线路</h2></div><RouterLink class="plain-link" to="/routes">查看全部线路 →</RouterLink></div>
      <div v-if="loading" class="card-grid"><div v-for="i in 3" :key="i" class="route-card loading-card"><div class="route-cover"></div><div class="route-card-body"><el-skeleton :rows="3" animated /></div></div></div>
      <div v-else-if="routes.length" class="card-grid"><RouteCard v-for="route in routes" :key="route.id" :route="route" /></div>
      <div v-else class="empty-box">暂无相关数据，旅行社发布可报名团期后会显示在这里。</div>
    </div>
  </section>

  <section class="home-values"><div class="container value-grid"><div><span class="eyebrow">A CLEAR JOURNEY</span><h2>从线路，到团期，再到每一个行程日。</h2></div><div class="value-item"><strong>01</strong><span>公开透明的团期与价格快照</span></div><div class="value-item"><strong>02</strong><span>订单状态全程可追踪</span></div><div class="value-item"><strong>03</strong><span>真实评价，不制造数据</span></div></div></section>
</template>

<style scoped>
.home-hero { overflow: hidden; background: linear-gradient(112deg, #e8f5ee, #f8fbf9 62%, #d9efe8); }
.hero-inner { min-height: 510px; display: flex; align-items: center; position: relative; }
.hero-copy { position: relative; z-index: 2; width: 56%; padding: 68px 0; }
.hero-copy h1 { margin: 15px 0 14px; font: 500 clamp(42px, 6vw, 76px)/1.08 Georgia, serif; letter-spacing: -3px; }
.hero-copy h1 em { font-style: normal; color: var(--teal); }
.hero-copy p { color: var(--muted); max-width: 460px; line-height: 1.9; font-size: 15px; }
.hero-search { display: flex; align-items: center; gap: 10px; background: white; max-width: 510px; padding: 7px 8px 7px 16px; margin-top: 30px; border-radius: 11px; box-shadow: var(--shadow); }
.hero-search span { color: var(--teal); font-size: 22px; }
.hero-search input { flex: 1; min-width: 0; border: 0; outline: 0; color: var(--ink); }
.hero-search button { white-space: nowrap; }
.hero-notes { display: flex; gap: 18px; color: var(--muted); font-size: 11px; margin-top: 19px; }
.hero-art { position: absolute; right: -45px; bottom: 0; width: 51%; height: 100%; overflow: hidden; }
.sun { position: absolute; width: 180px; height: 180px; border-radius: 50%; background: #f6c477; right: 17%; top: 13%; opacity: .85; }
.mountain { position: absolute; bottom: 55px; width: 0; height: 0; border-left: 210px solid transparent; border-right: 210px solid transparent; border-bottom: 305px solid #8bc6b7; }
.mountain.back { right: 28%; opacity: .48; border-bottom-color: #b6ddcf; transform: scale(1.18); }
.mountain.front { right: 2%; border-bottom-color: #559e95; }
.road { position: absolute; bottom: -70px; left: 18%; width: 110%; height: 190px; background: #f9e9c7; transform: rotate(-12deg); border-top: 3px solid rgba(237,128,98,.45); }
.art-label { position: absolute; bottom: 46px; left: 12%; color: #fff; letter-spacing: 3px; font-size: 10px; line-height: 1.9; text-shadow: 0 2px 14px rgba(12,60,55,.3); }
.art-label strong { font: 20px Georgia, serif; letter-spacing: 0; }
.home-values { background: var(--teal-dark); color: white; padding: 44px 0; }
.value-grid { display: grid; grid-template-columns: 1.7fr repeat(3, 1fr); gap: 30px; align-items: center; }
.value-grid h2 { font: 500 26px/1.35 Georgia, serif; margin: 9px 0 0; max-width: 290px; }
.value-item { display: flex; flex-direction: column; gap: 8px; border-left: 1px solid rgba(255,255,255,.2); padding-left: 22px; color: #c0dad4; font-size: 12px; line-height: 1.6; }
.value-item strong { color: #78ceb5; font-size: 12px; }
.value-item span { max-width: 120px; }
@media (max-width: 700px) { .hero-copy { width: 100%; padding-top: 55px; }.hero-art { opacity: .27; width: 100%; right: -30%; }.hero-search { max-width: 100%; }.hero-notes { flex-wrap: wrap; }.value-grid { grid-template-columns: 1fr 1fr; }.value-grid > :first-child { grid-column: 1 / -1; } }
</style>
