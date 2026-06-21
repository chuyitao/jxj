<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }" :style="{ background: sideTheme === 'theme-dark' ? 'linear-gradient(135deg, #243B4A 0%, #1A3A4A 100%)' : variables.menuLightBackground }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img :src="logo" class="sidebar-logo" alt="静心栖" />
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img :src="logo" class="sidebar-logo" alt="静心栖" />
        <span class="sidebar-title">{{ title }}</span>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
import variables from '@/assets/styles/variables.module.scss'
import logo from '@/assets/logo/logo.svg'
import useSettingsStore from '@/store/modules/settings'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const title = import.meta.env.VITE_APP_TITLE;
const settingsStore = useSettingsStore();
const sideTheme = computed(() => settingsStore.sideTheme);
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 0 12px;
    text-decoration: none;

    & .sidebar-logo {
      display: block;
      width: 32px;
      height: 32px;
      flex-shrink: 0;
      border-radius: 8px;
    }

    & .sidebar-title {
      margin: 0;
      padding: 0;
      line-height: 1;
      font-size: 18px;
      font-weight: 700;
      font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
      letter-spacing: 3px;
      white-space: nowrap;
      background: linear-gradient(90deg, #FFB7D5, #2EC4B6);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  &.collapse {
    .sidebar-logo-link {
      padding: 0;
      justify-content: center;
    }
  }
}
</style>
