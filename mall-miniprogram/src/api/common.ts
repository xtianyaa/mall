import type { ApiResponse } from "../types/mall";

/** 真机调试时请设置 .env 中 VITE_API_BASE_URL 为电脑本机 IP，如 http://192.168.1.100:8080 */
const baseURL = import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8080";

interface RequestOptions {
  url: string;
  method?: "GET" | "POST";
  data?: Record<string, any> | unknown[];
}

function buildUrl(url: string, method: string, data?: Record<string, any> | unknown[]): string {
  const full = `${baseURL}${url}`;
  if (method !== "GET" || !data || typeof data !== "object" || Array.isArray(data)) {
    return full;
  }

  const queryParts: string[] = [];
  Object.entries(data).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== "") {
      queryParts.push(`${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`);
    }
  });

  const query = queryParts.join("&");
  return query ? `${full}${url.includes("?") ? "&" : "?"}${query}` : full;
}

export function request<T>(options: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    const mallState = uni.getStorageSync("YA_MALL_PHASE1_STORE") as {
      userInfo?: { id?: number; token?: string };
      hasLogin?: boolean;
    };
    const userId = mallState?.hasLogin ? mallState?.userInfo?.id : undefined;
    const token = mallState?.hasLogin ? mallState?.userInfo?.token : undefined;
    const method = options.method || "GET";
    const url = buildUrl(options.url, method, options.data as Record<string, any> | undefined);

    uni.request({
      url,
      method,
      data: method === "GET" ? undefined : options.data,
      header: {
        ...(userId ? { "X-User-Id": String(userId) } : {}),
        ...(token ? { "X-Token": token } : {}),
      },
      success: (response) => {
        const result = response.data as ApiResponse<T>;
        if (result.code !== 200) {
          reject(new Error(result.message || "请求失败"));
          return;
        }
        resolve(result.data);
      },
      fail: (error) => reject(error),
    });
  });
}
