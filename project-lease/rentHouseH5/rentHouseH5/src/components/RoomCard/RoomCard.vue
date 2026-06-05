<template>
  <van-card @click="goRoomDetail" class="qingyu-room-card">
    <!--      title-->
    <template #title>
      <slot name="title">
        <!--      标题-->
        <span class="room-title">{{
          `${data.apartmentInfo?.name || ""} ${data.roomNumber}房间`
        }}</span>
      </slot>
    </template>
    <!--      desc-->
    <template #desc>
      <slot name="desc">
        <div>
          <span class="room-location">{{
            `${data?.apartmentInfo?.provinceName || ""} ${
              data?.apartmentInfo?.cityName || ""
            } ${data?.apartmentInfo?.districtName || ""}`
          }}</span>
        </div>
      </slot>
    </template>
    <!--    price-->
    <template #price>
      <slot name="price">
        <!--      价格-->
        <span class="price-symbol">￥</span>
        <span class="room-price">{{ data.rent }}</span>
        <span class="price-unit">/月</span>
      </slot>
    </template>
    <!--    thumb-->
    <template #thumb>
      <slot name="thumb">
        <van-image
          class="w-full h-full object-cover"
          :src="data.graphVoList?.[0]?.url || '失败'"
        >
          <template v-slot:error>加载失败</template>
          <template v-slot:loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
      </slot>
    </template>
    <!--    tags-->
    <template #tags>
      <slot name="tags">
        <van-tag
          class="room-tag last:mr-0 mr-[5px]"
          plain
          v-for="item in data?.labelInfoList"
          :key="item.id"
          type="primary"
          >{{ item.name }}
        </van-tag>
      </slot>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import type { RoomInterface } from "@/api/search/types";
import type { PropType } from "vue";
import { useRouter } from "vue-router";
//实际上只需要这些属性
// export interface RoomCardDataProps
//   extends Pick<
//     RoomInterface,
//     "id" | "roomNumber" | "rent" | "graphVoList" | "labelInfoList"
//   > {
//   apartmentInfo: Pick<
//     RoomInterface["apartmentInfo"],
//     "name" | "provinceName" | "cityName" | "districtName"
//   >;
// }
const router = useRouter();
const props = defineProps({
  // 房间的信息数据
  data: {
    type: Object as PropType<RoomInterface>,
    default: () => ({}),
    readOnly: true
  }
});
// 跳转到房间的详情页面
const goRoomDetail = () => {
  router.push({ path: "/roomDetail", query: { id: props.data.id } });
};
</script>

<style scoped lang="less">
.qingyu-room-card {
  margin: 12px 0;
  overflow: hidden;
  border: 1px solid var(--qingyu-border);
  border-radius: 20px;
  background: var(--qingyu-surface);
  box-shadow: var(--qingyu-shadow);

  :deep(.van-card__thumb) {
    width: 108px;
    height: 108px;
    overflow: hidden;
    border-radius: 16px;
  }

  :deep(.van-card__content) {
    min-height: 108px;
  }
}

.room-title {
  color: var(--qingyu-text);
  font-size: 15px;
  font-weight: 800;
  line-height: 1.35;
}

.room-location {
  color: var(--qingyu-muted);
  font-size: 12px;
  line-height: 1.6;
}

.price-symbol,
.room-price,
.price-unit {
  color: var(--qingyu-cta);
}

.price-symbol {
  font-size: 13px;
  font-weight: 700;
}

.room-price {
  font-size: 22px;
  font-weight: 900;
}

.price-unit {
  margin-left: 2px;
  font-size: 12px;
}

.room-tag {
  margin-top: 8px;
}
</style>
