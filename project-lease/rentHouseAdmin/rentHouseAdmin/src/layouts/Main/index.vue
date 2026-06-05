<template>
  <section class="app-main-container">
    <div class="app-mian-height">
      <router-view v-slot="{ Component, route }" v-if="isShow" :key="key">
        <transition appear name="fade-transform" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </div>
  </section>
</template>

<script lang="ts">
import { computed, defineComponent, nextTick, ref, watch } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { useRoute } from 'vue-router'

export default defineComponent({
  setup() {
    const settingsStore = useSettingsStore()
    const isShow = ref(true)
    const route = useRoute()
    const key = computed(() => {
      return route.path + Math.random()
    })
    watch(
      () => settingsStore.refresh,
      () => {
        isShow.value = false
        nextTick(() => {
          isShow.value = true
        })
      },
    )
    return { isShow, key }
  },
})
</script>

<style scoped lang="scss">
.app-main-container {
  background: #F6FAF9;
  min-height: 100%;
}

.app-mian-height {
  min-height: calc(100vh - #{$base-nav-bar-height} - #{$base-tabs-bar-height} - 56px - 40px);
  background: #F6FAF9;
  padding: 16px;
}
</style>
