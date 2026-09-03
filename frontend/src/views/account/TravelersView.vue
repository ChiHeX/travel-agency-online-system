<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { accountApi } from '@/api/modules'

const travelers = ref([])
const loading = ref(false)
const dialog = ref(false)
const editingId = ref(null)

const form = reactive({
  name: '',
  gender: '男',
  birthDate: '',
  idType: '身份证',
  idNo: '',
  phone: '',
  emergencyName: '',
  emergencyPhone: ''
})

function reset() {
  Object.assign(form, {
    name: '',
    gender: '男',
    birthDate: '',
    idType: '身份证',
    idNo: '',
    phone: '',
    emergencyName: '',
    emergencyPhone: ''
  })
  editingId.value = null
}

function open(item) {
  reset()
  if (item) {
    editingId.value = item.id
    Object.assign(form, item, { idNo: '' })
  }
  dialog.value = true
}

async function load() {
  loading.value = true
  try {
    travelers.value = (await accountApi.travelers()) || []
  } finally {
    loading.value = false
  }
}

async function save() {
  if (!form.name || (!form.idNo && !editingId.value) || !form.emergencyName) {
    return ElMessage.warning('请完整填写姓名、证件号码和紧急联系人')
  }
  if (editingId.value) await accountApi.updateTraveler(editingId.value, form)
  else await accountApi.createTraveler(form)
  dialog.value = false
  ElMessage.success('出行人资料已保存')
  load()
}

async function remove(item) {
  await ElMessageBox.confirm(`确认删除 ${item.name} 的常用出行人资料吗？`, '删除资料', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消'
  })
  await accountApi.deleteTraveler(item.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<template>
  <div class="account-page">
    <div class="container narrow-container page-section">
      <div class="section-head">
        <div>
          <span class="eyebrow">SAVED TRAVELERS</span>
          <h2>常用出行人管理</h2>
          <p>提前保存出行人资料，报名下单时可一键带入；已生成历史订单不受后续修改影响。</p>
        </div>
        <button type="button" class="primary-button" @click="open()">
          + 新增出行人
        </button>
      </div>

      <div class="admin-panel">
        <div v-if="loading">
          <el-skeleton :rows="5" animated />
        </div>

        <table v-else-if="travelers.length" class="data-table">
          <thead>
            <tr>
              <th>出行人姓名</th>
              <th>性别</th>
              <th>证件类型与脱敏号</th>
              <th>联系电话</th>
              <th>紧急联系人</th>
              <th style="text-align: right;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in travelers" :key="item.id">
              <td><strong>{{ item.name }}</strong></td>
              <td>{{ item.gender }}</td>
              <td>{{ item.idType }} {{ item.idNoMasked }}</td>
              <td>{{ item.phone || '—' }}</td>
              <td>{{ item.emergencyName }} ({{ item.emergencyPhone || '—' }})</td>
              <td style="text-align: right;">
                <button type="button" class="text-button" @click="open(item)">编辑</button>
                <span class="action-divider">|</span>
                <button type="button" class="text-button delete-btn" @click="remove(item)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-else class="empty-box">
          暂无常用出行人资料，点击右上角“+ 新增出行人”添加常用朋友或家人。
        </div>
      </div>
    </div>

    <!-- Edit Dialog -->
    <el-dialog
      v-model="dialog"
      :title="editingId ? '编辑常用出行人' : '新增常用出行人'"
      width="560px"
    >
      <div class="dialog-form-grid">
        <div class="form-field">
          <label>真实姓名 <span class="req">*</span></label>
          <input v-model="form.name" placeholder="请与证件姓名一致" required />
        </div>

        <div class="form-field">
          <label>性别</label>
          <select v-model="form.gender">
            <option>男</option>
            <option>女</option>
          </select>
        </div>

        <div class="form-field">
          <label>出生日期</label>
          <input v-model="form.birthDate" type="date" />
        </div>

        <div class="form-field">
          <label>证件类型</label>
          <select v-model="form.idType">
            <option>身份证</option>
            <option>护照</option>
            <option>港澳通行证</option>
          </select>
        </div>

        <div class="form-field wide">
          <label>证件号码 <span class="req">*</span></label>
          <input
            v-model="form.idNo"
            :placeholder="editingId ? '已加密隐藏，如需更改请重新输入' : '请输入完整有效证件号'"
          />
        </div>

        <div class="form-field">
          <label>联系电话</label>
          <input v-model="form.phone" inputmode="tel" placeholder="可选填写" />
        </div>

        <div class="form-field">
          <label>紧急联系人姓名 <span class="req">*</span></label>
          <input v-model="form.emergencyName" placeholder="如：家属 / 朋友" required />
        </div>

        <div class="form-field wide">
          <label>紧急联系人电话</label>
          <input v-model="form.emergencyPhone" inputmode="tel" placeholder="紧急联络号码" />
        </div>
      </div>

      <template #footer>
        <button class="secondary-button" @click="dialog = false">取消</button>
        <button class="primary-button" @click="save">保存出行人</button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.account-page {
  background: var(--bg-canvas);
  min-height: calc(100vh - 64px);
}

.action-divider {
  color: var(--border-strong);
  margin: 0 6px;
  font-size: 11px;
}

.delete-btn {
  color: var(--danger-text);
}

.delete-btn:hover {
  color: var(--danger-red);
}

.dialog-form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.dialog-form-grid .wide {
  grid-column: 1 / -1;
}

.req {
  color: var(--danger-red);
}

@media (max-width: 600px) {
  .dialog-form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
