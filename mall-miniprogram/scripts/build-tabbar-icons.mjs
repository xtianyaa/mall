/**
 * 将 static/tabbar/*.svg 转为 81x81 PNG（未选中 / 选中两套）
 * 运行: node scripts/build-tabbar-icons.mjs
 */
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, "..");
// uni-app 构建只复制 src/static，tabBar 图标需放在此处
const tabbarDir = path.join(root, "src", "static", "tabbar");

const NORMAL_COLOR = "#6b7280";
const SELECTED_COLOR = "#ef4444";
const SIZE = 81;

const tabs = [
  { name: "home", svg: "tab-home.svg", png: "tab-home.png", pngActive: "tab-home-active.png" },
  { name: "category", svg: "tab-category.svg", png: "tab-category.png", pngActive: "tab-category-active.png" },
  { name: "cart", svg: "tab-cart.svg", png: "tab-cart.png", pngActive: "tab-cart-active.png" },
  { name: "my", svg: "tab-my.svg", png: "tab-my.png", pngActive: "tab-my-active.png" },
];

function replaceColor(svgString, color) {
  return svgString.replace(/currentColor/g, color);
}

async function main() {
  let sharp;
  try {
    sharp = (await import("sharp")).default;
  } catch (e) {
    console.error("请先安装 sharp: npm i -D sharp");
    process.exit(1);
  }

  for (const tab of tabs) {
    const svgPath = path.join(tabbarDir, tab.svg);
    if (!fs.existsSync(svgPath)) {
      console.warn("跳过（不存在）:", tab.svg);
      continue;
    }
    const svgString = fs.readFileSync(svgPath, "utf8");
    const svgNormal = replaceColor(svgString, NORMAL_COLOR);
    const svgActive = replaceColor(svgString, SELECTED_COLOR);

    const bufNormal = Buffer.from(svgNormal);
    const bufActive = Buffer.from(svgActive);

    await sharp(bufNormal).resize(SIZE, SIZE).png().toFile(path.join(tabbarDir, tab.png));
    await sharp(bufActive).resize(SIZE, SIZE).png().toFile(path.join(tabbarDir, tab.pngActive));
    console.log("生成:", tab.png, tab.pngActive);
  }
  console.log("tabBar 图标生成完成。");
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
