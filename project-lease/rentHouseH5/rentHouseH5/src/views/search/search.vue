<template>
  <div class="search-hero main-container">
    <img src="/qingyu-icon.svg" alt="青寓" class="brand-icon" />
    <div>
      <div class="brand-title">青寓</div>
      <div class="brand-subtitle">找到离生活更近的家</div>
    </div>
  </div>
  <SearchBar :confirm-callback="confirmCallback"></SearchBar>

  <div class="main-container">
    <PullDownRefreshContainer
      :request="getRoomListHandler"
      ref="pullDownRefreshContainerRef"
      class="min-h-[70vh]"
    >
      <template v-if="roomList?.length">
        <RoomCard
          v-for="item in roomList"
          :key="item.id"
          :data="item"
        ></RoomCard>
      </template>
    </PullDownRefreshContainer>
  </div>
</template>

<script setup lang="ts" name="Search">
import SearchBar from "@/components/SearchBar/SearchBar.vue";
import type { RoomInterface, RoomListQueryInterface } from "@/api/search/types";
import { ref } from "vue";
import { getRoomList } from "@/api/search";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import type { ReqPage } from "@/api/types";
import RoomCard from "@/components/RoomCard/RoomCard.vue";
// 搜索参数
const searchParams = ref<Partial<RoomListQueryInterface>>({});
const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();
// 房间列表
const roomList = ref<RoomInterface[]>([]);
// 点击查找，搜索条件
function confirmCallback(params: RoomListQueryInterface) {
  //    收集参数，调用接口
  let targetObi = {};
  //   去除空值
  for (let key in params) {
    if ((params as any)[key] !== "") {
      (targetObi as any)[key] = (params as any)[key];
    }
  }
  searchParams.value = targetObi;
  pullDownRefreshContainerRef.value?.onRefresh();
}
// 分页获取房间的信息
async function getRoomListHandler(pageInfo: ReqPage) {
  //   调用接口
  const { data } = await getRoomList({ ...searchParams.value, ...pageInfo });
  console.log("pageInfo.current", pageInfo.current);
  if (pageInfo.current === 1) {
    roomList.value = data.records;
  } else {
    roomList.value = [...roomList.value, ...data.records];
  }
  pullDownRefreshContainerRef.value?.setFinished(
    roomList.value.length >= data.total
  );
}
</script>

<style scoped lang="less">
.search-hero {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-top: 16px;
  padding-bottom: 4px;
  color: var(--qingyu-text);
}

.brand-icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  box-shadow: 0 10px 26px rgba(15, 118, 110, 0.16);
}

.brand-title {
  color: var(--qingyu-primary);
  font-size: 22px;
  font-weight: 900;
  line-height: 1.2;
}

.brand-subtitle {
  margin-top: 3px;
  color: var(--qingyu-muted);
  font-size: 13px;
}
</style>
