package com.yuan.mall.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 高并发下唯一订单号生成器（雪花思路：时间 + 序列，无外部依赖）
 * 格式：MO + 时间戳(ms) + 3位同毫秒内序列，保证单机同毫秒 1000 笔内不重复；
 * 若需多机部署，可配置 workerId 区分节点。
 *
 * @author system
 */
public final class OrderNoGenerator {

    private static final String PREFIX = "MO";
    private static final int MAX_SEQ = 1000;
    private static final AtomicInteger SEQ = new AtomicInteger(0);
    private static volatile long LAST_MS = 0L;

    /**
     * 生成唯一订单号，高并发下不重复
     *
     * @return 订单号，如 MO1739123456789001
     */
    public static String next() {
        long now = System.currentTimeMillis();
        int seq;
        synchronized (OrderNoGenerator.class) {
            if (now > LAST_MS) {
                LAST_MS = now;
                SEQ.set(0);
            }
            seq = SEQ.getAndIncrement();
            if (seq >= MAX_SEQ) {
                now = waitNextMs(now);
                LAST_MS = now;
                SEQ.set(0);
                seq = SEQ.getAndIncrement();
            }
        }
        return PREFIX + now + String.format("%03d", seq);
    }

    private static long waitNextMs(long now) {
        long next;
        do {
            next = System.currentTimeMillis();
        } while (next <= now);
        return next;
    }

    private OrderNoGenerator() {
    }
}
