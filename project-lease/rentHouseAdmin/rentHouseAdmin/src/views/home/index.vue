<template>
  <div class="qingyu-dashboard">
    <!-- 欢迎区 -->
    <el-card shadow="never" class="welcome-card">
      <div class="welcome-inner">
        <div class="welcome-left">
          <div class="welcome-icon">
            <svg-icon name="qingyu-logo" size="48px" style="color: #14B8A6" />
          </div>
          <div class="welcome-text">
            <p class="greeting">{{ timeFix() }}，{{ userInfo?.name || '管理员' }}</p>
            <p class="subtitle">{{ welcomeText }}</p>
          </div>
        </div>
        <div class="welcome-stats">
          <div class="stat-item">
            <span class="stat-value">{{ todayAppointments }}</span>
            <span class="stat-label">今日预约</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ activeAgreements }}</span>
            <span class="stat-label">在租合同</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ totalRooms }}</span>
            <span class="stat-label">房源总数</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 快捷入口 -->
    <el-row :gutter="16" class="quick-entries">
      <el-col :xs="12" :sm="8" :md="6" v-for="entry in quickEntries" :key="entry.path">
        <el-card shadow="never" class="entry-card" @click="router.push(entry.path)">
          <div class="entry-inner">
            <el-icon :size="28" :color="entry.color"><component :is="entry.icon" /></el-icon>
            <span class="entry-label">{{ entry.label }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待处理状态 -->
    <el-card shadow="never" class="status-card">
      <template #header>
        <span class="card-title">待处理事项</span>
      </template>
      <el-empty v-if="pendingItems.length === 0" description="暂无待处理事项" :image-size="80" />
      <div v-else class="pending-list">
        <div class="pending-item" v-for="item in pendingItems" :key="item.label">
          <el-tag :type="item.type" size="small">{{ item.label }}</el-tag>
          <span class="pending-count">{{ item.count }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { welcome } from '@/utils'

const router = useRouter()
const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const welcomeText = ref(welcome())

const todayAppointments = ref('--')
const activeAgreements = ref('--')
const totalRooms = ref('--')

const quickEntries = [
  { icon: 'OfficeBuilding', label: '公寓管理', path: '/apartmentManagement/apartmentManagement/apartmentManagement', color: '#0F766E' },
  { icon: 'House', label: '房间管理', path: '/apartmentManagement/roomManagement/roomManagement', color: '#14B8A6' },
  { icon: 'Clock', label: '预约管理', path: '/rentManagement/appointment/appointment', color: '#0369A1' },
  { icon: 'Document', label: '租约管理', path: '/agreementManagement/agreement/agreement', color: '#0F766E' },
  { icon: 'List', label: '属性管理', path: '/apartmentManagement/attributeManagement/attributeManagement', color: '#14B8A6' },
  { icon: 'UserFilled', label: '用户管理', path: '/userManagement/userManagement', color: '#0369A1' },
]

type PendingItem = {
  label: string
  count: number
  type: '' | 'success' | 'warning' | 'info' | 'danger'
}

const pendingItems = ref<PendingItem[]>([])

function timeFix() {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
}
</script>

<style scoped lang="scss">
.qingyu-dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.welcome-card {
  border: 1px solid #D6F5EF;
  border-radius: 10px;

  :deep(.el-card__body) {
    padding: 24px;
  }

  .welcome-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 16px;
  }

  .welcome-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .welcome-icon {
    width: 56px;
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #F0FDFA;
    border-radius: 14px;
  }

  .greeting {
    margin: 0;
    font-size: 20px;
    font-weight: 700;
    color: #134E4A;
  }

  .subtitle {
    margin: 4px 0 0;
    font-size: 14px;
    color: #64748B;
  }

  .welcome-stats {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .stat-value {
    font-size: 24px;
    font-weight: 800;
    color: #0F766E;
  }

  .stat-label {
    font-size: 12px;
    color: #64748B;
    margin-top: 2px;
  }

  .stat-divider {
    width: 1px;
    height: 36px;
    background: #D6F5EF;
  }
}

.quick-entries {
  margin-top: 0 !important;
}

.entry-card {
  border: 1px solid #D6F5EF;
  border-radius: 10px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(15, 118, 110, 0.1);
    transform: translateY(-2px);
  }

  :deep(.el-card__body) {
    padding: 20px;
  }

  .entry-inner {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
  }

  .entry-label {
    font-size: 14px;
    font-weight: 600;
    color: #134E4A;
  }
}

.status-card {
  border: 1px solid #D6F5EF;
  border-radius: 10px;

  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #D6F5EF;
  }

  .card-title {
    font-size: 15px;
    font-weight: 700;
    color: #134E4A;
  }
}

.pending-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pending-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;

  .pending-count {
    font-size: 16px;
    font-weight: 700;
    color: #134E4A;
  }
}
</style>
