package com.imooc.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.mapper.OrderMapperCustom;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.MyOrdersVO;
import com.imooc.pojo.vo.OrderStatusCountsVO;
import com.imooc.service.impl.BaseService;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class MyOrderServiceImpl extends BaseService implements MyOrderService {
    @Autowired
    private OrderMapperCustom orderMapperCustom;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private OrdersMapper ordersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer currentPage, Integer pageSize) {
        Map<String , Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);
        if (orderStatus != null){
            paramsMap.put("orderStatus",orderStatus);
        }
        PageHelper.startPage(currentPage,pageSize);
        List<MyOrdersVO> myOrdersVOS = orderMapperCustom.queryMyOrders(paramsMap);
        return setterPagedGrid(myOrdersVOS,currentPage);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Boolean updateReceiveOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        orderStatus.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",orderId);
        criteria.andEqualTo("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
        int result = orderStatusMapper.updateByExampleSelective(orderStatus, example);
        return result == 1 ? true:false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyOrder(String userId, String orderId) {
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setId(orderId);
        orders.setIsDelete(YesOrNoEnum.NO.type);
        return ordersMapper.selectOne(orders);
    }

    @Override
    public boolean deleteOrder(String userId, String orderId) {
        Orders updateOrder = new Orders();
        updateOrder.setIsDelete(YesOrNoEnum.YES.type);
        updateOrder.setUpdatedTime(new Date());

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("userId", userId);

        int result = ordersMapper.updateByExampleSelective(updateOrder, example);

        return result == 1 ? true : false;
    }


    @Override
    public OrderStatusCountsVO queryMyOrderStatusCounts(String userId) {
        Map<String,Object> parmasMap = new HashMap<>();
        parmasMap.put("userId",userId);
        parmasMap.put("orderStatus",OrderStatusEnum.WAIT_PAY.type);
        int waitPayCounts = orderMapperCustom.queryMyOrderStatusCounts(parmasMap);

        parmasMap.put("orderStatus",OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts = orderMapperCustom.queryMyOrderStatusCounts(parmasMap);

        parmasMap.put("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts = orderMapperCustom.queryMyOrderStatusCounts(parmasMap);

        parmasMap.put("orderStatus",OrderStatusEnum.SUCCESS.type);
        parmasMap.put("isComment", YesOrNoEnum.NO.type);
        int waitCommentCounts = orderMapperCustom.queryMyOrderStatusCounts(parmasMap);
        OrderStatusCountsVO countsVO = new OrderStatusCountsVO(waitPayCounts,
                waitDeliverCounts,
                waitReceiveCounts,
                waitCommentCounts);

        return countsVO;
    }

    @Override
    public PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        PageHelper.startPage(page,pageSize);
        List<OrderStatus> list = orderMapperCustom.getMyOrderTrend(map);
        return setterPagedGrid(list, page);
    }
}
