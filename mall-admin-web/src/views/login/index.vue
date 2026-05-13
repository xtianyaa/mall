<template>
  <div class="login-page">
    <div class="login-brand">
      <div class="brand-shape brand-shape-1" aria-hidden="true" />
      <div class="brand-shape brand-shape-2" aria-hidden="true" />
      <div class="brand-shape brand-shape-3" aria-hidden="true" />
      <!-- 电商氛围：抽象商品卡 -->
      <div class="eco-product-cards" aria-hidden="true">
        <div class="eco-card eco-card-1" />
        <div class="eco-card eco-card-2" />
        <div class="eco-card eco-card-3" />
        <div class="eco-card eco-card-4" />
        <div class="eco-card eco-card-5" />
      </div>
      <!-- 购物袋剪影 -->
      <div class="eco-bag-silhouette" aria-hidden="true">
        <svg viewBox="0 0 120 160" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M30 40h60v100H30V40z" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.5" />
          <path d="M40 40c0-11 8-20 20-20s20 9 20 20" stroke="currentColor" stroke-width="3" stroke-linecap="round" fill="none" opacity="0.5" />
        </svg>
      </div>
      <!-- 收据线条纹理 -->
      <div class="eco-receipt-lines" aria-hidden="true" />
      <div class="brand-content">
        <h1 class="brand-title">{{ brandMallName || "商城" }}</h1>
        <p class="brand-tagline">管理后台</p>
        <p class="brand-desc">商品、订单、用户与首页装修，一站管理</p>
      </div>
    </div>
    <div class="login-form-wrap">
      <div class="login-form-panel">
        <h2 class="form-title">登录</h2>
        <p class="form-desc">使用管理员账号登录系统</p>
        <div class="form-fields">
          <div class="field">
            <label class="field-label">账号</label>
            <input
              v-model="formData.username"
              type="text"
              class="field-input"
              placeholder="请输入账号"
              autocomplete="username"
              @keyup.enter="handleLogin"
            />
          </div>
          <div class="field">
            <label class="field-label">密码</label>
            <div class="password-wrap">
              <input
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                class="field-input password-input"
                placeholder="请输入密码"
                autocomplete="current-password"
                @keyup.enter="handleLogin"
              />
              <button
                type="button"
                class="password-toggle"
                :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                @click="showPassword = !showPassword"
              >
                <svg v-if="showPassword" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
                <svg v-else class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
              </button>
            </div>
          </div>
          <div class="field captcha-field">
            <label class="field-label">验证码</label>
            <div class="captcha-row">
              <input
                v-model="formData.captcha"
                type="text"
                class="field-input captcha-input"
                placeholder="请输入验证码"
                maxlength="4"
                autocomplete="off"
                @keyup.enter="handleLogin"
              />
              <div
                class="captcha-box"
                role="button"
                tabindex="0"
                aria-label="刷新验证码"
                @click="generateCaptcha"
                @keydown.enter="generateCaptcha"
              >
                <span
                  v-for="(ch, idx) in formData.captchaText.split('')"
                  :key="idx"
                  class="captcha-char"
                  :style="captchaStyles[idx]"
                >
                  {{ ch }}
                </span>
              </div>
            </div>
          </div>
          <button
            type="button"
            class="btn-login"
            :disabled="formData.loading"
            @click="handleLogin"
          >
            <span v-if="!formData.loading">登录系统</span>
            <span v-else class="btn-loading">登录中…</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { adminLogin, getPublicMallConfig } from "../../api/yuan-mall-module";

const router = useRouter();
const showPassword = ref(false);
const brandMallName = ref("");
const formData = reactive({
  username: "admin",
  password: "Admin@123",
  captcha: "",
  captchaText: "",
  loading: false,
});

const captchaStyles = reactive<Record<number, { color: string; transform: string }>>({});

const CAPTCHA_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

const generateCaptcha = () => {
  let text = "";
  for (let i = 0; i < 4; i += 1) {
    const idx = Math.floor(Math.random() * CAPTCHA_CHARS.length);
    text += CAPTCHA_CHARS[idx];
  }
  formData.captchaText = text;

  for (let i = 0; i < text.length; i += 1) {
    const hue = 165 + Math.floor(Math.random() * 40);
    const saturation = 50 + Math.random() * 30;
    const lightness = 28 + Math.random() * 20;
    const rotate = (Math.random() - 0.5) * 16;
    const translateY = (Math.random() - 0.5) * 3;
    captchaStyles[i] = {
      color: `hsl(${hue}, ${saturation}%, ${lightness}%)`,
      transform: `rotate(${rotate}deg) translateY(${translateY}px)`,
    };
  }
};

onMounted(async () => {
  generateCaptcha();
  try {
    const config = await getPublicMallConfig();
    brandMallName.value = config?.mallName?.trim() ?? "";
  } catch {
    brandMallName.value = "";
  }
});

