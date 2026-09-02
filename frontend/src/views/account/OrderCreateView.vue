<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { orderApi, routeApi } from '@/api/modules'

const currentRoute = useRoute()
const router = useRouter()
const routeData = ref(null)
const loading = ref(true)
const submitting = ref(false)
const form = reactive({ departureId: Number(currentRoute.query.departureId), adultCount: 1, childCount: 0, contactName: '', contactPhone: '', contactEmail: '', remark: '', travelers: [] })
const participantCount = computed(() => Number(form.adultCount || 0) + Number(form.childCount || 0))
const departure = computed(() => routeData.value?.departures?.find((item) => item.id === form.departureId))
const totalAmount = computed(() => (Number(departure.value?.adultPrice || 0) * Number(form.adultCount || 0)) + (Number(departure.value?.childPrice || 0) * Number(form.childCount || 0)))

function emptyTraveler() { return { name: '', gender: '男', birthDate: '', idType: '身份证', idNo: '', phone: '', emergencyName: '', emergencyPhone: '' } }
function syncTravelers() {
  while (form.travelers.length < participantCount.value) form.travelers.push(emptyTraveler())
  if (form.travelers.length > participantCount.value) form.travelers.splice(participantCount.value)
}
watch(participantCount, syncTravelers, { immediate: true })
onMounted(async () => { try { routeData.value = await routeApi.detail(currentRoute.query.routeId || 0); if (!routeData.value?.route && currentRoute.query.departureId) { /* routeId can be provided by detail page */ } } catch (_) { /* interceptor displays the message */ } finally { loading.value = false } })

async function submit() {
  if (!departure.value) return ElMessage.warning('团期信息加载失败，请返回线路详情重新选择')
  if (participantCount.value <= 0 || form.travelers.some((item) => !item.name || !item.idNo || !item.emergencyName)) return ElMessage.warning('请完整填写每位出行人的实名与紧急联系人信息')
  submitting.value = true
  try { const order = await orderApi.create({ ...form, adultCount: Number(form.adultCount), childCount: Number(form.childCount) }); router.replace({ name: 'order-detail', params: { orderNo: order.orderNo } }) } finally { submitting.value = false }
}
</script>

<template>
  <section class="page-section account-page"><div class="container narrow-container"><div class="section-head"><div><span class="eyebrow">BOOK YOUR JOURNEY</span><h2>填写报名信息</h2><p>订单生成后将保存出行人信息快照。</p></div></div><div v-if="loading" class="admin-panel"><el-skeleton :rows="8" animated /></div><template v-else><div class="admin-panel order-form-panel"><div class="form-section"><h3>团期与联系人</h3><div class="selected-departure" v-if="departure"><strong>{{ routeData.route.name }}</strong><span>{{ departure.startDate }} 至 {{ departure.endDate }} · 成人 ¥{{ departure.adultPrice }} / 儿童 ¥{{ departure.childPrice }}</span></div><div class="form-grid"><div class="form-field"><label>成人</label><input v-model.number="form.adultCount" type="number" min="0" /></div><div class="form-field"><label>儿童</label><input v-model.number="form.childCount" type="number" min="0" /></div><div class="form-field"><label>联系人姓名</label><input v-model="form.contactName" required /></div><div class="form-field"><label>联系人手机号</label><input v-model="form.contactPhone" inputmode="tel" required /></div><div class="form-field wide-field"><label>联系人邮箱（可选）</label><input v-model="form.contactEmail" type="email" /></div></div></div><div class="form-section"><div class="section-title-row"><h3>出行人实名资料</h3><span>共 {{ participantCount }} 人</span></div><div v-for="(traveler, index) in form.travelers" :key="index" class="traveler-form"><div class="traveler-title">出行人 {{ index + 1 }}</div><div class="form-grid"><div class="form-field"><label>姓名</label><input v-model="traveler.name" required /></div><div class="form-field"><label>性别</label><select v-model="traveler.gender"><option>男</option><option>女</option></select></div><div class="form-field"><label>出生日期</label><input v-model="traveler.birthDate" type="date" /></div><div class="form-field"><label>证件类型</label><select v-model="traveler.idType"><option>身份证</option><option>护照</option><option>港澳通行证</option></select></div><div class="form-field"><label>证件号码</label><input v-model="traveler.idNo" required /></div><div class="form-field"><label>手机号</label><input v-model="traveler.phone" /></div><div class="form-field"><label>紧急联系人</label><input v-model="traveler.emergencyName" required /></div><div class="form-field"><label>紧急联系电话</label><input v-model="traveler.emergencyPhone" /></div></div></div></div><div class="form-section"><div class="form-field"><label>备注（可选）</label><textarea v-model="form.remark" rows="3" placeholder="饮食、交通等需要提前说明的事项"></textarea></div></div><div class="order-submit-bar"><div><span>订单金额</span><strong>¥{{ totalAmount.toFixed(2) }}</strong></div><button class="primary-button" :disabled="submitting" @click="submit">{{ submitting ? '提交中…' : '创建待支付订单' }}</button></div></div></template></div></section>
</template>

<style scoped>
.narrow-container { max-width: 900px; }.order-form-panel { padding: 0; overflow: hidden; }.form-section { padding: 24px; border-bottom: 1px solid var(--line); }.form-section h3 { margin: 0 0 18px; font-size: 16px; }.selected-departure { display: grid; gap: 5px; padding: 14px; background: var(--mint); color: var(--teal-dark); border-radius: 10px; margin-bottom: 18px; }.selected-departure span { font-size: 12px; color: var(--muted); }.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 15px; }.wide-field { grid-column: 1 / -1; }.form-field { margin-bottom: 14px; }.form-field select { min-height: 40px; border: 1px solid var(--line); border-radius: 9px; padding: 0 10px; background: white; }.section-title-row { display: flex; justify-content: space-between; align-items: center; }.section-title-row span { font-size: 12px; color: var(--muted); }.traveler-form { border: 1px solid var(--line); border-radius: 11px; padding: 16px; margin-top: 15px; }.traveler-title { color: var(--teal); font-weight: 700; font-size: 13px; margin-bottom: 13px; }.order-submit-bar { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; background: #fbfdfc; }.order-submit-bar div { display: flex; gap: 11px; align-items: baseline; color: var(--muted); font-size: 12px; }.order-submit-bar strong { color: var(--coral); font-size: 25px; }@media(max-width:600px){.form-grid{grid-template-columns:1fr}.wide-field{grid-column:auto}.form-section{padding:18px}.order-submit-bar{padding:17px;align-items:end;gap:15px}.order-submit-bar strong{font-size:20px}}
</style>
