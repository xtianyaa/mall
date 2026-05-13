# mall-server

微信商城首期后端工程，数据库使用 `MySQL`。

## 模块说明

- `safe-product-module-server`：启动模块
- `safe-product-module-web`：控制器与异常处理
- `safe-product-module-service`：DTO、VO、服务接口、统一返回结构
- `safe-product-module-core`：服务实现
- `safe-product-module-persistence`：Entity、Mapper

## 当前已完成

- 工作台统计接口
- 商品分类列表与新增
- 商品分类状态更新
- 商品列表与新增
- 商品上架状态更新
- 订单列表与发货
- 用户列表
- 优惠券列表与新增
- 首页 Banner 列表与排序更新
- 系统设置查询与保存
- 小程序登录、首页 Banner、分类、商品列表/详情
- 小程序收货地址查询、新增、编辑、删除
- 小程序优惠券列表、订单列表、订单详情、创建订单

## MySQL 启动方式

### 1. 创建数据库

```sql
CREATE DATABASE mall_phase DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 配置环境变量

```bash
MALL_DB_HOST=127.0.0.1
MALL_DB_PORT=33306
MALL_DB_NAME=mall_phase
MALL_DB_USERNAME=root
MALL_DB_PASSWORD=33taotao!
```

### 3. 启动项目

```bash
mvn -pl safe-product-module-server spring-boot:run
```

项目启动时会自动执行：

- `sql/schema-mysql.sql`
- `sql/data-mysql.sql`

## 核心接口

### 管理后台

- `GET /mall/admin/dashboard`
- `GET /mall/admin/category/list`
- `POST /mall/admin/category/create`
- `POST /mall/admin/category/status`
- `GET /mall/admin/goods/list`
- `POST /mall/admin/goods/create`
- `POST /mall/admin/goods/status`
- `GET /mall/admin/order/list`
- `POST /mall/admin/order/ship`
- `GET /mall/admin/user/list`
- `GET /mall/admin/coupon/list`
- `POST /mall/admin/coupon/create`
- `GET /mall/admin/banner/list`
- `POST /mall/admin/banner/sort`
- `GET /mall/admin/system/setting`
- `POST /mall/admin/system/setting`

### 小程序端

- `GET /mall/mini/banner/list`
- `GET /mall/mini/category/list`
- `GET /mall/mini/goods/list`
- `GET /mall/mini/goods/detail`
- `POST /mall/mini/login`
- `GET /mall/mini/user/profile`
- `GET /mall/mini/address/list`
- `POST /mall/mini/address/create`
- `POST /mall/mini/address/update`
- `POST /mall/mini/address/delete`
- `GET /mall/mini/coupon/list`
- `GET /mall/mini/order/list`
- `GET /mall/mini/order/detail`
- `POST /mall/mini/order/create`
