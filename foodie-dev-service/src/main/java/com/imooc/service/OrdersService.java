package com.imooc.service;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.bo.SubmitOrderBO;

import java.util.List;

public interface OrdersService {
    /**
     * 用于创建订单
     * @param submitOrderBO
     */
    String create(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param waitDeliver
     */
    void updateOrderStatus(String orderId, Integer waitDeliver);
}
