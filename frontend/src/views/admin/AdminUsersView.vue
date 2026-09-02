<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '@/api/modules'
const props = defineProps({ mode: { type: String, default: 'users' } })
const rows = ref([]); const loading = ref(false)
async function load() { loading.value = true; try { const data = props.mode === 'staff' ? await adminApi.staff({ page: 1, size: 20 }) : await adminApi.users({ page: 1, size: 20 }); rows.value = data?.records || [] } finally { loading.value = false } }
onMounted(load)
</script>

<template><div><div class="admin-page-head"><div><h2>{{ props.mode==='staff'?'工作人员管理':'用户管理' }}</h2><p>{{ props.mode==='staff'?'管理员负责工作人员账号和角色分配。':'查看账号基础资料与状态，不直接修改用户密码。' }}</p></div></div><div class="admin-panel"><div v-if="loading"><el-skeleton :rows="8" animated /></div><table v-else-if="rows.length" class="data-table"><thead><tr v-if="props.mode==='staff'"><th>员工编号</th><th>账号 ID</th><th>部门</th><th>职位</th><th>创建时间</th></tr><tr v-else><th>账号</th><th>昵称</th><th>手机号</th><th>邮箱</th><th>状态</th><th>创建时间</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id" v-if="props.mode==='staff'"><td>{{ row.employeeNo }}</td><td>{{ row.userId }}</td><td>{{ row.department || '—' }}</td><td>{{ row.position || '—' }}</td><td>{{ row.createdAt }}</td></tr><tr v-for="row in rows" :key="row.id" v-else><td>{{ row.username }}</td><td>{{ row.nickname }}</td><td>{{ row.phone || '—' }}</td><td>{{ row.email || '—' }}</td><td><span class="tag" :class="row.status===1?'success':'danger'">{{ row.status===1?'正常':'已停用' }}</span></td><td>{{ row.createdAt }}</td></tr></tbody></table><div v-else class="empty-box">暂无相关数据。</div></div></div></template>
