<template>
  <div class="user-container">
    <div class="user h-[30vh] flex flex-col justify-center items-center">
      <van-image
        @click="
          showImagePreview([userStore.userInfo?.avatarUrl || defaultAvatarUrl])
        "
        round
        width="30vw"
        height="30vw"
        :src="userStore.userInfo?.avatarUrl || defaultAvatarUrl"
      >
        <template v-slot:error>加载失败</template>
      </van-image>
      <div class="brand-name">青寓</div>
      <div class="mt-[8px] font-bold text-[16px]">
        {{ userStore.userInfo?.nickname || "测试" }}
      </div>
    </div>
    <div class="main-container user-actions flex justify-around mt-[30px]">
      <div
        v-for="item in navList"
        :key="item.path"
        class="user-action flex flex-col justify-center items-center"
        @click="router.push(item.path)"
      >
        <SvgIcon :name="item.icon" size="50" />
        <span>{{ item.name }}</span>
      </div>
    </div>
    <div class="main-container flex justify-center mt-[120px]">
      <!--      退出登录-->
      <van-button type="primary" class="w-[50vw]" @click="logoutHandle"
        >退出登录</van-button
      >
    </div>
  </div>
</template>
<script setup lang="ts" name="UserCenter">
import { useUserStore } from "@/store/modules/user";
import { showImagePreview } from "vant";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
const defaultAvatarUrl = "/qingyu-icon.svg";
console.log("router.currentRoute.value.path", router);
const navList = ref([
  {
    icon: "历史",
    name: "浏览历史",
    path: "/browsingHistory"
  },
  {
    icon: "预约",
    name: "我的预约",
    path: "/myAppointment"
  },
  {
    icon: "合同",
    name: "我的租约",
    path: "/myAgreement"
  }
]);
const userStore = useUserStore();
// 退出登陆
const logoutHandle = () => {
  userStore.Logout();
  // 清空路由浏览历史记录
  router.replace("/");
};
console.log(userStore);
onMounted(() => {
  userStore.GetInfoAction();
});
</script>

<style scoped lang="less">
.user {
  background: var(--van-primary-background-color);
  color: var(--qingyu-text);
  box-shadow: inset 0 -1px 0 var(--qingyu-border);
}

.brand-name {
  margin-top: 10px;
  color: var(--qingyu-primary);
  font-size: 18px;
  font-weight: 900;
  letter-spacing: 0;
}

.user-actions {
  gap: 12px;
}

.user-action {
  min-width: 92px;
  min-height: 92px;
  padding: 14px 10px;
  border: 1px solid var(--qingyu-border);
  border-radius: 18px;
  background: var(--qingyu-surface);
  box-shadow: var(--qingyu-shadow);
  color: var(--qingyu-text);
}

.user-action span {
  margin-top: 8px;
  font-weight: 700;
}
</style>
