<template>
  <div class="admin-page">
    <section class="profile-card">
      <div class="profile-main">
        <img :src="selectedAvatar" alt="avatar" class="avatar" />
        <div>
          <p class="eyebrow">个人信息</p>
          <h1>{{ userInfo?.username || '-' }}</h1>
          <p class="meta">角色：{{ userInfo?.role || '-' }}</p>
          <p class="meta">上次登录时间：{{ userInfo?.lastLoginTime || '-' }}</p>
          <p class="meta">
            可访问房间：
            {{ userInfo?.roomNames?.length ? userInfo.roomNames.join('，') : (isAdmin ? '全部房间' : '未分配房间') }}
          </p>
        </div>
      </div>

      <div class="profile-actions">
        <button v-if="isAdmin" class="secondary-btn" @click="showRoomModal = true">房间管理</button>
        <button class="secondary-btn" @click="showAvatarModal = true">更换头像</button>
        <button class="secondary-btn" @click="showPasswordModal = true">修改密码</button>
        <button class="danger-btn" @click="handleLogout">退出登录</button>
      </div>
    </section>

    <section v-if="isAdmin" class="panel">
      <div class="panel-header">
        <div>
          <h2>房间分配</h2>
          <p>管理员可为普通用户分配一个或多个房间。</p>
        </div>
      </div>

      <div class="user-grid">
        <article v-for="user in manageableUsers" :key="user.userId" class="user-card">
          <div class="user-card-top">
            <div>
              <h3>{{ user.username }}</h3>
              <p>{{ user.roomNames?.length ? user.roomNames.join('，') : '未分配房间' }}</p>
            </div>
          </div>

          <el-select
            v-model="roomSelection[user.userId]"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="请选择房间"
            style="width: 100%"
          >
            <el-option
              v-for="location in locations"
              :key="location.id"
              :label="location.displayName"
              :value="location.id"
            />
          </el-select>

          <button class="primary-btn" @click="saveRoomAssignment(user)" :disabled="savingUsers[user.userId]">
            {{ savingUsers[user.userId] ? '保存中...' : '保存房间分配' }}
          </button>
        </article>
      </div>
    </section>

    <div v-if="showRoomModal" class="modal-overlay" @click="closeRoomModal">
      <div class="modal-container room-modal" @click.stop>
        <div class="modal-header">
          <h2>房间管理</h2>
          <button class="close-button" @click="closeRoomModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="room-manager-layout">
            <section class="room-manager-card">
              <div class="section-head">
                <h3>新增房间</h3>
                <p>按楼栋、楼层和房间号创建房间。</p>
              </div>
              <form class="room-form" @submit.prevent="submitCreateLocation">
                <label class="modal-field">
                  <span>楼栋</span>
                  <input v-model.trim="locationForm.buildingname" type="text" maxlength="20" placeholder="如 A" />
                </label>
                <label class="modal-field">
                  <span>楼层</span>
                  <input v-model.number="locationForm.floornumber" type="number" min="1" placeholder="如 3" />
                </label>
                <label class="modal-field">
                  <span>房间号</span>
                  <input v-model.number="locationForm.roomnumber" type="number" min="1" placeholder="如 305" />
                </label>
                <label class="modal-field">
                  <span>描述</span>
                  <input v-model.trim="locationForm.description" type="text" maxlength="100" placeholder="可选" />
                </label>
                <button class="primary-btn" type="submit" :disabled="creatingLocation">
                  {{ creatingLocation ? '创建中...' : '新增房间' }}
                </button>
              </form>
            </section>

            <section class="room-manager-card">
              <div class="section-head">
                <h3>现有房间</h3>
                <p>删除前请先移除该房间下的设备和历史告警依赖。</p>
              </div>
              <div v-if="locations.length" class="room-list">
                <article v-for="location in locations" :key="location.id" class="room-item">
                  <div>
                    <h4>{{ location.displayName }}</h4>
                    <p>{{ location.description || '暂无描述' }}</p>
                  </div>
                  <button
                    class="danger-btn danger-btn--small"
                    :disabled="deletingLocationIds[location.id]"
                    @click="removeLocation(location)"
                  >
                    {{ deletingLocationIds[location.id] ? '删除中...' : '删除' }}
                  </button>
                </article>
              </div>
              <div v-else class="empty-state">当前没有房间。</div>
            </section>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showPasswordModal" @click="showPasswordModal = false">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>修改密码</h2>
          <button class="close-button" @click="showPasswordModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <form id="password-form" @submit.prevent="submitPasswordChange">
            <label class="modal-field">
              <span>当前密码</span>
              <input v-model="currentPassword" type="password" autocomplete="current-password" />
            </label>
            <label class="modal-field">
              <span>新密码</span>
              <input v-model="newPassword" type="password" autocomplete="new-password" />
            </label>
            <label class="modal-field">
              <span>确认密码</span>
              <input v-model="confirmPassword" type="password" autocomplete="new-password" />
            </label>
          </form>
          <button class="primary-btn" type="submit" form="password-form">提交</button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showAvatarModal" @click="showAvatarModal = false">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>选择头像</h2>
          <button class="close-button" @click="showAvatarModal = false">&times;</button>
        </div>
        <div class="avatar-grid">
          <button
            v-for="avatar in availableAvatars"
            :key="avatar"
            class="avatar-item"
            @click="selectAvatar(avatar)"
          >
            <img :src="avatar" alt="avatar choice" class="avatar-thumbnail" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dataService from '../utils/dataService'
