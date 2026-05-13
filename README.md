# mall

元选微信商城项目工作区，首期范围为核心商城闭环，包含 3 个子项目：

- `mall-miniprogram`：微信小程序端，技术栈为 `uni-app + Vue3 + TypeScript`
- `mall-admin-web`：管理后台，技术栈为 `Vue3 + TypeScript + Element Plus`
- `mall-server`：管理后端，技术栈为 `Java Spring Boot + MyBatis-Plus`

## 文档入口

- `docs/工程说明文档.md`：工程结构、模块说明、配置与常见问题
- `docs/部署上线文档.md`：构建、部署、Nginx/域名、上线验收与回滚
- `docs/phase1-scope.md`：首期范围说明
- `docs/phase1-design.md`：首期接口与数据模型
- `docs/image-url-logic.md`：分类/商品图片 URL 与上传逻辑

## 首期范围

首期仅实现核心商城闭环：

- 首页
- 分类页
- 搜索页
- 商品列表页
- 商品详情页
- 购物车页
- 结算页
- 地址管理
- 登录授权
- 我的页面
- 订单列表
- 订单详情
- 优惠券页

## 目录结构

```text
mall/
├── docs/
├── mall-admin-web/
├── mall-miniprogram/
└── mall-server/
```

## 小程序端：分类/商品图片正常显示

小程序中显示的图片（分类图标、商品主图）来自后端接口返回的 URL（如 `/upload/xxx.png`），前端会拼成完整地址（`VITE_API_BASE_URL` + 路径）。若**图片不显示或只显示为路径文字**，请检查：

1. **重新构建**：在 `mall-miniprogram` 下执行 `npm run dev:mp-weixin` 或 `npm run build:mp-weixin`，确保最新代码生效。
2. **合法域名**：在 [微信公众平台](https://mp.weixin.qq.com) → 开发 → 开发管理 → 开发设置 → 服务器域名中，把后端接口所在域名（含协议，如 `https://your-api.com`）同时加入 **request 合法域名** 和 **downloadFile 合法域名**，否则图片请求会被拦截、加载失败后可能显示为占位 emoji（🛍️）。
