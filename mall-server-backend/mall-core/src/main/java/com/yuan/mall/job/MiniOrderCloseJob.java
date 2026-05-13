package com.yuan.mall.job;

import com.yuan.mall.service.MiniMallService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 关闭超时未支付订单的定时任务
 */
@Component
public class MiniOrderCloseJob {

    private final MiniMallService miniMallService;

    public MiniOrderCloseJob(MiniMallService miniMallService) {
        this.miniMallService = miniMallService;
    }

    @Scheduled(fixedDelay = 60_000L)
    public void closeExpiredOrders() {
        miniMallService.closeExpiredPendingOrders();
    }
}

