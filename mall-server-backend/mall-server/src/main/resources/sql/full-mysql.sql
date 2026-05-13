-- ============================================================
-- mall 全量 SQL 初始化脚本（空库一键执行）
-- 用途：新建数据库 + 建表 + 初始化数据
-- 对齐：mall_goods_variant 使用 specs(JSON) + spec_names(JSON)，与 schema-mysql.sql、
--       migration-goods-variants.sql 及 Java 实体 MallGoodsVariantEntity 一致
-- 字符集：utf8mb4
-- 说明：默认库名 mall0319 与 application-dev.yml 中 MALL_DB_NAME 一致，可按环境修改
-- ============================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS mall0319 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mall0319;

-- ------------------------------------------------------------
-- 1. 表结构
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS mall_category (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(64) NOT NULL COMMENT '分类名称',
    icon VARCHAR(512) NULL COMMENT '分类图标 URL',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序（升序）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用 0=停用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS mall_goods (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    category_id BIGINT NOT NULL COMMENT '所属分类ID',
    category_name VARCHAR(64) NOT NULL COMMENT '所属分类名称快照',
    name VARCHAR(128) NOT NULL COMMENT '商品名称',
    discreet_name VARCHAR(128) NULL COMMENT '隐秘商品名称（用于面单打印）',
    characteristic VARCHAR(255) NOT NULL DEFAULT '' COMMENT '商品卖点/特色',
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '销售价',
    original_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '划线价/原价',
    unit VARCHAR(32) NOT NULL DEFAULT '件' COMMENT '计量单位',
    stock INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    locked_stock INT NOT NULL DEFAULT 0 COMMENT '锁定库存（下单未支付预占）',
    monthly_sales INT NOT NULL DEFAULT 0 COMMENT '月销量',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=上架 0=下架',
    tags VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标签（逗号分隔）',
    banner_list VARCHAR(255) NOT NULL DEFAULT '' COMMENT '轮播图列表（逗号分隔）',
    detail_list TEXT NOT NULL COMMENT '详情图/详情内容（逗号或文本）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_category_id (category_id)
) COMMENT='商品信息表';

CREATE TABLE IF NOT EXISTS mall_goods_variant (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '规格SKU主键ID',
    goods_id BIGINT NOT NULL COMMENT '商品ID',
    goods_name VARCHAR(128) NOT NULL COMMENT '商品名称快照（便于展示/下单兜底）',
    specs JSON NOT NULL DEFAULT (JSON_OBJECT()) COMMENT '规格维度键值对，如 {"颜色":"红","尺码":"S","套餐":"礼盒"}',
    spec_names JSON NOT NULL DEFAULT (JSON_ARRAY('颜色', '尺码')) COMMENT '维度名列表，按顺序最多5个，如 ["颜色","尺码","套餐"]',
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '销售价',
    original_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '划线价/原价',
    unit VARCHAR(32) NOT NULL DEFAULT '件' COMMENT '计量单位',
    stock INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    locked_stock INT NOT NULL DEFAULT 0 COMMENT '锁定库存（下单未支付预占）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=上架 0=下架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_variant_goods_id (goods_id)
) COMMENT='商品规格SKU表';

CREATE TABLE IF NOT EXISTS mall_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    nick_name VARCHAR(64) NOT NULL COMMENT '用户昵称',
    mobile VARCHAR(32) NOT NULL COMMENT '手机号',
    level_name VARCHAR(32) NOT NULL COMMENT '会员等级名称',
    points INT NOT NULL DEFAULT 0 COMMENT '积分',
    order_count INT NOT NULL DEFAULT 0 COMMENT '累计订单数',
    consume_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    openid VARCHAR(64) NULL COMMENT '微信小程序 openid，code2Session 登录绑定',
    token VARCHAR(64) NULL COMMENT '登录 token（简化版，后续可替换为 JWT/Session）',
    token_expire_time DATETIME NULL COMMENT 'token 过期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_openid (openid)
) COMMENT='用户信息表';

