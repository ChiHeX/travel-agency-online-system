<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { contentApi } from '@/api/modules'

const route = useRoute()
const article = ref(null)
const loading = ref(true)
onMounted(async () => { try { article.value = await contentApi.article(route.params.id) } finally { loading.value = false } })
</script>

<template>
  <section class="page-section"><div class="container article-detail"><el-skeleton v-if="loading" :rows="12" animated /><template v-else-if="article"><span class="eyebrow">{{ article.city || article.destination || 'TRAVEL NOTE' }}</span><h1>{{ article.title }}</h1><div class="article-detail-meta">发表于 {{ article.publishedAt || '—' }}</div><p class="article-summary">{{ article.summary }}</p><div class="article-content">{{ article.content }}</div></template><div v-else class="empty-box">攻略不存在。</div></div></section>
</template>

<style scoped>
.article-detail { max-width: 820px; }.article-detail h1 { font: 500 clamp(32px, 5vw, 54px) Georgia, serif; margin: 16px 0; }.article-detail-meta { color: var(--muted); font-size: 12px; }.article-summary { margin: 30px 0; padding: 18px 20px; border-left: 3px solid var(--coral); color: var(--muted); line-height: 1.9; background: white; }.article-content { white-space: pre-wrap; background: white; border: 1px solid var(--line); border-radius: 16px; padding: 28px; min-height: 300px; line-height: 2; color: #405654; font-size: 15px; }
</style>
