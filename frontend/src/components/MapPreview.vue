<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

const props = defineProps({ itinerary: { type: Array, default: () => [] } })
const mapElement = ref(null)
const hasAmapKey = Boolean(import.meta.env.VITE_AMAP_KEY)
let mapInstance

function points() {
  return props.itinerary.flatMap((day) => day.items || [])
    .filter((item) => item.longitude != null && item.latitude != null)
    .map((item) => [Number(item.longitude), Number(item.latitude), item.name])
}

function renderMap() {
  const key = import.meta.env.VITE_AMAP_KEY
  if (!key || !mapElement.value || !window.AMap) return
  const data = points()
  mapInstance?.destroy()
  mapInstance = new window.AMap.Map(mapElement.value, { zoom: data.length ? 5 : 4, center: data[0]?.slice(0, 2) || [116.397, 39.909] })
  const markers = data.map(([lng, lat, name]) => new window.AMap.Marker({ position: [lng, lat], title: name }))
  if (markers.length) mapInstance.add(markers)
  if (data.length > 1) {
    mapInstance.add(new window.AMap.Polyline({ path: data.map(([lng, lat]) => [lng, lat]), strokeColor: '#0d766e', strokeWeight: 4, strokeOpacity: .8 }))
  }
}

onMounted(() => {
  const key = import.meta.env.VITE_AMAP_KEY
  if (!key) return
  window._AMapSecurityConfig = { securityJsCode: import.meta.env.VITE_AMAP_SECURITY_CODE || '' }
  if (window.AMap) return renderMap()
  const script = document.createElement('script')
  script.src = `https://webapi.amap.com/maps?v=2.0&key=${key}`
  script.onload = renderMap
  document.head.appendChild(script)
})

watch(() => props.itinerary, renderMap, { deep: true })
onBeforeUnmount(() => mapInstance?.destroy())
</script>

<template>
  <div class="map-preview">
    <div ref="mapElement" class="map-canvas"></div>
    <div v-if="!hasAmapKey" class="map-empty">
      <span class="map-pin">⌖</span>
      <strong>行程路线地图</strong>
      <p>配置 VITE_AMAP_KEY 后显示高德地图 Marker 与路线示意</p>
      <small>地图能力由高德地图开放平台提供 · 请保留平台版权与来源信息</small>
    </div>
    <div v-else-if="!points().length" class="map-empty">
      <strong>暂无坐标数据</strong><p>为行程项目补充经纬度后显示地图</p>
    </div>
  </div>
</template>
