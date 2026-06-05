<template>
  <van-card
    class="qingyu-apartment-card"
    :title="`${data.name}`"
    :desc="`${data.provinceName} ${data.cityName} ${data.districtName}`"
    @click="goApartmentDetail"
  >
    <template #price>
      <!--      价格-->
      <span class="price-symbol">￥</span>
      <span class="apartment-price">{{ data.minRent }}</span>
      <span class="price-unit">/月起</span>
    </template>
    <!--    thumb-->
    <template #thumb>
      <van-image
        class="w-full h-full object-cover"
        :src="data.graphVoList?.[0]?.url || '失败'"
      >
        <template v-slot:error>加载失败</template>
        <template v-slot:loading>
          <van-loading type="spinner" size="20" />
        </template>
      </van-image>
    </template>
    <template #tags>
      <van-tag
        class="apartment-tag last:mr-0 mr-[5px]"
        plain
        v-for="item in data.labelInfoList"
        :key="item.id"
        type="primary"
        >{{ item.name }}
      </van-tag>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import type { ApartmentInterface } from "@/api/search/types";
import type { PropType } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

const props = defineProps({
  // 房间的信息数据
  data: {
    type: Object as PropType<ApartmentInterface>,
    default: () => ({}),
    readOnly: true
  }
});
// 跳转到公寓的详情页面
const goApartmentDetail = () => {
  router.push({ path: "/apartmentDetail", query: { id: props.data.id } });
};
</script>

<style scoped lang="less">
.qingyu-apartment-card {
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

  :deep(.van-card__title) {
    color: var(--qingyu-text);
    font-size: 15px;
    font-weight: 800;
  }

  :deep(.van-card__desc) {
    color: var(--qingyu-muted);
    line-height: 1.6;
  }
}

.price-symbol,
.apartment-price,
.price-unit {
  color: var(--qingyu-cta);
}

.price-symbol {
  font-size: 13px;
  font-weight: 700;
}

.apartment-price {
  font-size: 21px;
  font-weight: 900;
}

.price-unit {
  margin-left: 2px;
  font-size: 12px;
}

.apartment-tag {
  margin-top: 8px;
}
</style>
