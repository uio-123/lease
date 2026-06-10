<template>
  <van-skeleton :row="20" :loading="!appointmentList">
    <div class="p-[10px]">
      <van-card
        class="qingyu-list-card"
        v-for="item in appointmentList"
        :key="item.id"
        @click="goAppointmentDetail(item)"
      >
        <!--      title-->
        <template #title>
          <h2 class="text-[15px] font-bold">{{ item.apartmentName }}</h2>
        </template>
        <!--    thumb-->
        <template #thumb>
          <van-image
            class="w-full h-full object-cover"
            :src="item.graphVoList?.[0]?.url || placeholderImg"
          >
            <template v-slot:error>
          <img :src="placeholderImg" class="w-full h-full object-cover" />
        </template>
            <template v-slot:loading>
              <van-loading type="spinner" size="20" />
            </template>
          </van-image>
        </template>
        <!--      tags-->
        <template #tags>
          <van-tag
            v-if="item.appointmentStatus === AppointmentStatus.WAITING"
            class="mt-[10px]"
            type="success"
            size="medium"
            >{{
              getLabelByValue(AppointmentStatusMap, item.appointmentStatus)
            }}</van-tag
          >
          <van-tag v-else class="mt-[10px]" type="default" size="medium">{{
            getLabelByValue(AppointmentStatusMap, item.appointmentStatus)
          }}</van-tag>
        </template>
        <!--      price-->
        <template #price>
          <div class="flex justify-between">
            <div class="appointment-time">预约时间</div>
            <div class="appointment-time">
              {{ item.appointmentTime }}
            </div>
          </div>
        </template>
      </van-card>
    </div>

    <van-empty v-if="!appointmentList || appointmentList.length <= 0" description="搜索不到" />
  </van-skeleton>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import placeholderImg from "@/assets/placeholder.svg";
import type { AppointmentItemInterface } from "@/api/search/types";
import { getMyAppointmentList } from "@/api/search";
import {
  AppointmentStatus,
  AppointmentStatusMap,
  getLabelByValue
} from "@/enums/constEnums";

const router = useRouter();
// 预约列表
const appointmentList = ref<AppointmentItemInterface[]>();
// 获取预约列表
const getAppointmentListHandle = async () => {
  const { data } = await getMyAppointmentList();
  appointmentList.value = data;
};
// 跳转到公寓的详情页面
const goAppointmentDetail = (item: AppointmentItemInterface) => {
  router.push({
    path: "/appointment",
    query: { appointmentId: item.id }
  });
};
onMounted(async () => {
  await getAppointmentListHandle();
});
</script>

<style scoped lang="less">
.base-info-title {
  background-color: var(--van-primary-background-color);
  font-weight: bold;
  //color: white;
}

.qingyu-list-card {
  overflow: hidden;
  border: 1px solid var(--qingyu-border);
  border-radius: 20px;
  background: var(--qingyu-surface);
  box-shadow: var(--qingyu-shadow);
}

.qingyu-list-card :deep(.van-card__thumb) {
  overflow: hidden;
  border-radius: 16px;
}

.appointment-time {
  color: var(--qingyu-cta);
  font-size: 12px;
  font-weight: 800;
}
</style>
