import axios, { type InternalAxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
import router from "../router";

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8080",
  timeout: 10000,
});

const isAuthUrl = (url?: string) =>
  url != null && (url.includes("/mall/admin/login") || url.includes("/mall/admin/refresh"));

request.interceptors.request.use((config) => {
  if (isAuthUrl(config.url)) {
    return config;
  }
  const token = window.localStorage.getItem("YA_ADMIN_TOKEN");
  if (token) {
    config.headers = config.headers ?? {};
    (config.headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

let isRefreshing = false;
let refreshSubscribers: Array<(token: string) => void> = [];

function subscribeRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb);
}

function onRefreshed(token: string) {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
}

request.interceptors.response.use(
  (response) => {
    const { code, message } = response.data ?? {};
    if (code != null && code !== 200) {
      const err = new Error(message || "请求失败");
      return Promise.reject(err);
    }
    return response;
  },
  async (error) => {
    const status = error?.response?.status;
    const msg = error?.response?.data?.message || error?.message || "网络异常";
    const config = error?.config as InternalAxiosRequestConfig & { _retry?: boolean };

    if (status === 401 && config && !config._retry) {
      const refreshToken = window.localStorage.getItem("YA_ADMIN_REFRESH_TOKEN");
      const isRefreshRequest = config.url != null && config.url.includes("/mall/admin/refresh");

      if (!isRefreshRequest && refreshToken && !isRefreshing) {
        config._retry = true;
        isRefreshing = true;
        try {
          const { adminRefresh } = await import("./yuan-mall-module");
          const result = await adminRefresh(refreshToken);
          window.localStorage.setItem("YA_ADMIN_TOKEN", result.token);
          window.localStorage.setItem("YA_ADMIN_REFRESH_TOKEN", result.refreshToken);
          onRefreshed(result.token);
          if (config.headers) (config.headers as Record<string, string>)["Authorization"] = `Bearer ${result.token}`;
          return request(config);
        } catch {
          clearAuthAndRedirect();
        } finally {
          isRefreshing = false;
        }
      } else if (isRefreshRequest || !refreshToken) {
        clearAuthAndRedirect();
      } else if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeRefresh((token: string) => {
            if (config.headers) (config.headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
            resolve(request(config));
          });
        });
      }
    } else if (status !== 401) {
      ElMessage.error(msg);
    }
    return Promise.reject(error);
  }
);

function clearAuthAndRedirect() {
  window.localStorage.removeItem("YA_ADMIN_TOKEN");
  window.localStorage.removeItem("YA_ADMIN_REFRESH_TOKEN");
  if (router.currentRoute.value.path !== "/login") {
    router.replace("/login");
  }
  ElMessage.error("登录已失效，请重新登录");
}

export { request };
