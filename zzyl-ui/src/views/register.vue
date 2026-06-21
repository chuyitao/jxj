<template>
  <div class="register">
    <div class="register-cover" aria-hidden="true">
      <img src="@/assets/images/login-cover.png" alt="" class="cover-img" />
    </div>
    <div class="register-overlay" aria-hidden="true"></div>
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">静心栖 · 注册账号</h3>
      <el-form-item prop="username">
        <el-input 
          v-model="registerForm.username" 
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
          v-model="registerForm.password"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="确认密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          size="large" 
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img"/>
        </div>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large" 
          type="primary"
          style="width:100%;"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-register-footer">
      <span>Copyright © {{ new Date().getFullYear() }} 静心栖 All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus";
import { getCodeImg, register } from "@/api/login";

const router = useRouter();
const { proxy } = getCurrentInstance();

const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  code: "",
  uuid: ""
});

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, trigger: "blur", message: "请输入您的账号" },
    { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  password: [
    { required: true, trigger: "blur", message: "请输入您的密码" },
    { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, trigger: "blur", message: "请再次输入您的密码" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);

function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true;
      register(registerForm.value).then(res => {
        const username = registerForm.value.username;
        ElMessageBox.alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", "系统提示", {
          dangerouslyUseHTMLString: true,
          type: "success",
        }).then(() => {
          router.push("/login");
        }).catch(() => {});
      }).catch(() => {
        loading.value = false;
        if (captchaEnabled) {
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
      registerForm.value.uuid = res.uuid;
    }
  });
}

getCode();
</script>

<style lang='scss' scoped>
.register {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  overflow: hidden;
}

.register-cover {
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

.register-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(2px);
  pointer-events: none;
}

.title {
  margin: 0 auto 28px;
  text-align: center;
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #FF9EC7, #2EC4B6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-form {
  position: relative;
  z-index: 2;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.65);
  box-shadow: 0 24px 64px rgba(46, 196, 182, 0.12), 0 8px 32px rgba(255, 158, 199, 0.18);
  width: 420px;
  padding: 36px 36px 20px;

  :deep(.el-button--primary) {
    width: 100%;
    height: 46px;
    border-radius: 12px;
    background: linear-gradient(135deg, #FF9EC7, #2EC4B6);
    border: none;
  }

  .el-input {
    height: 44px;
    input { height: 44px; border-radius: 12px; }
  }
  .input-icon {
    height: 42px;
    width: 14px;
    margin-left: 0;
  }
}
.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.register-code {
  width: 33%;
  height: 40px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 4px rgba(26, 58, 74, 0.3);
  font-size: 12px;
  letter-spacing: 1px;
}
.register-code-img {
  height: 40px;
  padding-left: 12px;
}
</style>
