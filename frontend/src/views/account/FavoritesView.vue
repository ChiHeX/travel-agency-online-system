<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { accountApi } from '@/api/modules'
import RouteCard from '@/components/RouteCard.vue'
const routes = ref([]); const loading = ref(false)
async function load() { loading.value = true; try { routes.value = await accountApi.favorites() || [] } finally { loading.value = false } }
async function remove(id) { await accountApi.removeFavorite(id); routes.value = routes.value.filter((item) => item.id !== id); ElMessage.success('已取消收藏') }
onMounted(load)
</script>

<template><section class="page-section account-page"><div class="container"><div class="section-head"><div><span class="eyebrow">SAVED ROUTES</span><h2>我的收藏</h2><p>收藏感兴趣的线路，方便下次继续了解。</p></div></div><div v-if="loading" class="card-grid"><div v-for="i in 3" :key="i" class="route-card"><div class="route-cover"></div><div class="route-card-body"><el-skeleton :rows="3" animated /></div></div></div><div v-else-if="routes.length" class="card-grid"><div v-for="item in routes" :key="item.id" class="favorite-card"><RouteCard :route="item" /><button class="text-button remove-favorite" @click="remove(item.id)">取消收藏</button></div></div><div v-else class="empty-box">暂无相关数据，在线路详情页收藏后会显示在这里。</div></div></section></template>

<style scoped>.favorite-card{position:relative}.remove-favorite{position:absolute;right:14px;bottom:15px;font-size:11px;background:rgba(255,255,255,.85);padding:3px 5px}</style>
