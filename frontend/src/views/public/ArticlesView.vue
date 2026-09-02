<script setup>
import { onMounted, ref } from 'vue'
import { contentApi } from '@/api/modules'

const articles = ref([])
const loading = ref(false)
onMounted(async () => { loading.value = true; try { articles.value = await contentApi.articles() || [] } finally { loading.value = false } })
</script>

<template>
  <section class="page-section"><div class="container"><div class="section-head"><div><span class="eyebrow">FIELD NOTES</span><h2>旅行攻略</h2><p>来自旅行社的目的地与行程建议。</p></div></div><div v-if="loading" class="article-list"><el-skeleton v-for="i in 3" :key="i" :rows="3" animated /></div><div v-else-if="articles.length" class="article-list"><RouterLink v-for="article in articles" :key="article.id" class="article-card" :to="{ name: 'article-detail', params: { id: article.id } }"><div class="article-visual"><img v-if="article.coverUrl" :src="article.coverUrl" :alt="article.title" /><span v-else>✦</span></div><div><div class="article-kicker">{{ article.city || article.destination || 'TRAVEL NOTE' }}</div><h3>{{ article.title }}</h3><p>{{ article.summary || article.content?.slice(0, 110) }}</p><span class="plain-link">阅读攻略 →</span></div></RouterLink></div><div v-else class="empty-box">暂无相关数据，旅行攻略发布后会显示在这里。</div></div></section>
</template>

<style scoped>
.article-list { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }.article-card { background: white; border: 1px solid var(--line); border-radius: 16px; overflow: hidden; transition: transform .2s, box-shadow .2s; }.article-card:hover { transform: translateY(-3px); box-shadow: var(--shadow); }.article-card > div:last-child { padding: 19px; }.article-visual { height: 140px; background: linear-gradient(135deg, #e0f3e9, #83beb0); display: grid; place-items: center; color: white; font-size: 38px; }.article-visual img { width: 100%; height: 100%; object-fit: cover; }.article-kicker { color: var(--teal); font-size: 10px; letter-spacing: 1px; }.article-card h3 { margin: 10px 0; font-size: 17px; }.article-card p { min-height: 42px; color: var(--muted); line-height: 1.7; font-size: 12px; }.article-card .plain-link { display: inline-block; margin-top: 7px; }@media(max-width:750px){.article-list{grid-template-columns:1fr 1fr}}@media(max-width:520px){.article-list{grid-template-columns:1fr}}
</style>
