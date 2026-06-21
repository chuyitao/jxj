<template>
  <div class="app-container home">
    <div class="welcome-hero">
      <img src="@/assets/logo/logo.svg" alt="静心栖" class="hero-logo" />
      <h1 class="hero-title">静心栖</h1>
      <p class="hero-subtitle">智慧养老管理平台</p>
      <p class="hero-desc">用心守护每一位长者的安康与幸福，让科技传递温暖。</p>
    </div>
    <el-row :gutter="24" class="feature-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in features" :key="item.title">
        <el-card class="feature-card" shadow="hover">
          <div class="feature-icon" :style="{ background: item.bg }">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="24" style="margin-top: 24px;">
      <el-col :xs="24" :md="24">
        <el-card class="info-card">
          <template #header>
            <span>系统信息</span>
          </template>
          <div class="info-item">
            <span class="label">系统名称</span>
            <span class="value">静心栖管理系统</span>
          </div>
          <div class="info-item">
            <span class="label">当前版本</span>
            <span class="value">v{{ version }}</span>
          </div>
          <div class="info-item">
            <span class="label">技术栈</span>
            <span class="value">Vue 3 + Element Plus + Spring Boot</span>
          </div>
          <div class="info-item">
            <span class="label">使用提示</span>
            <span class="value">请通过左侧菜单进入各功能模块</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
const version = ref('3.8.7')

const features = [
  { icon: '🏠', title: '入住管理', desc: '一站式入住登记与床位管理', bg: 'linear-gradient(135deg, #FF9EC7, #FFB7D5)' },
  { icon: '💚', title: '健康评估', desc: '智能评估长者健康状况', bg: 'linear-gradient(135deg, #2EC4B6, #5EDDD4)' },
  { icon: '🤖', title: '智能照护', desc: 'AI 辅助护理与预警提醒', bg: 'linear-gradient(135deg, #FF9EC7, #2EC4B6)' },
  { icon: '📊', title: '数据看板', desc: '运营数据一目了然', bg: 'linear-gradient(135deg, #B8E0F0, #2EC4B6)' },
]
</script>

<style scoped lang="scss">
.home {
  min-height: calc(100vh - 120px);
}

.welcome-hero {
  text-align: center;
  padding: 48px 20px 40px;
  background: linear-gradient(135deg, rgba(255, 158, 199, 0.1) 0%, rgba(46, 196, 182, 0.1) 100%);
  border-radius: 16px;
  margin-bottom: 32px;

  .hero-logo {
    width: 64px;
    height: 64px;
    margin-bottom: 16px;
  }
  .hero-title {
    font-size: 36px;
    font-weight: 700;
    letter-spacing: 8px;
    background: linear-gradient(135deg, #FF9EC7, #2EC4B6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0 0 8px;
  }
  .hero-subtitle {
    font-size: 18px;
    color: #2EC4B6;
    margin: 0 0 12px;
    letter-spacing: 2px;
  }
  .hero-desc {
    font-size: 14px;
    color: #6B7280;
    margin: 0;
  }
}

.feature-card {
  border-radius: 12px;
  text-align: center;
  margin-bottom: 16px;
  border: none;

  .feature-icon {
    width: 48px;
    height: 48px;
    line-height: 48px;
    border-radius: 12px;
    font-size: 22px;
    margin: 0 auto 12px;
  }
  h3 {
    font-size: 16px;
    color: #1A3A4A;
    margin: 0 0 8px;
  }
  p {
    font-size: 13px;
    color: #9CA3AF;
    margin: 0;
  }
}

.info-card {
  border-radius: 12px;
  border: none;
  margin-bottom: 16px;

  .info-item {
    display: flex;
    padding: 10px 0;
    border-bottom: 1px solid #F3F4F6;
    &:last-child { border-bottom: none; }
    .label {
      width: 100px;
      color: #9CA3AF;
      flex-shrink: 0;
    }
    .value {
      color: #374151;
    }
  }
  .quick-links {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
}
</style>
