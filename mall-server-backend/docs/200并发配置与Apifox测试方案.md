# 200 并发支持：数据库配置与 Apifox 测试方案

本方案面向**购物高峰期性能瓶颈**，重点验证**下单相关操作**（订单列表、订单详情、创建订单、支付成功等）在 200 并发下的表现。

---

## 一、应用已做配置（application.yml）

以下已在 `mall-server/src/main/resources/application.yml` 中配置，无需再改：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `server.tomcat.threads.max` | 250 | Tomcat 最大工作线程，支持约 250 个并发 HTTP 请求 |
| `server.tomcat.accept-count` | 100 | 等待队列长度，超出线程处理能力时排队 |
| `spring.datasource.hikari.maximum-pool-size` | 200 | 数据库连接池最大连接数 |
| `spring.datasource.hikari.minimum-idle` | 20 | 连接池最小空闲连接 |
| `spring.datasource.hikari.connection-timeout` | 30000 | 获取连接超时（毫秒） |
| `spring.datasource.hikari.idle-timeout` | 600000 | 空闲连接回收时间（10 分钟） |
| `spring.datasource.hikari.max-lifetime` | 1800000 | 连接最大存活时间（30 分钟） |

---

## 二、MySQL 数据库配置（必做）

应用连接池最大为 200，MySQL 的 `max_connections` 必须 ≥ 200，并预留其他连接（管理、监控等）。建议设为 **250 或以上**。

### 2.1 查看当前 max_connections

```sql
SHOW VARIABLES LIKE 'max_connections';
```

### 2.2 临时修改（重启 MySQL 后失效）

```sql
SET GLOBAL max_connections = 250;
```

### 2.3 永久修改

- **Linux / 容器**：在 MySQL 配置文件（如 `/etc/mysql/mysql.conf.d/mysqld.cnf` 或 `my.cnf`）的 `[mysqld]` 下增加或修改：

```ini
[mysqld]
max_connections = 250
```

然后重启 MySQL：

```bash
sudo systemctl restart mysql
# 或
docker restart <mysql容器名>
```

- **Windows**：在 `my.ini` 的 `[mysqld]` 下添加或修改 `max_connections=250`，然后重启 MySQL 服务。

### 2.4 验证

```sql
SHOW VARIABLES LIKE 'max_connections';
-- 应显示 250 或你设置的值
```

---

## 三、Apifox 200 并发测试方案

### 3.1 目标与通过标准

| 指标 | 目标 |
|------|------|
| 并发数 | 至少 200 个请求同时发出 |
| 成功率 | ≥ 99%（允许极少数超时） |
| 平均响应时间 | 建议 P95 < 2s；下单/支付等写操作可放宽到 3s |
| 错误 | 无 5xx、无连接被拒绝 |

### 3.2 购物高峰期：推荐压测场景与接口（以下单为主）

| 场景 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 订单列表（高并发读） | `/mall/mini/order/list` | GET | 模拟用户同时查看「我的订单」 |
| 订单详情（高并发读） | `/mall/mini/order/detail?orderId=xxx` | GET | 模拟同时点进订单详情 |
| **创建订单（核心写）** | `/mall/mini/order/create` | POST | 锁库存、写订单，最容易成为瓶颈 |
| 从购物车下单（核心写） | `/mall/mini/order/createFromCart` | POST | 依赖购物车勾选，同样重 |
| **支付成功（核心写）** | `/mall/mini/order/pay/success` | POST | 扣库存、改订单状态，高并发易冲突 |
| 购物车加购 | `/mall/mini/cart/add` | POST | 写购物车，可做混合场景 |
| 购物车列表 | `/mall/mini/cart/list` | GET | 读多，轻量 |

**建议压测顺序**：先单接口 200 并发找出瓶颈（建议优先：**创建订单**、**支付成功**、订单列表），再按比例做混合场景（如 70% 订单列表 + 20% 下单 + 10% 支付成功）。

### 3.3 环境准备

1. **后端**：使用已配置 200 并发的 `application.yml` 启动 `mall-server`（如 `spring.profiles.active=dev`）。
2. **MySQL**：已按上文将 `max_connections` 设为 ≥ 250。
3. **Apifox**：安装最新版，能使用「自动化测试」或「批量运行」的并发/迭代功能。