const handleLogin = async () => {
  if (!formData.username || !formData.password) {
    ElMessage.warning("请输入账号和密码");
    return;
  }
  if (!formData.captcha) {
    ElMessage.warning("请输入验证码");
    return;
  }
  if (formData.captcha !== formData.captchaText) {
    ElMessage.error("验证码错误");
    generateCaptcha();
    formData.captcha = "";
    return;
  }
  if (formData.loading) return;
  try {
    formData.loading = true;
    const result = await adminLogin({
      username: formData.username,
      password: formData.password,
    });
    window.localStorage.setItem("YA_ADMIN_TOKEN", result.token);
    window.localStorage.setItem("YA_ADMIN_REFRESH_TOKEN", result.refreshToken);
    ElMessage.success("登录成功");
    router.replace("/");
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : "登录失败";
    ElMessage.error(msg);
  } finally {
    formData.loading = false;
    generateCaptcha();
    formData.captcha = "";
  }
};
</script>

<style lang="scss" scoped>
.login-page {
  --login-bg-dark: #0c1222;
  --login-bg-mid: #0f172a;
  --login-accent: #0d9488;
  --login-accent-dim: rgba(13, 148, 136, 0.25);
  --login-form-bg: #fafbfc;
  --login-form-border: #e5e7eb;
  --login-text: #0f172a;
  --login-text-muted: #64748b;
  --login-input-bg: #ffffff;
  --login-radius: 12px;
  --login-transition: 0.25s ease;

  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  font-family: "DM Sans", "PingFang SC", "Microsoft YaHei", sans-serif;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr;
  }
}

/* ---------- 左侧品牌区 ---------- */
.login-brand {
  position: relative;
  min-height: 100vh;
  padding: 48px 56px;
  display: flex;
  align-items: center;
  background: var(--login-bg-dark);
  overflow: hidden;
}

.login-brand::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 80% 60% at 20% 30%, var(--login-accent-dim) 0%, transparent 50%),
    radial-gradient(ellipse 60% 80% at 80% 70%, rgba(15, 23, 42, 0.9) 0%, transparent 45%);
  pointer-events: none;
}

.login-brand::after {
  content: "";
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.04'/%3E%3C/svg%3E");
  pointer-events: none;
}

.brand-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: brand-float 18s ease-in-out infinite;
}

.brand-shape-1 {
  width: 320px;
  height: 320px;
  background: var(--login-accent);
  top: -80px;
  right: -60px;
  animation-delay: 0s;
}

.brand-shape-2 {
  width: 240px;
  height: 240px;
  background: #134e4a;
  bottom: 20%;
  left: -40px;
  animation-delay: -6s;
}

.brand-shape-3 {
  width: 160px;
  height: 160px;
  background: #0f766e;
  bottom: 35%;
  right: 25%;
  animation-delay: -12s;
}

/* 电商元素：抽象商品卡 */
.eco-product-cards {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.eco-card {
  position: absolute;
  border-radius: 12px;
  background: linear-gradient(145deg, rgba(13, 148, 136, 0.2) 0%, rgba(19, 78, 74, 0.15) 100%);
  border: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  animation: eco-card-float 20s ease-in-out infinite;
}

.eco-card-1::after,
.eco-card-3::after {
  content: "";
  position: absolute;
  left: 8px;
  right: 8px;
  bottom: 8px;
  height: 6px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.12);
}

.eco-card-2::after {
  content: "";
  position: absolute;
  left: 6px;
  right: 6px;
  bottom: 6px;
  height: 4px;
  border-radius: 2px;
  background: rgba(13, 148, 136, 0.25);
}

.eco-card-1 {
  width: 100px;
  height: 130px;
  top: 18%;
  right: 22%;
  transform: rotate(-6deg);
  animation-delay: 0s;
}

.eco-card-2 {
  width: 88px;
  height: 115px;
  top: 42%;
  right: 12%;
  transform: rotate(8deg);
  animation-delay: -4s;
}

.eco-card-3 {
  width: 72px;
  height: 95px;
  bottom: 28%;
  left: 18%;
  transform: rotate(-10deg);
  animation-delay: -8s;
}

.eco-card-4 {
  width: 95px;
  height: 78px;
  top: 28%;
  left: 8%;
  transform: rotate(5deg);
  animation-delay: -12s;
}

.eco-card-5 {
  width: 68px;
  height: 88px;
  bottom: 18%;
  right: 35%;
  transform: rotate(3deg);
  animation-delay: -16s;
}

@keyframes eco-card-float {
  0%, 100% { transform: translateY(0) rotate(var(--r, 0deg)); }
  50% { transform: translateY(-8px) rotate(calc(var(--r, 0deg) + 2deg)); }
}

