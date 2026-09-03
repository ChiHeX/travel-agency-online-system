<script setup>
import { inject, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { routeApi } from '@/api/modules'
import AppIcon from '@/components/AppIcon.vue'

const currentRoute = useRoute()
const router = useRouter()
const closeDrawer = inject('closeDrawer', () => {})

const form = reactive({
  departureCity: currentRoute.query.departureCity || '',
  destination: currentRoute.query.keyword || '',
  durationDays: '',
  hasDeparture: true
})

const routes = ref([])
const total = ref(0)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const params = {
      keyword: form.destination || undefined,
      departureCity: form.departureCity || undefined,
      durationDays: form.durationDays || undefined,
      hasDeparture: true,
      page: 1,
      size: 15
    }
    const data = await routeApi.list(params)
    routes.value = data?.records || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function openRoute(id) {
  router.push({ name: 'route-detail', params: { id } })
}

function resetOriginDestination() {
  form.departureCity = ''
  form.destination = ''
  load()
}

onMounted(load)
</script>

<template>
  <div class="route-drawer-panel">
    <!-- Header: 路线 ↺ ✕ (Screenshot 3) -->
    <div class="drawer-header-bar">
      <h2>路线</h2>
      <div class="header-action-icons">
        <button type="button" class="drawer-icon-btn" title="重置" @click="resetOriginDestination">
          <AppIcon name="refresh" size="13" />
        </button>
        <button type="button" class="drawer-icon-btn" title="关闭面板" @click="closeDrawer">
          <AppIcon name="close" size="13" />
        </button>
      </div>
    </div>

    <div class="drawer-scroll-body">
      <!-- Waypoint Inputs with Vertical Connector (Screenshot 3) -->
      <div class="waypoints-card-box">
        <!-- Start Point (起点) -->
        <div class="waypoint-row">
          <span class="waypoint-bullet blue">
            <AppIcon name="circle" size="13" color="#0071e3" />
          </span>
          <div class="waypoint-input-box">
            <span class="input-sub">起点 / 出发城市</span>
            <input v-model="form.departureCity" placeholder="输入出发城市" @change="load" />
          </div>
          <span class="waypoint-drag">
            <AppIcon name="drag" size="14" color="#8e8e93" />
          </span>
        </div>

        <div class="vertical-connector-line"></div>

        <!-- End Point (终点) -->
        <div class="waypoint-row">
          <span class="waypoint-bullet blue">
            <AppIcon name="pin" size="13" color="#0071e3" />
          </span>
          <div class="waypoint-input-box">
            <span class="input-sub">终点 / 目的地</span>
            <input v-model="form.destination" placeholder="输入目的地" @change="load" />
          </div>
          <span class="waypoint-drag">
            <AppIcon name="drag" size="14" color="#8e8e93" />
          </span>
        </div>
      </div>

      <!-- Options Dropdowns (Screenshot 3) -->
      <div class="options-pills-row">
        <div class="pill-dropdown">
          <select v-model="form.durationDays" @change="load">
            <option value="">出游天数：全部</option>
            <option v-for="d in [3, 4, 5, 6, 7, 8, 10]" :key="d" :value="d">{{ d }} 天行程</option>
          </select>
        </div>
        <button type="button" class="pill-btn" @click="load">
          <AppIcon name="filter" size="11" />
          <span>可报名 ({{ total }})</span>
        </button>
      </div>

      <!-- Route Plans / Results List (地图 Route Cards) -->
      <div class="route-plans-section">
        <h4 class="plans-title">匹配跟团游方案</h4>

        <div v-if="loading" class="skeleton-wrap">
          <el-skeleton v-for="i in 3" :key="i" :rows="3" animated style="margin-bottom: 10px;" />
        </div>

        <div v-else-if="routes.length" class="route-plans-list">
          <div
            v-for="item in routes"
            :key="item.id"
            class="route-plan-card"
            @click="openRoute(item.id)"
          >
            <div class="plan-card-left">
              <span class="mode-icon">
                <AppIcon name="bus" size="18" color="#0071e3" />
              </span>
              <div class="plan-info">
                <h5>{{ item.name }}</h5>
                <span class="plan-specs">{{ item.departureCity }} 出发 · {{ item.durationDays }} 日行程 · {{ item.destination }}</span>
              </div>
            </div>

            <div class="plan-card-right">
              <strong v-if="item.minAdultPrice != null" class="plan-price">¥{{ item.minAdultPrice }}</strong>
              <span v-else class="plan-price pending-price">价格待发布</span>
              <AppIcon name="chevron-right" size="14" color="#8e8e93" />
            </div>
          </div>
        </div>

        <div v-else class="empty-box">
          暂无匹配的路线方案，请调整目的地或出发城市。
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.route-drawer-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: transparent;
}

/* Header (Screenshot 3: 路线 📤 ✕) */
.drawer-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.drawer-header-bar h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
}

.header-action-icons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.drawer-icon-btn {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.05);
  border: none;
  font-size: 14px;
  color: var(--text-secondary);
  display: grid;
  place-items: center;
  cursor: pointer;
}

.drawer-icon-btn:hover {
  background: rgba(0, 0, 0, 0.08);
  color: var(--text-primary);
}

/* Scroll Body */
.drawer-scroll-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Transport Mode Switcher (Screenshot 3) */
.transport-mode-switch {
  display: flex;
  background: rgba(0, 0, 0, 0.06);
  padding: 3px;
  border-radius: var(--radius-sm);
  gap: 2px;
}

.mode-btn {
  flex: 1;
  border: none;
  background: transparent;
  padding: 7px 0;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  color: #1d1d1f;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  transition: all 0.15s ease;
}

.mode-btn.active {
  background: var(--theme-blue);
  color: #ffffff;
  font-weight: 600;
}

/* Waypoints Card (Screenshot 3) */
.waypoints-card-box {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: var(--radius-md);
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
}

.waypoint-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.waypoint-bullet {
  display: flex;
  align-items: center;
}

.waypoint-input-box {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.input-sub {
  font-size: 9px;
  color: var(--text-tertiary);
  text-transform: uppercase;
}

.waypoint-input-box input {
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  padding: 2px 0;
}

.waypoint-drag {
  display: flex;
  align-items: center;
}

.vertical-connector-line {
  width: 2px;
  height: 18px;
  background: rgba(0, 0, 0, 0.08);
  margin-left: 6px;
  margin-top: 3px;
  margin-bottom: 3px;
}

/* Option Dropdowns (Screenshot 3) */
.options-pills-row {
  display: flex;
  gap: 8px;
}

.pill-dropdown select {
  height: 32px;
  border-radius: var(--radius-pill);
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.7);
  padding: 0 12px;
  font-size: 12px;
  color: #1d1d1f;
  outline: none;
}

.pill-btn {
  height: 32px;
  border-radius: var(--radius-pill);
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.7);
  padding: 0 12px;
  font-size: 12px;
  color: #1d1d1f;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* Route Plans Section */
.plans-title {
  font-size: 14px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 8px 0 10px;
}

.route-plans-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.route-plan-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  cursor: pointer;
  transition: all 0.15s ease;
}

.route-plan-card:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(0, 0, 0, 0.12);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.plan-card-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.mode-icon {
  display: flex;
  align-items: center;
}

.plan-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.plan-info h5 {
  font-size: 13px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.plan-specs {
  font-size: 11px;
  color: var(--text-secondary);
}

.plan-tag-text {
  font-size: 10px;
  color: var(--theme-blue);
  margin-top: 2px;
}

.plan-card-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.plan-price {
  font-size: 15px;
  font-weight: 700;
  color: var(--price-color);
}
</style>
