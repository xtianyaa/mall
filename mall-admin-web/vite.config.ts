import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": "/src",
    },
  },
  server: {
    host: true, // 监听所有网卡，允许通过 IP 访问（如 http://192.168.x.x:5173）
    port: 5173,
  },
});
