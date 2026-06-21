<template>
  <div class="login">
    <div class="login-cover" aria-hidden="true">
      <img src="@/assets/images/login-cover.png" alt="" class="cover-img" />
    </div>
    <div class="login-overlay" aria-hidden="true"></div>

    <div class="login-content">
      <div class="login-brand">
        <img src="@/assets/logo/logo.svg" alt="静心栖" class="brand-logo" />
        <h1 class="brand-name">静心栖</h1>
        <p class="brand-slogan">智慧养老 · 温暖相伴</p>
      </div>

      <div class="login-panel">
        <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
          <div class="form-header">
            <img src="@/assets/logo/logo.svg" alt="" class="form-logo" />
            <h3 class="title">欢迎回来</h3>
            <p class="subtitle">登录静心栖管理系统</p>
          </div>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              size="large"
              auto-complete="off"
              placeholder="账号"
            >
              <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              size="large"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter="handleLogin"
            >
              <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              size="large"
              auto-complete="off"
              placeholder="验证码"
              style="width: 63%"
              @keyup.enter="handleLogin"
            >
              <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>
          <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>
          <el-form-item style="width:100%; margin-bottom: 0;">
            <el-button
              :loading="loading"
              size="large"
              type="primary"
              class="login-btn"
              style="width:100%;"
              @click.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
            <div class="register-link" v-if="register">
              <router-link class="link-type" :to="'/register'">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="el-login-footer">
      <span>Copyright © {{ year }} 静心栖 All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const year = new Date().getFullYear();

const loginForm = ref({
  username: "admin",
  password: "admin123",
  rememberMe: false,
  code: "",
  uuid: ""
});

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);
const register = ref(false);
const redirect = ref(undefined);

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 });
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 });
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 });
      } else {
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      userStore.login(loginForm.value).then(() => {
        const query = route.query;
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur];
          }
          return acc;
        }, {});
        router.push({ path: redirect.value || "/", query: otherQueryParams });
      }).catch(() => {
        loading.value = false;
        if (captchaEnabled.value) {
          getCode();
        }
      });
    }
  });
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      loginForm.value.uuid = res.uuid;
    }
  });
}

function getCookie() {
  const username = Cookies.get("username");
  const password = Cookies.get("password");
  const rememberMe = Cookies.get("rememberMe");
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  };
}

getCode();
getCookie();
</script>

<style lang='scss' scoped>
.login {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.login-cover {
  position: absolute;
  inset: 0;
  z-index: 0;

  .cover-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center top;
  }
}

.login-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(
    105deg,
    rgba(26, 58, 74, 0.15) 0%,
    rgba(255, 182, 213, 0.25) 42%,
    rgba(255, 255, 255, 0.72) 68%,
    rgba(255, 255, 255, 0.92) 100%
  );
  pointer-events: none;
}

.login-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 8% 0 6%;
  gap: 40px;
}

.login-brand {
  flex: 1;
  max-width: 480px;
  color: #fff;
  text-shadow: 0 2px 24px rgba(26, 58, 74, 0.35);

  .brand-logo {
    width: 64px;
    height: 64px;
    margin-bottom: 20px;
    filter: drop-shadow(0 4px 16px rgba(46, 196, 182, 0.4));
  }
  .brand-name {
    font-size: 52px;
    font-weight: 700;
    letter-spacing: 10px;
    margin: 0 0 14px;
    background: linear-gradient(135deg, #fff 0%, #FFB7D5 50%, #B8F0EB 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.15));
  }
  .brand-slogan {
    font-size: 17px;
    letter-spacing: 5px;
    margin: 0;
    color: rgba(255, 255, 255, 0.92);
  }
}

.login-panel {
  flex-shrink: 0;
  width: 420px;
}

.login-form {
  border-radius: 24px;
  padding: 40px 36px 32px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(24px) saturate(1.4);
  -webkit-backdrop-filter: blur(24px) saturate(1.4);
  border: 1px solid rgba(255, 255, 255, 0.65);
  box-shadow:
    0 24px 64px rgba(46, 196, 182, 0.12),
    0 8px 32px rgba(255, 158, 199, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);

  .form-header {
    text-align: center;
    margin-bottom: 28px;

    .form-logo {
      width: 44px;
      height: 44px;
      margin-bottom: 12px;
    }
    .title {
      margin: 0 0 6px;
      font-size: 24px;
      font-weight: 700;
      background: linear-gradient(135deg, #FF9EC7, #2EC4B6);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    .subtitle {
      margin: 0;
      font-size: 13px;
      color: #8BA4B4;
      letter-spacing: 1px;
    }
  }

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.85);
    box-shadow: 0 0 0 1px rgba(46, 196, 182, 0.15) inset;
    transition: box-shadow 0.2s;

    &:hover, &.is-focus {
      box-shadow: 0 0 0 1px rgba(255, 158, 199, 0.5) inset;
    }
  }

  .el-input {
    height: 44px;
    input { height: 44px; }
  }
  .input-icon {
    height: 42px;
    width: 14px;
    margin-left: 0;
  }
}

.remember-me {
  margin: 0 0 24px;
  :deep(.el-checkbox__label) { color: #6B8A9A; }
  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: #2EC4B6;
    border-color: #2EC4B6;
  }
}

.login-btn {
  border-radius: 12px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #FF9EC7 0%, #2EC4B6 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(46, 196, 182, 0.35);

  &:hover, &:focus {
    background: linear-gradient(135deg, #FF8AB8 0%, #26B5A8 100%);
    box-shadow: 0 10px 28px rgba(255, 158, 199, 0.4);
  }
}

.login-code {
  width: 33%;
  height: 44px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
    border-radius: 10px;
  }
}

.login-code-img {
  height: 44px;
  padding-left: 12px;
}

.register-link {
  text-align: right;
  margin-top: 12px;
}

.link-type {
  color: #2EC4B6;
  font-size: 13px;
  &:hover { color: #FF9EC7; }
}

.el-login-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 3;
  height: 40px;
  line-height: 40px;
  text-align: center;
  color: rgba(255, 255, 255, 0.88);
  font-size: 12px;
  letter-spacing: 1px;
  text-shadow: 0 1px 4px rgba(26, 58, 74, 0.3);
}

@media (max-width: 960px) {
  .login-overlay {
    background: linear-gradient(
      180deg,
      rgba(26, 58, 74, 0.2) 0%,
      rgba(255, 255, 255, 0.88) 55%
    );
  }
  .login-content {
    flex-direction: column;
    justify-content: center;
    padding: 24px;
  }
  .login-brand { display: none; }
  .login-panel { width: 100%; max-width: 400px; }
}
</style>
