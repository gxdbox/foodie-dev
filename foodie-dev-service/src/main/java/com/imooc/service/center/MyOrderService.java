package com.imooc.service.center;

import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.OrderStatusCountsVO;
import com.imooc.utils.PagedGridResult;

public interface MyOrderService {
    /**
     * 查询我的订单列表
     * @param userId
     * @param orderStatus
     * @param currentPage
     * @param pageSize
     * @return
     */
    PagedGridResult queryMyOrders(String userId,
                                  Integer orderStatus,
                                  Integer currentPage,
                                  Integer pageSize);

    /**
     * 确认收货
     * @param orderId
     * @return
     */
    Boolean updateReceiveOrderStatus(String orderId);

    /**
     * 确认用户id是否与订单关联
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyOrder(String userId,String orderId);


    /**
     * 删除订单（逻辑删除）
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);

    /**
     * 用户中信，查询订单状态数量
     * @param userId
     * @return
     */
    public OrderStatusCountsVO queryMyOrderStatusCounts(String userId);

    /**
     * 获得分页的订单动向
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult getOrdersTrend(String userId,
                                          Integer page,
                                          Integer pageSize);
}