import userService from '../utils/userService'

const router = useRouter()

const userInfo = ref(null)
const users = ref([])
const locations = ref([])
const roomSelection = ref({})
const savingUsers = ref({})

const showAvatarModal = ref(false)
const showPasswordModal = ref(false)
const showRoomModal = ref(false)
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const creatingLocation = ref(false)
const deletingLocationIds = ref({})

const locationForm = ref({
  buildingname: '',
  floornumber: null,
  roomnumber: null,
  description: ''
})

const availableAvatars = [
  '/src/assets/avatar/fall.bmp',
  '/src/assets/avatar/avatar1-1.jpg',
  '/src/assets/avatar/avatar2-1.jpg',
  '/src/assets/avatar/avatar3-1.jpg',
  '/src/assets/avatar/avatar4-1.jpg',
  '/src/assets/avatar/avatar5-1.jpg'
]

const selectedAvatar = ref('/src/assets/avatar/fall.bmp')

const isAdmin = computed(() => ['admin', 'super_admin'].includes(userInfo.value?.role))
const manageableUsers = computed(() => users.value.filter((user) => !['admin', 'super_admin'].includes(user.role)))

const formatLocation = (location) => `${location.buildingname}-${location.floornumber}F-${location.roomnumber}`

const hydrateRoomSelection = () => {
  const nextSelection = {}
  manageableUsers.value.forEach((user) => {
    nextSelection[user.userId] = [...(user.roomIds || [])]
  })
  roomSelection.value = nextSelection
}

const resetLocationForm = () => {
  locationForm.value = {
    buildingname: '',
    floornumber: null,
    roomnumber: null,
    description: ''
  }
}

const loadCurrentUser = async () => {
  const user = await userService.fetchCurrentUser()
  userInfo.value = user
  selectedAvatar.value = user?.avatar || '/src/assets/avatar/fall.bmp'
}

const loadLocations = async () => {
  const locationList = await dataService.getAllLocations()
  locations.value = (locationList || []).map((location) => ({
    ...location,
    displayName: formatLocation(location)
  }))
}

const loadUsers = async () => {
  if (!isAdmin.value) {
    users.value = []
    roomSelection.value = {}
    return
  }

  users.value = await userService.getAllUsers()
  hydrateRoomSelection()
}

const reloadAdminData = async () => {
  await loadLocations()
  await loadUsers()
}

const closeRoomModal = () => {
  showRoomModal.value = false
  resetLocationForm()
}

