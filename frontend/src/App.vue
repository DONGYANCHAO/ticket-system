<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
  // 初始化用户信息
  if (userStore.token) {
    userStore.getUserInfo()
  }
})
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}
</style>
