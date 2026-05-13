/**
 * 商品/上传图片 URL 解析：相对路径补全为完整地址
 * 与分类图标的展示逻辑一致：仅当“像图片”时才返回 URL，否则返回空（避免把文件名/emoji 当无效地址用）。
 * 兼容：完整 URL、/upload/xxx、upload/xxx、仅文件名（含 .png/.jpg 等）
 */
const baseURL = (import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8080").replace(/\/$/, "");

/** 常见图片扩展名，用于识别“纯文件名”形式的图片 */
const IMAGE_EXT_RE = /\.(png|jpe?g|gif|webp|bmp|svg)(\?.*)?$/i;

/** 像“无扩展名文件名”的字符串（如上传后的 UUID），避免被当 emoji 显示成文字 */
function looksLikeFilename(s: string): boolean {
  if (s.length < 10) return false;
  return /^[a-zA-Z0-9_.-]+$/.test(s) && !/^[\u{1F300}-\u{1F9FF}]|[\u2600-\u26FF]|[\u2700-\u27BF]/u.test(s);
}

/**
 * 判断是否应视为图片 URL/路径：完整 URL、路径、带扩展名文件名、或长串无扩展名文件名。
 * 非图片（如 emoji 🫐）返回 false，避免拼出无效地址。
 */
export function isImageUrlOrPath(url: string | undefined): boolean {
  if (!url || typeof url !== "string") return false;
  const s = String(url).trim();
  if (s.startsWith("http://") || s.startsWith("https://")) return true;
  if (s.startsWith("/") || s.startsWith("upload") || s.includes("/upload")) return true;
  if (IMAGE_EXT_RE.test(s)) return true;
  if (looksLikeFilename(s)) return true;
  return false;
}

/**
 * 分类图标在“占位/失败”时应显示的文字：只显示 emoji，绝不把 URL 或路径当文字展示。
 * 当 icon 像图片地址时返回默认 emoji，否则返回 icon 本身（如 🍊）或默认 emoji。
 */
export function categoryIconPlaceholder(icon: string | undefined): string {
  if (!icon || typeof icon !== "string") return "🛍️";
  const s = String(icon).trim();
  if (isImageUrlOrPath(s)) return "🛍️";
  return s || "🛍️";
}

/**
 * 将商品主图/上传图的一项转为可访问的完整 URL。
 * 若非图片（如 emoji）则返回 ""，调用方用占位图即可。
 */
/**
 * 将「本地上传图」统一解析为当前小程序配置的接口域名下的地址。
 * 库内若存了 http://127.0.0.1、http://localhost 或其它电脑 IP 的完整 URL，
 * 开发者工具常能显示（与本机同源），真机上 localhost 指向手机自身导致详情图空白。
 */
function resolveUploadPathToBase(fullUrl: string): string | null {
  try {
    const u = new URL(fullUrl);
    const path = u.pathname || "";
    if (path.startsWith("/upload/")) {
      return baseURL + path + (u.search || "");
    }
  } catch {
    /* ignore */
  }
  return null;
}

export function goodsImageUrl(url: string | undefined): string {
  if (!url || !String(url).trim()) return "";
  const s = String(url).trim();
  if (!isImageUrlOrPath(s)) return "";
  if (s.startsWith("http://") || s.startsWith("https://")) {
    const rewritten = resolveUploadPathToBase(s);
    if (rewritten) return rewritten;
    return s;
  }
  const path = s.startsWith("/") ? s : s.startsWith("upload") ? "/" + s : "/upload/" + s;
  return baseURL + path;
}

/** 取商品主图 URL，兼容 bannerList 为数组或单字符串（接口有时返回字符串） */
export function firstBannerUrlFromGoods(goods: { bannerList?: string[] | string } | null): string {
  if (!goods?.bannerList) return "";
  const raw = goods.bannerList;
  const first = Array.isArray(raw) ? raw[0] : typeof raw === "string" ? raw.trim() : "";
  return goodsImageUrl(first || "");
}
