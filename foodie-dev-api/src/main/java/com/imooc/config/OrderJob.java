package com.imooc.config;

import com.imooc.service.OrdersService;
import com.imooc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderJob {
    @Autowired
    private OrdersService ordersService;

    @Scheduled(cron = "0/3 * * * * ?")
    public void autoCloseOrder() {
//        ordersService.closeOrder();
//        System.out.println("执行定时任务："+ DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
    }
}
