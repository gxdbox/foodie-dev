package com.imooc.service.impl;

import java.util.ArrayList;
import java.util.Date;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.mapper.CarouselMapper;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.service.CarouselService;
import com.imooc.service.ItemService;
import com.imooc.service.OrdersService;
import com.imooc.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderItemsMapper orderItemsMapper;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Orders create(List<ShopcartBO> shopcartList, SubmitOrderBO submitOrderBO) {
        Integer payMethod = submitOrderBO.getPayMethod();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        String userId = submitOrderBO.getUserId();
        String leftMsg = submitOrderBO.getLeftMsg();
        Integer postAccount = 0;//包邮费用

        //1、新订单信息保存
        String orderId = sid.nextShort();


        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setUserId(userId);

        UserAddress address = addressService.queryUserAddress(addressId, userId);
        orders.setReceiverName(address.getReceiver());
        orders.setReceiverMobile(address.getMobile());
        orders.setReceiverAddress(address.getProvince() + " "
                + address.getCity() + " "
                + address.getDistrict() + " "
                + address.getDetail());


        orders.setPostAmount(postAccount);
        orders.setPayMethod(payMethod);
        orders.setLeftMsg(leftMsg);
        orders.setExtand("");
        orders.setIsComment(YesOrNoEnum.NO.type);
        orders.setIsDelete(0);
        orders.setCreatedTime(new Date());
        orders.setUpdatedTime(new Date());


        //2、根据itemSpecIds保存规格表
        String itemSpecId[] = itemSpecIds.split(",");
        Integer totalAmmount = 0;
        Integer realAmmount = 0;
        List<ShopcartBO> beRemovedShopcartList = new ArrayList<>();
        for (String specId : itemSpecId) {
            // TODO 整合redis后，商品购买的数量重新从redis的购物车中获取
            ShopcartBO shopcartBO = getShopcartCountFromRedis(specId, shopcartList);
            int buyCounts = shopcartBO.getBuyCounts();
            beRemovedShopcartList.add(shopcartBO);

            // 2.1 根据规格id，查询规格的具体信息，主要获取价格
            ItemsSpec itemsSpec = itemService.queryItemSpecBySpecId(specId);
            totalAmmount += itemsSpec.getPriceDiscount() * buyCounts;
            realAmmount += itemsSpec.getPriceDiscount() * buyCounts;

            // 2.2 根据商品id，获得商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            String url = itemService.queryItemImgByImgId(itemId);
            Items items = itemService.queryItemById(itemId);

            // 2.3 循环保存子订单数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems orderItems = new OrderItems();
            orderItems.setId(subOrderId);
            orderItems.setOrderId(orderId);
            orderItems.setItemId(itemId);
            orderItems.setItemImg(url);
            orderItems.setItemName(items.getItemName());
            orderItems.setItemSpecId(itemsSpec.getItemId());
            orderItems.setItemSpecName(itemsSpec.getName());
            orderItems.setPrice(itemsSpec.getPriceDiscount());
            orderItems.setBuyCounts(buyCounts);
            orderItemsMapper.insert(orderItems);

            itemService.decreaseItemStock(specId, buyCounts);

            // 2.4 在用户提交订单以后，规格表中需要扣除库存
        }

        orders.setTotalAmount(totalAmmount);
        orders.setRealPayAmount(realAmmount);
        orders.setShopcartList(beRemovedShopcartList);
        ordersMapper.insert(orders);

        //3、保存订单状态表
        OrderStatus waitPayorderStatus = new OrderStatus();
        waitPayorderStatus.setOrderId(orderId);
        waitPayorderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayorderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayorderStatus);

        //构建商户订单，用于传给支付中心
        return orders;
    }

    private ShopcartBO getShopcartCountFromRedis(String specId, List<ShopcartBO> shopcartList) {
        for (ShopcartBO shopcartBO : shopcartList) {
            if (specId.equals(shopcartBO.getSpecId())) {
                return shopcartBO;
            }
        }
        return null;//todo 返回null，会不会不好
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer waitDeliver) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(waitDeliver);
        orderStatus.setPayTime(new Date());
        int result = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if (result != 1) {
            throw new RuntimeException("修改订单状态失败");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {
        // 查询所有未付款订单，判断时间是否超时（1天），超时则关闭交易
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(orderStatus);
        for (OrderStatus os : list) {
            Date createdTime = os.getCreatedTime();
            int i = DateUtil.daysBetween(createdTime, new Date());
            if (i >= 1) {
                doClose(os.getOrderId());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void doClose(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.CLOSE.type);
        orderStatus.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }
}