### 3.4 小程序下单压测：准备数据与鉴权

小程序端鉴权使用请求头 **`X-User-Id`** + **`X-Token`**（不是 Authorization Bearer）。压测前需保证有一个有效 token。

**方式一：数据库为测试用户写死 token（推荐，便于重复压测）**

种子数据中有用户 id=20260309、地址 id=1、商品 id=1001/1002/1003。在 MySQL 执行：

```sql
UPDATE mall_user
SET token = 'stress-test-token',
    token_expire_time = DATE_ADD(NOW(), INTERVAL 7 DAY)
WHERE id = 20260309;
```

在 Apifox 环境变量中设置：`mini_user_id` = `20260309`，`mini_token` = `stress-test-token`。所有 Mini 请求头加：`X-User-Id: {{mini_user_id}}`，`X-Token: {{mini_token}}`。

**方式二：真实小程序登录**

- 请求：`POST {{baseUrl}}/mall/mini/login`，Body：`{"code": "微信 code"}`（需微信环境）。
- 响应 `data.token`、`data.id` 填入环境变量 `mini_token`、`mini_user_id`。

**下单/支付类接口所需数据**（与种子数据一致即可）：

- 用户 id：20260309（或你已写入 token 的用户）
- 地址 id：1（该用户下的收货地址）
- 商品 id：1001、1002、1003（上架且有库存）
- 创建订单后得到的 `orderNo` 用于「支付成功」接口的 `orderId`

### 3.5 管理端鉴权（可选）

- 请求：`POST {{baseUrl}}/mall/admin/login`，Body：`{"username": "管理员账号", "password": "密码"}`。
- 响应 `data.accessToken` 填入环境变量 `admin_token`。管理端请求头：`Authorization: Bearer {{admin_token}}`。

### 3.6 Apifox 环境与集合配置

**快速导入**：将项目内接口集合导入 Apifox 后，按下面配置环境即可。

- 集合文件：`mall-server-backend/docs/apifox-200-concurrency-collection.json`（Postman 2.1 格式）
- 集合内已包含：**小程序下单链路**（订单列表、订单详情、加购、直接下单、从购物车下单、支付成功）及管理端登录、Dashboard。小程序接口需配置 `mini_user_id`、`mini_token`（见 3.4）；管理端需配置 `admin_token`。

1. **环境变量**（在 Apifox 环境中添加）：
   - `baseUrl` = `http://localhost:8080`
   - `mini_user_id` = `20260309`（与 DB 中写死 token 的用户一致）
   - `mini_token` = `stress-test-token`（或登录返回的 token）
   - `admin_token` = 管理端登录返回的 accessToken（可选）
   - `address_id` = `1`（下单用）
   - `goods_id` = `1001`（下单/加购用）
   - `order_id` = 创建订单后返回的 `orderNo`（支付成功用）

2. **请求头**：
   - 小程序：`X-User-Id`: `{{mini_user_id}}`，`X-Token`: `{{mini_token}}`
   - 管理端：`Authorization`: `Bearer {{admin_token}}`

### 3.7 执行 200 并发测试（Apifox 自动化测试）

1. 打开 Apifox，进入「自动化测试」。
2. 新建测试用例或测试套件，添加步骤：
   - 步骤类型：**发送接口请求**
   - 选择要压测的接口（如 **下单** `POST /mall/mini/order/create`、**支付成功** `POST /mall/mini/order/pay/success`、**订单列表** `GET /mall/mini/order/list`）。
3. 配置并发与迭代：
   - **并发数**：200（或 250，看 Apifox 当前界面是否叫「并发数」「虚拟用户数」等，设为 200）。
   - **迭代次数**：1（表示 200 个并发各发 1 次）或 2～5（200 并发 × 多轮，压力更大）。
4. 运行测试，观察：
   - 总请求数、成功数、失败数
   - 平均/最大/P95 响应时间
   - 错误信息（若有 5xx 或连接失败，需排查服务与 MySQL）

### 3.8 使用「批量运行」的替代方式（视 Apifox 版本）

