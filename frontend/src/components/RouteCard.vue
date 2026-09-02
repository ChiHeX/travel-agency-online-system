<script setup>
defineProps({
  route: { type: Object, required: true }
})
</script>

<template>
  <RouterLink class="route-card" :to="{ name: 'route-detail', params: { id: route.id } }">
    <div class="route-cover">
      <img v-if="route.coverUrl" :src="route.coverUrl" :alt="route.name" />
      <template v-else>
        <span>{{ route.departureCity }} → {{ route.destination }}</span>
        <strong>{{ route.name?.slice(0, 16) }}</strong>
      </template>
    </div>
    <div class="route-card-body">
      <h3>{{ route.name }}</h3>
      <div class="route-meta">
        <span>{{ route.durationDays }} 日行程</span>
        <span>{{ route.departureCity }} 出发</span>
        <span v-if="route.ratingCount">{{ route.ratingAvg }} 分</span>
        <span v-else>暂无评分</span>
      </div>
      <div class="route-card-foot">
        <div v-if="route.minAdultPrice" class="price"><small>¥</small>{{ route.minAdultPrice }}<small>起</small></div>
        <div v-else class="price-label">查看可报名团期</div>
        <span class="plain-link">查看详情 →</span>
      </div>
    </div>
  </RouterLink>
</template>