CREATE TABLE IF NOT EXISTS mall_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '订单自增ID',
    user_id BIGINT NOT NULL COMMENT '下单用户ID',
    coupon_id BIGINT NULL COMMENT '使用的优惠券ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    user_name VARCHAR(64) NOT NULL COMMENT '下单用户名称快照',
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    pay_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
    address_text VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收货地址快照（含姓名/电话）',
    is_discreet TINYINT NOT NULL DEFAULT 0 COMMENT '是否隐秘发货',
    status VARCHAR(32) NOT NULL COMMENT '订单状态：待支付/待发货/配送中/已完成/已取消/支付处理中',
    pay_channel VARCHAR(32) NULL COMMENT '支付渠道（wx_jsapi 等）',
    pay_trade_no VARCHAR(64) NULL COMMENT '支付流水号/第三方交易号',
    pay_deadline DATETIME NULL COMMENT '支付截止时间',
    pay_time DATETIME NULL COMMENT '支付成功时间',
    cancel_time DATETIME NULL COMMENT '取消时间（支付失败/超时/主动取消）',
    express_company VARCHAR(64) NULL COMMENT '快递公司',
    express_no VARCHAR(128) NULL COMMENT '快递单号',
    ship_time DATETIME NULL COMMENT '发货时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_user_id (user_id),
    KEY idx_order_status (status),
    KEY idx_order_pay_deadline (pay_deadline)
) COMMENT='订单主表';

CREATE TABLE IF NOT EXISTS mall_order_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '订单明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    goods_id BIGINT NOT NULL COMMENT '商品ID',
    variant_id BIGINT NULL COMMENT '规格SKU ID（旧订单可能为空）',
    goods_name VARCHAR(128) NOT NULL COMMENT '商品名称快照',
    goods_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '成交单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_order_id (order_id)
) COMMENT='订单明细表';

CREATE TABLE IF NOT EXISTS mall_user_address (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(64) NOT NULL COMMENT '收货人姓名',
    mobile VARCHAR(32) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(32) NOT NULL COMMENT '省',
    city VARCHAR(32) NOT NULL COMMENT '市',
    district VARCHAR(32) NOT NULL COMMENT '区/县',
    detail VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址：1=是 0=否',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_user_id (user_id)
) COMMENT='用户收货地址表';

CREATE TABLE IF NOT EXISTS mall_cart (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    goods_id BIGINT NOT NULL COMMENT '商品ID',
    variant_id BIGINT NULL COMMENT '规格SKU ID（旧购物车可能为空）',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    checked TINYINT NOT NULL DEFAULT 1 COMMENT '是否勾选：1=是 0=否',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_cart_user_goods_variant (user_id, goods_id, variant_id),
    KEY idx_cart_user_id (user_id)
) COMMENT='用户购物车表';

CREATE TABLE IF NOT EXISTS mall_coupon (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '优惠券ID',
    name VARCHAR(64) NOT NULL COMMENT '券名称',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '减免金额',
    min_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '使用门槛金额',
    receive_count INT NOT NULL DEFAULT 0 COMMENT '领取次数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用 0=停用',
    start_time DATETIME NULL COMMENT '可用开始时间，NULL表示立即生效',
    expire_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '券到期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='优惠券模板表';

CREATE TABLE IF NOT EXISTS mall_user_coupon (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户优惠券ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    status VARCHAR(32) NOT NULL COMMENT '状态：unused/locked/used/expired',
    expire_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '到期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_user_coupon_user_id (user_id),
    KEY idx_user_coupon_coupon_id (coupon_id)
) COMMENT='用户持有的优惠券表';

CREATE TABLE IF NOT EXISTS mall_pay_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '支付回调日志ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    trade_no VARCHAR(64) NULL COMMENT '第三方交易号/流水号',
    pay_channel VARCHAR(32) NULL COMMENT '支付渠道',
    result VARCHAR(32) NOT NULL COMMENT '结果：SUCCESS/FAIL',
    raw TEXT NULL COMMENT '原始回调内容（占位）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_pay_trade_no (trade_no),
    KEY idx_pay_order_no (order_no)
) COMMENT='支付回调日志表';

