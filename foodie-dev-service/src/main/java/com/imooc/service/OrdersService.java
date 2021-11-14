package com.imooc.service;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitOrderBO;

import java.util.List;

public interface OrdersService {
    /**
     * 用于创建订单
     *
     * @param submitOrderBO
     */
    Orders create(List<ShopcartBO> shopcartList, SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param waitDeliver
     */
    void updateOrderStatus(String orderId, Integer waitDeliver);

    /**
     * 定时关闭未支付的订单
     */
    void closeOrder();
}
