<template>
  <van-nav-bar
    v-if="isShowNavBar"
    :title="String(route.meta.title ?? '')"
    fixed
    placeholder
    left-arrow
    @click-left="onClickLeft"
    @click-right="onClickRight"
  >
    <template #right>
      <svg-icon class="text-[18px]" :name="useDarkMode() ? 'light' : 'dark'" />
    </template>
  </van-nav-bar>
</template>
<script setup lang="ts">
import { useDarkMode, useToggleDarkMode } from "@/hooks/useToggleDarkMode";
import { computed } from "vue";
import tabBarRoutes from "@/router/tabBarRoutes";
import otherRoutes from "@/router/otherRoutes";
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const onClickRight = () => {
  useToggleDarkMode();
};

const onClickLeft = () => {
  router.back();
};

const isShowNavBar = computed(() => {
  const showNavBarRoutePathList = [...tabBarRoutes, ...otherRoutes]
    .filter(item => item?.meta?.isShowNavBar)
    .map(item => item.path);
  return showNavBarRoutePathList.includes(route.path);
});
</script>

<style scoped></style>
