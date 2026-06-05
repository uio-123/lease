<template>
  <div class="logo-container flex-center">
    <a href="/">
      <svg-icon name="qingyu-logo" size="32px" style="flex-shrink: 0; color: #14B8A6;" />
      <h1 class="title" v-if="!collapse">
        {{ getEnvByName('VITE_APP_TITLE') }}
      </h1>
    </a>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { getEnvByName } from '@/utils/getEnv'
export default defineComponent({
  methods: { getEnvByName },
  setup() {
    const settingsStore = useSettingsStore()
    const collapse = computed(() => settingsStore.collapse)
    return { collapse }
  },
})
</script>

<style scoped lang="scss">
.logo-container {
  position: relative;
  display: flex;
  justify-content: center;

  // padding-left: 24px;
  height: 60px;
  overflow: hidden;
  line-height: 60px;
  background: transparent;

  .title {
    display: inline-block;
    margin-left: 12px;
    font-family: Avenir, 'Helvetica Neue', Arial, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 600;
    color: #fff;
    vertical-align: middle;
  }
}
</style>