.eco-card-1 { --r: -6deg; }
.eco-card-2 { --r: 8deg; }
.eco-card-3 { --r: -10deg; }
.eco-card-4 { --r: 5deg; }
.eco-card-5 { --r: 3deg; }

/* 购物袋剪影 */
.eco-bag-silhouette {
  position: absolute;
  right: 8%;
  top: 50%;
  transform: translateY(-50%);
  width: clamp(140px, 18vw, 220px);
  height: auto;
  color: var(--login-accent);
  opacity: 0.07;
  pointer-events: none;
  z-index: 0;
}

.eco-bag-silhouette svg {
  width: 100%;
  height: auto;
  display: block;
}

/* 收据线条纹理 */
.eco-receipt-lines {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 240px;
  pointer-events: none;
  z-index: 0;
  background: repeating-linear-gradient(
    to bottom,
    transparent 0,
    transparent 2px,
    rgba(255, 255, 255, 0.02) 2px,
    rgba(255, 255, 255, 0.02) 3px
  );
}

@keyframes brand-float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(12px, -8px) scale(1.02); }
  66% { transform: translate(-8px, 6px) scale(0.98); }
}

.brand-content {
  position: relative;
  z-index: 1;
  animation: brand-in 0.8s ease-out backwards;
}

.brand-title {
  font-family: "Syne", "DM Sans", sans-serif;
  font-size: clamp(2.5rem, 5vw, 3.5rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #fff;
  margin: 0 0 8px;
  line-height: 1.1;
}

.brand-tagline {
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 0.2em;
  color: var(--login-accent);
  margin: 0 0 24px;
  text-transform: uppercase;
}

.brand-desc {
  font-size: 0.9375rem;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  margin: 0;
  max-width: 280px;
}

/* ---------- 右侧表单区 ---------- */
.login-form-wrap {
  min-height: 100vh;
  padding: 48px 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--login-form-bg);
  border-left: 1px solid var(--login-form-border);
}

@media (max-width: 900px) {
  .login-form-wrap {
    border-left: none;
    border-top: 1px solid var(--login-form-border);
    padding: 32px 24px;
  }
}

.login-form-panel {
  width: 100%;
  max-width: 380px;
  animation: form-in 0.6s ease-out 0.2s backwards;
}

.form-title {
  font-family: "Syne", "DM Sans", sans-serif;
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--login-text);
  margin: 0 0 8px;
}

.form-desc {
  font-size: 0.875rem;
  color: var(--login-text-muted);
  margin: 0 0 32px;
}

.form-fields {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--login-text);
}

.field-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 0.9375rem;
  font-family: inherit;
  color: var(--login-text);
  background: var(--login-input-bg);
  border: 1px solid var(--login-form-border);
  border-radius: var(--login-radius);
  transition: border-color var(--login-transition), box-shadow var(--login-transition);
  outline: none;
}

.field-input::placeholder {
  color: #9ca3af;
}

.field-input:hover {
  border-color: #d1d5db;
}

.password-wrap {
  position: relative;
}

.password-input {
  padding-right: 44px;
}

.password-toggle {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--login-text-muted);
  border-radius: 6px;
  transition: color var(--login-transition);
  outline: none;
}

.password-toggle:hover {
  color: var(--login-accent);
}

.password-toggle .icon {
  width: 18px;
  height: 18px;
  pointer-events: none;
}

.field-input:focus {
  border-color: var(--login-accent);
  box-shadow: 0 0 0 3px rgba(13, 148, 136, 0.15);
}

.captcha-row {
  display: flex;
  gap: 12px;
  align-items: stretch;
}

.captcha-input {
  flex: 1;
  min-width: 0;
}

.captcha-box {
  width: 120px;
  flex-shrink: 0;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
  background: linear-gradient(145deg, #f1f5f9 0%, #e2e8f0 100%);
  border: 1px solid var(--login-form-border);
  border-radius: var(--login-radius);
  cursor: pointer;
  transition: border-color var(--login-transition), transform var(--login-transition);
}

.captcha-box:hover {
  border-color: var(--login-accent);
  transform: translateY(-1px);
}

.captcha-char {
  font-family: "JetBrains Mono", monospace;
  font-size: 1.125rem;
  font-weight: 700;
  letter-spacing: 2px;
}

.btn-login {
  margin-top: 8px;
  padding: 14px 24px;
  font-size: 1rem;
  font-weight: 600;
  font-family: inherit;
  color: #fff;
  background: var(--login-accent);
  border: none;
  border-radius: var(--login-radius);
  cursor: pointer;
  transition: background var(--login-transition), transform var(--login-transition), box-shadow var(--login-transition);
  outline: none;
}

.btn-login:hover:not(:disabled) {
  background: #0f766e;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(13, 148, 136, 0.35);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-loading {
  display: inline-block;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  50% { opacity: 0.6; }
}

@keyframes brand-in {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes form-in {
  from {
    opacity: 0;
    transform: translateX(16px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