CREATE TABLE IF NOT EXISTS mall_banner (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'Banner ID',
    title VARCHAR(128) NOT NULL COMMENT 'Banner 标题',
    image_url VARCHAR(512) NOT NULL DEFAULT '' COMMENT 'Banner 图片路径（/upload/xxx）',
    link_type VARCHAR(64) NOT NULL COMMENT '跳转类型：商品专题/优惠券/自定义链接等',
    link_url VARCHAR(512) NOT NULL DEFAULT '' COMMENT '跳转参数或完整链接',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用 0=停用',
    sort INT NOT NULL DEFAULT 1 COMMENT '排序（升序）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='首页 Banner 配置表';

CREATE TABLE IF NOT EXISTS mall_home_deco (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键（仅一条）',
    search_placeholder VARCHAR(256) NOT NULL DEFAULT '' COMMENT '搜索框占位文案',
    stat1_value VARCHAR(64) NOT NULL DEFAULT '' COMMENT '服务1数值',
    stat1_label VARCHAR(64) NOT NULL DEFAULT '' COMMENT '服务1描述',
    stat2_value VARCHAR(64) NOT NULL DEFAULT '' COMMENT '服务2数值',
    stat2_label VARCHAR(64) NOT NULL DEFAULT '' COMMENT '服务2描述',
    stat3_value VARCHAR(64) NOT NULL DEFAULT '' COMMENT '服务3数值',
    stat3_label VARCHAR(64) NOT NULL DEFAULT '' COMMENT '服务3描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='首页首屏文案配置（装修）';

CREATE TABLE IF NOT EXISTS mall_system_setting (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '系统设置ID',
    mall_name VARCHAR(128) NOT NULL COMMENT '商城名称',
    service_mobile VARCHAR(64) NOT NULL COMMENT '客服电话',
    cancel_minutes VARCHAR(32) NOT NULL COMMENT '未支付自动取消分钟数',
    free_shipping_amount VARCHAR(32) NOT NULL COMMENT '包邮门槛金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='商城系统配置表';

CREATE TABLE IF NOT EXISTS mall_admin (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    username VARCHAR(64) NOT NULL COMMENT '登录账号',
    password VARCHAR(128) NOT NULL COMMENT '密码（BCrypt 加密存储）',
    token VARCHAR(512) NULL COMMENT '访问 token（JWT）',
    token_expire_time DATETIME NULL COMMENT '访问 token 过期时间',
    refresh_token VARCHAR(512) NULL COMMENT '刷新 token（JWT）',
    refresh_token_expire_time DATETIME NULL COMMENT '刷新 token 过期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) COMMENT='商城后台管理员表';

CREATE TABLE IF NOT EXISTS mall_after_sale_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '售后主单ID',
    order_id BIGINT NOT NULL COMMENT '原订单 ID',
    user_id BIGINT NOT NULL COMMENT '申请用户 ID',
    type VARCHAR(32) NOT NULL COMMENT '售后类型：REFUND_ONLY / RETURN_REFUND / EXCHANGE',
    status VARCHAR(32) NOT NULL COMMENT '售后状态：APPLIED / APPROVED / REJECTED / COMPLETED / CLOSED',
    reason VARCHAR(255) NOT NULL DEFAULT '' COMMENT '申请原因',
    apply_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '申请金额',
    actual_amount DECIMAL(10, 2) NULL COMMENT '实际退款金额',
    remark VARCHAR(255) NULL COMMENT '商家备注',
    apply_time DATETIME NULL COMMENT '申请时间',
    update_time DATETIME NULL COMMENT '更新时间',
    deadline DATETIME NULL COMMENT '允许申请截止时间',
    KEY idx_after_sale_order_id (order_id),
    KEY idx_after_sale_user_id (user_id),
    KEY idx_after_sale_status (status)
) COMMENT='售后主单表';

CREATE TABLE IF NOT EXISTS mall_after_sale_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '售后明细ID',
    after_sale_id BIGINT NOT NULL COMMENT '售后主单 ID',
    order_item_id BIGINT NOT NULL COMMENT '原订单明细 ID',
    goods_id BIGINT NOT NULL COMMENT '商品 ID',
    apply_quantity INT NOT NULL DEFAULT 1 COMMENT '申请售后数量',
    approved_quantity INT NULL COMMENT '实际同意数量',
    create_time DATETIME NULL COMMENT '创建时间',
    update_time DATETIME NULL COMMENT '更新时间',
    KEY idx_after_sale_item_after_sale_id (after_sale_id)
) COMMENT='售后明细表';

CREATE TABLE IF NOT EXISTS mall_after_sale_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '售后日志ID',
    after_sale_id BIGINT NOT NULL COMMENT '售后主单 ID',
    action VARCHAR(32) NOT NULL COMMENT '操作类型：APPLY / APPROVE / REJECT / COMPLETE',
    operator_id BIGINT NULL COMMENT '操作人 ID',
    operator_role VARCHAR(32) NULL COMMENT '操作人角色：USER / ADMIN',
    remark VARCHAR(255) NULL COMMENT '备注',
    create_time DATETIME NULL COMMENT '创建时间',
    KEY idx_after_sale_log_after_sale_id (after_sale_id)
) COMMENT='售后日志表';



INSERT INTO mall_coupon (id, name, amount, min_amount, receive_count, status, start_time, expire_time)
SELECT 1, '新人立减券', 10.00, 59.00, 842, 1, NULL, '2026-03-31 23:59:00'
WHERE NOT EXISTS (SELECT 1 FROM mall_coupon WHERE id = 1);

INSERT INTO mall_coupon (id, name, amount, min_amount, receive_count, status, start_time, expire_time)
SELECT 2, '满减津贴券', 15.00, 199.00, 316, 1, '2026-03-01 00:00:00', '2026-03-20 23:59:00'
WHERE NOT EXISTS (SELECT 1 FROM mall_coupon WHERE id = 2);

INSERT INTO mall_user_coupon (id, user_id, coupon_id, status, expire_time)
SELECT 1, 20260309, 1, 'unused', '2026-03-31 23:59:00'
WHERE NOT EXISTS (SELECT 1 FROM mall_user_coupon WHERE id = 1);

INSERT INTO mall_user_coupon (id, user_id, coupon_id, status, expire_time)
SELECT 2, 20260309, 2, 'unused', '2026-03-20 23:59:00'
WHERE NOT EXISTS (SELECT 1 FROM mall_user_coupon WHERE id = 2);

INSERT INTO mall_banner (id, title, image_url, link_type, link_url, status, sort)
SELECT 1, '好物上新', '/upload/goods/g5101_b1.png', '商品专题', '', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM mall_banner WHERE id = 1);

INSERT INTO mall_banner (id, title, image_url, link_type, link_url, status, sort)
SELECT 2, '新人专享首单礼', '', '优惠券', '', 1, 2
WHERE NOT EXISTS (SELECT 1 FROM mall_banner WHERE id = 2);

INSERT INTO mall_home_deco (id, search_placeholder, stat1_value, stat1_label, stat2_value, stat2_label, stat3_value, stat3_label)
SELECT 1, '搜索新奇商品', '大牌保证', '快速发货', '199 元', '满额包邮', '7 天', '无忧售后'
WHERE NOT EXISTS (SELECT 1 FROM mall_home_deco WHERE id = 1);

INSERT INTO mall_system_setting (id, mall_name, service_mobile, cancel_minutes, free_shipping_amount)
SELECT 1, '元选商城', '400-100-2026', '30', '99'
WHERE NOT EXISTS (SELECT 1 FROM mall_system_setting WHERE id = 1);

INSERT INTO mall_admin (id, username, password, token, token_expire_time, refresh_token, refresh_token_expire_time)
SELECT 1, 'admin', 'Admin@123', NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM mall_admin WHERE id = 1);

-- ------------------------------------------------------------
-- 执行完毕
-- ------------------------------------------------------------