若当前 Apifox 版本没有明显的「并发数」输入框：

1. 将同一请求复制为多份（如 200 个请求），或使用「循环」+ 当前支持的并发能力。
2. 使用「批量运行」该接口/文件夹，并尽量调高「并发」或「同时运行数」到 200。
3. 或导出为 Postman Collection，用 Postman / Newman 的并发能力跑 200 并发，再在 Apifox 中查看/记录结果。

### 3.9 断言与通过标准（在 Apifox 中配置）

在自动化测试的该请求步骤后增加断言：

| 断言项 | 建议 |
|--------|------|
| 状态码 | 等于 200 |
| 响应体 JSON 中 code | 等于 200（或你项目成功码） |
| 响应时间 | 小于 3000 ms（可按业务放宽到 5000） |

通过标准建议：

- 200 个请求中成功数 ≥ 198（99% 成功率）。
- 无连接被拒绝、无 503/500。
- 平均响应时间在可接受范围内（如 < 2s）。

### 3.10 结果记录建议

| 项目 | 记录值 |
|------|--------|
| 并发数 | 200 |
| 总请求数 |  |
| 成功数 / 失败数 |  |
| 平均响应时间 |  |
| P95 响应时间 |  |
| 错误信息（若有） |  |

### 3.11 购物高峰期性能瓶颈分析建议

- **创建订单 / 从购物车下单**：若响应时间明显升高或出现「库存不足」「连接超时」，优先看数据库连接池、库存更新锁（行锁/乐观锁）及慢 SQL。
- **支付成功**：高并发下易出现订单状态竞争；关注 5xx、业务报错（如「订单已关闭」），以及数据库死锁/长事务。
- **订单列表/详情**：多为读请求，若 P95 明显升高，检查索引、连接池与 Tomcat 线程是否打满。
- 建议对**创建订单**、**支付成功**单独做 200 并发，先定位瓶颈再做混合场景。

---

## 四、可选：导入用接口示例（cURL）

便于在 Apifox 中快速新建请求：

**Admin - Dashboard（需先替换 `YOUR_ADMIN_TOKEN`）**

```bash
curl -X GET "http://localhost:8080/mall/admin/dashboard" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Mini - 订单列表（购物高峰期读场景，需 X-User-Id + X-Token）**

```bash
curl -X GET "http://localhost:8080/mall/mini/order/list" \
  -H "X-User-Id: 20260309" \
  -H "X-Token: stress-test-token"
```

**Mini - 创建订单（购物高峰期核心写场景）**

```bash
curl -X POST "http://localhost:8080/mall/mini/order/create" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 20260309" \
  -H "X-Token: stress-test-token" \
  -d '{"userId":20260309,"addressId":1,"totalAmount":19.90,"payAmount":19.90,"itemList":[{"goodsId":1001,"name":"云南高山蓝莓","price":19.90,"quantity":1}]}'
```

**Mini - 支付成功（需先创建订单得到 orderNo，替换 ORDER_NO）**

```bash
curl -X POST "http://localhost:8080/mall/mini/order/pay/success" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 20260309" \
  -H "X-Token: stress-test-token" \
  -d '{"userId":20260309,"orderId":"ORDER_NO"}'
```

将上述 URL 与 Header 在 Apifox 中建为对应请求，配合环境变量即可做 200 并发测试。

---

## 五、故障排查简表

| 现象 | 可能原因 | 处理 |
|------|----------|------|
| 大量连接超时 / 503 | Tomcat 线程或连接池耗尽 | 确认 `threads.max`、`maximum-pool-size` 已按上文配置并重启 |
| 数据库连接失败 / Too many connections | MySQL `max_connections` 不足 | 将 `max_connections` 设为 ≥ 250 并重启 MySQL |
| 401 未授权 | Token 过期或未带 | 重新登录取 token，并确认请求头 `Authorization: Bearer {{token}}` |
| 压测时本机 CPU/内存打满 | 单机能力上限 | 降低并发或增加机器/做分布式压测 |

完成上述数据库配置并按 Apifox 方案执行后，即可验证「至少 200 并发」是否达标。
