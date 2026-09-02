<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { accountApi } from '@/api/modules'

const travelers = ref([]); const loading = ref(false); const dialog = ref(false); const editingId = ref(null)
const form = reactive({ name: '', gender: '男', birthDate: '', idType: '身份证', idNo: '', phone: '', emergencyName: '', emergencyPhone: '' })
function reset() { Object.assign(form, { name: '', gender: '男', birthDate: '', idType: '身份证', idNo: '', phone: '', emergencyName: '', emergencyPhone: '' }); editingId.value = null }
function open(item) { reset(); if (item) { editingId.value = item.id; Object.assign(form, item, { idNo: '' }) } dialog.value = true }
async function load() { loading.value = true; try { travelers.value = await accountApi.travelers() || [] } finally { loading.value = false } }
async function save() { if (!form.name || !form.idNo && !editingId.value || !form.emergencyName) return ElMessage.warning('请完整填写姓名、证件号码和紧急联系人') ; if (editingId.value) await accountApi.updateTraveler(editingId.value, form); else await accountApi.createTraveler(form); dialog.value = false; ElMessage.success('已保存'); load() }
async function remove(item) { await ElMessageBox.confirm(`确认删除 ${item.name} 的常用出行人资料吗？`, '删除资料', { type: 'warning' }); await accountApi.deleteTraveler(item.id); ElMessage.success('已删除'); load() }
onMounted(load)
</script>

<template>
  <section class="page-section account-page"><div class="container"><div class="section-head"><div><span class="eyebrow">TRAVEL COMPANIONS</span><h2>常用出行人</h2><p>报名时快速选择；历史订单使用独立快照。</p></div><button class="primary-button" @click="open()">+ 新增出行人</button></div><div class="admin-panel"><div v-if="loading"><el-skeleton :rows="5" animated /></div><table v-else-if="travelers.length" class="data-table"><thead><tr><th>姓名</th><th>性别</th><th>证件</th><th>手机号</th><th>紧急联系人</th><th>操作</th></tr></thead><tbody><tr v-for="item in travelers" :key="item.id"><td>{{ item.name }}</td><td>{{ item.gender }}</td><td>{{ item.idType }} {{ item.idNoMasked }}</td><td>{{ item.phone || '—' }}</td><td>{{ item.emergencyName }} {{ item.emergencyPhone || '' }}</td><td><button class="text-button" @click="open(item)">编辑</button><button class="text-button" @click="remove(item)">删除</button></td></tr></tbody></table><div v-else class="empty-box">暂无相关数据，添加常用出行人后报名更快捷。</div></div></div></section>
  <el-dialog v-model="dialog" :title="editingId ? '编辑出行人' : '新增出行人'" width="560px"><div class="dialog-form"><div class="form-field"><label>姓名</label><input v-model="form.name" /></div><div class="form-field"><label>性别</label><select v-model="form.gender"><option>男</option><option>女</option></select></div><div class="form-field"><label>出生日期</label><input v-model="form.birthDate" type="date" /></div><div class="form-field"><label>证件类型</label><select v-model="form.idType"><option>身份证</option><option>护照</option></select></div><div class="form-field"><label>证件号码</label><input v-model="form.idNo" :placeholder="editingId ? '如需修改请重新填写' : ''" /></div><div class="form-field"><label>手机号</label><input v-model="form.phone" /></div><div class="form-field"><label>紧急联系人</label><input v-model="form.emergencyName" /></div><div class="form-field"><label>紧急联系电话</label><input v-model="form.emergencyPhone" /></div></div><template #footer><button class="secondary-button" @click="dialog=false">取消</button><button class="primary-button" @click="save">保存</button></template></el-dialog>
</template>

<style scoped>
.dialog-form{display:grid;grid-template-columns:1fr 1fr;gap:0 15px}.dialog-form .form-field select{min-height:40px;border:1px solid var(--line);border-radius:9px;padding:0 10px;background:white}@media(max-width:520px){.dialog-form{grid-template-columns:1fr}}
</style>