const submitPasswordChange = async () => {
  if (!userInfo.value) {
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  if (newPassword.value.length < 6) {
    ElMessage.error('新密码长度不能少于 6 位')
    return
  }

  const result = await userService.changePassword(
    userInfo.value.userId,
    currentPassword.value,
    newPassword.value
  )

  if (result === true) {
    ElMessage.success('密码修改成功')
    showPasswordModal.value = false
    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
    return
  }

  ElMessage.error(result)
}

const selectAvatar = async (avatar) => {
  if (!userInfo.value) {
    return
  }

  const result = await userService.updateUserAvatar(userInfo.value.userId, avatar)
  if (result) {
    selectedAvatar.value = avatar
    await loadCurrentUser()
    ElMessage.success('头像更新成功')
  } else {
    ElMessage.error('头像更新失败')
  }
  showAvatarModal.value = false
}

const saveRoomAssignment = async (user) => {
  savingUsers.value = { ...savingUsers.value, [user.userId]: true }

  try {
    const ok = await userService.assignRooms(user.userId, roomSelection.value[user.userId] || [])
    if (!ok) {
      ElMessage.error(`更新 ${user.username} 的房间分配失败`)
      return
    }

    ElMessage.success(`已更新 ${user.username} 的房间分配`)
    await loadUsers()
  } finally {
    savingUsers.value = { ...savingUsers.value, [user.userId]: false }
  }
}

const submitCreateLocation = async () => {
  const buildingname = locationForm.value.buildingname.trim()
  const floornumber = Number(locationForm.value.floornumber)
  const roomnumber = Number(locationForm.value.roomnumber)

  if (!buildingname) {
    ElMessage.error('请输入楼栋')
    return
  }
  if (!Number.isInteger(floornumber) || floornumber <= 0) {
    ElMessage.error('请输入正确的楼层')
    return
  }
  if (!Number.isInteger(roomnumber) || roomnumber <= 0) {
    ElMessage.error('请输入正确的房间号')
    return
  }

  creatingLocation.value = true
  try {
    const created = await dataService.createLocation({
      buildingname,
      floornumber,
      roomnumber,
      description: locationForm.value.description.trim()
    })

    if (!created) {
      ElMessage.error('新增房间失败')
      return
    }

    ElMessage.success(`已新增房间 ${formatLocation(created)}`)
    resetLocationForm()
    await reloadAdminData()
  } finally {
    creatingLocation.value = false
  }
}

const removeLocation = async (location) => {
  try {
    await ElMessageBox.confirm(
      `确定删除房间 ${location.displayName} 吗？`,
      '删除房间',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }

  deletingLocationIds.value = { ...deletingLocationIds.value, [location.id]: true }
  try {
    const result = await dataService.deleteLocation(location.id)
    if (!result.success) {
      ElMessage.error(result.msg || '删除房间失败')
      return
    }

    ElMessage.success(`已删除房间 ${location.displayName}`)
    await reloadAdminData()
  } finally {
    deletingLocationIds.value = { ...deletingLocationIds.value, [location.id]: false }
  }
}

const handleLogout = () => {
  userService.logout()
  router.replace('/login')
}

onMounted(async () => {
  await loadCurrentUser()
  await loadLocations()
  await loadUsers()
})
</script>

<style scoped>
.admin-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card,
.panel {
  background: #fff;
  border-radius: 20px;
  box-shadow: var(--card-shadow);
  padding: 28px;
}

.profile-card {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: center;
}

.profile-main {
  display: flex;
  align-items: center;
  gap: 22px;
}

.avatar {
  width: 112px;
  height: 112px;
  border-radius: 50%;
  object-fit: cover;
}

.eyebrow {
  margin: 0 0 8px;
  color: #6b7d8b;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  font-size: 12px;
}

.profile-main h1,
.panel-header h2,
.user-card h3,
.section-head h3,
.room-item h4 {
  margin: 0;
}

.meta,
.panel-header p,
.user-card p,
.section-head p,
.room-item p {
  color: #607280;
}

.profile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

.primary-btn,
.secondary-btn,
.danger-btn {
  min-width: 132px;
  height: 42px;
  border-radius: 12px;
  border: 0;
  cursor: pointer;
}

.primary-btn {
  background: #18456b;
  color: #fff;
}

.secondary-btn {
  background: #edf4fa;
  color: #18456b;
}

.danger-btn {
  background: #b93d3d;
  color: #fff;
}

.danger-btn--small {
  min-width: 84px;
  height: 38px;
}

.primary-btn:disabled,
.secondary-btn:disabled,
.danger-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 18px;
}

.user-card {
  border: 1px solid #e5edf2;
  border-radius: 18px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  width: min(560px, calc(100vw - 32px));
  background: #fff;
  border-radius: 18px;
  overflow: hidden;
}

.room-modal {
  width: min(920px, calc(100vw - 32px));
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 22px;
  border-bottom: 1px solid #edf1f4;
}

.close-button {
  border: 0;
  background: transparent;
  font-size: 28px;
  cursor: pointer;
}

.modal-body {
  padding: 24px;
}

.modal-field {
  display: block;
  margin-bottom: 16px;
}

.modal-field span {
  display: block;
  margin-bottom: 8px;
}

.modal-field input {
  width: 100%;
  height: 42px;
  border-radius: 12px;
  border: 1px solid #c9d6df;
  padding: 0 12px;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  padding: 24px;
}

.avatar-item {
  border: 0;
  background: transparent;
  padding: 0;
  cursor: pointer;
}

.avatar-thumbnail {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 16px;
  object-fit: cover;
}

.room-manager-layout {
  display: grid;
  grid-template-columns: minmax(260px, 320px) minmax(0, 1fr);
  gap: 18px;
}

.room-manager-card {
  border: 1px solid #e5edf2;
  border-radius: 18px;
  padding: 18px;
  background: #fbfdff;
}

.section-head {
  margin-bottom: 18px;
}

.room-form {
  display: flex;
  flex-direction: column;
}

.room-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 420px;
  overflow: auto;
}

.room-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  border: 1px solid #e5edf2;
  border-radius: 14px;
  padding: 14px 16px;
  background: #fff;
}

.empty-state {
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7d8b;
  border: 1px dashed #c9d6df;
  border-radius: 14px;
}

@media (max-width: 900px) {
  .profile-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .profile-actions {
    justify-content: flex-start;
  }

  .room-manager-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .profile-main {
    flex-direction: column;
    align-items: flex-start;
  }

  .avatar-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .room-item {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
